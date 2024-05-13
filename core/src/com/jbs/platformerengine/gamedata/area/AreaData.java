package com.jbs.platformerengine.gamedata.area;

import java.util.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.jbs.platformerengine.gamedata.Point;
import com.jbs.platformerengine.gamedata.Rect;
import com.jbs.platformerengine.gamedata.entity.BreakableObject;
import com.jbs.platformerengine.gamedata.entity.Mob;
import com.jbs.platformerengine.gamedata.entity.player.Player;
import com.jbs.platformerengine.screen.ImageManager;
import com.jbs.platformerengine.screen.gamescreen.CellCollidables;
import com.jbs.platformerengine.screen.gamescreen.GameScreen;
import com.jbs.platformerengine.screen.gamescreen.ScreenChunk;
import com.jbs.platformerengine.screen.gamescreen.Tile;

public class AreaData {
    public String levelName;
    public Rect size;
    public boolean initCheck = false;

    public ScreenChunk[][] screenChunks;
    public ArrayList<FrameBuffer> frameBufferBackground;

    public String defaultTileSet;
    public String defaultTileName;
    public int defaultTileNum;

    public ArrayList<String> tileSetList = new ArrayList<>();
    public ArrayList<String> animatedImageList = new ArrayList<>();

    public int areaTimer = 0;

    public boolean outside;
    public float nightTimer;
    public float nightTimerMax;

    public void loadScreenChunks() {
        screenChunks = new ScreenChunk[size.width][size.height];
        for(int y = 0; y < screenChunks[0].length; y++) {
            for(int x = 0; x < screenChunks.length; x++) {
                screenChunks[x][y] = new ScreenChunk(x, y);
            }
        }
    }

    public void loadArea(SpriteBatch spriteBatch, ImageManager imageManager) {}

    public void createFloor(String tileSetName, boolean createHills) {

        // Base Floor //
        int baseFloorThickness = 7;
        for(int chunkX = 0; chunkX < screenChunks.length; chunkX++) {
            for(int y = 0; y < baseFloorThickness; y++) {
                for(int x = 0; x < screenChunks[0][0].tiles.length; x++) {
                    int textureNum = 2;
                    if(y == 6) {
                        textureNum = 1;
                    }
                    screenChunks[chunkX][0].tiles[x][y] = new Tile(tileSetName, "Square", textureNum);
                }
            }
        }

        // Hills //
        if(createHills) {
            int hillCount = new Random().nextInt(20) + 10;
            int tileIndex = 15;
            for(int hillIndex = 0; hillIndex < hillCount; hillIndex++) {
                int hillWidth = new Random().nextInt(20) + 12;
                int yCount = 1;
                if(new Random().nextInt(2) == 0) {
                    yCount = 2;
                }
    
                for(int y = 0; y < yCount; y++) {
                    if(y == 1) {
                        int xOffset = new Random().nextInt(4) + 2;
                        tileIndex += xOffset;
                        hillWidth -= (new Random().nextInt(2) + 2) + xOffset;
                    }
    
                    String slopeLeftSide = "Ramp";
                    String slopeRightSide = "Ramp";
                    if(new Random().nextInt(2) == 0) {
                        slopeLeftSide = "Ramp-Half";
                    }
                    if(new Random().nextInt(2) == 0) {
                        slopeRightSide = "Ramp-Half";
                    }
                    
                    for(int x = tileIndex; x < tileIndex + hillWidth; x++) {
                        int chunkX = x / screenChunks[0][0].tiles.length;
                        int tileX = x % screenChunks[0][0].tiles.length;
                        if(chunkX < screenChunks.length && tileX < screenChunks[0][0].tiles.length) {
                            String tileType = "Square";
                            int tileNum = 1;
                            if(x == tileIndex) {
                                if(slopeLeftSide.equals("Ramp")) {
                                    tileType = "Ramp";
                                } else {
                                    tileType = "Ramp-Bottom";
                                }
                            } else if(x == tileIndex + 1 && slopeLeftSide.equals("Ramp-Half")) {
                                tileType = "Ramp-Top";
                            } else if(x == tileIndex + hillWidth - 1) {
                                if(slopeRightSide.equals("Ramp")) {
                                    tileType = "Ramp";
                                    tileNum = 2;
                                } else {
                                    tileType = "Ramp-Bottom";
                                    tileNum = 2;
                                }
                            } else if(x == tileIndex + hillWidth - 2 && slopeRightSide.equals("Ramp-Half")) {
                                tileType = "Ramp-Top";
                                tileNum = 2;
                            }
                            
                            screenChunks[chunkX][0].tiles[tileX][baseFloorThickness - 1 + y] = new Tile("Dirt-Floor", "Square", 2);
                            screenChunks[chunkX][0].tiles[tileX][baseFloorThickness + y] = new Tile("Dirt-Floor", tileType, tileNum);
                        }
                    }
                }
                
                tileIndex += hillWidth + new Random().nextInt(30) + 7;
            }
        }
    }

