package Week5.protocol;

import Week5.client.DataTable;
import Week5.client.IRoutingProtocol;
import Week5.client.LinkLayer;
import Week5.client.Packet;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OurProtocol implements IRoutingProtocol {
    private LinkLayer linkLayer;
    private ConcurrentHashMap<Integer, Map<Integer, DummyRoute>> fort = new ConcurrentHashMap<>();
    private HashMap<Integer, DummyRoute> forwardingTable = new HashMap<>();

    @Override
    public void init(LinkLayer linkLayer) {
        this.linkLayer = linkLayer;
    }

    @Override
    public void tick(Packet[] packets) {
        forwardingTable.clear();  //guarantee empty table
        fort.clear();             //guarantee empty concurrenthashmap

        System.out.println("tick; received " + packets.length + " packets");
        // first process the incoming packets; loop over them:
        for (int i = 0; i < packets.length; i++) {
            int neighbour = packets[i].getSourceAddress();
            int linkcost = this.linkLayer.getLinkCost(neighbour);

            if (linkcost != -1) {
                DataTable dt = packets[i].getDataTable();                    // other data contained in the packet
                System.out.println(this.linkLayer.getOwnAddress() + ", received packet from " + neighbour + " with " + dt.getNRows() + " rows of data");
                for (int j = 0; j < dt.getNRows(); j++) {
                    int dest = dt.get(j, 0);
                    int cost = dt.get(j, 1) + linkcost;
                    int hops = dt.get(j, 2);

                    if (hops < 6) {
                        Map<Integer, DummyRoute> routes;
                        if (fort.containsKey(dest)) {
                            routes = fort.get(dest);
                        } else {
                            routes = new HashMap<>();
                            fort.put(dest, routes);
                        }
                        DummyRoute r = new DummyRoute(dest, neighbour, cost, (hops + 1));
                        routes.put(neighbour, r);
                    }
                }
            }
        }

        for (Map.Entry<Integer, Map<Integer, DummyRoute>> entry : fort.entrySet()) {
            int dest = entry.getKey();

            if (dest == 1) {
                for (Map.Entry<Integer, DummyRoute> route : entry.getValue().entrySet()) {
//                    System.out.println(this.linkLayer.getOwnAddress() + ": " + route.getKey() + " | " + route.getValue().pathCost);
                }
            }

            for (Map.Entry<Integer, DummyRoute> route : entry.getValue().entrySet()) {
                if (forwardingTable.containsKey(dest)) {
                    if (forwardingTable.get(dest).pathCost() > route.getValue().pathCost()) {
                        forwardingTable.put(dest, route.getValue());
                    }
                } else {
                    forwardingTable.put(dest, route.getValue());
                }
            }
        }

        DummyRoute r = new DummyRoute(this.linkLayer.getOwnAddress(),
                this.linkLayer.getOwnAddress(),
                0,
                1);
        forwardingTable.put(this.linkLayer.getOwnAddress(), r);

        // and send out one (or more, if you want) distance vector packets
        // the actual distance vector data must be stored in the DataTable structure

        // you'll probably want to put some useful information into dt here
        // by using the  dt.set(row, column, value)  method.
        // next, actually send out the packet, with our own address as the source address
        // and 0 as the destination address: that's a broadcast to be received by all neighbours.
        for (int j = 1; j <= 6; j++) {
            if (j != this.linkLayer.getOwnAddress()) {
                DataTable dt = new DataTable(3);   // the 3 is the number of columns, you can change this
                for (Map.Entry<Integer, DummyRoute> route : forwardingTable.entrySet()) {
                    if (route.getValue().nextHop != j) {
                        Integer[] row = {
                                route.getValue().destination,
                                route.getValue().pathCost(),
                                route.getValue().hops
                        };
                        dt.addRow(row);
                    }
                }
                Packet pkt = new Packet(this.linkLayer.getOwnAddress(), j, dt);
                this.linkLayer.transmit(pkt);
            }
        }
    }

    @Override
    public HashMap<Integer, Integer> getForwardingTable() {
        HashMap<Integer, Integer> res = new HashMap<>();
        for (DummyRoute current : forwardingTable.values()) {
            res.put(current.destination, current.nextHop);
        }
        return res;
    }


}