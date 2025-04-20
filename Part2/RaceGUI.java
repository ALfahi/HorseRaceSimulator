package Part2;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

/**
 * This is the RaceGUI class, it will provide a GUI interface for the user for the horse race simulation.
 * This is a singleton class.
 *  Below are the attributes:
 * - screen: the screen/ container which will house the entire GUI.
 * - instance: holds the single active instance of RaceGUI (used to enforce the singleton property of this class)
 * 
 * @author Fahi Sabab, Al
 * @version 1.3 20/04/2025
 * - each screen is inside it's own method for extra clairity and readability.
 */
public class RaceGUI 
{
    private JFrame screen;
    private static Race race = new Race();
    private static JPanel track = new JPanel();
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

        // creating the main panel which will be used to swap between the different screens:
        CardLayout cardLayout = new CardLayout();
        JPanel cardContainer = new JPanel(cardLayout);

        // general button template for all menu buttons in this application
        ButtonTemplate menuButtonTemplate = new ButtonTemplate(120, 200,  "#2E86C1",
         "#FFFFFF", "Arial", 24, Font.BOLD );


        // creating all of the screens
        JPanel startScreen = createStartScreen(cardLayout, cardContainer, menuButtonTemplate);
        JPanel raceSetupScreen = createRaceSetupScreen(cardLayout, cardContainer, menuButtonTemplate);
        JPanel editTrackScreen = createEditTrackScreen(cardLayout, cardContainer, menuButtonTemplate);


        // adding in all the panels to the frame/ screen.
        //
        this.screen.add(cardContainer);
        cardContainer.add(startScreen, "startScreen");
        cardContainer.add(raceSetupScreen, "raceSetupScreen");
        cardContainer.add(editTrackScreen, "editTrackScreen");
        
        // show the starting screen:
        cardLayout.show(cardContainer, "startScreen");
        
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
            System.out.println("Current Working Directory: " + System.getProperty("user.dir"));

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
        final int MINIMUMLANES = 2;
        race.initialiseLanes(MINIMUMLANES);
        initialiseTrack(MINIMUMLANES);
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
            panel.add(buttons[i].getJButton());
        }
        return panel;
    }

    // this allows us to make icons into buttons
    //
     private ImageIcon scaleIcon(String filePath, int width, int height) 
     {
        try 
        {
            BufferedImage originalImage = ImageIO.read(new File(filePath));
            Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            return null;
        }
    }
    /*********** button action methods ********/

    // function to handle when the user wants to press a button to add lanes
    //
    private void addLane(ActionEvent e, Button minusButton) 
    {
        JButton button = (JButton) e.getSource(); // this just gets the button which called the function
    
        if (!race.exceedsMaxLanes()) 
        {
            race.addLane();
            Lane lane = new Lane(1, 100);// testing values.
            track.add(lane.getLane());
            updateTrack();

            System.out.println("number of lanes is " + race.getTotalLanes());
            if (!minusButton.isEnabled())// if minus button was disabled before, we added in an extra lane which can be deleted
            // there enable button again.
            {
                minusButton.setEnabled(true);
            }
        } 
        else 
        {
            System.out.println("Max lanes reached.");
            button.setEnabled(false);
        }
    }

    // function to handle when the user wants to click a button to remove a lane
    //
    private void removeLane(ActionEvent e, Button plusButton)
    {
        JButton button = (JButton) e.getSource();
        if (race.overMinimumLanes())
        {
            race.removeLane();
            // remove the last lane from the track:
            track.remove(track.getComponent(race.getTotalLanes() - 1));
            updateTrack();

            System.out.println("number of lanes is " + race.getTotalLanes());
            if(!plusButton.isEnabled())
            {
                plusButton.setEnabled(true);
            }
            // edge case if we were at 3 lanes before this function call, disable it now again
            //
            if (race.getTotalLanes() <= 2)
            {
                button.setEnabled(false);
            }
        }
    }

    /*********functions to update GUI */

    private static void initialiseTrack(int totalLanes)
    {
        for (int i = 0; i < totalLanes; i++)
        {
            track.add(new Lane(1, 100).getLane());

        }
        updateTrack();
    }
    private static void updateTrack()
    {
        // Revalidate and repaint the track panel to update the display
        track.revalidate();
        track.repaint();
    }

    /*****************  all the screens ***********/

    // this will screate the start screen, returns the corresponding JPanel
    //
    private JPanel createStartScreen(CardLayout cardLayout, JPanel cardContainer, ButtonTemplate template) 
    {
        Button startButton = new Button("start", template);
        startButton.addPanelSwitchAction(cardLayout, cardContainer, "raceSetupScreen");
        Button[] startScreenButtons = {startButton};
        return createPanel(startScreenButtons, new FlowLayout(), Color.ORANGE);
    }
    
    // this will screate the race set up screen, returns the corresponding JPanel
    //
    private JPanel createRaceSetupScreen(CardLayout cardLayout, JPanel cardContainer, ButtonTemplate template) 
    {
        Button addHorseButton = new Button("add Horse", template);
        Button editTrackButton = new Button("edit race track", template);
        editTrackButton.addPanelSwitchAction(cardLayout, cardContainer, "editTrackScreen");
        Button startRaceButton = new Button("start race", template);
    
        Button[] raceSetupButtons = {editTrackButton, addHorseButton, startRaceButton};
        return createPanel(raceSetupButtons, new GridLayout(3, 1), Color.RED);
    }
    
    // this will create the edit track screen, returns the corresponding JPanel
    //
    private JPanel createEditTrackScreen(CardLayout cardLayout, JPanel cardContainer, ButtonTemplate template) 
    {
        JLabel labelTotalTracks = new JLabel("please enter the total number of tracks (min2, max " + race.getMaxLanes() +")");
        JTextField inputTotalTracks = new JTextField(2);

        Button plusButton = new Button(scaleIcon("Part2/images/plusIcon.png", 50, 50));
        Button minusButton = new Button(scaleIcon("Part2/images/minusIcon.png", 50, 50), false);
    
        plusButton.addAction(e -> addLane(e, minusButton));
        minusButton.addAction(e -> removeLane(e, plusButton));
    
        Button[] editTrackButtons = {plusButton, minusButton};
        JPanel editTrackScreen = createPanel(editTrackButtons, new FlowLayout(), Color.BLUE);
    
        track.setLayout(new BoxLayout(track, BoxLayout.Y_AXIS));
        JScrollPane scrollableTrack = new JScrollPane(track);
        editTrackScreen.add(labelTotalTracks);
        editTrackScreen.add(inputTotalTracks);
        editTrackScreen.add(scrollableTrack);


    
        return editTrackScreen;
    }
}