    public void createTree(SpriteBatch spriteBatch, FrameBuffer targetFrameBuffer, int xLoc, int yLoc, boolean isThin, boolean isDark) {
        HashMap<String, ArrayList<Texture>> imageMap = new HashMap<String, ArrayList<Texture>>();

        // Load Tiles //
        for(FileHandle directoryHandle : Gdx.files.internal("assets/images/modular/Tree").list()) {
            String tileName = directoryHandle.toString().substring(directoryHandle.toString().lastIndexOf("/") + 1, directoryHandle.toString().lastIndexOf("_"));
            if(!imageMap.containsKey(tileName)) {
                imageMap.put(tileName, new ArrayList<Texture>());
            }

            String filePath = directoryHandle.toString().substring(directoryHandle.toString().indexOf("/") + 1);
            Texture tileTexture = new Texture(filePath);
            imageMap.get(tileName).add(tileTexture);
        }

        FrameBuffer frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        frameBuffer.begin();
        spriteBatch.begin();

        // Tree Trunk //
        boolean trunkHoleCreated = false;
        boolean trunkMossCreated = false;
        boolean noLeavesCheck = false;
        int trunkHeight = new Random().nextInt(7) + 9;

        String isDarkAffix = "";
        if(isDark) {
            isDarkAffix = "Dark-";
        }
        String sizeAffix = "Thick-";
        if(isThin) {
            trunkHeight -= 3;
            sizeAffix = "Thin-";
            if(!isDark && new Random().nextInt(2) == 0) {
                noLeavesCheck = true;
                trunkHeight -= 3;
            }
        }

        int leavesWidth = (new Random().nextInt(3) * 2) + 7;
        int leavesHeight = (new Random().nextInt(3) * 2) + 5;
        int centerXCord = xLoc;
        int centerYCord = yLoc + (16 * (trunkHeight + ((leavesHeight - 1) / 2) - 1));

        int maxTrimSize = (leavesWidth / 2) - 2;
        int trimSizeLeft = new Random().nextInt(maxTrimSize);
        int trimSizeRight = new Random().nextInt(maxTrimSize);

        // Draw Background (Bottom Layer) Leaves //
        if(!noLeavesCheck) {
            int bottomLayerWidth = leavesWidth - trimSizeLeft - trimSizeRight - 2;
            for(int x = 0; x < bottomLayerWidth; x++) {
                int backgroundX = centerXCord - (((leavesWidth / 2) - trimSizeLeft) * 16) + (x * 16) + 16;
                int backgroundY = centerYCord - ((leavesHeight / 2) * 16) - 10;
                int tileIndex = 1;
                if(x == 0) {
                    tileIndex = 0;
                } else if(x == leavesWidth - 3) {
                    tileIndex = 2;
                }
                
                spriteBatch.draw(imageMap.get("Leaves-Bottom").get(tileIndex), backgroundX, backgroundY);
            }
        }

        // Draw Tree Base & Trunk //
        for(int y = 0; y < trunkHeight; y++) {
            
            // Trunk Base //
            if(y == 0) {

                // Triple Trunk Base //
                if((!isDark && new Random().nextInt(2) == 0)
                || new Random().nextInt(4) == 0) {
                    for(int x = 0; x < 3; x++) {
                        if(x == 0) {
                            spriteBatch.draw(imageMap.get(isDarkAffix + sizeAffix + "Base").get(0), xLoc - 8 - 16, yLoc);
                        } else if(x == 1) {
                            if(isDark || isThin || new Random().nextInt(2) == 0) {
                                spriteBatch.draw(imageMap.get(isDarkAffix + sizeAffix + "Base").get(1), xLoc - 8, yLoc);
                            } else {
                                spriteBatch.draw(imageMap.get(isDarkAffix + sizeAffix + "Base").get(2), xLoc - 8, yLoc);
                            }
                        } else {
                            int indexMod = 0;
                            if(isDark || isThin) {
                                indexMod = -1;
                            }

                            if(isThin || new Random().nextInt(2) == 0) {
                                spriteBatch.draw(imageMap.get(isDarkAffix + sizeAffix + "Base").get(3 + indexMod), xLoc + 8, yLoc);
                            } else {
                                spriteBatch.draw(imageMap.get(isDarkAffix + sizeAffix + "Base").get(4 + indexMod), xLoc + 8, yLoc);
                            }
                        }
                    }
                }
                
                // Single Trunk Base //
                else {
                    spriteBatch.draw(imageMap.get(isDarkAffix + sizeAffix + "Trunk").get(0), xLoc - 8, yLoc);
                }
            }

            // Main Trunk //
            else {
                int randomTrunkIndex = 0;
                int maxRandom = 3;
                if(isThin) {
                    maxRandom = 2;
                }

                if(trunkHoleCreated || y == 1 || y == trunkHeight - 1) {
                    randomTrunkIndex = new Random().nextInt(maxRandom);
                } else {
                    int holeCheck = 0;
                    if(new Random().nextInt(10) == 0) {
                        holeCheck = 1;
                    }
                    randomTrunkIndex = new Random().nextInt(maxRandom + holeCheck);
                }

                // Moss Check //
                if(!isThin && isDarkAffix.equals("") && !trunkMossCreated && new Random().nextInt(10) == 0) {
                    randomTrunkIndex = 4;
                    trunkMossCreated = true;
                }

                // Branch Check //
                if(isThin && y > 1 && new Random().nextInt(5) == 0) {
                    randomTrunkIndex = new Random().nextInt(3) + 3;
                }
                
                spriteBatch.draw(imageMap.get(isDarkAffix + sizeAffix + "Trunk").get(randomTrunkIndex), xLoc - 8, yLoc + (y * 16));

                // Draw Branches //
                if(isThin && randomTrunkIndex >= 3) {
                    if(randomTrunkIndex == 3 || randomTrunkIndex == 5) {
                        spriteBatch.draw(imageMap.get(isDarkAffix + "Thin-Branch").get(0), xLoc - 8 + 16, yLoc + (y * 16));
                    }
                    if(randomTrunkIndex == 4 || randomTrunkIndex == 5) {
                        spriteBatch.draw(imageMap.get(isDarkAffix + "Thin-Branch").get(randomTrunkIndex - 3), xLoc - 8 - 16, yLoc + (y * 16));
                    }
                }

                if((!isThin && randomTrunkIndex == 3) || (isThin && randomTrunkIndex == 2)) {
                    trunkHoleCreated = true;
                }
            }

            // Top Trunk (No Leaves) //
            if(noLeavesCheck) {
                spriteBatch.draw(imageMap.get("Thin-Top-Trunk").get(0), xLoc - 8, yLoc + (y * 16) + 16);
                spriteBatch.draw(imageMap.get("Thin-Top-Trunk").get(1), xLoc - 8, yLoc + (y * 16) + 32);
                spriteBatch.draw(imageMap.get("Thin-Top-Trunk").get(2), xLoc - 8, yLoc + (y * 16) + 48);
                spriteBatch.draw(imageMap.get("Thin-Top-Trunk").get(3), xLoc - 8, yLoc + (y * 16) + 64);
            }
        }

        // Leaves //
        if(!noLeavesCheck) {

            // Top Leaves //
            boolean[][] treeMap = new boolean[leavesWidth][leavesHeight];
            for(int leavesYIndex = 0; leavesYIndex < leavesHeight; leavesYIndex++) {
                for(int leavesXIndex = 0; leavesXIndex < leavesWidth; leavesXIndex++) {
                    treeMap[leavesXIndex][leavesYIndex] = true;
                }
            }
            
            // Trim TreeMap //
            int trimLeftCount = 0;
            int trimRightCount = 0;
            for(int y = 0 ; y < leavesHeight; y++) {
                for(int side = 0; side < 2; side++) {
                    int trimSize = trimSizeLeft;
                    if(side == 1) {
                        trimSize = trimSizeRight;
                    }
                    if(trimSize > 0) {
                        for(int x = 0; x < trimSize; x++) {
                            int xMod = 0;
                            if(side == 1) {
                                xMod = leavesWidth - 1 - x;
                            }
                            treeMap[x + xMod][y] = false;
                        }
                    }
                    int trimSizeMod = new Random().nextInt(3) - 1;
                    if(y == leavesHeight - 2 && trimSizeMod == -1) {
                        trimSizeMod = 1;
                    }
                    if(trimSize + trimSizeMod >= 0 && trimSize + trimSizeMod <= maxTrimSize) {
                        if(side == 0 && trimLeftCount >= 2) {
                            trimSizeLeft += trimSizeMod;
                            trimLeftCount = 0;
                        } else if(side == 1 && trimRightCount >= 2) {
                            trimSizeRight += trimSizeMod;
                            trimRightCount = 0;
                        }
                    }
                }
                trimLeftCount++;
                trimRightCount++;
            }

            for(int y = 0; y < treeMap[0].length; y++) {
                for(int x = 0; x < treeMap.length; x++) {
                    if(treeMap[x][y]) {
                        String tileType = "Leaves";
                        String tileSection = "MiddleA";
                        int tileIndex = 1;
                        if(x == 0 || !treeMap[x - 1][y]) {
                            tileIndex = 0;
                        } else if(x == treeMap.length - 1 || !treeMap[x + 1][y]) {
                            tileIndex = 2;
                        }

                        if(y == 0) {
                            tileSection = "Bottom";
                        } else if(y == leavesHeight - 1) {
                            tileSection = "Top";
                        } else if(y == 1) {
                            tileSection = "MiddleB";
                        }

                        if(y > 0 && (x == 0 || treeMap[x - 1][y] == false) && treeMap[x][y - 1] == false) {
                            tileSection = "Bottom-Corner";
                            tileIndex = 0;
                        } else if(y > 0 && (x == leavesWidth - 1 || treeMap[x + 1][y] == false) && treeMap[x][y - 1] == false) {
                            tileSection = "Bottom-Corner";
                            tileIndex = 1;
                        } else if(y < leavesHeight - 1 && (x == 0 || treeMap[x - 1][y] == false) && treeMap[x][y + 1] == false) {
                            tileSection = "Top-Corner";
                            tileIndex = 0;
                        } else if(y < leavesHeight - 1 && (x == leavesWidth - 1 || treeMap[x + 1][y] == false) && treeMap[x][y + 1] == false) {
                            tileSection = "Top-Corner";
                            tileIndex = 1;
                        }
                        
                        int leavesXCord = centerXCord - (leavesWidth / 2) + x;
                        int leavesYCord = centerYCord - (leavesHeight / 2) + y;
                        int leavesX = centerXCord + ((leavesXCord - centerXCord) * 16);
                        int leavesY = centerYCord + ((leavesYCord - centerYCord) * 16);
                        spriteBatch.draw(imageMap.get(tileType + "-" + tileSection).get(tileIndex), leavesX, leavesY);
                    }
                }
            }
        }
        
        spriteBatch.end();
        frameBuffer.end();

        targetFrameBuffer.begin();
        spriteBatch.begin();
        spriteBatch.draw(frameBuffer.getColorBufferTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1, 1);
        spriteBatch.end();
        targetFrameBuffer.end();

        // Dispose Textures //
        for(String key : imageMap.keySet()) {
            for(Texture texture : imageMap.get(key)) {
                texture.dispose();
            }
        }
    }

