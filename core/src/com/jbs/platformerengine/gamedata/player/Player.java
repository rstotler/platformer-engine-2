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
        spriteArea = new Rect(170, 800, 16, 48);

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
        velocity.x = 0;
        if(keyboard.left && !keyboard.right) {
            velocity.x = -moveSpeed;
        } else if(!keyboard.left && keyboard.right) {
            velocity.x = moveSpeed;
        }

        // Jump Velocity //
        if(keyboard.up) {
            if(keyboard.lastDown.equals("Up") && !jumpCheck && !jumpButtonPressedCheck) {
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

        // X Collision Detection //
        for(int i = 0; i < 4; i++) {
            int heightMod = (i * 16);
            if(i == 3) {
                heightMod = (i * 16) - 1;
            }

            // X Collision Detection (Left Side) //
            if(velocity.x < 0) {
                int chunkX = (spriteArea.x - (spriteArea.width / 2)) / Gdx.graphics.getWidth();
                int chunkY = (spriteArea.y + heightMod) / Gdx.graphics.getHeight();
                if(chunkX < screenChunks.length) {
                    ScreenChunk targetChunk = screenChunks[chunkX][chunkY];

                    int targetTileX = ((spriteArea.x - (spriteArea.width / 2)) % Gdx.graphics.getWidth()) / 16;
                    int targetTileY = ((spriteArea.y + heightMod) % Gdx.graphics.getHeight()) / 16;
                    if(targetTileX >= 0 && targetTileX < 80 && targetTileY >= 0 && targetTileY < 48) {
                        Tile targetTile = targetChunk.tiles[targetTileX][targetTileY];
        
                        if(targetTile != null) {
                            spriteArea.x = (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + (spriteArea.width / 2) + 16;
                            velocity.x = 0;
                            break;
                        }
                    }
                }
            }
            
            // X Collision Detection (Right Side)
            else if(velocity.x > 0) {
                int chunkX = (spriteArea.x + (spriteArea.width / 2)) / Gdx.graphics.getWidth();
                int chunkY = (spriteArea.y + heightMod) / Gdx.graphics.getHeight();
                if(chunkX < screenChunks.length) {
                    ScreenChunk targetChunk = screenChunks[chunkX][chunkY];

                    int targetTileX = ((spriteArea.x + (spriteArea.width / 2)) % Gdx.graphics.getWidth()) / 16;
                    int targetTileY = ((spriteArea.y + heightMod) % Gdx.graphics.getHeight()) / 16;
                    if(targetTileX >= 0 && targetTileX < 80 && targetTileY >= 0 && targetTileY < 48) {
                        Tile targetTile = targetChunk.tiles[targetTileX][targetTileY];

                        if(targetTile != null) {
                            spriteArea.x = (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) - (spriteArea.width / 2);
                            velocity.x = 0;
                            break;
                        }
                    }
                }
            }
        }

        // Gravity //
        if(velocity.y > maxFallVelocity) {
            if(jumpTimer == jumpTimerMax) {
                velocity.y -= .7;
            } else {
                velocity.y -= .07;
            }
        }

        // Up/Down Movement //
        spriteArea.y += velocity.y;

        // Up Collision Detection //
        if(velocity.y > 0) {

            // Y Collision Detection (Left Side) //
            boolean yCollisionCheck = false;
            if((spriteArea.x - (spriteArea.width / 2)) < (Gdx.graphics.getWidth() * screenChunks.length)) {
                int chunkX = (spriteArea.x - (spriteArea.width / 2)) / Gdx.graphics.getWidth();
                int chunkY = (spriteArea.y + spriteArea.height) / Gdx.graphics.getHeight();
                ScreenChunk targetChunk = screenChunks[chunkX][chunkY];

                int targetTileX = ((spriteArea.x - (spriteArea.width / 2)) % Gdx.graphics.getWidth()) / 16;
                int targetTileY = ((spriteArea.y + spriteArea.height) % Gdx.graphics.getHeight()) / 16;
                if(targetTileX >= 0 && targetTileX < 80 && targetTileY >= 0 && targetTileY < 48) {
                    Tile targetTile = targetChunk.tiles[targetTileX][targetTileY];

                    if(targetTile != null) {
                        spriteArea.y = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) - spriteArea.height;
                        velocity.y = 0;
                        jumpTimer = jumpTimerMax;

                        yCollisionCheck = true;
                    }
                }
            }
            
            // Y Collision Detection (Right Side) //
            if(!yCollisionCheck && spriteArea.x + (spriteArea.width / 2) - 1 >= 0) {
                int chunkX = (spriteArea.x + (spriteArea.width / 2) - 1) / Gdx.graphics.getWidth();
                int chunkY = (spriteArea.y + spriteArea.height) / Gdx.graphics.getHeight();
                if(chunkX < screenChunks.length) {
                    ScreenChunk targetChunk = screenChunks[chunkX][chunkY];
        
                    int targetTileX = ((spriteArea.x + (spriteArea.width / 2) - 1) % Gdx.graphics.getWidth()) / 16;
                    int targetTileY = ((spriteArea.y + spriteArea.height) % Gdx.graphics.getHeight()) / 16;
                    if(targetTileX >= 0 && targetTileX < 80 && targetTileY >= 0 && targetTileY < 48){
                        Tile targetTile = targetChunk.tiles[targetTileX][targetTileY];
            
                        if(targetTile != null) {
                            spriteArea.y = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) - spriteArea.height;
                            velocity.y = 0;
                            jumpTimer = jumpTimerMax;
                        }
                    }
                }
            }
        }

        // Down Collision Detection //
        else {

            // Y Collision Detection (Left Side) //
            boolean yCollisionCheck = false;
            if((spriteArea.x - (spriteArea.width / 2)) < (Gdx.graphics.getWidth() * screenChunks.length)) {
                int chunkX = (spriteArea.x - (spriteArea.width / 2)) / Gdx.graphics.getWidth();
                int chunkY = spriteArea.y / Gdx.graphics.getHeight();
                ScreenChunk targetChunk = screenChunks[chunkX][chunkY];

                int targetTileX = ((spriteArea.x - (spriteArea.width / 2)) % Gdx.graphics.getWidth()) / 16;
                int targetTileY = (spriteArea.y % Gdx.graphics.getHeight()) / 16;
                if(targetTileX >= 0 && targetTileX < 80 && targetTileY >= 0 && targetTileY < 48) {
                    Tile targetTile = targetChunk.tiles[targetTileX][targetTileY];

                    if(targetTile != null) {
                        spriteArea.y = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 16;
                        velocity.y = 0;
                        jumpCheck = false;
                        jumpTimer = jumpTimerMax;

                        yCollisionCheck = true;
                    }
                }
            }
            
            // Y Collision Detection (Right Side) //
            if(!yCollisionCheck && spriteArea.x + (spriteArea.width / 2) - 1 >= 0) {
                int chunkX = (spriteArea.x + (spriteArea.width / 2) - 1) / Gdx.graphics.getWidth();
                int chunkY = spriteArea.y / Gdx.graphics.getHeight();
                if(chunkX < screenChunks.length) {
                    ScreenChunk targetChunk = screenChunks[chunkX][chunkY];
        
                    int targetTileX = ((spriteArea.x + (spriteArea.width / 2) - 1) % Gdx.graphics.getWidth()) / 16;
                    int targetTileY = (spriteArea.y % Gdx.graphics.getHeight()) / 16;
                    if(targetTileX >= 0 && targetTileX < 80 && targetTileY >= 0 && targetTileY < 48){
                        Tile targetTile = targetChunk.tiles[targetTileX][targetTileY];
            
                        if(targetTile != null) {
                            spriteArea.y = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 16;
                            velocity.y = 0;
                            jumpCheck = false;
                            jumpTimer = jumpTimerMax;
                        }
                    }
                }
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
