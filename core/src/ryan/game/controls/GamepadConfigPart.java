package ryan.game.controls;

public class GamepadConfigPart {

    public final int key;
    public final String display;
    public boolean optional = false;

    public GamepadConfigPart(int key, String display) {
        this.key = key;
        this.display = display;
    }

    public GamepadConfigPart(int key, String display, boolean optional) {
        this.key = key;
        this.display = display;
        this.optional = optional;
    }
}
