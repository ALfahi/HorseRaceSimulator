package Part2;

import javax.swing.*;
import java.awt.*;


/**
 * This is the Lane class, this class will be used to create and store information of each lane
 *  Below are the attributes:
 * - laneNumber: which lane this is, (used for visuals)
 * - horse: checks to see if the lane contains a horse or not
 * - lane: the actual JPanel which will be used to display the lane.
 * - horseVisual: what the horse is displayed as to the screen (it's a JLabel)
 * - trackCondition: what type of condition the track has.
 * 
 * @author Fahi Sabab, Al
 * @version 1.2 20/04/2025
 * 
 * -- used the RaceGUI's scaleIcon method instead of making our own to to have better modularity and clarity.
 * 
 */
public class Lane 
{
    int laneNumber;
    Horse horse = null;
    JPanel lane = new JPanel();
    JLabel horseVisual = new JLabel();// this will move as the race goes along.
    ImageIcon horseIcon;
    JLabel finishLine;
    // some dimensions.
    final static  int SCALE = 20;
    final int FINISHLINEWIDTH = 50;
    final int LANEHEIGHT = 60;// lane width depends on the race length, so don't include here.
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

     // this sets the lane number attribute with a new lane number
    //
    public void setLaneNumber(int laneNumber)
    {
        this.laneNumber = laneNumber;
        updateNumberVisual(laneNumber);
    }

    // this function just sets a new distance value to the race track., it will also move the finish line to the new end of lane.
    //
    public void setDistance(int newDistance)
    {
        lane.setPreferredSize(new Dimension((newDistance * SCALE) + FINISHLINEWIDTH, LANEHEIGHT));

        // Move finish line to new right-side
        if (finishLine != null)
        {
            finishLine.setBounds(newDistance * SCALE, 0, FINISHLINEWIDTH, LANEHEIGHT);
        }
        // Revalidate to apply changes
        update();
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

    // this function returns the scale
    //
    public static int getScale()
    {
        return SCALE;
    }

    /**************** other functions***********/

    // this function creates the visual aspect of the lane
    //
    
    private JPanel createLane(int distance)
    {
        JPanel lane = new JPanel();
        lane.setLayout(null);
        lane.setPreferredSize(new Dimension((distance * SCALE) + FINISHLINEWIDTH, LANEHEIGHT));// add some padding to always show 
                                                                                               // finish line at end of lane.
        lane.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        lane.setBackground(Color.GREEN); 

        // draw on the finish line:
        finishLine = new JLabel(RaceGUI.scaleIcon("Part2/images/finishLine.png", FINISHLINEWIDTH, LANEHEIGHT));
        finishLine.setBounds(distance  * SCALE, 0, FINISHLINEWIDTH, LANEHEIGHT);

        lane.add(finishLine);
        return lane;

    
    }

    // This functions appends the horse's symbol to the lane
    //
    private void addHorseToLane()// for now it's a character
    {
        if (horse.getImagePath() == null)// user has decided to give this horse a symbol instead
        {
            this.horseVisual = new JLabel(String.valueOf(horse.getSymbol()));
        }
        else
        {
            this.horseIcon = RaceGUI.scaleIcon(horse.getImagePath(), 50, 50);
            this.horseVisual = new JLabel(this.horseIcon);
        }
        horseVisual.setBounds(15, 20, 50, 50);// x and y values here are redundant, they will get overwritten
                                                              // by update Horse visual.
        lane.add(this.horseVisual);
        updateHorseVisual();
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
        numberVisual.setBounds(5, 20, 40, 40);
        lane.add(numberVisual);

        // update laneVisuals:
        update();
    }

    // this function updates the horse's position in the lane:
    //
    public void updateHorseVisual()
    {
        if (horse != null && horseVisual != null) 
        {
            int paddingLeft = (2 * SCALE);
            int position = (horse.getDistanceTravelled() * SCALE) + paddingLeft; // scale: 1 step/ movement is 20px
            int yCenter = (lane.getPreferredSize().height - horseVisual.getPreferredSize().height) / 2;
            horseVisual.setLocation(position, yCenter);
            update();
        }
    }

    // this resets the horses visual to what they were displayed as before, either an image or a character.
    //
    public void resetHorseVisual()
    {
        if (this.horseIcon != null)// user chose to display horse as an image rather than a character
        {
            this.horseVisual.setIcon(this.horseIcon);
            return;
        }
        this.horseVisual.setText(String.valueOf(horse.getBackUpSymbol()));
    }

    // this function shows the horse's death/ eliminated symbol to the screen.
    //
    public void showEliminatedHorse()
    {
        this.horseVisual.setText(String.valueOf(horse.getDeathSymbol()));
        this.horseVisual.setIcon(null);// get rid of the image Icon.
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
