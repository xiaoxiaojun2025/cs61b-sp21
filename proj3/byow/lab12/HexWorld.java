package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;


/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 80;
    private static final int HEIGHT = 30;

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        Hexagon hexagon1 = new Hexagon(30, 10, 5, Tileset.FLOWER);
        Hexagon hexagon2 = new Hexagon(50, 10, 2, Tileset.AVATAR);
        Hexagon hexagon3 = new Hexagon(0, 15, 3, Tileset.GRASS);
        Hexagon hexagon4 = new Hexagon(20, 0, 5, Tileset.MOUNTAIN);
        addHexagon(world, hexagon1);
        addHexagon(world, hexagon2);
        addHexagon(world, hexagon3);
        addHexagon(world, hexagon4);
        ter.renderFrame(world);
    }

    public static void addHexagon(TETile[][] world, Hexagon hexagon) {
        int R = world.length;
        int C = world[0].length;
        try {
            if (!isHexagonInRange(R, C, hexagon)) {
                throw new RuntimeException("六边形越界");
            }
            int X = hexagon.X();
            int width = hexagon.getWidth();
            int Y = hexagon.Y();
            int height = hexagon.getHeight();
            for (int i = Y; i < Y + height; ++i) {
                for (int j = X; j < X + width; ++j) {
                    TETile currTile = calculateOneTile(i, j, hexagon);
                    if (currTile != null) {
                        //先横坐标，后纵坐标
                        world[j][i] = currTile;
                    }
                }
            }
        } catch (RuntimeException e) {
            System.out.println("错误：" + e.getMessage());
            System.exit(1);
        }

    }
    private static TETile calculateOneTile(int i, int j, Hexagon hexagon) {
        double xMid = (double) (hexagon.X() * 2 + hexagon.getWidth() - 1) / 2;
        double yMid = (double) (hexagon.Y() * 2 + hexagon.getHeight() - 1) / 2;
        if (Math.ceil(Math.abs(j - xMid)) <= (double) hexagon.getWidth() / 2 - Math.floor(Math.abs(i - yMid))) {
            return hexagon.type();
        }
        return null;
    }
    private static boolean isHexagonInRange(int R, int C, Hexagon hexagon) {
        int X = hexagon.X();
        int height = hexagon.getHeight();
        int width = hexagon.getWidth();
        int Y = hexagon.Y();
        return X >= 0 && X + width <= R && Y >= 0 && Y + height <= C;
    }
}

