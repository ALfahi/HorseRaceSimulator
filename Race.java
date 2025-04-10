import java.util.concurrent.TimeUnit;
import java.lang.Math;
import java.util.List;
import java.util.ArrayList;
/**
 * A three-horse race, each horse running in its own lane
 * for a given distance
 * 
 * @author McRaceface, Fahi Sabab, Al
 * @version 1.6 9/4/2025
 * 
 * - User can now add in horses in real time
 *-  User can now start a race or replay a race without having to exit the program.
 * - added in some user validation
 */
public class Race
{
    private int raceLength;
    private List<Horse> lane = new ArrayList<Horse>();// use an arrayList to dynamically store the horses in the lanes.
    private List<Horse> currentHorses = new ArrayList<Horse>();// store all the used horses.
    private int maxLanes = 3;// max number of lanes available.
    private int remainingHorses = 0;

    /**
     * Constructor for objects of class Race
     * Initially there are no horses in the lanes
     * 
     * @param distance the length of the racetrack (in metres/yards...)
     */
    public Race(int distance)
    {
        // initialise instance variables
        this.raceLength = distance;
        for (int i = 0; i < maxLanes; i++)
        {
            lane.add(null); // add null to the list of horses
        }

    }
    // checks if the passed in symbol is already in use by another horse.
    // if it is, then return false, otherwise return true.
    //
    public boolean isUniqueHorse(char symbol)
    {
        for (int i = 0; i < currentHorses.size(); i++)
        {
            if (currentHorses.get(i).getSymbol() == symbol)// mabye extend this check to names as well later.
            {
                return false; // horse is not unique
            }
        }
        return true; // horse is unique
    }

    // checks if the lane is empty or not.
    //
    public boolean isLaneEmpty(int laneNumber)
    {
        return (lane.get(laneNumber - 1) == null);// check if the lane is empty
    }

    // checks if the lane is full or not.
    //
    public boolean isLaneFull()
    {
        return (lane.size() == currentHorses.size());// check if all lanes are full (e.g. number of horses are the same as number of lanes)

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
       
        lane.set(laneNumber - 1, theHorse); // add the horse to the lane
        currentHorses.add(theHorse); // add the horse to the list of  the current horses
        remainingHorses ++;
    }

    public  char getValidHorseSymbol(String message) 
    {
        String input;
        //System.out.print(message);
        
        input = helperFunctions.getInput(message);
        while (input.length() != 1 || !isUniqueHorse(input.charAt(0)))// check if the symbol is valid
        {
            if (input.length() != 1) // check if the input is a single character
            {
                System.out.println("Please enter a single character.");
            }
            else
            {
                System.out.println("sorry but this symbol is already in use. Please choose another symbol.");
            }
            input = helperFunctions.getInput(message);
        }
        return input.charAt(0); // return the valid character
    }

    public static String getValidHorseName(String message) // mabye extend this to prevent duplicate names.
    {
        String input = "";
        while (input.trim() == "") // name must be a non empty string.
        {
            input = helperFunctions.getInput(message);
        }
        return input; // return the valid name
    }

    public static double getValidHorseConfidence(String message)
    {
        double horseConfidence = -1.0;
        while (horseConfidence < 0 || horseConfidence > 1) // check if the confidence is valid
        {
            try{
                horseConfidence = Double.parseDouble(helperFunctions.getInput(message)); // get the horse confidence from the user
            }catch(NumberFormatException e)
            {
                System.out.println("Please enter a number between 0 and 1, as a decimal");
                horseConfidence = -1.0; // set the confidence to -1 to continue the loop
            }
            if (horseConfidence < 0 || horseConfidence > 1) // check if the confidence is inside valid range
            {
                System.out.println("must be between 0 and 1, please try again.");
            }
    
        }
        return horseConfidence; // return the valid confidence
    }

    // if -1 is returned, lanes are full, otherwise return a valid number.
    public  int getValidLaneNumber(String message)
    {
        int laneNumber = -1;
        while (laneNumber < 1 || laneNumber > maxLanes) // check if the lane number is valid
        {
            try{
                laneNumber = Integer.parseInt(helperFunctions.getInput(message)); // get the lane number from the user
            }catch(NumberFormatException e)
            {
                System.out.println(message);
                laneNumber = -1; 
            }

            if (laneNumber < 1 || laneNumber > maxLanes) // check if the lane number is inside valid range
            {
                System.out.println(message);
            }
            else if (!isLaneEmpty(laneNumber)) // check if the lane is empty
            {
                System.out.println("Lane " + laneNumber + " is already occupied by " + lane.get(laneNumber - 1).getName());
                laneNumber = -1; // set the lane number to -1 to continue the loop
            }
    
        }
        return laneNumber; // return the valid lane number
    }
    public void createValidHorse()
    {
    
        if (isLaneFull())
        {
            System.out.println("sorry but all lanes are full, we cannot add new horses at the moment")
        }
        else
        {
            char horseSymbol = getValidHorseSymbol("Enter the horse symbol: "); // get the horse symbol from the user
            String horseName = getValidHorseName("Enter the horse name: "); // get the horse name from the user
            double horseConfidence = getValidHorseConfidence("Enter the horse confidence (0.0 - 1.0): "); // get the horse confidence from the user
        
            Horse newHorse = new Horse(horseSymbol, horseName, horseConfidence); // create a new horse object
    
        
            int laneNumber = getValidLaneNumber("Enter the lane number must be between (1-" + maxLanes + "): "); // get the lane number from the user
            addHorse(newHorse, laneNumber);
        }
       

    }


    public void mainGameLoop()
    {
        String userInput = "";
        boolean endProgram = false;
        String  initialChoices= "do you want to add a horse (ADD), start the race (START) or end the program (END)? ";
        String postRaceChoices = "Do you want to replay the race (CONTINUE) or end the program (END)? ";
        while (!endProgram)
        {
            userInput = helperFunctions.getValidInput(initialChoices, new String[]{"ADD", "START", "END"}); // get the user input from the user
            if (userInput.equals("ADD"))
            {
                createValidHorse();
            }
            else if (userInput.equals("START"))
            {
                if (currentHorses.size() <2)
                {
                    System.out.println("we need at least 2 horses to start, currently we have " + currentHorses.size() + " horses.");
                }
                else
                {
                    startRace();
                    // either ask if you would like to start a new race or end the program. or continue with old race.
                    userInput = helperFunctions.getValidInput(postRaceChoices, new String[]{"CONTINUE", "END"});// add a option to start a new race from scratch.
                    if (userInput.equals("END"))
                    {
                        endProgram = true;
                    }
                }
            }
            else if (userInput.equals("END"))
            {
                System.out.println("Thanks for playing, goodbye.");
                endProgram = true; // end the program
            }
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
