package com.jbs.platformerengine.gamedata.entity.player;

import java.util.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.jbs.platformerengine.components.Keyboard;
import com.jbs.platformerengine.gamedata.CollidableObject;
import com.jbs.platformerengine.gamedata.Point;
import com.jbs.platformerengine.gamedata.PointF;
import com.jbs.platformerengine.gamedata.Rect;
import com.jbs.platformerengine.gamedata.entity.BreakableObject;
import com.jbs.platformerengine.gamedata.entity.mob.Mob;
import com.jbs.platformerengine.screen.ImageManager;
import com.jbs.platformerengine.screen.gamescreen.GameScreen;
import com.jbs.platformerengine.screen.gamescreen.ScreenChunk;
import com.jbs.platformerengine.screen.gamescreen.Tile;

public class Player extends CollidableObject {
    ShapeRenderer shapeRenderer;

    public PointF velocity;
    public int moveSpeed;
    public float runMod;
    public String facingDirection;

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
    public ArrayList<CollidableObject> hitObjectList;
    public float attackDecayTimer;

    public boolean onRamp;
    public boolean onHalfRamp;
    public boolean ducking;
    public boolean falling;
    public boolean justLanded;
    public boolean flying;

    public ArrayList<String> updateActionList;

    public int healthPoints;

    public Player(String imageName, ImageManager imageManager) {
        super(imageName, imageManager);

        shapeRenderer = new ShapeRenderer();
        hitBoxArea = new Rect(100, 490, 16, 48);

        velocity = new PointF(0, 0);
        moveSpeed = 2;
        runMod = 1.5f;
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
        dashTimerMax = 25f;
        dashPercent = 0f;

        dropKickCheck = false;
        dropKickBounceCheck = false;

        attackCount = 0;
        attackDecayTimer = 0f;
        hitObjectList = new ArrayList<>();
        attackData = AttackData.loadAttackData();

        onRamp = false;
        onHalfRamp = false;
        ducking = false;
        falling = false;
        justLanded = false;
        flying = false;

        updateActionList = new ArrayList<>();

        healthPoints = 5;
    }

    public void update(Keyboard keyboard, ScreenChunk[][] screenChunks) {
        updateInput(keyboard);
        //updateTileCollisions(screenChunks);
        updateTileCollisions2(screenChunks);
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
            velocity.x *= runMod;
        }
        