    public void createPlatform(String tileSetName, int xLoc, int yLoc, int width, int height) {
        int chunkX = xLoc / screenChunks[0][0].tiles.length;
        int chunkY = yLoc / screenChunks[0][0].tiles[0].length;

        if(chunkX < screenChunks.length && chunkY < screenChunks[0].length
        && (xLoc % screenChunks[0][0].tiles.length) + width < screenChunks[0][0].tiles.length) {

            // Create Platform Map //
            boolean[][] platformMap = new boolean[width][height];
            for(int yIndex = 0; yIndex < height; yIndex++) {
                for(int xIndex = 0; xIndex < width; xIndex++) {
                    platformMap[xIndex][yIndex] = false;
                }
            }

            int trimSize = height - 2;
            int xWidth = (width / 2) + (width % 2);
            for(int x = 0; x < xWidth; x++) {
                for(int y = 0; y < height; y++) {
                    if(y >= trimSize || (x >= ((width - 2) / 2) && x <= ((width - 1) / 2) + 1)) {
                        platformMap[x][y] = true;
                        platformMap[width - 1 - x][y] = true;
                    }
                }

                // Update Trim //
                trimSize -= ((width - 2) / 2) / (height - trimSize);
            }

            // Set Platform Tile Type //
            int startX = xLoc % screenChunks[0][0].tiles.length;
            int startY = yLoc % screenChunks[0][0].tiles[0].length;

            for(int y = 0; y < height; y++) {
                for(int x = 0; x < width; x++) {
                    if(startX + x >= 0 && startX + x < screenChunks[0][0].tiles.length && startY + y >= 0 && startY + y < screenChunks[0][0].tiles[0].length) {
                        String tileName = "Middle";
                        if(y == 0) {
                            tileName = "Bottom";
                        } else if(y == height - 1) {
                            tileName = "Top";
                        }

                        int tileNum = 2;
                        if(x == 0 || platformMap[x - 1][y] == false) {
                            tileNum = 1;
                        } else if(x == width - 1 || platformMap[x + 1][y] == false) {
                            tileNum = 3;
                        }

                        if(x == 0 || platformMap[x - 1][y] == false) {
                            if(y == 0 || platformMap[x][y - 1] == false) {
                                tileName = "Bottom";
                                tileNum = 1;
                            }
                        } else if(x == width - 1 || platformMap[x + 1][y] == false) {
                            if(y == 0 || platformMap[x][y - 1] == false) {
                                tileName = "Bottom";
                                tileNum = 3;
                            }
                        }
                        if(y == height - 1 && platformMap[x][y - 1] == false) {
                            tileName = "Horizontal";
                            if(x == 0 || platformMap[x - 1][y] == false) {
                                tileNum = 1;
                            } else if(x == width - 1 || platformMap[x + 1][y] == false) {
                                tileNum = 3;
                            } else {
                                tileNum = 2;
                            }
                        }

                        if(y < height - 1 && x > 0 && x < width - 1 && platformMap[x - 1][y] == true && platformMap[x + 1][y] == true) {
                            if(y == 0 || platformMap[x][y - 1] == false) {
                                tileName = "Bottom";
                                tileNum = 2;
                            }
                        }

                        if(platformMap[x][y] == true) {
                            screenChunks[chunkX][chunkY].tiles[startX + x][startY + y] = new Tile(tileSetName, tileName, tileNum);
                        }
                    }
                }
            }
        }
    }

