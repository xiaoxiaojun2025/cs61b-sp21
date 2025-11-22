package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import static byow.Core.HallwayGenerator.*;

import java.util.Random;

import static byow.Core.RandomUtils.*;

class WorldGenerator {
    //特殊按键
    public static final char NEW_WORLD = 'N';
    public static final char SEED = 'S';

    //房间参数相关
    private static final int MIN_ROOM_NUM = 30;
    private static final int MAX_ROOM_NUM = 80;
    private static final int MIN_ROOM_SIZE = 3;
    private static final int MAX_ROOM_SIZE = 8;

    //走廊参数相关
    private static final int HALLWAY_WIDTH = 2;
    private TETile[][] world;
    private int width;
    private int height;
    private Random random;
    WorldGenerator(int width, int height) {
        this.width = width;
        this.height = height;
        world = new TETile[width][height];
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                world[i][j] = Tileset.NOTHING;
            }
        }
        random = new Random();
    }
    WorldGenerator(int width, int height, String input) {
        this(width, height);
        long seed = getSeed(input);
        if (seed > 0) {
            random = new Random(seed);
        }
        createWorld(input);
    }
    TETile[][] getWorld() {return world;}
    void createWorld(String input) {
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
    }
    boolean isRoomValid(Room room) {
        int x = room.getX(), y = room.getY(), width = room.getWidth(), height = room.getHeight();
        TETile wall = room.getWall();
        if (x < 0 || y < 0 || x + width > this.width || y + height > this.height) {
            return false;
        }
        for (int i = x; i < x + width; ++i) {
            for (int j = y; j < y + height; ++j) {
                if (world[i][j] != wall && world[i][j] != Tileset.NOTHING) {
                    return false;
                }
            }
        }
        return true;
    }
    void addRoom(Room room) {
        if (!isRoomValid(room)) {return;}
        int x = room.getX(), y = room.getY(), width = room.getWidth(), height = room.getHeight();
        TETile wall = room.getWall(), floor = room.getFloor();
        for (int i = x; i < x + width; ++i) {
            for (int j = y; j < y + height; ++j) {
                if (i == x || i == x + width - 1 || j == y || j == y + height - 1) {
                    world[i][j] = wall;
                } else {
                    world[i][j] = floor;
                }
            }
        }
    }
    private static int findMax(int...nums) {
        int max = nums[0];
        for (int i = 1; i < nums.length; ++i) {
            max = Math.max(max, nums[i]);
        }
        return max;
    }
    private static int maxHallwayWidth(int length) {return 2 * ((length - 1) / 2) - 1;}
    //生成的走廊不会超过房间
    void connectRooms(Room r1, Room r2) {
        if (r1 == null || r2 == null) {return;}
        TETile wall = r1.getWall(), floor = r1.getFloor();
        int x1 = r1.getMiddleX(), y1 = r1.getMiddleY(), x2 = r2.getMiddleX(), y2 = r2.getMiddleY();
        int validHallway = Math.min(HALLWAY_WIDTH, findMax(maxHallwayWidth(r1.getWidth()), maxHallwayWidth(r1.getHeight())
                ,maxHallwayWidth(r2.getWidth()), maxHallwayWidth(r2.getHeight())));
        Hallway hallway = new Hallway(wall, floor, validHallway);
        //order为真先水平后垂直，否则先垂直后水平（以r1为起点）
        boolean order = true;
        if (order) {
            //水平
            connectHorizontally(world, hallway, x1, x2, y1);
            //垂直
            connectVertically(world, hallway, y1, y2, x2);
        } else {
            connectVertically(world, hallway, y1, y2, x1);
            connectHorizontally(world, hallway, x1, x2, y2);
        }
    }
    static long getSeed(String input) {
        InputThing inputThing = new StringInput(input);
        while (inputThing.haveNextKey()) {
            char c = inputThing.getNextKey();
            if (c == NEW_WORLD) {
                StringBuilder stringBuilder = new StringBuilder();
                while (inputThing.haveNextKey()) {
                    c = inputThing.getNextKey();
                    if (c == SEED) {
                        if (stringBuilder.length() == 0) {
                            return -1;
                        }
                        return Long.parseLong(stringBuilder.toString());
                    }
                    stringBuilder.append(c);
                }
            }
        }
        return -1;
    }
}
