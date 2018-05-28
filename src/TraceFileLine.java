public class TraceFileLine {

    private String lineString;
    private String sourceIP;
    private String destinationIP;
    private float timestamp;
    private int packetSize;

    public TraceFileLine(String lineString) {
        this.lineString = lineString;
        parseLine();

    }

    /**
     * sets parameters based on info from the lineString
     */
    private void parseLine() {
        if (getLineSize() == 16) {
            String[] line = lineString.split("\\t");
            timestamp = Float.valueOf(line[1]);
            sourceIP = line[2];
            destinationIP = line[4];
            packetSize = Integer.valueOf(line[7]);
        }
    }

    /**
     * returns the length of the line
     * @return length of line
     */
    public int getLineSize() {
        String[] line = lineString.split("\\t");
        return line.length;
    }

    /**
     * returns source IP address
     * @return sourceIP
     */
    public String getSourceIP() {
        return sourceIP;
    }

    /**
     * returns the destination IP address
     * @return destinationIP
     */
    public String getDestinationIP() {
        return destinationIP;
    }

    /**
     * returns the timestamp for this line
     * @return timestamp
     */
    public float getTimestamp() {
        return timestamp;
    }

    /**
     * returns the packet size of this line
     * @return packetSize
     */
    public int getPacketSize() {
        return packetSize;
    }

    /**
     * returns the string which was provided in the construction of this class
     * @return lineString
     */
    public String getString() {
        return lineString;
    }

}
