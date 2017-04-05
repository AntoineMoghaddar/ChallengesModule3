package Week6.java.src.ns.tcphack;

public class TCPPacket {
    public enum ControlBit {
        URG(32), ACK(16), PSH(8), RST(4), SYN(2), FIN(1);

        private final int value;

        ControlBit(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private byte[] pkt;
    byte[] header = new byte[40];

    public int getSequenceNumber() {
        return ((pkt[4] & 0xFF) << 24) + ((pkt[5] & 0xFF) << 16) + ((pkt[6] & 0xFF) << 8) + (pkt[7] & 0xFF);
    }

    public int getDataOffset() {
        return (pkt[12] & 0xFF) >> 4;
    }

    public int getControlBits() {
        return pkt[13] & 63;
    }

    public byte[] getData() {
        byte[] data = new byte[pkt.length - getDataOffset() * 4];
        System.arraycopy(pkt, getDataOffset() * 4, data, 0, pkt.length - getDataOffset() * 4);
        return data;
    }

    public byte[] getPkt() {
        return pkt;
    }

    public TCPPacket(byte[] data) {
        pkt = data;
    }

    public TCPPacket(int sourcePort, int destinationPort, int sequenceNumber, int acknowledgementNumber, int controlBits, int window, byte[] data) {
        pkt = new byte[5 * 4 + data.length]; // TODO: Options

        pkt[0] = (byte) ((sourcePort & 0xFF00) >> 8);
        pkt[1] = (byte) (sourcePort & 0xFF);
        pkt[2] = (byte) ((destinationPort & 0xFF00) >> 8);
        pkt[3] = (byte) (destinationPort & 0xFF);

        pkt[4] = (byte) ((sequenceNumber & 0xFF000000) >> 24);
        pkt[5] = (byte) ((sequenceNumber & 0xFF0000) >> 16);
        pkt[6] = (byte) ((sequenceNumber & 0xFF00) >> 8);
        pkt[7] = (byte) (sequenceNumber & 0xFF);

        pkt[8] = (byte) ((acknowledgementNumber & 0xFF000000) >> 24);
        pkt[9] = (byte) ((acknowledgementNumber & 0xFF0000) >> 16);
        pkt[10] = (byte) ((acknowledgementNumber & 0xFF00) >> 8);
        pkt[11] = (byte) (acknowledgementNumber & 0xFF);

        pkt[12] = (byte) (5 << 4);
        pkt[13] = (byte) (controlBits & 63);
        pkt[14] = (byte) ((window & 0xFF00) >> 8);
        pkt[15] = (byte) (window & 0xFF);

        pkt[16] = 0;
        pkt[17] = 0;
        pkt[18] = 0;
        pkt[19] = 0;

        System.arraycopy(data, 0, pkt, 20, data.length);
        System.arraycopy(checksum(), 0, pkt, 16, 2);
    }

    public byte[] checksum() {
        byte[] header = new byte[40];
        System.arraycopy(IPv6.SourceIP, 0, header, 0, IPv6.SourceIP.length);
        System.arraycopy(IPv6.destinationIP, 0, header, 16, IPv6.destinationIP.length);

        header[32] = (byte) ((pkt.length & 0xFF000000) >> 24);
        header[33] = (byte) ((pkt.length & 0xFF0000) >> 16);
        header[34] = (byte) ((pkt.length & 0xFF00) >> 8);
        header[35] = (byte) ((pkt.length & 0xFF));

        header[39] = (byte) 253;
        System.out.println(header[39]);
//                (byte)MyTcpHandler.VERSION;

        byte[] temp = new byte[header.length + pkt.length];
        System.arraycopy(header, 0, temp, 0, header.length);
        System.arraycopy(pkt, 0, temp, header.length, pkt.length);
        long result = 0;
        for (int i = 0; i < temp.length; i += 2) {
            long data = (i + 1 >= temp.length) ? ((temp[i] << 8) & 0xFF00) : (((temp[i] << 8) & 0xFF00) | (temp[i + 1] & 0xFF));
            result += data;
            if ((result & 0xFFFF0000) > 0) {
                result &= 0xFFFF;
                result++;
            }
        }
        result = ~result;
        result = result & 0xFFFF;
        return new byte[]{(byte) ((result & 0xFF00) >> 8), (byte) (result & 0xFF)};
    }
}