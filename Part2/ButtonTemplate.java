package Part2;

/**
 * This class serves as a template for button styling. It stores various styling properties 
 * such as dimensions, colors, and font attributes, which can be applied to create consistent 
 * and reusable button designs throughout the application. 
 * The template is supposed to be passed into other buttons for styling, and should not
 * be used by itself.
 * 
 * Below are the attributes:
 * - height: The height of the button.
 * - width: The width of the button.
 * - backgroundColor: The background color of the button in hexadecimal or color name format.
 * - foregroundColor: The text (foreground) color of the button in hexadecimal or color name format.
 * - font: The font family used for the button's text.
 * - fontSize: The size of the text on the button.
 * - fontWeight: The weight/style of the text (e.g., Font.PLAIN, Font.BOLD, etc.).
 * 
 * @author Fahi Sabab, Al
 * @version 1.0 18/04/2025
 */

public class ButtonTemplate {
    private int height;
    private int width;
    private String backgroundColor;
    private String foregroundColor;
    private int fontSize;
    private String font;
    private int fontWeight;

    // Constructor to initialize styling properties
    public ButtonTemplate(int height, int width, String backgroundColor, String foregroundColor, String font, int fontSize, int fontWeight) 
    {
        this.height = height;
        this.width = width;
        this.backgroundColor = backgroundColor;
        this.foregroundColor = foregroundColor;
        this.fontSize = fontSize;
        this.font = font;
        this.fontWeight = fontWeight;
    }

    /********** Getter methods for each of the attributes **********/
    public int getHeight()
    {
        return this.height;
    }
    public int getWidth()
    {
        return this.width;
    }
    public String getBackgroundColor() 
    {
        return backgroundColor;
    }

    public String getForegroundColor() 
    {
        return foregroundColor;
    }

    public int getFontSize() 
    {
        return fontSize;
    }

    public String getFont() 
    {
        return font;
    }

    public int getFontWeight() 
    {
        return fontWeight;
    }
}
