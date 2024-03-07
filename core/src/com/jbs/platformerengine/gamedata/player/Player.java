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
    int moveSpeed;

    boolean jumpCheck;
    boolean jumpButtonPressedCheck;
    int jumpTimerMax;
    int jumpTimer;
    float maxFallVelocity;

    public Player() {
        shapeRenderer = new ShapeRenderer();
        spriteArea = new Rect(100, 250, 16, 48);

        velocity = new PointF(0, 0);
        moveSpeed = 3;

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

        // Sideways Velocity //
        if(keyboard.left && !keyboard.right) {
            velocity.x = -moveSpeed;
        } else if(!keyboard.left && keyboard.right) {
            velocity.x = moveSpeed;
        }
        if(keyboard.lastUp != null && (keyboard.lastUp.equals("Left") || keyboard.lastUp.equals("Right"))) {
            velocity.x = 0;
        }

        // Jump Velocity //
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

        // Sideways Movement //
        if(velocity.x != 0) {
            spriteArea.x += velocity.x;
        }

        // Gravity //
        if(velocity.y > maxFallVelocity) {
            if(jumpTimer == jumpTimerMax) {
                velocity.y -= .7;
            } else {
                velocity.y -= .07;
            }
        }

        // X Collision Detection //
        int chunkX = (spriteArea.x - (spriteArea.width / 2)) / Gdx.graphics.getWidth();
        int chunkY = spriteArea.y / Gdx.graphics.getHeight();
        ScreenChunk targetChunk = screenChunks[chunkX][chunkY];

        if(velocity.x < 0) {
            int targetTileX = ((spriteArea.x % Gdx.graphics.getWidth()) - (spriteArea.width / 2)) / 16;
            int targetTileY = (spriteArea.y % Gdx.graphics.getHeight()) / 16;
            Tile targetTile = targetChunk.tiles[targetTileX][targetTileY];

            if(targetTile != null) {
                spriteArea.x = (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + (spriteArea.width / 2) + 16;
                velocity.x = 0;
            }
        } else if(velocity.x > 0) {
            int targetTileX = ((spriteArea.x % Gdx.graphics.getWidth()) + (spriteArea.width / 2)) / 16;
            int targetTileY = (spriteArea.y % Gdx.graphics.getHeight()) / 16;
            Tile targetTile = targetChunk.tiles[targetTileX][targetTileY];

            if(targetTile != null) {
                spriteArea.x = (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) - (spriteArea.width / 2);
                velocity.x = 0;
            }
        }

        // Up/Down Movement //
        spriteArea.y += velocity.y;

        // Y Collision Detection (Left Side) //
        boolean yCollisionCheck = false;
        chunkX = (spriteArea.x - (spriteArea.width / 2)) / Gdx.graphics.getWidth();
        chunkY = spriteArea.y / Gdx.graphics.getHeight();
        targetChunk = screenChunks[chunkX][chunkY];

        int targetTileX = ((spriteArea.x - (spriteArea.width / 2)) % Gdx.graphics.getWidth()) / 16;
        int targetTileY = (spriteArea.y % Gdx.graphics.getHeight()) / 16;
        Tile targetTile = targetChunk.tiles[targetTileX][targetTileY];

        if(targetTile != null) {
            spriteArea.y = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 16;
            velocity.y = 0;
            jumpCheck = false;
            jumpTimer = jumpTimerMax;
            yCollisionCheck = true;
        }

        // Y Collision Detection (Right Side) //
        if(!yCollisionCheck) {
            chunkX = (spriteArea.x + (spriteArea.width / 2) - 1) / Gdx.graphics.getWidth();
            chunkY = spriteArea.y / Gdx.graphics.getHeight();
            targetChunk = screenChunks[chunkX][chunkY];
    
            targetTileX = ((spriteArea.x + (spriteArea.width / 2) - 1) % Gdx.graphics.getWidth()) / 16;
            targetTileY = (spriteArea.y % Gdx.graphics.getHeight()) / 16;
            targetTile = targetChunk.tiles[targetTileX][targetTileY];
    
            if(targetTile != null) {
                spriteArea.y = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 16;
                velocity.y = 0;
                jumpCheck = false;
                jumpTimer = jumpTimerMax;
            }
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
