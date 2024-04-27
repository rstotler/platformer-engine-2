package com.jbs.platformerengine.gamedata.entity.player;

import java.util.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.jbs.platformerengine.components.Keyboard;
import com.jbs.platformerengine.gamedata.Point;
import com.jbs.platformerengine.gamedata.PointF;
import com.jbs.platformerengine.gamedata.Rect;
import com.jbs.platformerengine.gamedata.entity.BreakableObject;
import com.jbs.platformerengine.screen.gamescreen.GameScreen;
import com.jbs.platformerengine.screen.gamescreen.ScreenChunk;
import com.jbs.platformerengine.screen.gamescreen.Tile;

public class Player {
    ShapeRenderer shapeRenderer;
    public Rect hitBoxArea;

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
    public boolean dropKickBounceCheck;

    public HashMap<String, AttackData> attackData;
    public int attackCount;
    public float attackDecayTimer;

    public boolean onRamp;
    public boolean onHalfRamp;
    public boolean ducking;
    public boolean falling;
    public boolean justLanded;

    public Player() {
        shapeRenderer = new ShapeRenderer();
        hitBoxArea = new Rect(1295, 650, 16, 48);

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
        dropKickBounceCheck = false;

        attackCount = 0;
        attackDecayTimer = 0f;
        attackData = AttackData.loadAttackData();

        onRamp = false;
        onHalfRamp = false;
        ducking = false;
        falling = false;
        justLanded = false;
    }

    public void update(Keyboard keyboard, ScreenChunk[][] screenChunks) {
        updateInput(keyboard);
        updateTileCollisions(screenChunks);
        updateAttack();
        updateCollidables(screenChunks);
    }

