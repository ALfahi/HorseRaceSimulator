package Part2;
/**
 * This class holds some helper functions which are used by the Horse clas and Race class for this program to work, 
 * it offers many general functions which can also be used outside of this program.
 * 
 * @author Fahi Sabab, Al
 * @version 1.1 11/4/2025
 * - added a function to return a string representing a decimal number to n places.
 */

import java.util.Scanner;

public class HelperFunctions {
    // constructor to create an instance of this function:
    //
    public HelperFunctions()
    {
        
    }
    // method to get an input from the user, returns a String
    //
    public static String getInput(String message) 
    {
        Scanner scanner = new Scanner(System.in);
        String input;
        System.out.print(message);
        input = scanner.nextLine();
        return input;
    }

    // creating a generic linear search method that takes an array and a target value as input
    // and returns the index of the target value in the array or -1 if not found
    //
    public static<T> int linearSearch(T[] array, T target) 
    {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(target)) {
                return i; // Return the index of the found element
            }
        }
        return -1; // Return -1 if the element is not found
    }

    // method to get a valid input from the user, the valid inputs are passed into a string array
    //
    public static String getValidInput(String message, String [] availableOpions)
    {
        String input = getInput(message);
        int index = linearSearch(availableOpions, input);
        while (index == -1) {
            System.out.println("Invalid input. Please try again.");
            input = getInput(message);
            index = linearSearch(availableOpions, input);
        }
        return input;
    }

    // this function forces the user to type in a valid integer within the bounds specified by the parameters.
    // returns an integer (that's within the passed in bounds.)
    //
    public static int getValidInteger(String message, int lowerBound, int upperBound)
    {
        int userInput = 0;
        while(userInput < lowerBound || userInput > upperBound)
        {
            try{
                 userInput = Integer.parseInt(getInput(message));

            }catch(NumberFormatException e)
            {
                System.out.println("please enter an integer");
                userInput = 0;
            }
        }
        return userInput;
    }

    // this function displays a decimal number to n decimal places.
    //
    public static String displayNDecimalPlaces(double number, int decimalPlace)
    {
        String formattedString = "%." +  decimalPlace + "f";
        return String.format(formattedString, number);
    }
}
