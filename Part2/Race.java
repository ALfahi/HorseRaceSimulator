package Part2;
import java.lang.Math;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Collections;
/**
 * A three-horse race, each horse running in its own lane
 * for a given distance
 * 
 * @author McRaceface, Fahi Sabab, Al
 * @version 1.23 28/4/2025
 * 
 * - gave the horse races a more realistic distance range.
 * - fixed bug of not resetting horse odds and bet amounts etc whenever a brand new race is started.
 * - fixed bug of always adding in an extra row when saving horse/ track data.
 * - added in check to prevent excess rows for betting data.
 * - added in function to remove any excess rows for both horse amd record data.
 *
 */
public class Race
{
    private final int MAXLANES = 10;// maximum number of lanes that the program will allow
    private final int MAXDISTANCE = 2400;
    private final int  MINDISTANCE= 500;
    private int raceLength = MINDISTANCE;// default value
    private List<Lane> lanes = new ArrayList<>();// use an arrayList to dynamically store the current number of lanes.
    private List<Horse> currentHorses = new ArrayList<>();// store all the used horses.
    public int activeHorses; // used to represent horses who have yet to finish race and has not fell.
    private int finishedCount = 0;// a counter of the number of horses who finished race.
    private int round = 0;
    private int totalFalls = 0;// keeps track of number of horses which fell this round.
    private int remainingHorses = 0;
    private String raceStartTime;
    private String currentWeather = "Normal";
    private TrackRecord record;
    private UserBets bettingRecord;
    private boolean hasBetted = false;// checks if user has already betted this round.
    private boolean raceStarted = false;
    private Horse bettedHorse;// this is here to keep track of which horse the user has betted for per round.
    private static final Map<String, double[]> AllWEATHER = new HashMap<String, double[]>();
    static {
        // double values are as follows: confidence (multiplier), speed (additivie), fall chance (multiplier)
        // smaller fall chance, more likley horse is to fall.
        AllWEATHER.put("Normal", new double[]{1, 0, 1});
        AllWEATHER.put("Rainy", new double[]{0.85, 0, 0.8});
        AllWEATHER.put("Sunny", new double[]{1.25, 0, 1});
        AllWEATHER.put("Muddy", new double[]{1, -1, 1.2});
        AllWEATHER.put("Icy", new double[]{0.8, -1, 0.5});
    }

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

    // this function will generate a new random weather:
    //
    public void setRandomWeather()
    {
        Random roll = new Random();
        double randomValue = roll.nextDouble();
        if (randomValue <= 0.4) // 40% to be normal weather
        {
            this.currentWeather = "Normal";
        }
        else if(randomValue <= 0.55) // 15% to be rainy
        {
            this.currentWeather = "Rainy";
        }

        else if (randomValue <= 0.7)// 15% to be sunny
        {
            this.currentWeather = "Sunny";
        }
        else if(randomValue <= 0.9)// 20% to be muddy
        {
            this.currentWeather = "Muddy";
        }
        else// 10% to be icy.
        {
            this.currentWeather = "Icy";
        }
        this.record.addWeather(this.currentWeather);
    }

