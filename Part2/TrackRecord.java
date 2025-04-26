package Part2;
import java.util.ArrayList;
/* This class is just a record, and it will store some data about the race which will then be written to csv.
*
* @author Fahi Sabab, Al
* @version 1.1 26/4/2025
* 
* 
* - added in some getters, setters and useful helper functions to intialise this class. 
* - mapped out general structure and purpose of this class by using comments.
*/


public class TrackRecord 
{
    String trackDate;
    int length;
    int laneCount;
    int horseCount;
    ArrayList<String> weathers = new ArrayList<>(); 
    ArrayList<Double> averageSpeeds = new ArrayList<>();// average finish time speeds per round.
    double fastestFinishTime = -1;
    String fastestHorse;// name of horse who got fastest finish time in entire race so far.
    ArrayList<Integer> horseFalls = new ArrayList<>();
    int horsesWon;
    int totalRounds;

    // in race UI we just display round (totalRounds), current weather and fastestFinishTime + horse name;
    // in historical data section we display everything.


    // consturctor:

    public TrackRecord(String trackDate, int length, int laneCount, int horseCount)
    {
        this.trackDate = trackDate;
         this.length = length; 
         this.laneCount = laneCount;
         this.horseCount = horseCount;
    }
    /********** Getters **********/

    // returns the date when the race was started.
    public String getTrackDate() 
    {
        return trackDate;
    }

    // returns the race's length.
    public int getLength() 
    {
        return length;
    }

    // returns total lanes for this race.
    public int getLaneCount() 
    {
        return laneCount;
    }

    // returns total number of horses in this race.
    public int getHorseCount() 
    {
        return horseCount;
    }

    // returns the entire arrayList of weathers.
    public ArrayList<String> getWeathers() 
    {
        return weathers;
    }

    // returns the entire average speed array list.
    public ArrayList<Double> getAverageSpeed() 
    {
        return averageSpeeds;
    }


    // returns the fastest finish time.
    public double getFastestFinishTime() 
    {
        return fastestFinishTime;
    }


    // returns name of fastest horse in the race.
    public String getFastestHorse() 
    {
        return fastestHorse;
    }

    // returns the entire array list of horses falling
    //
    public ArrayList<Integer> getHorseFalls() 
    {
        return horseFalls;
    }

    // returns total number of wins in this race (i.e. every horse did not fall)
    //
    public int getHorsesWon() 
    {
        return horsesWon;
    }


    // returns the total number of rounds that this race had.
    public int getTotalRounds() 
    {
        return totalRounds;
    }

    /********** Setters **********/

    // set's/ records the track's date.
    //
    public void setTrackDate(String trackDate) 
    {
        this.trackDate = trackDate;
    }

    // set's/ records the tracks length
    public void setLength(int length) 
    {
        this.length = length;
    }

    // sets/ records total lanes in this race.
    public void setLaneCount(int laneCount) 
    {
        this.laneCount = laneCount;
    }

    // sets/ records total number of horses for this race.
    public void setHorseCount(int horseCount) 
    {
        this.horseCount = horseCount;
    }

    // set's the entire average speed array.
    public void setAverageSpeeds(ArrayList<Double> averageSpeeds) 
    {
        this.averageSpeeds = averageSpeeds;
    }

    // this function set's the fastest time and name of the track by the passed in time and string, if time passed in
    // if bigger than what we are currently storing, we just exit out.
    //
    public void setHorseLeaderData(long time, String horseName)
    {
        double newTime = time / 1000.0;// convert into seconds first.
        if ((this.fastestFinishTime == -1) || (newTime < this.fastestFinishTime))
        {
            this.fastestFinishTime = newTime;
            this.fastestHorse = horseName;
        }
    }


    // set's the entire horse falling arrayList.
    public void setHorseFalls(ArrayList<Integer> horseFalls) 
    {
        this.horseFalls = horseFalls;
    }

    // set's the new value for total amount of horses which won in this race.
    public void setHorsesWon(int horsesWon) 
    {
        this.horsesWon = horsesWon;
    }

    // set's the total rounds for this race.
    public void setTotalRounds(int totalRounds) 
    {
        this.totalRounds = totalRounds;
    }

    /********** Adders **********/

    // adds a new weather to the existing array list.
    //
    public void addWeather(String weather) 
    {
        this.weathers.add(weather);
    }


    // adds a new average speed into the average speed array list.
    //
    public void addAverageSpeed(double newSpeed)
    {
        this.averageSpeeds.add(newSpeed);
    }

    // This function just adds a new entry of how many horses fell during this round
    //
    public void addHorseFallStats(int fallCount)
    {
        this.horseFalls.add(fallCount);
    }
    /*******methods to return values in a more readable format **********/

    // Returns average speed formatted to 2 decimal places, or "n/a" if it's 0 or uninitialized
    public String getReadableAverageSpeed(int index)
    {
        if ((index < 0 ) || ( averageSpeeds.size() <= 0) || (index > averageSpeeds.size())  || (averageSpeeds.get(index) == -1.0))
        {
            return "n/a";
        }
        else
        {
            return String.format("%.2f", this.averageSpeeds.get(index)) + "m/s";
        }
    }

    // Returns fastest finish time formatted to 2 decimal places, or "n/a" if not yet set
    public String getReadableFastestFinishTime() 
    {
        if (fastestFinishTime == -1) 
        {
            return "n/a";
        }
        return String.format("%.2f", fastestFinishTime) + " s";
    }

    // Returns a readable string for fastest horse, or "n/a" if none recorded
    public String getReadableFastestHorse() 
    {
        if (fastestHorse == null || fastestHorse.isEmpty()) {
            return "n/a";
        }
        return fastestHorse;
    }


}

