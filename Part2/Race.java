package Part2;
import java.lang.Math;
import java.util.List;
import java.util.ArrayList;
/**
 * A three-horse race, each horse running in its own lane
 * for a given distance
 * 
 * @author McRaceface, Fahi Sabab, Al
 * @version 1.14 10/4/2025
 * 
 * - fixed bug to now check if hors distance is greather than or equal to length, rather than strictly equal.
 *
 */
public class Race
{
    private int raceLength = 30;// default value
    private List<Lane> lanes = new ArrayList<>();// use an arrayList to dynamically store the current number of lanes.
    private List<Horse> currentHorses = new ArrayList<>();// store all the used horses.
    private final int MAXLANES = 20;// maximum number of lanes that the program will allow
    private final int MAXDISTANCE = 30;
    private final int  MINDISTANCE= 100;
    private int remainingHorses = 0;

    /**
     * Constructor for objects of class Race
     * Initially there are no horses in the lanes
     * 
     * @param distance the length of the racetrack (in metres/yards...)
     */
    public Race() 
    {
        initialiseLanes(2);
    }

    /**********race set up **************/
    // this function insitialies the track with empty lanes, number of lanes is passed in
    //
    public void initialiseLanes(int numberOfLanes) 
    {
        for (int i = 0; i < numberOfLanes; i++) 
        {
            addLane();
        }
    }

    // this methods resets the attrbute for a fresh new race
    //
    public void resetRace()
    {
        this.lanes.clear();
        initialiseLanes(2);
        this.currentHorses = new ArrayList<>();
        this.remainingHorses = 0;
        this.raceLength = 30;// default value
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
    public void addHorse(Horse theHorse, int laneNumber)
    {
        lanes.get(laneNumber).setHorse(theHorse); // add the horse to the lane
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
        if (theHorse.getDistanceTravelled() >= raceLength)
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
    public void resetDistanceAllHorses()
    {
        for (int i = 0; i < lanes.size(); i++)
        {
            Horse horse = lanes.get(i).getHorse();
            if (horse != null)
            {
                horse.goBackToStart();
                // reset the horse's visual to the normal horse symbol, and move it back to the start of the lane.
                lanes.get(i).resetHorseVisual();
                lanes.get(i).updateHorseVisual();
            }
        }
        remainingHorses = currentHorses.size();
        
    }

    // goes through all active horses and moves them
    //
    public void moveAllHorses()
    {
        for (int i = 0; i< lanes.size(); i ++)
        {
            Horse horse = lanes.get(i).getHorse();
            if (horse != null)
            {
                moveHorse(horse);
                // if horse has fallen, make the visual be the 'X' emoji.
                if (horse.hasFallen())
                {
                    lanes.get(i).showEliminatedHorse();
                }
                lanes.get(i).updateHorseVisual();

            }
        }
    }

    // goes through all active horses and checks if any of them won, if yes, then output true, otherwise output false
    // returns boolean
    //
    public boolean checkWin()
    {
        boolean win = false;
        for (int i = 0; i < currentHorses.size(); i++)
        {
            if(raceWonBy(currentHorses.get(i)))
            {
                win = true;
                Horse winner = currentHorses.get(i);
                winner.setConfidence(winner.getConfidence() * 1.2);
                winner.setWin(winner.getWins() + 1);

                // now also go back to every  other horse and increase their loss count:
                for (int j = 0; j < currentHorses.size();j++)
                {
                    Horse horse = currentHorses.get(j);
                    if (!horse.equals(winner))
                    {
                        horse.setLoss(horse.getLosses() + 1);
                    }
                }
            }
        }
        return win;
    }

    /************** User validation **********/

    // checks if the lane is empty or not.
    //
    public boolean isLaneEmpty(int laneNumber)
    {
        return (lanes.get(laneNumber).getHorse() == null);
    }

    // checks if the lane is full or not. Returns boolean
    //
    public boolean isLaneFull()
    {
        return (lanes.size() == currentHorses.size());
    }

    // this function is used to check if at least 2 horses are inside the lanes (before race starts)
    //
    public boolean isAtLeastTwoHorses()
    {
        return currentHorses.size() >= 2;
    }

    // checks to see if the number of lanes is less than or equal to 2( can't have a race with fewer than 2 lanes)
    // returns boolean
    //
    public boolean overMinimumLanes()
    {
        return (lanes.size() > 2);
    }

    // check to see if number of lanes exceeds the maximum number of allowed lanes
    // returns boolean
    public boolean exceedsMaxLanes()
    {
        return lanes.size() >= MAXLANES;
    }

    /************lane management ******/

    // adds an empty lane to the race track.
    // precondition: lane is not full
    //
    public void addLane()
    {
        lanes.add(new Lane(lanes.size() + 1, raceLength));
    }

    //removes a lane in the race track
    // precondition: number of lanes is greater than 2
    //
    public void removeLane()
    {
        // if the current lane has a horse, remove that horse.
        if (lanes.get(lanes.size() -1).getHorse() != null)
        {
            currentHorses.remove(currentHorses.size() - 1);
        }
        lanes.remove(lanes.size() -1);
    }
   

    /*********getter methods */

    // this function gets the total amount of remaining horses.
    //
    public int getRemainingHorses()
    {
        return this.remainingHorses;
    }

    // returns the Lane from the specified index
    //
    public Lane getLane(int index)
    {
        return lanes.get(index);
    }

    // returns the entire arrayList of lanes
    //
    public List<Lane> getAllLanes()
    {
        return this.lanes;
    }
    // returns the number of lanes currently in the race.
    //
    public int getTotalLanes()
    {
        return lanes.size();
    }

    // returns the maximum number of lanes for the race class
    //
    public int getMaxLanes()
    {
        return MAXLANES;
    }

    // function to return the current race lenght of the track( returns an int)
    //
    public int getRaceLength()
    {
        return this.raceLength;
    }
    // this function returns an int representing minimum distance a track can be
    //
    public int getMaxDistance()
    {
        return MAXDISTANCE;
    }

    // this function returns an int representing the maximum distance a track can be
    //
    public int getMinDistance()
    {
        return MINDISTANCE;
    }

    // this function returns the lead horse.
    //
    public Horse getLeadHorse()
    {
        int maxDistance = -1;
        Horse leadHorse = new Horse('a', null, maxDistance, null);
        for (int i =0; i < currentHorses.size(); i++)
        {
            if (currentHorses.get(i).getDistanceTravelled() > maxDistance)
            {
                leadHorse = currentHorses.get(i);
                maxDistance = leadHorse.getDistanceTravelled();
            }
        }
        return leadHorse;
    }

    // This function is used to return the indexes of all empty lanes, returns an array of integers.
    //
    public int[] getAllEmptyLaneIndexes()
    {
        int nextFree = 0;
        int[] emptyLanes = new int[lanes.size() - currentHorses.size()];// assign just the right amount of space
        for (int i = 0; i < lanes.size(); i++)
        {
            if (lanes.get(i).getHorse() == null)
            {
                emptyLanes[nextFree] = i;
            }
        }
        return emptyLanes;
    }

    /**************setter methods ************/
    
    // this function get's a distance and then set's the race's length to that distance.
    //
    public void setRaceLength(int distance)
    {
        this.raceLength = distance;
        for (int i = 0; i < lanes.size(); i++)// also change the distances of the rest of the lanes
        {
            lanes.get(i).setDistance(distance);
        }
    }
}

