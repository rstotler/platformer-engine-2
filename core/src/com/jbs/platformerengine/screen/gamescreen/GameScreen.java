package com.jbs.platformerengine.screen.gamescreen;

import java.util.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import com.jbs.platformerengine.components.Keyboard;
import com.jbs.platformerengine.gamedata.area.Area01;
import com.jbs.platformerengine.gamedata.area.Area02;
import com.jbs.platformerengine.gamedata.area.AreaData;
import com.jbs.platformerengine.gamedata.entity.BreakableObject;
import com.jbs.platformerengine.gamedata.entity.Mob;
import com.jbs.platformerengine.gamedata.entity.player.Player;
import com.jbs.platformerengine.screen.ImageManager;
import com.jbs.platformerengine.screen.Screen;

/* To-Do List:
 * Fix Ceiling Ramp Collisions
 * Wave Shader When Walking Past Grass
 * Fix animation stack order
 * Bridge - top background, round the pillar sprites
 * Combat - charged attacks, combo attacks
 * Moon, blending
 * Basic mob
 * Input audit (2 buttons at same time?)
 * 
 * Bugs:
 * Superjumps Can Get Disabled Somehow Through Excessive Dropkick/Superjumping
 * Jumps get disabled somehow when holding jump when bouncing?
 * Bug when landing bottom right corner on top corner of right ramp
 */

public class GameScreen extends Screen {
    OrthographicCamera camera;
    OrthographicCamera cameraDebug;
    Keyboard keyboard;

    AreaData areaData;
    
    ImageManager imageManager;
    
    public GameScreen() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        cameraDebug = new OrthographicCamera();
        cameraDebug.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        keyboard = new Keyboard();

