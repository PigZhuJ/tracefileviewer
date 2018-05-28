import java.util.ArrayList;

public class Flow {
    private String sourceIP;
    private String destinationIP;
    private ArrayList<TraceFileLine> packets = new ArrayList<>();
    private float maxTimestamp = 0;
    private ArrayList<Integer> values = new ArrayList<>();

    public Flow(String sourceIP, String destinationIP) {
        this.sourceIP = sourceIP;
        this.destinationIP = destinationIP;
    }

    /**
     * sets the maxTimestamp parameter
     */
    public void setMaxTimestamp() {
        if (packets.size() > 0){
            maxTimestamp = packets.get(packets.size()-1).getTimestamp();
        }
    }

    /**
     * fills the values ArrayList with the volume of data per 2 sec
     */
    public void fillValues(){
        values.clear();
        int totalVolume;
        for (int i = 0; i < maxTimestamp; i+=2){
            totalVolume = 0;
            for (TraceFileLine packet : packets) {
                if (packet.getTimestamp() >= i && packet.getTimestamp() < i+2) {
                    totalVolume += packet.getPacketSize();
                }
            }
            values.add(totalVolume);
        }
    }

    /**
     * adds packets to the packets ArrayList
     * @param packet -- packet to add
     */
    public void addPacket(TraceFileLine packet){
        packets.add(packet);
    }

    /**
     * returns a String representation of this flow
     * @return String for comboBox
     */
    public String toString(){
        return sourceIP + " --> " + destinationIP;
    }

    /**
     * returns the values to graph
     * @return values to graph
     */
    public ArrayList<Integer> getValues() {
        return values;
    }
}
