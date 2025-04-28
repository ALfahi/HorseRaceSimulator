package Part2;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/*
 * This is the Track class, this class will store all the lanes and handle both the back end and GUI aspect of the track
 *  Below are the attributes:
 * - lanes: this is an arrayList which stores the Lane class, this attribute is used to connect the track to the back end.
 * - trackPanel: this is a JPanel which stores all the lanes, it is used to visually display the lanes.
 * 
 * @author Fahi Sabab, Al
 * @version 1.2 24/04/2025
 * - the track now changes all the lane's background depending on the weather.
 */
public class Track
{
    private List<Lane> lanes;
    private JPanel trackPanel;
    private JScrollPane trackScrollPane;
    private static final Map<String, Color> WEATHERBACKGROUND = new HashMap<String, Color>();
    static 
    {
        WEATHERBACKGROUND.put("Normal", Color.GREEN);
        WEATHERBACKGROUND.put("Rainy", Color.decode("#1d404f"));
        WEATHERBACKGROUND.put("Sunny", Color.decode("#b1e868"));
        WEATHERBACKGROUND.put("Muddy", Color.decode("#6e4224"));
        WEATHERBACKGROUND.put("Icy", Color.decode("#95c4c9"));
    }


    // contructor of the track class., get's passed in the Race classe's lanes attribute when constructed.
    //
    public Track(List<Lane> lanes) 
    {
        this.lanes = lanes;
        trackPanel = new JPanel();
        trackPanel.setLayout(new BoxLayout(trackPanel, BoxLayout.Y_AXIS));

        for (Lane lane : lanes) {
            trackPanel.add(lane.getLane());
        }
        

        // Wrap track panel in scroll pane
        trackScrollPane = new JScrollPane(trackPanel);
        trackScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        trackScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        updatePanel();
    }

    // This method just refreshes the track panel, so that the changes can be seen visually.
    //
    public void refresh() 
    {
    
        trackPanel.removeAll();
        for (int i = 0; i < lanes.size(); i++) 
        {
            Lane lane = lanes.get(i);
            trackPanel.add(lane.getLane());
        }
        updatePanel();
    }

    // returns the entire track and all it's lanes
    // returns a JPanel
    //
    public JPanel getTrackPanel() 
    {
        return trackPanel;
    }

    // returns the entire track and it's scrollPane
    //
    public JScrollPane getTrackScrollPane()
    {
        return this.trackScrollPane;
    }

    // actually updates the panel visually.
    //
    private void updatePanel() 
    {
        trackPanel.revalidate();
        trackPanel.repaint();
    }

    // this set's the weather background for the track.
    //
    public void setWeather(String weather)
    {
        for (int i = 0; i < lanes.size(); i++)
        {
            lanes.get(i).changeColor(WEATHERBACKGROUND.get(weather));
        }
        updatePanel();
    }

}
