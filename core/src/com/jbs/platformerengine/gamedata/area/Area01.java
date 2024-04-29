package com.jbs.platformerengine.gamedata.area;

import java.util.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jbs.platformerengine.gamedata.Point;
import com.jbs.platformerengine.gamedata.Rect;
import com.jbs.platformerengine.gamedata.entity.Mob;
import com.jbs.platformerengine.screen.ImageManager;
import com.jbs.platformerengine.screen.gamescreen.Tile;

public class Area01 extends AreaData {
    public Area01() {
        levelName = "Area01";
        size = new Rect(3, 2);

        defaultTileSet = "Dirt-Floor";
        defaultTileName = "Square";
        defaultTileNum = 2;

        tileSetList = new ArrayList<>(Arrays.asList("Dirt-Floor", "Dirt-Platform", "Stone", "Wood"));
        animatedImageList = new ArrayList<>(Arrays.asList("Torch_01"));

        loadScreenChunks();
    }

    public void loadArea(SpriteBatch spriteBatch, ImageManager imageManager, boolean initCheck) {
        if(initCheck) {

            // Base Floor //
            createFloor("Dirt-Floor", true);

            // Floating Platforms (Vertical) //
            for(int platformNum = 0; platformNum < 0; platformNum++) {
                int platformWidth = new Random().nextInt(4) + 4;
                int platformHeight = new Random().nextInt(3) + 3;
                int xMod = 4 + new Random().nextInt(6);
                if(platformNum % 2 == 1) {
                    xMod *= -1;
                }
                createPlatform("Dirt-Platform", 60 + xMod, 12 + (platformNum * 6), platformWidth, platformHeight);
            }

            // Floating Platforms (Horizontal) //
            for(int platformNum = 0; platformNum < 20; platformNum++) {
                int platformWidth = new Random().nextInt(6) + 5;
                int platformHeight = new Random().nextInt(3) + 3;
                createPlatform("Dirt-Platform", 4 + (platformNum * 13), 66 + (new Random().nextInt(3) - 1), platformWidth, platformHeight);
            }
        }

        // Trees //
        int treeCount = 0; // Count Per Layer (2)
        for(int chunkX = 0; chunkX < screenChunks.length; chunkX++) {
            for(int treeLayerIndex = 0; treeLayerIndex < 2; treeLayerIndex++) {
                if(treeLayerIndex == 0 || treeLayerIndex == 1) {
                    ArrayList<Integer> treeLocationList = new ArrayList<>();
                    for(int i = 0; i < treeCount; i++) {
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
                                if(treeLayerIndex == 0) {
                                    isDark = true;
                                }
        
                                createTree(spriteBatch, screenChunks[chunkX][0].frameBufferWalls, xLoc, yLoc, isThin, isDark);
                                treeLocationList.add(xLoc);
                            }
                        }
                    }
                }
            }
        }

        // Grass (Foreground) //
        ArrayList<Texture> grassTexture = new ArrayList<>();
        grassTexture.add(new Texture("images/objects/Grass_01.png"));
        grassTexture.add(new Texture("images/objects/Grass_02.png"));
        grassTexture.add(new Texture("images/objects/Grass_03.png"));
        grassTexture.add(new Texture("images/objects/Grass_04.png"));

        for(int chunkX = 0; chunkX < screenChunks.length; chunkX++) {
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

                    if(targetTile != null && targetTile.tileName.equals("Square")) {
                        spriteBatch.draw(grassTexture.get(new Random().nextInt(4)), x * 16, (targetYLevel + 1) * 16);
                    }
                }
            }

            spriteBatch.end();
            screenChunks[chunkX][0].frameBufferForeground.end();
        }

        // Bridge //
        int bridgeYLoc = 37;
        createBridge(spriteBatch, imageManager, 80, bridgeYLoc, 160, 4, 11, initCheck);

        // Bridge Exit To Area02 //
        for(int y = 0; y < 7; y++) {
            int chunkY = (bridgeYLoc + 1 + y) / 48;
            int tileY = (bridgeYLoc + 1 + y) % 48;
            screenChunks[size.width - 1][chunkY].tiles[79][tileY] = new Tile("Area02", new Point(16, 112));
        }

        // Rocks & Tombstones //
        Texture rockTexture = new Texture("images/objects/Rock_01.png");
        Texture tombstoneTexture = new Texture("images/objects/Tombstone_01.png");

        for(int chunkX = 0; chunkX < screenChunks.length; chunkX++) {
            screenChunks[chunkX][0].frameBufferWalls.begin();
            spriteBatch.begin();
            
            for(int x = 0; x < screenChunks[0][0].tiles.length; x++) {
                if(new Random().nextInt(24) == 0) {

                    // Get Target Y-Level & Tile //
                    int targetYLevel = 6;
                    if(screenChunks[chunkX][0].tiles[x][8] != null) {
                        targetYLevel = 8;
                    } else if(screenChunks[chunkX][0].tiles[x][7] != null) {
                        targetYLevel = 7;
                    }
                    Tile targetTile = screenChunks[chunkX][0].tiles[x][targetYLevel];

                    if(targetTile != null && targetTile.tileName.equals("Square")) {
                        if(new Random().nextInt(5) == 0) {
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

        // Test Mobs //
        Mob testMob = new Mob();
        // GameScreen.addObjectToCellCollidables(screenChunks, testMob);

        // Dispose Textures //
        for(Texture texture : grassTexture) {
            texture.dispose();
        }
        rockTexture.dispose();
        tombstoneTexture.dispose();
    }
}
