package Part2;
import java.util.concurrent.TimeUnit;
import java.lang.Math;
import java.util.List;
import java.util.ArrayList;
/**
 * A three-horse race, each horse running in its own lane
 * for a given distance
 * 
 * @author McRaceface, Fahi Sabab, Al
 * @version 1.10 10/4/2025
 * 
 * - removed all the unnecessary methods which won't be used for this GUI version
 * - added in some public validation methods.
 *
 */
public class Race
{
    private int raceLength;// default value
    private List<Horse> lane;// use an arrayList to dynamically store the horses in the lanes.
    private List<Horse> currentHorses;// store all the used horses.
    private final int MAXLANES = 20;// maximum number of lanes that the program will allow
    private int remainingHorses = 0;

    /**
     * Constructor for objects of class Race
     * Initially there are no horses in the lanes
     * 
     * @param distance the length of the racetrack (in metres/yards...)
     */
    public Race()
    {
        raceLength = 30;
        lane = new ArrayList<Horse>();
        
        // the race class must be initialised with 2 lanes
        for (int i = 0; i < 2; i++)
        {
            lane.add(null);
        }
        currentHorses = new ArrayList<Horse>();
    }

    /**********race set up **************/

    // This funcrion just initialises the lane attribute with nulls, it gets passed in an integer ( > 2) where it 
    // then creates that many lanes
    //
    private void initialiseLanes(int numberOfLanes)
    {
        for (int i = 0; i < numberOfLanes; i++)
        {
            lane.add(null);
        }
    }

    // this methods resets the attrbute for a fresh new race
    //
    private void resetRace()
    {
        this.lane = new ArrayList<Horse>();
        this.currentHorses = new ArrayList<Horse>();
        this.remainingHorses = 0;
        this.raceLength = 30;
    }

    /********** Horse management ************/

    /**
     * Adds a horse to the race in a given lane
     * 
     * @param theHorse the horse to be added to the race
     * @param laneNumber the lane that the horse will be added to (starts at 1)
     */
    // if lane is empty, add the horse to the lane, otherwise print out name of the horse that is already in the lane.
    //
    private void addHorse(Horse theHorse, int laneNumber)
    {
        lane.set(laneNumber - 1, theHorse); // add the horse to the lane
        currentHorses.add(theHorse); // add the horse to the list of  the current horses
        remainingHorses ++;
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
        if  (!theHorse.hasFallen())
        {
            if (Math.random() < theHorse.getConfidence())
            {
               theHorse.moveForward();
            }

            if (Math.random() < (0.1 * theHorse.getConfidence() * theHorse.getConfidence()))
            {
                theHorse.fall();
                remainingHorses --;
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
            System.out.println(theHorse.getName() + " has won!!!");
            return true;
        }
        else
        {
            return false;
        }
    }

    // checks if the passed in symbol is already in use by another horse.
    // if it is, then return false, otherwise return true.
    //
    private boolean isUniqueHorse(char symbol)
    {
        for (int i = 0; i < currentHorses.size(); i++)
        {
            if (currentHorses.get(i).getSymbol() == symbol)
            {
                return false;
            }
        }
        return true;
    }

    // This method goes through all the active horses and then makes them go back to start, also resetting their 
    // hasFallen attributes back to false.
    // also make remainingHorses be the size of the currentHorses arrayList again
    //
    private void resetDistanceAllHorses()
    {
        for (int i = 0; i < currentHorses.size(); i++)
        {
            currentHorses.get(i).goBackToStart();
        }
        remainingHorses = currentHorses.size();
    }

    // goes through all active horses and moves them
    //
    private void moveAllHorses()
    {
        for (int i = 0; i< currentHorses.size(); i ++)
        {
            moveHorse(currentHorses.get(i));
        }
    }

    // goes through all active horses and checks if any of them won, if yes, then output true, otherwise output false
    // returns boolean
    //
    private boolean checkWin()
    {
        boolean win = false;
        for (int i = 0; i < currentHorses.size(); i++)
        {
            if(raceWonBy(currentHorses.get(i)))
            {
                win = true;
                Horse winner = currentHorses.get(i);
                winner.setConfidence(winner.getConfidence() * 1.2);
            }
        }
        return win;
    }

    /************** User validation **********/

    // checks if the lane is empty or not.
    //
    private boolean isLaneEmpty(int laneNumber)
    {
        return (lane.get(laneNumber - 1) == null);
    }

    // checks if the lane is full or not. Returns boolean
    //
    private boolean isLaneFull()
    {
        return (lane.size() == currentHorses.size());
    }

    // checks to see if the number of lanes is less than or equal to 2( can't have a race with fewer than 2 lanes)
    // returns boolean
    //
    public boolean overMinimumLanes()
    {
        return (lane.size() > 2);
    }

    // check to see if number of lanes exceeds the maximum number of allowed lanes
    // returns boolean
    public boolean exceedsMaxLanes()
    {
        return lane.size() >= MAXLANES;
    }

    /************lane management ******/

    // adds an empty lane to the race track.
    // precondition: lane is not full
    //
    public void addLane()
    {
        lane.add(null);
    }

    //removes a lane in the race track
    // precondition: number of lanes is greater than 2
    //
    public void removeLane()
    {
        lane.remove(lane.size() -1);
    }

    /******* functions to handle the main game loop */

    /**
     * Start the race
     * The horse are brought to the start and
     * then repeatedly moved forward until the 
     * race is finished
     */
    public void launchRace()
    {
        boolean finished = false;

        resetDistanceAllHorses();    
        while (!finished)
        {
            moveAllHorses();

            finished = checkWin();

            if (remainingHorses == 0)
            {
                finished = true;
                System.out.println("All horses have fallen. No winner.");
            } 

            try{ 
                TimeUnit.MILLISECONDS.sleep(100);
            }catch(Exception e){}
        }
    }

    /*********getter methods */

    // returns the number of lanes currently in the race.
    //
    public int getTotalLanes()
    {
        return lane.size();
    }
}
