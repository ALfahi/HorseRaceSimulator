package Part2;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import javax.swing.text.NumberFormatter;
import java.io.*;

/**
 * This is the RaceGUI class, it will provide a GUI interface for the user for the horse race simulation.
 * This is a singleton class.
 *  Below are the attributes:
 * - screen: the screen/ container which will house the entire GUI.
 * - instance: holds the single active instance of RaceGUI (used to enforce the singleton property of this class)
 * 
 * @author Fahi Sabab, Al
 * @version 1.5 21/04/2025
 * - refactored internal logic to use the Track class instead of a raw JPanel.
*/
public class RaceGUI 
{
    private JFrame screen;
    private static Race race = new Race();
    private static Track track = new Track();
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

    // this function get's passed in some Swing components that the panel will contain (if any), the layout it will be useing
    // and it's background color to initialise and set up a JPanel, it will then return the panel.
    // if background color is set to null, it will make the panel's background be transparent.
    //
    private static JPanel createPanel(Component[] components, LayoutManager layout, Color color)
    {
        JPanel panel = new JPanel();
        panel.setLayout(layout);

        if (color!= null)
        {
            panel.setBackground(color);
        }
        else// make it transparent
        {
            panel.setOpaque(false);
        }

        // add in the components.
        for (int i = 0; i < components.length; i ++)
        {
            panel.add(components[i]);
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

    // this function is used to create a text field that only accepts integers in a given range.
    //we can choose, if we want to add in some function/ logic if the value of the textField changes, if we pass in null
    // for the action then this textField will not do any action.
    //
    private JFormattedTextField createIntegerTextField(int min, int max, PropertyChangeListener action)
    {
        // creating the actual foratter
        NumberFormat format = NumberFormat.getIntegerInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(min);
        formatter.setMaximum(max);
        formatter.setAllowsInvalid(true);
        formatter.setCommitsOnValidEdit(true);

        JFormattedTextField textField = new JFormattedTextField(formatter);
        textField.setText(String.valueOf(min));
        textField.setColumns(3);// allow it to display 3 characters at a time
        if (action != null)
        {
            textField.addPropertyChangeListener("value", action);
        }
        return textField;
    }
    /*********** button action methods ********/

    // function to handle when the user wants to press a button to add lanes
    //
    private void addLane(ActionEvent e, Button minusButton, JTextField textField) 
    {
        JButton button = (JButton) e.getSource(); // this just gets the button which called the function
    
        if (!race.exceedsMaxLanes()) 
        {
            race.addLane();
            track.addLane(race.getRaceLength());
            textField.setText(String.valueOf(track.getLaneCount()));

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
    private void removeLane(ActionEvent e, Button plusButton, JTextField textField)
    {
        JButton button = (JButton) e.getSource();
        if (race.overMinimumLanes())
        {
            race.removeLane();
            // remove the last lane from the track:
            track.removeLane();
            textField.setText(String.valueOf(track.getLaneCount()));

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

    /***********text field action methods **********/

    // this function  is used to update the lanes in real time as the value in the text field changes.
    // returns a PropertyChangeListener instance,
    //
    private PropertyChangeListener updateLaneListener(Button plusButton, Button minusButton) {
        PropertyChangeListener listener = event -> {
            int newLaneCount = (int) ((JFormattedTextField) event.getSource()).getValue();
            race.initialiseLanes(newLaneCount);
            track.clear();// reset the track.
            initialiseTrack(newLaneCount);

            // enable/ disable the appropriate buttons when the user types in 20 or 2.
            plusButton.setEnabled(track.getLaneCount() < race.getMaxLanes());
            minusButton.setEnabled(track.getLaneCount() > 2);
        };
    
        return listener;
    }

     // returns a PropertyChangeListener instance,
    //
    private PropertyChangeListener updateDistanceListener() {
        PropertyChangeListener listener = event -> {
            int newDistance = (int) ((JFormattedTextField) event.getSource()).getValue();
            race.setRaceLength(newDistance);
        };
    
        return listener;
    }

    /***********JComboBox action methods **********/
    /*private void assignHorseToLane(int laneNumber, Horse horse)
    {
        track.get(laneNumber);// to do
    }
    */

    // this function converts an array of Buttons[] into an array of JButtons[]
    //
    private JButton[] convertButtonArrayToJButtons(Button[] buttons)
    {
        JButton[] jButtons = new JButton[buttons.length];
        for (int i = 0; i < buttons.length; i++)
        {
            jButtons[i] = buttons[i].getJButton();
        }

        return jButtons;
    }
   
    /*********functions to update GUI */

    private static void initialiseTrack(int totalLanes)
    {
        for (int i = 0; i < totalLanes; i++)
        {
            track.addLane(race.getRaceLength());

        }
    }


    /*****************  all the screens ***********/

    // this will screate the start screen, returns the corresponding JPanel
    //
    private JPanel createStartScreen(CardLayout cardLayout, JPanel cardContainer, ButtonTemplate template) 
    {
        Button startButton = new Button("start", template);
        startButton.addPanelSwitchAction(cardLayout, cardContainer, "raceSetupScreen");
        Button[] startScreenButtons = {startButton};
        return createPanel(convertButtonArrayToJButtons(startScreenButtons), new FlowLayout(), Color.ORANGE);
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
        return createPanel(convertButtonArrayToJButtons(raceSetupButtons), new GridLayout(3, 1), Color.RED);
    }
    
    // this will create the edit track screen, returns the corresponding JPanel
    //
    private JPanel createEditTrackScreen(CardLayout cardLayout, JPanel cardContainer, ButtonTemplate template) {
        // Main panel with BorderLayout
        JPanel editTrackScreen = createPanel(new Component[] {}, new BorderLayout(), Color.BLUE);
    
        // Track count form/ controls
        JLabel trackCountLabel = new JLabel("Number of Tracks:");
        Button plusButton = new Button(scaleIcon("Part2/images/plusIcon.png", 50, 50));
        Button minusButton = new Button(scaleIcon("Part2/images/minusIcon.png", 50, 50), false);
        JFormattedTextField trackInput = createIntegerTextField(2, race.getMaxLanes(), updateLaneListener(plusButton, minusButton));
        plusButton.addAction(e -> addLane(e, minusButton, trackInput));
        minusButton.addAction(e -> removeLane(e, plusButton, trackInput));
    
        JPanel trackCountPanel = createPanel(new Component[] {trackCountLabel, trackInput,plusButton.getJButton(), 
                minusButton.getJButton()},new FlowLayout(), null);
    
        //  track length form/ controls
        JLabel trackDistanceLabel = new JLabel("Track Length (max 100, min 30m):");
        JFormattedTextField distanceInput = createIntegerTextField(30, 100, updateDistanceListener());
    
        JPanel trackLengthPanel = createPanel(new Component[] {trackDistanceLabel, distanceInput},new FlowLayout(), null);
    
        // Combine both into one vertical section
        JPanel controlsPanel = createPanel(new Component[] {trackLengthPanel, trackCountPanel},new GridLayout(2, 1), null);
    
        // place holder for track shape options
        Button dummy1 = new Button("Dummy1", template);
        Button dummy2 = new Button("Dummy2", template);
        Button dummy3 = new Button("Dummy3", template);
    
        JPanel dummyButtons = createPanel(new Component[] {dummy1.getJButton(), dummy2.getJButton(), dummy3.getJButton()},new FlowLayout(), null);
    
        // Group all top elements
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);
        topSection.add(controlsPanel, BorderLayout.NORTH);
        topSection.add(dummyButtons, BorderLayout.CENTER);
    
        // scrollable track preview
        JScrollPane scrollableTrack = new JScrollPane(track.getTrackPanel());
        scrollableTrack.setPreferredSize(new Dimension(1200, 400));
    
        // Add to main layout
        editTrackScreen.add(topSection, BorderLayout.NORTH);
        editTrackScreen.add(scrollableTrack, BorderLayout.SOUTH);
    
        return editTrackScreen;
    }    

    /*private JPanel createAddHorseScreen(CardLayout cardLayout, JPanel cardContainer, ButtonTemplate template)
    {
        // main panel that stores everything in this screen

        JPanel addHorseScreen = createPanel(new Component[] {}, new BorderLayout(), Color.RED);
        
        JLabel horseNameLabel = new JLabel("Horse name:");
        JTextField horseNameInput = new JTextField(18);// example column, tweak this later.

        JLabel laneLabel = new JLabel("please pick one of the empty lanes");
        Integer[] emptyLanes = race.getAllEmptyLanes();// convert this into a string in a later update.

        JComboBox<Integer> availableLanes = new JComboBox<>(emptyLanes);


    } */
}