    public void createBridge(SpriteBatch spriteBatch, ImageManager imageManager, int xLoc, int yLoc, int width, int pillarWidth, int pillarSpace, boolean initCheck) {
        ArrayList<Texture> archTexture = new ArrayList<>();
        archTexture.add(new Texture("images/objects/Stone-Arch_01.png"));
        archTexture.add(new Texture("images/objects/Stone-Arch_02.png"));
        archTexture.add(new Texture("images/objects/Stone-Arch_03.png"));
        archTexture.add(new Texture("images/objects/Stone-Arch_04.png"));
        archTexture.add(new Texture("images/objects/Stone-Arch_05.png"));
        Texture stoneBandEdgeTexture = new Texture("images/objects/Stone-Band_01.png");
        Texture stoneBandTexture = new Texture("images/objects/Stone-Band_02.png");

        if(pillarSpace < 11) {
            pillarSpace = 11;
        }
        
        // Bridge //
        for(int x = 0; x < width; x++) {
            int chunkX = (xLoc + x) / screenChunks[0][0].tiles.length;
            int chunkY = yLoc / screenChunks[0][0].tiles[0].length;
            int tileX = (xLoc + x) % screenChunks[0][0].tiles.length;

            screenChunks[chunkX][chunkY].frameBufferWalls.begin();
            spriteBatch.begin();

            // Top Walkable Platform //
            if(!initCheck) {
                screenChunks[chunkX][chunkY].tiles[tileX][yLoc] = new Tile("Stone", "Square-Half");
            }
            
            // Rooftop //
            if(!initCheck && x >= 2) {
                int rooftopYLoc = yLoc + 9;
                int rooftopChunkY = rooftopYLoc / screenChunks[0][0].tiles[0].length;
                int rooftopTileY = rooftopYLoc % screenChunks[0][0].tiles[0].length;
                screenChunks[chunkX][rooftopChunkY].tiles[tileX][rooftopTileY] = new Tile("Stone", "Square-Half");
            }

            // Area Below Top Walkable Platform //
            for(int y = 0; y < 4; y++) {
                if(y == 1 || (y < 3 && x > 0) || x > 1) {
                    int tileNum = 0;
                    if((y == 0 && x == 0) || y == 1 && x == 0 || (y > 1 && y < 3 && x == 1) || (y == 3 && x == 2)) {
                        tileNum = 1;
                    }
                    spriteBatch.draw(imageManager.tile.get("Stone").get("Square").get(tileNum), tileX * 16, (yLoc - 1 - y) * 16);
                }
            }

            // Area Below Top Walkable Platform (Bottom Border) //
            if(x > 1) {
                spriteBatch.draw(imageManager.tile.get("Stone").get("Square-Half").get(1), tileX * 16, (yLoc - 4) * 16);
            }

            // Bottom Area (Before Pillars) //
            int tileNum = 10;
            for(int y = 0; y < 2; y++) {
                if((y == 0 && x > 2) || (y == 1 && x > 4)) {
                    if(y == 0 && x == 3) {
                        tileNum = 8;
                    } else if(y == 1 && x == 5) {
                        tileNum = 12;
                    }
                    spriteBatch.draw(imageManager.tile.get("Stone").get("Square").get(tileNum), tileX * 16, (yLoc - 5 - y) * 16);
                }
            }

            // Pillars //
            if((x + pillarWidth) % (pillarWidth + pillarSpace) < pillarWidth) {
                for(int y = 0; y < yLoc - 6; y++) {
                    if(x == (pillarSpace) && y > 2) {
                        tileNum = 8;
                    }
                    spriteBatch.draw(imageManager.tile.get("Stone").get("Square").get(tileNum), tileX * 16, (yLoc - 7 - y) * 16);
                }
            }

            // Side Of Pillars (Arch) //
            String tileName = "Square";
            if((x > pillarWidth && (x + pillarWidth) % (pillarWidth + pillarSpace) == pillarWidth)
            || (x > pillarWidth && (x + pillarWidth) % (pillarWidth + pillarSpace) == pillarWidth + pillarSpace - 1)) {
                for(int y = 0; y < 3; y++) {
                    if(y == 2 && (x > pillarWidth && (x + pillarWidth) % (pillarWidth + pillarSpace) == pillarWidth + pillarSpace - 1)) {
                        tileNum = 8;
                        if(x > pillarSpace) {
                            tileName = "Corner";
                            tileNum = 2;
                        }
                    } else if(y == 2 && (x > pillarWidth && (x + pillarWidth) % (pillarWidth + pillarSpace) == pillarWidth)) {
                        tileNum = 4;
                        if(x > pillarSpace) {
                            tileName = "Corner";
                            tileNum = 1;
                        }
                    }
                    if(y == 2 && x <= pillarSpace) {
                        tileName = "Square";
                        tileNum = 12;
                    }
                    
                    spriteBatch.draw(imageManager.tile.get("Stone").get(tileName).get(tileNum), tileX * 16, (yLoc - 7 - y) * 16);
                }
            }
            else if((x > pillarWidth && (x + pillarWidth) % (pillarWidth + pillarSpace) == pillarWidth + 1)
            || (x > pillarWidth && (x + pillarWidth) % (pillarWidth + pillarSpace) == pillarWidth + pillarSpace - 2)) {
                for(int y = 0; y < 2; y++) {
                    if(y == 1 && (x > pillarWidth && (x + pillarWidth) % (pillarWidth + pillarSpace) == pillarWidth + pillarSpace - 2)) {
                        tileNum = 8;
                        if(x > pillarSpace) {
                            tileName = "Corner";
                            tileNum = 2;
                        }
                    } else if(y == 1 && (x > pillarWidth && (x + pillarWidth) % (pillarWidth + pillarSpace) == pillarWidth + 1)) {
                        tileNum = 4;
                        if(x > pillarSpace) {
                            tileName = "Corner";
                            tileNum = 1;
                        }
                    }
                    if(y == 1 && x <= pillarSpace) {
                        tileName = "Square";
                        tileNum = 12;
                    }

                    spriteBatch.draw(imageManager.tile.get("Stone").get(tileName).get(tileNum), tileX * 16, (yLoc - 7 - y) * 16);
                }
            }
            else if((x > pillarWidth && (x + pillarWidth) % (pillarWidth + pillarSpace) == pillarWidth + 2)
            || (x > pillarWidth && (x + pillarWidth) % (pillarWidth + pillarSpace) == pillarWidth + pillarSpace - 3)) {
                for(int y = 0; y < 1; y++) {
                    if(x > pillarWidth && (x + pillarWidth) % (pillarWidth + pillarSpace) == pillarWidth + pillarSpace - 3) {
                        tileNum = 8;
                        if(x > pillarSpace) {
                            tileName = "Corner";
                            tileNum = 2;
                        }
                    } else {
                        tileNum = 4;
                        if(x > pillarSpace) {
                            tileName = "Corner";
                            tileNum = 1;
                        }
                    }
                    if(x <= pillarSpace) {
                        tileName = "Square";
                        tileNum = 12;
                    }

                    spriteBatch.draw(imageManager.tile.get("Stone").get(tileName).get(tileNum), tileX * 16, (yLoc - 7 - y) * 16);
                }
            }

            spriteBatch.end();
            screenChunks[chunkX][chunkY].frameBufferWalls.end();

            screenChunks[chunkX][chunkY].frameBufferForeground.begin();
            spriteBatch.begin();

            // Top Wall //
            int topWallIndent = 3;
            boolean drawTopWallBackground = false;

            if(x >= topWallIndent) {
                tileNum = 8;
                if(x > topWallIndent) {
                    tileNum = 11;
                }
                spriteBatch.draw(imageManager.tile.get("Stone").get("Square").get(tileNum), tileX * 16, ((yLoc + 6) * 16) - 4);
            }

            for(int y = 0; y < 6; y++) {
                if(x % 7 == topWallIndent) {
                    tileName = "Vertical-Middle";
                    if(x == topWallIndent) {
                        tileName = "Vertical-Left";
                    }

                    tileNum = 0;
                    if(y == 5) {
                        tileNum = 2;
                    } else if(y != 0) {
                        tileNum = 1;
                    }

                    spriteBatch.draw(imageManager.tile.get("Wood").get(tileName).get(tileNum), tileX * 16, (yLoc + y) * 16);
                    drawTopWallBackground = true;
                }
                else if(x > (topWallIndent - 1) && y == 5) {
                    spriteBatch.draw(imageManager.tile.get("Wood").get("Horizontal").get(0), tileX * 16, (yLoc + y) * 16);
                }
            }
            spriteBatch.draw(imageManager.tile.get("Stone").get("Square-Half").get(0), tileX * 16, yLoc * 16);

            spriteBatch.end();
            screenChunks[chunkX][chunkY].frameBufferForeground.end();

            // Top Wall Background (Tinted) //
            if(drawTopWallBackground) {
                Sprite sprite = new Sprite(imageManager.tile.get("Wood").get("Vertical-Middle").get(1));
                sprite.setColor(0, 0, 1, 1);
                for(int y = 0; y < 6; y++) {
                    screenChunks[chunkX][chunkY].frameBufferWalls.begin();
                    spriteBatch.begin();
                    spriteBatch.setColor(100/255f, 100/255f, 100/255f, 1);
                    spriteBatch.draw(sprite, (tileX + 1) * 16, (yLoc + y) * 16);
                    spriteBatch.setColor(1, 1, 1, 1);
                    spriteBatch.end();
                    screenChunks[chunkX][chunkY].frameBufferWalls.end();
                }
            }
        }

        // Rooftop //
        Texture rooftopTextureLeft = new Texture("images/objects/Rooftop_01.png");
        Texture rooftopTextureMiddle = new Texture("images/objects/Rooftop_02.png");
        
        int rooftopXMod = 32;
        int rooftopYMod = 104;
        int previousChunkX = -1;
        int rooftopCount = (((width * 16) - rooftopXMod - 16) / rooftopTextureMiddle.getWidth()) + 2;
        for(int rooftopIndex = 0; rooftopIndex < rooftopCount; rooftopIndex++) {
            int rooftopX = -1;
            Texture rooftopTexture = null;
            if(rooftopIndex == 0) {
                rooftopX = (xLoc * 16) + rooftopXMod;
                rooftopTexture = rooftopTextureLeft;
            } else {
                rooftopX = (xLoc * 16) + rooftopXMod + 16 + (rooftopTextureMiddle.getWidth() * (rooftopIndex - 1));
                rooftopTexture = rooftopTextureMiddle;
            }

            int rooftopY = (yLoc * 16) + rooftopYMod;
            int chunkX = rooftopX / Gdx.graphics.getWidth();
            int chunkY = rooftopY / Gdx.graphics.getHeight();
            int rooftopDrawX = rooftopX % Gdx.graphics.getWidth();
            int rooftopDrawY = rooftopY % Gdx.graphics.getHeight();

            if(chunkX < screenChunks.length && chunkY < screenChunks[0].length) {
                screenChunks[chunkX][chunkY].frameBufferForeground.begin();
                spriteBatch.begin();
                if(previousChunkX != -1 && previousChunkX != chunkX) {
                    spriteBatch.draw(rooftopTexture, rooftopDrawX - rooftopTextureMiddle.getWidth(), rooftopDrawY);
                }
                spriteBatch.draw(rooftopTexture, rooftopDrawX, rooftopDrawY);
                spriteBatch.end();
                screenChunks[chunkX][chunkY].frameBufferForeground.end();
            }

            if(previousChunkX == -1 || previousChunkX != chunkX) {
                previousChunkX = chunkX;
            }
        }
        
        // Stone Band (Above Pillars) //
        int bandCount = ((width * 16) / 96) + 1;
        for(int bandIndex = 0; bandIndex < bandCount; bandIndex++) {
            int bandX = (xLoc * 16) + (bandIndex * 96);
            int bandY = ((yLoc - 2) * 16) + 7;
            int bandDrawX = bandX % Gdx.graphics.getWidth();
            int bandDrawY = bandY % Gdx.graphics.getHeight();
            int chunkX = bandX / Gdx.graphics.getWidth();
            int chunkY = bandY / Gdx.graphics.getHeight();

            int bandDrawCount = 1;
            if(bandDrawX + 96 >= Gdx.graphics.getWidth()) {
                bandDrawCount = 2;
            }

            for(int i = 0; i < bandDrawCount; i++) {
                if(i == 1) {
                    chunkX += 1;
                    bandDrawX -= Gdx.graphics.getWidth();
                }

                if(chunkX < screenChunks.length && chunkY < screenChunks[0].length) {
                    screenChunks[chunkX][chunkY].frameBufferWalls.begin();
                    spriteBatch.begin();
    
                    if(bandIndex == 0) {
                        spriteBatch.draw(stoneBandEdgeTexture, bandDrawX, bandDrawY);
                    } else {
                        spriteBatch.draw(stoneBandTexture, bandDrawX, bandDrawY);
                    }
    
                    spriteBatch.end();
                    screenChunks[chunkX][chunkY].frameBufferWalls.end();
                }
            }
        }

        // Stone Arches //
        int archCount = (width / (pillarWidth + pillarSpace));
        for(int archIndex = 0; archIndex < archCount; archIndex++) {
            int archX = (((xLoc + ((pillarWidth + pillarSpace) * (archIndex + 1))) * 16) - 11);
            int archY = (((yLoc - 6) * 16) - 63);
            int archDrawX = archX % Gdx.graphics.getWidth();
            int rightArchDrawX = archDrawX + 96 + ((pillarSpace - 11) * 16);
            int archDrawY = archY % Gdx.graphics.getHeight();
            int chunkX = archX / Gdx.graphics.getWidth();
            int chunkY = archY / Gdx.graphics.getHeight();

            int archDrawCount = 1;
            if(archDrawX + 96 + 96 + ((pillarSpace - 11) * 16) >= Gdx.graphics.getWidth()) {
                archDrawCount = 2;
            }

            for(int i = 0; i < archDrawCount; i++) {
                if(i == 1) {
                    chunkX += 1;
                    archDrawX -= Gdx.graphics.getWidth();
                    rightArchDrawX -= Gdx.graphics.getWidth();
                }

                if(chunkX < screenChunks.length) {
                    screenChunks[chunkX][chunkY].frameBufferWalls.begin();
                    spriteBatch.begin();
        
                    // Top Arch //
                    spriteBatch.draw(archTexture.get(0), archDrawX, archDrawY);
                    spriteBatch.draw(archTexture.get(1), rightArchDrawX, archDrawY);

                    for(int x = 0; x < pillarSpace - 11; x++) {
                        spriteBatch.draw(archTexture.get(4), archDrawX + 96 + (x * 16), archDrawY);
                    }

                    if(pillarSpace > 11) {
                        spriteBatch.draw(archTexture.get(4), archDrawX + 96, archDrawY);
                        if(pillarSpace > 12) {
                            spriteBatch.draw(archTexture.get(4), archDrawX + 96 + 27, archDrawY);
                        }
                    }
        
                    // Side Walls //
                    int archSideCount = (((yLoc - 9) * 16) % Gdx.graphics.getHeight()) / 74;
                    for(int sideIndex = 1; sideIndex <= archSideCount; sideIndex++) {
                        spriteBatch.draw(archTexture.get(2), archDrawX, archDrawY - (74 * sideIndex));
                        spriteBatch.draw(archTexture.get(3), archDrawX + 96 + ((pillarSpace - 11) * 16), archDrawY - (74 * sideIndex));
                    }
        
                    spriteBatch.end();
                    screenChunks[chunkX][chunkY].frameBufferWalls.end();
                }
            }
        }

        // Torches (Breakable Object) //
        if(!initCheck) {
            for(int x = 0; x < archCount; x++) {
                int torchX = (xLoc * 16) + (((pillarWidth + pillarSpace) * 16) * (x + 1)) - (pillarWidth * 8) - 5;
                int torchYCount = 3;
                if(yLoc >= 25) {
                    torchYCount = 4;
                }
    
                int tileX = (torchX % Gdx.graphics.getWidth()) / 16;
                int tileY = 7;
                int chunkX = torchX / Gdx.graphics.getWidth();
                int chunkY = 0;
                int yMod = 0;
                for(int y = 2; y >= 0; y--) {
                    if(chunkX < screenChunks.length && chunkY < screenChunks[0].length) {
                        Tile targetTile = screenChunks[chunkX][chunkY].tiles[tileX][tileY + y];
                        if(targetTile != null) {
                            yMod = y + 1;
                            break;
                        }
                    }
                }
                
                for(int y = 0; y < torchYCount; y++) {
                    int torchY = 175 + (30 * y);
                    if(y == 3) {
                        torchY += ((yLoc - 23) * 16);
                    } else {
                        torchY += (yMod * 16);
                    }
                    
                    BreakableObject breakableObject = new BreakableObject("Torch_01", new Point(torchX, torchY), 3, imageManager);
                    GameScreen.addObjectToCellCollidables(screenChunks, breakableObject);
                }
            }
        }
        
        // Dispose Textures //
        for(Texture texture : archTexture) {
            texture.dispose();
        }
        stoneBandTexture.dispose();
        rooftopTextureLeft.dispose();
        rooftopTextureMiddle.dispose();
    }