    public void updateInput(Keyboard keyboard) {

        // Sideways Velocity //
        velocity.x = 0;
        if(keyboard.left && !keyboard.right && (attackCount == 0 || inAir())) {
            if(!ducking) {
                velocity.x = -moveSpeed;
            }
            if(facingDirection.equals("Right")) {
                facingDirection = "Left";
            }
        } else if(!keyboard.left && keyboard.right && (attackCount == 0 || inAir())) {
            if(!ducking) {
                velocity.x = moveSpeed;
            }
            if(facingDirection.equals("Left")) {
                facingDirection = "Right";
            }
        }
        if(velocity.x != 0 && keyboard.shift) {
            velocity.x *= 1.5;
        }
        
        // Jump Velocity //
        if(keyboard.up || dropKickBounceCheck) {
            if(((keyboard.lastDown.equals("Up") || keyboard.lastDown.equals("W")))
            && !ducking) {
                if(jumpCount < getMaxJumpCount() && !jumpButtonPressedCheck) {
                    if(superJumpPercent < .30) {
                        jump();
                    }
                    
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
        if(keyboard.lastUp != null
        && (keyboard.lastUp.equals("Up") || keyboard.lastUp.equals("W"))
        && dropKickBounceCheck == false) {
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

    public void updateTileCollisions(ScreenChunk[][] screenChunks) {
        
        // Sideways Movement //
        if(velocity.x != 0) {
            hitBoxArea.x += velocity.x;
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
                int chunkX = hitBoxArea.getMiddle().x / Gdx.graphics.getWidth();
                int chunkY = (hitBoxArea.y + heightMod) / Gdx.graphics.getHeight();
                if(chunkX < screenChunks.length) {
                    ScreenChunk targetChunk = screenChunks[chunkX][chunkY];
                    if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length) {
                        int targetTileX = (hitBoxArea.getMiddle().x % Gdx.graphics.getWidth()) / 16;
                        int targetTileY = ((hitBoxArea.y + heightMod) % Gdx.graphics.getHeight()) / 16;
                        if(targetTileX >= 0 && targetTileX < 80 && targetTileY >= 0 && targetTileY < 48) {
                            Tile targetTile = targetChunk.tiles[targetTileX][targetTileY];
    
                            if(targetTile != null && !jumpCheck) {
                                if(targetTile.tileShape.equals("Ramp-Left")) {
                                    int tileHeightMod = (((targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16) - hitBoxArea.getMiddle().x) - 2;
                                    if(hitBoxArea.y < (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 1) {
                                        hitBoxArea.y = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 1;
                                        onRamp = true;
                                    }
                                } else if(targetTile.tileShape.equals("Ramp-Right")) {
                                    int tileHeightMod = 16 - (((targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16) - hitBoxArea.getMiddle().x);
                                    if(hitBoxArea.y < (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 2) {
                                        hitBoxArea.y = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 2;
                                        onRamp = true;
                                    }
                                }
                            }
                        }
                    }
                }

                // X Collision Detection (Left Side) //
                chunkX = hitBoxArea.x / Gdx.graphics.getWidth();
                chunkY = (hitBoxArea.y + heightMod) / Gdx.graphics.getHeight();
                if(chunkX < screenChunks.length) {
                    ScreenChunk targetChunk = screenChunks[chunkX][chunkY];
                    if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length) {
                        int targetTileX = (hitBoxArea.x % Gdx.graphics.getWidth()) / 16;
                        int targetTileY = ((hitBoxArea.y + heightMod) % Gdx.graphics.getHeight()) / 16;
                        if(targetTileX >= 0 && targetTileX < 80 && targetTileY >= 0 && targetTileY < 48) {
                            Tile targetTile = targetChunk.tiles[targetTileX][targetTileY];
                            boolean collideCheck = false;
    
                            if(targetTile != null) {
                                if(targetTile.tileShape.equals("Square") && (!onRamp || i > 0)) {
                                    hitBoxArea.x = (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16;
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Square-Half") && ((!onHalfRamp && hitBoxArea.y + heightMod < (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8) || i > 0)) {
                                    hitBoxArea.x = (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16;
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Ramp-Right") && !onRamp && hitBoxArea.y < (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 15 && hitBoxArea.getMiddle().x > (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16) {
                                    hitBoxArea.x = (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16;
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Ramp-Right-Half-Bottom") && !onHalfRamp && hitBoxArea.y < (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8) {
                                    hitBoxArea.x = (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16;
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Ramp-Right-Half-Top") && !onRamp) {
                                    hitBoxArea.x = (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16;
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Ramp-Left-Half-Top") && ((!onHalfRamp && hitBoxArea.y + heightMod < (targetTileY * 16) + 8) || i > 0)) {
                                    hitBoxArea.x = (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16;
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
                if(hitBoxArea.x < 0) {
                    hitBoxArea.x = 0;
                    velocity.x = 0;
                    break;
                }
            }
            
            // X Collision Detection (Moving Right) //
            else if(velocity.x > 0) {

                // X Collision Detection (Middle) //
                int chunkX = hitBoxArea.getMiddle().x / Gdx.graphics.getWidth();
                int chunkY = (hitBoxArea.y + heightMod) / Gdx.graphics.getHeight();
                if(chunkX < screenChunks.length) {
                    ScreenChunk targetChunk = screenChunks[chunkX][chunkY];

                    int targetTileX = (hitBoxArea.getMiddle().x % Gdx.graphics.getWidth()) / 16;
                    int targetTileY = ((hitBoxArea.y + heightMod) % Gdx.graphics.getHeight()) / 16;
                    if(targetTileX >= 0 && targetTileX < 80 && targetTileY >= 0 && targetTileY < 48) {
                        Tile targetTile = targetChunk.tiles[targetTileX][targetTileY];
                        if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length) {
                            if(targetTile != null && !jumpCheck) {
                                if(targetTile.tileShape.equals("Ramp-Right")) {
                                    int tileHeightMod = 16 - (((targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16) - hitBoxArea.getMiddle().x);
                                    if(hitBoxArea.y < (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 1) {
                                        hitBoxArea.y = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 1;
                                        onRamp = true;
                                    }
                                } else if(targetTile.tileShape.equals("Ramp-Left")) {
                                    int tileHeightMod = (((targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16) - hitBoxArea.getMiddle().x) - 2;
                                    if(hitBoxArea.y < (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 1) {
                                        hitBoxArea.y = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 1;
                                        onRamp = true;
                                    }
                                }
                            }
                        }
                    }
                }

                // X Collision Detection (Right Side) //
                chunkX = (hitBoxArea.x + hitBoxArea.width) / Gdx.graphics.getWidth();
                chunkY = (hitBoxArea.y + heightMod) / Gdx.graphics.getHeight();
                if(chunkX < screenChunks.length) {
                    ScreenChunk targetChunk = screenChunks[chunkX][chunkY];
                    if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length) {
                        int targetTileX = ((hitBoxArea.x + hitBoxArea.width) % Gdx.graphics.getWidth()) / 16;
                        int targetTileY = ((hitBoxArea.y + heightMod) % Gdx.graphics.getHeight()) / 16;
                        if(targetTileX >= 0 && targetTileX < 80 && targetTileY >= 0 && targetTileY < 48) {
                            Tile targetTile = targetChunk.tiles[targetTileX][targetTileY];
                            boolean collideCheck = false;
    
                            if(targetTile != null) {
                                if(targetTile.tileShape.equals("Square") && (!onRamp || i > 0)) {
                                    hitBoxArea.x = (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) - hitBoxArea.width;
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Square-Half") && ((!onHalfRamp && hitBoxArea.y + heightMod < (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8) || i > 0)) {
                                    hitBoxArea.x = (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) - hitBoxArea.width;
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Ramp-Left") && !onRamp && hitBoxArea.y < (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 16 && hitBoxArea.getMiddle().x < (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16)) {
                                    hitBoxArea.x = (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) - hitBoxArea.width;
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Ramp-Left-Half-Bottom") && !onHalfRamp && hitBoxArea.y < (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8) {
                                    hitBoxArea.x = (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) - hitBoxArea.width;
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Ramp-Right-Half-Top") && ((!onHalfRamp && hitBoxArea.y + heightMod < (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8) || i > 0)) {
                                    hitBoxArea.x = (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) - hitBoxArea.width;
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Ramp-Left-Half-Top") && !onRamp) {
                                    hitBoxArea.x = (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) - hitBoxArea.width;
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
                if(hitBoxArea.x + hitBoxArea.width >= (Gdx.graphics.getWidth() * screenChunks.length)) {
                    hitBoxArea.x = (Gdx.graphics.getWidth() * screenChunks.length) - hitBoxArea.width;
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
        hitBoxArea.y += velocity.y;

        // Up Collision Detection //
        if(velocity.y > 0) {
            onRamp = false;
            onHalfRamp = false;
            boolean yCollisionCheck = false;
            
            // Screen Boundary Collision //
            if(hitBoxArea.y + hitBoxArea.height >= (Gdx.graphics.getHeight() * screenChunks[0].length)) {
                hitBoxArea.y = (Gdx.graphics.getHeight() * screenChunks[0].length) - hitBoxArea.height;
                velocity.y = 0;
                jumpTimer = jumpTimerMax;
                yCollisionCheck = true;
            }
            
            // Y Collision Detection (Left Side) //
            if(!yCollisionCheck && hitBoxArea.x < (Gdx.graphics.getWidth() * screenChunks.length)) {
                int chunkX = hitBoxArea.x / Gdx.graphics.getWidth();
                int chunkY = (hitBoxArea.y + hitBoxArea.height) / Gdx.graphics.getHeight();
                ScreenChunk targetChunk = screenChunks[chunkX][chunkY];
                if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length) {
                    int targetTileX = (hitBoxArea.x % Gdx.graphics.getWidth()) / 16;
                    int targetTileY = ((hitBoxArea.y + hitBoxArea.height) % Gdx.graphics.getHeight()) / 16;
                    if(targetTileX >= 0 && targetTileX < 80 && targetTileY >= 0 && targetTileY < 48) {
                        Tile targetTile = targetChunk.tiles[targetTileX][targetTileY];
    
                        if(targetTile != null) {
                            if(!targetTile.tileShape.equals("Ceiling-Ramp-Right") || !targetTile.tileShape.equals("Ceiling-Ramp-Left")) {
                                hitBoxArea.y = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) - hitBoxArea.height;
                                velocity.y = 0;
                                jumpTimer = jumpTimerMax;
                                yCollisionCheck = true;
                            }
                        }
                    }
                }
            }
            
            // Y Collision Detection (Right Side) //
            if(!yCollisionCheck && hitBoxArea.x + hitBoxArea.width - 1 >= 0) {
                int chunkX = (hitBoxArea.x + hitBoxArea.width - 1) / Gdx.graphics.getWidth();
                int chunkY = (hitBoxArea.y + hitBoxArea.height) / Gdx.graphics.getHeight();
                if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length) {
                    ScreenChunk targetChunk = screenChunks[chunkX][chunkY];
        
                    int targetTileX = ((hitBoxArea.x + hitBoxArea.width - 1) % Gdx.graphics.getWidth()) / 16;
                    int targetTileY = ((hitBoxArea.y + hitBoxArea.height) % Gdx.graphics.getHeight()) / 16;
                    if(targetTileX >= 0 && targetTileX < 80 && targetTileY >= 0 && targetTileY < 48){
                        Tile targetTile = targetChunk.tiles[targetTileX][targetTileY];
            
                        if(targetTile != null) {
                            if(!targetTile.tileShape.equals("Ceiling-Ramp-Right") || !targetTile.tileShape.equals("Ceiling-Ramp-Left")) {
                                hitBoxArea.y = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) - hitBoxArea.height;
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
                if(i == 0) {
                    falling = true;
                }
                
                // Y Collision Detection (Middle) //
                if(hitBoxArea.getMiddle().x >= 0) {
                    int chunkX = hitBoxArea.getMiddle().x / Gdx.graphics.getWidth();
                    int chunkY = hitBoxArea.y / Gdx.graphics.getHeight();
                    if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length) {
                        ScreenChunk targetChunk = screenChunks[chunkX][chunkY];
            
                        int targetTileX = (hitBoxArea.getMiddle().x % Gdx.graphics.getWidth()) / 16;
                        int targetTileY = (hitBoxArea.y % Gdx.graphics.getHeight()) / 16;
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
                                    int tileHeightMod = 16 - (((targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16) - hitBoxArea.getMiddle().x);
                                    if(hitBoxArea.y <= (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 2) {
                                        newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 2;
                                        onRamp = true;
                                        collideCheck = true;
                                    }
                                } else if(targetTile.tileShape.equals("Ramp-Left")) {
                                    inRampTileCell = true;
                                    int tileHeightMod = (((targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16) - hitBoxArea.getMiddle().x) - 2;
                                    if(hitBoxArea.y <= (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 2) {
                                        newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 2;
                                        onRamp = true;
                                        collideCheck = true;
                                    }
                                } else if(targetTile.tileShape.equals("Ramp-Right-Half-Bottom")) {
                                    int tileHeightMod = (int) (8 * (1 - (((targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16) - hitBoxArea.getMiddle().x) / 16.0f)) - 1;
                                    if(hitBoxArea.y <= (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 1) {
                                        newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 1;
                                        onHalfRamp = true;
                                        collideCheck = true;
                                    }
                                } else if(targetTile.tileShape.equals("Ramp-Left-Half-Bottom")) {
                                    int tileHeightMod = (int) (8 - (8 * (1 - (((targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16) - hitBoxArea.getMiddle().x) / 16.0f))) - 1;
                                    if(hitBoxArea.y <= (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 1) {
                                        newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 1;
                                        onHalfRamp = true;
                                        collideCheck = true;
                                    }
                                } else if(targetTile.tileShape.equals("Ramp-Right-Half-Top")) {
                                    inRampTileCell = true;
                                    int tileHeightMod = (int) (8 + (8 * (1 - (((targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16) - hitBoxArea.getMiddle().x) / 16.0f)));
                                    if(hitBoxArea.y <= (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod + 1) {
                                        newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod;
                                        onRamp = true;
                                        collideCheck = true;
                                    }
                                } else if(targetTile.tileShape.equals("Ramp-Left-Half-Top")) {
                                    inRampTileCell = true;
                                    int tileHeightMod = (int) (16 - (8 * (1 - (((targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16) - hitBoxArea.getMiddle().x) / 16.0f)));
                                    if(hitBoxArea.y <= (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + tileHeightMod) {
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
                if(hitBoxArea.x < (Gdx.graphics.getWidth() * screenChunks.length)) {
                    int chunkX = hitBoxArea.x / Gdx.graphics.getWidth();
                    int chunkY = hitBoxArea.y / Gdx.graphics.getHeight();
                    if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length) {
                        ScreenChunk targetChunk = screenChunks[chunkX][chunkY];

                        int targetTileX = (hitBoxArea.x % Gdx.graphics.getWidth()) / 16;
                        int targetTileY = (hitBoxArea.y % Gdx.graphics.getHeight()) / 16;
                        if(targetTileX >= 0 && targetTileX < 80 && targetTileY >= 0 && targetTileY < 48) {
                            Tile targetTile = targetChunk.tiles[targetTileX][targetTileY];
                            boolean collideCheck = false;
    
                            if(targetTile != null && !inRampTileCell) {
                                if(targetTile.tileShape.equals("Square")) {
                                    newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 16;
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Square-Half") && !onHalfRamp && hitBoxArea.y < (targetTileY * 16) + 8) {
                                    newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8;
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Ramp-Right")) {
                                    newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 16;
                                    onRamp = true;
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Ramp-Right-Half-Bottom") && hitBoxArea.getMiddle().x >= (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16 && hitBoxArea.y <= (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8) {
                                    newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8;
                                    onHalfRamp = true;
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Ramp-Right-Half-Top") && hitBoxArea.getMiddle().x >= (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) + 16) {
                                    newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 16;
                                    onRamp = true;
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Ramp-Left-Half-Top") && !onHalfRamp && hitBoxArea.y <= (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8) {
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
                if(hitBoxArea.x + hitBoxArea.width - 1 >= 0) {
                    int chunkX = (hitBoxArea.x + hitBoxArea.width - 1) / Gdx.graphics.getWidth();
                    int chunkY = hitBoxArea.y / Gdx.graphics.getHeight();
                    if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length) {
                        ScreenChunk targetChunk = screenChunks[chunkX][chunkY];
            
                        int targetTileX = ((hitBoxArea.x + hitBoxArea.width - 1) % Gdx.graphics.getWidth()) / 16;
                        int targetTileY = (hitBoxArea.y % Gdx.graphics.getHeight()) / 16;
                        if(targetTileX >= 0 && targetTileX < 80 && targetTileY >= 0 && targetTileY < 48){
                            Tile targetTile = targetChunk.tiles[targetTileX][targetTileY];
                            boolean collideCheck = false;
                            
                            if(targetTile != null && !inRampTileCell) {
                                if(targetTile.tileShape.equals("Square")) {
                                    newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 16;
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Square-Half") && !onHalfRamp && hitBoxArea.y < (targetTileY * 16) + 8) {
                                    newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8;
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Ramp-Left")) {
                                    newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 16;
                                    onRamp = true;
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Ramp-Left-Half-Bottom") && hitBoxArea.getMiddle().x <= (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16) && hitBoxArea.y <= (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8) {
                                    newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8;
                                    onHalfRamp = true;
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Ramp-Right-Half-Top") && !onHalfRamp && hitBoxArea.y <= (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8) {
                                    newYLoc = (targetChunk.location.y * Gdx.graphics.getHeight()) + (targetTileY * 16) + 8 + 1;
                                    onRamp = true;
                                    collideCheck = true;
                                } else if(targetTile.tileShape.equals("Ramp-Left-Half-Top") && hitBoxArea.getMiddle().x <= (targetChunk.location.x * Gdx.graphics.getWidth()) + (targetTileX * 16)) {
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
                if(hitBoxArea.y <= 0) {
                    newYLoc = 0;
                    yCollisionCheck = true;
                }

                if(!inRampTileCell && !yCollisionCheck && i == 0) {
                    onRamp = false;
                    onHalfRamp = false;
                }
                if(newYLoc != -9999) {
                    hitBoxArea.y = newYLoc;

                    velocity.y = 0;
                    jumpCheck = false;
                    jumpTimer = jumpTimerMax;
                    jumpCount = 0;
                    falling = false;

                    superJumpCheck = false;
                    if(dropKickCheck) {
                        dropKickCheck = false;
                    }

                    if(!justLanded) {
                        justLanded = true;
                        attackCount = 0;
                        attackDecayTimer = 0f;
                    }
                }
            }
        }
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

    public void updateCollidables(ScreenChunk[][] screenChunks) {
        Rect attackRect = null;
        if(dropKickCheck) {
            attackRect = new Rect(hitBoxArea.x, hitBoxArea.y, hitBoxArea.width, 20);
        } else if(attackCount > 0) {
            attackRect = getAttackHitBox();
        }

        if(attackRect != null) {
            int chunkX = attackRect.x / Gdx.graphics.getWidth();
            int chunkY = attackRect.y / Gdx.graphics.getHeight();
            int xCellStartIndex = (attackRect.x % Gdx.graphics.getWidth()) / 64;
            int yCellStartIndex = (attackRect.y % Gdx.graphics.getHeight()) / 64;
            int xPadding = attackRect.x - ((chunkX * Gdx.graphics.getWidth()) + (xCellStartIndex * 64));
            int yPadding = attackRect.y - ((chunkY * Gdx.graphics.getHeight()) + (yCellStartIndex * 64));
            int xCellSize = ((attackRect.width + xPadding) / 64) + 1;
            int yCellSize = ((attackRect.height + yPadding) / 64) + 1;

            for(int y = 0; y < yCellSize; y++) {
                chunkY = (attackRect.y + (y * 64)) / Gdx.graphics.getHeight();
                int cellY = ((attackRect.y + (y * 64)) % Gdx.graphics.getHeight()) / 64;
                for(int x = 0; x < xCellSize; x++) {
                    chunkX = (attackRect.x + (x * 64)) / Gdx.graphics.getWidth();
                    int cellX = ((attackRect.x + (x * 64)) % Gdx.graphics.getWidth()) / 64;

                    if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length) {
                        ArrayList<BreakableObject> deleteObjectList = new ArrayList<>();
                        for(BreakableObject breakableObject : screenChunks[chunkX][chunkY].cellCollidables[cellX][cellY].breakableList) {
                            if(dropKickCheck ||
                            (attackCount > 0 && attackDecayTimer >= attackData.get(getCurrentAttack()).attackFrameStart[attackCount - 1] && attackDecayTimer < attackData.get(getCurrentAttack()).attackFrameEnd[attackCount - 1])) {
                                if(attackRect.rectCollide(breakableObject.hitBoxArea)) {
                                    deleteObjectList.add(breakableObject);
                                    
                                    if(dropKickCheck) {
                                        velocity.y = 10;
                                        jumpCheck = true;
                                        jumpCount = 1;
                                        jumpTimer = 0;
                                        dropKickCheck = false;
                                        dropKickBounceCheck = true;
                                    }

                                    if(dropKickCheck
                                    || breakableObject.imageName.contains("Torch")) {
                                        break;
                                    }
                                }     
                            }             
                        }

                        for(BreakableObject deleteObject : deleteObjectList) {
                            GameScreen.removeObjectFromCellCollidables(screenChunks, deleteObject);
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
        shapeRenderer.setColor(82/255f, 0/255f, 0/255f, 1f);
        shapeRenderer.rect(hitBoxArea.x, hitBoxArea.y, hitBoxArea.width, hitBoxArea.height);
        
        // X & Y (Location) //
        // shapeRenderer.setColor(Color.GREEN);
        // shapeRenderer.circle(hitBoxArea.x, hitBoxArea.y, 2);

        // Facing Direction //
        shapeRenderer.setColor(Color.YELLOW);
        if(facingDirection.equals("Right")) {
            shapeRenderer.circle(hitBoxArea.x + hitBoxArea.width, hitBoxArea.y + (hitBoxArea.height / 2), 1);
        } else {
            shapeRenderer.circle(hitBoxArea.x, hitBoxArea.y + (hitBoxArea.height / 2), 1);
        }

        // X & Y (Screen Center)
        // shapeRenderer.setColor(Color.YELLOW);
        // shapeRenderer.circle(hitBoxArea.x, hitBoxArea.y + 146, 2);

        // Attack Hit Box //
        if(attackCount > 0
        && attackDecayTimer >= attackData.get(getCurrentAttack()).attackFrameStart[attackCount - 1]
        && attackDecayTimer < attackData.get(getCurrentAttack()).attackFrameEnd[attackCount - 1]) {
            if(attackCount == 1) {
                shapeRenderer.setColor(82/255f, 0/255f, 0/255f, 1f);
            } else if(attackCount == 2) {
                shapeRenderer.setColor(92/255f, 0/255f, 0/255f, 1f);
            } else {
                shapeRenderer.setColor(102/255f, 0/255f, 0/255f, 1f);
            }

            Rect attackHitBox = getAttackHitBox();
            shapeRenderer.rect(attackHitBox.x, attackHitBox.y, attackHitBox.width, attackHitBox.height);
        }

        shapeRenderer.end();
    }

    public void attack() {
        if(attackCount < attackData.get(getCurrentAttack()).attackDecayTimerMax.length) {
            attackCount += 1;
            attackDecayTimer = 0;
        }
    }

    public void jump() {
        velocity.y = 8;
        jumpCheck = true;
        jumpButtonPressedCheck = true;
        jumpTimer = 0;
        jumpCount += 1;
        justLanded = false;

        dropKickBounceCheck = false;
        superJumpCheck = false;
        superJumpTimer = 0f;
        superJumpPercent = 0f;
    }

    public void superJump() {
        if(!dropKickCheck && superJumpPercent < .05) {
            superJumpCheck = true;
            superJumpTimer = 0f;
            if(jumpCount == 0) {
                jumpCount = 1;
            }

            justLanded = false;
        }
    }

    public void dash(String direction) {
        if(dashPercent < .15) {
            dashCheck = true;
            dashTimer = 0f;
            dashDirection = direction;
        }
    }

    public void duck(boolean keyDown) {
        if(keyDown && !inAir()) {
            int duckHeightDiff = 13;
            hitBoxArea.height = 48 - duckHeightDiff;
            ducking = true;
        }

        else if(ducking && !keyDown) {
            hitBoxArea.height = 48;
            ducking = false;
        }
    }

    public Rect getAttackHitBox() {
        int attackX = hitBoxArea.x + (hitBoxArea.width / 2) + attackData.get(getCurrentAttack()).attackXMod[attackCount - 1];
        if(facingDirection.equals("Left")) {
            attackX -= attackData.get(getCurrentAttack()).attackWidth[attackCount - 1] + (attackData.get(getCurrentAttack()).attackXMod[attackCount - 1] * 2);
        }
        int attackY = hitBoxArea.y + attackData.get(getCurrentAttack()).attackYMod[attackCount - 1];
        if(ducking) {
            attackY -= 13;
        }
        int attackWidth = attackData.get(getCurrentAttack()).attackWidth[attackCount - 1];
        int attackHeight = attackData.get(getCurrentAttack()).attackHeight[attackCount - 1];
        
        return new Rect(attackX, attackY, attackWidth, attackHeight);
    }

    public String getCurrentAttack() {
        return "Sword 01";
    }

    public boolean inAir() {
        return jumpCount > 0 || falling;
    }

    public int getMaxJumpCount() {
        return 2;
    }
}
