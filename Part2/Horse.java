package Part2;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Random;
/**
 * This is the Horse class, horses are instantiated from this class, they have the follwoing attributes:
 * - name: the horse's name
 * - type: what type of horse it is e.g. Wild, Arabian etc.
 * - horseImagePath: the image path to the corresponding horse icon, (used to display horse in GUI).
 * - horseSymbol: what symbol/ character to depict the horse during a race, (an alterantive way to display horse).
 * - distanceTravelled: how far the horse has travelled during a race.
 * - hasFallen: a boolean variable to check if the horse has fell
 * - confidence: a double variable depicting how confident a horse is where higher confidence = faster speeds but more prone to falling
 *               and vice versa.
 * - backUpSymbol: stores the horse's usual symbol.
 * - death symbol: what the horse will be displayed as when they fall.
 * - TYPETOSPEED: maps each possible horse type (minus wild horse) to a set speed multiplier (integer)
 *   if a horse has fallen, they can't move for that current race, and their confidence decreases
 *   if a horse wins a race, their confidence increases.
 * 
 * @author Fahi Sabab, Al
 * @version 1.9 26/4/2025
 * 
 * - added a new function and attribute to reset horse's base confidence to what the user initially inputted it as,
 *   it ignores previos wins and falls and starts froma  fresh new clean slate.
 * - fixed some rounding error when setting finishTime in horse records (did integer division by accident).
 */
public class Horse
{
    //Fields of class Horse
    private String name;
    private String type;
    private String horseImagePath = "";
    private char horseSymbol;
    private char backupSymbol;
    private final char deathSymbol = '❌';
    private double distanceTravelled;
    private int speed = 0;
    private boolean hasFallen;
    private double confidence;
    // creating some base stat values which will be used to reset horse stats prior to weather conditions
    // (so weather conditions don't stack, but wins/ losses should stack with base speed.)
    private int baseSpeed = speed;
    private double baseConfidence;
    private final double ORIGINALCONFIDENCE;
    private String item;
    private static final Map<String, Integer> TYPETOSPEED = new HashMap<String, Integer>();
    static {
        TYPETOSPEED.put("Arabian", 1);
        TYPETOSPEED.put("Quarter Horse", 2);
        TYPETOSPEED.put("Thoroughbred", 3);
    }
    private double finishTime = -1;
    private boolean finishedRace = false;

    private HorseRecord record;// a personal record for the horse class.
      
    /*
     * Constructors for objects of class Horse
     */
    
    // Base constructor holding shared logic
    private Horse(String horseName, double horseConfidence, String type, String item) 
    {
        this.name = horseName;
        this.confidence = horseConfidence;
        this.baseConfidence = horseConfidence;
        this.ORIGINALCONFIDENCE = horseConfidence;
        this.distanceTravelled = 0;
        this.hasFallen = false;
        this.type = type;
        this.item = item;

        if (!type.equals("Wild")) {
            this.speed = TYPETOSPEED.get(type);
            this.baseSpeed = this.speed;
        }

        // populate the horse record with initial values
        this.record = new HorseRecord(horseName, type, item);
        this.record.addConfidence(horseConfidence);
    }

    // constructor which uses symbols to display horse:
    public Horse(char horseSymbol, String horseName, double horseConfidence, String type, String item) 
    {
        this(horseName, horseConfidence, type, item);
        this.horseSymbol = horseSymbol;
        this.backupSymbol = horseSymbol;
    }
    
    // consturctor which uses an imagePath to display horse:
    public Horse(String imagePath, String horseName, double horseConfidence, String type, String item) 
    {
        this(horseName, horseConfidence, type, item);
        this.horseImagePath = imagePath;
    }
    
    
    /******getters and setters for the horse class. ******/
    
    // returns the current confidence of the horse as a double/
    //
    public double getConfidence()
    {
        return this.confidence;
    }

    // returns the base confidence of the horse (double)
    //
    public double getBaseConfidence()
    {
        return this.baseConfidence;
    }
    
    //returns the current distance travelled of the horse as an integer.
    //
    public double getDistanceTravelled()
    {
        return this.distanceTravelled;
    }
    
    //returns the name of the horse, as a string.
    //
    public String getName()
    {
        return this.name;
    }
    
    // returns a character, represeting the horse's symbol
    //
    public char getSymbol()
    {
        return this.horseSymbol;
    }

    // returns the backed up symbol
    //
    public char getBackUpSymbol()
    {
        return this.backupSymbol;
    }

    // returns the death or eliminated symbol of the horse
    //
    public char getDeathSymbol()
    {
        return this.deathSymbol;
    }

    // returns the specific image path for the horse. (returns a string)
    //
    public String getImagePath()
    {
        return this.horseImagePath;
    }

    // this returns the type of horse.
    //
    public String getType()
    {
        return this.type;
    }

    // This functions returns the speed with the modifier included.
    //
    public int getSpeed()
    {
       return this.speed;
    }