        loadAreaData();
    }

    public void loadAreaData() {
        areaData = new Area01();

        imageManager = new ImageManager(areaData.tileSetList, areaData.animatedImageList);
        areaData.loadArea(spriteBatch, imageManager);
        areaData.loadBackgroundFrameBuffers(spriteBatch);

        bufferChunks();
    }

    public void bufferChunks() {
        for(int y = 0; y < areaData.screenChunks[0].length; y++) {
            for(int x = 0; x < areaData.screenChunks.length; x++) {
                areaData.screenChunks[x][y].bufferTiles(spriteBatch, imageManager);
            }
        }
    }

    public void handleInput(Player player) {
        Gdx.input.setInputProcessor(new InputAdapter() {

            @Override
            public boolean keyDown(int keyCode) {
                String key = Input.Keys.toString(keyCode);

                if(key.equals("Left") || key.equals("Right") || key.equals("Up") || key.equals("Down")
                || key.equals("A") || key.equals("S") || key.equals("D") || key.equals("W")
                || key.equals("L-Shift") || key.equals("R-Shift")) {
                    keyboard.keyDown(key);
                }

                else if(key.equals("Escape")) {
                    System.exit(0);
                }

                return true;
            }

            @Override
            public boolean keyUp(int keyCode) {
                String key = Input.Keys.toString(keyCode);
                
                if(key.equals("Left") || key.equals("Right") || key.equals("Up") || key.equals("Down")
                || key.equals("A") || key.equals("S") || key.equals("D") || key.equals("W")
                || key.equals("L-Shift") || key.equals("R-Shift")) {
                    keyboard.keyUp(key);
                }

                return true;
            }
        });

        if(player.jumpButtonPressedCheck && (!Gdx.input.isKeyPressed(Keys.UP) && !Gdx.input.isKeyPressed(Input.Keys.W))) {
            player.jumpTimer = player.jumpTimerMax;
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            player.superJump();
        } else if(Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            player.dash("Left");
        } else if(Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            player.dash("Right");
        } else if(Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            player.attack();
        } else if(Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            player.duck(true);
        } else if(keyboard.lastUp.equals("S") || keyboard.lastUp.equals("Down")) {
            player.duck(false);
        }

        // Click Screen //
        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            clickScreen(true, "Left");
        } else if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            clickScreen(false, "Left");
        } else if(Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            clickScreen(true, "Right");
        }
    }

    public void clickScreen(boolean justClicked, String targetButton) {
        float cameraZoomPercent = camera.viewportHeight / Gdx.graphics.getHeight();
        int xLoc = (int) (camera.position.x - (camera.viewportWidth / 2) + (Gdx.input.getX() * cameraZoomPercent));
        int yLoc = (int) (camera.position.y - (camera.viewportHeight / 2) + ((Gdx.graphics.getHeight() - Gdx.input.getY()) * cameraZoomPercent));
        
        if(xLoc >= 0 && yLoc >= 0) {
            int chunkX = (xLoc / 16) / areaData.screenChunks[0][0].tiles.length;
            int chunkY = (yLoc / 16) / areaData.screenChunks[0][0].tiles[0].length;
            int tileX = (xLoc / 16) % areaData.screenChunks[0][0].tiles.length;
            int tileY = (yLoc / 16) % areaData.screenChunks[0][0].tiles[0].length;

            if(chunkX >= 0 && chunkX < areaData.screenChunks.length && chunkY >= 0 && chunkY < areaData.screenChunks[0].length
            && tileX >= 0 && tileX < areaData.screenChunks[0][0].tiles.length && tileY >= 0 && tileY < areaData.screenChunks[0][0].tiles[0].length) {
                String tileType = "None";
                
                if(targetButton.equals("Left")) {
                    if(!justClicked && areaData.screenChunks[chunkX][chunkY].tiles[tileX][tileY] == null) {
                        areaData.screenChunks[chunkX][chunkY].tiles[tileX][tileY] = new Tile(areaData.defaultTileSet, areaData.defaultTileName, areaData.defaultTileNum);
                        tileType = areaData.screenChunks[chunkX][chunkY].tiles[tileX][tileY].tileShape;
        
                        Texture texture = new Texture("images/tiles/Debug/Square_01.png");
                        areaData.screenChunks[chunkX][chunkY].frameBufferTiles.begin();
                        spriteBatch.begin();
                        spriteBatch.draw(texture, tileX * 16, tileY * 16);
                        spriteBatch.end();
                        areaData.screenChunks[chunkX][chunkY].frameBufferTiles.end();
                    }
                    else if(justClicked) {
                        int tileTypeIndex = 0;
                        ArrayList<String> tileTypeList = new ArrayList<>(Arrays.asList("Square", "Square-Half", "Ramp", "Ramp", "Ramp-Bottom", "Ramp-Bottom", "Ramp-Top", "Ramp-Top", "Ceiling-Ramp", "Ceiling-Ramp"));
                        ArrayList<String> tileShapeList = new ArrayList<>(Arrays.asList("Square", "Square-Half", "Ramp-Right", "Ramp-Left", "Ramp-Right-Half-Bottom", "Ramp-Left-Half-Bottom", "Ramp-Right-Half-Top", "Ramp-Left-Half-Top", "Ceiling-Ramp-Right", "Ceiling-Ramp-Left"));
                        if(areaData.screenChunks[chunkX][chunkY].tiles[tileX][tileY] != null) {
                            tileTypeIndex = tileTypeList.indexOf(areaData.screenChunks[chunkX][chunkY].tiles[tileX][tileY].tileName) + areaData.screenChunks[chunkX][chunkY].tiles[tileX][tileY].num;
                            if(tileTypeIndex >= tileTypeList.size()) {
                                tileTypeIndex = 0;
                            }
                            areaData.screenChunks[chunkX][chunkY].tiles[tileX][tileY].tileName = tileTypeList.get(tileTypeIndex);
                            areaData.screenChunks[chunkX][chunkY].tiles[tileX][tileY].tileShape = tileShapeList.get(tileTypeIndex);
                            areaData.screenChunks[chunkX][chunkY].tiles[tileX][tileY].num = 1;
                            if(tileShapeList.get(tileTypeIndex).contains("Left")) {
                                areaData.screenChunks[chunkX][chunkY].tiles[tileX][tileY].num = 2;
                            }
                        } else {
                            areaData.screenChunks[chunkX][chunkY].tiles[tileX][tileY] = new Tile(areaData.defaultTileSet, areaData.defaultTileName, areaData.defaultTileNum);
                        }
        
                        areaData.screenChunks[chunkX][chunkY].bufferTiles(spriteBatch, imageManager);
                    }

                    tileType = areaData.screenChunks[chunkX][chunkY].tiles[tileX][tileY].tileShape;
                }
                else if(targetButton.equals("Right")) {
                    areaData.screenChunks[chunkX][chunkY].tiles[tileX][tileY] = null;
                    areaData.screenChunks[chunkX][chunkY].bufferTiles(spriteBatch, imageManager);
                }

                System.out.println(tileX + " " + tileY + " " + tileType);
            }
        }
    }

    public void update(Player player) {
        player.update(keyboard, areaData.screenChunks);
    }

    public void render(Player player) {
        ScreenUtils.clear(0/255f, 0/255f, 7/255f, 1);

        // Update Camera //
        if(player.hitBoxArea.x < 312) {
            camera.position.set(320, (player.hitBoxArea.y + 80), 0);
        } else if(player.hitBoxArea.x > 952 + (Gdx.graphics.getWidth() * (areaData.screenChunks.length - 1))) {
            camera.position.set(960 + (Gdx.graphics.getWidth() * (areaData.screenChunks.length - 1)), (player.hitBoxArea.y + 80), 0);
        } else if(player.hitBoxArea.x >= 312) {
            camera.position.set(player.hitBoxArea.getMiddle().x, (player.hitBoxArea.y + 80), 0);
        }
        camera.update();

        renderBackground(player);
        renderWalls(player);
        renderTiles(player);
        renderAnimations(player);
        player.render(camera);
        renderForeground(player);

        renderDebugData(player);
    }

    public void renderBackground(Player player) {
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        float xPercent = (player.hitBoxArea.x - 312.0f) / ((Gdx.graphics.getWidth() * areaData.screenChunks.length) - 640);
        if(areaData.frameBufferBackground != null) {
            for(int i = 0; i < areaData.frameBufferBackground.size(); i++) {
                float xMod = 0;
                if(xPercent >= 0) {
                    if(xPercent > 1) {
                        xPercent = 1;
                    }
                    if(i == 0) {
                        xMod = 1750 * xPercent;
                    } else if(i == 1) {
                        xMod = 1500 * xPercent;
                    } else if(i == 2) {
                        xMod = 750 * xPercent;
                    }
                }
                int xIndex = (int) ((player.hitBoxArea.x - 312) / (Gdx.graphics.getWidth() + xMod));
                float xLoc = (xIndex * Gdx.graphics.getWidth()) + xMod;

                float yMod = 0;
                if(player.hitBoxArea.y > (6 * 16) + 16) {
                    int playerHeight = player.hitBoxArea.y - (6 * 16) - 16;
                    float heightPercent = (playerHeight / 600.0f);
                    yMod = heightPercent * 400;

                    if(i == 1) {
                        yMod += heightPercent * 14;
                    } else if(i == 2) {
                        yMod += heightPercent * 50;
                    }
                }

                if(i == 0 || i == 2) {
                    spriteBatch.draw(areaData.frameBufferBackground.get(i).getColorBufferTexture(), xLoc - Gdx.graphics.getWidth(), yMod, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1, 1);
                }
                spriteBatch.draw(areaData.frameBufferBackground.get(i).getColorBufferTexture(), xLoc, yMod, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1, 1);
                spriteBatch.draw(areaData.frameBufferBackground.get(i).getColorBufferTexture(), xLoc + Gdx.graphics.getWidth(), yMod, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1, 1);

                if(player.hitBoxArea.x >= 4600) {
                    spriteBatch.draw(areaData.frameBufferBackground.get(i).getColorBufferTexture(), xLoc + (Gdx.graphics.getWidth() * 2), yMod, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1, 1);
                }
            }
        }

        spriteBatch.end();
    }

    public void renderWalls(Player player) {
        int chunkStartX = player.hitBoxArea.x / Gdx.graphics.getWidth() - 1;
        int chunkStartY = player.hitBoxArea.y / Gdx.graphics.getHeight() - 1;

        for(int y = chunkStartY; y < chunkStartY + 3; y++) {
            for(int x = chunkStartX; x < chunkStartX + 3; x++) {
                if(x >= 0 && y >= 0 && x < areaData.screenChunks.length && y < areaData.screenChunks[0].length) {
                    spriteBatch.setProjectionMatrix(camera.combined);
                    spriteBatch.begin();
                    int xLoc = x * Gdx.graphics.getWidth();
                    int yLoc = y * Gdx.graphics.getHeight();
                    spriteBatch.draw(areaData.screenChunks[x][y].frameBufferWalls.getColorBufferTexture(), xLoc, yLoc, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1, 1);
                    spriteBatch.end();
                }
            }
        }
    }

    public void renderTiles(Player player) {
        int chunkStartX = player.hitBoxArea.x / Gdx.graphics.getWidth() - 1;
        int chunkStartY = player.hitBoxArea.y / Gdx.graphics.getHeight() - 1;

        for(int y = chunkStartY; y < chunkStartY + 3; y++) {
            for(int x = chunkStartX; x < chunkStartX + 3; x++) {
                if(x >= 0 && y >= 0 && x < areaData.screenChunks.length && y < areaData.screenChunks[0].length) {
                    areaData.screenChunks[x][y].renderTiles(camera, spriteBatch);
                }
            }
        }
    }

    public void renderAnimations(Player player) {
        int chunkStartX = player.hitBoxArea.x / Gdx.graphics.getWidth() - 1;
        int chunkStartY = player.hitBoxArea.y / Gdx.graphics.getHeight() - 1;

        for(int y = chunkStartY; y < chunkStartY + 3; y++) {
            for(int x = chunkStartX; x < chunkStartX + 3; x++) {
                if(x >= 0 && y >= 0 && x < areaData.screenChunks.length && y < areaData.screenChunks[0].length) {
                    areaData.screenChunks[x][y].bufferAnimations(camera, spriteBatch, imageManager);

                    spriteBatch.setProjectionMatrix(camera.combined);
                    spriteBatch.begin();
                    int xLoc = x * Gdx.graphics.getWidth();
                    int yLoc = y * Gdx.graphics.getHeight();
                    spriteBatch.draw(areaData.screenChunks[x][y].frameBufferAnimation.getColorBufferTexture(), xLoc, yLoc, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1, 1);
                    spriteBatch.end();
                }
            }
        }
    }

    public void renderForeground(Player player) {
        int chunkStartX = player.hitBoxArea.x / Gdx.graphics.getWidth() - 1;
        int chunkStartY = player.hitBoxArea.y / Gdx.graphics.getHeight() - 1;

        for(int y = chunkStartY; y < chunkStartY + 3; y++) {
            for(int x = chunkStartX; x < chunkStartX + 3; x++) {
                if(x >= 0 && y >= 0 && x < areaData.screenChunks.length && y < areaData.screenChunks[0].length) {
                    spriteBatch.setProjectionMatrix(camera.combined);
                    spriteBatch.begin();
                    int xLoc = x * Gdx.graphics.getWidth();
                    int yLoc = y * Gdx.graphics.getHeight();
                    spriteBatch.draw(areaData.screenChunks[x][y].frameBufferForeground.getColorBufferTexture(), xLoc, yLoc, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1, 1);
                    spriteBatch.end();
                }
            }
        }
    }

    public void renderDebugData(Player player) {
        spriteBatch.setProjectionMatrix(cameraDebug.combined);
        spriteBatch.begin();
        font.setColor(Color.WHITE);
        font.draw(spriteBatch, "FPS: " + String.valueOf(Gdx.graphics.getFramesPerSecond()), 1205, 767);

        font.draw(spriteBatch, "Player Pos: X - " + player.hitBoxArea.x + " (" + (player.hitBoxArea.x % Gdx.graphics.getWidth()) + ") " + " Y - " + player.hitBoxArea.y + " (" + (player.hitBoxArea.y % Gdx.graphics.getHeight()) + ")", 3, 765);
        font.draw(spriteBatch, "Velocity: X - " + player.velocity.x + " Y - " + player.velocity.y, 3, 750);
        font.draw(spriteBatch, "On Ramp: " + player.onRamp + " - On Half-Ramp: " + player.onHalfRamp, 3, 735);
        font.draw(spriteBatch, "Jumping: " + player.jumpCheck + " (" + player.jumpTimer + ") " + player.jumpCount + " Falling: " + player.falling, 3, 720);
        
        String attackString = " (0)";
        if(player.attackCount > 0) {
            attackString = " (" + player.attackDecayTimer + "/" + player.attackData.get(player.getCurrentAttack()).attackDecayTimerMax[player.attackCount - 1] + ")";
        }
        font.draw(spriteBatch, "Attack: " + player.attackCount + attackString + " - DK: " + player.dropKickCheck + " SJ: " + player.superJumpCheck, 3, 705);

        spriteBatch.end();
    }

    // Utility Functions //
    public static <T> void addObjectToCellCollidables(ScreenChunk[][] screenChunks, T object) {
        String objectClass = object.getClass().toString().substring(object.getClass().toString().lastIndexOf(".") + 1);
        
        if(objectClass.equals("BreakableObject")) {
            BreakableObject breakableObject = (BreakableObject) object;

            for(CellCollidables cellCollidables : getObjectCellCollidables(screenChunks, breakableObject)) {
                if(!cellCollidables.breakableList.contains(breakableObject)) {
                    cellCollidables.breakableList.add(breakableObject);
                }

                if(!screenChunks[cellCollidables.chunkX][cellCollidables.chunkY].breakableList.contains(breakableObject)) {
                    screenChunks[cellCollidables.chunkX][cellCollidables.chunkY].breakableList.add(breakableObject);
                }
            }
        }

        else if(objectClass.equals("Mob")) {
            Mob mobObject = (Mob) object;

            for(CellCollidables cellCollidables : getObjectCellCollidables(screenChunks, mobObject)) {
                if(!cellCollidables.mobList.contains(mobObject)) {
                    cellCollidables.mobList.add(mobObject);
                }

                if(!screenChunks[cellCollidables.chunkX][cellCollidables.chunkY].mobList.contains(mobObject)) {
                    screenChunks[cellCollidables.chunkX][cellCollidables.chunkY].mobList.add(mobObject);
                }
            }
        }
    }

    public static <T> void removeObjectFromCellCollidables(ScreenChunk[][] screenChunks, T object) {
        String objectClass = object.getClass().toString().substring(object.getClass().toString().lastIndexOf(".") + 1);
        
        if(objectClass.equals("BreakableObject")) {
            BreakableObject breakableObject = (BreakableObject) object;

            for(CellCollidables cellCollidables : getObjectCellCollidables(screenChunks, object)) {
                if(cellCollidables.breakableList.contains(breakableObject)) {
                    cellCollidables.breakableList.remove(breakableObject);
                }
    
                if(screenChunks[cellCollidables.chunkX][cellCollidables.chunkY].breakableList.contains(breakableObject)) {
                    screenChunks[cellCollidables.chunkX][cellCollidables.chunkY].breakableList.remove(breakableObject);
                }
            }
        }
    }

    public static <T> ArrayList<CellCollidables> getObjectCellCollidables(ScreenChunk[][] screenChunks, T object) {
        String objectClass = object.getClass().toString().substring(object.getClass().toString().lastIndexOf(".") + 1);
        BreakableObject breakableObject = null;
        Mob mobObject = null;

        ArrayList<CellCollidables> objectCellCollidables = new ArrayList<>();

        int xLoc = 0;
        int yLoc = 0;
        int objectWidth = 0;
        int objectHeight = 0;
        int chunkX;
        int chunkY;
        int xCellStartIndex;
        int yCellStartIndex;
        int xPadding;
        int yPadding;
        int xCellSize = -1;
        int yCellSize = -1;

        if(objectClass.equals("BreakableObject") || objectClass.equals("Mob")) {
            if(objectClass.equals("BreakableObject")) {
                breakableObject = (BreakableObject) object;
                xLoc = breakableObject.hitBoxArea.x;
                yLoc = breakableObject.hitBoxArea.y;
                objectWidth = breakableObject.hitBoxArea.width;
                objectHeight = breakableObject.hitBoxArea.height;
            } else if(objectClass.equals("Mob")) {
                mobObject = (Mob) object;
                xLoc = mobObject.hitBoxArea.x;
                yLoc = mobObject.hitBoxArea.y;
                objectWidth = mobObject.hitBoxArea.width;
                objectHeight = mobObject.hitBoxArea.height;
            }

            chunkX = xLoc / Gdx.graphics.getWidth();
            chunkY = yLoc / Gdx.graphics.getHeight();
            xCellStartIndex = (xLoc % Gdx.graphics.getWidth()) / 64;
            yCellStartIndex = (yLoc % Gdx.graphics.getHeight()) / 64;
            xPadding = xLoc - ((chunkX * Gdx.graphics.getWidth()) + (xCellStartIndex * 64));
            yPadding = yLoc - ((chunkY * Gdx.graphics.getHeight()) + (yCellStartIndex * 64));
            xCellSize = ((objectWidth + xPadding) / 64) + 1;
            yCellSize = ((objectHeight + yPadding) / 64) + 1;
        }

        if(xCellSize != -1) {
            for(int y = 0; y < yCellSize; y++) {
                chunkY = (yLoc + (y * 64)) / Gdx.graphics.getHeight();
                int cellY = ((yLoc + (y * 64)) % Gdx.graphics.getHeight()) / 64;
                for(int x = 0; x < xCellSize; x++) {
                    chunkX = (xLoc + (x * 64)) / Gdx.graphics.getWidth();
                    int cellX = ((xLoc + (x * 64)) % Gdx.graphics.getWidth()) / 64;
    
                    if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length) {
                        objectCellCollidables.add(screenChunks[chunkX][chunkY].cellCollidables[cellX][cellY]);
                    }
                }
            }
        }

        return objectCellCollidables;
    }
}
