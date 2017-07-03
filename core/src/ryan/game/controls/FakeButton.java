package ryan.game.controls;

public class FakeButton extends Button {

    public FakeButton() {
        super(null);
    }

    @Override
    public boolean get() {
        return false;
    }
}
