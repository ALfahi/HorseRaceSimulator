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
 * @version 1.0 21/04/2025
 */
public class Track // TO DO: Make sure that all lanes have an equal length.
{

    private List<Lane> lanes;   // List of lanes
    private JPanel trackPanel;  // JPanel to display lanes

    // constructor
    //
    public Track() 
    {
        lanes = new ArrayList<>();
        trackPanel = new JPanel();
        trackPanel.setLayout(new BoxLayout(trackPanel, BoxLayout.Y_AXIS));  // Vertical stack of lanes
    }


    // Add a lane to the track and set it's length.
    //
    public void addLane(int length) 
    {
        Lane lane = new Lane(lanes.size() + 1, length);
        lanes.add(lane);  
        trackPanel.add(lane.getLane());  // Add the lane's JPanel to the track panel
        updatePanel();  // Refresh the track display
    }

    // Remove the last lane from the track
    //
    public void removeLane() 
    {
        if (!lanes.isEmpty()) 
        {
            lanes.remove(lanes.size() - 1);
            trackPanel.remove(trackPanel.getComponentCount() - 1);  // Remove the last JPanel from the display
            updatePanel();  
        }
    }

    public void clear()
    {
        lanes.clear();
        trackPanel.removeAll();
        updatePanel();
    }

    // Get the entire JPanel for the track (the visual component for the GUI)
    //
    public JPanel getTrackPanel() 
    {
        return trackPanel;
    }

    // Refreshes/ rerenders the track display
    //
    private void updatePanel() 
    {
        trackPanel.revalidate();  
        trackPanel.repaint();
    }

    // Get a specific lane based on its index (returns the Lane object)
    //
    public Lane getLane(int index) 
    {
        if (index >= 0 && index < lanes.size()) 
        {
            return lanes.get(index);
        } else 
        {
            return null;  // If the index is out of bounds
        }
    }

    // Get all the lanes
    //
    public List<Lane> getAllLanes() 
    {
        return lanes;
    }

    // Get a list of indexes for all empty lanes
    //
    public List<Integer> getEmptyLaneIndexes() 
    {
        List<Integer> emptyLanes = new ArrayList<>();
        for (int i = 0; i < lanes.size(); i++) 
        {
            if (lanes.get(i).getHorse() == null) // check if the lane is empty:
            {  
                emptyLanes.add(i);
            }
        }
        return emptyLanes;
    }

    // Get the count of total lanes
    //
    public int getLaneCount() 
    {
        return lanes.size();
    }
}
