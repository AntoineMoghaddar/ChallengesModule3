package Week4;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

class LongestPrefixMatcher {
    // TODO: Request access token with your student assistant
    public static final String ACCESS_TOKEN = "s1880241_j6fye";
    public static final String ROUTES_FILE = "routes.txt";
    public static final String LOOKUP_FILE = "lookup.txt";
    private Node root;

    /**
     * Main entry point
     */
    public static void main(String[] args) {
        System.out.println(ACCESS_TOKEN);
        new LongestPrefixMatcher();
    }

    /**
     * Constructs a new LongestPrefixMatcher and starts routing
     */
    public LongestPrefixMatcher() {
        root = new Node(-1);
        this.readRoutes();
        this.readLookup();
    }

    private class Node {
        int val = -1;
        private Node pos;
        private Node neg;

        /**
         * Constructor Node
         */
        public Node(int value) {
            this.val = value;
        }

        /**
         * Adds a new route to the trie
         */
        public void addRoute(int ip, int sub, int port, int index) {
            if (index < sub) {
                if ((ip >> (31 - index) & 1) == 1) {
                    if (pos == null) pos = new Node(val);
                    pos.addRoute(ip, sub, port, index + 1);
                } else {
                    if (neg == null) neg = new Node(val);
                    neg.addRoute(ip, sub, port, index + 1);
                }
            } else val = port;
        }

        /**
         * Finds a Node in the trie or return -1
         */
        public int lookup(int ip, int i, int res) {
            if ((ip >> (31 - i) & 1) == 1) return pos == null ? val : pos.lookup(ip, i + 1, val != -1 ? val : res);
            else return neg == null ? val : neg.lookup(ip, i + 1, val != -1 ? val : res);
        }
    }

    /**
     * Converts an integer representation IP to the human readable form
     *
     * @param ip The IP address to convert
     * @return The String representation for the IP (as xxx.xxx.xxx.xxx)
     */
    private String ipToHuman(int ip) {
        return Integer.toString(ip >> 24 & 0xff) + "."
                + Integer.toString(ip >> 16 & 0xff) + "."
                + Integer.toString(ip >> 8 & 0xff) + "."
                + Integer.toString(ip & 0xff);
    }

    /**
     * Reads routes from routes.txt and parses each
     */
    private void readRoutes() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(ROUTES_FILE));
            String line;
            while ((line = br.readLine()) != null) {
                this.parseRoute(line);
            }
        } catch (IOException e) {
            System.err.println("Could not open " + ROUTES_FILE);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Parses a route and passes it to this.addRoute
     */
    private void parseRoute(String line) {
        String[] split = line.split("\t");
        int portNumber = Integer.parseInt(split[1]);

        split = split[0].split("/");
        byte prefixLength = Byte.parseByte(split[1]);

        int ip = this.parseIP(split[0]);

        root.addRoute(ip, prefixLength, portNumber, 0);
    }

    /**
     * Reads IPs to look up from lookup.bin and passes them to this.lookup
     */
    private void readLookup() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(LOOKUP_FILE));
            int count = 0;
            StringBuilder sb = new StringBuilder(1024 * 4);

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(Integer.toString(root.lookup(this.parseIP(line), 0, -1))
                        + "\n");
                count++;

                if (count >= 1024) {
                    System.out.print(sb);
                    sb.delete(0, sb.capacity());
                    count = 0;
                }
            }

            System.out.print(sb);
        } catch (IOException e) {
            System.err.println("Could not open " + LOOKUP_FILE);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Parses an IP
     *
     * @param ipString The IP address to convert
     * @return The integer representation for the IP
     */
    private int parseIP(String ipString) {
        String[] ipParts = ipString.split("\\.");
        int ip = 0;
        for (int i = 0; i < 4; i++) {
            ip |= Integer.parseInt(ipParts[i]) << (24 - (8 * i));
        }
        return ip;
    }
}