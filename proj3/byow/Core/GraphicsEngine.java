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
    private boolean seedScreenButtonLockout;

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
        seedScreenButtonLockout = true;
    }

    public void setWorldFrame(TETile[][] worldFrame) {
        this.worldFrame = worldFrame;
    }

    private void clear() {
        StdDraw.clear(Color.BLACK);
    }

    /**
     * MAIN MENU METHODS
     */
    public void showMainMenu() {
        clear();
        StdDraw.setFont(splashFont);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(Math.floorDiv(WIDTH, 2), Math.floorDiv(HEIGHT * 10, 16),
                "CS61B: BYOW: The Game: The Sequel: Revengenace");


        StdDraw.setPenRadius(.005);
        mainMenuMouseOvers();
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.rectangle(40, 15, 10, 1.5);
        StdDraw.rectangle(40, 12, 10, 1.5);
        StdDraw.rectangle(40, 9, 10, 1.5);


        StdDraw.setFont(promptFont);
        StdDraw.text(Math.floorDiv(WIDTH, 2), 15,
                "Build Your Own World! (N)");
        StdDraw.text(Math.floorDiv(WIDTH, 2), 12,
                "Load Your Own World! (L)");
        StdDraw.text(Math.floorDiv(WIDTH, 2), 9,
                "Replay Your Own World! (R)");

        StdDraw.show();
    }

    public void mainMenuMouseOvers() {
        double x = StdDraw.mouseX();
        double y = StdDraw.mouseY();

        if (x >= 30 && x <= 50) {
            StdDraw.setPenColor(Color.GRAY);
            if (y > 10.5 && y < 13.5) {
                StdDraw.filledRectangle(40, 12, 10, 1.5);
                if (StdDraw.isMousePressed()) {
                    gameEngine.inputHandler('l');
                }
            } else if (y > 7.5 && y < 10.5) {
                StdDraw.filledRectangle(40, 9, 10, 1.5);
                if (StdDraw.isMousePressed()) {
                    gameEngine.inputHandler('r');
                }
            } else if (y > 13.5 && y < 16.5) {
                StdDraw.filledRectangle(40, 15, 10, 1.5);
                if (StdDraw.isMousePressed()) {
                    gameEngine.inputHandler('n');
                }
            }
        }
    }

    /**
     * SEED INPUT MENU METHODS
     */
    public void showSeedInputScreen() {
        clear();
        StdDraw.setFont(promptFont);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(Math.floorDiv(WIDTH, 2), Math.floorDiv(HEIGHT * 10, 16),
                "Please type your desired seed (Numbers only!)");
        seedInput = Long.toString(gameEngine.getSeed());
        StdDraw.setFont(new Font("Calibri", Font.BOLD, 32));
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(Math.floorDiv(WIDTH, 2), 19, seedInput + "#");

        // Draw button
        seedScreenMouseovers();
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.rectangle(40, 14, 10, 1.5);
        StdDraw.setFont(promptFont);
        StdDraw.text(Math.floorDiv(WIDTH, 2), 14,
                "Finish (S)");


        StdDraw.show();
    }
    public void seedScreenMouseovers() {
        double x = StdDraw.mouseX();
        double y = StdDraw.mouseY();

        if (x >= 30 && x <= 50) {
            StdDraw.setPenColor(Color.GRAY);
            if (y > 12.5 && y < 15.5) {
                StdDraw.filledRectangle(40, 14, 10, 1.5);
                if (StdDraw.isMousePressed() && !seedScreenButtonLockout) {
                    gameEngine.inputHandler('s');
                } else if (seedScreenButtonLockout && !StdDraw.isMousePressed()) {
                    seedScreenButtonLockout = false;
                }
            }
        }
    }

    /**
     * OVEWORLD METHODS
     */

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
        } else if (gameEngine.getCurrentMenu().equals("Overworld")) {
            if (worldFrame == null) {
                worldFrame = gameEngine.getWorldFrame();
            }
            mouseOverX = getXTile() - 2;
            mouseOverY = getYTile() - 2;
            if (checkMouseInBounds()) {
                if (mouseOverTileType == null
                        || !mouseOverTileType.equals(worldFrame[mouseOverX][mouseOverY])) {
                    mouseOverTileType = worldFrame[mouseOverX][mouseOverY];
                    tooltipText = mouseOverTileType.description();
                }
            }
            showWorldScreen();
        }
    }

    /**
     * StdDraw Helper Methods
     * These are necessary because all StdDraw calls must happen from the graphics engine
     * in order to make sure only one StdDraw JFrame opens
     * Unfortunately, the Engine class wants to call certain StdDraw methods so it needs to invoke
     * them from here.
     *
     * All these do is call the corresponding method from within StdDraw
     */
    public void graphicsPause(int millis) {
        StdDraw.pause(millis);
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

    public static void main(String[] args) {
    }
}
