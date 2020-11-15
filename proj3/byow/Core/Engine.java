package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.*;

public class Engine {
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    public TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];
    Random r;

    private class leaf {
        private int MIN_SIZE = 10;
        public int x, y, width, height;
        public leaf left = null, right = null;
        public rec room = null;

        public leaf(int X, int Y, int W, int H) {
            x = X;
            y = Y;
            width = W;
            height = H;
        }

        public boolean split() {
            if (left != null || right != null) {
                return false;
            }
            boolean horizontal = r.nextBoolean();
            if (width > height) {
                horizontal = false;
            } else if (width < height) {
                horizontal = true;
            }

            int max = (horizontal ? height : width) - MIN_SIZE;

            if (max <= MIN_SIZE) {
                return false;
            }

            int split = MIN_SIZE + r.nextInt(max - MIN_SIZE);

            if (horizontal) {
                left = new leaf(x, y, width, split);
                right = new leaf(x, y + split, width, height - split);
            } else {
                left = new leaf(x, y, split, height);
                right = new leaf(x + split, y, width - split, height);
            }
            return true;
        }

        public void rooms() {
            if (left != null || right != null) {
                if (left != null) {
                    left.rooms();
                }
                if (right != null) {
                    right.rooms();
                }
                if (right != null && left != null) {
                    connect(left.getRoom(), right.getRoom());
                }
            }
            else {
                int roomWidth = 4 + r.nextInt(Math.max(1, width - 1 - 4)),
                        roomHeight = 4 + r.nextInt(Math.max(1, height - 1 - 4)),
                        roomX = 1 + r.nextInt(Math.max(1, width - roomWidth - 1 - 1)),
                        roomY = 1 + r.nextInt(Math.max(1, height - roomHeight - 1 - 1));
                room = new rec(x + roomX, y + roomY, roomWidth, roomHeight);
                paintRoom(room);
            }
        }

        public rec getRoom() {
            if (room != null) {
                return room;
            }
            rec leftRoom = null, rightRoom = null;
            if (left != null) {
                leftRoom = left.getRoom();
            }
            if (right != null) {
                rightRoom = right.getRoom();
            }

            if (leftRoom == null && rightRoom == null) {
                return null;
            } else if (leftRoom == null) {
                return rightRoom;
            } else if (rightRoom == null) {
                return leftRoom;
            } else if (r.nextBoolean()) {
                return leftRoom;
            }
            return rightRoom;
        }
    }

    private class rec {
        public int x, y, width, height,
                    left, right, bot, top;
        public rec(int X, int Y, int w, int h) {
            x = X;
            y = Y;
            width = w;
            height = h;
            left = x;
            right = x + width;
            bot = y;
            top = y + height;
        }
    }

    public void paintRoom(rec room) {
        int x = room.x, y = room.y, width = room.width, height = room.height;
        int i = x, j = y;

        while (i < x + width && i < WIDTH) {
            if (finalWorldFrame[i][j-1] != Tileset.FLOOR)
                finalWorldFrame[i][j-1] = Tileset.WALL;
            while (j < y + height && j < HEIGHT) {
                finalWorldFrame[i][j] = Tileset.FLOOR;
                if (i == x && finalWorldFrame[i-1][j] != Tileset.FLOOR) {
                    finalWorldFrame[i-1][j] = Tileset.WALL;
                }
                if (i == x + width - 1 && finalWorldFrame[i+1][j] != Tileset.FLOOR) {
                    finalWorldFrame[i+1][j] = Tileset.WALL;
                }
                j++;
            }
            if (finalWorldFrame[i][j] != Tileset.FLOOR)
                finalWorldFrame[i][j] = Tileset.WALL;
            j = y;
            i++;
        }

        if (finalWorldFrame[x-1][y-1] != Tileset.FLOOR)
            finalWorldFrame[x-1][y-1] = Tileset.WALL;
        if (finalWorldFrame[x+width][y-1] != Tileset.FLOOR)
            finalWorldFrame[x+width][y-1] = Tileset.WALL;
        if (finalWorldFrame[x-1][y+height] != Tileset.FLOOR)
            finalWorldFrame[x-1][y+height] = Tileset.WALL;
        if (finalWorldFrame[x+width][y+height] != Tileset.FLOOR)
            finalWorldFrame[x+width][y+height] = Tileset.WALL;

    }


    public void createLeafs() {
        int MAX_SIZE = 12;
        List<leaf> leaves = new ArrayList<>();
        leaf singleLeaf = new leaf(0, 0, WIDTH, HEIGHT);
        leaves.add(singleLeaf);
        boolean doSplit = true;

        while (doSplit) {
            doSplit = false;
            for (int i = 0; i < leaves.size(); i++) {
                leaf l = leaves.get(i);
                if (l.left == null && l.right == null) {
                    if (l.width > MAX_SIZE || l.height > MAX_SIZE) {
                        if (l.split()) {
                            leaves.add(l.left);
                            leaves.add(l.right);
                            doSplit = true;
                        }
                    }
                }
            }
        }
        singleLeaf.rooms();
    }

    public void connect(rec front, rec back) {

            int frontX = front.left + 1 + r.nextInt(Math.max(1, front.right - front.left - 3)),
                    frontY = front.bot + 1 + r.nextInt(Math.max(1, front.top - front.bot - 3)),
                    backX = back.left + 1 + r.nextInt(Math.max(1, back.right - back.left - 3)),
                    backY = back.bot + 1 + r.nextInt(Math.max(1, back.top - back.bot - 3)),
                    width = backX - frontX,
                    height = backY - frontY;

            if (width < 0) {
                if (height < 0) {
                    if (r.nextBoolean()) {
                        paintRoom(new rec(backX, frontY, Math.abs(width) + 1, 1));
                        paintRoom(new rec(backX, backY, 1, Math.abs(height) + 1));
                    } else {
                        paintRoom(new rec(backX, backY, Math.abs(width) + 1, 1));
                        paintRoom(new rec(frontX, backY, 1, Math.abs(height) + 1));
                    }
                } else if (height > 0) {
                    if (r.nextBoolean()) {
                        paintRoom(new rec(backX, frontY, Math.abs(width) + 1, 1));
                        paintRoom(new rec(backX, frontY, 1, Math.abs(height) + 1));
                    } else {
                        paintRoom(new rec(frontX, frontY, 1, Math.abs(height) + 1));
                        paintRoom(new rec(backX, backY, Math.abs(width) + 1, 1));
                    }
                } else {
                    paintRoom(new rec(backX, backY, Math.abs(width) + 1, 1));
                }
            } else if (width > 0) {
                if (height < 0) {
                    if (r.nextBoolean()) {
                        paintRoom(new rec(frontX, backY, Math.abs(width) + 1, 1));
                        paintRoom(new rec(frontX, backY, 1, Math.abs(height) + 1));
                    } else {
                        paintRoom(new rec(frontX, frontY, Math.abs(width) + 1, 1));
                        paintRoom(new rec(backX, backY, 1, Math.abs(height) + 1));
                    }
                } else if (height > 0) {
                    if (r.nextBoolean()) {
                        paintRoom(new rec(frontX, frontY, Math.abs(width) + 1, 1));
                        paintRoom(new rec(backX, frontY, 1, Math.abs(height) + 1));
                    } else {
                        paintRoom(new rec(frontX, backY, Math.abs(width) + 1, 1));
                        paintRoom(new rec(frontX, frontY, 1, Math.abs(height) + 1));
                    }
                } else {
                    paintRoom(new rec(frontX, frontY, Math.abs(width) + 1, 1));
                }
            } else {
                if (height < 0) {
                    paintRoom(new rec(backX, backY, 1, Math.abs(height) + 1));
                } else {
                    paintRoom(new rec(frontX, frontY, 1, Math.abs(height) + 1));
                }
            }

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
        // TODO: Fill out this method so that it run the engine using the input
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
                while ((input.charAt(index) >= '0' && input.charAt(index) <= '9') && (input.charAt(index) != 'S' || input.charAt(index) != 's')) {
                    seed = seed * 10 + (input.charAt(index) - 48);
                    index++;
                }
                index++;
                r  = new Random(seed);
                finalWorldFrame = TeClear(finalWorldFrame);
                createLeafs();

            } else if (input.charAt(index) == 'l' || input.charAt(index) == 'L') {
                index++;
            } else if (input.charAt(index) == ':' && (input.charAt(index) == 'q' || input.charAt(index) == 'Q')) {
                index++;
            } else {
                index++;
            }
        }

        return finalWorldFrame;
    }

    private TETile[][] TeClear(TETile[][] world) {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                world[i][j] = Tileset.NOTHING;
            }
        }
        return world;
    }

    private TETile[][] newWorld(TETile[][] world) {


        return world;
    }

    public static void main(String[] args) {
        Engine e = new Engine();
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        e.interactWithInputString("N232441S");
        ter.renderFrame(e.finalWorldFrame);
    }
}