        // Jump Velocity //
        if(keyboard.up || dropKickBounceCheck) {
            if(((keyboard.lastDown.contains("Up") || keyboard.lastDown.contains("W")))
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
        if((keyboard.lastUp.contains("Up") || keyboard.lastUp.contains("W"))
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

        keyboard.lastDown.clear();
        keyboard.lastUp.clear();
    }

    public void updateTileCollisions(ScreenChunk[][] screenChunks) {
        
        // Sideways Movement (X Collision Detection) //
        if(velocity.x != 0) {
            hitBoxArea.x += (int) velocity.x;

            for(int i = 0; i < 4; i++) {
                int heightMod = (i * 16);
                if(i == 3) {
                    heightMod = (i * 16) - 1;
                }

                // X Collision Detection (Moving Left) //
                if(velocity.x < 0) {

                    // X Collision Detection (Middle) //
                    int chunkX = hitBoxArea.getMiddle().x / Gdx.graphics.getWidth();
                    int chunkY = ((int) hitBoxArea.y + heightMod) / Gdx.graphics.getHeight();
                    if(chunkX < screenChunks.length) {
                        ScreenChunk targetChunk = screenChunks[chunkX][chunkY];
                        if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length) {
                            int targetTileX = (hitBoxArea.getMiddle().x % Gdx.graphics.getWidth()) / 16;
                            int targetTileY = (((int) hitBoxArea.y + heightMod) % Gdx.graphics.getHeight()) / 16;
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
                    chunkX = (int) hitBoxArea.x / Gdx.graphics.getWidth();
                    chunkY = ((int) hitBoxArea.y + heightMod) / Gdx.graphics.getHeight();
                    if(chunkX < screenChunks.length) {
                        ScreenChunk targetChunk = screenChunks[chunkX][chunkY];
                        if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length) {
                            int targetTileX = ((int) hitBoxArea.x % Gdx.graphics.getWidth()) / 16;
                            int targetTileY = (((int) hitBoxArea.y + heightMod) % Gdx.graphics.getHeight()) / 16;
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
                                        updateActionList.add("Hit Wall");
                                        velocity.x = 0;

                                        break;
                                    }
                                }
                            }
                        }
                    }
                    
                    // Screen Boundary Collision //
                    if(hitBoxArea.x < 0) {
                        updateActionList.add("Hit Wall");
                        hitBoxArea.x = 0;
                        velocity.x = 0;
                        
                        break;
                    }
                }
                
                // X Collision Detection (Moving Right) //
                else if(velocity.x > 0) {

                    // X Collision Detection (Middle) //
                    int chunkX = hitBoxArea.getMiddle().x / Gdx.graphics.getWidth();
                    int chunkY = ((int) hitBoxArea.y + heightMod) / Gdx.graphics.getHeight();
                    if(chunkX < screenChunks.length) {
                        ScreenChunk targetChunk = screenChunks[chunkX][chunkY];

                        int targetTileX = (hitBoxArea.getMiddle().x % Gdx.graphics.getWidth()) / 16;
                        int targetTileY = (((int) hitBoxArea.y + heightMod) % Gdx.graphics.getHeight()) / 16;
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
                    chunkX = ((int) hitBoxArea.x + hitBoxArea.width) / Gdx.graphics.getWidth();
                    chunkY = ((int) hitBoxArea.y + heightMod) / Gdx.graphics.getHeight();
                    if(chunkX < screenChunks.length) {
                        ScreenChunk targetChunk = screenChunks[chunkX][chunkY];
                        if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length) {
                            int targetTileX = (((int) hitBoxArea.x + hitBoxArea.width) % Gdx.graphics.getWidth()) / 16;
                            int targetTileY = (((int) hitBoxArea.y + heightMod) % Gdx.graphics.getHeight()) / 16;
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
                                        updateActionList.add("Hit Wall");
                                        velocity.x = 0;

                                        break;
                                    }
                                }
                            }
                        }
                    }
                    
