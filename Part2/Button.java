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
 * @version 1.2 19/04/2025
 * - Button class can now handle Icons, it now supprts icons which can act as buttons.
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
    
    public Button(ImageIcon icon, ActionListener action)
    {
        button = new JButton(icon);
        // Set the preferred size of the button based on the icon size
        button.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));

        button.addActionListener(action);
    }
    public Button(ImageIcon icon)
    {
        button = new JButton(icon);

        // Set the preferred size of the button based on the icon size, so that the icon fits perfeclty inside the button.
       button.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
    }

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
    
    // returns the JButton version to the program
    //
    public JButton getJButton()
    {
        return button;
    }

    // used to efficiecntly make buttons have the action to swap between one panel to another.
    //
    public void addPanelSwitchAction(CardLayout layout, JPanel screen, String newPanel)
    {
        button.addActionListener(e -> {
            layout.show(screen, newPanel);
        });
    }
}
