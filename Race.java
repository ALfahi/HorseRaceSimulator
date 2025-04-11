import java.util.concurrent.TimeUnit;
import java.lang.Math;
import java.util.List;
import java.util.ArrayList;
/**
 * A three-horse race, each horse running in its own lane
 * for a given distance
 * 
 * @author McRaceface, Fahi Sabab, Al
 * @version 1.9 10/4/2025
 * 
 * - when a horse is eliminated it now displays 'X', rather than the placeholder due to unsupported unicode characters.
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


    // This function is used intialise the race with the number of lanes and alos the race length:
    //
    private void intitialiseRace()
    {
        final int MAXRACELENGTH = 100;
        final int MINRACELENGTH = 10;

        initialiseLanes(HelperFunctions.getValidInteger("please enter the amount of lanes that you want (minimum 2, max 20)", 2, MAXLANES));
        this.raceLength = HelperFunctions.getValidInteger("please enter in the race length as an integer (min 10, max 100)", MINRACELENGTH, MAXRACELENGTH);
    }
    //********** Horse management ************/

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

     // checks if the passed in symbol is already in use by another horse.
    // if it is, then return false, otherwise return true.
    //
    private boolean isUniqueHorse(char symbol)
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


     // this functions allows user to create a horse if the lanes are not full, uses the addHorse function to update both the lane
    // and currentHorses arrayLists.
    //
    private void createValidHorse()
    {
        int laneLength = lane.size();
        if (isLaneFull())
        {
            System.out.println("sorry but all lanes are full, we cannot add new horses at the moment");
        }
        else
        {
            char horseSymbol = getValidHorseSymbol("Enter the horse symbol: "); // get the horse symbol from the user
            String horseName = getValidHorseName("Enter the horse name: "); // get the horse name from the user
            double horseConfidence = getValidHorseConfidence("Enter the horse confidence (0.0 - 1.0): "); // get the horse confidence from the user
        
            Horse newHorse = new Horse(horseSymbol, horseName, horseConfidence); // create a new horse object
    
        
            int laneNumber = getValidLaneNumber("Enter the lane number must be between (1-" + laneLength + "): "); // get the lane number from the user
            addHorse(newHorse, laneNumber);
        }
       

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
    private boolean  checkWin()
    {
        boolean win = false;
        //if any of the horses has won the race is finished
        for (int i = 0; i < currentHorses.size(); i++)
        {
            if(raceWonBy(currentHorses.get(i)))
            {
                win = true;
                Horse winner =currentHorses.get(i);
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
        return (lane.get(laneNumber - 1) == null);// check if the lane is empty
    }

    // checks if the lane is full or not. Returns boolean
    //
    private boolean isLaneFull()
    {
        return (lane.size() == currentHorses.size());// check if all lanes are full (e.g. number of horses are the same as number of lanes)

    }

    
    // this function makes sure that the user types in a valid Horse symbol before the program moves on
    // returns the valid symbol
    //
    private  char getValidHorseSymbol(String message) 
    {
        String input;
        input = HelperFunctions.getInput(message);
        while (input.length() != 1 || !isUniqueHorse(input.charAt(0)) || input.equals("X"))// check if the symbol is valid
        {
            if (input.length() != 1) // check if the input is a single character
            {
                System.out.println("Please enter a single character.");
            }
            else if (input.equals("X"))
            {
                System.out.println("Sorry but this character is reserved for a special purpose");
            }
            else
            {
                System.out.println("sorry but this symbol is already in use. Please choose another symbol.");
            }
            input = HelperFunctions.getInput(message);
        }
        return input.charAt(0); // return the valid character
    }

    //This function makes sure that the user types in a valid horse name before the program moves on,
    // returns the valid name
    //
    private static String getValidHorseName(String message) // mabye extend this to prevent duplicate names.
    {
        String input = "";
        while (input.trim() == "") // name must be a non empty string.
        {
            input = HelperFunctions.getInput(message);
        }
        return input; // return the valid name
    }

    // makes sure user inputs in a valid confidence value during initial set up of horse
    // returns a double value representing the valid confidence
    //
    private static double getValidHorseConfidence(String message)
    {
        double horseConfidence = -1.0;
        while (horseConfidence < 0 || horseConfidence > 1) // check if the confidence is valid
        {
            try{
                horseConfidence = Double.parseDouble(HelperFunctions.getInput(message)); // get the horse confidence from the user
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

    // gets valid Lane number from user when entering in a lane for the horse
    // precondition: lanes are not full
    // retuns an integer representing the lane number.
    //
    private  int getValidLaneNumber(String message)
    {
        int laneNumber = -1;
        int laneLength = lane.size();
        while (laneNumber < 1 || laneNumber > laneLength) // check if the lane number is valid
        {
            try{
                laneNumber = Integer.parseInt(HelperFunctions.getInput(message)); // get the lane number from the user
            }catch(NumberFormatException e)
            {
                System.out.println(message);
                laneNumber = -1; 
            }

            if (laneNumber < 1 || laneNumber > laneLength) // check if the lane number is inside valid range
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

    /******* functions to handle the main game loop */
   

    // This is the main loop and entry point of the program, user can start races, add horses and end the program from here
    // they can also start races from scratch.
    //
    public void mainGameLoop()
    {
        String userInput = "";
        while (!userInput.equals("END"))
        {
            userInput = handleIndivisualRace();// will either be new race, or end program, if it;s end program it will exit out of
            // the game, otherwise repeat the entire loop from scratch.
            if (userInput.equals("NEW"))
            {
                resetRace();
            }
        }
    }

    /**
     * Start the race
     * The horse are brought to the start and
     * then repeatedly moved forward until the 
     * race is finished
     */
    private void startRace()
    {
        boolean finished = false;

        resetDistanceAllHorses();// reset all horses except for their confidence.     
        while (!finished)
        {
            moveAllHorses();// move all horses, some horses may fall
    
            //print the race positions
            printRace();
            
            // check if a horse has won yet.
            finished = checkWin();
        
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

    
    // This function handles the main user actions which relates to an indivisual race (e.g. add horse, replay race, end program etc)
    //
    private String handleIndivisualRace()
    {
        String userInput = "";
        String  initialChoices= "do you want to add a horse (ADD), start the race (START), create a new race (NEW) or end the program (END)? ";
        String postRaceChoices = "Do you want to replay the race (CONTINUE), start a new race from scratch (NEW) or end the program (END)? ";
        
        intitialiseRace();
        while (!userInput.equals("END") && !userInput.equals("NEW"))
        {
            userInput = HelperFunctions.getValidInput(initialChoices, new String[]{"ADD", "START", "NEW", "END"}); // get the user input from the user
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
                    userInput = HelperFunctions.getValidInput(postRaceChoices, new String[]{"CONTINUE", "END", "NEW"});// add a option to start a new race from scratch.
                }
            }
        }
        return userInput;// either they want to end program to make a new race.
    }

    
    
    /******* functions relating to how the program looks in the terminal **********/
    

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
    {   
        final int MARGINFORSTATS = 30;
        if (theHorse == null) // check if the lane is empty
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
            System.out.print("X");// symbol to depict that horse has fallen over.
        }
        else
        {
            System.out.print(theHorse.getSymbol());
        }
        
        //print the spaces after the horse
        multiplePrint(' ',spacesAfter);
        
        //print the | for the end of the track
        System.out.print('|');
        multiplePrint(' ', MARGINFORSTATS);// give consistent spacing between lane and stats.
        theHorse.printStats();// print out name and confidence of the horse.
        
    }

    // method to print an empty lane.
    //
    private void printEmptyLane()
    {
        System.out.print("|");
        multiplePrint(' ', raceLength);
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
