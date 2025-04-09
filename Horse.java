
/**
 * Write a description of class Horse here.
 * 
 * @author Fahi Sabab, Al
 * @version 1.0 9/4/2025
 */
public class Horse
{
    //Fields of class Horse
    private String name;
    private char horseSymbol;
    private int distanceTravelled;
    private boolean hasFallen;
    private double confidence;
    
      
    //Constructor of class Horse
    /**
     * Constructor for objects of class Horse
     */
    public Horse(char horseSymbol, String horseName, double horseConfidence)
    {
        // initialise instance variables
        this.horseSymbol = horseSymbol;
        this.name = horseName;
        this.confidence = horseConfidence;
        this.distanceTravelled = 0;
        this.hasFallen = false;
       
    }
    
    
    
    //getters and setters for the horse class.
    
    public double getConfidence()
    {
        return this.confidence;
    }
    
    public int getDistanceTravelled()
    {
        return this.distanceTravelled;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public char getSymbol()
    {
        return this.horseSymbol;
    }
    
    
    public boolean hasFallen()
    {
        return this.hasFallen;
    }


    public void setConfidence(double newConfidence)
    {
        if (newConfidence <0.0 || newConfidence > 1.0)
        {
            System.out.println("Confidence must be between 0.0 and 1.0");
        }
        else
        {
            this.confidence = newConfidence;
        }
    }
    
    public void setSymbol(char newSymbol)
    {
        this.horseSymbol = newSymbol;
    }
    
    // Other horse methods.
    
    public void fall()
    {
        this.hasFallen = true;
    }
    public void moveForward()
    {
        
    }
    public void goBackToStart()
    {
        
    }
}
