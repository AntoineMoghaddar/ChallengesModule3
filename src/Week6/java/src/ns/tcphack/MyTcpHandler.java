package Week6.java.src.ns.tcphack;

class MyTcpHandler extends TcpHandler {
    public static int VERSION = 253;
    public boolean done, sent;

    public static void main(String[] args) {
        new MyTcpHandler();
    }

    public MyTcpHandler() {
        super();

        done = false;
        sent = false;
        NetworkLayer nLayer = new NetworkLayer(this, IPv6.SourceIP);
        TransportLayer tLayer = new TransportLayer(nLayer, IPv6.destinationIP, 7710);
        tLayer.connect();
        System.out.println("tLayer connected");
        long lastMsg = System.currentTimeMillis();
        while (!done) {
            String result = tLayer.recv();
            if (result != null) {
                lastMsg = System.currentTimeMillis();
                System.out.print(result);
            }
            if (tLayer.connected() && !sent) {
                sent = true;
                tLayer.send("GET /?nr=1570900 HTTP/1.0\n\n");
                System.out.println("GET request send");
            }
            if (System.currentTimeMillis() - lastMsg >= 10 * 1000) {
                tLayer.close();
                done = true;
                break;
            }
            System.out.println("nothing....");
        }
        System.out.println("system closed");
    }

}
