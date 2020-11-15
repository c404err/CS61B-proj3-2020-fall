package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private int width;
    private int height;
    private TERenderer renderer;
    private TETile[][] world;
    private int hexSize;


    public HexWorld(int width, int height, int size) {
        this.width = width;
        this.height = height;
        renderer = new TERenderer();
        world = new TETile[width][height];
        renderer.initialize(width, height);
        // initialize tiles
        world = new TETile[width][height];
        hexSize = size;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        renderer.renderFrame(world);
    }


    private int maxWidth() {
        return 4 + (3 * (hexSize - 2));
    }

    private class HexagonDrawer {
        TETile tileType;
        int size;
        int startX;
        int startY;

        HexagonDrawer(TETile tileType, int size, int x, int y) {
            this.tileType = tileType;
            this.size = size;
            startX = x;
            startY = y;
            drawBottomHalfOfHex();
            drawTopHalfOfHex();
            //
        }

        public void drawBottomHalfOfHex() {
            for (int x = 0; x < maxWidth(); x++) {
                for (int y = 0; y < size; y++) {
                    if (x >= size - 1 - y && x <= maxWidth() - (size - y)) {
                        drawTile(startX + x, startY + y);
                    }
                }
            }
        }

        public void drawTopHalfOfHex() {
            for (int x = 0; x < maxWidth(); x++) {
                for (int y = size; y < size * 2; y++) {
                    if (x >= y - size && x < maxWidth() - (y - size)) {
                        drawTile(startX + x, startY + y);
                    }
                }
            }
        }

        public void drawTile(int x, int y) {
            world[x][y] = tileType;
            renderer.renderFrame(world);
        }
    }

    private class Hexallation {
        int gridWidth;
        int gridHeight;
        int[][] hexallationGrid;
        TETile[] tileset;
        Random rando;

        Hexallation(TETile[] tileset) {
            rando = new Random(12309);
            gridWidth = (maxWidth() * 3) + (hexSize * 2);
            gridHeight = (hexSize * 2) * 5;
            hexallationGrid = new int[][] {{0, 1, 0}, {0, 1, 1, 0}, {1, 1, 1},
                    {0, 1, 1, 0}, {1, 1, 1}, {0, 1, 1, 0}, {1, 1, 1}, {0, 1, 1, 0}, {0, 1, 0}};
            this.tileset = tileset;
            drawHexallation();
        }

        Hexallation() {
            gridWidth = (maxWidth() * 3) + (hexSize * 2);
            gridHeight = (hexSize * 2) * 5;
            hexallationGrid = new int[][] {{0, 1, 0}, {0, 1, 1, 0}, {1, 1, 1},
                    {0, 1, 1, 0}, {1, 1, 1}, {0, 1, 1, 0}, {1, 1, 1}, {0, 1, 1, 0}, {0, 1, 0}};
            tileset = new TETile[]{Tileset.WALL};
            drawHexallation();
        }

        public void drawHexallation() {
            for (int i = 0; i < hexallationGrid.length; i++) {
                if (i % 2 == 0) {
                    drawEvenRowHex(hexallationGrid[i], i);
                } else {
                    drawOddRowHex(hexallationGrid[i], i);
                }
            }
        }

        public void drawEvenRowHex(int[] hexArray, int y) {
            y = y / 2;
            for (int i = 0; i < hexArray.length; i++) {
                if (hexArray[i] == 1) {
                    int hexXCoordinate = i * (maxWidth() + hexSize);
                    addHexagon(hexXCoordinate, y * (hexSize * 2), chooseTile());
                }
            }
        }

        public void drawOddRowHex(int[] hexArray, int y) {
            y = (y - 1) / 2;
            for (int i = 0; i < hexArray.length; i++) {
                if (hexArray[i] == 1) {
                    int hexXCoordinate = (i * maxWidth()) + ((i - 1) * hexSize) - (hexSize - 1);
                    addHexagon(hexXCoordinate, (y * (hexSize * 2)) + hexSize, chooseTile());
                }
            }
        }

        public TETile chooseTile() {
            if (tileset.length > 1) {
                return tileset[rando.nextInt(tileset.length)];
            } else {
                return tileset[0];
            }
        }
    }
    public void hexallate() {
        new Hexallation();
    }

    public void hexallate(TETile[] tileset) {
        new Hexallation(tileset);
    }

    public void addHexagon(int x, int y) {
        new HexagonDrawer(Tileset.WALL, hexSize, x, y);
    }

    public void addHexagon(int x, int y, TETile tileType) {
        new HexagonDrawer(tileType, hexSize, x, y);
    }

    public static void main(String[] args) {
        HexWorld myHex = new HexWorld(50, 50, 5);
        //myHex.addHexagon(0, 0);
        TETile[] tileset = new TETile[] {Tileset.GRASS, Tileset.FLOOR, Tileset.WATER};
        myHex.hexallate(tileset);
    }
}
