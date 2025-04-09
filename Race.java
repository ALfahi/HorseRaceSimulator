import java.util.concurrent.TimeUnit;
import java.lang.Math;
import java.util.List;
import java.util.ArrayList;
/**
 * A three-horse race, each horse running in its own lane
 * for a given distance
 * 
 * @author McRaceface
 * @version 1.0
 */
public class Race
{
    private int raceLength;
    private List<Horse> lane = new ArrayList<Horse>();// use an arrayList to dynamically store the horses in the lanes.
    private List<Horse> currentHorses = new ArrayList<Horse>();// store all the used horses.
    private int maxLanes;
    private int remainingHorses = 0;

    /**
     * Constructor for objects of class Race
     * Initially there are no horses in the lanes
     * 
     * @param distance the length of the racetrack (in metres/yards...)
     */
    public Race(int distance, int lanes)
    {
        // initialise instance variables
        this.raceLength = distance;
        this.maxLanes = lanes;
        for (int i = 0; i < lanes; i++)
        {
            lane.add(null); // add null to the list of horses
        }

    }
    
    /**
     * Adds a horse to the race in a given lane
     * 
     * @param theHorse the horse to be added to the race
     * @param laneNumber the lane that the horse will be added to (starts at 1)
     */
    // if lane is empty, add the horse to the lane, otherwise print out name of the horse that is already in the lane.
    public void addHorse(Horse theHorse, int laneNumber)
    {
        
        if (laneNumber > 0 && laneNumber <= maxLanes)
        {
            if (lane.get(laneNumber - 1) == null) // check if the lane is empty
            {
                lane.set(laneNumber - 1, theHorse); // add the horse to the lane
                currentHorses.add(theHorse); // add the horse to the list of  the current horses
                remainingHorses ++;
            }
            else
            {
                System.out.println("Lane " + laneNumber + " is already occupied by " + lane.get(laneNumber - 1).getName());
            }
        }
        else
        {
            System.out.println("Sorry but lane " + laneNumber + " does not exist. Max lanes are " + this.maxLanes);
        }
    }
    
    /**
     * Start the race
     * The horse are brought to the start and
     * then repeatedly moved forward until the 
     * race is finished
     */
    public void startRace()
    {
        //declare a local variable to tell us when the race is finished
        boolean finished = false;
        
        // reset all the lanes.
        for (int i = 0; i < currentHorses.size(); i++)
        {
           currentHorses.get(i).goBackToStart();
        }
                      
        while (!finished)
        {
            //move each horse
            for (int i = 0; i < currentHorses.size(); i++)
            {
                moveHorse(currentHorses.get(i)); // move the horse
            }
    
            //print the race positions
            printRace();
            
            //if any of the three horses has won the race is finished
            for (int i = 0; i < currentHorses.size(); i++)
            {
                if(raceWonBy(currentHorses.get(i)))
                {
                    finished = true;
                }
            }
           
            if (remainingHorses == 0) // check if all horses have fallen, if so then end the race early.
            {
                finished = true;
                System.out.println("All horses have fallen. No winner.");
            } 
            //wait for 100 milliseconds
            try{ 
                TimeUnit.MILLISECONDS.sleep(100);
            }catch(Exception e){}
        }
    }
    
    /**
     * Randomly make a horse move forward or fall depending
     * on its confidence rating
     * A fallen horse cannot move
     * 
     * @param theHorse the horse to be moved
     */
    private void moveHorse(Horse theHorse)
    {
        //if the horse has fallen it cannot move, 
        //so only run if it has not fallen
        if  (!theHorse.hasFallen())
        {
            //the probability that the horse will move forward depends on the confidence;
            if (Math.random() < theHorse.getConfidence())
            {
               theHorse.moveForward();
            }
            
            //the probability that the horse will fall is very small (max is 0.1)
            //but will also will depends exponentially on confidence 
            //so if you double the confidence, the probability that it will fall is *2
            if (Math.random() < (0.1*theHorse.getConfidence()*theHorse.getConfidence()))
            {
                theHorse.fall();
                remainingHorses --;// if horse has fallen, then decrement the number of remaining horses.
            }
        }
    }
        
    /** 
     * Determines if a horse has won the race
     *
     * @param theHorse The horse we are testing
     * @return true if the horse has won, false otherwise.
     */
    private boolean raceWonBy(Horse theHorse)
    {
        if (theHorse.getDistanceTravelled() == raceLength)
        {
            System.out.println(theHorse.getName() + " has won!!!");// print out name of horse that won
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /***
     * Print the race on the terminal
     */
    private void printRace()
    {
        System.out.print('\u000C');  //clear the terminal window
        
        multiplePrint('=',raceLength+3); //top edge of track
        System.out.println();
        
        for (int i = 0; i < lane.size(); i++)
        {
            //print the lane for each horse
            printLane(lane.get(i));
            System.out.println();
        }
        
        multiplePrint('=',raceLength+3); //bottom edge of track
        System.out.println();    
    }
    
    /**
     * print a horse's lane during the race
     * for example
     * |           X                      |
     * to show how far the horse has run
     */
    private void printLane(Horse theHorse)
    {   if (theHorse == null) // check if the lane is empty
        {
            printEmptyLane(); // print empty lane
            return; 
        }

        //calculate how many spaces are needed before
        //and after the horse
        int spacesBefore = theHorse.getDistanceTravelled();
        int spacesAfter = raceLength - theHorse.getDistanceTravelled();
        
        //print a | for the beginning of the lane
        System.out.print('|');
        
        //print the spaces before the horse
        multiplePrint(' ',spacesBefore);
        
        //if the horse has fallen then print dead
        //else print the horse's symbol
        if(theHorse.hasFallen())
        {
            System.out.print("\u274C");// wrong symbols
        }
        else
        {
            System.out.print(theHorse.getSymbol());
        }
        
        //print the spaces after the horse
        multiplePrint(' ',spacesAfter);
        
        //print the | for the end of the track
        System.out.print('|');
    }

    // method to print an empty lane.
    //
    public void printEmptyLane()
    {
        System.out.print("|");
        multiplePrint(' ', 30);
        System.out.println("|");
    }
        
    
    /***
     * print a character a given number of times.
     * e.g. printmany('x',5) will print: xxxxx
     * 
     * @param aChar the character to Print
     */
    private void multiplePrint(char aChar, int times)
    {
        int i = 0;
        while (i < times)
        {
            System.out.print(aChar);
            i = i + 1;
        }
    }
}
