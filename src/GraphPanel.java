import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class GraphPanel extends JPanel  {

    private int xTicks = 16;
    private int yTicks = -1;
    private int yTicksScale;
    private int padding = 50;
    private int labelPadding = 25;
    private int tickSize = 4;
    private int maxTime;
    private ArrayList<Integer> values = new ArrayList<>();

    public GraphPanel () {
        // default graph settings
        setLocation(0, 100);
        setSize(1000, 325);
        setBackground(Color.WHITE);
    }

    /**
     * called when a IP has been selected.
     * sets the values ArrayList and repaints the panel
     * @param values -- the volume of bytes sent per 2 seconds by this IP
     */
    public void setValues(ArrayList<Integer> values){
        this.values = values;
        maxTime = values.size() * 2;
        if (values.size() > 0) {
            if (Collections.max(values) > 2000000) {
                yTicksScale = 400000;
            } else if (Collections.max(values) > 1000000) {
                yTicksScale = 200000;
            } else if (Collections.max(values) > 500000) {
                yTicksScale = 100000;
            } else if (Collections.max(values) > 100000){
                yTicksScale = 50000;
            } else {
                yTicksScale = 10000;
            }
            yTicks = Collections.max(values) / yTicksScale;
            xTicks = maxTime / 50;
        }

        if (yTicks == 0){
            yTicks = -1;
        }


        repaint();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;


        if (values.size() > 0) {
            double xScale = ((double) getWidth() - (2 * padding) - labelPadding) / (values.size() - 1);
            double yScale = ((double) getHeight() - 2 * padding - labelPadding) / (Collections.max(values));

            // get points to graph from values ArrayList
            ArrayList<Point> graphPoints = new ArrayList<>();
            for (int i = 0; i < values.size(); i++) {
                int x1 = (int) (i * xScale + padding + labelPadding);
                int y1 = (int) ((Collections.max(values) - values.get(i)) * yScale + padding);
                graphPoints.add(new Point(x1, y1));
            }
            if (yTicks != -1) {
                g2.setColor(Color.RED);
                for (int i = 0; i < graphPoints.size() - 1; i++) {
                    int x1 = graphPoints.get(i).x;
                    int y1 = graphPoints.get(i).y;
                    int x2 = graphPoints.get(i + 1).x;
                    int y2 = graphPoints.get(i + 1).y;
                    if (TraceFileViewer.linesRadioButton.isSelected()) {
                        g2.drawLine(x1, y1, x2, y2);
                    }
                    else {
                        g2.drawRect(x1, y1, (int) xScale, getHeight() - y1 - padding - labelPadding);
                    }
                }
            }

        }

        // lines for x axis
        for (int i = 0; i < xTicks + 1; i++) {
            int x0 = i * (getWidth() - padding * 2 - labelPadding) / xTicks + padding + labelPadding;
            int x1 = x0;
            int y0 = getHeight() - padding - labelPadding;
            int y1 = y0 + tickSize;
            g2.setColor(Color.BLACK);
            g2.drawLine(x0, y0, x1, y1);

            // draw centered label for each tick
            String tickLabel = i * 50 + "";
            FontMetrics metrics = g2.getFontMetrics();
            int labelWidth = metrics.stringWidth(tickLabel);
            g2.drawString(tickLabel, x0 - labelWidth / 2, y0 + labelPadding);
        }

        // y axis
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, padding + labelPadding, padding);
        g2.drawString("Volume [bytes]", padding - labelPadding, padding - labelPadding);
        // x axis
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, getWidth() - padding, getHeight() - padding - labelPadding);
        g2.drawString("Time [s]", getWidth()/2, getHeight() - labelPadding);

        // if actual data has been provided, draw the Y axis ticks
        if (yTicks != -1) {
            // lines for y axis
            for (int i = 0; i < yTicks + 1; i++) {
                int x0 = padding + labelPadding;
                int x1 = padding + labelPadding - tickSize;
                int y0 = getHeight() - ((i * (getHeight() - padding * 2 - labelPadding)) / yTicks + padding +labelPadding);
                int y1 = y0;
                g2.drawLine(x0, y0, x1, y1);

                // draw centered label for each tick
                String tickLabel;
                if (i * yTicksScale >= 1000000){
                    tickLabel = (double) i * yTicksScale / 1000000 + "M";
                }
                else if (i * yTicksScale > 0){
                    tickLabel = i * yTicksScale / 1000 + "k";
                }
                else {
                    tickLabel = i * yTicksScale + "";
                }
                g2.drawString(tickLabel, x0 - labelPadding * 2, y0 + 5);
            }
        }
    }


}
