package ryan.game.controls;

import net.java.games.input.Component;

public class Button {

    public final int id;
    Component button;

    private static int buttons = 0;

    protected Button(Component c/*, int id*/) {
        button = c;
        id = buttons++;
        //this.id = id;
    }

    public boolean get() {
        return button.getPollData() == 1;
    }
}
