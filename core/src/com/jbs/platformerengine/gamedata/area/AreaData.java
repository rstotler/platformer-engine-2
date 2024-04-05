package com.jbs.platformerengine.gamedata.area;

import java.util.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.jbs.platformerengine.gamedata.Rect;
import com.jbs.platformerengine.screen.gamescreen.ScreenChunk;

public class AreaData {
    public String levelName;
    public Rect size;

    public void loadArea(ScreenChunk[][] screenChunks, SpriteBatch spriteBatch) {}

    public void drawTree(SpriteBatch spriteBatch, FrameBuffer targetFrameBuffer, int xLoc, int yLoc, boolean isThin, boolean isDark) {
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

    public void createDirtPlatform(ScreenChunk[][] screenChunks, int xLoc, int yLoc, int width, int height) {
        int chunkX = xLoc / screenChunks[0][0].tiles.length;
        int chunkY = yLoc / screenChunks[0][0].tiles[0].length;
        int startX = xLoc % screenChunks[0][0].tiles.length;
        int startY = yLoc % screenChunks[0][0].tiles[0].length;

        if(chunkX < screenChunks.length && chunkY < screenChunks[0].length) {
            for(int y = 0; y < height; y++) {
                for(int x = 0; x < width; x++) {
                    if(startX + x < screenChunks[0][0].tiles.length && startY + y < screenChunks[0][0].tiles[0].length) {
                        //screenChunks[chunkX][chunkY].tiles[startX + x][startY + y] = new Tile("Square", "Dirt-Platform-Top", 2);
                    }
                }
            }
        }
    }
}
