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
    Random randomNumberGen;
    private Player player;
    private int seed;
    private boolean seedInput = false;
    private boolean seedFinalized = false;
    private boolean quitPrimed = false;
    private boolean playerControl = false;
    private StringBuilder inputHistory;
    private boolean quit;
    private boolean graphics;
    private GraphicsEngine gEngine;
    private String currentMenu;




    //public Player p;

    public Engine() {
        seed = 0;
        inputHistory = new StringBuilder();
        quit = false;
    }
    public Engine(boolean graphics) {
        this.graphics = graphics;
        seed = 0;
        inputHistory = new StringBuilder();
        quit = false;
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        if (graphics) {
            currentMenu = "Main";
            gEngine = new GraphicsEngine(Engine.WIDTH, Engine.HEIGHT, this);
            graphicalGameLoop();
        }
        gameLoop();
    }

    private void gameLoop() {
        while (!quit) {
            if (StdDraw.hasNextKeyTyped()) {
                String input = Character.toString(StdDraw.nextKeyTyped()).toLowerCase();
                inputHandler(input.charAt(0));
            }
        }
    }

    private void graphicalGameLoop() {
        while (!quit) {
            gEngine.gameLoopHook();
            if (gEngine.hasNextKeyTyped()) {
                String input = Character.toString(gEngine.nextKeyTyped()).toLowerCase();
                inputHandler(input.charAt(0));
            }
        }
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
    public TETile[][] interactWithInputString(String input) throws Exception {

        if (input == null) {
            return null;
        }
        input = input.toLowerCase();
        for (int i = 0; i < input.length(); i++) {
            inputHandler(input.charAt(i));
        }
        return worldFrame;
    }

    public void inputHandler(char input)
    {
        inputHistory.append(input);
        if (seedInput) {
            seedHandler(input);
        } else if (!seedFinalized) {
            if (input == 'n') {
                seedInput = true;
                currentMenu = "SeedInput";
            } else if (input == 'l') {
                //load();
            } else if (input == 'r') {
                //replay();
            }
        } else if (playerControl) {
            if (input == 'w' || input == 'a' || input == 's' || input == 'd') {
                player.move(input);
            } else if (input == ':' && !quitPrimed) {
                quitPrimed = true;
            } else if (input == 'q' && quitPrimed) {
                //save();
                quit = true;
            }
        }
    }

    private void seedHandler(char input) {
        if (input == 's') {
            seedInput = false;
            seedFinalized = true;
            generateWorld();
        } else if (input >= '0' && input <= '9') {
            seed = seed * 10 + (input - 48);
        }
    }

    private void generateWorld() {
        randomNumberGen = new Random(seed);
        worldFrame = new BSP(WIDTH, HEIGHT, randomNumberGen).createLeaves();
        player = new Player(WIDTH, HEIGHT, worldFrame, randomNumberGen);
        player.addPlayer();
        playerControl = true;
        currentMenu = "Overworld";
    }

    public int getSeed() {
        return seed;
    }

    private TETile[][] load()
            throws Exception {
        File inputFile = new File("fa20-proj3-g488\\proj3\\byow\\Core\\saved.txt");
        Scanner reader = new Scanner(inputFile);
        player = new Player(WIDTH, HEIGHT, worldFrame, new Random());
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                if (reader.hasNextInt()) {
                    int num = reader.nextInt();
                    if (num == 0) {
                        worldFrame[i][j] = Tileset.AVATAR;
                        player.setX(i);
                        player.setY(j);
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

    public String getCurrentMenu() {
        return currentMenu;
    }

    public static void main(String[] args) throws Exception {
        Engine engine = new Engine(true);
        engine.interactWithKeyboard();
        System.out.println("done");
    }
}
