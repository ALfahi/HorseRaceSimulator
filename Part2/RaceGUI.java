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
import java.util.ArrayList;
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
 * @version 1.19 27/04/2025
 * 
 * 
 * - gave table buttons a new look to differentiate form menu buttons.
 * - renamed the replay button's text to start race to make it more obvious of button's purpose.
 * TO DO: 
 *  - add another overloaded method of createPanel which takes in (component, component, component, component,, component, Color)
 *    where each attribute is NORTH, SOUTH, EAST, WEST, CENTER for Borderbox layout.
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
    private JPanel currentRaceStats = createPanel(new Component[]{},BoxLayout.Y_AXIS, Color.decode("#FFFDD0"));
    private JPanel currentBettingContainer = createPanel(new Component[]{}, new GridLayout(2,0), Color.decode("#FFFDD0"));
    private static RaceGUI instance = null;
    private  JComboBox<String> availableLanes = new JComboBox<>();// to do make this not class level later.;
    private Timer raceTimer; // Move this outside, make it a class field!

    //constructor method for this class, initialises the screen.
    //
    private RaceGUI()
    {
        
        // initialise the screen
        this.screen = new JFrame("GUI Horse Race");
        this.screen.setSize((int) getScreenWidth(),(int) getScreenHeight());// change these later.
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
        cardContainer.add(startScreen, "startScreen");
        cardContainer.add(raceSetupScreen, "raceSetupScreen");
        cardContainer.add(editTrackScreen, "editTrackScreen");
        cardContainer.add(addHorseScreen, "addHorseScreen");
        cardContainer.add(raceScreen, "raceScreen");


        // show the starting screen:
        cardLayout.show(cardContainer, "startScreen");

        // add a scrollPane to be able to scroll through the entire screen itself.
        JScrollPane scrollPane = new JScrollPane(cardContainer);
        this.screen.add(scrollPane);
        this.screen.setVisible(true);
        
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

    // This function will create a JTable with the passed in header, default values, and a string array of data.
    //
    private JTable createJTable(String[] headers, String[]uncleanedData, String[]defaultValues)
    {
        String[][] table = generateTableData(uncleanedData, defaultValues);
         
        // creating the actual table component
        JTable tableComponent = new JTable(table, headers);
        return tableComponent;
    }

    // this function is used to create a stats panel for the horses, it will only show the current stats (won't be used to
    // to show historical data)
    //
    private JPanel createHorseStatsPanel(Horse horse, int laneNumber)
    {

        HorseRecord record = horse.getHorseRecord();
        JLabel title = new JLabel("Name: " + horse.getName() + "                              " + "Lane: " + laneNumber );
        JLabel divider = new JLabel("------------------------------------------------------------");
        JLabel typeLabel = new JLabel("Breed: " + horse.getType());
        JLabel positionLabel = new JLabel("Previous race position: " + 
        record.getReadablePosition(record.getPosition().size() - 1));

        JLabel confidenceLabel = new JLabel("current Confidence: " + (int)(horse.getConfidence() * 100) + "%");
        JLabel itemLabel = new JLabel("current Item: " + horse.getItem());
        JLabel winsLabel = new JLabel("Wins: " + record.getWinNumber());
        JLabel lossesLabel = new JLabel("Losses: " + record.getLossNumber());
        JLabel fallLabel = new JLabel("Falls:" + record.getFallCount());
        JLabel winRatioLabel = new JLabel("Win ratio: " + record.getReadableWinRatio());
        JLabel prevAverageTime = new JLabel("average speed to finish previous round: " +
         record.getReadableAverageSpeed(record.getAverageSpeed().size() - 1));

        JLabel fastestTimeLabel = new JLabel("Fastest finish time: " +  record.getReadableFastestFinishTime());

        JLabel space = new JLabel(" ");// just adds a space below so that the different stats aren't too close together.


        JPanel horseStats = createPanel(new Component[]{title, divider, typeLabel, positionLabel, confidenceLabel, 
        itemLabel, winsLabel, lossesLabel,fallLabel, winRatioLabel, prevAverageTime, fastestTimeLabel, space, space}
        , BoxLayout.Y_AXIS, null);
        return horseStats;
    }

    // This function will be used to create a JPanel to house all the necessary components needed to bet
    // and display betting information on a single horse.
    //
    private JPanel createhorseBetPanel(Horse horse)
    {
        // creating header for each horse betting panel
        JLabel name = new JLabel(horse.getName());
        JLabel horseOdd = new JLabel(" Betting odds: " + horse.getOdds());

        JPanel title = createPanel(new Component[]{name, horseOdd}, new FlowLayout(FlowLayout.LEFT), null);
        // creating title for each horse betting panel.
        JLabel totalBets = new JLabel("total bets on this horse: " + horse.getTotalBets());
        JLabel totalAmount = new JLabel("total amount: " + horse.getBettingAmount());

        JLabel bettingtext = new JLabel("choose your betting amount( min £5, max £50):");
        JTextField bettingField = new JTextField(3);
        JPanel bettingInput = createPanel(new Component[]{bettingtext, bettingField}, new FlowLayout(FlowLayout.LEFT)
        , null);

        JPanel body = createPanel(new Component[]{totalBets, totalAmount, bettingInput}, new FlowLayout(FlowLayout.LEFT), null);
        JPanel bettingInformation = createPanel(new Component[]{title, body}, BoxLayout.Y_AXIS, null);

        JButton button = new JButton("Bet");
        button.addActionListener(e -> {manageUserBet(bettingField, horse);});
        // need to change method to a more specific one to keep track of user bets.
        JPanel bettingContainer = createPanel(new Component[]{bettingInformation, button}, BoxLayout.X_AXIS, null);
        return bettingContainer;


    }

    // This function will be used to create a small panel for the racetrack showing it's current condtions
    // e.g. current weather, current fastest horse + time, round number etc.
    //
    private JPanel createRaceStatsPanel()
    {
        TrackRecord record = race.getRecord();
        JPanel raceStatsPanel = new JPanel();
        JLabel weather;
        if (record != null)// if the record has been initialised, then will the panel with the record values.
        {
            JLabel round = new JLabel("Round Number " + (record.getTotalRounds()));
            // this is getting most rescent/ current weather from the records.
            if (!(record.getWeathers().size() <= 0))// since this function is called before the race begins,
            // in the beggining we just set the weather to be an empty label, and after the race sets a specifc weather condtition
            // we can finally refresh and add the weather back in.
            {
             weather = new JLabel("Current Weather: " + record.getWeathers().get(record.getWeathers().size() - 1));
            }
            else
            {
                weather = new JLabel();
            }
            JLabel fastestHorse = new JLabel("Fastest Horse: " + record.getReadableFastestHorse());
            JLabel fastestTime = new JLabel("Fastest Finish Time: " + record.getReadableFastestFinishTime());
            
            raceStatsPanel = createPanel(new Component[]{round,weather, fastestHorse, fastestTime}, 
            new GridLayout(2, 2), null);// change this to be color of current lanes later.
    
        }
        return raceStatsPanel;
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

    // This function just checks if user has betted already, if they didn't then they can bet, otherwise they cant:
    //
    private void manageUserBet(JTextField textfield, Horse horse)
    {
        if (race.hasUserBetted()) 
        {
            JOptionPane.showMessageDialog(null, "You have already placed a bet! You can't bet again.", "Bet Error", JOptionPane.ERROR_MESSAGE);
        }
        else
        {
            validateNumberTextField(Betting.MAXBETAMOUNT, Betting.MINBETAMOUNT, textfield, 
            () -> 
            {
                // this only runs after we validated that the text field has a correct double, so no errors.
                double betAmount = Double.parseDouble(textfield.getText());
                race.handleUserBet(horse, betAmount);  // This method should place the bet
                JOptionPane.showMessageDialog(null, "Bet placed successfully on " + horse.getName() + "!", "Bet Confirmed", JOptionPane.INFORMATION_MESSAGE);
            });
        }
    }   

    // This function shows the user their betting result.
    //
    private void showBetResults()
    {
        double wins = 0;
        int totalBets = 0;
        final double CUTOFF = (0.5);// a half.
        double earnings = race.getBettingRecord().getEarnings();
        int WinRecordSize = race.getBettingRecord().getWinRecord().size();
        // this message is used if user get's negative final amount.
        String performanceMessage = "You have lost quite a bit of money, your balance is at " + earnings  + 
        " consider trying to bet on more safer options to get a positive earning" ;
        String winsMessage= "";
        String finalMessage = "";
        for (int i = 0; i < WinRecordSize; i++)
        {
            if (race.getBettingRecord().getWinRecord().get(i).equals("yes"))// user won
            {
                wins++;
                totalBets++;
            }
            else if (race.getBettingRecord().getWinRecord().get(i).equals("no"))// user placed a bet and lost.
            {
                totalBets++;
            }

        }
        if (wins <  totalBets* CUTOFF)// user lost 2/3 of the bets that they made.
        {
            winsMessage = "You have only won " + (int)wins + " out of " +  totalBets +". Try different horses or different " + 
            "items to make the underdogs be useful!!!";
        }
        else
        {
          winsMessage = "Wow you won " + wins + " out of " + totalBets +", next time try out different horse options" + 
        " to spice up your betting journey even more";
        }
        if (earnings > 0)
        {
            performanceMessage = "Wow you have made £ " + earnings + " !!!. Consider betting for riskier horses to increase "
             + "your payout even more!!!";
        }

        // Show the final message in a JOptionPane only if a bet has been made:
        if (totalBets > 0)
        {
            finalMessage = winsMessage + "\n" + performanceMessage;
            JOptionPane.showMessageDialog(null, finalMessage, "Betting Results", JOptionPane.INFORMATION_MESSAGE);
        }
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
        // refresh the page before going to the page.
        raceTrack.refresh();
        refreshCurrentStats();
        redirectScreen(cardLayout, cardContainer, screenName);

    }

    // This function will make sure that the data cannot be submitted unless the textField is properly defined.
    //
    private void validateNumberTextField(double max, double min, JTextField textField, Runnable action)
    {
        String input = textField.getText();
        if (input.trim().isEmpty() || input.contains(" ") || input == null)
        {
            JOptionPane.showMessageDialog(null, "Please do not leave the field blank or add spaces.",
             "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        else
        {
            if (!isNumber(input) || !hasNDecmialPlaces(input, 2))
            {
                JOptionPane.showMessageDialog(null, "It needs to be a number with exactly 2 decimal places"
                , "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (Double.parseDouble(input) > max || Double.parseDouble(input) < min)
            {
                JOptionPane.showMessageDialog(null, "Please enter a number between " + min + " and " + 
                max + ".", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            action.run();// at this point the code is correct.
            refreshCurrentStats();
        }
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

        else if (horseName.contains(",") || horseName.contains("|"))
        {
            JOptionPane.showMessageDialog(null, "Horse name cannot contain '|' or ',' !", "Input Error", JOptionPane.ERROR_MESSAGE);
            return; 
        }
        else if (!race.isUniqueHorse(horseName))
        {
            JOptionPane.showMessageDialog(null, "Sorry but this name is already in use.", "Input Error", JOptionPane.ERROR_MESSAGE);
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

    // This function will create a pop up screen with options yes or no, if yes is clicked then passed in function will run.
    //
    public void createPopUp(String message, Runnable function, Runnable fallBackFunction)
    {
        int userResponse = JOptionPane.showOptionDialog(
            this.screen, // if we pass in this, then it will center it inside the frame (the entire window/ screen)/
            message, 
            null,
            JOptionPane.YES_NO_OPTION, // show "Yes" and "No" buttons
            JOptionPane.QUESTION_MESSAGE, 
            null, 
            null, 
            JOptionPane.NO_OPTION);// default to the no option
    
        // if the user chooses the yes button, then we can run the passed in function.
        if (userResponse == JOptionPane.YES_OPTION)
        {
            function.run();
        }
        fallBackFunction.run();
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

        // horse table
        String[] horseRecordHeaders = {"race date", "horse name", "horse breed", "horse item", "horse confidence", 
        "total wins", "total loss", "total falls", "win ratio", "fastest finish time", "average completion speed", 
        " finished position"}; 
        String [] horseRecordDefaultValues = {"", "", "", "", "n/a", "n/a", "n/a","n/a", 
        "n/a", "DNF", "DNF", "DNF"};

        // track table
        String[] trackRecordsHeader = {"date", "race length", "total lanes", "total rounds", "weather", 
        "average completion speed", "total horses", "total winners", "winning horse", "fastest finish time", 
    "fastest horse", "total falls/ incidents per round" };
        String[] trackRecordDefaultValues = {"", "", "", "", "", "DNF", "",  "", "", "n/a", "", "0"};

        // betting table
        String[] BettingRecordsHeader = {"date", "horse", "did they win?", "winnings", "net earnings" };
        String [] BettingRecordDefaultValues = {"", "n/a", "n/a", "n/a", ""};

        Button addHorseButton = new Button("add Horse", template);
        addHorseButton.addAction(e -> redirectToAddHorsePage(cardLayout, cardContainer, "addHorseScreen"));
        addHorseButton.addAction(e -> refreshComboBox());

        Button editTrackButton = new Button("edit race track", template);
        // always refresh and reset track when accessing the page.
        editTrackButton.addAction(e-> race.resetDistanceAllHorses());
        editTrackButton.addAction(e -> editTrack.refresh());
        editTrackButton.addPanelSwitchAction(cardLayout, cardContainer, "editTrackScreen");


        Button startRaceButton = new Button("start race", template);
        startRaceButton.addAction(e -> {
            race.startRecord(generateDateTime());
            redirectToRace(cardLayout, cardContainer, "raceScreen");
        });

        // buttons leading to the tables
        Button horseRecords = new Button("horse records", null);
        horseRecords.addAction(
            e -> {createTableFrame("Part2/Records/horseStats.csv", "past horse records", horseRecordHeaders,
             horseRecordDefaultValues);});

        Button trackRecords = new Button("past race records", null);
        trackRecords.addAction(
            e -> {createTableFrame("Part2/Records/TrackStats.csv", "past race records", trackRecordsHeader,
            trackRecordDefaultValues);});
        Button BettingRecords = new Button("Betting history", null);
        BettingRecords.addAction(
            e -> {createTableFrame("Part2/Records/Bettings.csv","past Bets", BettingRecordsHeader,
            BettingRecordDefaultValues);});

        JPanel tableButtonPanel = createPanel(new Component[]{horseRecords.getJButton(), trackRecords.getJButton(), 
            BettingRecords.getJButton()}, new FlowLayout(FlowLayout.CENTER), null);
            
        Button[] raceSetupButtons = {editTrackButton, addHorseButton, startRaceButton, horseRecords, trackRecords, BettingRecords};
        JPanel menuButtonContainer = createPanel(convertButtonArrayToJButtons(raceSetupButtons), new GridLayout(3, 1, 0, 20), null);

        JPanel centerWrapper = createPanel(new Component[]{menuButtonContainer, tableButtonPanel}, new FlowLayout(FlowLayout.CENTER), null);
        
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
        JLabel trackDistanceLabel = new JLabel("Track Length (max " + race.getMinDistance() + ", min " + race.getMaxDistance() + "30m):");
        JFormattedTextField distanceInput = createIntegerTextField(race.getMinDistance(), race.getMaxDistance(), e -> 
        {
            // get the valid integer from the field and pass it into the tace function.
            int newDistance = Integer.parseInt(((JFormattedTextField) e.getSource()).getText()); 
            race.setRaceLength(newDistance); 
        });// Set the new distance);
    
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
        String[] itemTypes = {"No Item", "Weather proof jacket", "Speedy Horseshoe", "Balanced Horseshoe", "Winner's Saddle"};
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
        Button backButton = new Button("Back", template);
        backButton.addAction(e -> {
            showBetResults();
            createPopUp("Do you want to save the race data?",
                () -> race.saveData(),
                () -> redirectScreen(cardLayout, cardContainer, "raceSetupScreen"));
            stopRaceTimer();
        });

        JPanel backContainer = createPanel(new Component[]{backButton.getJButton()}, new FlowLayout(FlowLayout.LEFT), null);


    // create the other buttons:
    Button replayButton = new Button("Start Race", template);
    replayButton.addAction(e ->{// refresh the racetrack before accessing the page
        race.resetDistanceAllHorses();
        raceTrack.refresh();
        refreshCurrentStats();
        redirectScreen(cardLayout, cardContainer, "raceScreen");
        startRaceAnimation();});

    // This panel will house the betting information.
    JScrollPane bettingScroller = new JScrollPane(this.currentBettingContainer);// add a scroll pane to it.

    // scrollPanes to store both the racce track and also the stats
    // this container will house both the track and also the current race stats/ conditions
    JScrollPane raceTrackJScrollPane = new JScrollPane(raceTrack.getTrackScrollPane());
    JPanel fullRacePanel = createPanel(new Component[]{this.currentRaceStats, raceTrackJScrollPane, bettingScroller}
    , BoxLayout.Y_AXIS, null);
    JScrollPane statsJScrollPane = new JScrollPane(this.currentStatsContainer);
    refreshCurrentStats();// make sure to refresh the stats container with all the new information.
    // enable scrollbars as needed


    // this panel will hold both the track and also the panel to house the information of theindivisual horses
    // e.g. name, confidence etc.
    JPanel northContainer = createPanel(
        new Component[]{fullRacePanel, statsJScrollPane},
        new GridLayout(1, 2), null);

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

    // This will create a table page/ frame with the passed in values: (it will be in a seperate JFrame)
    //
    private void createTableFrame(String fileName, String title, String[] headers, String[] defaultValues)
    {
        String[][] allFileData = CSVUtils.getDataFromCSV(fileName);
        JFrame tableFrame = new JFrame(title);
        JPanel tablePanel = createPanel(new Component[]{}, BoxLayout.Y_AXIS, null);// stores all the tables.
        tableFrame.setLayout(new BoxLayout(tableFrame.getContentPane(), BoxLayout.Y_AXIS)); // Stack tables vertically
        
        // Creating all the tables and adding them to the tablePanel
        for (int i = 0; i < allFileData.length; i++)
        {
            JTable table = createJTable(headers, allFileData[i], defaultValues);
            JScrollPane indivisualTableScroller = new JScrollPane(table); // Wrap the table in a JScrollPane
            tablePanel.add(indivisualTableScroller);
        }


        // Set the frame size and make it visible
        tableFrame.add(new JScrollPane(tablePanel));// wrap entire window with a scrollbar.
        tableFrame.setSize(800, 800);
        tableFrame.setVisible(true);
        
        // close the frame without closing the entire app.
        tableFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    // This will replace the race.start() function and allow us to see the race happen in real time.
    // this function acts as the main loop for the race, race ends when either all horses pass finish line, fall or time runs out
    //(2 minutes).
    //
    public void startRaceAnimation() 
    { 
        final long TWOMINUTES =  120000;
        cleanUpRace();
        race.incrementRound();
        //JScrollPane raceTJScrollPane = raceTrack.getTrackScrollPane();// this will always be the JScrollPane

        // before the animation  starts we can start another timer which will help us to determine how long it took to win race.
        long raceStartTimestamp = System.currentTimeMillis();
        raceTimer = new Timer(100, e -> 
        {
            race.setStartedRace(true);
            race.randomBet();
            refreshCurrentStats();
            //followLeadHorse(raceTJScrollPane, race.getLeadHorse());
            race.moveAllHorses(); // handles logic + visuals
    
            long elapsedTime = System.currentTimeMillis() - raceStartTimestamp;// this be used to check if 2 minutes since race 
                                                                              // start has passed.
            if (race.didremainingHorseFinish(raceStartTimestamp) || race.getRemainingHorses() == 0 || elapsedTime >= TWOMINUTES) 
            {
                ((Timer) e.getSource()).stop();// stop the race since either all horse eliminated or someone has won.
                if (this.raceTimer != null)// if user resets race then don't show if horses fell or won from previous race.
                {
                    if (race.getRemainingHorses() == 0 ) 
                    {
                        JOptionPane.showMessageDialog(null, "All horses have fallen. No winner.");
                        race.getRecord().getWinningHorses().add("n/a");
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(null, "Winner is: " + race.getLeadHorse().getName());
                    }
                }
                // now go back to fallen/ active horses and give thier position a -1 (DNF) e.g. they fell or took too long
                race.giveDNFs();
                // and finally finalise the race records.
                race.finaliseRaceRecord();
                race.evaluateUserBet();
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
            int leadHorsePosition =(int) (leadHorse.getDistanceTravelled() * Lane.getScale());
    
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
        this.currentBettingContainer.removeAll();
        for (int i = 0; i <race.getTotalLanes(); i++)
        {
            if (race.getLane(i).getHorse() != null)
            {
                this.currentStatsContainer.add(createHorseStatsPanel(race.getLane(i).getHorse() , i + 1));
                this.currentBettingContainer.add(createhorseBetPanel(race.getLane(i).getHorse()));
            }
        }

        // create the new updated race stats panel and swap the old one out for the new one.
        JPanel raceStats = createRaceStatsPanel();
        this.currentRaceStats.removeAll();
        this.currentRaceStats.add(raceStats);

        this.currentRaceStats.revalidate();
        this.currentRaceStats.repaint();

        this.currentStatsContainer.revalidate();
        this.currentStatsContainer.repaint();

        this.currentBettingContainer.revalidate();
        this.currentBettingContainer.repaint();
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
        return formattedDateTime;
    }
    // This function just stops the current race timer if it still exists.
    //
    private void stopRaceTimer()
    {
        if (raceTimer != null)
        {
            raceTimer.stop();
            raceTimer = null;
        }
    }

    // This function is used to reset the race timer and do some clean up (e.g. saving race data) when a race forcefull ends
    // (e.g. user presses replay button or back button)
    //
    private void  cleanUpRace()
    {
        stopRaceTimer();
        if (race.hasRaceStarted())// rounds started from before, same them.
        {
            // stop existing race and save the data.
            race.giveDNFs();
            race.finaliseRaceRecord();
        }

        // setting up for a new race to begin.
        resetRaceView();
        race.setRandomWeather();
        raceTrack.setWeather(race.getCurrentWeather());
        refreshCurrentStats();
    }

    // This function will get a string of data and also a string array of default values
    // it will then generate a 2-d array, making sure to fill in any empty cells withthe default values
    // it will also take in e.g. -1.0, -1 "" and turn them into a more readable format e.g. 'DNF', specifics
    // are stroed in defaultValues.
    //
    public String[][] generateTableData(String[] data, String[] defaultValues) 
    {
        ArrayList<ArrayList<String>> splitData = new ArrayList<>();
        int maxRows = 0;// some data may be missing a single piece of data at the end due to how race is structured
        // so we need to keep track what is the max number of rows needed.

        // iterate trhough the columns (each element in the String[] data is a column)
        //
        for (int i = 0; i < data.length; i++) 
        {
            String cell = data[i].trim();

            // remove any quotation marks as they were there only for readability in the file,
            // won't be needed for the table.
            if (cell.startsWith("\"") && cell.endsWith("\"") && cell.length() >= 2) 
            {
                cell = cell.substring(1, cell.length() - 1);
            }

            String[] parts = cell.split("\\|");// columns may store multiple vlaues in the file, so we need to split
                                                    // them up now. (they were seperated by '|', when we wrote data to file.)
            ArrayList<String> columnValues = new ArrayList<>();// stores every single piece of data for a specific column.

            for (int j = 0; j < parts.length; j++) 
            {
                String part = parts[j];
                part = part.trim();
                // some default values that we need to clean before putting it into the table.
                if (part.equals("-1") || part.equals("-1.0") || part.isEmpty()) 
                {
                    columnValues.add(defaultValues[i]);
                } 
                else 
                {
                    columnValues.add(part);
                }
            }

            splitData.add(columnValues);
            maxRows = Math.max(maxRows, columnValues.size());// if we found a column/ array with a bigger size, add a new row.
        }

        // Fill missing spots with default values
        for (int i = 0; i < splitData.size(); i++) 
        {
            ArrayList<String> column = splitData.get(i);
            while (column.size() < maxRows) 
            {
                column.add(defaultValues[i]);
            }
        }

        String[][] tableData = new String[maxRows][defaultValues.length];

        // populate the 2-d array.
        for (int row = 0; row < maxRows; row++) 
        {
            for (int col = 0; col < defaultValues.length; col++) 
            {
                if (splitData.get(col).size() > row) // we check if we have column values left to fill out the row
                {
                    tableData[row][col] = splitData.get(col).get(row);
                } 
                else // otherwise fill it with the default values.
                {
                    tableData[row][col] = defaultValues[col];
                }
            }
        }
        return tableData;
    }

    // just checks if passed in string is a number (double or int),
    //
    private static boolean isNumber(String string) 
    {
        try 
        {
            Double.parseDouble(string); // Try parsing it as a double (handles both integers and doubles)
            return true;
        } 
        catch (NumberFormatException e) 
        {
            return false; // Not a valid number
        }
    }

    // just checks if passed in number has n decimal places
    // pre condtion: passed in string is a number
    //
    private static boolean hasNDecmialPlaces(String s, int n)
    {
        int start;
        if (!s.contains("."))
        {
            return false;
        }
        else
        {
            start = s.indexOf(".");
            return (s.length() - start) - 1 == n;// see if remaining numbers match up to what they wanted.
        }
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
