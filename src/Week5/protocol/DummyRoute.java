package Week5.protocol;

/**
 * Simple object which describes a route entry in the forwarding table.
 * Can be extended to include additional data.
 */
public class DummyRoute {
    public int destination;
    public int nextHop;
    public int cost;
    public int hops;

    public DummyRoute(int destination, int nextHop, int cost, int hops) {
        this.cost = cost;
        this.destination = destination;
        this.nextHop = nextHop;
    }
    public boolean knowsDestination(int compare) {
        return this.destination == compare;
    }

    public int pathCost(){
        return cost;
    }

    public int getDestination() {
        return destination;
    }

    public int getNextHop() {
        return nextHop;
    }
}