    // this function returns the base speed multiplier.
    //
    public int getBaseSpeed()
    {
        return this.baseSpeed;
    }

    // This function returns of what item the horse is currently holding:
    //
    public String getItem()
    {
        return this.item;
    }
    
    // returns a boolean value, depicting if the horse has fallen or not, returns true if it has fallen, false otherwise.
    //
    public boolean hasFallen()
    {
        return this.hasFallen;
    }

    // returns if the horse has finished the race.
    //
    public boolean hasFinishedRace()
    {
        return this.finishedRace;
    }

    // this function returns the record.
    //
    public HorseRecord getHorseRecord()
    {
        return this.record;
    }


    // this function gets passed in a value, and then the current confidence of the horse is set to the new value,
    // if the new value is > 1, the program will just make it be 1 and if the new value is < 0, the program will set it to 0, before
    // the current confidecne is set to the new value.
    //
    public void setConfidence(double newConfidence)
    {
        if (newConfidence <0.0)
        {
            newConfidence = 0.0;
        }
        else if (newConfidence > 1.0)
        {
            newConfidence = 1.0;
        }
       
        this.confidence = newConfidence;
       
    }

    // this function is used to set the base confidence:
    //
    public void setBaseConfidence(double newConfidence)
    {
        if (newConfidence <0.0)
        {
            newConfidence = 0.0;
        }
        else if (newConfidence > 1.0)
        {
            newConfidence = 1.0;
        }
       
        this.baseConfidence = newConfidence;
    }

    // this is used to 
    
    // set's the horse's symbol with a new symbol.
    //
    public void setSymbol(char newSymbol)
    {
        this.horseSymbol = newSymbol;
    }

    // used to set the finishedRace variable
    //
    public void setFinishedRace(boolean finished)
    {
        this.finishedRace = finished;
    }

    // this function sets the rececords loss numbber to the new passed in number.
    //
    public void setLoss(int loss)
    {
        if (loss >= 0)
        {
            record.setLossNumber(loss);
        }

    }

    // this function will allow us to set the speed of the horse.
    //
    public void setSpeed(int speed)
    {
        this.speed = speed;
    }
    
    // this function will set the new fastest finish time, but only if it's smaller than the previosu fastest finish time.
    //
    public void setFinishTime(long finishTime)// convert into seconds which is a more readable format.
    {
        double finishTimeInSeconds = finishTime / 1000.0;
        this.finishTime = finishTimeInSeconds ;// update most recent finish time.
        if ((finishTime < this.record.getFastestFinishTime()) && (this.record.getFastestFinishTime() != -1))
        {
            this.record.setFastestFinishTime(this.finishTime);
        }
        else if (this.record.getFastestFinishTime()  == -1)// the horse hasn't won yet, so any finish time is it's fastest finish time.
        {
            this.record.setFastestFinishTime(this.finishTime);
        }
        // also calculate and add the average speed for this race to the records.
        this.record.addAverageSpeed(this.distanceTravelled / finishTime);
    }

    /**************other methods for Horse class. **********/
    
    // if the horse has fallen, reduce it;s confidence by 10% and set the hasFallen attribute to true.
    //
    public void fall()
    {
        this.hasFallen = true;
        this.record.setFallCount(this.record.getFallCount() + 1);
        this.record.addAverageSpeed(-1.0);// give it a DNF value.
        this.record.addPosition(-1);// can't even finish race.
        if (!(this.item.equals("winner's saddle")))// decrease confidence only if horse isn't wearing the winner's saddle.
        {
            this.setBaseConfidence(this.getBaseConfidence() * 0.7);
            System.out.println(this.name + "base confidence has decreased");
        }

    }

    // incrments horse's current distance travelled by one
    //
    public void moveForward()
    {
        double baseMovement = this.distanceTravelled + getSpeed();
        if (this.item.equals("Speedy Horseshoe"))
        {
            baseMovement++;
        }

        if (this.item.equals("Balanced Horseshoe"))
        {
            baseMovement--;
        }

        if (!this.type.equals("Wild"))
        {
            this.distanceTravelled = baseMovement;
        }
        else
        {
            Random randomMovement = new Random();
            this.distanceTravelled = baseMovement + randomMovement.nextInt(5);
        }
    }
    // reset's the horse, except for it's confidence (i.e distance travelled and hasFallen attributes are back to their default)
    // values
    //
    public void goBackToStart()
    {
        this.distanceTravelled = 0;
        this.hasFallen = false;
        this.finishedRace = false;
        this.setConfidence(this.getBaseConfidence());
        this.setSpeed(this.getBaseSpeed());// this initialises the speed 
        this.setSymbol(this.getBackUpSymbol());
    }

    // This function will hard reset the horse's confidence back to what the user initially inputted the confidecne to be
    // (used whenever a new race starts)
    //
    public void hardResetConfidence()
    {
        this.baseConfidence = this.ORIGINALCONFIDENCE;
    }
}
