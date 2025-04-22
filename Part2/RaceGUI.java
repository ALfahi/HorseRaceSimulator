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
import java.util.List;

/**
 * This is the RaceGUI class, it will provide a GUI interface for the user for the horse race simulation.
 * This is a singleton class.
 *  Below are the attributes:
 * - screen: the screen/ container which will house the entire GUI.
 * - instance: holds the single active instance of RaceGUI (used to enforce the singleton property of this class)
 * 
 * @author Fahi Sabab, Al
 * @version 1.7 22/04/2025
 * 
 *  - horses can now be seen visually in the screen,
 * - added the race screen (user can now see how a race occurs in real time)
 * - removed startRace() function, race now initialises lanes internally when constructed the first time.
 * 
*/
public class RaceGUI 
{
    private JFrame screen;
    private static Race race = new Race();
    // since swift doesn't allow for same component to be inside 2 different parents, just make 2 copies all looking at the same
    // data (therefore they are all synced).
    private static Track raceTrack = new Track(race.getAllLanes());
    private static Track editTrack = new Track(race.getAllLanes());
    private static RaceGUI instance = null;
    private  JComboBox<String> availableLanes = new JComboBox<>();// to do make this not class level later.;

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
        JPanel addHorseScreen = createAddHorseScreen(cardLayout, cardContainer, menuButtonTemplate);
        JPanel raceScreen = createRaceScreen(cardLayout, cardContainer, menuButtonTemplate);


