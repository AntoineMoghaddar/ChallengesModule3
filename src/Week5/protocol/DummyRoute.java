package Week5.protocol;

/**
 * Simple object which describes a route entry in the forwarding table.
 * Can be extended to include additional data.
 */
public class DummyRoute {
    private int destination, nextHop, cost, hops;


    public DummyRoute(int destination, int nextHop, int cost, int hops) {
        this.cost = cost;
        this.destination = destination;
        this.nextHop = nextHop;
        this.hops = hops;
    }

    public int getCost(){
        return cost;
    }

    public int getDestination() {
        return destination;
    }

    public int getNextHop() {
        return nextHop;
    }


    public int getHops() {
        return hops;
    }
}
