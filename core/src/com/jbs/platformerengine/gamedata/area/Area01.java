package com.jbs.platformerengine.gamedata.area;

import java.util.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jbs.platformerengine.gamedata.Rect;
import com.jbs.platformerengine.screen.gamescreen.ScreenChunk;
import com.jbs.platformerengine.screen.gamescreen.Tile;

public class Area01 extends AreaData {
    public Area01() {
        levelName = "Area01";
        size = new Rect(2, 2);
        tileSetList = new ArrayList<>(Arrays.asList("Dirt-Floor", "Dirt-Platform"));
    }

    public void loadArea(ScreenChunk[][] screenChunks, SpriteBatch spriteBatch) {

        // Bottom Floor //
        int baseFloorThickness = 7;
        for(int chunkX = 0; chunkX < 2; chunkX++) {
            for(int y = 0; y < baseFloorThickness; y++) {
                for(int x = 0; x < screenChunks[0][0].tiles.length; x++) {
                    int textureNum = 2;
                    if(y == 6) {
                        textureNum = 1;
                    }
                    screenChunks[chunkX][0].tiles[x][y] = new Tile("Dirt-Floor", "Square", textureNum);
                }
            }
        }

        // Hills //
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
                    hillWidth -= (new Random().nextInt(3) + 3) + xOffset;
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

        // Floating Platforms (Vertical) //
        for(int platformNum = 0; platformNum < 0; platformNum++) {
            int platformWidth = new Random().nextInt(4) + 4;
            int platformHeight = new Random().nextInt(3) + 3;
            int xMod = 4 + new Random().nextInt(6);
            if(platformNum % 2 == 1) {
                xMod *= -1;
            }
            createDirtPlatform(screenChunks, 60 + xMod, 12 + (platformNum * 6), platformWidth, platformHeight);
        }

        // Floating Platforms (Horizontal) //
        for(int platformNum = 0; platformNum < 20; platformNum++) {
            int platformWidth = new Random().nextInt(6) + 6;
            int platformHeight = new Random().nextInt(3) + 3;
            createDirtPlatform(screenChunks, 4 + (platformNum * 14), 66, platformWidth, platformHeight);
        }

        // Wall FrameBuffer (Trees, Grass, Rocks) //
        ArrayList<Texture> grassTexture = new ArrayList<>();
        grassTexture.add(new Texture("images/objects/Grass_A.png"));
        grassTexture.add(new Texture("images/objects/Grass_B.png"));
        grassTexture.add(new Texture("images/objects/Grass_C.png"));
        grassTexture.add(new Texture("images/objects/Grass_D.png"));
        Texture rockTexture = new Texture("images/objects/Rock.png");
        Texture tombstoneTexture = new Texture("images/objects/Tombstone.png");

        for(int chunkX = 0; chunkX < screenChunks.length; chunkX++) {
            for(int frameBufferIndex = 0; frameBufferIndex < 3; frameBufferIndex++) {

                // Trees //
                if(frameBufferIndex == 0 || frameBufferIndex == 1) {
                    ArrayList<Integer> treeLocationList = new ArrayList<>();
                    for(int i = 0; i < 0; i++) {
                        int xLoc;
                        if(chunkX == 0) {
                            xLoc = new Random().nextInt(Gdx.graphics.getWidth()) - 81;
                        } else if(chunkX == screenChunks.length - 1) {
                            xLoc = new Random().nextInt(Gdx.graphics.getWidth() - 73) + 73;
                        } else {
                            xLoc = new Random().nextInt(Gdx.graphics.getWidth() - (73 * 2)) + 73;
                        }
    
                        // Adjacent Tree Check //
                        boolean drawTree = true;
                        for(Integer previousX : treeLocationList) {
                            if(previousX > xLoc - 20 && previousX < xLoc + 20) {
                                drawTree = false;
                                break;
                            }
                        }
    
                        // Draw Tree //
                        if(drawTree) {
                            int yLoc = 112;
                            int tileX = (xLoc - 8) / 16;
                            int tileY = 6;
            
                            // Get Start Tile Height //
                            if(tileX >= 0 && tileX < screenChunks[0][0].tiles.length) {
                                if(screenChunks[chunkX][0].tiles[tileX][tileY + 2] != null) {
                                    tileY += 2;
                                } else if(screenChunks[chunkX][0].tiles[tileX][tileY + 1] != null) {
                                    tileY += 1;
                                }
                            }
                            
                            // No Building Trees On Ramps //
                            for(int xMod = -2; xMod < 3; xMod++) {
                                if(tileX + xMod >= 0 && tileX + xMod < screenChunks[0][0].tiles.length) {
                                    Tile targetTile = screenChunks[chunkX][0].tiles[tileX + xMod][tileY];
                                    if(targetTile == null || !targetTile.tileName.equals("Square")) {
                                        drawTree = false;
                                        break;
                                    }
                                }
                            }
                
                            if(drawTree) {
                                yLoc = (tileY * 16) + 16;
        
                                boolean isThin = false;
                                if(new Random().nextInt(3) == 0) {
                                    isThin = true;
                                }
        
                                boolean isDark = false;
                                if(frameBufferIndex == 0) {
                                    isDark = true;
                                }
        
                                drawTree(spriteBatch, screenChunks[chunkX][0].frameBufferWalls, xLoc, yLoc, isThin, isDark);
                                treeLocationList.add(xLoc);
                            }
                        }
                    }
                }

                // Rocks & Tombstones //
                if(frameBufferIndex == 2) {
                    screenChunks[chunkX][0].frameBufferWalls.begin();
                    spriteBatch.begin();
                    
                    for(int x = 0; x < screenChunks[0][0].tiles.length; x++) {
                        if(new Random().nextInt(20) == 0) {

                            // Get Target Y-Level & Tile //
                            int targetYLevel = 6;
                            if(screenChunks[chunkX][0].tiles[x][8] != null) {
                                targetYLevel = 8;
                            } else if(screenChunks[chunkX][0].tiles[x][7] != null) {
                                targetYLevel = 7;
                            }
                            Tile targetTile = screenChunks[chunkX][0].tiles[x][targetYLevel];

                            if(targetTile != null && targetTile.tileName.equals("Square")) {
                                if(new Random().nextInt(4) == 0) {
                                    if(x < screenChunks[0][0].tiles.length - 1 && screenChunks[chunkX][0].tiles[x + 1][targetYLevel].tileName.equals("Square")) {
                                        spriteBatch.draw(tombstoneTexture, x * 16, (targetYLevel + 1) * 16);
                                    }
                                } else {
                                    spriteBatch.draw(rockTexture, x * 16, (targetYLevel + 1) * 16);
                                }
                            }
                        }
                    }

                    spriteBatch.end();
                    screenChunks[chunkX][0].frameBufferWalls.end();
                }

                // Grass //
                if(frameBufferIndex == 0) {
                    screenChunks[chunkX][0].frameBufferForeground.begin();
                    spriteBatch.begin();
                    
                    for(int x = 0; x < screenChunks[0][0].tiles.length; x++) {
                        if(new Random().nextInt(3) == 0) {

                            // Get Target Y-Level & Tile //
                            int targetYLevel = 6;
                            if(screenChunks[chunkX][0].tiles[x][8] != null) {
                                targetYLevel = 8;
                            } else if(screenChunks[chunkX][0].tiles[x][7] != null) {
                                targetYLevel = 7;
                            }
                            Tile targetTile = screenChunks[chunkX][0].tiles[x][targetYLevel];

                            // Draw Grass //
                            if(targetTile != null && targetTile.tileName.equals("Square")) {
                                spriteBatch.draw(grassTexture.get(new Random().nextInt(4)), x * 16, (targetYLevel + 1) * 16);
                            }
                        }
                    }

                    spriteBatch.end();
                    screenChunks[chunkX][0].frameBufferForeground.end();
                }
            }
        }

        // Dispose Textures //
        for(Texture texture : grassTexture) {
            texture.dispose();
        }
        rockTexture.dispose();
    }
}
