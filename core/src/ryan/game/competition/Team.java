package ryan.game.competition;

import com.badlogic.gdx.graphics.Color;

public class Team {

    public int number;
    public String name;
    public final Color primary, secondary;

    public Team(int number, String name) {
        this(number, name, Color.BLUE, Color.DARK_GRAY);
    }

    public Team(int number, String name, Color primary, Color secondary) {
        this.number = number;
        this.name = name;
        this.primary = primary;
        this.secondary = secondary;
        if (name.equals("null")) {
            this.name = NameMagic.generateName();
        }
    }
}
