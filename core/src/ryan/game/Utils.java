package ryan.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.gson.Gson;

import java.awt.geom.Point2D;
import java.io.*;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

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

    public static float fixAngle(float angle) {
        while (angle > 360) angle -= 360;
        while (angle < 0) angle = 360 + angle;
        return angle;
    }

    public static double getAngle(Point2D start, Point2D target) {
        float angle = (float) Math.toDegrees(Math.atan2(target.getY() - start.getY(), target.getX() - start.getX()));

        if(angle < 0){
            angle += 360;
        }

        return angle;
    }

    public static float distance(float x1, float y1, float x2, float y2) {
        return (float)Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
    }

    public static boolean hasDecimal(double d) {
        return !((d == Math.floor(d)) && !Double.isInfinite(d));
    }

    static Random rand = new Random();

    public static int randomInt(int min, int max) {
        return rand.nextInt((max - min) + 1) + min;
    }

    public static float randomFloat(float min, float max) {
        return (rand.nextInt((Math.round((max - min) * 1000)) + 1) + (min*1000))/1000;
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


    public static Texture colorImage(String texturename, Color... newColors) {
        Texture image = new Texture(texturename);

        image.getTextureData().prepare();
        Pixmap pixmap = image.getTextureData().consumePixmap();

        for (int xx = 0; xx < pixmap.getWidth(); xx++) {
            for (int yy = 0; yy < pixmap.getHeight(); yy++) {
                Color color = new Color();
                Color.rgba8888ToColor(color, pixmap.getPixel(xx, yy));
                if (color.a > 0) {
                    int[] rgb = new int[3];
                    rgb[0] = Math.round(color.r * 255);
                    rgb[1] = Math.round(color.g * 255);
                    rgb[2] = Math.round(color.b * 255);

                    float changeAmount = 0;
                    int colorType = -1;

                    if (rgb[0] == rgb[1] && rgb[0] == rgb[2]) { //White
                        changeAmount = rgb[0] / 255f;
                        colorType = 0;
                    } else if (rgb[0] != 0 && rgb[1] == 0 && rgb[2] == 0) { //Red
                        changeAmount = rgb[0] / 255f;
                        colorType = 1;
                    } else if (rgb[0] == 0 && rgb[1] != 0 && rgb[2] == 0) { //Blue
                        changeAmount = rgb[1] / 255f;
                        colorType = 2;
                    } else if (rgb[0] == 0 && rgb[1] == 0 && rgb[2] != 0) { //Green
                        changeAmount = rgb[2] / 255f;
                        colorType = 3;
                    }
                    float[] hsb = new float[3];
                    if (newColors.length - 1 >= colorType && colorType != -1) {
                        Color newC = newColors[colorType];
                        if (newC != null) {
                            java.awt.Color.RGBtoHSB(Math.round(newC.r * 255), Math.round(newC.g * 255), Math.round(newC.b * 255), hsb);
                            hsb[2] *= changeAmount;
                            java.awt.Color c = new java.awt.Color(java.awt.Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));

                            Color recolor = toColor(c.getRed(), c.getGreen(), c.getBlue());
                            pixmap.setColor(recolor);
                            pixmap.fillRectangle(xx, yy, 1, 1);
                        }
                    }
                }
            }
        }
        Texture recolor = new Texture(pixmap);
        image.getTextureData().disposePixmap();
        pixmap.dispose();
        return recolor;
    }

    public static void drawProgressBar(float x, float y, float width, float height, float progress, SpriteBatch b) {
        x = x - (width/2);
        progress = Utils.cap(progress, 1);
        Sprite outline = new Sprite(Utils.colorImage("core/assets/whitepixel.png", Color.BLACK));
        Sprite background = new Sprite(Utils.colorImage("core/assets/whitepixel.png", Color.RED));
        Sprite front = new Sprite(Utils.colorImage("core/assets/whitepixel.png", Color.GREEN));
        outline.setBounds(x, y, width, height);
        background.setBounds(x + .1f, y + .1f, width-.2f, height-.2f);
        float frontLength = progress * (width-.2f);
        front.setBounds(x + .1f, y + .1f, frontLength, height-.2f);
        outline.draw(b);
        background.draw(b);
        front.draw(b);
    }

    public static void drawUnscaledProgressBar(float x, float y, float width, float height, float progress, SpriteBatch b) {
        x = x - (width/2);
        progress = Utils.cap(progress, 1);
        Sprite outline = new Sprite(Utils.colorImage("core/assets/whitepixel.png", Color.BLACK));
        Sprite background = new Sprite(Utils.colorImage("core/assets/whitepixel.png", Color.RED));
        Sprite front = new Sprite(Utils.colorImage("core/assets/whitepixel.png", Color.GREEN));
        outline.setBounds(x, y, width, height);
        background.setBounds(x + (Main.meterToPixelWidth*.1f), y + (Main.meterToPixelHeight*.1f),
                width-(Main.meterToPixelWidth*.2f), height-(Main.meterToPixelHeight*.2f));
        float frontLength = progress * (width-(Main.meterToPixelWidth*.2f));
        front.setBounds(x + (Main.meterToPixelWidth*.1f), y + (Main.meterToPixelHeight*.1f),
                frontLength, height-(Main.meterToPixelHeight*.2f));
        outline.draw(b);
        background.draw(b);
        front.draw(b);
    }

    public static Color toColor(int r, int g, int b) {
        return new Color(r / 255f, g / 255f, b / 255f, 1);
    }

    public static <T> T fromJSON(String fileName, Class<T> clazz) {
        Gson g = new Gson();
        try {
            return g.fromJson(new FileReader(fileName), clazz);
        } catch (Exception e) {
            Utils.log("File not found: " + fileName);
            //e.printStackTrace();
            return null;
        }
    }

    public static <T> T fromJSON(File f, Class<T> clazz) {
        Gson g = new Gson();
        try {
            return g.fromJson(new FileReader(f), clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //TODO: fix problem where this gets angry if any folders don't exist
    public static void writeFile(String fileName, String content) {
        BufferedWriter bw = null;
        try {
            File file = new File(fileName);
            file.mkdirs();
            file.delete();
            file.createNewFile();
            FileWriter fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            bw.write(content);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (bw != null)
                    bw.close();
            } catch (Exception ex) {
                Utils.log("Error in closing the BufferedWriter" + ex);
            }
        }
        Utils.log("wrote file");
    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