    // this methods resets the attrbute for a fresh new race
    //
    public void resetRace()
    {
        this.lanes.clear();
        initialiseLanes(2);
        this.currentHorses = new ArrayList<>();
        this.remainingHorses = 0;
        this.raceLength = MINDISTANCE;// default value
        this.finishedCount = 0;
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
        // we will also generate a new betting odds value for the horse here.
        double confidenceModifier = AllWEATHER.get(this.currentWeather)[0];
        double FallChanceModifier =  AllWEATHER.get(this.currentWeather)[2];
        String[] currentWeatherValues = {this.currentWeather, String.valueOf(confidenceModifier), String.valueOf(FallChanceModifier)};
        theHorse.setOdds(Betting.calculateOdds(currentWeatherValues, this.record.getWeathers().toArray(new String[0]), 
        theHorse, currentHorses.size()));

        double baseMultiplier = 1.0;// base chance for horse to fall.
        if (theHorse.getItem().equals("Speedy Horseshoe"))
        {
            baseMultiplier = 0.75;
        }
        if (theHorse.getItem().equals("Balanced Horseshoe"))
        {
            baseMultiplier = 1.25;
        }
        if  (!theHorse.hasFallen())
        {
            if (Math.random() < theHorse.getConfidence())
            {
               theHorse.moveForward();
            }

            // consider the falling chance from the specific weather.
            if (Math.random() < (0.1 * theHorse.getConfidence() * theHorse.getConfidence() * AllWEATHER.get(this.currentWeather)[2]
             * baseMultiplier))
            {
                this.totalFalls++;
                theHorse.fall();
                this.activeHorses --;
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
    private boolean horsePassedFinishLine(Horse theHorse)
    {
        if (theHorse.getDistanceTravelled() >= raceLength)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    // checks if the passed in name is already in use by another horse.
    // if it is, then return false, otherwise return true.
    //
    public boolean isUniqueHorse(String name)
    {
        for (int i = 0; i < currentHorses.size(); i++)
        {
            if (currentHorses.get(i).getName().equals(name))
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
                double confidenceModifier = AllWEATHER.get(this.currentWeather)[0];
                double speedModifier = AllWEATHER.get(this.currentWeather)[1];
                // applying weather modifiers to horses, but only if the horse isn't wearing the weather proof jacket.
                if (!horse.getItem().equals("Weather proof jacket"))
                {
                    horse.setConfidence(horse.getConfidence() * confidenceModifier);
                    horse.setSpeed(horse.getSpeed() + (int) speedModifier);
                }
                horse.getHorseRecord().addConfidence(horse.getConfidence());// record confidence change.
                // reset the horse's visual to the normal horse symbol, and move it back to the start of the lane.
                lanes.get(i).resetHorseVisual();
                lanes.get(i).updateHorseVisual();
            }
        }
        
        remainingHorses = currentHorses.size();
        activeHorses = currentHorses.size();
        this.finishedCount = 0;
        this.totalFalls = 0;
        
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
                if (horse.hasFinishedRace()) 
                {
                    continue; // already finished race, move onto next horse.
                }

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

    //This function will get a horse, and then determine how good it's performance is inside the race.
    // returns how good the horse as a double.
    //
    private double getHorsePerformanceValue(Horse horse)
    {
        double winRatio = horse.getHorseRecord().getWinLossRatio() * 30;// random scale value
        double falls = horse.getHorseRecord().getFallCount();
        return winRatio - falls;

    }

    // This function will get let a simple AI/algorithm to bet on the selected horse.
    //
    private void AIBetOnHorse(Horse horse)
    {
        Random random = new Random();
        double amount = 0;
        double peformance = getHorsePerformanceValue(horse);
        if (peformance >= 60)
        {
            // AI will bet between 35 and 50
            amount = 35 + (50 - 35) * random.nextDouble();
        }
        else if (peformance >= 20)
        {
            // AI will bet between 15 and 35
            amount = 15 + (35 - 15) * random.nextDouble();
        }
        else
        {
            // AI will bet between MINBETAMOUNT and 20
            amount = Betting.MINBETAMOUNT + (20 - Betting.MINBETAMOUNT) * random.nextDouble();
        }
        horse.addBetAmount(amount);
    }

    // This function fill be used to decide if AI wats to randomly bet on a horse or not.
    //
    public void randomBet()
    {
        Random random = new Random();
        int roll =  random.nextInt(30);// 1/30 change for ai to bet on a random horse.
        if (roll == 0)
        {
            roll = random.nextInt(currentHorses.size());// pick out a random horse thats in the race.
            AIBetOnHorse(currentHorses.get(roll));
        }
    }

    // this function just sorts the arrayList of current horses based on distances, the first index contains the leading horse
    // (most distance covered)
    //
    private void sortHorsesByDistance() 
    {
        Collections.sort(currentHorses, (h1, h2) -> 
            Double.compare(h2.getDistanceTravelled(), h1.getDistanceTravelled()));
    }

    // This function checks and processes when a horse has passed the finishLine( finished the race)
    // preCondition: every horse passed in here has crossed the finish line.
    //
    private void processHorseFinish(Horse horse, long timer) 
    {
        long finishTime = System.currentTimeMillis() - timer;
    
        record.setHorseLeaderData(finishTime, horse.getName());
        horse.getHorseRecord().addPosition(finishedCount + 1);
    
        if (finishedCount == 0) // Winner
        { 
            horse.getHorseRecord().setWinNumber(horse.getHorseRecord().getWinNumber() + 1);
            this.record.getWinningHorses().add(horse.getName());
            
            if (!horse.getItem().equals("Winner's Saddle")) 
            {
                horse.setBaseConfidence(horse.getBaseConfidence() * 1.2);
            }
        } 
        else // Loser
        { 
            horse.getHorseRecord().setLossNumber(horse.getHorseRecord().getLossNumber() + 1);
        }
    
        horse.setFinishTime(finishTime);
        horse.setFinishedRace(true);
    
        activeHorses--;
        finishedCount++;
    }

    // This function checks to see if all remaining horses have crossed the lane. (ignores fallen horses)
    // returns a boolean value
    //
    public boolean didremainingHorseFinish(long timer)
    {
        sortHorsesByDistance();

        if (activeHorses > 0) {
            for (int i = 0; i < currentHorses.size(); i++) 
            {
                Horse horse = currentHorses.get(i);
                if (horse.hasFallen() || horse.hasFinishedRace()) 
                {
                    continue;
                }
    
                if (horsePassedFinishLine(horse)) 
                {
                    processHorseFinish(horse, timer);
                }
            }
        }
        return activeHorses <= 0;
    }

    // this gives horses who took too long a dnf placement and time. (-1)
    //
    public void giveDNFs()
    {
        for (int i = 0; i < currentHorses.size(); i++)
        {
            if ((currentHorses.get(i).hasFinishedRace() == false && !currentHorses.get(i).hasFallen()))
            {
                currentHorses.get(i).getHorseRecord().addPosition(-1);
                currentHorses.get(i).getHorseRecord().addAverageSpeed(-1.0);

            }
        }
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

    // this function returns the current weather.
    //
    public String getCurrentWeather()
    {
        return this.currentWeather;
    }

    // returns an arrayList of all active horses.
    //
    public List<Horse> getCurrentHorses()
    {
        return this.currentHorses;
    }

    // this function returns the stored race-time, (the first time the race was started from the race set up screen).
    //
    public String getRaceStartTime()
    {
        return this.raceStartTime;
    }

    //This function checks if the race has started.
    public boolean hasRaceStarted()
    {
        return this.raceStarted;
    }

    // This function is used to get the race's record.
    //
    public TrackRecord getRecord()
    {
        return this.record;
    }
    // this function returns the lead horse.
    //
    public Horse getLeadHorse()
    {
        double maxDistance = -1;
        Horse leadHorse = new Horse('a', null, maxDistance, "Arabian", "no Item");
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

    // This function will get the userBetting record.
    //
    public UserBets getBettingRecord()
    {
        return this.bettingRecord;
    }

    // Returns if user has already betted for this round(boolean)
    //
    public boolean hasUserBetted()
    {
        return this.hasBetted;
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

    // we can set if the race has started to true or false with this function
    //
    public void setStartedRace(boolean started)
    {
        this.raceStarted = started;
    }

    // this function will set the raceStartTime attribute:
    //
    public void setRaceStartTime(String newTime)
    {
        this.raceStartTime = newTime;
    }

    // This function increments the race's round varibale:
    //
    public void incrementRound()
    {
        this.round++;
        this.record.setTotalRounds(round);// make sure to update the record.
    }


    /******** functions relating to trackRecrod and HorseRecord ********/

    // this function will hard reset all horses (icluding their horse records)
    // and also create a fresh new TrackRecord.
    public void startRecord(String date)
    {
        this.bettedHorse = null;// reset this variable, as it needs to be reset every racem not every round.
        this.round = 0;// also initialise/ reset the round number to 1 again.
        // this also resets old record if it existed previously.
        this.record = new TrackRecord(date, raceLength, lanes.size(), currentHorses.size());
        this.bettingRecord = new UserBets(date);// reset the user betting history again.
        resetHorseRecords(date);
        resetHorseBets();
    }

    public void finaliseRaceRecord()
    {
        int totalWins = 0;
        double averageSpeed = 0.0;
        int horsesWithValidSpeeds = currentHorses.size();
        this.raceStarted = false;
        for (int i = 0; i < currentHorses.size(); i++)
        {
            // get each horse's record/ stats.
            HorseRecord horseStats = currentHorses.get(i).getHorseRecord();
            if ((round) >= horseStats.getAverageSpeed().size() || (round <0))// invalid ranges, just return from the function.
            {
                return;
            }

            totalWins = totalWins + horseStats.getWinNumber();
            if (horseStats.getAverageSpeed().get(round) != -1.0)
            {
                averageSpeed = averageSpeed + horseStats.getAverageSpeed().get(round);
            }
            else// horse had an invalid average speed.
            {
                horsesWithValidSpeeds --;
            }
        }

        if (averageSpeed <= 0.0)// all horses fell or no horses won for some reason
        {
            this.record.addAverageSpeed(-1.0);// DNF value
        }
        else
        {
            this.record.addAverageSpeed(averageSpeed / horsesWithValidSpeeds);// only use the valid horses with valid average speed
                                                                             // for calculation.
        }

        this.record.setHorsesWon(totalWins);
        this.record.addHorseFallStats(this.totalFalls);
        this.record.fillMissingData(this.round);
        if (this.record.getTotalRounds() < this.round)
        {
            this.record.setTotalRounds(this.round);
        }
    }

    // This function will reset the horse record to be brand new.
    //
    public void resetHorseRecords(String date)
    {
        for (int i = 0; i < currentHorses.size(); i ++)
        {
            currentHorses.get(i).hardResetConfidence();// reset confidence to what the user initially inputted it as (ignores
                                                       // wins and falls)
            currentHorses.get(i).getHorseRecord().reset(date);
        }
    }

    // This function will go around and reset the bet amount, total bets and odds on every horse.
    //
    private void resetHorseBets()
    {
        for (int i = 0; i < currentHorses.size(); i++)
        {
            currentHorses.get(i).setHorseBetAmount(0.0);
            currentHorses.get(i).setTotalBets(0);
            currentHorses.get(i).setOdds(0);
        }
    }

    // This function will just add a valid bet to the userBets record and also store the betted amount on the horse itself
    //
    public void handleUserBet(Horse horse, double amount)
    {
        if (!this.hasBetted)// user hasn't betted yet.
        {
            horse.addBetAmount(amount);// add the bet amount to the horse
            // store dat in the userBets record.
            bettingRecord.getNames().add(horse.getName());
            bettingRecord.getWinnings().add(amount);// this specific value will be overwirtten when user wins/ loses
            this.bettedHorse = horse;
        }
        this.hasBetted = true;

    }

    //This function checks if user has betted, and if they did, then thier results are recorded.
    //
    public void evaluateUserBet() 
    {
        double betAmount = 0;
        int winningsLastIndex = 0;
        double odds;
        int winningHorseIndex = this.record.getWinningHorses().size() -1;
        if (this.bettingRecord == null || this.bettedHorse == null) {
            return;
        }
    
        if (hasBetted) 
        { // check if the user has betted this round
            odds = this.bettedHorse.getOdds();
            betAmount = bettingRecord.getWinnings().get(winningsLastIndex);
            // if winning names are empty or the last winning horse does not match with what user has betted for: they lost.
            if (!this.record.getWinningHorses().isEmpty())
            {
                if (!this.record.getWinningHorses().get(winningHorseIndex).equals(this.bettedHorse.getName())) // user lost.
                {
                    bettingRecord.getWinRecord().add("no");
                    bettingRecord.getWinnings().set(winningsLastIndex, betAmount * -1);
                    bettingRecord.updateEarnings(bettingRecord.getWinnings().get(winningsLastIndex));
                }
                else// user won.
                {
                    bettingRecord.getWinRecord().add("yes");
                    bettingRecord.getWinnings().set(bettingRecord.getWinnings().size() - 1, betAmount * odds);
                    bettingRecord.updateEarnings(bettingRecord.getWinnings().get(winningsLastIndex));
                }
            }
            else
            {
                bettingRecord.getWinRecord().add("no");
            }
            this.hasBetted = false;// reset this.
        }
    }
    
    // This function will be used to write the track and horse records into their respective files.
    //
    public void saveData()
    {
        giveDNFs();
        finaliseRaceRecord();
        this.record.setTotalRounds(this.round);// before saving quicly update round to reflect most up to date version.

        // trim off any excess duplicate data for the track record.
        trimListToRound(this.record.getAverageSpeed());
        trimListToRound(this.record.getWinningHorses());
        trimListToRound(this.record.getHorseFalls());
        trimListToRound(this.record.getWeathers());
        CSVUtils.AppendToCSV("Part2/Records/TrackStats.csv", this.record);
        if (this.bettedHorse != null)// since this does not reset into null until whenever a brandnew race starts
        // we can use this to check if user even made a bet, if they didn't then no point of saving that file.
        {
            CSVUtils.AppendToCSV("Part2/Records/Bettings.csv", this.bettingRecord);
        }
        for (int i = 0; i < currentHorses.size(); i++)
        {
            // remove any duplicate redundant data.
            trimListToRound(currentHorses.get(i).getHorseRecord().getPosition());
            trimListToRound(currentHorses.get(i).getHorseRecord().getConfidence());
            trimListToRound(currentHorses.get(i).getHorseRecord().getAverageSpeed());
            CSVUtils.AppendToCSV("Part2/Records/horseStats.csv", currentHorses.get(i).getHorseRecord());
        }
    }

    //This function will get passed in a list and then truncate it to the right length (used to remove any redundant data)
    //
    private <T> void trimListToRound(List<T> list) {
        while (list.size() > this.round) 
        {
            list.remove(list.size() - 1);
        }
    }
}

