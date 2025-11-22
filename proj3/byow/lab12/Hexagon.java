package byow.lab12;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Hexagon {
    private int size;
    private TETile type;
    private Position pos;
    public Hexagon(Position pos, int size, TETile type) {
        this.size = size;
        this.type = type;
        this.pos = pos;
    }
    public Hexagon(int xpos, int ypos, int size, TETile type) {
        this.size = size;
        this.type = type;
        this.pos = new Position(xpos, ypos);
    }
    public int size() {return size;}
    public TETile type() {return type;}
    public int X() {return pos.getX();}
    public int Y() {return pos.getY();}
    public int getWidth() {
        return size() + 2 * (size() - 1);
    }
    public int getHeight() {
        return size() * 2;
    }
}
