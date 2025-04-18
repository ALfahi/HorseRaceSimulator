package Part2;
import javax.swing.*;

/**
 * This is the RaceGUI class, it will provide a GUI interface for the user for the horse race simulation.
 * This is a singleton class.
 *  Below are the attributes:
 * - screen: the screen/ container which will house the entire GUI.
 * - instance: holds the single active instance of RaceGUI (used to enforce the singleton property of this class)
 * 
 * @author Fahi Sabab, Al
 * @version 1.0 18/04/2025
 */
public class RaceGUI 
{
    private JFrame screen;
    private static RaceGUI instance = null;

    //constructor method for this class, initialises the screen.
    //
    private RaceGUI()
    {
        
        // initialise the screen
        this.screen = new JFrame("GUI Horse Race");
        this.screen.setSize(600,500);
        this.screen.setVisible(true);
        // closes application once user closes the window.
        this.screen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    }

    // checks if an instance is already made, if it isn't then we make a new instance of this class.
    // otherwise we return the current active instance
    // makes sure that this class is a singleton.
    //
    public static RaceGUI createGUI()
    {
        if (instance == null)
        {
            instance = new RaceGUI();
            start();

        }
        else
        {
            System.out.println("sorry but this application is already running somewhere else, cannot run it more than once");
        }
        return instance;
    }

    // creates an instance of the Race class which holds the actual logic of how the race works.
    //
    private static void start()
    {
        Race race = new Race();
        race.startRaceGUI();
    }
       
}
