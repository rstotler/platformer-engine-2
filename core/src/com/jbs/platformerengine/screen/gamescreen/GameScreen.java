package com.jbs.platformerengine.screen.gamescreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import com.jbs.platformerengine.components.Keyboard;
import com.jbs.platformerengine.gamedata.player.Player;
import com.jbs.platformerengine.screen.Screen;

public class GameScreen extends Screen {
    OrthographicCamera camera;
    OrthographicCamera cameraDebug;
    Keyboard keyboard;
    ScreenChunk[][] screenChunks;
    
    public GameScreen() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        cameraDebug = new OrthographicCamera();
        cameraDebug.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        keyboard = new Keyboard();
        loadAreaDebug();
        renderChunkWalls();
    }

    public void loadAreaDebug() {
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
        
        // TestPillar //
        for(int i = 0; i < 42; i++) {
            screenChunks[0][0].tiles[10][1 + i] = new Tile("Square");
            screenChunks[0][0].tiles[11][1 + i] = new Tile("Square");
        }

        // Floor 2 Floor //
        for(int i = 0; i < screenChunks[0][1].tiles.length; i++) {
            if(i < 10 || i > 11) {
                screenChunks[0][1].tiles[i][0] = new Tile("Square");
            }
        }
    }

    public void renderChunkWalls() {
        for(int y = 0; y < screenChunks[0].length; y++) {
            for(int x = 0; x < screenChunks.length; x++) {
                screenChunks[x][y].renderChunkWalls(camera, spriteBatch);
            }
        }
    }

    public void handleInput() {
        Gdx.input.setInputProcessor(new InputAdapter() {

            @Override
            public boolean keyDown(int keyCode) {
                String key = Input.Keys.toString(keyCode);

                if(key.equals("Left") || key.equals("Right") || key.equals("Up") || key.equals("Down")) {
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
                
                if(key.equals("Left") || key.equals("Right") || key.equals("Up") || key.equals("Down")) {
                    keyboard.keyUp(key);
                }

                return true;
            }
        });
    }

    public void update(Player player) {
        player.update(keyboard, screenChunks);
    }

    public void render(Player player) {
        ScreenUtils.clear(0, 0, 0, 1);
        camera.position.set(player.spriteArea.x, (player.spriteArea.y + 146), 0);
        camera.update();
        
        renderChunks(camera, player);
        player.render(camera);

        renderDebugData(player);
    }

    public void renderChunks(OrthographicCamera camera, Player player) {
        int chunkStartX = player.spriteArea.x / Gdx.graphics.getWidth() - 1;
        int chunkStartY = player.spriteArea.y / Gdx.graphics.getHeight() - 1;

        for(int y = chunkStartY; y < chunkStartY + 3; y++) {
            for(int x = chunkStartX; x < chunkStartX + 3; x++) {
                if(x >= 0 && y >= 0 && x < screenChunks.length && y < screenChunks[0].length) {
                    screenChunks[x][y].render(camera, spriteBatch);
                }
            }
        }
    }

    public void renderDebugData(Player player) {
        spriteBatch.setProjectionMatrix(cameraDebug.combined);
        spriteBatch.begin();
        font.setColor(Color.WHITE);
        font.draw(spriteBatch, "FPS: " + String.valueOf(Gdx.graphics.getFramesPerSecond()), 1, 767);

        font.draw(spriteBatch, "Player Pos: X - " + player.spriteArea.x + " Y - " + player.spriteArea.y, 980, 767);

        spriteBatch.end();
    }
}
