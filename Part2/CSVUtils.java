package Part2;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;// we use this library to get access to the fields of any passed in class.

/*
 * This is a little helper utility fileIO class which is used to write and  records/ objects to and from csvs very easily.
 * 
 *  @author Fahi Sabab, Al
 * @version 1.0 26/4/2025
 * 
 * 
 */

class CSVUtils
{
    // This function is used to append the data from the passed in class/record into the passed in file in append mode (doesn't )
    // overwrite the file.
    //
    public static void AppendToCSV(String fileName, Object dataToBePutIn)
    {
        // writing in append mode.
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true)))
        {
            String csvLine = getCSVFriendlyString(dataToBePutIn); // get the CSV-formatted string
            writer.write(csvLine);
            writer.newLine(); // move to next line
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    // method to get an object or record and then append all data into a csv friendly format.
    // pre conditions: continuase data must be stored in a java.util.List, NOT in other classes or normal arrays.
    //
    private static String getCSVFriendlyString(Object object)
    {
        String ret = "";
        Field field;
        Field[] fields = object.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++)
        {
            field = fields[i];
            try
            {
                field.setAccessible(true);

                Object value = field.get(object);// getting the value inside that field.
                if (value == null)// skip to next field.
                {
                    continue;
                }
                if (value instanceof List)// the field is some sort of list e.g. arrayList
                {
                    // wrap list in double quotes
                    ret = ret + "\"";
                    for (int j = 0; j < ((List<?>)value).size(); j++)
                    {
                        ret = ret + String.valueOf(((List<?>)value).get(j));
                        if (j <((List<?>)value).size() - 1)
                        {
                            ret = ret + "|"; //seperate the elements with a bar.
                        }
                    }
                    // close the double quotes:
                    ret = ret + "\"";
                }
                else
                {
                    ret = ret + String.valueOf(value);
                }
                ret = ret + ",";// append a comma at the end of each field.
                
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

        }
        return ret.substring(0, ret.length()- 1);// remove the last comma.
    }



   // Function to read all data from a specific file, and then we return a 2-d string array (each inner array represents one line
   // within the file)
   //
    public static String[] getDataFromCSV(String filePath)
    {
        ArrayList<String[]> processedData = new ArrayList<>();
        String line;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath)))
        {
            while ((line = reader.readLine()) != null)
            {
                // Remove double quotes
                String processedLine = line.replace("\"", ""); 
                String[] splitData = processedLine.split(",");  // Split by comma
                
                // Add each part of the split data to the ArrayList
                processedData.add(splitData);
            }
        } 
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
        return (String []) processedData.toArray();
    }
}