package Part2;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/**
 * This is the Button class, it's used to create multiple buttons very efficiently.
 *  Below are the arguements:
 * - JButton: the actual JButton that the wrapper class is built upon;
 * 
 * @author Fahi Sabab, Al
 * @version 1.3 19/04/2025
 * - Added in extra consturctors to specify if button should be enabled/ disabled when first created.
 */
public class Button 
{
    private JButton button;
    private boolean enabled = true;

    /********constructors for this class */
    // text: the text displayed on the button
    // template: the template which this button will get it's styling from
    // action: what action the button will perform when clicked
    // enabled: whether the button starts out as enabled or disabled.
    //icon: the button can also be represented as an image/ icon.


    public Button(String text, ButtonTemplate template, ActionListener action) {
        button = new JButton(text);
        applyStyles(template);
        button.addActionListener(action);
    }

    public Button(String text, ButtonTemplate template, ActionListener action, boolean enabled) {
        button = new JButton(text);
        applyStyles(template);
        button.addActionListener(action);
        setEnabled(enabled);
    }

    public Button(String text, ButtonTemplate template) {
        button = new JButton(text);
        applyStyles(template);
    }

    public Button(String text, ButtonTemplate template, boolean enabled) {
        button = new JButton(text);
        applyStyles(template);
        setEnabled(enabled);
    }

    public Button(ImageIcon icon, ActionListener action) {
        button = new JButton(icon);
        // Set the preferred size of the button based on the icon size
        button.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
        button.addActionListener(action);
    }

    public Button(ImageIcon icon, ActionListener action, boolean enabled) {
        button = new JButton(icon);
        button.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
        button.addActionListener(action);
        setEnabled(enabled);
    }

    public Button(ImageIcon icon) {
        button = new JButton(icon);
        // Set the preferred size of the button based on the icon size, so that the icon fits perfeclty inside the button.
        button.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
    }

    public Button(ImageIcon icon, boolean enabled) {
        button = new JButton(icon);
        button.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
        setEnabled(enabled);
    }

    /********************other functions for this class *************/

    // This function copies over the styles from the template to the button:
    //
    private void applyStyles(ButtonTemplate template)
    {
        button.setBackground(Color.decode(template.getBackgroundColor()));
        button.setForeground(Color.decode(template.getForegroundColor()));
        button.setFont(new Font(template.getFont(), template.getFontWeight(), template.getFontSize() ));
        button.setPreferredSize(new Dimension(template.getWidth(), template.getHeight()));

        // code to show the button's border properly:
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        button.setFocusPainted(false);
    }

     // used to efficiecntly make buttons have the action to swap between one panel to another.
    //
    public void addPanelSwitchAction(CardLayout layout, JPanel screen, String newPanel)
    {
        button.addActionListener(e -> {
            layout.show(screen, newPanel);
        });
    }

    public void addAction(ActionListener action)
    {
        button.addActionListener(action);
    }
    /**********creating setters */

    //this function just enabled and disables the button
    //
    public void setEnabled(boolean enabled)
    {
        button.setEnabled(enabled);
    }
    /*****************getters  ***********/


    // returns the JButton version to the program
    //
    public JButton getJButton()
    {
        return button;
    }

    // returns whether the current button is enabled or disabled, returns boolean
    //
    public boolean isEnabled()
    {
        return button.isEnabled();
    }
}
