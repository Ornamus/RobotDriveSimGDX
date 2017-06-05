package ryan.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import java.awt.geom.Point2D;
import java.util.Random;

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

    public static boolean hasDecimal(double d) {
        if ((d == Math.floor(d)) && !Double.isInfinite(d)) {
            return false;
        }
        return true;
    }

    static Random rand = new Random();

    public static int randomInt(int min, int max) {
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    public static float maxFloat(float... nums) {
        float currMax = Math.abs(nums[0]);

        for (float i : nums) {
            currMax = Math.abs(i) > currMax ? Math.abs(i) : currMax;
        }

        return currMax;
    }

    public static float minMax(float val, float min, float max) {
        if (val == 0) return 0; //Needed to avoid DivideByZeroException
        val = Math.abs(val) < min ? 0 : val;
        val = Math.abs(val) > max ? max * sign(val) : val;
        return val;
    }

    /*
    public static Texture colorImage(String texturename, Color... newColors) {
        Texture image = new Texture(texturename);

        //int width = image.getWidth();
        //int height = image.getHeight();
        //WritableRaster raster = image.getRaster();

        Pixmap pixmap = image.getTextureData().consumePixmap();

        for (int xx = 0; xx < pixmap.getWidth(); xx++) {
            for (int yy = 0; yy < pixmap.getHeight(); yy++) {

                Color color = new Color();
                Color.rgba8888ToColor(color, pixmap.getPixel(xx, yy));

                float changeAmount = 0;
                int colorType = -1;
                if (true) { //pixels.length >= 3
                    if (color.r == color.g && color.r == color.b) { //White
                        changeAmount = color.r / 255f;
                        colorType = 0;
                    } else if (color.r != 0 && color.g == 0 && color.b == 0) { //Red
                        changeAmount = color.r / 255f;
                        colorType = 1;
                    } else if (color.r == 0 && color.g != 0 && color.b == 0) { //Blue
                        changeAmount = color.g / 255f;
                        colorType = 2;
                    } else if (color.r == 0 && color.g == 0 && color.b != 0) { //Green
                        changeAmount = color.b / 255f;
                        colorType = 3;
                    }
                    float[] hsb = new float[3];
                    if (newColors.length - 1 >= colorType && colorType != -1) {



                        Color newC = newColors[colorType];
                        Color.RGBtoHSB(newC.getRed(), newC.getBlue(), newC.getGreen(), hsb);
                        hsb[2] *= changeAmount;
                        Color c = new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));

                        pixels[0] = c.getRed();
                        pixels[1] = c.getBlue();
                        pixels[2] = c.getGreen();
                        raster.setPixel(xx, yy, pixels);
                    }
                } else {

                    Log.d("Rejected pixel data: ");
                    int index = 0;
                    for (int i : pixels) {
                        Log.d("[" + index + "] " + i);
                        index++;
                    }
                }
            }
        }
        return image;
    }*/
}
