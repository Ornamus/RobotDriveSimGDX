package ryan.game.competition;

public class Team {

    public int number;
    public String name;

    public Team(int number, String name) {
        this.number = number;
        this.name = name;
        if (name.equals("null")) {
            this.name = NameMagic.generateName();
        }
    }
}