    public void loadBackgroundFrameBuffers(SpriteBatch spriteBatch) {
        frameBufferBackground = new ArrayList<>();

        for(FileHandle directoryHandle : Gdx.files.internal("assets/images/backgrounds").list()) {
            String directoryName = directoryHandle.toString().substring(directoryHandle.toString().lastIndexOf("/") + 1);
            if(directoryName.equals(levelName)) {
                for(FileHandle fileHandle : Gdx.files.internal(directoryHandle.toString()).list()) {
                    int fileNameIndex = fileHandle.toString().lastIndexOf("/");
                    if(fileHandle.toString().substring(fileNameIndex + 1).length() >= 10 && fileHandle.toString().substring(fileNameIndex + 1, fileNameIndex + 11).equals("Background")) {
                        FrameBuffer frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
                    
                        frameBuffer.begin();
                        spriteBatch.begin();
    
                        Gdx.graphics.getGL20().glClearColor(0f, 0f, 0f, 0f);
                        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
    
                        Texture texture = new Texture(fileHandle.toString().substring(fileHandle.toString().indexOf("/") + 1));
                        spriteBatch.draw(texture, 0, 0);
                        
                        spriteBatch.end();
                        frameBuffer.end();
    
                        texture.dispose();
                        frameBufferBackground.add(frameBuffer);
                    }
                }

                break;
            }
        }
    }

