package com.jbs.platformerengine.screen.gamescreen;

import java.util.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.jbs.platformerengine.PlatformerEngine;
import com.jbs.platformerengine.components.Keyboard;
import com.jbs.platformerengine.gamedata.area.*;
import com.jbs.platformerengine.gamedata.area.entity.Cloud;
import com.jbs.platformerengine.gamedata.entity.BreakableObject;
import com.jbs.platformerengine.gamedata.entity.mob.Mob;
import com.jbs.platformerengine.gamedata.entity.player.Player;
import com.jbs.platformerengine.screen.ImageManager;
import com.jbs.platformerengine.screen.Screen;

/* To-Do List:
 * Wave Shader When Walking Past Grass
 * Combat - Charged Attacks, Class Out Different Attacks, Multiple Hitboxes Per Attack, Knife Throw Ability
 * Movement - Mob After Images, Fall Speed Off Of Tiles
 * Background - Clouds, Stars, Pixelate Moon Glow
 * Areas - Tower, Underground
 * Audit Enemies In GameScreen.getObjectCellCollidables()
 * No Combos When Dashing
 * 
 * Bugs:
 * Superjumps Can Get Disabled Somehow Through Excessive Dropkick/Superjumping (Still)
 * Jumps Somehow Get Disabled When Holding Jump When Bouncing?
 * Can't Dropkick Bat After Targeting From Sword Attack
 * Left/Right Collision Error On Small Hitboxes?
 * Going Through Tiles Other Than Square At High Speed? (Debug Area)
 * Reset RunAcceleration After Hitting Wall, Clipping Through Ramps Running Too Fast
 */

public class GameScreen extends Screen {
    OrthographicCamera cameraDebug;
    Keyboard keyboard;

    public static AreaData areaData;
    public static HashMap<String, AreaData> unusedAreaData;
    
    ShapeRenderer shapeRenderer;
    ImageManager imageManager;

    int displayDebugData;
    
    public GameScreen(PlatformerEngine platformerEngine) {
        super();
        
        cameraDebug = new OrthographicCamera();
        cameraDebug.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        keyboard = new Keyboard();
        initInputAdapter();
        
        areaData = new Area01();
        unusedAreaData = new HashMap<>();
        unusedAreaData.put("AreaDebug", new AreaDebug());
        unusedAreaData.put("Area02", new Area02());

        shapeRenderer = new ShapeRenderer();
        imageManager = new ImageManager(areaData.tileSetList, areaData.breakableImageList, areaData.mobImageList, areaData.outside);
        
        displayDebugData = 1;

        areaData.loadArea(spriteBatch, imageManager);
        areaData.loadBackgroundFrameBuffers(spriteBatch);

        bufferChunks();

        platformerEngine.player = new Player(imageManager);
    }

    public void initInputAdapter() {
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
    }

    public void bufferChunks() {
        for(int y = 0; y < areaData.screenChunks[0].length; y++) {
            for(int x = 0; x < areaData.screenChunks.length; x++) {
                areaData.screenChunks[x][y].bufferTiles(spriteBatch, imageManager);
            }
        }
    }

    public void handleInput(Player player) {
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
        } else if(Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            displayDebugData += 1;
            if(displayDebugData >= 3) {
                displayDebugData = 0;
            }
        } else if(keyboard.lastUp.contains("S") || keyboard.lastUp.contains("Down")) {
            player.duck(false);
        }

