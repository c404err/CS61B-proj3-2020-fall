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
    private final Engine gameEngine;

    public GraphicsEngine(int width, int height, Engine gameEngine) {
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
        this.gameEngine = gameEngine;
    }

    public void setWorldFrame(TETile[][] worldFrame) {
        this.worldFrame = worldFrame;
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
        seedInput = Integer.toString(gameEngine.getSeed());
        StdDraw.setFont(promptFont);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(Math.floorDiv(WIDTH, 2), Math.floorDiv(HEIGHT * 10, 21), seedInput + "#");
        StdDraw.show();
    }
    public void seedScreenTextUpdate() {
        //StdDraw.show();
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
        if (gameEngine.getCurrentMenu().equals("Main")) {
            showMainMenu();
        } else if (gameEngine.getCurrentMenu().equals("SeedInput")) {
            showSeedInputScreen();
            seedScreenTextUpdate();
        } else if (gameEngine.getCurrentMenu().equals("Overworld")) {
            if (worldFrame == null) {
                worldFrame = gameEngine.worldFrame;
            }
            mouseOverX = getXTile() - 2;
            mouseOverY = getYTile() - 2;
            if (checkMouseInBounds()) {
                if (mouseOverTileType == null || !mouseOverTileType.equals(worldFrame[mouseOverX][mouseOverY])) {
                    mouseOverTileType = worldFrame[mouseOverX][mouseOverY];
                    setTooltipText(mouseOverTileType.description());
                }
            }
            showWorldScreen();
        }
    }

    public boolean hasNextKeyTyped() {
        return StdDraw.hasNextKeyTyped();
    }
    public char nextKeyTyped() {
        return StdDraw.nextKeyTyped();
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

    public static void main(String[] args) throws Exception {
    }
}
