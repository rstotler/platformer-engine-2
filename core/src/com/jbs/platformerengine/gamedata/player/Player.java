package com.jbs.platformerengine.gamedata.player;

import java.util.*;

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
    String facingDirection;

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

    public boolean dropKickCheck;

    public HashMap<String, AttackData> attackData;
    public int attackCount;
    public float attackDecayTimer;

    public boolean onRamp;
    public boolean onHalfRamp;

    public Player() {
        shapeRenderer = new ShapeRenderer();
        spriteArea = new Rect(1250, 250, 16, 48);

        velocity = new PointF(0, 0);
        moveSpeed = 2;
        facingDirection = "Right";

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

        dropKickCheck = false;

        attackCount = 0;
        attackDecayTimer = 0f;
        attackData = AttackData.loadAttackData();

        onRamp = false;
        onHalfRamp = false;
    }

    public void update(Keyboard keyboard, ScreenChunk[][] screenChunks) {
        updateInput(keyboard);
        updateAttack();
        updateCollisions(screenChunks);
    }

    public void updateInput(Keyboard keyboard) {

        // Sideways Velocity //
        velocity.x = 0;
        if(keyboard.left && !keyboard.right) {
            velocity.x = -moveSpeed;
            if(facingDirection.equals("Right")) {
                facingDirection = "Left";
            }
        } else if(!keyboard.left && keyboard.right) {
            velocity.x = moveSpeed;
            if(facingDirection.equals("Left")) {
                facingDirection = "Right";
            }
        }
        if(velocity.x != 0 && keyboard.shift) {
            velocity.x *= 1.5;
        }

        // Jump Velocity //
        if(keyboard.up) {
            if((keyboard.lastDown.equals("Up") || keyboard.lastDown.equals("W"))) {
                if(jumpCount < getMaxJumpCount() && !jumpButtonPressedCheck) {
                    velocity.y = 8;
                    jumpCheck = true;
                    jumpButtonPressedCheck = true;
                    jumpTimer = 0;
                    jumpCount += 1;

                // Drop Kick (Button Press) //
                } else {
                    if(!dropKickCheck && (superJumpTimer == 0 || superJumpTimer >= superJumpTimerMax)) {
                        dropKickCheck = true;
                    }
                }

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

    public void updateAttack() {
        if(attackCount > 0) {
            attackDecayTimer += 1;
            if(attackDecayTimer >= attackData.get(getCurrentAttack()).attackDecayTimerMax[attackCount - 1]) {
                attackCount = 0;
                attackDecayTimer = 0;
            }
        }
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
                                if(targetTile.tileShape.equals("Ramp-Left")) {
                                    int tileHeightMod = (((targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16) - spriteArea.x) - 2;
                                    if(spriteArea.y < (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 1) {
                                        spriteArea.y = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 1;
                                        onRamp = true;
                                    }
                                } else if(targetTile.tileShape.equals("Ramp-Right")) {
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
                                if(targetTile.tileShape.equals("Square") && (!onRamp || i > 0)) {
                                    spriteArea.x = (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + (spriteArea.width / 2) + 16;
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Square-Half") && ((!onHalfRamp && spriteArea.y + heightMod < (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8) || i > 0)) {
                                    spriteArea.x = (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + (spriteArea.width / 2) + 16;
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Ramp-Right") && !onRamp && spriteArea.y < (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 15 && spriteArea.x > (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16) {
                                    spriteArea.x = (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + (spriteArea.width / 2) + 16;
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Ramp-Right-Half-Bottom") && !onHalfRamp && spriteArea.y < (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8) {
                                    spriteArea.x = (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + (spriteArea.width / 2) + 16;
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Ramp-Right-Half-Top") && !onRamp) {
                                    spriteArea.x = (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + (spriteArea.width / 2) + 16;
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Ramp-Left-Half-Top") && ((!onHalfRamp && spriteArea.y + heightMod < (targetTileY * 16) + 8) || i > 0)) {
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
                                if(targetTile.tileShape.equals("Ramp-Right")) {
                                    int tileHeightMod = 16 - (((targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16) - spriteArea.x);
                                    if(spriteArea.y < (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 1) {
                                        spriteArea.y = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 1;
                                        onRamp = true;
                                    }
                                } else if(targetTile.tileShape.equals("Ramp-Left")) {
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
                                if(targetTile.tileShape.equals("Square") && (!onRamp || i > 0)) {
                                    spriteArea.x = (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) - (spriteArea.width / 2);
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Square-Half") && ((!onHalfRamp && spriteArea.y + heightMod < (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8) || i > 0)) {
                                    spriteArea.x = (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) - (spriteArea.width / 2);
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Ramp-Left") && !onRamp && spriteArea.y < (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 16 && spriteArea.x < (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16)) {
                                    spriteArea.x = (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) - (spriteArea.width / 2);
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Ramp-Left-Half-Bottom") && !onHalfRamp && spriteArea.y < (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8) {
                                    spriteArea.x = (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) - (spriteArea.width / 2);
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Ramp-Right-Half-Top") && ((!onHalfRamp && spriteArea.y + heightMod < (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8) || i > 0)) {
                                    spriteArea.x = (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) - (spriteArea.width / 2);
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Ramp-Left-Half-Top") && !onRamp) {
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

        // Gravity (Or Drop Kick) //
        if(dropKickCheck) {
            velocity.y = -15;
        }
        else if(velocity.y > maxFallVelocity) {
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
                            if(!targetTile.tileShape.equals("Ceiling-Ramp-Right") || !targetTile.tileShape.equals("Ceiling-Ramp-Left")) {
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
                            if(!targetTile.tileShape.equals("Ceiling-Ramp-Right") || !targetTile.tileShape.equals("Ceiling-Ramp-Left")) {
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
                                if(targetTile.tileShape.equals("Square") || targetTile.tileShape.equals("Square-Half")) {
                                    onRamp = false;
                                    onHalfRamp = false;
                                }

                                if(targetTile.tileShape.equals("Ramp-Right")) {
                                    inRampTileCell = true;
                                    int tileHeightMod = 16 - (((targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16) - spriteArea.x);
                                    if(spriteArea.y <= (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 2) {
                                        newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 2;
                                        onRamp = true;
                                        collideCheck = true;
                                    }
                                } else if(targetTile.tileShape.equals("Ramp-Left")) {
                                    inRampTileCell = true;
                                    int tileHeightMod = (((targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16) - spriteArea.x) - 2;
                                    if(spriteArea.y <= (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 2) {
                                        newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 2;
                                        onRamp = true;
                                        collideCheck = true;
                                    }
                                } else if(targetTile.tileShape.equals("Ramp-Right-Half-Bottom")) {
                                    int tileHeightMod = (int) (8 * (1 - (((targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16) - spriteArea.x) / 16.0f)) - 1;
                                    if(spriteArea.y <= (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 1) {
                                        newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 1;
                                        onHalfRamp = true;
                                        collideCheck = true;
                                    }
                                } else if(targetTile.tileShape.equals("Ramp-Left-Half-Bottom")) {
                                    int tileHeightMod = (int) (8 - (8 * (1 - (((targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16) - spriteArea.x) / 16.0f))) - 1;
                                    if(spriteArea.y <= (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 1) {
                                        newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 1;
                                        onHalfRamp = true;
                                        collideCheck = true;
                                    }
                                } else if(targetTile.tileShape.equals("Ramp-Right-Half-Top")) {
                                    inRampTileCell = true;
                                    int tileHeightMod = (int) (8 + (8 * (1 - (((targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16) - spriteArea.x) / 16.0f)));
                                    if(spriteArea.y <= (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 1) {
                                        newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod;
                                        onRamp = true;
                                        collideCheck = true;
                                    }
                                } else if(targetTile.tileShape.equals("Ramp-Left-Half-Top")) {
                                    inRampTileCell = true;
                                    int tileHeightMod = (int) (16 - (8 * (1 - (((targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16) - spriteArea.x) / 16.0f)));
                                    if(spriteArea.y <= (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod) {
                                        newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod;
                                        onRamp = true;
                                        collideCheck = true;
                                    }
                                }

                                if(collideCheck) {
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
                                if(targetTile.tileShape.equals("Square")) {
                                    newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 16;
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Square-Half") && !onHalfRamp && spriteArea.y < (targetTileY * 16) + 8) {
                                    newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8;
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Ramp-Right")) {
                                    newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 16;
                                    onRamp = true;
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Ramp-Right-Half-Bottom") && spriteArea.x >= (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16 && spriteArea.y <= (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8) {
                                    newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8;
                                    onHalfRamp = true;
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Ramp-Right-Half-Top") && spriteArea.x >= (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16) {
                                    newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 16;
                                    onRamp = true;
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Ramp-Left-Half-Top") && !onHalfRamp && spriteArea.y <= (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8) {
                                    newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8 + 1;
                                    onRamp = true;
                                    collideCheck = true;
                                }
    
                                if(collideCheck) {
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
                                if(targetTile.tileShape.equals("Square")) {
                                    newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 16;
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Square-Half") && !onHalfRamp && spriteArea.y < (targetTileY * 16) + 8) {
                                    newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8;
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Ramp-Left")) {
                                    newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 16;
                                    onRamp = true;
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Ramp-Left-Half-Bottom") && spriteArea.x <= (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) && spriteArea.y <= (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8) {
                                    newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8;
                                    onHalfRamp = true;
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Ramp-Right-Half-Top") && !onHalfRamp && spriteArea.y <= (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8) {
                                    newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8 + 1;
                                    onRamp = true;
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Ramp-Left-Half-Top") && spriteArea.x <= (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16)) {
                                    newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 16;
                                    onRamp = true;
                                    collideCheck = true;
                                }

                                if(collideCheck) {
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

                    velocity.y = 0;
                    jumpCheck = false;
                    jumpTimer = jumpTimerMax;
                    jumpCount = 0;

                    superJumpCheck = false;
                    if(dropKickCheck) {
                        dropKickCheck = false;
                    }
                }
            }
        }
    }

    public void render(OrthographicCamera camera) {
        
        // Area Rectangle //
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(82/255f, 0/255f, 0/255f, 1f);
        shapeRenderer.rect(spriteArea.x - (spriteArea.width / 2), spriteArea.y, spriteArea.width, spriteArea.height);
        
        // X & Y (Location) //
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.circle(spriteArea.x, spriteArea.y, 2);

        // Facing Direction //
        shapeRenderer.setColor(Color.YELLOW);
        if(facingDirection.equals("Right")) {
            shapeRenderer.circle(spriteArea.x + (spriteArea.width / 2), spriteArea.y + (spriteArea.height / 2), 1);
        } else {
            shapeRenderer.circle(spriteArea.x - (spriteArea.width / 2), spriteArea.y + (spriteArea.height / 2), 1);
        }

        // X & Y (Screen Center)
        // shapeRenderer.setColor(Color.YELLOW);
        // shapeRenderer.circle(spriteArea.x, spriteArea.y + 146, 2);

        // Attack Hit Box //
        if(attackCount > 0
        && attackDecayTimer >= attackData.get(getCurrentAttack()).attackFrameStart[attackCount - 1]
        && attackDecayTimer <= attackData.get(getCurrentAttack()).attackFrameEnd[attackCount - 1]) {
            if(attackCount == 1) {
                shapeRenderer.setColor(82/255f, 0/255f, 0/255f, 1f);
            } else if(attackCount == 2) {
                shapeRenderer.setColor(92/255f, 0/255f, 0/255f, 1f);
            } else {
                shapeRenderer.setColor(102/255f, 0/255f, 0/255f, 1f);
            }
            int attackXMod = attackData.get(getCurrentAttack()).attackXMod[attackCount - 1];
            if(facingDirection.equals("Left")) {
                attackXMod -= attackData.get(getCurrentAttack()).attackWidth[attackCount - 1] + (attackData.get(getCurrentAttack()).attackXMod[attackCount - 1] * 2);
            }
            int attackYMod = attackData.get(getCurrentAttack()).attackYMod[attackCount - 1];
            int attackWidth = attackData.get(getCurrentAttack()).attackWidth[attackCount - 1];
            int attackHeight = attackData.get(getCurrentAttack()).attackHeight[attackCount - 1];
            shapeRenderer.rect(spriteArea.x + attackXMod, spriteArea.y + attackYMod, attackWidth, attackHeight);
        
        }

        shapeRenderer.end();
    }

    public int getMaxJumpCount() {
        return 2;
    }

    public void superJump() {
        if(!dropKickCheck && superJumpPercent < .05) {
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

    public void attack() {
        if(attackCount < attackData.get(getCurrentAttack()).attackDecayTimerMax.length) {
            attackCount += 1;
            attackDecayTimer = 0;
        }
    }

    public String getCurrentAttack() {
        return "Sword 01";
    }
}
