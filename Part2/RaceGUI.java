package Part2;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

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
        this.screen.setSize(1200,800);// change these later.
        this.screen.setVisible(true);
        // closes application once user closes the window.
        this.screen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // general button template for all menu buttons in this application
        ButtonTemplate menuButtonTemplate = new ButtonTemplate(150, 200,  "#2E86C1",
         "#FFFFFF", "Arial", 16, Font.BOLD );

         // start screen panel
        Button startButton = new Button("start", menuButtonTemplate);
        Button[] startScreenButtons = {startButton};
        JPanel startScreen = createPanel(startScreenButtons, new CardLayout(), Color.ORANGE);


        // adding in all the panels to the frame/ screen.
        //
        this.screen.add(startScreen);
        
    }

    // checks if an instance is already made, if it isn't the'n we make a new instance of this class.
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
    /******************************* some functions to help create the GUI aspect***********/

    // this function get's passed in the buttons that the panel will contain (if any), the layout it will be useing
    // and it's background color to initialise and set up a JPanel, it will then return the panel.
    //
    private static JPanel createPanel(Button[] buttons, LayoutManager layout, Color color)
    {
        JPanel panel = new JPanel();
        panel.setLayout(layout);
        panel.setBackground(color);

        // add in the buttons
        for (int i = 0; i < buttons.length; i ++)
        {
            panel.add(buttons[i].convertToJButton());
        }
        return panel;
    }
}
