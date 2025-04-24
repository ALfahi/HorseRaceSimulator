package Part2;
// This class is just a record, and it will store some data which will then be written to csv.
//
import java.util.ArrayList;

class HorseRecord
{
    String name;
    String breed;
    ArrayList<Double> confidence;
    String item;
    int winNumber;
    int lossNumber;
    int fallCount;
    double winLossRatio;
    double fastestFinishTime;
    String raceDate;// also stores number of lanes e.g. racedate( 10 lanes)
    double averageSpeed;
    ArrayList<Integer> position;

}