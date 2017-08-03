package ryan.game.controls;

public class Button {

    public final int id;
    Object button;

    private static int buttons = 0;

    protected Button(Object c/*, int id*/) {
        button = c;
        id = buttons++;
        //this.id = id;
    }

    public boolean get() {
        //return button.getPollData() == 1;
        return false;
    }
}
