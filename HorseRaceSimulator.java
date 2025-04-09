class HorseRaceSimulator{
    public static void main(String[] args) {
       Race race = new Race(30);
       race.addHorse(new Horse('&', "Bob", 0.1), 1);
       race.addHorse(new Horse('^', "Jeff", 0.2), 2);
       race.addHorse(new Horse('+', "Tony", 0.9), 3);
       race.startRace();
    }
}