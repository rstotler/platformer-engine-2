package com.jbs.platformerengine.screen.gamescreen;

import java.util.ArrayList;
import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.jbs.platformerengine.components.Keyboard;
import com.jbs.platformerengine.gamedata.player.Player;
import com.jbs.platformerengine.screen.ImageManager;
import com.jbs.platformerengine.screen.Screen;

public class GameScreen extends Screen {
    OrthographicCamera camera;
    OrthographicCamera cameraDebug;
    Keyboard keyboard;

    String levelName;
    ScreenChunk[][] screenChunks;
    
    ArrayList<FrameBuffer> frameBufferBackground; 
    ImageManager imageManager;
    
    public GameScreen() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        cameraDebug = new OrthographicCamera();
        cameraDebug.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        keyboard = new Keyboard();

        // loadAreaDebug();
        loadLevel01();

        loadBackgroundFrameBuffers(levelName);
        imageManager = new ImageManager(Arrays.asList(levelName));
        bufferChunks();
    }

    public void loadAreaDebug() {
        levelName = "Debug";

        screenChunks = new ScreenChunk[6][8];
        for(int y = 0; y < screenChunks[0].length; y++) {
            for(int x = 0; x < screenChunks.length; x++) {
                screenChunks[x][y] = new ScreenChunk(x, y);
            }
        }

        // Bottom Floor //
        for(int i = 0; i < screenChunks.length; i++) {
            for(int ii = 0; ii < screenChunks[0][0].tiles.length; ii++) {
                screenChunks[i][0].tiles[ii][0] = new Tile("Square");
            }
        }

        // Ceiling //
        for(int i = 0; i < screenChunks[0][0].tiles.length; i++) {
            for(int ii = 10; ii < 30; ii++) {
                screenChunks[0][0].tiles[i][ii] = new Tile("Square");
            }
        }
        for(int i = 0; i < 12; i++) {
            screenChunks[0][0].tiles[i + 9][9] = new Tile("Square");
        }
        for(int i = 0; i < 7; i++) {
            screenChunks[0][0].tiles[i + 26][9] = new Tile("Square");
        }
        for(int i = 0; i < 3; i++) {
            screenChunks[0][0].tiles[i + 22][10] = null;
        }

        // Ceiling Ramps //
        screenChunks[0][0].tiles[8][9] = new Tile("Ceiling-Ramp-Right");
        screenChunks[0][0].tiles[21][9] = new Tile("Ceiling-Ramp-Left");
        screenChunks[0][0].tiles[22][10] = new Tile("Ceiling-Ramp-Left");
        screenChunks[0][0].tiles[24][10] = new Tile("Ceiling-Ramp-Right");
        screenChunks[0][0].tiles[25][9] = new Tile("Ceiling-Ramp-Right");
        screenChunks[0][0].tiles[33][9] = new Tile("Ceiling-Ramp-Left");

        // Left Wall //
        for(int i = 1; i < 10; i++) {
            screenChunks[0][0].tiles[0][i] = new Tile("Square");
        }

        screenChunks[0][0].tiles[3][1] = new Tile("Ramp-Right");
        screenChunks[0][0].tiles[4][1] = new Tile("Square");
        screenChunks[0][0].tiles[5][1] = new Tile("Ramp-Left");
        
        screenChunks[0][0].tiles[7][1] = new Tile("Ramp-Right");
        screenChunks[0][0].tiles[8][1] = new Tile("Square");
        screenChunks[0][0].tiles[8][2] = new Tile("Square");
        screenChunks[0][0].tiles[8][3] = new Tile("Square");
        screenChunks[0][0].tiles[8][4] = new Tile("Ramp-Left");
        screenChunks[0][0].tiles[9][1] = new Tile("Ramp-Left");

        screenChunks[0][0].tiles[11][1] = new Tile("Ramp-Right");
        screenChunks[0][0].tiles[12][1] = new Tile("Square");
        screenChunks[0][0].tiles[12][2] = new Tile("Square-Half");
        screenChunks[0][0].tiles[13][1] = new Tile("Ramp-Left");

        screenChunks[0][0].tiles[15][1] = new Tile("Square");
        screenChunks[0][0].tiles[15][2] = new Tile("Square");
        screenChunks[0][0].tiles[15][3] = new Tile("Square-Half");

        screenChunks[0][0].tiles[18][1] = new Tile("Ramp-Right-Half-Bottom");
        screenChunks[0][0].tiles[19][1] = new Tile("Square-Half");
        screenChunks[0][0].tiles[20][1] = new Tile("Ramp-Left-Half-Bottom");

        screenChunks[0][0].tiles[23][1] = new Tile("Ramp-Right-Half-Top");
        screenChunks[0][0].tiles[24][1] = new Tile("Square");
        screenChunks[0][0].tiles[24][2] = new Tile("Square");
        screenChunks[0][0].tiles[24][3] = new Tile("Ramp-Left-Half-Top");
        screenChunks[0][0].tiles[25][1] = new Tile("Ramp-Left-Half-Top");

        screenChunks[0][0].tiles[28][1] = new Tile("Ramp-Right-Half-Bottom");
        screenChunks[0][0].tiles[29][1] = new Tile("Ramp-Right-Half-Top");
        screenChunks[0][0].tiles[30][1] = new Tile("Ramp-Left-Half-Top");
        screenChunks[0][0].tiles[31][1] = new Tile("Ramp-Left-Half-Bottom");

        screenChunks[0][0].tiles[34][1] = new Tile("Ramp-Right");
        screenChunks[0][0].tiles[35][1] = new Tile("Square");
        screenChunks[0][0].tiles[35][2] = new Tile("Ramp-Right");
        screenChunks[0][0].tiles[36][2] = new Tile("Square");
        screenChunks[0][0].tiles[36][3] = new Tile("Ramp-Right");
        screenChunks[0][0].tiles[37][3] = new Tile("Square");
        screenChunks[0][0].tiles[37][4] = new Tile("Ramp-Right");
        screenChunks[0][0].tiles[36][1] = new Tile("Square");
        screenChunks[0][0].tiles[37][1] = new Tile("Square");
        screenChunks[0][0].tiles[37][2] = new Tile("Square");
        
        // TestPillar //
        // for(int i = 0; i < 42; i++) {
        //     screenChunks[0][0].tiles[10][1 + i] = new Tile("Square");
        //     screenChunks[0][0].tiles[11][1 + i] = new Tile("Square");
        // }

        // Floor 2 Floor //
        for(int i = 0; i < screenChunks[0][1].tiles.length; i++) {
            if(i < 10 || i > 11) {
                screenChunks[0][1].tiles[i][0] = new Tile("Square");
            }
        }
    }

    public void loadLevel01() {
        levelName = "Level01";
        
        screenChunks = new ScreenChunk[2][2];
        for(int y = 0; y < screenChunks[0].length; y++) {
            for(int x = 0; x < screenChunks.length; x++) {
                screenChunks[x][y] = new ScreenChunk(x, y);
            }
        }

        // Bottom Floor //
        for(int chunkX = 0; chunkX < 2; chunkX++) {
            for(int y = 0; y < 7; y++) {
                for(int x = 0; x < screenChunks[0][0].tiles.length; x++) {
                    int textureNum = 2;
                    if(y == 6) {
                        textureNum = 1;
                    }
                    screenChunks[chunkX][0].tiles[x][y] = new Tile("Square", textureNum);
                }
            }
        }
    }

    public void loadBackgroundFrameBuffers(String levelName) {
        if(levelName.equals("Level01")) {
            frameBufferBackground = new ArrayList<>();

            for(FileHandle directoryHandle : Gdx.files.internal("assets/images/backgrounds").list()) {
                String directoryName = directoryHandle.toString().substring(directoryHandle.toString().lastIndexOf("/") + 1);
                if(directoryName.equals(levelName)) {
                    for(FileHandle fileHandle : Gdx.files.internal(directoryHandle.toString()).list()) {
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
    
                    break;
                }
            }
        }
    }

    public void bufferChunks() {
        for(int y = 0; y < screenChunks[0].length; y++) {
            for(int x = 0; x < screenChunks.length; x++) {
                screenChunks[x][y].bufferTiles(camera, spriteBatch, levelName, imageManager);
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

        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            clickScreen(true, "Left");
        } else if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            clickScreen(false, "Left");
        } else if(Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            clickScreen(true, "Right");
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            player.superJump();
        } else if(Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            player.dash("Left");
        } else if(Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            player.dash("Right");
        }
    }

    public void clickScreen(boolean justClicked, String targetButton) {
        float cameraZoomPercent = camera.viewportHeight / Gdx.graphics.getHeight();
        int xLoc = (int) (camera.position.x - (camera.viewportWidth / 2) + (Gdx.input.getX() * cameraZoomPercent));
        int yLoc = (int) (camera.position.y - (camera.viewportHeight / 2) + ((Gdx.graphics.getHeight() - Gdx.input.getY()) * cameraZoomPercent));
        
        if(xLoc >= 0 && yLoc >= 0) {
            int chunkX = (xLoc / 16) / screenChunks[0][0].tiles.length;
            int chunkY = (yLoc / 16) / screenChunks[0][0].tiles[0].length;
            int tileX = (xLoc / 16) % screenChunks[0][0].tiles.length;
            int tileY = (yLoc / 16) % screenChunks[0][0].tiles[0].length;

            if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length
            && tileX >= 0 && tileX < screenChunks[0][0].tiles.length && tileY >= 0 && tileY < screenChunks[0][0].tiles[0].length) {
                System.out.println(tileX + " " + tileY);

                if(targetButton.equals("Left")) {
                    if(!justClicked && screenChunks[chunkX][chunkY].tiles[tileX][tileY] == null) {
                        screenChunks[chunkX][chunkY].tiles[tileX][tileY] = new Tile("Square");
        
                        Texture texture = new Texture("images/tiles/Debug/Square.png");
                        screenChunks[chunkX][chunkY].frameBufferWalls.begin();
                        spriteBatch.begin();
                        spriteBatch.draw(texture, tileX * 16, tileY * 16);
                        spriteBatch.end();
                        screenChunks[chunkX][chunkY].frameBufferWalls.end();
                    }
                    else if(justClicked) {
                        int tileTypeIndex = 0;
                        ArrayList<String> tileTypeList = new ArrayList<>(Arrays.asList("Square", "Square-Half", "Ramp-Right", "Ramp-Left", "Ramp-Right-Half-Bottom", "Ramp-Left-Half-Bottom", "Ramp-Right-Half-Top", "Ramp-Left-Half-Top", "Ceiling-Ramp-Right", "Ceiling-Ramp-Left"));
                        if(screenChunks[chunkX][chunkY].tiles[tileX][tileY] != null) {
                            tileTypeIndex = tileTypeList.indexOf(screenChunks[chunkX][chunkY].tiles[tileX][tileY].type) + 1;
                            if(tileTypeIndex >= tileTypeList.size()) {
                                tileTypeIndex = 0;
                            }
                            screenChunks[chunkX][chunkY].tiles[tileX][tileY].type = tileTypeList.get(tileTypeIndex);
                        } else {
                            screenChunks[chunkX][chunkY].tiles[tileX][tileY] = new Tile("Square");
                        }
        
                        screenChunks[chunkX][chunkY].bufferTiles(camera, spriteBatch, levelName, imageManager);
                    }
                }
                else if(targetButton.equals("Right")) {
                    screenChunks[chunkX][chunkY].tiles[tileX][tileY] = null;
                    screenChunks[chunkX][chunkY].bufferTiles(camera, spriteBatch, levelName, imageManager);
                }
            }
        }
    }

    public void update(Player player) {
        player.update(keyboard, screenChunks);
    }

    public void render(Player player) {
        ScreenUtils.clear(0, 0, 0, 1);

        if(player.spriteArea.x < 320) {
            camera.position.set(320, (player.spriteArea.y + 80), 0);
        } else if(player.spriteArea.x > 960 + (Gdx.graphics.getWidth() * (screenChunks.length - 1))) {
            camera.position.set(960 + (Gdx.graphics.getWidth() * (screenChunks.length - 1)), (player.spriteArea.y + 80), 0);
        } else if(player.spriteArea.x >= 320) {
            camera.position.set(player.spriteArea.x, (player.spriteArea.y + 80), 0);
        }
        camera.update();

        renderBackground(player);
        renderTiles(camera, player);
        player.render(camera);

        renderDebugData(player);
    }

    public void renderBackground(Player player) {
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        float xPercent = (player.spriteArea.x - 320.0f) / ((Gdx.graphics.getWidth() * screenChunks.length) - 640);
        if(frameBufferBackground != null) {
            for(int i = 0; i < frameBufferBackground.size(); i++) {
                float xMod = 0;
                if(xPercent >= 0) {
                    if(xPercent > 1) {
                        xPercent = 1;
                    }
                    if(i == 0) {
                        xMod = 2000 * xPercent;
                    } else if(i == 1) {
                        xMod = 1500 * xPercent;
                    } else if(i == 2) {
                        xMod = 750 * xPercent;
                    }
                }
                int xIndex = (int) ((player.spriteArea.x - 320) / (Gdx.graphics.getWidth() + xMod));
                float xLoc = (xIndex * Gdx.graphics.getWidth()) + xMod;

                if(i == 0 || i == 2) {
                    spriteBatch.draw(frameBufferBackground.get(i).getColorBufferTexture(), xLoc - Gdx.graphics.getWidth(), 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1, 1);
                }
                spriteBatch.draw(frameBufferBackground.get(i).getColorBufferTexture(), xLoc, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1, 1);
                spriteBatch.draw(frameBufferBackground.get(i).getColorBufferTexture(), xLoc + Gdx.graphics.getWidth(), 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1, 1);
            }
        }

        spriteBatch.end();
    }

    public void renderTiles(OrthographicCamera camera, Player player) {
        int chunkStartX = player.spriteArea.x / Gdx.graphics.getWidth() - 1;
        int chunkStartY = player.spriteArea.y / Gdx.graphics.getHeight() - 1;

        for(int y = chunkStartY; y < chunkStartY + 3; y++) {
            for(int x = chunkStartX; x < chunkStartX + 3; x++) {
                if(x >= 0 && y >= 0 && x < screenChunks.length && y < screenChunks[0].length) {
                    screenChunks[x][y].renderTiles(camera, spriteBatch);
                }
            }
        }
    }

    public void renderDebugData(Player player) {
        spriteBatch.setProjectionMatrix(cameraDebug.combined);
        spriteBatch.begin();
        font.setColor(Color.WHITE);
        font.draw(spriteBatch, "FPS: " + String.valueOf(Gdx.graphics.getFramesPerSecond()), 1205, 767);

        font.draw(spriteBatch, "Player Pos: X - " + player.spriteArea.x + " Y - " + player.spriteArea.y, 3, 765);
        font.draw(spriteBatch, "Velocity: X - " + player.velocity.x + " Y - " + player.velocity.y, 3, 750);
        font.draw(spriteBatch, "On Ramp: " + player.onRamp + " - On Half-Ramp: " + player.onHalfRamp, 3, 735);
        font.draw(spriteBatch, "Jumping: " + player.jumpCheck + " (" + player.jumpTimer + ") " + player.jumpCount, 3, 720);

        spriteBatch.end();
    }
}