    public void changeArea(SpriteBatch spriteBatch, ImageManager imageManager) {
        for(int y = 0; y < screenChunks[0].length; y++) {
            for(int x = 0; x < screenChunks.length; x++) {
                screenChunks[x][y].initChunk();
            }
        }

        // Create AddTileSetList & AddAnimationImageList //
        ArrayList<String> addTileSetList = new ArrayList<>();
        for(String tileSet : tileSetList) {
            if(!imageManager.tile.containsKey(tileSet)) {
                addTileSetList.add(tileSet);
            }
        }
        ArrayList<String> addAnimatedImageList = new ArrayList<>();
        for(String animatedImageName : animatedImageList) {
            if(!imageManager.animatedImage.containsKey(animatedImageName)) {
                addAnimatedImageList.add(animatedImageName);
            }
        }
        imageManager.loadImages(addTileSetList, addAnimatedImageList, outside);

        loadArea(spriteBatch, imageManager);
        loadBackgroundFrameBuffers(spriteBatch);

        for(int y = 0; y < screenChunks[0].length; y++) {
            for(int x = 0; x < screenChunks.length; x++) {
                screenChunks[x][y].bufferTiles(spriteBatch, imageManager);
            }
        }

        // Other Properties //
        if(outside) {
            nightTimer = 0;
        }
    }

