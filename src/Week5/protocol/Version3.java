//package Week5.protocol;
//
//import Week5.client.IRoutingProtocol;
//import Week5.client.LinkLayer;
//import Week5.client.Packet;
//
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * @author Moose.
// */
//public class Version3 implements IRoutingProtocol{
//
//    private LinkLayer linkLayer;
//    private int myAddress;
//    private ConcurrentHashMap<Integer, DummyRoute>;
//
//    @Override
//    public void init(LinkLayer linkLayer) {
//        this.linkLayer = linkLayer;
//        myAddress = linkLayer.getOwnAddress();
//    }
//
//    @Override
//    public void tick(Packet[] packets) {
//        new DummyRoute()
//
//    }
//
//    @Override
//    public ConcurrentHashMap<Integer, DummyRoute> getForwardingTable() {
//        return null;
//    }
//}
//
