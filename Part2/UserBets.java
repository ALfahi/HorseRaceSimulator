package Part2;
import java.util.ArrayList;
/*
 * 
 * This is the user bet's record, it will be used to store information of what the user has betted on,( used to keep
 *  track on the user's betting acitivty).
 *  
 * 
 * @author Fahi Sabab, Al
 * @version 1.0 27/4/2025
 * 
 * - added in some new attributes to help with implmentation of the betting feature.
 *
 * 
 * 
 */
class UserBets 
{
    private String date;// we use reflection to access all the fields, so we can ignore this warning.
    private ArrayList<String> names = new ArrayList<>();// names of the horses that user betted for
    private ArrayList<String> winRecord = new ArrayList<>();// did they win? (yes or no)
    private ArrayList<Double> winnings = new ArrayList<>();// their earnings (negative and positive).
    private double netEarnings = 0;// overall score.

    // constructor to intialise the date variable
    //
    public UserBets(String date)
    {
        this.date = date;
    }
    /******* getters and setters ******/

    // This function will get the names arrayList
    //
    public ArrayList<String> getNames()
    {
        return this.names;
    }

    //This funcrtion will get the win record arraylist.
    //
    public ArrayList<String> getWinRecord()
    {
        return this.winRecord;
    }

    //This function will get the winnings arrayList.
    //
    public ArrayList<Double> getWinnings()
    {
        return this.winnings;
    }

    // This returns the current earnings
    //
    public double getEarnings()
    {
        return this.netEarnings;
    }

    // This function will set the date:
    //
    public void setDate(String date)
    {
        this.date = date;
    }

    // This function increments the netAmount total by the new double that's passed in.
    //
    public void updateEarnings(double amount)
    {
        System.out.println("the earnign before is" +this.netEarnings);
        this.netEarnings = this.netEarnings + amount;
        System.out.println("earning after is" + this.netEarnings);
    }
}
