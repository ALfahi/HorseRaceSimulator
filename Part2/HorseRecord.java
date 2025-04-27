package Part2;
/* This class is just a record, and it will store some data about specific horses which will then be written to csv.
*
* @author Fahi Sabab, Al
* @version 1.3 25/4/2025
* 
* 
* - added and refactored some attributes to better store useful data.
*/
import java.util.ArrayList;

class HorseRecord
{
    String raceDate;
    String name;
    String breed;
    String item;
    ArrayList<Double> confidence = new ArrayList<>();
    int winNumber = 0;
    int lossNumber = 0;
    int fallCount = 0;
    double winLossRatio = 0.0;
    double fastestFinishTime = -1;// default value which will then be overwritten when the specific horse wins.
   // ArrayList<Double> finishTimes;
    ArrayList<Double> averageSpeed = new ArrayList<>();
    ArrayList<Integer> position = new ArrayList<>();

    // information to show in UI (not race histroy)
    // name, breed,  current Confidence, item, win number, lossNumber, winlossRatio, fastest finish time, 
    //average speed ( of previous race) and finally most recent placement in current race.

    // race histroy button: (of current horse, below the information of each horse, all horse information in another menu)
    //(NEEDS TO BE SAVED TO FILE BEFORE THESE CAN BE ACCESSED)
    // everything in this record.

    /******* contructor for this class */

    public HorseRecord(String name, String breed, String item)
    {
        this.name = name;
        this.breed = breed;
        this.item = item;
    }

    /******* Getters ************/

    // This function will the name stored in this class
    //
    public String getName() 
    {
        return name;
    }

    // returns the breed stored in this class
    //
    public String getBreed() 
    {
        return breed;
    }

    // returns the entire arrayList of confidence
    //
    public ArrayList<Double> getConfidence() 
    {
        return confidence;
    }

    // returns most recent confidence (the current confidene level)
    //
    public double getCurrentConfidence()
    {
        return this.confidence.get(this.confidence.size() -1);
    }

    // returns the item
    //
    public String getItem() 
    {
        return item;
    }

    // returns total number of wins
    //
    public int getWinNumber() 
    {
        return winNumber;
    }

    // returns total umber of losses
    //
    public int getLossNumber() 
    {
        return lossNumber;
    }

    // returns total number of falls
    public int getFallCount() 
    {
        return fallCount;
    }

    // returns the current winLossRatio (icludes falls)
    public double getWinLossRatio() 
    {
        return winLossRatio;
    }


    // returns the fasted Finish time
    public double getFastestFinishTime() 
    {
        return fastestFinishTime;
    }

    // returns the race date
    //
    public String getRaceDate() 
    {
        return raceDate;
    }

    // returns the entire arrayList for average speed
    //
    public ArrayList<Double> getAverageSpeed() 
    {
        return averageSpeed;
    }

    // returns the average speed of previous race.
    //
    public double getRecentAverageSpeed()
    {
        if (averageSpeed.size() > 0)
        {
            return averageSpeed.get(averageSpeed.size() - 1);
        }
        return 1.0;
    }

    // returns entire arrayList of positions
    //
    public ArrayList<Integer> getPosition() 
    {
        return position;
    }

    // returns position of the previsous race.
    //
    public int getPreviousPosition()
    {
        if (position.size() > 0)
        {
            return position.get(position.size() - 1);
        }
        return -1;// this is the first race so just return a default value of -1.
    }

    /******** Setters ********/

    // set's the name
    //
    public void setName(String name) 
    {
        this.name = name;
    }

    // sets the breed
    //
    public void setBreed(String breed) 
    {
        this.breed = breed;
    }

    // set's the entire confidence arrayList.
    //
    public void setConfidence(ArrayList<Double> confidence) 
    {
        this.confidence = confidence;
    }

    // sets the item.
    //
    public void setItem(String item) 
    {
        this.item = item;
    }

