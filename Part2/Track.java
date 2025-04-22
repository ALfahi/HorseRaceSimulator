package Part2;

import javax.swing.*;
import java.util.*;

/*
 * This is the Track class, this class will store all the lanes and handle both the back end and GUI aspect of the track
 *  Below are the attributes:
 * - lanes: this is an arrayList which stores the Lane class, this attribute is used to connect the track to the back end.
 * - trackPanel: this is a JPanel which stores all the lanes, it is used to visually display the lanes.
 * 
 * @author Fahi Sabab, Al
 * @version 1.1 21/04/2025
 * - the track now get's passed in a lanes arrayList which will be the same exact one as the one in the race class, makes both classes
 *  be synced.
 * - since race class adds/ removes lanes, removed every function that does not relate to the GUI.
 */
public class Track
{
    private List<Lane> lanes;
    private JPanel trackPanel;

    // contructor of the track class., get's passed in the Race classe's lanes attribute when constructed.
    //
    public Track(List<Lane> lanes) 
    {
        this.lanes = lanes;
        System.out.println(lanes);
        trackPanel = new JPanel();
        trackPanel.setLayout(new BoxLayout(trackPanel, BoxLayout.Y_AXIS));

        for (Lane lane : lanes) {
            trackPanel.add(lane.getLane());
        }
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

    // actually updates the panel visually.
    //
    private void updatePanel() 
    {
        trackPanel.revalidate();
        trackPanel.repaint();
    }
}
