package Part2;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.HashMap;
import javax.swing.text.NumberFormatter;
import java.io.*;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * This is the RaceGUI class, it will provide a GUI interface for the user for the horse race simulation.
 * This is a singleton class.
 *  Below are the attributes:
 * - screen: the screen/ container which will house the entire GUI.
 * - instance: holds the single active instance of RaceGUI (used to enforce the singleton property of this class)
 * 
 * @author Fahi Sabab, Al
 * @version 1.13 25/04/2025
 * 
 * - refactored code and main game loop to the new race system (race continues after initial horse wins, until all horses fall
 *  or pass finish line.).
 * TO DO: 
 *  - add another overloaded method of createPanel which takes in (component, component, component, component,, component, Color)
 *    where each attribute is NORTH, SOUTH, EAST, WEST, CENTER for Borderbox layout.
 *  - fix back button positioning in some pages
 *  - link up the race edit form to dynamically change the distance and then go through all lanes to change the distance.
 *  - mabye limit number of lanes into (screen height / lane height) / 2.
 *  - fix auto scroll to lead horse feature.
 * 
*/
public class RaceGUI 
{
    private JFrame screen;
    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final double SCREENHEIGHT = screenSize.getHeight();
    private final double SCREENWIDTH = screenSize.getWidth();
    private static Race race = new Race();
    // since swift doesn't allow for same component to be inside 2 different parents, just make 2 copies all looking at the same
    // data (therefore they are all synced).
    private static Track raceTrack = new Track(race.getAllLanes());
    private static Track editTrack = new Track(race.getAllLanes());
    private JPanel currentStatsContainer =  createPanel(new Component[]{}, BoxLayout.Y_AXIS, Color.decode("#FFFDD0"));
    private static RaceGUI instance = null;
    private  JComboBox<String> availableLanes = new JComboBox<>();// to do make this not class level later.;