    public void update(Player player) {
        areaTimer++;

        if(outside && nightTimer < nightTimerMax) {
            nightTimer += 1;
        }

        // Update Mobs //
        int chunkStartX = player.hitBoxArea.x / Gdx.graphics.getWidth() - 1;
        int chunkStartY = player.hitBoxArea.y / Gdx.graphics.getHeight() - 1;

        for(int y = chunkStartY; y < chunkStartY + 3; y++) {
            for(int x = chunkStartX; x < chunkStartX + 3; x++) {
                if(x >= 0 && y >= 0 && x < screenChunks.length && y < screenChunks[0].length) {
                    HashMap<Mob, ArrayList<CellCollidables>> updateMobScreenChunkMap = new HashMap<>();
                    for(Mob mob : screenChunks[x][y].mobList) {
                        if(mob.updateTimer != areaTimer) {
                            ArrayList<CellCollidables> oldCellCollidables = GameScreen.getObjectCellCollidables(screenChunks, mob);
                        
                            mob.updateAI(this);
                            mob.updateTileCollisions(screenChunks);
                            mob.updateTimer = areaTimer;
    
                            // Update Mob Cell Collidables //
                            ArrayList<CellCollidables> newCellCollidables = GameScreen.getObjectCellCollidables(screenChunks, mob);
                            if(!oldCellCollidables.equals(newCellCollidables)) {
                                ArrayList<CellCollidables> removeFromScreenChunkList = GameScreen.updateObjectCellCollidables(screenChunks, mob, oldCellCollidables, newCellCollidables);
                                if(removeFromScreenChunkList.size() > 0) {
                                    updateMobScreenChunkMap.put(mob, removeFromScreenChunkList);
                                }
                            }
                        }
                    }

                    for(Mob mob : updateMobScreenChunkMap.keySet()) {
                        if(screenChunks[x][y].mobList.contains(mob)) {
                            screenChunks[x][y].mobList.remove(mob);
                        }
                    }
                }
            }
        }
    }

    public void dispose() {
        for(int y = 0; y < screenChunks[0].length; y++) {
            for(int x = 0; x < screenChunks.length; x++) {
                screenChunks[x][y].dispose();
            }
        }

        for(FrameBuffer frameBuffer : frameBufferBackground) {
            frameBuffer.dispose();
        }
    }
}
