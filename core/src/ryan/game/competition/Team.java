package ryan.game.competition;

public class Team {

    public int number;
    public String name;
    public RobotStats stats = null;

    public Team(int number, String name) {
        this.number = number;
        this.name = name;
        if (name.equals("null")) {
            name = NameMagic.generateName();
            //Utils.log(name);
        }
    }
}
