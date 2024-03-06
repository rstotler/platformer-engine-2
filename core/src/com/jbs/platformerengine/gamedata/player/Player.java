package com.jbs.platformerengine.gamedata.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.jbs.platformerengine.components.Keyboard;
import com.jbs.platformerengine.gamedata.PointF;
import com.jbs.platformerengine.gamedata.Rect;
import com.jbs.platformerengine.screen.gamescreen.ScreenChunk;
import com.jbs.platformerengine.screen.gamescreen.Tile;

public class Player {
    ShapeRenderer shapeRenderer;
    public Rect spriteArea;

    PointF velocity;
    boolean jumpCheck;
    boolean jumpButtonPressedCheck;
    int jumpTimerMax;
    int jumpTimer;
    float maxFallVelocity;

    public Player() {
        shapeRenderer = new ShapeRenderer();
        spriteArea = new Rect(100, 250, 16, 48);

        velocity = new PointF(0, 0);
        jumpCheck = false;
        jumpButtonPressedCheck = false;
        jumpTimerMax = 10;
        jumpTimer = jumpTimerMax;
        maxFallVelocity = -7;
    }

    public void update(Keyboard keyboard, ScreenChunk[][] screenChunks) {
        updateInput(keyboard);
        updateCollisions(screenChunks);
    }

    public void updateInput(Keyboard keyboard) {
        int moveSpeed = 5;
        
        if(keyboard.left && !keyboard.right) {
            spriteArea.x -= moveSpeed;
        } else if(!keyboard.left && keyboard.right) {
            spriteArea.x += moveSpeed;
        }

        if(keyboard.up) {
            if(!jumpCheck && !jumpButtonPressedCheck) {
                velocity.y = 10;
                jumpCheck = true;
                jumpButtonPressedCheck = true;
                jumpTimer = 0;
            } else if(jumpTimer < jumpTimerMax) {
                jumpTimer += 1;
            }
        }
        if(keyboard.lastUp != null && keyboard.lastUp.equals("Up")) {
            jumpTimer = jumpTimerMax;
            jumpButtonPressedCheck = false;
        }

        keyboard.lastDown = "";
        keyboard.lastUp = "";
    }

    public void updateCollisions(ScreenChunk[][] screenChunks) {
        if(velocity.y > maxFallVelocity) {
            if(jumpTimer == jumpTimerMax) {
                velocity.y -= .7;
            } else {
                velocity.y -= .07;
            }
        }

        spriteArea.y += velocity.y;

        int chunkX = spriteArea.x / Gdx.graphics.getWidth();
        int chunkY = spriteArea.y / Gdx.graphics.getHeight();
        ScreenChunk targetChunk = screenChunks[chunkX][chunkY];

        int targetTileX = (spriteArea.x % Gdx.graphics.getWidth()) / 16;
        int targetTileY = (spriteArea.y % Gdx.graphics.getHeight()) / 16;
        Tile targetTile = targetChunk.tiles[targetTileX][targetTileY];

        if(targetTile != null) {
            spriteArea.y = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 16;
            velocity.y = 0;
            jumpCheck = false;
            jumpTimer = jumpTimerMax;
        }
    }

    public void render(OrthographicCamera camera) {
        
        // Area Rectangle //
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(spriteArea.x - (spriteArea.width / 2), spriteArea.y, spriteArea.width, spriteArea.height);
        
        // X & Y (Location) //
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.circle(spriteArea.x, spriteArea.y, 2);

        // X & Y (Screen Center)
        // shapeRenderer.setColor(Color.YELLOW);
        // shapeRenderer.circle(spriteArea.x, spriteArea.y + 146, 2);

        shapeRenderer.end();
    }
}
