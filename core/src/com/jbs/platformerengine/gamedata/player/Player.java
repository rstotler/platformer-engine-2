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

    public PointF velocity;
    int moveSpeed;

    public boolean jumpCheck;
    public boolean jumpButtonPressedCheck;
    public int jumpTimerMax;
    public int jumpTimer;
    public int jumpCount;
    float maxFallVelocity;
    
    public boolean superJumpCheck;
    public float superJumpTimer;
    public float superJumpTimerMax;
    public float superJumpPercent;

    public boolean dashCheck;
    public float dashTimer;
    public float dashTimerMax;
    public float dashPercent;
    public String dashDirection;

    public boolean onRamp;
    public boolean onHalfRamp;

    public Player() {
        shapeRenderer = new ShapeRenderer();
        spriteArea = new Rect(100, 150, 16, 48);

        velocity = new PointF(0, 0);
        moveSpeed = 2;

        jumpCheck = false;
        jumpButtonPressedCheck = false;
        jumpTimerMax = 10;
        jumpTimer = jumpTimerMax;
        jumpCount = 0;
        maxFallVelocity = -5;

        superJumpCheck = false;
        superJumpTimer = 0f;
        superJumpTimerMax = 60f;
        superJumpPercent = 0f;

        dashCheck = false;
        dashTimer = 0f;
        dashTimerMax = 20f;
        dashPercent = 0f;

        onRamp = false;
        onHalfRamp = false;
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
        if(velocity.x != 0 && keyboard.shift) {
            velocity.x *= 1.5;
        }

        // Jump Velocity //
        if(keyboard.up) {
            if((keyboard.lastDown.equals("Up") || keyboard.lastDown.equals("W")) && jumpCount < getMaxJumpCount() && !jumpButtonPressedCheck) {
                velocity.y = 8;
                jumpCheck = true;
                jumpButtonPressedCheck = true;
                jumpTimer = 0;
                jumpCount += 1;
            } else if(jumpTimer < jumpTimerMax) {
                jumpTimer += 1;
            }
        }
        if(keyboard.lastUp != null && (keyboard.lastUp.equals("Up") || keyboard.lastUp.equals("W"))) {
            jumpTimer = jumpTimerMax;
            jumpButtonPressedCheck = false;
        }

        // Super Jump //
        if(superJumpCheck) {
            if(superJumpTimer < superJumpTimerMax) {
                superJumpTimer += 1;
                superJumpPercent = (float) (1 - Math.sin(Math.toRadians((superJumpTimer / superJumpTimerMax) * 90)));
                if(24 * superJumpPercent >= 1.25) {
                    velocity.y = 24 * superJumpPercent;
                }
            }
        }

        // Dash //
        if(dashCheck) {
            if(dashTimer < dashTimerMax) {
                dashTimer += 1;
                dashPercent = (float) (1 - Math.sin(Math.toRadians((dashTimer / dashTimerMax) * 90)));
                if(7 * dashPercent >= 1.25) {
                    int directionMod = 1;
                    if(dashDirection.equals("Left")) {
                        directionMod = -1;
                    }
                    velocity.x = (7 * dashPercent) * directionMod;
                }
            } else {
                dashCheck = false;
            }
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

            // X Collision Detection (Moving Left) //
            if(velocity.x < 0) {

                // X Collision Detection (Middle) //
                int chunkX = spriteArea.x / Gdx.graphics.getWidth();
                int chunkY = (spriteArea.y + heightMod) / Gdx.graphics.getHeight();
                if(chunkX < screenChunks.length) {
                    ScreenChunk targetChunk = screenChunks[chunkX][chunkY];
                    if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length) {
                        int targetTileX = (spriteArea.x % Gdx.graphics.getWidth()) / 16;
                        int targetTileY = ((spriteArea.y + heightMod) % Gdx.graphics.getHeight()) / 16;
                        if(targetTileX >= 0 && targetTileX < 80 && targetTileY >= 0 && targetTileY < 48) {
                            Tile targetTile = targetChunk.tiles[targetTileX][targetTileY];
    
                            if(targetTile != null && !jumpCheck) {
                                if(targetTile.type.equals("Ramp-Left")) {
                                    int tileHeightMod = (((targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16) - spriteArea.x) - 2;
                                    if(spriteArea.y < (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 1) {
                                        spriteArea.y = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 1;
                                        onRamp = true;
                                    }
                                } else if(targetTile.type.equals("Ramp-Right")) {
                                    int tileHeightMod = 16 - (((targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16) - spriteArea.x);
                                    if(spriteArea.y < (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 2) {
                                        spriteArea.y = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 2;
                                        onRamp = true;
                                    }
                                }
                            }
                        }
                    }
                }

                // X Collision Detection (Left Side) //
                chunkX = (spriteArea.x - (spriteArea.width / 2)) / Gdx.graphics.getWidth();
                chunkY = (spriteArea.y + heightMod) / Gdx.graphics.getHeight();
                if(chunkX < screenChunks.length) {
                    ScreenChunk targetChunk = screenChunks[chunkX][chunkY];
                    if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length) {
                        int targetTileX = ((spriteArea.x - (spriteArea.width / 2)) % Gdx.graphics.getWidth()) / 16;
                        int targetTileY = ((spriteArea.y + heightMod) % Gdx.graphics.getHeight()) / 16;
                        if(targetTileX >= 0 && targetTileX < 80 && targetTileY >= 0 && targetTileY < 48) {
                            Tile targetTile = targetChunk.tiles[targetTileX][targetTileY];
                            boolean collideCheck = false;
    
                            if(targetTile != null) {
                                if(targetTile.type.equals("Square") && (!onRamp || i > 0)) {
                                    spriteArea.x = (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + (spriteArea.width / 2) + 16;
                                    collideCheck = true;
                                } else if(targetTile.type.equals("Square-Half") && ((!onHalfRamp && spriteArea.y + heightMod < (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8) || i > 0)) {
                                    spriteArea.x = (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + (spriteArea.width / 2) + 16;
                                    collideCheck = true;
                                } else if(targetTile.type.equals("Ramp-Right") && !onRamp && spriteArea.y < (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 15 && spriteArea.x > (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16) {
                                    spriteArea.x = (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + (spriteArea.width / 2) + 16;
                                    collideCheck = true;
                                } else if(targetTile.type.equals("Ramp-Right-Half-Bottom") && !onHalfRamp && spriteArea.y < (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8) {
                                    spriteArea.x = (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + (spriteArea.width / 2) + 16;
                                    collideCheck = true;
                                } else if(targetTile.type.equals("Ramp-Right-Half-Top") && !onRamp) {
                                    spriteArea.x = (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + (spriteArea.width / 2) + 16;
                                    collideCheck = true;
                                } else if(targetTile.type.equals("Ramp-Left-Half-Top") && ((!onHalfRamp && spriteArea.y + heightMod < (targetTileY * 16) + 8) || i > 0)) {
                                    spriteArea.x = (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + (spriteArea.width / 2) + 16;
                                    collideCheck = true;
                                }

                                if(collideCheck) {
                                    velocity.x = 0;
                                    break;
                                }
                            }
                        }
                    }
                }
                
                // Screen Boundary Collision //
                if(spriteArea.x - (spriteArea.width / 2) < 0) {
                    spriteArea.x = (spriteArea.width / 2);
                    velocity.x = 0;
                    break;
                }
            }
            
            // X Collision Detection (Moving Right) //
            else if(velocity.x > 0) {

                // X Collision Detection (Middle) //
                int chunkX = spriteArea.x / Gdx.graphics.getWidth();
                int chunkY = (spriteArea.y + heightMod) / Gdx.graphics.getHeight();
                if(chunkX < screenChunks.length) {
                    ScreenChunk targetChunk = screenChunks[chunkX][chunkY];

                    int targetTileX = (spriteArea.x % Gdx.graphics.getWidth()) / 16;
                    int targetTileY = ((spriteArea.y + heightMod) % Gdx.graphics.getHeight()) / 16;
                    if(targetTileX >= 0 && targetTileX < 80 && targetTileY >= 0 && targetTileY < 48) {
                        Tile targetTile = targetChunk.tiles[targetTileX][targetTileY];
                        if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length) {
                            if(targetTile != null && !jumpCheck) {
                                if(targetTile.type.equals("Ramp-Right")) {
                                    int tileHeightMod = 16 - (((targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16) - spriteArea.x);
                                    if(spriteArea.y < (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 1) {
                                        spriteArea.y = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 1;
                                        onRamp = true;
                                    }
                                } else if(targetTile.type.equals("Ramp-Left")) {
                                    int tileHeightMod = (((targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16) - spriteArea.x) - 2;
                                    if(spriteArea.y < (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 1) {
                                        spriteArea.y = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 1;
                                        onRamp = true;
                                    }
                                }
                            }
                        }
                    }
                }

                // X Collision Detection (Right Side) //
                chunkX = (spriteArea.x + (spriteArea.width / 2)) / Gdx.graphics.getWidth();
                chunkY = (spriteArea.y + heightMod) / Gdx.graphics.getHeight();
                if(chunkX < screenChunks.length) {
                    ScreenChunk targetChunk = screenChunks[chunkX][chunkY];
                    if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length) {
                        int targetTileX = ((spriteArea.x + (spriteArea.width / 2)) % Gdx.graphics.getWidth()) / 16;
                        int targetTileY = ((spriteArea.y + heightMod) % Gdx.graphics.getHeight()) / 16;
                        if(targetTileX >= 0 && targetTileX < 80 && targetTileY >= 0 && targetTileY < 48) {
                            Tile targetTile = targetChunk.tiles[targetTileX][targetTileY];
                            boolean collideCheck = false;
    
                            if(targetTile != null) {
                                if(targetTile.type.equals("Square") && (!onRamp || i > 0)) {
                                    spriteArea.x = (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) - (spriteArea.width / 2);
                                    collideCheck = true;
                                } else if(targetTile.type.equals("Square-Half") && ((!onHalfRamp && spriteArea.y + heightMod < (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8) || i > 0)) {
                                    spriteArea.x = (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) - (spriteArea.width / 2);
                                    collideCheck = true;
                                } else if(targetTile.type.equals("Ramp-Left") && !onRamp && spriteArea.y < (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 16 && spriteArea.x < (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16)) {
                                    spriteArea.x = (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) - (spriteArea.width / 2);
                                    collideCheck = true;
                                } else if(targetTile.type.equals("Ramp-Left-Half-Bottom") && !onHalfRamp && spriteArea.y < (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8) {
                                    spriteArea.x = (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) - (spriteArea.width / 2);
                                    collideCheck = true;
                                } else if(targetTile.type.equals("Ramp-Right-Half-Top") && ((!onHalfRamp && spriteArea.y + heightMod < (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8) || i > 0)) {
                                    spriteArea.x = (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) - (spriteArea.width / 2);
                                    collideCheck = true;
                                } else if(targetTile.type.equals("Ramp-Left-Half-Top") && !onRamp) {
                                    spriteArea.x = (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) - (spriteArea.width / 2);
                                    collideCheck = true;
                                }

                                if(collideCheck) {
                                    velocity.x = 0;
                                    break;
                                }
                            }
                        }
                    }
                }
                
                // Screen Boundary Collision //
                if(spriteArea.x + (spriteArea.width / 2) >= (Gdx.graphics.getWidth() * screenChunks.length)) {
                    spriteArea.x = (Gdx.graphics.getWidth() * screenChunks.length) - (spriteArea.width / 2);
                    velocity.x = 0;
                    break;
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
            onRamp = false;
            onHalfRamp = false;
            boolean yCollisionCheck = false;
            
            // Screen Boundary Collision //
            if(spriteArea.y + spriteArea.height >= (Gdx.graphics.getHeight() * screenChunks[0].length)) {
                spriteArea.y = (Gdx.graphics.getHeight() * screenChunks[0].length) - spriteArea.height;
                velocity.y = 0;
                jumpTimer = jumpTimerMax;
                yCollisionCheck = true;
            }
            
            // Y Collision Detection (Left Side) //
            if(!yCollisionCheck && (spriteArea.x - (spriteArea.width / 2)) < (Gdx.graphics.getWidth() * screenChunks.length)) {
                int chunkX = (spriteArea.x - (spriteArea.width / 2)) / Gdx.graphics.getWidth();
                int chunkY = (spriteArea.y + spriteArea.height) / Gdx.graphics.getHeight();
                ScreenChunk targetChunk = screenChunks[chunkX][chunkY];
                if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length) {
                    int targetTileX = ((spriteArea.x - (spriteArea.width / 2)) % Gdx.graphics.getWidth()) / 16;
                    int targetTileY = ((spriteArea.y + spriteArea.height) % Gdx.graphics.getHeight()) / 16;
                    if(targetTileX >= 0 && targetTileX < 80 && targetTileY >= 0 && targetTileY < 48) {
                        Tile targetTile = targetChunk.tiles[targetTileX][targetTileY];
    
                        if(targetTile != null) {
                            if(!targetTile.type.equals("Ceiling-Ramp-Right") || !targetTile.type.equals("Ceiling-Ramp-Left")) {
                                spriteArea.y = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) - spriteArea.height;
                                velocity.y = 0;
                                jumpTimer = jumpTimerMax;
                                yCollisionCheck = true;
                            }
                        }
                    }
                }
            }
            
            // Y Collision Detection (Right Side) //
            if(!yCollisionCheck && spriteArea.x + (spriteArea.width / 2) - 1 >= 0) {
                int chunkX = (spriteArea.x + (spriteArea.width / 2) - 1) / Gdx.graphics.getWidth();
                int chunkY = (spriteArea.y + spriteArea.height) / Gdx.graphics.getHeight();
                if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length) {
                    ScreenChunk targetChunk = screenChunks[chunkX][chunkY];
        
                    int targetTileX = ((spriteArea.x + (spriteArea.width / 2) - 1) % Gdx.graphics.getWidth()) / 16;
                    int targetTileY = ((spriteArea.y + spriteArea.height) % Gdx.graphics.getHeight()) / 16;
                    if(targetTileX >= 0 && targetTileX < 80 && targetTileY >= 0 && targetTileY < 48){
                        Tile targetTile = targetChunk.tiles[targetTileX][targetTileY];
            
                        if(targetTile != null) {
                            if(!targetTile.type.equals("Ceiling-Ramp-Right") || !targetTile.type.equals("Ceiling-Ramp-Left")) {
                                spriteArea.y = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) - spriteArea.height;
                                velocity.y = 0;
                                jumpTimer = jumpTimerMax;
                            }
                        }
                    }
                }
            }
        }

        // Down Collision Detection //
        else {
            for(int i = 0; i < 2; i++) {
                boolean yCollisionCheck = false;
                boolean inRampTileCell = false;
                int newYLoc = -9999;
                
                // Y Collision Detection (Middle) //
                if(spriteArea.x >= 0) {
                    int chunkX = spriteArea.x / Gdx.graphics.getWidth();
                    int chunkY = spriteArea.y / Gdx.graphics.getHeight();
                    if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length) {
                        ScreenChunk targetChunk = screenChunks[chunkX][chunkY];
            
                        int targetTileX = (spriteArea.x % Gdx.graphics.getWidth()) / 16;
                        int targetTileY = (spriteArea.y % Gdx.graphics.getHeight()) / 16;
                        if(targetTileX >= 0 && targetTileX < 80 && targetTileY >= 0 && targetTileY < 48){
                            Tile targetTile = targetChunk.tiles[targetTileX][targetTileY];
                            boolean collideCheck = false;

                            if(targetTile != null) {
                                if(targetTile.type.equals("Square") || targetTile.type.equals("Square-Half")) {
                                    onRamp = false;
                                    onHalfRamp = false;
                                }

                                if(targetTile.type.equals("Ramp-Right")) {
                                    inRampTileCell = true;
                                    int tileHeightMod = 16 - (((targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16) - spriteArea.x);
                                    if(spriteArea.y <= (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 2) {
                                        newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 2;
                                        onRamp = true;
                                        collideCheck = true;
                                    }
                                } else if(targetTile.type.equals("Ramp-Left")) {
                                    inRampTileCell = true;
                                    int tileHeightMod = (((targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16) - spriteArea.x) - 2;
                                    if(spriteArea.y <= (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 2) {
                                        newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 2;
                                        onRamp = true;
                                        collideCheck = true;
                                    }
                                } else if(targetTile.type.equals("Ramp-Right-Half-Bottom")) {
                                    int tileHeightMod = (int) (8 * (1 - (((targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16) - spriteArea.x) / 16.0f)) - 1;
                                    if(spriteArea.y <= (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 1) {
                                        newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 1;
                                        onHalfRamp = true;
                                        collideCheck = true;
                                    }
                                } else if(targetTile.type.equals("Ramp-Left-Half-Bottom")) {
                                    int tileHeightMod = (int) (8 - (8 * (1 - (((targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16) - spriteArea.x) / 16.0f))) - 1;
                                    if(spriteArea.y <= (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 1) {
                                        newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 1;
                                        onHalfRamp = true;
                                        collideCheck = true;
                                    }
                                } else if(targetTile.type.equals("Ramp-Right-Half-Top")) {
                                    inRampTileCell = true;
                                    int tileHeightMod = (int) (8 + (8 * (1 - (((targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16) - spriteArea.x) / 16.0f)));
                                    if(spriteArea.y <= (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 1) {
                                        newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod;
                                        onRamp = true;
                                        collideCheck = true;
                                    }
                                } else if(targetTile.type.equals("Ramp-Left-Half-Top")) {
                                    inRampTileCell = true;
                                    int tileHeightMod = (int) (16 - (8 * (1 - (((targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16) - spriteArea.x) / 16.0f)));
                                    if(spriteArea.y <= (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod) {
                                        newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod;
                                        onRamp = true;
                                        collideCheck = true;
                                    }
                                }

                                if(collideCheck) {
                                    velocity.y = 0;
                                    jumpCheck = false;
                                    jumpTimer = jumpTimerMax;

                                    yCollisionCheck = true;
                                }
                            }
                        }
                    }
                }

                // Y Collision Detection (Left Side) //
                if((spriteArea.x - (spriteArea.width / 2)) < (Gdx.graphics.getWidth() * screenChunks.length)) {
                    int chunkX = (spriteArea.x - (spriteArea.width / 2)) / Gdx.graphics.getWidth();
                    int chunkY = spriteArea.y / Gdx.graphics.getHeight();
                    if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length) {
                        ScreenChunk targetChunk = screenChunks[chunkX][chunkY];

                        int targetTileX = ((spriteArea.x - (spriteArea.width / 2)) % Gdx.graphics.getWidth()) / 16;
                        int targetTileY = (spriteArea.y % Gdx.graphics.getHeight()) / 16;
                        if(targetTileX >= 0 && targetTileX < 80 && targetTileY >= 0 && targetTileY < 48) {
                            Tile targetTile = targetChunk.tiles[targetTileX][targetTileY];
                            boolean collideCheck = false;
    
                            if(targetTile != null && !inRampTileCell) {
                                if(targetTile.type.equals("Square")) {
                                    newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 16;
                                    collideCheck = true;
                                } else if(targetTile.type.equals("Square-Half") && !onHalfRamp && spriteArea.y < (targetTileY * 16) + 8) {
                                    newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8;
                                    collideCheck = true;
                                } else if(targetTile.type.equals("Ramp-Right")) {
                                    newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 16;
                                    onRamp = true;
                                    collideCheck = true;
                                } else if(targetTile.type.equals("Ramp-Right-Half-Bottom") && spriteArea.x >= (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16 && spriteArea.y <= (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8) {
                                    newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8;
                                    onHalfRamp = true;
                                    collideCheck = true;
                                } else if(targetTile.type.equals("Ramp-Right-Half-Top") && spriteArea.x >= (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16) {
                                    newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 16;
                                    onRamp = true;
                                    collideCheck = true;
                                } else if(targetTile.type.equals("Ramp-Left-Half-Top") && !onHalfRamp && spriteArea.y <= (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8) {
                                    newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8 + 1;
                                    onRamp = true;
                                    collideCheck = true;
                                }
    
                                if(collideCheck) {
                                    velocity.y = 0;
                                    jumpCheck = false;
                                    jumpTimer = jumpTimerMax;
                                    jumpCount = 0;
                                    superJumpCheck = false;
    
                                    yCollisionCheck = true;
                                }
                            }
                        }
                    }
                }
                
                // Y Collision Detection (Right Side) //
                if(spriteArea.x + (spriteArea.width / 2) - 1 >= 0) {
                    int chunkX = (spriteArea.x + (spriteArea.width / 2) - 1) / Gdx.graphics.getWidth();
                    int chunkY = spriteArea.y / Gdx.graphics.getHeight();
                    if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length) {
                        ScreenChunk targetChunk = screenChunks[chunkX][chunkY];
            
                        int targetTileX = ((spriteArea.x + (spriteArea.width / 2) - 1) % Gdx.graphics.getWidth()) / 16;
                        int targetTileY = (spriteArea.y % Gdx.graphics.getHeight()) / 16;
                        if(targetTileX >= 0 && targetTileX < 80 && targetTileY >= 0 && targetTileY < 48){
                            Tile targetTile = targetChunk.tiles[targetTileX][targetTileY];
                            boolean collideCheck = false;
                            
                            if(targetTile != null && !inRampTileCell) {
                                if(targetTile.type.equals("Square")) {
                                    newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 16;
                                    collideCheck = true;
                                } else if(targetTile.type.equals("Square-Half") && !onHalfRamp && spriteArea.y < (targetTileY * 16) + 8) {
                                    newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8;
                                    collideCheck = true;
                                } else if(targetTile.type.equals("Ramp-Left")) {
                                    newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 16;
                                    onRamp = true;
                                    collideCheck = true;
                                } else if(targetTile.type.equals("Ramp-Left-Half-Bottom") && spriteArea.x <= (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) && spriteArea.y <= (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8) {
                                    newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8;
                                    onHalfRamp = true;
                                    collideCheck = true;
                                } else if(targetTile.type.equals("Ramp-Right-Half-Top") && !onHalfRamp && spriteArea.y <= (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8) {
                                    newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8 + 1;
                                    onRamp = true;
                                    collideCheck = true;
                                } else if(targetTile.type.equals("Ramp-Left-Half-Top") && spriteArea.x <= (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16)) {
                                    newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 16;
                                    onRamp = true;
                                    collideCheck = true;
                                }

                                if(collideCheck) {
                                    velocity.y = 0;
                                    jumpCheck = false;
                                    jumpTimer = jumpTimerMax;
                                    jumpCount = 0;
                                    superJumpCheck = false;

                                    yCollisionCheck = true;
                                }
                            }
                        }
                    }
                }
            
                // Screen Boundary Collision //
                if(spriteArea.y <= 0) {
                    newYLoc = 0;
                    yCollisionCheck = true;
                }

                if(!inRampTileCell && !yCollisionCheck && i == 0) {
                    onRamp = false;
                    onHalfRamp = false;
                }
                if(newYLoc != -9999) {
                    spriteArea.y = newYLoc;
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

    public int getMaxJumpCount() {
        return 2;
    }

    public void superJump() {
        if(superJumpPercent < .05) {
            superJumpCheck = true;
            superJumpTimer = 0f;
            if(jumpCount == 0) {
                jumpCount = 1;
            }
        }
    }

    public void dash(String direction) {
        if(dashPercent < .15) {
            dashCheck = true;
            dashTimer = 0f;
            dashDirection = direction;
        }
    }
}
