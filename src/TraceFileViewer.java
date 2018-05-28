import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;


public class TraceFileViewer extends JFrame implements ItemListener{

    private Set<String> sourceStrings = new HashSet<String>();
    private Set<String> destinationStrings = new HashSet<String>();
    private ArrayList<String> sourceSorted;
    private ArrayList<String> destinationSorted;
    private JRadioButton sourceRadioButton;
    private JRadioButton destinationRadioButton;
    private JRadioButton hostsRadioButton;
    private JRadioButton flowsRadioButton;
    private JComboBox<String> hostsComboBox;
    private GraphPanel graph;
    private ArrayList<TraceFileLine> traceFileLines;
    private ArrayList<Host> sourceHosts = new ArrayList<Host>();
    private ArrayList<Host> destinationHosts = new ArrayList<Host>();
    private ArrayList<Flow> flows = new ArrayList<Flow>();
    public static JRadioButton barsRadioButton;
    public static JRadioButton linesRadioButton;

    public TraceFileViewer() {
        super("Trace File Viewer");
        setSize(1000, 500);
        setLayout(null); // absolute positioning
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setupMenu();
        setupRadioButtons();
        setupGraphPanel();
        setupComboBox();
        setupFlowRadioButtons();
        setupBarLineRadioButtons();

        setVisible(true);

    }

