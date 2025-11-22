package byow.Core;

import byow.TileEngine.TETile;

class Hallway {
    private int width = 1;
    private TETile wall;
    private TETile floor;
    Hallway(TETile wall, TETile floor) {
        this.wall = wall;
        this.floor = floor;
    }
    Hallway(TETile wall, TETile floor, int width) {
        this(wall, floor);
        this.width = width;
    }
    public int getWidth() {return width;}
    public TETile getWall() {return wall;}
    public TETile getFloor() {return floor;}
}
