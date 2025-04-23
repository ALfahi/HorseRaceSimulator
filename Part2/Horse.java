package Part2;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
/**
 * This is the Horse class, horses are instantiated from this class, they have the follwoing attributes:
 * - name: the horse's name
 * - horseSymbol: what symbol/ character to depict the horse during a race.
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
 * @version 1.4 10/4/2025
 * - changed logic of how the horse moves by incoporating the different horse types.
 */
public class Horse
{
    //Fields of class Horse
    private String name;
    private String type;
    private char horseSymbol;
    private char backupSymbol;
    private final char deathSymbol = '❌';
    private int distanceTravelled;
    private boolean hasFallen;
    private double confidence;
    private static final Map<String, Integer> TYPETOSPEED = new HashMap<String, Integer>();
    static {
        TYPETOSPEED.put("Arabian", 1);
        TYPETOSPEED.put("Quarter Horse", 2);
        TYPETOSPEED.put("Thoroughbred", 3);
    }
      
    /*
     * Constructor for objects of class Horse
     */
    public Horse(char horseSymbol, String horseName, double horseConfidence, String type)
    {
        // initialise instance variables
        this.horseSymbol = horseSymbol;
        this.backupSymbol = this.horseSymbol;
        this.name = horseName;
        this.confidence = horseConfidence;
        this.distanceTravelled = 0;
        this.hasFallen = false;
        this.type = type;
       
    }
    
    
    /******getters and setters for the horse class. ******/
    
    // returns the current confidence of the horse as a double/
    //
    public double getConfidence()
    {
        return this.confidence;
    }
    
    //returns the current distance travelled of the horse as an integer.
    //
    public int getDistanceTravelled()
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

    // This functions gets the speed multiplier of the horse depending on it's type, if it's a wild horse, then speed is random.
    //
    public int getSpeed()
    {
        Random roll = new Random();
        if (!type.equals("Wild"))
        {
            return TYPETOSPEED.get(this.type);
        }
        // it's a wild horse.
        return roll.nextInt(5);// 0 - 4 is the multipluer for horses of wild types.
    }
    
    // returns a boolean value, depicting if the horse has fallen or not, returns true if it has fallen, false otherwise.
    //
    public boolean hasFallen()
    {
        return this.hasFallen;
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
    
    // set's the horse's symbol with a new symbol.
    //
    public void setSymbol(char newSymbol)
    {
        this.horseSymbol = newSymbol;
    }
    
    /**************other methods for Horse class. **********/
    
    // if the horse has fallen, reduce it;s confidence by 10% and set the hasFallen attribute to true.
    //
    public void fall()
    {
        this.hasFallen = true;
        this.setConfidence(this.getConfidence() * 0.9);
    }

    // incrments horse's current distance travelled by one
    //
    public void moveForward()
    {
        this.distanceTravelled = this.distanceTravelled  + getSpeed();
    }
    // reset's the horse, except for it's confidence (i.e distance travelled and hasFallen attributes are back to their default)
    // values
    //
    public void goBackToStart()
    {
        this.distanceTravelled = 0;
        this.hasFallen = false;
        this.setSymbol(this.getBackUpSymbol());
    }

    // method to print out the horse stats and name
    //
    public void printStats()
    {
        String formattedConfidenceString = HelperFunctions.displayNDecimalPlaces(this.getConfidence(),2 );// display confidence to 2dp
        System.out.println(this.getName() + "( current confidence " + formattedConfidenceString + ")");
    }
}
