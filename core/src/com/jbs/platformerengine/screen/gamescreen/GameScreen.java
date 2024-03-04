package com.jbs.platformerengine.screen.gamescreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.jbs.platformerengine.components.Keyboard;
import com.jbs.platformerengine.gamedata.player.Player;
import com.jbs.platformerengine.screen.Screen;

public class GameScreen extends Screen {
    Keyboard keyboard;
    ScreenChunk[][] screenChunks;
    
    public GameScreen() {
        keyboard = new Keyboard();
        loadScreenDebug();
    }

    public void loadScreenDebug() {
        screenChunks = new ScreenChunk[6][8];
        for(int x = 0; x < screenChunks.length; x++) {
            for(int y = 0; y < screenChunks[x].length; y++) {
                screenChunks[x][y] = new ScreenChunk();
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
        int moveSpeed = 5;
        
        if(keyboard.left && !keyboard.right) {
            player.spriteArea.x -= moveSpeed;
        } else if(!keyboard.left && keyboard.right) {
            player.spriteArea.x += moveSpeed;
        }

        if(keyboard.up && !keyboard.down) {
            player.spriteArea.y += moveSpeed;
        } else if(!keyboard.up && keyboard.down) {
            player.spriteArea.y -= moveSpeed;
        }
    }

    public void render(Player player) {
        ScreenUtils.clear(0, 0, 0, 1);
        
        renderChunks();
        player.render();

        renderDebugData(player);
    }

    public void renderChunks() {

    }

    public void renderDebugData(Player player) {
        spriteBatch.begin();
        font.setColor(Color.WHITE);
        font.draw(spriteBatch, "FPS: " + String.valueOf(Gdx.graphics.getFramesPerSecond()), 1, 767);

        font.draw(spriteBatch, "Player Pos: X - " + player.spriteArea.x + " Y - " + player.spriteArea.y, 1000, 767);

        spriteBatch.end();
    }
}
