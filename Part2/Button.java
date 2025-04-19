package Part2;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/**
 * This is the Button class, it's used to create multiple buttons very efficiently.
 *  Below are the attributes:
 * - text: the text on the button
 * - template: This is the template of the button, all styling will be copied over from the template into this button.
 * - action: what event/ action will be triggered when the button is clicked.
 * 
 * @author Fahi Sabab, Al
 * @version 1.0 18/04/2025
 */
public class Button {
    private JButton button;

    public Button(String text, ButtonTemplate template, ActionListener action)
    {
        button = new JButton(text);
        applyStyles(template);
        button.addActionListener(action);

    }

    public Button(String text, ButtonTemplate template)
    {
        button = new JButton(text);
        applyStyles(template);
    }

    // This function copies over the styles from the template to the button:
    //
    private void applyStyles(ButtonTemplate template)
    {
        button.setBackground(Color.decode(template.getBackgroundColor()));
        button.setForeground(Color.decode(template.getForegroundColor()));
        button.setFont(new Font(template.getFont(), template.getFontWeight(), template.getFontSize() ));
        button.setPreferredSize(new Dimension(template.getHeight(), template.getWidth()));
    }
    
    // returns the JButton version to the program
    //
    public JButton convertToJButton()
    {
        return button;
    }
}
