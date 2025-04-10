import java.util.Scanner;

public class helperFunctions {
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

}
