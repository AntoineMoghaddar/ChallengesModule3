package Week6.java.src.ns.tcphack;

public class IPv6 {


    //byte values
    public static byte[] SourceIP = new byte[]{
            (byte) 0x20, (byte) 0x01, (byte) 0x06, (byte) 0x7c,
            (byte) 0x25, (byte) 0x64, (byte) 0xa1, (byte) 0x30,
            (byte) 0x00, (byte) 0xb0, (byte) 0xa2, (byte) 0xf8,
            (byte) 0x3a, (byte) 0x95, (byte) 0x76, (byte) 0x7c
    };

    public static byte[] destinationIP = new byte[]{
            (byte) 0x20, (byte) 0x01, (byte) 0x06, (byte) 0x7c,
            (byte) 0x25, (byte) 0x64, (byte) 0xa1, (byte) 0x30,
            (byte) 0xa0, (byte) 0x1b, (byte) 0x90, (byte) 0x23,
            (byte) 0xdb, (byte) 0x1f, (byte) 0x84, (byte) 0xac
    };


    // Integer values
//    public static int[] SourceIP = new int[]{
//            0x20, 0x01, 0x14, 0x50,
//            0x40, 0x13, 0x0c, 0x06,
//            0x00, 0x00, 0x00, 0x00,
//            0x00, 0x00, 0x00, 0xbc
//    };
//
//    public static int[] destinationIP = new int[]{
//            0x20, 0x01, 0x06, 0x7c,
//            0x25, 0x64, 0xa1, 0x30,
//            0xa0, 0x1b, 0x90, 0x23,
//            0xdb, 0x1f, 0x84, 0xac
//    };


    // creation of headers
    public static byte[] makeHeaders(int payloadLen, byte[] source, byte[] destination) {
        byte[] headers = new byte[40];
        headers[0] = 96; // 16 bits converted
        headers[4] = (byte) ((payloadLen & 0xFF00) >> 8);
        headers[5] = (byte) (payloadLen & 0xFF);
        headers[6] = (byte) 253;
//                (byte) MyTcpHandler.VERSION;
        headers[7] = 64;
        for (int i = 0; i != 16; ++i) {
            headers[8 + i] = source[i];
            headers[24 + i] = destination[i];
        }
        return headers;
    }
}
