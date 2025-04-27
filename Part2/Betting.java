package Part2;

/*
 * 
 * This is the Betting class, it's main purpose to to contain the calculate odds function which will calculate the odds
 *  of a passed in Horse object over multiple different variables. It will keep it's own information which is needed for the 
 * algorithm e.g. total bets, it's a public class and can be accessed from anywhere in this program.
 *  
 * 
 * @author Fahi Sabab, Al
 * @version 1.1 27/4/2025
 * 
 * - added in some new attributes to help with implmentation of the betting feature.
 *
 * 
 * 
 */
public class Betting 
{

    static int totalBets = 0;// keep track of total bets across all objects.
    final static int MINBETAMOUNT = 5;
    final static int MAXBETAMOUNT = 50;


    public static  double calculateOdds(String[] currentWeather, String[] previousWeathers, Horse horse, int totalHorses)
    {
        // to do: adding dampening factor as needed.
        // the current weatherarry will contain it's own stats which is needed e.g. name of weather, fall chance modifier, 
        // confidence modifier.
        final double MINODDS = 1.0;
        final double MAXODDS = 25.0;

        final double FALLPENALTY = 1.15;
        final double LOSSPENALTY = 1.05;
        final double WINREWARD = 1.15;
        final double DAMPINGACTOR = 0.1;
        final double MINBETAMOUNT = 240; // make's sure that we don't blow up the base odds with the betting trend multiplier.

        double baseOdds = 0;
        double horseConfidence = Math.max(horse.getConfidence(), 0.1);// make sure we limit this number as we don't want to
        // divide by a really small number

        // i.e. if horse has a lot of confidence and it fell, it would be shocking, hence value will increase, and vice versa.
        double pentaltyFalls = horse.getHorseRecord().getFallCount() * FALLPENALTY * horseConfidence;
        double pentaltyLoss = horse.getHorseRecord().getLossNumber() * LOSSPENALTY;
double rewardWins = horse.getHorseRecord().getWinNumber() * WINREWARD;

// divide by win ratio (when it's not 0.0)
        double pentaltyWinRatio = Math.max(0.35, horse.getHorseRecord().getWinLossRatio());// limit the win ratio.
        // we wannt horses with lower bets placed to have higher odds.
        // alos need to make sure that we don't divide by zero when the horse hasn't gotten any bets yet.
        // we need to add in a damping factor as the bettingTrendMultiplier can get very high.
        double bettingTrendMultiplier = (Math.min(horse.getBettingAmount(), MINBETAMOUNT)
         / Math.max(horse.getTotalBets(), 1) * DAMPINGACTOR);

        double WeatherConfidenceModifier = Double.parseDouble(currentWeather[1]);
        double WeatherFallChanceModifier = Double.parseDouble(currentWeather[2]);
        double weatherDifficulty = Math.abs(WeatherConfidenceModifier * WeatherFallChanceModifier); 

        double horsePrevStatsOnWeather = calculateWeatherImpact(currentWeather, previousWeathers, horse, totalHorses);

        
        // horses with more falls and losses makes them underdogs, and should have more value
        // horses with more wins are less risky, therefore less value.
        // horses with lower win ratio should have higher odds as it's more risky.
        // horses less confidennt are more risky, so higher value.
        double denominator = Math.min(1.0, (5 * pentaltyWinRatio * horseConfidence));// just a safetey catch
                                                                            // even though min deominator can be is about 0.625
        baseOdds = Math.abs((baseOdds - rewardWins + pentaltyFalls + pentaltyLoss + horsePrevStatsOnWeather) 
        / denominator);
        // harsher weathers have lower decimal values, so we divide base odds by it to get higher values
        // (high risk high reward etc), we need to tone down the base odds with the bettingTrendMultiplier.
        baseOdds = (baseOdds / weatherDifficulty)  * (bettingTrendMultiplier + 1);

        return Math.min(Math.max(baseOdds, MINODDS), MAXODDS);// limit the odds range to be within the specified bounds.
    }

    // This function will be used to dynamically affect the odds of the horse depending on the current weater
    // based on how well the horse has performed last time in that weather.
    //
    private static double calculateWeatherImpact(String[] currentWeather, String [] previousWeathers, Horse horse, int totalHorses) 
    {
        double weatherImpact = 0;

        for (int i = 0; i < previousWeathers.length; i++) 
        {  
            // since positions are added after round ends, we need to make sure that we end after we have seen all horse
            // positions. (weathers gets added at the start of each race, so there is a displarity.)
            //
            if (i >= horse.getHorseRecord().getPosition().size()) 
            {
                break;
            }
            // Horse has participated in this weather before
            if (currentWeather[0].equals(previousWeathers[i])) 
            { 
                int position = horse.getHorseRecord().getPosition().get(i);

                if (position == -1) 
                {
                    weatherImpact += 1.15; // Horse DNF in this weather (riskier, increase odds)
                } 
                else if (position < (totalHorses / 2)) 
                {
                    weatherImpact += 1.05; // Placed below half of current horses
                } 
                else 
                {
                    weatherImpact -= 1.20; // Placed well in this weather
                }
            }
        }
        return weatherImpact;
    }

    // this function will just increment the total bets value.
    //
    public static void incrementTotalBets()
    {
        totalBets++;
    }

    // this function will reset the total bets counter to 0.
    //
    public static void resetTotalBets()
    {
        totalBets = 0;
    }
    
}