    // sets the win number and updates the win/loss ratio.
    public void setWinNumber(int winNumber) 
    {
        this.winNumber = winNumber;
        updateWinRatio();
    }

    // sets the loss number and updates the win/loss ratio.
    public void setLossNumber(int lossNumber) 
    {
        this.lossNumber = lossNumber;
        updateWinRatio();
    }

    // sets the fall count
    public void setFallCount(int fallCount) 
    {
        this.fallCount = fallCount;
    }

    // sets the new winLossRatio
    public void setWinLossRatio(double winLossRatio) 
    {
        this.winLossRatio = winLossRatio;
    }

    // set's the new fastest finish time
    public void setFastestFinishTime(double fastestFinishTime) 
    {
        this.fastestFinishTime = fastestFinishTime;
    }

    // sets the new race date
    public void setRaceDate(String raceDate) 
    {
        this.raceDate = raceDate;
    }

    // sets a new average speed arrayList
    public void setAverageSpeed(ArrayList<Double> averageSpeed) 
    {
        this.averageSpeed = averageSpeed;
    }

    // sets a new arrayList for positions
    public void setPosition(ArrayList<Integer> position) 
    {
        this.position = position;
    }

    /********* display functions  *********/

    // this will get the current winRatio number output a more readable string.
    //
    public String getReadableWinRatio()
    {
        return String.format("%.2f", this.winLossRatio * 100) + "%";
    }

    // this function will get the position at the specified index and return a more readable formatted text.
    //
    public String getReadablePosition(int index)
    {
        if ((position.size() == 0)  || (index < 0) || (index > position.size()) || (position.get(index) == -1))
        {
            return "n/a";// or mabye swap to "didn't finish race."
        }
        else
        {
            return String.valueOf(position.get(index));
        }
    }

    // This will get a string which will represent the speed in a more readable format.
    //
    public String getReadableAverageSpeed(int index)
    {
        if ((index < 0 ) || ( averageSpeed.size() <= 0) || (index > averageSpeed.size())  || (averageSpeed.get(index) == -1.0))
        {
            return "n/a";
        }
        else
        {
            return String.format("%.2f", this.averageSpeed.get(index)) + "m/s";
        }
    }

    // This will get a String which represents the fastest time in a more readable format.
    //
    public String getReadableFastestFinishTime()
    {
        if (this.fastestFinishTime == -1.0)
        {
            return "n/a";
        }
        else
        {
            return String.format("%.2f", this.fastestFinishTime) + "s";
        }
    }

    /********other functions ************/

    // adds in a new position at end of position list.
    //
    public void addPosition(Integer position)
    {
        this.position.add(position);
    }

    // this adds a new average speed at end of the average speed list.
    //
    public void addAverageSpeed(Double newSpeed)
    {
        this.averageSpeed.add(newSpeed);
    }

    // this adds a new confidence at the end of the confidence list.
    //
    public void addConfidence(Double newConfidence)
    {
        this.confidence.add(newConfidence);
    }

    // This function will update the win/loss ratio whenever it's called.
    //
    public void updateWinRatio()
    {
      if (this.winNumber + this.lossNumber >0)
      {
        this.winLossRatio = (this.winNumber)/ (double) (this.winNumber + this.lossNumber);
      }
      else
      {
        this.winLossRatio = 0.0;
      }
    }


    // this function is used to reset the record for all the data that needs to be reset (i.e. name will always be same 
    // so that won't be included here)

    public void reset(String raceDate)
    {
        this.confidence = new ArrayList<>();
        this.winNumber = 0;
        this.lossNumber = 0;
        this.fallCount = 0;
        this.winLossRatio = 0.0;
        this.fastestFinishTime = -1;
        this.raceDate = raceDate;
        this.averageSpeed = new ArrayList<>() ;
        this.position = new ArrayList<>();

    }

    /********* File IO methods ********/

}


/*
 * reset win ratio
 * wins
 * losses
 * falls
 * average speed
 * fastest finish time
 */