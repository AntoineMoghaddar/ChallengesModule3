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
    private static final int NODE_COUNT = 6; // This is the number of nodes (instances we run)
    private ConcurrentHashMap<Integer, Map<Integer, DummyRoute>> fort = new ConcurrentHashMap<>();
    private HashMap<Integer, DummyRoute> forwardingTable = new HashMap<>();

    @Override
    public void init(LinkLayer linkLayer) {
        this.linkLayer = linkLayer;
    }

    @Override
    public void tick(Packet[] packets) {
        // Since the topology changes we also restart the collections every iteration
        forwardingTable.clear();
        fort.clear();

        // Loop through the packets
        for (Packet packet : packets) {
            int linkCost = this.linkLayer.getLinkCost(packet.getSourceAddress());
            int j = 0;
            if (linkCost != -1) {
                DataTable dt = packet.getDataTable();
                while (j < dt.getNRows()) {
                    if (dt.get(j, 2) < NODE_COUNT) {  // Make sure that we don't have packets running around
                        Map<Integer, DummyRoute> routes;
                        if (fort.containsKey(dt.get(j, 0))) {
                            routes = fort.get(dt.get(j, 0));
                        } else {
                            routes = new HashMap<>();
                            fort.put(dt.get(j, 0), routes);  // Put the new information (vectors) to the collection
                        }
                        DummyRoute r = new DummyRoute(dt.get(j, 0), packet.getSourceAddress(), dt.get(j, 1) + linkCost, (dt.get(j, 2) + 1));
                        routes.put(packet.getSourceAddress(), r);
                    }
                    j++;
                }
            }
        }

        for (Map.Entry<Integer, Map<Integer, DummyRoute>> entry : fort.entrySet()) {
            int dest = entry.getKey();
            for (Map.Entry<Integer, DummyRoute> route : entry.getValue().entrySet()) { // Looping in the inner map of the fort
                if (forwardingTable.containsKey(dest)) {
                    if (forwardingTable.get(dest).getCost() > route.getValue().getCost()) forwardingTable.put(dest, route.getValue());
                } else { // The entry is not in the table so just put it there
                    forwardingTable.put(dest, route.getValue());
                }
            }
        }
        // Add the node itself to the table so it also contains that
        DummyRoute r = new DummyRoute(
                this.linkLayer.getOwnAddress(),
                this.linkLayer.getOwnAddress(),
                0,
                1
        );
        forwardingTable.put(this.linkLayer.getOwnAddress(), r);


        for (int j = 1; j <= NODE_COUNT; j++) { // Here we also know the number of nodes but oh well...
            if (j != this.linkLayer.getOwnAddress()) {
                DataTable dt = new DataTable(3);
                for (Map.Entry<Integer, DummyRoute> route : forwardingTable.entrySet()) {
                    if (route.getValue().getNextHop() != j) {
                        Integer[] row = {
                                route.getValue().getDestination(),
                                route.getValue().getCost(),
                                route.getValue().getHops()
                        };
                        dt.addRow(row); // Build the data structure that we are going to send
                    }
                }
                Packet pkt = new Packet(this.linkLayer.getOwnAddress(), j, dt); // Send the data to all the nodes one by one (We don't use dest = 0)
                this.linkLayer.transmit(pkt);
            }
        }
    }

    // Makes the result presentable to the server
    @Override
    public HashMap<Integer, Integer> getForwardingTable() {
        HashMap<Integer, Integer> res = new HashMap<>();
        for (DummyRoute current : forwardingTable.values()) {
            res.put(current.getDestination(), current.getNextHop());
        }
        return res;
    }


}