    //constructor method for this class, initialises the screen.
    //
    private RaceGUI()
    {
        
        // initialise the screen
        this.screen = new JFrame("GUI Horse Race");
        this.screen.setSize((int) getScreenWidth(),(int) getScreenHeight());// change these later.
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
    private JPanel createPanel(Component[] components, int boxLayoutAxis, Color color) 
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, boxLayoutAxis));
        
        if (color != null) 
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

    // this function is used to create a stats panel for the horses, it will only show the current stats (won't be used to
    // to show historical data)
    //
    private JPanel createHorseStatsPanel(Horse horse, int laneNumber)
    {
        JLabel title = new JLabel("Name: " + horse.getName() + "                              " + "Lane: " + laneNumber );
        JLabel divider = new JLabel("------------------------------------------------------------");
        JLabel typeLabel = new JLabel("Breed: " + horse.getType());
        JLabel positionLabel = new JLabel("Previous race position: " + horse.getHorseRecord().getPreviousPosition());
        JLabel confidenceLabel = new JLabel("current Confidence: " + (int)(horse.getConfidence() * 100) + "%");
        JLabel itemLabel = new JLabel("current Item: " + horse.getItem());
        JLabel winsLabel = new JLabel("Wins: " + horse.getHorseRecord().getWinNumber());
        JLabel lossesLabel = new JLabel("Losses: " + horse.getHorseRecord().getLossNumber());
        JLabel fallLabel = new JLabel("Falls:" + horse.getHorseRecord().getFallCount());
        JLabel winRatioLabel = new JLabel("Win ratio: ");
        JLabel prevAverageTime = new JLabel("previous time to finish race: ");
        JLabel fastestTimeLabel = new JLabel("Fastest finish time: ");

        JLabel space = new JLabel(" ");// just adds a space below so that the different stats aren't too close together.


        JPanel horseStats = createPanel(new Component[]{title, divider, typeLabel, positionLabel, confidenceLabel, 
        itemLabel, winsLabel, lossesLabel, space, space}, BoxLayout.Y_AXIS, null);
        return horseStats;
    }

    // this allows us to make icons into buttons
    //
     public static ImageIcon scaleIcon(String filePath, int width, int height) 
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
         backButton.addPanelSwitchAction(layout, cardContainer, previousScreen);
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
            }
        }

    }
    // this function is used to initialise a bunch of Jbuttons in an array to be ready to act as a radio button.
    //
    private void setupHorseImageButtons(JButton[] buttons, final JButton[] selected) 
    {
    
        Border selectedBorder = BorderFactory.createLineBorder(Color.BLUE, 4);
        Border defaultBorder = BorderFactory.createEmptyBorder();
    
        for (int i = 0; i < buttons.length; i++) 
        {
            JButton button = buttons[i];
            final JButton currentButton = button;// we need this to avoid some lambda referencing issues
            buttons[i].setBorder(defaultBorder);
            buttons[i].addActionListener(e -> handleHorseSelection(buttons, selected, currentButton, selectedBorder, defaultBorder));
        }
    }

    // this functions makes an array of JButtons act as radio buttons (i.e only one can be selected)
    //
    private void handleHorseSelection(JButton[] allButtons, JButton[] oldSelectedButton, JButton newSelectedButton,
     Border selectedBorder, Border defaultBorder) 
     {
        
        if (oldSelectedButton[0] != null) // if a button has been previosuly clicked, reset it.
        {
            oldSelectedButton[0].setBorder(defaultBorder);// reset the previous horse button visually.
        }

        oldSelectedButton[0] = newSelectedButton;// assign it the new selectedButton to be selected logically.
        newSelectedButton.setBorder(selectedBorder);// visually display it.
    }

    // this function enbles the passed in textfield the checkbox is checked, otherwise it diables it
    //This is an actionListener for the checkbox in add horse screen.
    //
    private void toggleSymbolPicker(JCheckBox checkBox, JButton[] buttons, JTextField textfield)
    {
        boolean isChecked = checkBox.isSelected();
        
        // Enable or disable the symbol input field based on checkbox state
        textfield.setEnabled(isChecked);

        // Enable or disable the horse image buttons based on checkbox state
        for (int i = 0; i < buttons.length; i++)
        {
            buttons[i].setEnabled(!isChecked);
        }

        // reset the horse symbol input field when switching back to the images.
        if (!isChecked) {
            textfield.setText("");
        }
    }

    // this function will change the text on the label for each of the options that's also passed in.
    // pre-condition: length of options and answers are the same.
    //
    private void changeLabelInformation(String[] options, String[]answers, JLabel label, String answer)
    {
        HashMap<String, String> convertOptionsToAnswers = new HashMap<String, String>();
        if (options.length != answers.length)
        {
            System.out.println("sorry but size of options and answers must be the same.");
            return;
        }
        for (int i =0; i < options.length; i++)
        {
            convertOptionsToAnswers.put(options[i], answers[i]);

        }
        label.setText(convertOptionsToAnswers.get(answer));
    }

    // This function just makes it so that the user can't add a horse if the race tracks are full, otherwise it redirects into the
    // add horse page.
    //
    private void redirectToAddHorsePage(CardLayout cardLayout, JPanel cardContainer, String screenName)// fix error (doesn;t work)
    {
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
        refreshCurrentStats();
        redirectScreen(cardLayout, cardContainer, screenName);
        startRaceAnimation();

    }

    // This function adds a horse to the lane if the form inputs are valid, otherwise it returns an error message and does nothing
    //
    private void addFormHorse(Component[] components, JButton[] selected, CardLayout cardLayout, JPanel cardContainer, String screenName ) 
    {
        // Extract the inputs
        String horseName = ((JTextField) components[0]).getText(); // JTextField for horse name
        double horseConfidence = ((JSlider) components[1]).getValue() / 1000.0; // JSlider for confidence
        String horseSymbol = ((JTextField)components[2]).getText(); // JTextField for the horse symbol
        JComboBox<String> comboBoxInput = (JComboBox<String>) components[3]; // JComboBox for lane selection
        String selectedLane = "";
        String type = (String) ((JComboBox<String>) components[4]).getSelectedItem(); // this stores the horse type that the user picked.
        boolean isSymbolEnabled = ((JCheckBox)components[5]).isSelected();// this stores the user's checkbox answer.
        String item = (String)((JComboBox<String>) components[6]).getSelectedItem();
        
        // we pass in the selected array, rather than keeping it inside components just so we always get the most up to date image
        // that the user picked.
        String horseImagePath = (String) selected[0].getClientProperty("imagePath");
        // Validate the horse name
        if (horseName.trim().isEmpty()) 
        {
            JOptionPane.showMessageDialog(null, "Horse name cannot be empty!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return; 
        }
        
        // check if user decided to pick symbols instead of images:
        if (isSymbolEnabled)
        {
             // Validate the horse symbol
            if (horseSymbol.trim().isEmpty() || horseSymbol.length() != 1) 
            {
                JOptionPane.showMessageDialog(null, "Horse symbol must be a single character!", "Input Error", JOptionPane.ERROR_MESSAGE);
                return; // Stop further execution if there's an error
            }
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
        // if user picked symbol, we do the constructor which takes in that symbol, otherwise we pass in the image path of the 
        // selected image.
        Horse horse;
        if (isSymbolEnabled)
        {
            horse = new Horse(horseSymbol.charAt(0), horseName, horseConfidence, type, item);
        }
        else
        {
            horse = new Horse(horseImagePath, horseName, horseConfidence, type, item);
        }
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

    /*********actions relating to JComboBoxes **************/

    // this funcion will take in the breed that the user selected, and limit the confidecne slider based upon it
    //
    private void changeHorseTypeSlider (String type, JSlider slider, JLabel label)
    {
        String[] options = { "Arabian", "Thoroughbred", "Quarter Horse", "Wild" };
        String[] answers = { "Arabian: speed multiplier of 1", "Thoroughbred: speed multiplier of 3", 
        "Quarter Horse: speed multiplier of 2", "Wild: speed multiplier between 0 and 4 (random each step it takes)"};
        // creating some default max and min values for slider as a fallback
        int min = 300;
        int max = 900;
        if (type.equals("Arabian"))
        {
            min = 200;
            max = 700;
        }
        else if (type.equals("Thoroughbred"))
        {
            min = 700;
            max = 1000;
        }
        else if (type.equals("Quarter Horse"))
        {
            min = 300;
            max = 600;
        }
        else// it's a wild horse
        {
            min = 300;
            max = 900;
        }
        // change the label:
        changeLabelInformation(options, answers, label, type);

        slider.setMaximum(max);
        slider.setMinimum(min);
        slider.setValue((max + min) / 2);
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
        // start button will reset the entire race (i.e remove all horses from lanes and also go to the main menu/ reace set up page)
        startButton.addPanelSwitchAction(cardLayout, cardContainer, "raceSetupScreen");
        startButton.addAction(e -> race.resetRace());
        return createPanel(new Component[]{startButton.getJButton()}, new FlowLayout(), Color.ORANGE);
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
        JPanel menuButtonContainer = createPanel(convertButtonArrayToJButtons(raceSetupButtons), new GridLayout(3, 1, 0, 20), null);

        JPanel centerWrapper = createPanel(new Component[]{menuButtonContainer}, new FlowLayout(FlowLayout.CENTER), null);
        
        // this is the main panel for this screen (will contain every other component)
        JPanel backButtonContainer = createBackButtonPanel(template, cardLayout, cardContainer, "startScreen");
        JPanel raceSetUpScreen = new JPanel(new BorderLayout());
        raceSetUpScreen.setBackground(Color.YELLOW);
        raceSetUpScreen.add(centerWrapper, BorderLayout.CENTER);
        raceSetUpScreen.add(backButtonContainer, BorderLayout.SOUTH);

        return raceSetUpScreen;
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
        JScrollPane scrollableTrack = editTrack.getTrackScrollPane();
        scrollableTrack.setPreferredSize(new Dimension((int)SCREENWIDTH, (int) SCREENHEIGHT/ 2));
    
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
        
        // intitialise the horse confidence slider as we will need it here
        JSlider confidenceSlider = new JSlider(200, 700, 450); // default confidence slider into the arabain horse type.
        // horse type field:
        JLabel horseTypeLabel = new JLabel("Horse Type:");
        String[] horseTypes = {"Arabian", "Thoroughbred", "Quarter Horse", "Wild"};
        JComboBox<String> typeSelector = new JComboBox<>(horseTypes);
        // a label to tell user what speed multiplier of each horse is.
        JLabel typeInfoLabel = new JLabel("Arabian horse: speed multiplier of 1");
        typeSelector.addActionListener(e -> changeHorseTypeSlider((String)typeSelector.getSelectedItem(), confidenceSlider,
        typeInfoLabel));
        JPanel typePanel = createPanel(new Component[]{horseTypeLabel, typeSelector}, new FlowLayout(FlowLayout.LEFT),
        null);

        // this panel includes the tye selector, but also a label which says what the multiplier is for that type.
        JPanel completeTypePanel = createPanel(new Component[]{typePanel, typeInfoLabel}, new GridLayout(2, 1), null);

        // Confidence slider input
        JLabel confidenceLabel = new JLabel("Confidence (0.0 to 1.0):");

        // first creating a reference slider to sit behind the real slider: (cannnot be edited by user)
        JSlider fullRangeSlider = new JSlider(0, 1000);
        fullRangeSlider.setEnabled(false);// user can't edit it.
        //fullRangeSlider.setOpaque(false);
        fullRangeSlider.setFocusable(false);
        fullRangeSlider.setPaintTicks(false);
        fullRangeSlider.setPaintLabels(true);
        fullRangeSlider.setMajorTickSpacing(1000);// only showing the end labels.
        fullRangeSlider.setUI(new BasicSliderUI(fullRangeSlider) 
        {
            // overwritting this methd to hide the thumb/ circle 
            public void paintThumb(Graphics g) {} 
        });


        // actual slider.
        confidenceSlider.setMajorTickSpacing(100);
        confidenceSlider.setPaintTicks(true);
        confidenceSlider.setPaintLabels(true);
        // Make the slider transparent, so that it blends in with the background one.
        confidenceSlider.setBackground(new Color(0, 0, 0, 0)); // Transparent background
        confidenceSlider.setOpaque(false); 

        // Remove the border
        confidenceSlider.setBorder(BorderFactory.createEmptyBorder());

        //layering the sliders
        JLayeredPane layeredSliders = new JLayeredPane();
        int width = 400, 
        height = 60;
        fullRangeSlider.setBounds(0, 0, width, height);
        confidenceSlider.setBounds(40, 0, width - 80, height);// offset the editiable slider more inside the bacgkround one,
                                                                // also make it have a smaller width so the background slider can be
                                                                // visible.

        layeredSliders.setPreferredSize(new Dimension(width, height));
        layeredSliders.add(fullRangeSlider, JLayeredPane.DEFAULT_LAYER);
        layeredSliders.add(confidenceSlider, JLayeredPane.PALETTE_LAYER);

        JPanel confidencePanel = createPanel(new Component[]{confidenceLabel, layeredSliders}, new FlowLayout(FlowLayout.LEFT),
        null);
    
        // horse image field.
        // add some client property to these so we can access the image paths for when we finally add a horse.
        JButton brownHorseButton = new JButton(scaleIcon("Part2/images/brownHorse.png", 200, 150));
        brownHorseButton.putClientProperty("imagePath", "Part2/images/brownHorse.png");
        JButton blackHorseButton = new JButton(scaleIcon("Part2/images/blackHorse.png", 200, 150));
        blackHorseButton.putClientProperty("imagePath", "Part2/images/blackHorse.png");
        JButton whiteHorseButton = new JButton(scaleIcon("Part2/images/whiteHorse.png", 200, 150));
        whiteHorseButton.putClientProperty("imagePath", "Part2/images/whiteHorse.png");
        JButton blueHorseButton = new JButton(scaleIcon("Part2/images/blueHorse.png", 200, 150));
        blueHorseButton.putClientProperty("imagePath", "Part2/images/blueHorse.png");

        JButton[] horseImageButtons = {brownHorseButton, blackHorseButton, whiteHorseButton, blueHorseButton};

        //  give them all the radio button action:
        JButton[] selected = {null};
        setupHorseImageButtons(horseImageButtons, selected);
        // make the brownHorse button be clicked by default, by simulating a click.
        handleHorseSelection(horseImageButtons, selected, brownHorseButton,
        BorderFactory.createLineBorder(Color.BLUE, 4),
        BorderFactory.createEmptyBorder());

        JLabel horseImageLabel = new JLabel("please pick what horse you want to play as");
        JPanel horseImageContainer = createPanel(horseImageButtons,  new FlowLayout(FlowLayout.LEFT), null);
        horseImageContainer = createPanel(new Component[]{horseImageLabel, horseImageContainer}, new GridLayout(2, 1), null);

        // horse symbol field
        JLabel characterLabel = new JLabel("Single Character:");
        JFormattedTextField characterInput = new JFormattedTextField();
        characterInput.setEnabled(false);// initially disable it.
        characterInput.setColumns(1);  // Only allow a single character
        JPanel symbolPanel = createPanel(new Component[]{characterLabel, characterInput}, new FlowLayout(FlowLayout.LEFT),
        null);

        // creating a checkBox to toggle on and off horse image selector and symbol picker.
        JCheckBox imagCheckBox = new JCheckBox("I want to play as my own symbol instead");
        imagCheckBox.addActionListener(e -> toggleSymbolPicker(imagCheckBox, horseImageButtons, characterInput));
        JPanel imagCheckBoxContainer = createPanel(new Component[]{imagCheckBox}, new FlowLayout(FlowLayout.LEFT), null);
    
        // creating the jBox to select an item for the horse.
        JLabel itemLabel = new JLabel("Choose an item (optional):");
        String[] itemTypes = {"No Item", "Weather proof jacket", "Speedy Horseshoe", "Balanced Horseshoe", "winner's saddle"};
        JComboBox<String> itemSelector = new JComboBox<>(itemTypes);
        // a label to tell user what each item does.
        JLabel itemInfoLabel = new JLabel("This horse will have no items.");

        String[] Itemdescriptions = {"This horse will not have an item", "Horse's stats will be unaffected by weather", 
        "Increases horse's speed stat by 1, but more likley to fall", "decreases horses speed stat by 1, but less likley to fall",
        "Horse's confidence will not decrease or increase when winning or losing."};
        itemSelector.addActionListener(e -> changeLabelInformation(itemTypes, Itemdescriptions, itemInfoLabel, 
        (String) itemSelector.getSelectedItem()));
        JPanel itemPanel = createPanel(new Component[]{itemLabel, itemSelector}, new FlowLayout(FlowLayout.LEFT),null);
        itemPanel = createPanel(new Component[]{itemPanel, itemInfoLabel}, new GridLayout(2, 1), null);
    
        JLabel laneLabel = new JLabel("Please pick one of the following empty lanes");
        JPanel lanePanel = createPanel(new Component[]{laneLabel,this.availableLanes}, new FlowLayout(FlowLayout.LEFT), null);
    
        // add horse button panel

        // array of all inputs that need validating.
        Component[] inputs = {horseNameInput, confidenceSlider, characterInput, this.availableLanes, typeSelector, imagCheckBox,
        itemSelector};
        // solution: we need to dynamically built the inputs array to get that fresh selected[0] value.
        JButton addHorseButton = new Button("add horse", template).getJButton();
        addHorseButton.addActionListener(e -> addFormHorse(inputs, selected, cardLayout, cardContainer, "raceSetupScreen"));
        JPanel addHorsePanel = createPanel(new Component[] {addHorseButton},
        new FlowLayout(FlowLayout.CENTER), null);
        
        // back button panel.
        JPanel backContainer = createBackButtonPanel(template, cardLayout, cardContainer, "raceSetupScreen");

        addHorseScreen = createPanel(new Component[]{namePanel, completeTypePanel, confidencePanel,
            horseImageContainer, symbolPanel, imagCheckBoxContainer,itemPanel ,lanePanel, addHorsePanel, backContainer}, BoxLayout.Y_AXIS, Color.RED);
    
        return addHorseScreen;
    }

    // this is the race screen page, a race is started and after the race the user can choose ot reset/ edit or replay the 
    // race.
    //
    private JPanel createRaceScreen(CardLayout cardLayout, JPanel cardContainer, ButtonTemplate template)
    {
        JPanel backContainer = createBackButtonPanel(template, cardLayout, cardContainer, "raceSetupScreen");

    // create the other buttons:
    Button replayButton = new Button("replay reace", template);
    replayButton.addAction(e -> redirectToRace(cardLayout, cardContainer, "raceScreen"));

    // scrollPanes to store both the racce track and also the stats
    JScrollPane raceTrackJScrollPane = new JScrollPane(raceTrack.getTrackScrollPane());
    JScrollPane statsJScrollPane = new JScrollPane(this.currentStatsContainer);
    refreshCurrentStats();// make sure to refresh the stats container with all the new information.
    // enable scrollbars as needed
    raceTrackJScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    raceTrackJScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    statsJScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    statsJScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    // this panel will hold both the track and also the panel to house the information of theindivisual horses
    // e.g. name, confidence etc.
    JPanel northContainer = createPanel(
        new Component[]{raceTrackJScrollPane, statsJScrollPane},
        new GridLayout(1, 2), null
    );

    // make sure northContainer only takes up 50% of the screen height.

   northContainer.setPreferredSize(new Dimension((int)getScreenWidth(), (int) (getScreenHeight()) / 2));

    // main panel for this screen.
    JPanel raceScreen = new JPanel();
    raceScreen.setLayout(new BoxLayout(raceScreen, BoxLayout.Y_AXIS));
    raceScreen.setBackground(Color.DARK_GRAY);

    // button container for bottom controls
    JPanel buttonContainer = new JPanel(new FlowLayout());
    buttonContainer.add(replayButton.getJButton());
    buttonContainer.add(backContainer);

    // add components to screen
    raceScreen.add(northContainer);
    raceScreen.add(buttonContainer);

    return raceScreen;

    }

    // This will replace the race.start() function and allow us to see the race happen in real time.
    // this function acts as the main loop for the race, race ends when either all horses pass finish line, fall or time runs out
    //(2 minutes).
    //
    public void startRaceAnimation() 
    {
        final long TWOMINUTES = 120000;// move to outer loop
        long raceStartTimestamp;
        Timer raceTimer;
        resetRaceView();
        //System.out.println("before");

       /*  for (int i = 0; i < race.getTotalLanes(); i++)
        {
            if (race.getLane(i).getHorse() != null)
            {
                Horse horse = race.getLane(i).getHorse();
                System.out.println("speed: " + horse.getSpeed() + " base speed: " + horse.getBaseSpeed() + " base confidence " + 
                horse.getBaseConfidence() + "confidence: " + horse.getConfidence());
            }
        }
        */
        race.setRandomWeather();
        raceTrack.setWeather(race.getCurrentWeather());
        race.resetDistanceAllHorses();
       // System.out.println("after");
        // change track background.
        refreshCurrentStats();
        //JScrollPane raceTJScrollPane = raceTrack.getTrackScrollPane();// this will always be the JScrollPane

        // before the animation  starts we can start another timer which will help us to determine how long it took to win race.
        raceStartTimestamp = System.currentTimeMillis();
        raceTimer = new Timer(100, e -> 
        {
            //followLeadHorse(raceTJScrollPane, race.getLeadHorse());
            race.moveAllHorses(); // handles logic + visuals
    
            long elapsedTime = System.currentTimeMillis() - raceStartTimestamp;// this be used to check if 2 minutes since race 
                                                                              // start has passed.
            if (race.didremainingHorseFinish(raceStartTimestamp) || race.getRemainingHorses() == 0 || elapsedTime >= TWOMINUTES) 
            {
                ((Timer) e.getSource()).stop();// stop the race since either all horse eliminated or someone has won.
    
                if (race.getRemainingHorses() == 0) 
                {
                    JOptionPane.showMessageDialog(null, "All horses have fallen. No winner.");
                }
                // now go back to fallen/ active horses and give thier position a -1 (DNF) e.g. they fell or took too long
                race.giveDNFs();
            }
        });

    
        raceTimer.start(); // start GUI-friendly race loop ( it doesn't block the thread.)
    }

    // this function will update the race's horizontal scrollpane to follow the lead horse (if it goes off screen)
    //
    private void followLeadHorse(JScrollPane raceTrackJScrollPane, Horse leadHorse)
    {
        // Get the scroll bar and update its position
        JScrollBar horizontalScrollBar = raceTrackJScrollPane.getHorizontalScrollBar();
        if (horizontalScrollBar != null && leadHorse != null) 
        {
            // Calculate the scroll position based on the lead horse's distance
            int maxScroll = raceTrackJScrollPane.getHorizontalScrollBar().getMaximum();
            int leadHorsePosition =(int) leadHorse.getDistanceTravelled() * Lane.getScale();
    
            // Scroll only when the lead horse moves out of view (i.e., it is near the edge of the visible area)
            if (leadHorsePosition > horizontalScrollBar.getValue() + raceTrackJScrollPane.getViewport().getWidth()) 
            {
                horizontalScrollBar.setValue(Math.min(leadHorsePosition, maxScroll));
            }
        }
    }

    // this function will reset the race's horizontal scroll pane back to it's starting position.
    //
    private void resetRaceView()
    {
        JScrollPane raceTrackJScrollPane = raceTrack.getTrackScrollPane();// we grab the scroll pane from the race track.
        if (raceTrackJScrollPane != null) 
        {
            JScrollBar horizontalScrollBar = raceTrackJScrollPane.getHorizontalScrollBar();
            if (horizontalScrollBar != null) 
            {
                horizontalScrollBar.setValue(0); // Reset the scroll position
            }
        }
    }

    // this function will refresh the current stats panel's data.
    //
    private void refreshCurrentStats()
    {
        this.currentStatsContainer.removeAll();
        for (int i = 0; i <race.getTotalLanes(); i++)
        {
            if (race.getLane(i).getHorse() != null)
            {
                this.currentStatsContainer.add(createHorseStatsPanel(race.getLane(i).getHorse() , i + 1));
            }
        }
        
        this.currentStatsContainer.revalidate();
        this.currentStatsContainer.repaint();
    }


    /****** other functions *********/

    // this function will generate the current daate-time when this function is called.
    //
    private String generateDateTime()
    {
        LocalDateTime currenTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");// format the value to a more
                                                                                                // readable format.
        String formattedDateTime = currenTime.format(formatter);
        System.out.println("Formatted: " + formattedDateTime);
        return formattedDateTime;
    }
    /*********** getters */

    // this function just gets the height of the user's screen.
    public double getScreenHeight()
    {
        return this.SCREENHEIGHT;
    }

    public double getScreenWidth()
    {
        return this.SCREENWIDTH;
    }
}
