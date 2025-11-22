package byow.Core;

import byow.TileEngine.TETile;

public class MyMain {
    public static void main(String[] args) {
        if (args.length < 1) {
            return;
        } else {
            Engine engine = new Engine();
            engine.ter.initialize(Engine.WIDTH, Engine.HEIGHT);
            TETile[][] world = engine.interactWithInputString(args[0]);
            engine.ter.renderFrame(world);
        }
    }
}
