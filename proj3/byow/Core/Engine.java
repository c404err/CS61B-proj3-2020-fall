package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

import java.util.Random;

public class Engine {
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private TETile[][] worldFrame;
    private TERenderer renderer;
    Random r;

    public Engine() {
        worldFrame = new TETile[WIDTH][HEIGHT];
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */


    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        int index = 0;
        long seed = 0;

        if (input == null) {
            return null;
        }

        while (index < input.length()) {
            if (input.charAt(index) == 'N' || input.charAt(index) == 'n') {
                index++;
                while ((input.charAt(index) >= '0' && input.charAt(index) <= '9')
                        && (input.charAt(index) != 'S' || input.charAt(index) != 's')) {
                    seed = seed * 10 + (input.charAt(index) - 48);
                    index++;
                }
                index++;
                r  = new Random(seed);
                worldFrame = new BSP(WIDTH, HEIGHT, r).createLeaves();
            } else if (input.charAt(index) == 'l' || input.charAt(index) == 'L') {
                index++;
            } else if (input.charAt(index) == ':' && (input.charAt(index) == 'q'
                    || input.charAt(index) == 'Q')) {
                index++;
            } else {
                index++;
            }
        }

        return worldFrame;
    }


    private TETile[][] newWorld(TETile[][] world) {
        return world;
    }

    public void initializeRenderer() {
        renderer = new TERenderer();
        renderer.initialize(WIDTH, HEIGHT);
    }
    public void renderWorld() {
        renderer.renderFrame(worldFrame);
    }

    public static void main(String[] args) {
        Engine engine = new Engine();
        engine.initializeRenderer();
        engine.interactWithInputString("N" + new Random().nextInt() + "S");
        engine.renderWorld();
        System.out.println("done");
    }
}