        // adding in all the panels to the frame/ screen.
        //
        this.screen.add(cardContainer);
        cardContainer.add(startScreen, "startScreen");
        cardContainer.add(raceSetupScreen, "raceSetupScreen");
        cardContainer.add(editTrackScreen, "editTrackScreen");
        cardContainer.add(addHorseScreen, "addHorseScreen");
        cardContainer.add(raceScreen, "raceScreen");


        
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

        }
        return instance;
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

    // Overloaded createPanel method that uses BoxLayout
    //
    private JPanel createPanel(Component[] components, int boxLayoutAxis, Color backgroundColor) 
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, boxLayoutAxis));
        
        if (backgroundColor != null) 
        {
            panel.setBackground(backgroundColor);
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

    // this function easily creates a panel to hold the back button at the bottom left corner of the screen.
    //
    private JPanel createBackButtonPanel(ButtonTemplate template, CardLayout layout, JPanel cardContainer, String previousScreen)
    {
         // Create a back button to go back to the previous screen
         Button backButton = new Button("Back", template);
         backButton.addPanelSwitchAction(layout, cardContainer, "raceSetupScreen");
         JPanel backContainer = createPanel(new Component[]{backButton.getJButton()}, new FlowLayout(FlowLayout.LEFT), null);

         return backContainer;
    }
    /*********** button action methods ********/

    // function to handle when the user wants to press a button to add lanes
    //
    private void addLane(ActionEvent e, Button minusButton, JTextField textField) 
    {
        JButton button = (JButton) e.getSource(); // this just gets the button which called the function
    
        if (!race.exceedsMaxLanes()) 
        {
            // increment textField.
            textField.setText(String.valueOf(race.getTotalLanes() + 1));

            System.out.println("number of lanes is " + race.getTotalLanes());
            if (!minusButton.isEnabled())// if minus button was disabled before, we added in an extra lane which can be deleted
            // there enable button again.
            {
                minusButton.setEnabled(true);
            }
        } 
        else 
        {
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
            // decrement the value in the textField.
            textField.setText(String.valueOf(race.getTotalLanes() - 1));

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

    // this function updates the combobox to keep on showing the most up to date empty lanes
    //
    private void refreshComboBox()
    {
        this.availableLanes.removeAllItems();
        for (int i = 0; i < race.getTotalLanes(); i++) 
        {
            if (race.isLaneEmpty(i)) // // Lane is empty
            {  
                this.availableLanes.addItem("Lane " + (i + 1));
                System.out.println("Lane " + (i + 1));
            }
        }

    }

    // This function just makes it so that the user can't add a horse if the race tracks are full, otherwise it redirects into the
    // add horse page.
    //
    private void redirectToAddHorsePage(CardLayout cardLayout, JPanel cardContainer, String screenName)// fix error (doesn;t work)
    {
        System.out.println(race.isLaneFull());
        System.out.println(race.getTotalLanes());
        if (race.isLaneFull())
        {
            JOptionPane.showMessageDialog(null, "Sorry but all lanes are full!!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return; 
        }
        redirectScreen(cardLayout, cardContainer, screenName);
    }

     // This function just makes it so that the user can't start a race with less than 2 horses, otherwise it redirects to race.
    //
    private void redirectToRace(CardLayout cardLayout, JPanel cardContainer, String screenName)
    {
        if (!race.isAtLeastTwoHorses())
        {
            JOptionPane.showMessageDialog(null, "Sorry but you need at least two horses to start the race"
            , "Input Error", JOptionPane.ERROR_MESSAGE);
            return; 
        } 
        // refresh the racetrack before accessing the page
        race.resetDistanceAllHorses();
        raceTrack.refresh();
        redirectScreen(cardLayout, cardContainer, screenName);
        startRaceAnimation();

    }

    // This function adds a horse to the lane if the form inputs are valid, otherwise it returns an error message and does nothing
    //
    private void addFormHorse(Component[] components, CardLayout cardLayout, JPanel cardContainer, String screenName ) 
    {
        // Extract the inputs
        String horseName = ((JTextField) components[0]).getText(); // JTextField for horse name
        double horseConfidence = ((JSlider) components[1]).getValue() / 1000.0; // JSlider for confidence
        String horseSymbol = ((JTextField)components[2]).getText(); // JTextField for the horse symbol
        JComboBox<String> comboBoxInput = (JComboBox<String>) components[3]; // JComboBox for lane selection
        String selectedLane = "";
        
        // Validate the horse name
        if (horseName.trim().isEmpty()) 
        {
            JOptionPane.showMessageDialog(null, "Horse name cannot be empty!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return; 
        }
        
        // Validate the horse symbol
        if (horseSymbol.trim().isEmpty() || horseSymbol.length() != 1) 
        {
            JOptionPane.showMessageDialog(null, "Horse symbol must be a single character!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return; // Stop further execution if there's an error
        }
        
        // no need to validate confidence as the slider will ensure that it's within the valid range.
        
        // Validate the lane selection (ensure something valid is selected)
        selectedLane = (String) comboBoxInput.getSelectedItem();
        if (selectedLane == null || selectedLane.trim().isEmpty()) 
        {
            JOptionPane.showMessageDialog(null, "Please select a valid lane!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return; // Stop further execution if there's an error
        }
        
        // add horse to lane and redirect back to race set up screen.
        int lane = Integer.parseInt(selectedLane.substring(5)) -1;// translate the input into a valid lane (remove 'lane ')
        Horse horse = new Horse(horseSymbol.charAt(0), horseName, horseConfidence);
        race.addHorse(horse, lane);
        editTrack.refresh();
        // redirect to new screen:
        redirectScreen(cardLayout, cardContainer, screenName);
        
    }

    /***********text field action methods **********/

    // this function  is used to update the lanes in real time as the value in the text field changes.
    // returns a PropertyChangeListener instance,
    //
    private PropertyChangeListener updateLaneListener(Button plusButton, Button minusButton) 
    {
        PropertyChangeListener listener = event -> {
            int newLaneCount = (int) ((JFormattedTextField) event.getSource()).getValue();
           // Get the current lanes (this assumes you can get an array or list of lanes)
            List<Lane> oldLanes = race.getAllLanes(); // Get the current lanes
            
            // If the new lane count is greater, we need to add new empty lanes.
            if (newLaneCount > oldLanes.size()) 
            {
                // Add empty lanes at the end
                for (int i = oldLanes.size(); i < newLaneCount; i++) 
                {
                    race.addLane();
                    editTrack.refresh();
                }
            } 
            // If the new lane count is smaller, we need to trim the lanes array.
            else if (newLaneCount < oldLanes.size()) 
            {
                // Remove lanes that are beyond the new lane count
                for (int i = oldLanes.size() - 1; i >= newLaneCount; i--) 
                {
                    race.removeLane();
                    editTrack.refresh();
                }
            }

            // enable/ disable the appropriate buttons when the user types in 20 or 2.
            plusButton.setEnabled(race.getTotalLanes() < race.getMaxLanes());
            minusButton.setEnabled(race.getTotalLanes() > 2);
        };
    
        return listener;
    }

     // returns a PropertyChangeListener instance,
    //
    private PropertyChangeListener updateDistanceListener() 
    {
        PropertyChangeListener listener = event -> {
            int newDistance = (int) ((JFormattedTextField) event.getSource()).getValue();
            race.setRaceLength(newDistance);
        };
    
        return listener;
    }



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


    private static  void redirectScreen(CardLayout cardLayout, JPanel cardContainer, String newScreen)
    {
        cardLayout.show(cardContainer, newScreen);
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
        addHorseButton.addAction(e -> redirectToAddHorsePage(cardLayout, cardContainer, "addHorseScreen"));
        addHorseButton.addAction(e -> refreshComboBox());

        Button editTrackButton = new Button("edit race track", template);
        // always refresh and reset track when accessing the page.
        editTrackButton.addAction(e-> race.resetDistanceAllHorses());
        editTrackButton.addAction(e -> editTrack.refresh());
        editTrackButton.addPanelSwitchAction(cardLayout, cardContainer, "editTrackScreen");


        Button startRaceButton = new Button("start race", template);
        startRaceButton.addAction(e -> redirectToRace(cardLayout, cardContainer, "raceScreen"));
    
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
        JScrollPane scrollableTrack = new JScrollPane(editTrack.getTrackPanel());
        scrollableTrack.setPreferredSize(new Dimension(1200, 400));
    
        // back button panel.
        JPanel backContainer = createBackButtonPanel(template, cardLayout, cardContainer, "raceSetupScreen");
        // Add to main layout
        editTrackScreen.add(topSection, BorderLayout.NORTH);
        editTrackScreen.add(scrollableTrack, BorderLayout.CENTER);
        editTrackScreen.add(backContainer,BorderLayout.SOUTH );
    
        return editTrackScreen;
    }    

    private JPanel createAddHorseScreen(CardLayout cardLayout, JPanel cardContainer, ButtonTemplate template) 
    {
        // Main panel for this screen
        JPanel addHorseScreen = new JPanel();
        
        // name field
        JLabel horseNameLabel = new JLabel("Horse Name:");
        JFormattedTextField horseNameInput = new JFormattedTextField();
        horseNameInput.setColumns(18);
        JPanel namePanel = createPanel(new Component[]{ horseNameLabel,horseNameInput  }, new FlowLayout(FlowLayout.LEFT),
        null);
        
        // Confidence slider input
        JLabel confidenceLabel = new JLabel("Confidence (0.0 to 1.0):");
        JSlider confidenceSlider = new JSlider(0, 1000, 500); // Slider from 0 to 1000 representing 0.0 to 1.0
        confidenceSlider.setMajorTickSpacing(100);
        confidenceSlider.setMinorTickSpacing(10);
        confidenceSlider.setPaintTicks(true);
        confidenceSlider.setPaintLabels(true);

        JPanel confidencePanel = createPanel(new Component[]{confidenceLabel, confidenceSlider}, new FlowLayout(FlowLayout.LEFT),
        null);
    
        // horse symbol field
        JLabel characterLabel = new JLabel("Single Character:");
        JFormattedTextField characterInput = new JFormattedTextField();
        characterInput.setColumns(1);  // Only allow a single character
        JPanel symbolPanel = createPanel(new Component[]{characterLabel, characterInput}, new FlowLayout(FlowLayout.LEFT),
        null);
    
    
        JLabel laneLabel = new JLabel("Please pick one of the following empty lanes");
        JPanel lanePanel = createPanel(new Component[]{laneLabel,this.availableLanes}, new FlowLayout(FlowLayout.LEFT), null);
    
        // add horse button panel

        // array of all inputs
        Component[] inputs = {horseNameInput, confidenceSlider, characterInput, this.availableLanes};
        JButton addHorseButton = new Button("add horse", template).getJButton();
        addHorseButton.addActionListener(e -> addFormHorse(inputs, cardLayout, cardContainer, "raceSetupScreen"));
        JPanel addHorsePanel = createPanel(new Component[] {addHorseButton},
        new FlowLayout(FlowLayout.CENTER), null);
        
        // back button panel.
        JPanel backContainer = createBackButtonPanel(template, cardLayout, cardContainer, "raceSetupScreen");

        addHorseScreen = createPanel(new Component[]{namePanel, confidencePanel, symbolPanel, lanePanel, addHorsePanel, backContainer}
        , BoxLayout.Y_AXIS, Color.RED);
    
        return addHorseScreen;
    }

    // this is the race screen page, a race is started and after the race the user can choose ot reset/ edit or replay the 
    // race.
    //
    private JPanel createRaceScreen(CardLayout cardLayout, JPanel cardContainer, ButtonTemplate template)
    {
        JPanel backContainer = createBackButtonPanel(template, cardLayout, cardContainer, "raceSetupScreen");
        JPanel raceScreen = createPanel(new Component[]{}, new BorderLayout(), Color.DARK_GRAY);
        raceScreen.add(backContainer, BorderLayout.SOUTH);
        raceScreen.add(raceTrack.getTrackPanel(), BorderLayout.NORTH);
        return raceScreen;

    }

    // This will replace the race.start() function and allow us to see the race happen in real time.
    //
    public void startRaceAnimation() 
    {
        race.resetDistanceAllHorses();
    
        Timer raceTimer = new Timer(100, e -> 
        {
            race.moveAllHorses(); // handles logic + visuals
    
            if (race.checkWin() || race.getRemainingHorses() == 0) {
                ((Timer) e.getSource()).stop();// stop the race since either all horse eliminated or someone has won.
    
                if (race.getRemainingHorses() == 0) {
                    JOptionPane.showMessageDialog(null, "All horses have fallen. No winner.");
                }
            }
        });
    
        raceTimer.start(); // start GUI-friendly race loop
    }
}
