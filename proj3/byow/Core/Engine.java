package byow.Core;

import byow.TileEngine.TETile;
import edu.princeton.cs.algs4.StdDraw;

import java.io.PrintWriter;
import java.util.Random;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Engine {
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private TETile[][] worldFrame;
    private Random randomNumberGen;
    private Player player;
    private long seed;
    private boolean seedInput = false;
    private boolean seedFinalized = false;
    private boolean quitPrimed = false;
    private boolean playerControl = false;
    private StringBuilder inputHistory;
    private boolean quit;
    private boolean graphics;
    private GraphicsEngine gEngine;
    private String currentMenu;

    public Engine() {
        seed = 0;
        inputHistory = new StringBuilder();
        quit = false;
    }
    public Engine(boolean graphic) {
        this.graphics = graphic;
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
    public TETile[][] interactWithInputString(String input) {

        if (input == null) {
            return null;
        }
        input = input.toLowerCase();
        for (int i = 0; i < input.length(); i++) {
            inputHandler(input.charAt(i));
        }
        return worldFrame;
    }

    public void inputHandler(char input) {
        inputHistory.append(input);
        if (seedInput) {
            seedHandler(input);
        } else if (!seedFinalized) {
            if (input == 'n') {
                seedInput = true;
                currentMenu = "SeedInput";
            } else if (input == 'l') {
                load(false);
            } else if (input == 'r') {
                load(true);
            }
        } else if (playerControl) {
            if (input == 'w' || input == 'a' || input == 's' || input == 'd') {
                player.move(input);
            } else if (input == ':' && !quitPrimed) {
                quitPrimed = true;
            } else if (input == 'q' && quitPrimed) {
                save();
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

    public long getSeed() {
        return seed;
    }

    public TETile[][] getWorldFrame() {
        return worldFrame;
    }


    public String getCurrentMenu() {
        return currentMenu;
    }

    private void load(boolean replay) {
        try {
            File inputFile = new File("saved.txt");
            Scanner reader = new Scanner(inputFile);
            String input = "";

            if (!reader.hasNext()) {
                System.exit(0);
            } else {
                input = reader.nextLine();
            }

            for (int i = 0; i < input.length(); i++) {
                inputHandler(input.charAt(i));
                gEngine.gameLoopHook();
                if (replay) {
                    gEngine.graphicsPause(150);
                }
            }
            reader.close();
            inputHistory = new StringBuilder(input);
        } catch (IOException e) {
            System.exit(0);
        }
    }

    public void save() {
        try {
            File outputFile = new File("saved.txt");
            PrintWriter writer = new PrintWriter(outputFile, "UTF-8");

            // Chop last two characters (:q)
            writer.println(inputHistory.substring(0, inputHistory.length() - 2));
            writer.close();
        } catch (IOException e) {
            System.out.println("save error");
        }
    }



    public static void main(String[] args) {
        Engine engine = new Engine(true);
        engine.interactWithKeyboard();
        System.out.println("done");
    }
}
