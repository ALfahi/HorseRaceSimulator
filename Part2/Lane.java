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
        setLaneNumber(laneNumber);
    }

    /**********setters *************/

    // this function will be used to add a horse to the lane
    //
    public void setHorse(Horse horse)
    {
        this.horse = horse;
        addHorseToLane();
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
        lane.setLayout(null);
        lane.setPreferredSize(new Dimension(distance, 50));
        lane.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        lane.setBackground(Color.GREEN); 
        return lane;
    }

    // This functions appends the horse's symbol to the lane
    //
    private void addHorseToLane()// for now it's a character
    {
        JLabel horseVisual = new JLabel(String.valueOf(horse.getSymbol()));
        horseVisual.setBounds(20, 25, 40, 40);
        lane.add(horseVisual);
        update();
    }

    // this function just updates the visuals
    //
    private void updateNumberVisual(int number)
    {
        JLabel numberVisual = new JLabel(String.valueOf(number));
        numberVisual.setFont(new Font("Arial", Font.BOLD, 24));
        
        // Set text color
        numberVisual.setForeground(Color.WHITE);
        numberVisual.setBounds(5, 25, 40, 40);
        lane.add(numberVisual);

        // update laneVisuals:
        update();
    }


    // this sets the lane number attribute with a new lane number
    //
    public void setLaneNumber(int laneNumber)
    {
        this.laneNumber = laneNumber;
        updateNumberVisual(laneNumber);
    }

    // this function updates the lane visuals.
    //
    private void update()
    {
        // update laneVisuals:
        lane.revalidate();
        lane.repaint();
    }
    
}
