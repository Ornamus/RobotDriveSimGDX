package ryan.game.entity.overboard;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.competition.Team;
import ryan.game.competition.overboard.OverboardTeam;
import ryan.game.entity.BodyFactory;
import ryan.game.entity.Entity;
import ryan.game.games.overboard.robots.OverRobotStats;

import java.util.ArrayList;
import java.util.List;

public class HP_Receive extends Entity {

    //static final Texture HUMAN = new Texture("core/assets/person.png");
    static final Texture CHECK = new Texture("core/assets/check.png");
    static final Texture X = new Texture("core/assets/redx.png");

    //TODO: defined by constructor
    boolean isChest = true;
    boolean blue = true;
    static final float CHEST_CHECK_TIME = 3000;

    Sprite result = null;
    List<Chest.ChestInfo> chestsToCheck = new ArrayList<>();
    long start = -1;

    Sprite player;

    float resAlpha = 1;

    OverRobotStats stats = new OverRobotStats();

    static final float width = .9f, height = .75f;

    public HP_Receive(float x, float y, boolean blue) {
        super(width, height, BodyFactory.getRectangleStatic(x,y, width, height, 2));
        this.blue = blue;
        setSprite(new Texture("core/assets/hp_chute.png"));
        //shirt -> hair -> skin -> pants
        Sprite p = new Sprite(Utils.colorImage("core/assets/person.png", Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE));
        updatePlayer(p);
        /*
        if (Utils.randomInt(0,1) == 0) {
            player = new Sprite(Utils.colorImage("core/assets/person.png", Color.RED, Color.BROWN, Utils.toColor(255, 204, 165), Color.DARK_GRAY));
        } else {
            player = new Sprite(Utils.colorImage("core/assets/person.png", Color.valueOf("f04d23"), Color.BROWN, Utils.toColor(255, 204, 165), Color.valueOf("50b04d")));
        }*/

    }

    public void updateStats(OverboardTeam t) {
        stats = t.stats;
        updatePlayer(new Sprite(Utils.colorImage("core/assets/person.png", t.primary, Color.BROWN, Utils.toColor(255, 204, 165), t.secondary)));
    }

    public void updatePlayer(Sprite p) {
        float rat = 11f/38f;
        //p.setSize(rat*2, 2);
        p.setOriginCenter();
        p.setScale(.06f);
        player = p;
    }

    @Override
    public void tick() {
        super.tick();
        player.setPosition(getX() - 3,getY()-19);
        if (result != null) {
            resAlpha -= 0.005f;
            result.setAlpha(resAlpha);
            if (resAlpha < 0) result = null;
        }
        if (!chestsToCheck.isEmpty() && start != -1 && System.currentTimeMillis() - start > CHEST_CHECK_TIME) {
            start = -1;
            Chest.ChestInfo info = chestsToCheck.get(0);
            chestsToCheck.remove(0);

            result = new Sprite(info.heavy ? CHECK : X);
            result.setSize(1,1);
            resAlpha = 1;

            //eject the chest back out
            float distance = .7f;
            float angle = blue ? 270 : 90;
            float xChange = distance * (float) Math.sin(Math.toRadians(angle));
            float yChange = -distance * (float) Math.cos(Math.toRadians(angle));

            Chest chest = new Chest(getX() + xChange, getY() + yChange, info.heavy, info.alliance);
            chest.cameFromHP = System.currentTimeMillis();

            Main.getInstance().spawnEntity(chest);
            synchronized (Main.WORLD_USE) {
                chest.getPrimary().applyForceToCenter(xChange * 40, yChange * 40, true);
            }
        }
    }

    @Override
    public void draw(SpriteBatch b) {
        super.draw(b);

        float dX = getX() + (blue ? 2.5f : -2.5f);
        float dY = getY();

        player.draw(b);

        if (result != null) {
            result.setPosition(dX - (result.getWidth()/2), getY() + 0.5f);
            result.draw(b);
        }

        if (!chestsToCheck.isEmpty()) {
            Chest.ChestInfo c = chestsToCheck.get(0);
            if (start == -1) start = System.currentTimeMillis();
            Utils.drawProgressBar(dX, dY - 1.25f, 2f, .5f, (System.currentTimeMillis() - start) / CHEST_CHECK_TIME, b);
        }
    }

    @Override
    public void collideStart(Entity e, Body self, Body other, Contact contact) {
        contact.setEnabled(false);
    }

    @Override
    public void onCollide(Entity e, Body self, Body other, Contact contact) {
        if (e instanceof Chest && isChest && /*System.currentTimeMillis() - */((Chest) e).cameFromHP == 0) {
            Chest.ChestInfo f = ((Chest) e).getInfo();
            chestsToCheck.add(f);
            Main.getInstance().removeEntity(e);
        }
    }
}
