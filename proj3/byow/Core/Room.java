package byow.Core;

import byow.TileEngine.*;

class Room {
    private int x;
    private int y;
    private int width = 3;
    private int height = 3;
    private TETile wall = Tileset.MOUNTAIN;
    private TETile floor = Tileset.WATER;
    Room() {
        this(0, 0);
    }
    Room(int x, int y) {
        this.x = x;
        this.y = y;
    }
    Room(int x, int y, int height, int width) {
        this(x, y);
        this.height = Math.max(this.height, height);
        this.width = Math.max(this.width, width);
    }
    Room(int x, int y, int height, int width, TETile wall, TETile floor) {
        this(x, y, height, width);
        this.wall = wall;
        this.floor = floor;
    }
    int getX() {return x;}
    int getY() {return y;}
    int getWidth() {return width;}
    int getHeight() {return height;}
    TETile getWall() {return wall;}
    TETile getFloor() {return floor;}
    int getMiddleX() {return x + width / 2;}
    int getMiddleY() {return y + height / 2;}
    static int distance(Room r1, Room r2) {
        return Math.abs(r1.getMiddleX() - r2.getMiddleX()) + Math.abs(r1.getMiddleY() - r2.getMiddleY());
    }
}