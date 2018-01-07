package ryan.game.ai;

import org.xguzm.pathfinding.grid.GridCell;
import org.xguzm.pathfinding.grid.NavigationGrid;
import org.xguzm.pathfinding.grid.finders.AStarGridFinder;
import ryan.game.Main;
import ryan.game.Utils;
import ryan.game.entity.Entity;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class Pathfinding {

    AStarGridFinder<GridCell> finder = null;
    public GridCell[][] cells;
    NavigationGrid<GridCell> navGrid = null; //TODO: should be created each pathfind*
    public float resolutionInMeters = 1;

    public Pathfinding() {
        cells = createCells();
        navGrid = new NavigationGrid<>(cells, true);
        //noinspection unchecked
        finder = new AStarGridFinder(GridCell.class);
    }

    public Point2D.Float toFinderCoords(float x, float y) {
        float newX = x + ((Main.world_width * resolutionInMeters) / 2f);
        float newY = y + ((Main.world_height * resolutionInMeters) / 2f);
        return new Point2D.Float(newX, newY);
    }

    public Point2D.Float toWorldCoords(float x, float y) {
        float newX = x - ((Main.world_width * resolutionInMeters) / 2f);
        float newY = y - ((Main.world_height * resolutionInMeters) / 2f);
        return new Point2D.Float(newX, newY);
    }

    public List<Point2D.Float> findPath(int sX, int sY, int eX, int eY) {
        Point2D.Float start = toFinderCoords(sX, sY);
        Point2D.Float end = toFinderCoords(eX, eY);

        List<GridCell> path = finder.findPath(Math.round(start.x), Math.round(start.y), Math.round(end.x), Math.round(end.y), navGrid);
        List<Point2D.Float> points = new ArrayList<>();
        for (GridCell c : path) {
            points.add(toWorldCoords(c.getX(), c.getY()));
        }
        return points;
    }

    public GridCell[][] createCells() {
        GridCell[][] cells = new GridCell[Main.world_width][Main.world_height];
        for (int x=0; x<Main.world_width; x++) {
            for (int y=0; y<Main.world_height; y++) {
                GridCell c = new GridCell(x, y);
                c.setWalkable(true);
                cells[x][y] = c;
            }
        }

        for (Entity e : Main.getInstance().getEntities()) {
            if (!Main.drawablesRemove.contains(e)) {
                //TODO: additional IF statements to factor things like entities that can be driven through or not
                float realWidth = e.width * resolutionInMeters;
                float realHeight = e.height * resolutionInMeters;

                Point2D.Float loc = toFinderCoords(e.getX(), e.getY());
                float realX = loc.x - (realWidth / 2);
                float realY = loc.y - (realHeight / 2);

                Utils.log("[" + realX + "," + realY + "] to " + " [" + (realX + realWidth) + "," + (realY + realHeight) + "]");
                //Utils.log("(" + e.getX() + "," + e.getY() + " )   ->   " + realX + "," + realY + "");
                float rectX = (float) Math.floor(realX), rectY = (float) Math.floor(realY);
                float rectWidth = (float) Math.ceil(realX + realWidth) - rectX;
                float rectHeight = (float) Math.ceil(realY + realHeight) - rectY;
                Rectangle2D.Float area = new Rectangle2D.Float(rectX, rectY, rectWidth, rectHeight);
                //Rectangle2D.Float area = new Rectangle2D.Float((float) Math.floor(realX), (float)Math.floor(realY), (float)Math.ceil(realWidth), (float)Math.ceil(realHeight));
                //Rectangle2D.Float area = new Rectangle2D.Float(realX, realY, realWidth, realHeight);
                String recS = "Rect is [" + area.x + "," + area.y + "] to [" + (area.x + area.width) + "," + (area.y + area.height) + "]";
                Utils.log(recS);
                for (int x = 0; x < cells.length; x++) {
                    for (int y = 0; y < cells[0].length; y++) {
                        //Utils.log("Does " + recS + " contain (" + x + "," + y + ")?");
                        if (area.contains(x, y)) {
                            Utils.log("locked tile (" + x + "," + y + ")");
                            cells[x][y].setWalkable(false);
                        }
                    }
                }
            }
        }
        return cells;
    }
}
