package byow.Core;


import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Random;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Engine {
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    public TETile[][] worldFrame;
    private TERenderer renderer;
    Random r;
    private BSP map;
    private Player p;




    //public Player p;

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


    public TETile[][] interactWithInputString(String input)
    throws Exception {
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
                map = new BSP(WIDTH, HEIGHT, r);
                worldFrame = map.createLeaves();
                p = new Player(WIDTH, HEIGHT, worldFrame, r);
                worldFrame = p.addPlayer();
            } else if (input.charAt(index) == 'W' || input.charAt(index) == 'w'
                    || input.charAt(index) == 'S' || input.charAt(index) == 's'
                    || input.charAt(index) == 'A' || input.charAt(index) == 'a'
                    || input.charAt(index) == 'D' || input.charAt(index) == 'd') {
                worldFrame = p.move(input.charAt(index));
                index++;
            } else if (input.charAt(index) == 'l' || input.charAt(index) == 'L') {
                load();
                index++;
            } else if (input.charAt(index) == 'S' || input.charAt(index) == 's') {
                save();
                index++;
            } else if (input.charAt(index) == ':' && (input.charAt(index + 1) == 'q'
                    || input.charAt(index + 1) == 'Q')) {
                save();
                break;
            } else {
                index++;
            }
        }

        return worldFrame;
    }


    private TETile[][] getWorldFrame() {
        return worldFrame;
    }

    private void setWorldFrame(TETile[][] world) {
        worldFrame = world;
    }

    public void initializeRenderer() {
        renderer = new TERenderer();
        renderer.initialize(WIDTH, HEIGHT);
    }
    public void renderWorld() {
        renderer.renderFrame(worldFrame);
    }

    public void move(char c) {
        worldFrame = p.move(c);
    }

    private TETile[][] load()
            throws Exception {
        File inputFile = new File("fa20-proj3-g488\\proj3\\byow\\Core\\saved.txt");
        Scanner reader = new Scanner(inputFile);
        p = new Player(WIDTH, HEIGHT, worldFrame, new Random());
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (reader.hasNextInt()) {
                    int num = reader.nextInt();
                    if (num == 0) {
                        worldFrame[i][j] = Tileset.AVATAR;
                        p.setX(i);
                        p.setY(j);
                    } else if (num == 1) {
                        worldFrame[i][j] = Tileset.WALL;
                    } else if (num == 2) {
                        worldFrame[i][j] = Tileset.FLOOR;
                    } else if (num == 3) {
                        worldFrame[i][j] = Tileset.NOTHING;
                    } else if (num == 4) {
                        worldFrame[i][j] = Tileset.GRASS;
                    } else if (num == 5) {
                        worldFrame[i][j] = Tileset.WATER;
                    } else if (num == 6) {
                        worldFrame[i][j] = Tileset.FLOWER;
                    } else if (num == 7) {
                        worldFrame[i][j] = Tileset.LOCKED_DOOR;
                    } else if (num == 8) {
                        worldFrame[i][j] = Tileset.UNLOCKED_DOOR;
                    } else if (num == 9) {
                        worldFrame[i][j] = Tileset.SAND;
                    } else if (num == 10) {
                        worldFrame[i][j] = Tileset.MOUNTAIN;
                    } else if (num == 11) {
                        worldFrame[i][j] = Tileset.TREE;
                    }
                }
            }
        }return worldFrame;
    }

    public void save()
            throws Exception {
        File outputFile = new File("fa20-proj3-g488\\proj3\\byow\\Core\\saved.txt");
        if (!outputFile.createNewFile()) {
            outputFile.delete();
            outputFile.createNewFile();
        }
        PrintWriter writer = new PrintWriter(outputFile);
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (worldFrame[i][j].equals(Tileset.AVATAR)) {
                    writer.write("0 ");
                } else if (worldFrame[i][j].equals(Tileset.WALL)) {
                    writer.write("1 ");
                } else if (worldFrame[i][j].equals(Tileset.FLOOR)) {
                    writer.write("2 ");
                } else if (worldFrame[i][j].equals(Tileset.NOTHING)) {
                    writer.write("3 ");
                } else if (worldFrame[i][j].equals(Tileset.GRASS)) {
                    writer.write("4 ");
                } else if (worldFrame[i][j].equals(Tileset.WATER)) {
                    writer.write("5 ");
                } else if (worldFrame[i][j].equals(Tileset.FLOWER)) {
                    writer.write("6 ");
                } else if (worldFrame[i][j].equals(Tileset.LOCKED_DOOR)) {
                    writer.write("7 ");
                } else if (worldFrame[i][j].equals(Tileset.UNLOCKED_DOOR)) {
                    writer.write("8 ");
                } else if (worldFrame[i][j].equals(Tileset.SAND)) {
                    writer.write("9 ");
                } else if (worldFrame[i][j].equals(Tileset.MOUNTAIN)) {
                    writer.write("10 ");
                } else if (worldFrame[i][j].equals(Tileset.TREE)) {
                    writer.write("11 ");
                }
            }
        }
        writer.close();
    }

    public static void main(String[] args)
            throws Exception {
        Engine engine = new Engine();
        engine.initializeRenderer();
        //engine.interactWithInputString("N19980711S:q");
        engine.load();
        engine.renderWorld();
//        //engine.save();

        char input = 0;
        while (input != 'q') {
            if (StdDraw.hasNextKeyTyped()) {
                input = StdDraw.nextKeyTyped();
                engine.move(input);
                engine.renderWorld();
            }
            Thread.sleep(500);
        }
        engine.save();
        System.out.println("done");
        return;
    }
}
