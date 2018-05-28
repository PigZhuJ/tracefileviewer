import java.util.ArrayList;

public class Host {
    private String hostType;
    private String ipAddress;
    public ArrayList<TraceFileLine> packets = new ArrayList<>();
    private float maxTimestamp = 0;
    private ArrayList<Integer> values = new ArrayList<>();

    public Host(String ip) {
        this.ipAddress = ip;
        if (ip.matches("^192\\.168\\..+$")) {
            hostType = "source";
        }
        else if (ip.matches("^10\\.0\\..+$")) {
            hostType = "destination";
        }

    }

    /**
     * adds given packet to ArrayList of packets
     * @param packet -- packet to add
     */
    public void addPacket(TraceFileLine packet) {
        packets.add(packet);
    }

    /**
     * sets the maxTimestamp variable to the max timestamp
     */
    public void setMaxTimestamp() {
        if (packets.size() > 0){
            maxTimestamp = packets.get(packets.size()-1).getTimestamp();
        }
    }

    /**
     * fills the values ArrayList with the volume of data sent per 2 sec
     */
    public void fillValues() {
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
     * returns the values ArrayList
     * @return values ArrayList
     */
    public ArrayList<Integer> getValues() {
        return values;
    }

    public String getIpAddress() {
        return ipAddress;
    }



}
