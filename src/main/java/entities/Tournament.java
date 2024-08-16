package entities;

public class Tournament {
    private int id;
    private String name;


    public Tournament(String name) {
        this.name = name;
    }

    public Tournament(int id, String name) {
        this.id = id;
        this.name = name;
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
