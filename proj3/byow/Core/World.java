package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import static byow.Core.HallwayGenerator.*;

import java.util.Random;

import static byow.Core.RandomUtils.*;

class World {

    //房间参数相关
    static final int MIN_ROOM_NUM = 30;
    static final int MAX_ROOM_NUM = 80;
    static final int MIN_ROOM_SIZE = 3;
    static final int MAX_ROOM_SIZE = 8;

    //走廊参数相关
    static final int HALLWAY_WIDTH = 3;


    //世界实例种类
    static final TETile WALL = Tileset.MOUNTAIN;
    static final TETile FLOOR = Tileset.WATER;
    static final TETile BACKGROUND = Tileset.NOTHING;
    static final TETile PLAYER = Tileset.AVATAR;
    static final TETile DOOR = Tileset.SAND;


    //实例变量
    private final TETile[][] world;
    private Random random;
    private final int width;
    private final int height;
    private int playerX;
    private int playerY;
    World(int width, int height) {
        this.width = width;
        this.height = height;
        world = new TETile[width][height];
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                world[i][j] = BACKGROUND;
            }
        }
        random = new Random();
    }
    World(int width, int height, long seed) {
        this(width, height);
        random = new Random(seed);
        createWorld();
    }

    TETile[][] getWorld() {return world;}

    void createWorld() {
        int roomNum = uniform(random, MIN_ROOM_NUM, MAX_ROOM_NUM);
        Room room = null, preRoom;
        for (int i = 0; i < roomNum; ++i) {
            int x = uniform(random, this.width), y = uniform(random, this.height);
            int width = uniform(random, MIN_ROOM_SIZE, MAX_ROOM_SIZE);
            int height = uniform(random, MIN_ROOM_SIZE, MAX_ROOM_SIZE);
            Room tempRoom = new Room(x, y, width, height);
            if (!isRoomValid(tempRoom)) {continue;}
            preRoom = room;
            room = tempRoom;
            addRoom(room);
            connectRooms(preRoom, room);
        }
        generatePlayer();
    }

    /**
     * Check if a room is in range and not overlapped with other room or hallway.
     * @param room A room to be checked.
     * @return Return true if a room is in the world and only have walls or background in the range.
     */
    boolean isRoomValid(Room room) {
        int x = room.getX(), y = room.getY(), width = room.getWidth(), height = room.getHeight();
        if (x < 0 || y < 0 || x + width > this.width || y + height > this.height) {
            return false;
        }
        for (int i = x; i < x + width; ++i) {
            for (int j = y; j < y + height; ++j) {
                if (world[i][j] != WALL && world[i][j] != BACKGROUND) {
                    return false;
                }
            }
        }
        return true;
    }

    void addRoom(Room room) {
        if (!isRoomValid(room)) {return;}
        int x = room.getX(), y = room.getY(), width = room.getWidth(), height = room.getHeight();
        for (int i = x; i < x + width; ++i) {
            for (int j = y; j < y + height; ++j) {
                if (i == x || i == x + width - 1 || j == y || j == y + height - 1) {
                    world[i][j] = WALL;
                } else {
                    world[i][j] = FLOOR;
                }
            }
        }
    }

    //生成的走廊不会超过房间
    void connectRooms(Room r1, Room r2) {
        if (r1 == null || r2 == null) {return;}
        int x1 = r1.getMiddleX(), y1 = r1.getMiddleY(), x2 = r2.getMiddleX(), y2 = r2.getMiddleY();
        int validHallwayWidth = Math.min(HALLWAY_WIDTH, Math.min(r1.maxHallwayWidth(), r2.maxHallwayWidth()));
        //order为真先水平后垂直，否则先垂直后水平（以r1为起点）
        boolean order = bernoulli(random);
        if (order) {
            //水平
            connectHorizontally(world, validHallwayWidth, x1, x2, y1);
            //垂直
            connectVertically(world, validHallwayWidth, y1, y2, x2);
        } else {
            connectVertically(world, validHallwayWidth, y1, y2, x1);
            connectHorizontally(world, validHallwayWidth, x1, x2, y2);
        }
    }

    void generatePlayer() {
        int x = uniform(random, width), y = uniform(random, height);
        while (world[x][y] != FLOOR) {
            x = uniform(random, width);
            y = uniform(random, height);
        }
        world[x][y] = PLAYER;
        playerX = x;
        playerY = y;
    }

    void movePlayer(int dx, int dy) {
        int nx = playerX + dx, ny = playerY + dy;
        if (!outOfRange(nx, ny) && world[nx][ny] == FLOOR) {
            world[playerX][playerY] = FLOOR;
            world[nx][ny] = PLAYER;
            playerX = nx;
            playerY = ny;
        }
    }
    boolean outOfRange(int x, int y) {
        return x < 0 || x >= width || y < 0 || y >= height;
    }



}
