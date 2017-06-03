package ryan.game;

import java.awt.geom.Point2D;

public class Utils {

    private Utils() {}

    public static float cap(float val, float cap) {
        if (Math.abs(val) > Math.abs(cap)) {
            return cap * (val / Math.abs(val));
        }
        return val;
    }

    public static float deadzone(float val, float deadzone) {
        if (Math.abs(val) < Math.abs(deadzone)) {
            return 0;
        }
        return val;
    }

    public static void log(String s) {
        System.out.println("[INFO] " + s);
    }

    public static float roundToPlace(float d, float place) {
        float amount = 1;
        for (int i=0;i<place;i++) {
            amount *= 10;
        }
        return Math.round(d * amount) / amount;
    }

    public static float sign(float val) {
        if (val == 0) return 1;
        return Math.abs(val) / val;
    }

    public static double getAngle(Point2D start, Point2D target) {
        float angle = (float) Math.toDegrees(Math.atan2(target.getY() - start.getY(), target.getX() - start.getX()));

        if(angle < 0){
            angle += 360;
        }

        return angle;
    }

    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
    }
}
