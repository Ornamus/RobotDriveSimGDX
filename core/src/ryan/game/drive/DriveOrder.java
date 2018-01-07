package ryan.game.drive;

public class DriveOrder {

    public final float left, right;
    public Float middle = null;

    public DriveOrder(float left, float right) {
        this.left = left;
        this.right = right;
    }

    public DriveOrder(float left, float right, float middle) {
        this.left = left;
        this.right = right;
        this.middle = middle;
    }

    public boolean hasMiddle() {
        return middle != null;
    }
}
