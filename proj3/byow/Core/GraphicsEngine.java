package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Font;
import java.awt.Color;

public class GraphicsEngine {
    private final int WIDTH;
    private final int HEIGHT;
    private final Font splashFont;
    private final Font promptFont;
    private final Font tooltipFont;
    private final TERenderer renderer;
    private String seedInput;
    private String tooltipText;
    private TETile[][] worldFrame;
    private int mouseOverX;
    private int mouseOverY;
    private TETile mouseOverTileType;

    public GraphicsEngine(int width, int height) {
        int yOffset = 2;
        int yOffsetTop = 5;
        renderer = new TERenderer();
        renderer.initialize(width, height, 2, yOffset);
        splashFont = new Font("Calibri", Font.BOLD, 48);
        promptFont = new Font("Calibri", Font.BOLD, 24);
        tooltipFont = new Font("Calibri", Font.BOLD, 18);
        WIDTH = width;
        HEIGHT = height + yOffset + yOffsetTop;
        seedInput = "";
        tooltipText = "";
        mouseOverX = 0;
        mouseOverY = 0;
    }

    private void clear() {
        StdDraw.clear(Color.BLACK);
    }

    public void showMainMenu() {
        clear();
        StdDraw.setFont(splashFont);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(Math.floorDiv(WIDTH, 2), Math.floorDiv(HEIGHT * 10, 16), "CS61B: BYOW: The Game: The Sequel: Revengenace");
        StdDraw.setFont(promptFont);
        StdDraw.text(Math.floorDiv(WIDTH, 2), Math.floorDiv(HEIGHT, 3), "Press N to Build Your Own World!");
        StdDraw.show();
    }

    public void showSeedInputScreen() {
        clear();
        StdDraw.setFont(promptFont);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(Math.floorDiv(WIDTH, 2), Math.floorDiv(HEIGHT * 10, 16), "Please type your desired seed (Numbers only!)");
        StdDraw.text(Math.floorDiv(WIDTH, 2), Math.floorDiv(HEIGHT * 10, 18), "Type S when finished");
        StdDraw.show();
    }
    public void seedScreenTextUpdate(String input) {
        showSeedInputScreen();
        seedInput += input;
        StdDraw.setFont(promptFont);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(Math.floorDiv(WIDTH, 2), Math.floorDiv(HEIGHT * 10, 21), seedInput + "#");
        StdDraw.show();
    }

    public void showWorldScreen() {
        clear();
        renderer.renderFrame(worldFrame);
        StdDraw.setFont(tooltipFont);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.textLeft(1, HEIGHT - 1, tooltipText);
        StdDraw.line(0, HEIGHT - 2, WIDTH + 4, HEIGHT - 2);
        StdDraw.show();
    }
    public void setTooltipText(String newTooltip) {
        tooltipText = newTooltip;
        showWorldScreen();
    }
    /**
     * getXTile() and getYTile()
     * Returns the x or y coordinate of the TETile[][]
     * array that corresponds to mouse location
     * This is necessary for displaying tooltips about tiles,
     * and may be useful for later game functions.
     * @source: CS61B Homework 2, Percolation
     * Inspiration for these methods was pulled from the percolation simulator.
     */
    public int getXTile() {
        double x = StdDraw.mouseX();
        return (int) Math.floor(x);
    }
    public int getYTile() {
        double y = StdDraw.mouseY();
        return (int) Math.floor(y);
    }

    /**
     * gameLoopHook()
     * Hook into this method from the main game loop to trigger updates as necessary
     */
    public void gameLoopHook() {
        mouseOverX = getXTile() - 2;
        mouseOverY = getYTile() - 2;
        if (checkMouseInBounds()) {
            if (mouseOverTileType == null || !mouseOverTileType.equals(worldFrame[mouseOverX][mouseOverY])) {
                mouseOverTileType = worldFrame[mouseOverX][mouseOverY];
                setTooltipText(mouseOverTileType.description());
            }
        }
    }

    /**
     * checkMouseInBounds()
     * @return bool - whether or not the mouse is in the bounds of the main
     * game grid.
     */
    private boolean checkMouseInBounds() {
        return (mouseOverX < worldFrame.length && mouseOverY >= 0
                && mouseOverY < worldFrame[0].length && mouseOverX >= 0);
    }


    /**
     * SIMULATION METHODS
     * These methods exist for testing purposes only!
     */
    private void simulateGameLoop() {
        while (true) {
            gameLoopHook();
        }
    }
    private void simulateSeedEntry(String seedEntry) {
        for (int i = 0; i < seedEntry.length(); i++) {
            seedScreenTextUpdate(seedEntry.substring(i, i+1));
            StdDraw.pause(200);
        }
    }


    public static void main(String[] args) {
        int dummySeed = 123495182;
        GraphicsEngine myFrame = new GraphicsEngine(Engine.WIDTH, Engine.HEIGHT);
        myFrame.showMainMenu();
        StdDraw.pause(3500);
        //myFrame.showSeedInputScreen();
        myFrame.simulateSeedEntry(dummySeed + "S");
        myFrame.worldFrame = new Engine().interactWithInputString("N" + dummySeed + "S");
        StdDraw.pause(2500);
        myFrame.showWorldScreen();
        StdDraw.pause(4000);
        myFrame.setTooltipText("Hello World! This is a tooltip!");
        myFrame.showWorldScreen();
        myFrame.simulateGameLoop();
    }
}
