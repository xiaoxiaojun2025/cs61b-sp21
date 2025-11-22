package byow.Core;

import byow.TileEngine.TETile;

class HallwayGenerator {
    /**
     * Vertically create a matrix hallway from minX to maxX, or [minX, maxX].
     * @param world The world needed to add hallway.
     * @param hallway The specific hallway.
     * @param startX Start X position.
     * @param endX End X position.
     * @param y Fixed Y position.
     */
    static void connectHorizontally(TETile[][] world, Hallway hallway,int startX, int endX, int y) {
        int width = hallway.getWidth();
        TETile wall = hallway.getWall(), floor = hallway.getFloor();
        int minX = Math.min(startX, endX), maxX = Math.max(startX, endX);
        int down = y - width / 2 - 1, up = y + (width - 1) / 2 + 1;
        for (int i = minX; i <= maxX; ++i) {
            for (int j = down; j <= up; ++j) {
                if ((j == up || j == down) && world[i][j] != floor) {
                    world[i][j] = wall;
                } else {
                    world[i][j] = floor;
                }
            }
        }
    }

    /**
     * Vertically create a matrix hallway from startY to endY, or [startY, endY].
     * @param world The world needed to add hallway.
     * @param hallway The specific hallway.
     * @param startY Start Y position.
     * @param endY End Y position.
     * @param x Fixed X position.
     */
    static void connectVertically(TETile[][] world, Hallway hallway,int startY, int endY, int x) {
        int width = hallway.getWidth();
        TETile wall = hallway.getWall(), floor = hallway.getFloor();
        int minY = Math.min(startY, endY), maxY = Math.max(startY, endY);
        for (int j = minY; j <= maxY; ++j) {
            int left = x - width / 2 - 1, right = x + (width - 1) / 2 + 1;
            for (int i = left; i <= right; ++i) {
                if ((i == left || i == right) && world[i][j] != floor) {
                    world[i][j] = wall;
                } else {
                    world[i][j] = floor;
                }
            }
        }
    }
}