        // Temp. Shapeshift //
        else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            player.changeSize(1);
        } else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            player.changeSize(2);
        } else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            player.changeSize(3);
        } else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
            player.changeForm(true);
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
                        areaData.screenChunks[chunkX][chunkY].tiles[tileX][tileY] = new Tile(areaData.defaultTileSet, areaData.defaultTileName, areaData.defaultTileNum, chunkX, chunkY, tileX, tileY);
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
                            areaData.screenChunks[chunkX][chunkY].tiles[tileX][tileY] = new Tile(areaData.defaultTileSet, areaData.defaultTileName, areaData.defaultTileNum, chunkX, chunkY, tileX, tileY);
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
        areaData.update(player);
        player.updateInput(keyboard);
        player.update(areaData.screenChunks, null);
        
        // Change Area Check //
        int chunkX = player.hitBoxArea.getMiddle().x / Gdx.graphics.getWidth();
        int chunkY = player.hitBoxArea.getMiddle().y / Gdx.graphics.getHeight();
        int tileX = (player.hitBoxArea.getMiddle().x % Gdx.graphics.getWidth()) / 16;
        int tileY = (player.hitBoxArea.getMiddle().y % Gdx.graphics.getHeight()) / 16;
        int xLoc = (chunkX * Gdx.graphics.getWidth()) + (tileX * 16);
        int yLoc = (chunkY * Gdx.graphics.getHeight()) + (tileY * 16);
        Tile changeAreaTile = ScreenChunk.getTile(areaData.screenChunks, xLoc, yLoc);
        if(changeAreaTile != null
        && !changeAreaTile.changeArea.equals("None")) {
            changeArea(player, changeAreaTile);
        }
    }

    public void changeArea(Player player, Tile targetTile) {
        if(unusedAreaData.containsKey(targetTile.changeArea)) {
            areaData.dispose();

            // Create RemoveTileSetList & RemoveBreakableImageList //
            ArrayList<String> removeTileSetList = new ArrayList<>();
            for(String tileSet : areaData.tileSetList) {
                if(!unusedAreaData.get(targetTile.changeArea).tileSetList.contains(tileSet)) {
                    removeTileSetList.add(tileSet);
                }
            }
            ArrayList<String> removeBreakableImageList = new ArrayList<>();
            for(String breakableImageName : areaData.breakableImageList) {
                if(!unusedAreaData.get(targetTile.changeArea).breakableImageList.contains(breakableImageName)) {
                    removeBreakableImageList.add(breakableImageName);
                }
            }
            ArrayList<String> removeMobImageList = new ArrayList<>();
            for(String mobImageName : areaData.mobImageList) {
                if(!unusedAreaData.get(targetTile.changeArea).mobImageList.contains(mobImageName)) {
                    removeMobImageList.add(mobImageName);
                }
            }
            imageManager.removeImages(removeTileSetList, removeBreakableImageList, removeMobImageList, unusedAreaData.get(targetTile.changeArea).outside);
            
            unusedAreaData.put(areaData.levelName, areaData);
            areaData = unusedAreaData.get(targetTile.changeArea);
            unusedAreaData.remove(targetTile.changeArea);

            areaData.changeArea(spriteBatch, imageManager, player);

            player.hitBoxArea.x = targetTile.changeLocation.x;
            player.hitBoxArea.y = targetTile.changeLocation.y;
        }
    }

    public void render(Player player) {
        ScreenUtils.clear(0/255f, 0/255f, 7/255f, 1);

        // Update Camera //
        if(player.hitBoxArea.getMiddle().x < 336) {
            camera.position.set(336, ((int) player.hitBoxArea.y + 80), 0);
        } else if(player.hitBoxArea.getMiddle().x > 944 + (Gdx.graphics.getWidth() * (areaData.screenChunks.length - 1))) {
            camera.position.set(944 + (Gdx.graphics.getWidth() * (areaData.screenChunks.length - 1)), ((int) player.hitBoxArea.y + 80), 0);
        } else if(player.hitBoxArea.getMiddle().x >= 336) {
            camera.position.set(player.hitBoxArea.getMiddle().x, ((int) player.hitBoxArea.y + 80), 0);
        }
        camera.update();

        renderBackground(player);

        int chunkStartX = (((int) player.hitBoxArea.x) / Gdx.graphics.getWidth()) - 1;
        int chunkStartY = (((int) player.hitBoxArea.y) / Gdx.graphics.getHeight()) - 1;

        // Walls & Tiles //
        for(int y = chunkStartY; y < chunkStartY + 3; y++) {
            for(int x = chunkStartX; x < chunkStartX + 3; x++) {
                if(x >= 0 && y >= 0 && x < areaData.screenChunks.length && y < areaData.screenChunks[0].length) {
                    int xLoc = x * Gdx.graphics.getWidth();
                    int yLoc = y * Gdx.graphics.getHeight();
                    
                    spriteBatch.setProjectionMatrix(camera.combined);

                    spriteBatch.begin();
                    spriteBatch.draw(areaData.screenChunks[x][y].frameBufferWalls.getColorBufferTexture(), xLoc, yLoc, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1, 1);
                    spriteBatch.draw(areaData.screenChunks[x][y].frameBufferTiles.getColorBufferTexture(), xLoc, yLoc, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1, 1);
                    spriteBatch.end();
                }
            }
        }

        // Animations //
        for(int y = chunkStartY; y < chunkStartY + 3; y++) {
            for(int x = chunkStartX; x < chunkStartX + 3; x++) {
                if(x >= 0 && y >= 0 && x < areaData.screenChunks.length && y < areaData.screenChunks[0].length) {
                    int xLoc = x * Gdx.graphics.getWidth();
                    int yLoc = y * Gdx.graphics.getHeight();
                    
                    areaData.screenChunks[x][y].bufferAnimations(camera, spriteBatch, imageManager, shapeRenderer, areaData.areaTimer);

                    spriteBatch.begin();
                    spriteBatch.draw(areaData.screenChunks[x][y].frameBufferAnimation.getColorBufferTexture(), xLoc, yLoc, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1, 1);
                    spriteBatch.end();
                }
            }
        }

        if(player.displayHitBox) {
            player.renderHitBox(camera, shapeRenderer, player.facingDirection);
        }
        
        player.renderAnimatedObject(imageManager, spriteBatch, player.hitBoxArea, player.facingDirection, true);
        player.updateAnimation();

        if(player.attackCount > 0) {
            player.renderAttackHitBox(shapeRenderer);
        }

        // Foreground //
        for(int y = chunkStartY; y < chunkStartY + 3; y++) {
            for(int x = chunkStartX; x < chunkStartX + 3; x++) {
                if(x >= 0 && y >= 0 && x < areaData.screenChunks.length && y < areaData.screenChunks[0].length) {
                    int xLoc = x * Gdx.graphics.getWidth();
                    int yLoc = y * Gdx.graphics.getHeight();
                    
                    spriteBatch.begin();
                    spriteBatch.draw(areaData.screenChunks[x][y].frameBufferForeground.getColorBufferTexture(), xLoc, yLoc, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1, 1);
                    spriteBatch.end();
                }
            }
        }
        
        if(displayDebugData != 0) {
            renderDebugData(player);
        }
    }

    public void renderBackground(Player player) {
        float xPercent = (player.hitBoxArea.getMiddle().x - 336.0f) / ((Gdx.graphics.getWidth() * areaData.screenChunks.length) - 640 - 32);
        float yPercent = ((int) player.hitBoxArea.y - 0.0f) / ((Gdx.graphics.getHeight() * areaData.screenChunks[0].length));
        if(xPercent > 1) {
            xPercent = 1;
        }
        if(yPercent > 1) {
            yPercent = 1;
        }

        if(areaData.outside) {
            renderOutside(xPercent, yPercent);
        }

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        if(areaData.frameBufferBackground != null) {
            for(int i = 0; i < areaData.frameBufferBackground.size(); i++) {
                float xMod = 0;
                if(xPercent >= 0) {
                    if(i == 0) {
                        xMod = 1750 * xPercent;
                    } else if(i == 1) {
                        xMod = 1500 * xPercent;
                    } else if(i == 2) {
                        xMod = 750 * xPercent;
                    }
                }
                int xIndex = (int) ((player.hitBoxArea.x - 328) / (Gdx.graphics.getWidth() + xMod));
                float xLoc = (xIndex * Gdx.graphics.getWidth()) + xMod;

                float yMod = 0;
                if(player.hitBoxArea.y > (6 * 16) + 16) {
                    int playerHeight = (int) player.hitBoxArea.y - (6 * 16) - 16;
                    float heightPercent = (playerHeight / 600.0f);
                    yMod = heightPercent * 400;

                    if(i == 1) {
                        yMod -= heightPercent * 14;
                    } else if(i == 2) {
                        yMod -= heightPercent * 50;
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

    public void renderOutside(float xPercent, float yPercent) {
        Texture textureMoon = imageManager.outsideImage.get("Moon").get(0);;
        Texture textureMoonGlow = imageManager.outsideImage.get("Moon-Glow").get(0);

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        float moonXMod = 0f;
        float moonYMod = 0f;
        float cloudXMod = 0f;
        float cloudYMod = 0f;
        if(xPercent >= 0) {
            moonXMod = ((Gdx.graphics.getWidth() * areaData.screenChunks.length) - (Gdx.graphics.getWidth() * .60f)) * xPercent;
            cloudXMod = ((Gdx.graphics.getWidth() * areaData.screenChunks.length) - (Gdx.graphics.getWidth() * .80f)) * xPercent;
        }
        if(yPercent >= 0) {
            moonYMod = 1400 * yPercent;
            cloudYMod = 1400 * yPercent;
        }

        float nightPercent = areaData.nightTimer / areaData.nightTimerMax;
        float xPercentMod = nightPercent * 225;
        float yPercentMod = (float) (1 - Math.cos(Math.toRadians(nightPercent * 90))) * 20;

        float moonX = 550 + moonXMod;
        float moonY = 240 + moonYMod;

        for(Cloud cloud : areaData.cloudBackList) {
            cloud.update();
            spriteBatch.setColor(cloud.tintColor/255f, cloud.tintColor/255f, cloud.tintColor/255f, 1);
            spriteBatch.draw(imageManager.outsideImage.get("Cloud").get(cloud.num), cloud.location.x + cloudXMod + cloud.getMoveMod(), cloud.location.y + cloudYMod);
            spriteBatch.setColor(1, 1, 1, 1);
        }

        spriteBatch.draw(textureMoonGlow, moonX - xPercentMod - 46, moonY - yPercentMod - 46);
        spriteBatch.draw(textureMoon, moonX - xPercentMod, moonY - yPercentMod);

        for(Cloud cloud : areaData.cloudFrontList) {
            cloud.update();
            spriteBatch.setColor(cloud.tintColor/255f, cloud.tintColor/255f, cloud.tintColor/255f, 1);
            spriteBatch.draw(imageManager.outsideImage.get("Cloud").get(cloud.num), cloud.location.x + cloudXMod + cloud.getMoveMod(), cloud.location.y + cloudYMod);
            spriteBatch.setColor(1, 1, 1, 1);
        }

        spriteBatch.end();
    }

    public void renderDebugData(Player player) {
        spriteBatch.setProjectionMatrix(cameraDebug.combined);
        spriteBatch.begin();
        font.setColor(Color.WHITE);
        font.draw(spriteBatch, "FPS: " + String.valueOf(Gdx.graphics.getFramesPerSecond()), 1205, 767);

        if(displayDebugData == 1) {
            font.draw(spriteBatch, "Pos X: " + player.hitBoxArea.x + " (" + (player.hitBoxArea.x % Gdx.graphics.getWidth()) + ") " + " Y: " + player.hitBoxArea.y + " (" + (player.hitBoxArea.y % Gdx.graphics.getHeight()) + ") " + " Size: " + player.hitBoxArea.width + "x" + player.hitBoxArea.height, 3, 765);
            font.draw(spriteBatch, "Velocity X: " + player.velocity.x + " Y: " + player.velocity.y, 3, 750);
            font.draw(spriteBatch, "R: " + player.onRamp + " - HRB: " + player.onHalfRampBottom + " - HRT: " + player.onHalfRampTop, 3, 735);
            font.draw(spriteBatch, "Jumping: " + player.jumpCheck + " (" + player.jumpTimer + ") " + player.jumpCount + " Falling: " + player.falling, 3, 720);
            
            String attackString = " (0)";
            if(player.attackCount > 0) {
                attackString = " (" + player.attackDecayTimer + "/" + player.attackData.get(player.getCurrentAttack()).attackDecayTimerMax[player.attackCount - 1] + ")";
            }
            font.draw(spriteBatch, "Attack: " + player.attackCount + attackString + " - DK: " + player.dropKickCheck + " SJ: " + player.superJumpCheck, 3, 705);
            font.draw(spriteBatch, "Run Acc: " + player.runAcceleration + " Fly Acc: " + player.flyingAcceleration, 3, 690);
            
        }
        
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

        else if(objectClass.equals("Mob")) {
            Mob mob = (Mob) object;

            for(CellCollidables cellCollidables : getObjectCellCollidables(screenChunks, object)) {
                if(cellCollidables.mobList.contains(mob)) {
                    cellCollidables.mobList.remove(mob);
                }
    
                if(screenChunks[cellCollidables.chunkX][cellCollidables.chunkY].mobList.contains(mob)) {
                    screenChunks[cellCollidables.chunkX][cellCollidables.chunkY].mobList.remove(mob);
                }
            }
        }
    }

    public static <T> ArrayList<CellCollidables> updateObjectCellCollidables(ScreenChunk[][] screenChunks, T object, ArrayList<CellCollidables> oldCellCollidables, ArrayList<CellCollidables> newCellCollidables) {
        // Function Still Needs To Be Updated For Other Object Types //
        
        ArrayList<ScreenChunk> newChunkList = new ArrayList<>();
        for(CellCollidables newCell : newCellCollidables) {
            if(!oldCellCollidables.contains(newCell)) {
                newCell.mobList.add((Mob) object);
                
                if(!screenChunks[newCell.chunkX][newCell.chunkY].mobList.contains((Mob) object)) {
                    screenChunks[newCell.chunkX][newCell.chunkY].mobList.add((Mob) object);
                }
            }

            if(!newChunkList.contains(screenChunks[newCell.chunkX][newCell.chunkY])) {
                newChunkList.add(screenChunks[newCell.chunkX][newCell.chunkY]);
            }
        }

        ArrayList<CellCollidables> removeFromScreenChunkList = new ArrayList<>();
        for(CellCollidables oldCell : oldCellCollidables) {
            if(!newCellCollidables.contains(oldCell)) {
                if(oldCell.mobList.contains(object)) {
                    oldCell.mobList.remove(object);
                }

                if(!newChunkList.contains(screenChunks[oldCell.chunkX][oldCell.chunkY])
                && !removeFromScreenChunkList.contains(oldCell)) {
                    removeFromScreenChunkList.add(oldCell);
                }
            }
        }
        
        return removeFromScreenChunkList;
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
                xLoc = (int) breakableObject.hitBoxArea.x;
                yLoc = (int) breakableObject.hitBoxArea.y;
                objectWidth = breakableObject.hitBoxArea.width;
                objectHeight = breakableObject.hitBoxArea.height;
            } else if(objectClass.equals("Mob")) {
                mobObject = (Mob) object;
                xLoc = (int) mobObject.hitBoxArea.x;
                yLoc = (int) mobObject.hitBoxArea.y;
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

    public static AreaData getAreaData(String targetAreaName) {
        if(areaData != null && areaData.levelName.equals(targetAreaName)) {
            return areaData;
        } else if(unusedAreaData != null && unusedAreaData.containsKey(targetAreaName)) {
            return unusedAreaData.get(targetAreaName);
        }

        return null;
    }
}
