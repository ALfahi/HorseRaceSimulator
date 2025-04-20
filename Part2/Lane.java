package Part2;

import javax.swing.*;
import java.awt.*;

/**
 * This is the Lane class, this class will be used to create and store information of each lane
 *  Below are the attributes:
 * - laneNumber: which lane this is, (used for visuals)
 * - horse: checks to see if the lane contains a horse or not
 * - lane: the actual JPanel which will be used to display the lane.
 * 
 * @author Fahi Sabab, Al
 * @version 1.0 20/04/2025
 * 
 */
public class Lane 
{
    int laneNumber;
    Horse horse = null;
    JPanel lane = new JPanel();
    private String trackCondition;

    // constructor for this class:
    public Lane(int laneNumber, int distance)
    {
        this.laneNumber = laneNumber;
        this.lane = createLane(distance);
    }

    /**********setters *************/

    // this function will be used to add a horse to the lane
    //
    public void setHorse(Horse horse)
    {
        this.horse = horse;
    }

    /************getters ************/

    // this get's the value of the horse attribute
    //
    public Horse getHorse()
    {
        return this.horse;
    }

    // this function returns the lane (a JPanel component).
    //
    public JPanel getLane()
    {
        return this.lane;
    }

    /**************** other functions***********/

    // this function creates the visual aspect of the lane
    //
    
    private JPanel createLane(int distance)
    {
        JPanel lane = new JPanel();
        lane.setPreferredSize(new Dimension(distance, 50));
        lane.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        lane.setBackground(Color.GREEN); 
        return lane;
    }
    
}
