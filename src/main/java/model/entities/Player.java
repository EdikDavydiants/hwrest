package model.entities;


import java.util.List;
import java.util.Random;

public class Player {
    private int id;
    private final String firstName;
    private final String lastName;
    private int elo;
    private List<Game> gameList;


    public Player(int id, String firstName, String lastName, int elo, List<Game> gameList) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.elo = elo;
        this.gameList = gameList;
    }

    public Player(String firstName, String lastName, int elo) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.elo = elo;
    }


    public String htmlFormString() {
        return id + "   " + firstName + " " + lastName + "  " + elo + "<br>";
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getElo() {
        return elo;
    }

    public void generateElo(Random random) {
        float rand = random.nextFloat();

        float bound1 = 0.10f;
        float bound2 = 0.25f;
        float bound3 = 0.50f;
        float bound4 = 0.70f;
        float bound5 = 0.85f;
        float bound6 = 0.95f;

        if (rand < bound1) {
            elo = 1300 + random.nextInt(200);
        } else if (rand < bound2) {
            elo = 1500 + random.nextInt(200);
        } else if (rand < bound3) {
            elo = 1700 + random.nextInt(200);
        } else if (rand < bound4) {
            elo = 1900 + random.nextInt(300);
        } else if (rand < bound5) {
            elo = 2200 + random.nextInt(300);
        } else if (rand < bound6) {
            elo = 2500 + random.nextInt(200);
        } else {
            elo = 2700 + random.nextInt(150);
        }
    }

}