                    // Screen Boundary Collision //
                    if(hitBoxArea.x + hitBoxArea.width >= (Gdx.graphics.getWidth() * screenChunks.length)) {
                        updateActionList.add("Hit Wall");
                        hitBoxArea.x = (Gdx.graphics.getWidth() * screenChunks.length) - hitBoxArea.width;
                        velocity.x = 0;

                        break;
                    }
                }
            }
        }

        // Gravity (Or Drop Kick) //
        if(dropKickCheck) {
            velocity.y = -15;
        }
        else if(!flying && velocity.y > maxFallVelocity) {
            if(jumpTimer == jumpTimerMax) {
                velocity.y -= .7;
            } else {
                velocity.y -= .07;
            }
        }

        // Up/Down Movement //
        hitBoxArea.y += (int) velocity.y;

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
                int chunkX = (int) hitBoxArea.x / Gdx.graphics.getWidth();
                int chunkY = ((int) hitBoxArea.y + hitBoxArea.height) / Gdx.graphics.getHeight();
                ScreenChunk targetChunk = screenChunks[chunkX][chunkY];
                if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length) {
                    int targetTileX = ((int) hitBoxArea.x % Gdx.graphics.getWidth()) / 16;
                    int targetTileY = (((int) hitBoxArea.y + hitBoxArea.height) % Gdx.graphics.getHeight()) / 16;
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
                int chunkX = ((int) hitBoxArea.x + hitBoxArea.width - 1) / Gdx.graphics.getWidth();
                int chunkY = ((int) hitBoxArea.y + hitBoxArea.height) / Gdx.graphics.getHeight();
                if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length) {
                    ScreenChunk targetChunk = screenChunks[chunkX][chunkY];
        
                    int targetTileX = (((int) hitBoxArea.x + hitBoxArea.width - 1) % Gdx.graphics.getWidth()) / 16;
                    int targetTileY = (((int) hitBoxArea.y + hitBoxArea.height) % Gdx.graphics.getHeight()) / 16;
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
        else if(velocity.y <= -1) {
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
                    int chunkY = (int) hitBoxArea.y / Gdx.graphics.getHeight();
                    if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length) {
                        ScreenChunk targetChunk = screenChunks[chunkX][chunkY];
            
                        int targetTileX = (hitBoxArea.getMiddle().x % Gdx.graphics.getWidth()) / 16;
                        int targetTileY = ((int) hitBoxArea.y % Gdx.graphics.getHeight()) / 16;
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
                    int chunkX = (int) hitBoxArea.x / Gdx.graphics.getWidth();
                    int chunkY = (int) hitBoxArea.y / Gdx.graphics.getHeight();
                    if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length) {
                        ScreenChunk targetChunk = screenChunks[chunkX][chunkY];

                        int targetTileX = ((int) hitBoxArea.x % Gdx.graphics.getWidth()) / 16;
                        int targetTileY = ((int) hitBoxArea.y % Gdx.graphics.getHeight()) / 16;
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
                    int chunkX = ((int) hitBoxArea.x + hitBoxArea.width - 1) / Gdx.graphics.getWidth();
                    int chunkY = (int) hitBoxArea.y / Gdx.graphics.getHeight();
                    if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length) {
                        ScreenChunk targetChunk = screenChunks[chunkX][chunkY];
            
                        int targetTileX = (((int) hitBoxArea.x + hitBoxArea.width - 1) % Gdx.graphics.getWidth()) / 16;
                        int targetTileY = ((int) hitBoxArea.y % Gdx.graphics.getHeight()) / 16;
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

    public void updateTileCollisions2(ScreenChunk[][] screenChunks) {

        // Apply Gravity (Or Drop Kick) //
        if(dropKickCheck) {
            velocity.y = -15;
        }
        else if(!flying && velocity.y > maxFallVelocity) {
            if(jumpTimer == jumpTimerMax) {
                velocity.y -= .7;
            } else {
                velocity.y -= .07;
            }
        }
        
        // Update Collisions //
        Point targetPoint = new Point((int) (hitBoxArea.x + velocity.x), (int) (hitBoxArea.y + velocity.y));
        Point startPoint = new Point((int) hitBoxArea.x, (int) hitBoxArea.y);
        
        float xDistance = targetPoint.x - startPoint.x;
        float yDistance = targetPoint.y - startPoint.y;
        float slope = yDistance / xDistance;

        // Get UpdateCount, UpdateXMove, & UpdateYMove //
        int updateCount = -1;
        float updateXMove = -1;
        float updateYMove = -1;
        boolean xHitWallCheck = false;
        boolean yHitWallCheck = false;

        // Longer Left & Right //
        if(Math.abs(xDistance) >= Math.abs(yDistance)) {
            updateCount = ((int) Math.abs(xDistance) / 16);
            if((int) xDistance % 16 != 0) {
                updateCount += 1;
            }

            if((int) Math.abs(xDistance) < 16) {
                updateXMove = xDistance;
                updateYMove = slope * (int) xDistance;
            } else {
                updateXMove = 16;
                updateYMove = Math.abs(slope) * 16;

                if(xDistance < 0) {
                    updateXMove *= -1;
                }
                if(yDistance < 0) {
                    updateYMove *= -1;
                }
            }
        }
        
        // Longer Top & Bottom //
        else {
            updateCount = ((int) Math.abs(yDistance) / 16);
            if((int) yDistance % 16 != 0) {
                updateCount += 1;
            }

            slope = xDistance / yDistance;

            if((int) Math.abs(yDistance) < 16) {
                updateYMove = yDistance;
                updateXMove = slope * (int) yDistance;
            } else {
                updateYMove = 16;
                updateXMove = Math.abs(slope) * 16;

                if(yDistance < 0) {
                    updateYMove *= -1;
                }
                if(xDistance < 0) {
                    updateXMove *= -1;
                }
            }
        }
        
        for(int i = 0; i < updateCount; i++) {
            
            // Update X Location //
            if(i == updateCount - 1) {
                if(!xHitWallCheck) {
                    hitBoxArea.x = startPoint.x + xDistance;
                }
            } else {
                if(!xHitWallCheck) {
                    hitBoxArea.x += updateXMove;
                }
            }

            // X Collision Check //
            if(!xHitWallCheck) {
                int yCount = (hitBoxArea.height / 16) + 1;
                if(hitBoxArea.height % 16 != 0) {
                    yCount += 1;
                }

                String movingDirX = "";
                if(updateXMove < 0) {
                    movingDirX = "Left";
                } else if(updateXMove > 0) {
                    movingDirX = "Right";
                }

                for(int y = 0; y < yCount; y++) {
                    int yMod = 0;
                    if(y == yCount - 1) {
                        yMod = -1;
                    }

                    // Left Side Collision //
                    if(updateXMove < 0) {
                        int chunkX = (int) hitBoxArea.x / Gdx.graphics.getWidth();
                        int chunkY = ((int) hitBoxArea.y + (16 * y) + yMod) / Gdx.graphics.getHeight();
                        int tileX = ((int) hitBoxArea.x % Gdx.graphics.getWidth()) / 16;
                        int tileY = (((int) hitBoxArea.y + (16 * y) + yMod) % Gdx.graphics.getHeight()) / 16;
                        if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length
                        && tileX >= 0 && tileX < screenChunks[0][0].tiles.length && tileY >= 0 && tileY < screenChunks[0][0].tiles[0].length
                        && screenChunks[chunkX][chunkY].tiles[tileX][tileY] != null) {
                            xHitWallCheck = screenChunks[chunkX][chunkY].tiles[tileX][tileY].collisionCheck(this, movingDirX, y);
                            if(xHitWallCheck) {
                                break;
                            }
                        }
                    }

                    // Right Side Collision //
                    else if(updateXMove > 0) {
                        int chunkX = ((int) hitBoxArea.x + hitBoxArea.width - 1) / Gdx.graphics.getWidth();
                        int chunkY = ((int) hitBoxArea.y + (16 * y) + yMod) / Gdx.graphics.getHeight();
                        int tileX = (((int) hitBoxArea.x + hitBoxArea.width - 1) % Gdx.graphics.getWidth()) / 16;
                        int tileY = (((int) hitBoxArea.y + (16 * y) + yMod) % Gdx.graphics.getHeight()) / 16;
                        if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length
                        && tileX >= 0 && tileX < screenChunks[0][0].tiles.length && tileY >= 0 && tileY < screenChunks[0][0].tiles[0].length
                        && screenChunks[chunkX][chunkY].tiles[tileX][tileY] != null) {
                            xHitWallCheck = screenChunks[chunkX][chunkY].tiles[tileX][tileY].collisionCheck(this, movingDirX, y);
                            if(xHitWallCheck) {
                                break;
                            }
                        }
                    }

                    // Middle Collision (Ramps) //
                    if(y == 0 && updateXMove != 0) {
                        int chunkX = (int) hitBoxArea.getMiddle().x / Gdx.graphics.getWidth();
                        int chunkY = ((int) hitBoxArea.y + (16 * y) + yMod) / Gdx.graphics.getHeight();
                        int tileX = ((int) hitBoxArea.getMiddle().x % Gdx.graphics.getWidth()) / 16;
                        int tileY = (((int) hitBoxArea.y + (16 * y) + yMod) % Gdx.graphics.getHeight()) / 16;
                        if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length
                        && tileX >= 0 && tileX < screenChunks[0][0].tiles.length && tileY >= 0 && tileY < screenChunks[0][0].tiles[0].length
                        && screenChunks[chunkX][chunkY].tiles[tileX][tileY] != null) {
                            yHitWallCheck = screenChunks[chunkX][chunkY].tiles[tileX][tileY].collisionCheck(this, "Middle", y);
                        }
                    }
                }
            }

            // Update Y Location //
            if(i == updateCount - 1) {
                if(!yHitWallCheck) {
                    hitBoxArea.y = startPoint.y + yDistance;
                }
            } else {
                if(!yHitWallCheck) {
                    hitBoxArea.y += updateYMove;
                }
            }

            // Y Collision Check //
            if(!yHitWallCheck) {
                int yOffset = 0;
                String movingDirY = "";
                if(velocity.y > 0) {
                    yOffset = hitBoxArea.height - 1;
                    movingDirY = "Up";
                } else if(velocity.y < 0) {
                    movingDirY = "Down";
                }

                int yCount = 2;
                for(int y = 0; y < yCount; y++) {
                    if(y == yCount - 1) {
                        int yMod = 0;
                        if(hitBoxArea.height < 16) {
                            yMod = hitBoxArea.height;
                        } else {
                            yMod = 16;
                        }
                        if(movingDirY.equals("Up")) {
                            yMod *= -1;
                        }
                        yOffset += yMod;
                    }

                    // Left Side Collision Check //
                    if(velocity.y != 0) {
                        int chunkX = (int) hitBoxArea.x / Gdx.graphics.getWidth();
                        int chunkY = ((int) hitBoxArea.y + yOffset) / Gdx.graphics.getHeight();
                        int tileX = ((int) hitBoxArea.x % Gdx.graphics.getWidth()) / 16;
                        int tileY = (((int) hitBoxArea.y + yOffset) % Gdx.graphics.getHeight()) / 16;
                        if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length
                        && tileX >= 0 && tileX < screenChunks[0][0].tiles.length && tileY >= 0 && tileY < screenChunks[0][0].tiles[0].length
                        && screenChunks[chunkX][chunkY].tiles[tileX][tileY] != null) {
                            yHitWallCheck = screenChunks[chunkX][chunkY].tiles[tileX][tileY].collisionCheck(this, movingDirY, 0);
                            if(yHitWallCheck) {
                                break;
                            }
                        }
                    }

                    // Right Side Collision Check //
                    if(velocity.y != 0) {
                        int chunkX = ((int) hitBoxArea.x + hitBoxArea.width - 1) / Gdx.graphics.getWidth();
                        int chunkY = ((int) hitBoxArea.y + yOffset) / Gdx.graphics.getHeight();
                        int tileX = (((int) hitBoxArea.x + hitBoxArea.width - 1) % Gdx.graphics.getWidth()) / 16;
                        int tileY = (((int) hitBoxArea.y + yOffset) % Gdx.graphics.getHeight()) / 16;
                        if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length
                        && tileX >= 0 && tileX < screenChunks[0][0].tiles.length && tileY >= 0 && tileY < screenChunks[0][0].tiles[0].length
                        && screenChunks[chunkX][chunkY].tiles[tileX][tileY] != null) {
                            yHitWallCheck = screenChunks[chunkX][chunkY].tiles[tileX][tileY].collisionCheck(this, movingDirY, 1);
                            if(yHitWallCheck) {
                                break;
                            }
                        }
                    }

                    // Middle Collision Check //
                    if(velocity.y != 0) {
                        int chunkX = hitBoxArea.getMiddle().x / Gdx.graphics.getWidth();
                        int chunkY = ((int) hitBoxArea.y + yOffset) / Gdx.graphics.getHeight();
                        int tileX = (hitBoxArea.getMiddle().x % Gdx.graphics.getWidth()) / 16;
                        int tileY = (((int) hitBoxArea.y + yOffset) % Gdx.graphics.getHeight()) / 16;
                        if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length
                        && tileX >= 0 && tileX < screenChunks[0][0].tiles.length && tileY >= 0 && tileY < screenChunks[0][0].tiles[0].length
                        && screenChunks[chunkX][chunkY].tiles[tileX][tileY] != null) {
                            yHitWallCheck = screenChunks[chunkX][chunkY].tiles[tileX][tileY].collisionCheck(this, movingDirY, 2);
                            if(yHitWallCheck) {
                                break;
                            }
                        }
                    }

                    // Index Collision Check //
                    if(velocity.y != 0 && !yHitWallCheck && hitBoxArea.width > 32) {
                        int xCount = hitBoxArea.width / 16;
        
                        for(int x = 0; x < xCount; x++) {
                            int chunkX = ((int) hitBoxArea.x + 16 + (x * 16)) / Gdx.graphics.getWidth();
                            int chunkY = ((int) hitBoxArea.y + yOffset) / Gdx.graphics.getHeight();
                            int tileX = (((int) hitBoxArea.x + 16 + (x * 16)) % Gdx.graphics.getWidth()) / 16;
                            int tileY = (((int) hitBoxArea.y + yOffset) % Gdx.graphics.getHeight()) / 16;
                            if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length
                            && tileX >= 0 && tileX < screenChunks[0][0].tiles.length && tileY >= 0 && tileY < screenChunks[0][0].tiles[0].length
                            && screenChunks[chunkX][chunkY].tiles[tileX][tileY] != null) {
                                yHitWallCheck = screenChunks[chunkX][chunkY].tiles[tileX][tileY].collisionCheck(this, movingDirY, 3 + x);
                                if(yHitWallCheck) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        hitBoxArea.x = (int) hitBoxArea.x;
        hitBoxArea.y = (int) hitBoxArea.y;
    }

    public void updateAttack() {
        if(attackCount > 0) {
            attackDecayTimer += 1;
            if(attackDecayTimer >= attackData.get(getCurrentAttack()).attackDecayTimerMax[attackCount - 1]) {
                attackCount = 0;
                attackDecayTimer = 0;

                hitObjectList.clear();
            }
        }
    }

    public void updateCollidables(ScreenChunk[][] screenChunks) {
        Rect attackRect = null;
        if(dropKickCheck) {
            attackRect = new Rect((int) hitBoxArea.x, (int) hitBoxArea.y, hitBoxArea.width, 20);
        } else if(attackCount > 0) {
            attackRect = getAttackHitBox();
        }

        if(attackRect != null) {
            int chunkX = (int) attackRect.x / Gdx.graphics.getWidth();
            int chunkY = (int) attackRect.y / Gdx.graphics.getHeight();
            int xCellStartIndex = ((int) attackRect.x % Gdx.graphics.getWidth()) / 64;
            int yCellStartIndex = ((int) attackRect.y % Gdx.graphics.getHeight()) / 64;
            int xPadding = (int) attackRect.x - ((chunkX * Gdx.graphics.getWidth()) + (xCellStartIndex * 64));
            int yPadding = (int) attackRect.y - ((chunkY * Gdx.graphics.getHeight()) + (yCellStartIndex * 64));
            int xCellSize = ((attackRect.width + xPadding) / 64) + 1;
            int yCellSize = ((attackRect.height + yPadding) / 64) + 1;

            for(int y = 0; y < yCellSize; y++) {
                chunkY = ((int) attackRect.y + (y * 64)) / Gdx.graphics.getHeight();
                int cellY = (((int) attackRect.y + (y * 64)) % Gdx.graphics.getHeight()) / 64;
                for(int x = 0; x < xCellSize; x++) {
                    chunkX = ((int) attackRect.x + (x * 64)) / Gdx.graphics.getWidth();
                    int cellX = (((int) attackRect.x + (x * 64)) % Gdx.graphics.getWidth()) / 64;

                    if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length) {
                        attackCollidableObject(screenChunks[chunkX][chunkY].cellCollidables[cellX][cellY].breakableList, screenChunks, attackRect);
                        attackCollidableObject(screenChunks[chunkX][chunkY].cellCollidables[cellX][cellY].mobList, screenChunks, attackRect);
                    }
                }
            }
        }
    }

    public <T> void attackCollidableObject(ArrayList<T> objectList, ScreenChunk[][] screenChunks, Rect attackRect) {
        ArrayList<T> deleteObjectList = new ArrayList<>();
        for(T object : objectList) {
            if(dropKickCheck ||
            (attackCount > 0 && attackDecayTimer >= attackData.get(getCurrentAttack()).attackFrameStart[attackCount - 1] && attackDecayTimer < attackData.get(getCurrentAttack()).attackFrameEnd[attackCount - 1])) {
                String objectType = object.getClass().toString().substring(object.getClass().toString().lastIndexOf(".") + 1);
                CollidableObject collidableObject = null;
                if(objectType.equals("BreakableObject")) {
                    collidableObject = (BreakableObject) object;
                } else if(objectType.equals("Mob")) {
                    collidableObject = (Mob) object;
                }

                if(collidableObject != null
                && attackRect.rectCollide(collidableObject.hitBoxArea)
                && !hitObjectList.contains(collidableObject)) {
                    if(objectType.equals("Mob")) {
                        ((Mob) object).healthPoints -= 1;

                        if(!((Mob) object).enemyTargetList.contains(this)) {
                            ((Mob) object).enemyTargetList.add(0, this);
                        }
                    }

                    if(objectType.equals("BreakableObject")
                    || (objectType.equals("Mob") && ((Mob) object).healthPoints <= 0)) {
                        deleteObjectList.add(object);
                    } else if(!dropKickCheck && !hitObjectList.contains(collidableObject)) {
                        hitObjectList.add(collidableObject);
                    }

                    if(dropKickCheck
                    || (collidableObject.imageName != null && collidableObject.imageName.contains("Torch"))) {
                        if(dropKickCheck) {
                            velocity.y = 10;
                            jumpCheck = true;
                            jumpCount = 1;
                            jumpTimer = 0;
                            dropKickCheck = false;
                            dropKickBounceCheck = true;
                        }
                        
                        break;
                    }
                }     
            }             
        }

        for(T deleteObject : deleteObjectList) {
            GameScreen.removeObjectFromCellCollidables(screenChunks, deleteObject);
        }
    }

    public void render(OrthographicCamera camera) {
        
        // Area Rectangle //
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeType.Filled);

        if(getClass().toString().substring(getClass().toString().lastIndexOf(".") + 1).equals("Player")) {
            shapeRenderer.setColor(82/255f, 0/255f, 0/255f, 1f);
        } else {
            shapeRenderer.setColor(140/255f, 0/255f, 140/255f, 1f);
        }

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
        // If Combo: Can Attack After Combo Start Frame //
        // Else: Can Attack After DecayTimerMax //
        // Combos Disabled While In The Air //

        if(attackCount < attackData.get(getCurrentAttack()).attackDecayTimerMax.length) {
            if((inAir() && attackCount == 0)
            || (!inAir() && (attackCount == 0 || (attackCount < attackData.get(getCurrentAttack()).attackDecayTimerMax.length && attackDecayTimer >= attackData.get(getCurrentAttack()).attackComboStartFrame[attackCount - 1])))) {
                if(!inAir()) {
                    attackCount += 1;
                } else {
                    attackCount = 1;
                }
                
                attackDecayTimer = 0;
                hitObjectList.clear();
            }
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
        // if(dashPercent < .15) {
        if(dashPercent < .75) {
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
        float movePercent = 0f;
        int xMove = 0;
        int yMove = 0;
        if(attackDecayTimer > attackData.get(getCurrentAttack()).attackFrameStart[attackCount - 1]
        && attackDecayTimer < attackData.get(getCurrentAttack()).attackFrameEnd[attackCount - 1]) {
            movePercent = (attackDecayTimer - attackData.get(getCurrentAttack()).attackFrameStart[attackCount - 1] - 1) / (attackData.get(getCurrentAttack()).attackFrameEnd[attackCount - 1] - attackData.get(getCurrentAttack()).attackFrameStart[attackCount - 1] - 2);
            if(attackData.get(getCurrentAttack()).attackMoveWidth[attackCount - 1] != -1) {
                xMove = (int) (movePercent * attackData.get(getCurrentAttack()).attackMoveWidth[attackCount - 1]);
            }
            if(attackData.get(getCurrentAttack()).attackMoveHeight[attackCount - 1] != -1) {
                yMove = (int) (movePercent * attackData.get(getCurrentAttack()).attackMoveHeight[attackCount - 1]);
            }
        }

        int attackX = (int) hitBoxArea.x + (hitBoxArea.width / 2) + attackData.get(getCurrentAttack()).attackXMod[attackCount - 1] + xMove;
        if(facingDirection.equals("Left")) {
            attackX -= attackData.get(getCurrentAttack()).attackWidth[attackCount - 1] + (attackData.get(getCurrentAttack()).attackXMod[attackCount - 1] * 2) + xMove;
        }
        int attackY = (int) hitBoxArea.y + attackData.get(getCurrentAttack()).attackYMod[attackCount - 1] + yMove;
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

    public void land() {
        jumpCount = 0;
    }

    public void hitCeiling() {
        
    }

    public int getMaxJumpCount() {
        return 2;
    }
}
