package map;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

import core.Config.BlockColor;
import core.Config;

public class Map {
    protected Rectangle2D.Double worldRect = new Rectangle2D.Double();

    protected ArrayList<Obstacle> obstacles;
    protected ArrayList<MapBlock> blocks;
    public Robot bot;

    protected Point robotStart;
    protected Point robotGoal;

    // takes bot +
    public Map(double margin) {

    }

    public boolean addBlock(MapBlock b) {        
        if (!this.isOnMap(b)) {
            blocks.add(b);
            return true;
        }
        
        return false;
    }

    

    public boolean checkSegment(Segment seg) {
        return false;
    }

    public void nearestIntersectingSegment(Segment seg) {

    }

    public boolean isOnMap(MapBlock block) {
        for (MapBlock b : blocks) {
            BlockColor color = b.getColor();
            if (b.distance(block) < Config.minDist && color == block.getColor()) {
                return true;
            }
        }
        return false;
    }

    public MapBlock closestBlock() {
        MapBlock bestBlock = blocks.get(0);
        double minDist = bestBlock.distance(bot.center);

        for (MapBlock b : blocks) {
            double d = b.distance(bot.center);

            if (d < minDist) {
                minDist = d;
                bestBlock = b;
            }
        }
        return bestBlock;
    }

    private void parsePoint(Point point, BufferedReader br, String name, int lineNumber) throws IOException,
            ParseException, NumberFormatException {

        String line = br.readLine();
        String[] tok = (line != null) ? line.split("\\s+") : null;

        if ((tok == null) || (tok.length < 2)) {
            throw new ParseException(name + " (line " + lineNumber + ")", lineNumber);
        }

        point.x = Double.parseDouble(tok[0]);
        point.y = Double.parseDouble(tok[1]);
    }

    private void parseRect(Rectangle2D.Double rect, BufferedReader br, String name, int lineNumber) throws IOException,
            ParseException, NumberFormatException {

        String line = br.readLine();
        String[] tok = (line != null) ? line.split("\\s+") : null;

        if ((tok == null) || (tok.length < 4))
            throw new ParseException(name + " (line " + lineNumber + ")", lineNumber);

        rect.x = Double.parseDouble(tok[0]);
        rect.y = Double.parseDouble(tok[1]);
        rect.width = Double.parseDouble(tok[2]);
        rect.height = Double.parseDouble(tok[3]);
    }

    private Obstacle parseObs(BufferedReader br, String name, int lineNumber) throws IOException, ParseException,
            NumberFormatException {

        String line = br.readLine();

        if (line == null)
            return null;

        String[] tok = line.trim().split("\\s+");

        if (tok.length == 0)
            return null;

        if (tok.length % 2 != 0)
            throw new ParseException(name + " (line " + lineNumber + ")", lineNumber);

        Obstacle poly = new Obstacle();

        for (int i = 0; i < tok.length / 2; i++)
            poly.addVertex(new Point(Double.parseDouble(tok[2 * i]), Double.parseDouble(tok[2 * i + 1])));

        poly.close();

        return poly;
    }

    protected void parse(File mapFile) throws IOException, ParseException {
        int lineNumber = 1;
        try {

            BufferedReader br = new BufferedReader(new FileReader(mapFile));

            parsePoint(robotStart, br, "robot start", lineNumber++);
            parsePoint(robotGoal, br, "robot goal", lineNumber++);
            parseRect(worldRect, br, "world rect", lineNumber++);

            for (int obstacleNumber = 0;; obstacleNumber++) {

                Obstacle obs = parseObs(br, "obstacle " + obstacleNumber, lineNumber++);
                if (obs != null) {
                    obs.color = Color.blue;
                    obstacles.add(obs);
                } else
                    break;
            }

        } catch (NumberFormatException e) {
            throw new ParseException("malformed number on line " + lineNumber, lineNumber);
        }
    }
}