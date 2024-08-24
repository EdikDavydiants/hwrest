package model.entities;

import java.util.List;

public class Tournament {
    private int id;
    private String name;
    private List<Game> gameList;


    public Tournament(String name) {
        this.name = name;
    }

    public Tournament(int id, String name, List<Game> gameList) {
        this.id = id;
        this.name = name;
        this.gameList = gameList;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String htmlFormString() {
        return id + "   " + name + "<br>";
    }

}