    /**
     * creates the menubar with file --> open, quit
     */
    private void setupMenu() {
        // todo add open functionality

        // create menu bar
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // create option file
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        menuBar.add(fileMenu);

        // add open item under file
        JMenuItem fileMenuOpen = new JMenuItem("Open trace file");
        fileMenu.add(fileMenuOpen);

        // add quit option under file
        JMenuItem fileMenuQuit = new JMenuItem("Quit");
        fileMenu.add(fileMenuQuit);

        // quit functionality
        fileMenuQuit.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        System.exit(0);
                    }
                }
        );

        // open functionality
        fileMenuOpen.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        JFileChooser chooser = new JFileChooser(".");
                        FileNameExtensionFilter filter = new FileNameExtensionFilter("TXT files", "txt");
                        chooser.setFileFilter(filter);
                        int returnVal = chooser.showOpenDialog(TraceFileViewer.this);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            File f = chooser.getSelectedFile();
                            traceFileLines = new ArrayList<TraceFileLine>();
                            try {
                                Scanner sc = new Scanner(f);
                                while (sc.hasNextLine()) {
                                    TraceFileLine lineToAdd = new TraceFileLine(sc.nextLine());
                                    if (lineToAdd.getLineSize() == 16) {
                                        traceFileLines.add(lineToAdd);
                                    }
                                }
                                sc.close();
                            } catch (FileNotFoundException exception) {
                                // panic
                            }
                            // call this method to store file lines
                            getIPaddresses();
                        }
                    }
                }
        );
    }

    /**
     * creates and positions the radio buttons for selecting source/destination
     */
    private void setupRadioButtons() {
        // create both radio buttons
        sourceRadioButton = new JRadioButton("Source hosts");
        sourceRadioButton.setSelected(true);
        destinationRadioButton = new JRadioButton("Destination hosts");

        // action listeners for radio buttons
        sourceRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateComboBox();
            }
        });

        destinationRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateComboBox();
            }
        });

        // ButtonGroup for radio buttons
        ButtonGroup group = new ButtonGroup();
        group.add(sourceRadioButton);
        group.add(destinationRadioButton);

        // JPanel for radio buttons
        JPanel radioButtonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // add buttons to JPanel
        c.fill = GridBagConstraints.HORIZONTAL;
        radioButtonPanel.add(sourceRadioButton, c);

        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        radioButtonPanel.add(destinationRadioButton, c);

        radioButtonPanel.setLocation(0, 0);
        radioButtonPanel.setSize(200, 100);

        // add the JPanel to the JFrame
        add(radioButtonPanel);

    }

    private void setupFlowRadioButtons() {
        // create both radio buttons
        hostsRadioButton = new JRadioButton("Host view");
        hostsRadioButton.setSelected(true);
        flowsRadioButton = new JRadioButton("Flow view");
        hostsRadioButton.setVisible(false);
        flowsRadioButton.setVisible(false);

        // action listeners for radio buttons
        hostsRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateComboBox();
            }
        });

        flowsRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateComboBox();
            }
        });

        // ButtonGroup for radio buttons
        ButtonGroup group = new ButtonGroup();
        group.add(hostsRadioButton);
        group.add(flowsRadioButton);

        // JPanel for radio buttons
        JPanel radioButtonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // add buttons to JPanel
        c.fill = GridBagConstraints.HORIZONTAL;
        radioButtonPanel.add(hostsRadioButton, c);

        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        radioButtonPanel.add(flowsRadioButton, c);

        radioButtonPanel.setLocation(400, 0);
        radioButtonPanel.setSize(200, 100);

        // add the JPanel to the JFrame
        add(radioButtonPanel);

    }

    private void setupBarLineRadioButtons(){
        // create both radio buttons
        barsRadioButton = new JRadioButton("Bars");
        barsRadioButton.setSelected(true);
        linesRadioButton = new JRadioButton("Lines");

        // action listeners for radio buttons
        barsRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                graph.repaint();
            }
        });

        linesRadioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                graph.repaint();
            }
        });

        // ButtonGroup for radio buttons
        ButtonGroup group = new ButtonGroup();
        group.add(barsRadioButton);
        group.add(linesRadioButton);

        // JPanel for radio buttons
        JPanel radioButtonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // add buttons to JPanel
        c.fill = GridBagConstraints.HORIZONTAL;
        radioButtonPanel.add(barsRadioButton, c);

        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        radioButtonPanel.add(linesRadioButton, c);

        radioButtonPanel.setLocation(800, 0);
        radioButtonPanel.setSize(200, 100);

        // add the JPanel to the JFrame
        add(radioButtonPanel);

    }

    /**
     * creates the GraphPanel object which is a JPanel
     */
    private void setupGraphPanel() {
        graph = new GraphPanel();
        add(graph);
    }

    /**
     * creates the JComboBox on launch and sets to hidden
     */
    private void setupComboBox() {
        hostsComboBox = new JComboBox<String>();
        hostsComboBox.setLocation(200, 25);
        hostsComboBox.setSize(250, 50);
        hostsComboBox.setVisible(false);
        hostsComboBox.addItemListener(this);
        add(hostsComboBox);
    }

    /**
     * updates values in combobox
     * called by radiobuttons changing and opening file
     */
    private void updateComboBox() {
        hostsComboBox.removeAllItems();
        if (hostsRadioButton.isSelected()) {
            if (sourceRadioButton.isSelected()) {
                for (String i : sourceSorted) {
                    hostsComboBox.addItem(i);
                }
            } else if (destinationRadioButton.isSelected()) {
                for (String i : destinationSorted) {
                    hostsComboBox.addItem(i);
                }
            }
        } else {
            for (Flow flow : flows){
                hostsComboBox.addItem(flow.toString());
            }
        }
        hostsComboBox.setSelectedIndex(0);
        hostsComboBox.setVisible(true);
        hostsRadioButton.setVisible(true);
        flowsRadioButton.setVisible(true);

    }

    /**
     * loops through the lines of the TraceFile and adds each IP to the hashSet
     */
    private void getIPaddresses() {
        for (TraceFileLine line : traceFileLines){
            sourceStrings.add(line.getSourceIP());
            destinationStrings.add(line.getDestinationIP());
        }
        sortHashSet();
        updateComboBox();

        fillHosts();

    }

    /**
     * sort HashSet of IP addresses
     */
    private void sortHashSet() {
        ArrayList<InetAddress> sourceInet = new ArrayList<>();
        ArrayList<InetAddress> destinationInet = new ArrayList<>();

        try {
            for (String ip : sourceStrings) {
                sourceInet.add(InetAddress.getByName(ip));
            }
            for (String ip : destinationStrings) {
                destinationInet.add(InetAddress.getByName(ip));
            }
        } catch (UnknownHostException exception) {
            // panic
        }

        class InetAddressComparator implements Comparator<InetAddress> {
            public int compare(InetAddress adr1, InetAddress adr2) {
                byte[] ba1 = adr1.getAddress();
                byte[] ba2 = adr2.getAddress();

                for (int i = 0; i < ba1.length; i++) {
                    int b1 = unsignedByteToInt(ba1[i]);
                    int b2 = unsignedByteToInt(ba2[i]);
                    if (b1 == b2){
                        continue;
                    }
                    if (b1 < b2) {
                        return -1;
                    }
                    else {
                        return 1;
                    }
                }
                return 0;
            }
            private int unsignedByteToInt(byte b){
                return (int) b & 0xFF;
            }
        }
        InetAddressComparator c = new InetAddressComparator();
        Collections.sort(sourceInet, c);
        Collections.sort(destinationInet, c);

        sourceSorted = new ArrayList<String>();
        destinationSorted = new ArrayList<String>();
        for (InetAddress ip : sourceInet) {
            sourceSorted.add(ip.getHostAddress());
        }
        for (InetAddress ip : destinationInet) {
            destinationSorted.add(ip.getHostAddress());
        }
    }

    /**
     * fills the host ArrayLists with Host objects
     * also fills the host objects with their packets
     */
    private void fillHosts() {
        int i = 0;
        for (String ip : sourceSorted) {
            sourceHosts.add(new Host(ip));
            for (TraceFileLine packet : traceFileLines){
                if (packet.getSourceIP().equals(ip)) {
                    sourceHosts.get(i).addPacket(packet);
                }
            }
            i++;
        }
        int j = 0;
        for (String ip : destinationSorted) {
            destinationHosts.add(new Host(ip));
            for (TraceFileLine packet : traceFileLines){
                if (packet.getDestinationIP().equals(ip)) {
                    destinationHosts.get(j).addPacket(packet);
                }
            }
            j++;
        }
        for (Host host : sourceHosts){
            host.setMaxTimestamp();
            host.fillValues();
        }
        for (Host host : destinationHosts){
            host.setMaxTimestamp();
            host.fillValues();
        }
        fillFlows();

    }

    /**
     * fills the flows ArrayList with all possible combinations of a source
     * and destination.
     * fills flow objects with their packets
     */
    private void fillFlows() {
        int i = 0;
        for (String sourceIP : sourceSorted){
            for (String destinationIP : destinationSorted){
                flows.add(new Flow(sourceIP, destinationIP));
                    for (Host host : sourceHosts){
                        if (host.getIpAddress().equals(sourceIP)){
                            for (TraceFileLine packet : host.packets){
                                if (packet.getDestinationIP().equals(destinationIP)){
                                    flows.get(i).addPacket(packet);
                                }
                            }
                        }
                    }
                    i++;
            }
        }
        for (Flow flow: flows){
            flow.setMaxTimestamp();
            flow.fillValues();
        }
        // call to repaint immediately after processing file
        if (hostsRadioButton.isSelected()) {
            if (sourceRadioButton.isSelected()) {
                if (sourceHosts.size() > 1) {
                    graph.setValues(sourceHosts.get(hostsComboBox.getSelectedIndex()).getValues());
                }
            } else {
                if (destinationHosts.size() > 1) {
                    graph.setValues(destinationHosts.get(hostsComboBox.getSelectedIndex()).getValues());
                }
            }
        } else {
            graph.setValues(flows.get(hostsComboBox.getSelectedIndex()).getValues());
        }

    }

    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {

            if (hostsRadioButton.isSelected()) {
                if (sourceRadioButton.isSelected()) {
                    if (sourceHosts.size() > 1) {
                        graph.setValues(sourceHosts.get(hostsComboBox.getSelectedIndex()).getValues());
                    }
                } else {
                    if (destinationHosts.size() > 1) {
                        graph.setValues(destinationHosts.get(hostsComboBox.getSelectedIndex()).getValues());
                    }
                }
            } else {
                graph.setValues(flows.get(hostsComboBox.getSelectedIndex()).getValues());
            }
        }
        return;
    }

}
