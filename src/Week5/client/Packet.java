package Week5.client;

/**
 * Data packet consisting of source, destination and data
 * 
 * @author Jaco ter Braak & Frans van Dijk, University of Twente.
 * @version 13-03-2017
 */
/*
 * 
 * DO NOT EDIT
 */
public class Packet {
    private int sourceAddress;
    private int destinationAddress;
    private final DataTable dataTable;
    private final byte[] rawData;
    private boolean isRaw;

    /**
     * Instantiates a new packet with a {@link DataTable} as data.
     * @param sourceAddress int
     * @param destinationAddress int
     * @param dataTable a DataTable object. Can be a {@link DataTable} object with 0 columns, to represent no data.
     */
    public Packet(int sourceAddress, int destinationAddress, DataTable dataTable) {
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
        this.dataTable = dataTable;
        this.rawData = null;
        this.isRaw = false;
    }

    /**
     * Instantiates a new packet with a byte[] as data.
     * @param sourceAddress int
     * @param destinationAddress int
     * @param rawData a byte[].
     */
    public Packet(int sourceAddress, int destinationAddress, byte[] rawData) {
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
        this.dataTable = null;
        this.rawData = rawData;
        this.isRaw = true;
    }

    public int getSourceAddress() {
        return sourceAddress;
    }

    public int getDestinationAddress() {
        return destinationAddress;
    }

    /**
     * Gets the {@link DataTable} if this is not a raw packet.
     * @return the {@link DataTable} if not {@link #isRaw()}; otherwise {@code null}
     */
    public DataTable getDataTable() {
        return dataTable;
    }

    /**
     * Gets the {@code byte[]} data if this is a raw packet.
     * @return the {@code byte[]} data if {@link #isRaw()}; otherwise {@code null}
     */
    public byte[] getRawData() {
        return rawData;
    }

    /**
     * Indicates if this a raw packet or if it has a {@link DataTable} as data.
     * @return {@code true} if this is a raw packet; {@code false} otherwise.
     */
    public boolean isRaw() {
        return isRaw;
    }
}
