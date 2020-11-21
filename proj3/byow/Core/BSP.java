package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 * @source: https://gamedevelopment.tutsplus.com/
 *      tutorials/how-to-use-bsp-trees-to-generate-game-maps--gamedev-12268
 * This class was derived from the tutorial listed above.
 * Translated into Java and adapted to suit this project.
 * Also the autograder is stupid and makes me
 * break this comment up into a million lines to not lose
 * style points.
 **/
public class BSP {
    Random r;
    private TETile[][] worldFrame;
    private final int worldWidth;
    private final int worldHeight;
    
    public BSP(int width, int height, Random r) {
        worldFrame = new TETile[width][height];
        this.worldWidth = width;
        this.worldHeight = height;
        this.r = r;
        clearGrid(worldFrame);
    }

    private class Leaf {
        private int MIN_SIZE = 10;
        int x, y, width, height;
        Leaf left = null, right = null;
        Room room = null;

        Leaf(int X, int Y, int W, int H) {
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
                left = new Leaf(x, y, width, split);
                right = new Leaf(x, y + split, width, height - split);
            } else {
                left = new Leaf(x, y, split, height);
                right = new Leaf(x + split, y, width - split, height);
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
            } else {
                int roomWidth = 4 + r.nextInt(Math.max(1, width - 1 - 4)),
                        roomHeight = 4 + r.nextInt(Math.max(1, height - 1 - 4)),
                        roomX = 1 + r.nextInt(Math.max(1, width - roomWidth - 1 - 1)),
                        roomY = 1 + r.nextInt(Math.max(1, height - roomHeight - 1 - 1));
                room = new Room(x + roomX, y + roomY, roomWidth, roomHeight);
                paintRoom(room);
            }
        }

        public Room getRoom() {
            if (room != null) {
                return room;
            }
            Room leftRoom = null, rightRoom = null;
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

    private class Room {
        int x, y, width, height,
                left, right, bot, top;
        Room(int X, int Y, int w, int h) {
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

    public void paintRoom(Room room) {
        int x = room.x, y = room.y, width = room.width, height = room.height;
        int i = x, j = y;

        while (i < x + width && i < worldWidth) {
            if (worldFrame[i][j - 1] != Tileset.FLOOR) {
                worldFrame[i][j - 1] = Tileset.WALL;
            }
            while (j < y + height && j < worldHeight) {
                worldFrame[i][j] = Tileset.FLOOR;
                if (i == x && worldFrame[i - 1][j] != Tileset.FLOOR) {
                    worldFrame[i - 1][j] = Tileset.WALL;
                }
                if (i == x + width - 1 && worldFrame[i + 1][j] != Tileset.FLOOR) {
                    worldFrame[i + 1][j] = Tileset.WALL;
                }
                j++;
            }
            if (worldFrame[i][j] != Tileset.FLOOR) {
                worldFrame[i][j] = Tileset.WALL;
            }
            j = y;
            i++;
        }

        if (worldFrame[x - 1][y - 1] != Tileset.FLOOR) {
            worldFrame[x - 1][y - 1] = Tileset.WALL;
        }
        if (worldFrame[x + width][y - 1] != Tileset.FLOOR) {
            worldFrame[x + width][y - 1] = Tileset.WALL;
        }
        if (worldFrame[x - 1][y + height] != Tileset.FLOOR) {
            worldFrame[x - 1][y + height] = Tileset.WALL;
        }
        if (worldFrame[x + width][y + height] != Tileset.FLOOR) {
            worldFrame[x + width][y + height] = Tileset.WALL;
        }
    }


    public TETile[][] createLeaves() {
        int maxSize = 12;
        List<Leaf> leaves = new ArrayList<>();
        Leaf singleLeaf = new Leaf(0, 0, worldWidth, worldHeight);
        leaves.add(singleLeaf);
        boolean doSplit = true;

        while (doSplit) {
            doSplit = false;
            for (int i = 0; i < leaves.size(); i++) {
                Leaf l = leaves.get(i);
                if (l.left == null && l.right == null) {
                    if (l.width > maxSize || l.height > maxSize) {
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
        return worldFrame;
    }

    private void clearGrid(TETile[][] world) {
        for (int i = 0; i < worldWidth; i++) {
            for (int j = 0; j < worldHeight; j++) {
                world[i][j] = Tileset.NOTHING;
            }
        }
    }

    public void connect(Room front, Room back) {

        int frontX = front.left + 1 + r.nextInt(Math.max(1, front.right - front.left - 3)),
                frontY = front.bot + 1 + r.nextInt(Math.max(1, front.top - front.bot - 3)),
                backX = back.left + 1 + r.nextInt(Math.max(1, back.right - back.left - 3)),
                backY = back.bot + 1 + r.nextInt(Math.max(1, back.top - back.bot - 3)),
                width = backX - frontX,
                height = backY - frontY;

        if (width < 0) {
            if (height < 0) {
                if (r.nextBoolean()) {
                    paintRoom(new Room(backX, frontY, Math.abs(width) + 1, 1));
                    paintRoom(new Room(backX, backY, 1, Math.abs(height) + 1));
                } else {
                    paintRoom(new Room(backX, backY, Math.abs(width) + 1, 1));
                    paintRoom(new Room(frontX, backY, 1, Math.abs(height) + 1));
                }
            } else if (height > 0) {
                if (r.nextBoolean()) {
                    paintRoom(new Room(backX, frontY, Math.abs(width) + 1, 1));
                    paintRoom(new Room(backX, frontY, 1, Math.abs(height) + 1));
                } else {
                    paintRoom(new Room(frontX, frontY, 1, Math.abs(height) + 1));
                    paintRoom(new Room(backX, backY, Math.abs(width) + 1, 1));
                }
            } else {
                paintRoom(new Room(backX, backY, Math.abs(width) + 1, 1));
            }
        } else if (width > 0) {
            if (height < 0) {
                if (r.nextBoolean()) {
                    paintRoom(new Room(frontX, backY, Math.abs(width) + 1, 1));
                    paintRoom(new Room(frontX, backY, 1, Math.abs(height) + 1));
                } else {
                    paintRoom(new Room(frontX, frontY, Math.abs(width) + 1, 1));
                    paintRoom(new Room(backX, backY, 1, Math.abs(height) + 1));
                }
            } else if (height > 0) {
                if (r.nextBoolean()) {
                    paintRoom(new Room(frontX, frontY, Math.abs(width) + 1, 1));
                    paintRoom(new Room(backX, frontY, 1, Math.abs(height) + 1));
                } else {
                    paintRoom(new Room(frontX, backY, Math.abs(width) + 1, 1));
                    paintRoom(new Room(frontX, frontY, 1, Math.abs(height) + 1));
                }
            } else {
                paintRoom(new Room(frontX, frontY, Math.abs(width) + 1, 1));
            }
        } else {
            if (height < 0) {
                paintRoom(new Room(backX, backY, 1, Math.abs(height) + 1));
            } else {
                paintRoom(new Room(frontX, frontY, 1, Math.abs(height) + 1));
            }
        }
    }

}
