package com.jbs.platformerengine.gamedata.entity.mob;

import java.util.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.jbs.platformerengine.gamedata.CollidableObject;
import com.jbs.platformerengine.gamedata.Point;
import com.jbs.platformerengine.gamedata.PointF;
import com.jbs.platformerengine.gamedata.Rect;
import com.jbs.platformerengine.gamedata.entity.BreakableObject;
import com.jbs.platformerengine.gamedata.entity.mob.logic.movement.*;
import com.jbs.platformerengine.gamedata.entity.player.AttackData;
import com.jbs.platformerengine.screen.ImageManager;
import com.jbs.platformerengine.screen.gamescreen.GameScreen;
import com.jbs.platformerengine.screen.gamescreen.ScreenChunk;
import com.jbs.platformerengine.screen.gamescreen.Tile;

public class Mob extends CollidableObject {
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

    public Tile onRamp;
    public Tile onHalfRampBottom;
    public Tile onHalfRampTop;

    public Tile justFellInRamp;
    public Tile fellInRampLastFrame;
    public Tile middleJustFellInRamp;
    public Tile middleFellInRampLastFrame;
    public Tile rightJustFellInRamp;
    public Tile rightFellInRampLastFrame;
    public Tile justFellInSquareHalf;
    public Tile fellInSquareHalfLastFrame;

    public boolean ducking;
    public boolean falling;
    public boolean justLanded;

    public boolean flying;

    public int healthPoints;

    public int updateTimer;
    public int updateAnimationTimer;
    public MovementPattern movementPattern;
    public ArrayList<String> updateActionList; // Hit Wall

    public ArrayList<Mob> enemyTargetList;

    public Mob(String imageName, Point location, ImageManager imageManager, boolean isPlayer) {
        super(imageName, imageManager);

        shapeRenderer = new ShapeRenderer();
        hitBoxArea = new Rect(location.x, location.y, 16, 48);

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

        onRamp = null;
        onHalfRampBottom = null;
        onHalfRampTop = null;

        justFellInRamp = null;
        fellInRampLastFrame = null;
        justFellInSquareHalf = null;
        middleJustFellInRamp = null;
        middleFellInRampLastFrame = null;
        rightJustFellInRamp = null;
        rightFellInRampLastFrame = null;

        ducking = false;
        falling = false;
        justLanded = false;
        flying = false;

        healthPoints = 5;
        
        updateTimer = -1;
        updateAnimationTimer = -1;
        movementPattern = null; 
        updateActionList = new ArrayList<>();

        enemyTargetList = new ArrayList<>();

        loadMob(imageName, isPlayer);
    }

    public void loadMob(String imageName, boolean isPlayer) {
        if(!isPlayer) {
            facingDirection = "Left";
            if(new Random().nextInt(2) == 0) {
                facingDirection = "Right";
            }
        }
        
        if(imageName.equals("Default")) {
            
        }
        
        else if(imageName.equals("Bat")) {
            hitBoxArea.width = 18;
            hitBoxArea.height = 12;
            flying = true;

            frameLength = 3;

            if(!isPlayer) {
                movementPattern = new Wander(this);
            }
        }
    }

    public void updateTileCollisions(ScreenChunk[][] screenChunks) {
        // System.out.println("---New Frame---");

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
        PointF targetPoint = new PointF(hitBoxArea.x + velocity.x, hitBoxArea.y + velocity.y);
        PointF startPoint = new PointF(hitBoxArea.x, hitBoxArea.y);
        
        float xDistance = targetPoint.x - startPoint.x;
        float yDistance = targetPoint.y - startPoint.y;
        float slope = yDistance / xDistance;

        // Get UpdateCount, UpdateXMove, & UpdateYMove //
        int updateCount = -1;
        float updateXMove = -1;
        float updateYMove = -1;
        Tile xHitWallCheck = null;
        Tile yHitWallCheck = null;
        boolean hitLevelEdge = false;

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
        
        if(velocity.x == 0) {
            hitBoxArea.x = (int) hitBoxArea.x;
        }
        if(velocity.y == 0) {
            hitBoxArea.y = (int) hitBoxArea.y;
        }

        // Collision Checks //
        for(int i = 0; i < updateCount; i++) {
            
            // Update X Location //
            if(i == updateCount - 1) {
                if(xHitWallCheck == null) {
                    hitBoxArea.x = startPoint.x + xDistance;
                }
            } else {
                if(xHitWallCheck == null) {
                    hitBoxArea.x += updateXMove;
                }
            }

            // X Collision Check //
            if(xHitWallCheck == null) {
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

                // Screen Boundary Collision Detection //
                if(hitBoxArea.x < 0) {
                    hitBoxArea.x = 0;
                    hitLevelEdge = true;
                } else if(hitBoxArea.x + hitBoxArea.width >= screenChunks.length * Gdx.graphics.getWidth()) {
                    hitBoxArea.x = (screenChunks.length * Gdx.graphics.getWidth()) - hitBoxArea.width;
                    hitLevelEdge = true;
                }
                
                // Tile Collision Detection //
                else {
                    for(int y = 0; y < yCount; y++) {
                        int yMod = 0;
                        if(y == yCount - 1) {
                            yMod = -1;
                        }
    
                        // Left Side Collision //
                        if(updateXMove < 0) {
                            Tile targetTile = ScreenChunk.getTile(screenChunks, (int) hitBoxArea.x, (int) hitBoxArea.y + (16 * y) + yMod);
                            if(targetTile != null) {
                                xHitWallCheck = targetTile.collisionCheck(screenChunks, this, movingDirX, 0, y);
                                if(xHitWallCheck != null) {
                                    // System.out.println("-Hit-");
                                    break;
                                }
                            }
                        }
    
                        // Right Side Collision //
                        else if(updateXMove > 0) {
                            Tile targetTile = ScreenChunk.getTile(screenChunks, (int) hitBoxArea.x + hitBoxArea.width - 1, (int) hitBoxArea.y + (16 * y) + yMod);
                            if(targetTile != null) {
                                xHitWallCheck = targetTile.collisionCheck(screenChunks, this, movingDirX, 1, y);
                                if(xHitWallCheck != null) {
                                    // System.out.println("-Hit-");
                                    break;
                                }
                            }
                        }
    
                        // Middle Collision (Ramps) //
                        if(y == 0 && updateXMove != 0) {
                            Tile targetTile = ScreenChunk.getTile(screenChunks, (int) hitBoxArea.getMiddle().x, (int) hitBoxArea.y + (16 * y) + yMod);
                            if(targetTile != null) {
                                yHitWallCheck = targetTile.collisionCheck(screenChunks, this, "Middle", 2, y);
                                if(yHitWallCheck != null) {
                                    // System.out.println("-(Non-Breaking) Hit-");
                                }
                            }
                        }
                    }
                }
            }

            // Update Y Location //
            if(yHitWallCheck == null && velocity.y != 0) {
                if(i == updateCount - 1) {
                    hitBoxArea.y = startPoint.y + yDistance;
                } else {
                    hitBoxArea.y += updateYMove;
                }
            }

            // Set FellInRampLastFrame Variables //
            fellInRampLastFrame = justFellInRamp;
            middleFellInRampLastFrame = middleJustFellInRamp;
            rightFellInRampLastFrame = rightJustFellInRamp;
            fellInSquareHalfLastFrame = justFellInSquareHalf;

            // JustFellInRamp, MiddleJustFellInRamp & JustFellInSquareHalf Check //
            if(velocity.y <= 0) {
                justFellInRamp = null;
                middleJustFellInRamp = null;
                rightJustFellInRamp = null;
                justFellInSquareHalf = null;

                int xCount = (hitBoxArea.width / 16) + 1;
                if(hitBoxArea.width % 16 > 0) {
                    xCount += 1;
                }
                for(int x = 0; x < xCount; x++) {
                    int xMod = x * 16;
                    if(x == xCount - 1) {
                        xMod = hitBoxArea.width - 1;
                    }
        
                    // JustFellInRamp, RightJustFellInRamp & JustFellInSquareHalf Check //
                    Tile targetTile = ScreenChunk.getTile(screenChunks, (int) hitBoxArea.x + xMod, (int) hitBoxArea.y);
                    if(targetTile != null) {
                        if(targetTile.tileShape.contains("Ramp")
                        && justFellInRamp == null) {
                            justFellInRamp = targetTile;
                        }
                        if(targetTile.tileShape.contains("Ramp")) {
                            rightJustFellInRamp = targetTile;
                        }
                        if(targetTile.tileShape.equals("Square-Half")
                        && justFellInSquareHalf == null) {
                            justFellInSquareHalf = targetTile;
                        }
                    }
                }
                if(rightJustFellInRamp != null && justFellInRamp != null
                && rightJustFellInRamp.getLocation().x < justFellInRamp.getLocation().x) {
                    rightJustFellInRamp = null;
                }

                // MiddleJustFellInRamp Check //
                Tile targetTile = ScreenChunk.getTile(screenChunks, (int) hitBoxArea.getMiddle().x, (int) hitBoxArea.y);
                if(targetTile != null
                && targetTile.tileShape.length() >= 4
                && targetTile.tileShape.substring(0, 4).equals("Ramp")) {
                    middleJustFellInRamp = targetTile;
                }

                // Remove OnRamp If Falling From Opposite Ramp //
                if((onRamp != null && fellInRampLastFrame != null && justFellInRamp == null && (fellInRampLastFrame.tileShape.equals("Ramp-Right") || fellInRampLastFrame.tileShape.equals("Ramp-Left")))
                || (onRamp != null && rightFellInRampLastFrame != null && rightJustFellInRamp == null && (rightFellInRampLastFrame.tileShape.equals("Ramp-Right") || rightFellInRampLastFrame.tileShape.equals("Ramp-Left")))) {
                    onRamp = null;
                } else if((onHalfRampBottom != null && fellInRampLastFrame != null && justFellInRamp == null && (fellInRampLastFrame.tileShape.equals("Ramp-Right-Half-Bottom") || fellInRampLastFrame.tileShape.equals("Ramp-Left-Half-Bottom")))
                || (onHalfRampBottom != null && rightFellInRampLastFrame != null && rightJustFellInRamp == null) && (rightFellInRampLastFrame.tileShape.equals("Ramp-Right-Half-Bottom") || rightFellInRampLastFrame.tileShape.equals("Ramp-Left-Half-Bottom"))) {
                    onHalfRampBottom = null;
                } else if((onHalfRampTop != null && fellInRampLastFrame != null && justFellInRamp == null && (fellInRampLastFrame.tileShape.equals("Ramp-Right-Half-Top") || fellInRampLastFrame.tileShape.equals("Ramp-Left-Half-Top")))
                || (onHalfRampTop != null && rightFellInRampLastFrame != null && rightJustFellInRamp == null) && (rightFellInRampLastFrame.tileShape.equals("Ramp-Right-Half-Top") || rightFellInRampLastFrame.tileShape.equals("Ramp-Left-Half-Top"))) {
                    onHalfRampTop = null;
                }

                // Move Player Up One Tile (MiddleFellInRampLastFrame & FellInSquareHalfLastFrame Check) //
                if((middleFellInRampLastFrame != null || fellInSquareHalfLastFrame != null)
                && onRamp == null
                && onHalfRampBottom == null
                && onHalfRampTop == null) {
                    if(middleFellInRampLastFrame != null
                    && (middleFellInRampLastFrame.getLocation().y / 16) > ((int) hitBoxArea.y / 16)) {
                        targetTile = ScreenChunk.getTile(screenChunks, (int) hitBoxArea.getMiddle().x, (int) hitBoxArea.y);
                        if(targetTile == null
                        || targetTile.getLocation().y < middleFellInRampLastFrame.getLocation().y) {
                            hitBoxArea.y = (int) hitBoxArea.y + (16 - (((int) hitBoxArea.y) % 16));
                        }
                    }
                    
                    else if(fellInSquareHalfLastFrame != null
                    && (fellInSquareHalfLastFrame.getLocation().y / 16) > ((int) hitBoxArea.y / 16)) {
                        targetTile = ScreenChunk.getTile(screenChunks, (int) hitBoxArea.getMiddle().x, (int) hitBoxArea.y);
                        if((targetTile == null
                        && !(Tile.isEmptyTile(screenChunks, (int) hitBoxArea.getMiddle().x, (int) hitBoxArea.y)) == true)

                        || (targetTile != null
                        && targetTile.getLocation().y < fellInSquareHalfLastFrame.getLocation().y)) {
                            hitBoxArea.y = (int) hitBoxArea.y + (16 - (((int) hitBoxArea.y) % 16));
                        }
                    }
                }
            }

            // Y Collision Check //
            if(yHitWallCheck == null && velocity.y != 0) {
                int yOffset = 0;
                String movingDirY = "";
                if(velocity.y > 0) {
                    yOffset = hitBoxArea.height - 1;
                    movingDirY = "Up";
                } else if(velocity.y < 0) {
                    movingDirY = "Down";
                }

                // Screen Boundary Collision Detection //
                if(hitBoxArea.y < 0) {
                    hitBoxArea.y = 0;
                } else if(hitBoxArea.y + hitBoxArea.height >= screenChunks[0].length * Gdx.graphics.getHeight()) {
                    hitBoxArea.y = (screenChunks[0].length * Gdx.graphics.getHeight()) - hitBoxArea.height;
                }
                
                // Tile Collision Detection //
                else {
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
                        
                        // Middle Collision Check //
                        if(velocity.y != 0) {
                            Tile targetTile = ScreenChunk.getTile(screenChunks, (int) hitBoxArea.getMiddle().x, (int) hitBoxArea.y + yOffset);
                            if(targetTile != null) {
                                yHitWallCheck = targetTile.collisionCheck(screenChunks, this, movingDirY, 0, y);
                                if(yHitWallCheck != null) {
                                    // System.out.println("-Hit-");
                                    break;
                                }
                            }
                        }

                        // Left Side Collision Check //
                        if(velocity.y != 0) {
                            Tile targetTile = ScreenChunk.getTile(screenChunks, (int) hitBoxArea.x, (int) hitBoxArea.y + yOffset);
                            if(targetTile != null) {
                                yHitWallCheck = targetTile.collisionCheck(screenChunks, this, movingDirY, 1, y);
                                if(yHitWallCheck != null) {
                                    // System.out.println("-Hit-");
                                    break;
                                }
                            }
                        }

                        // Right Side Collision Check //
                        if(velocity.y != 0) {
                            Tile targetTile = ScreenChunk.getTile(screenChunks, (int) hitBoxArea.x + hitBoxArea.width - 1, (int) hitBoxArea.y + yOffset);
                            if(targetTile != null) {
                                yHitWallCheck = targetTile.collisionCheck(screenChunks, this, movingDirY, 2, y);
                                if(yHitWallCheck != null) {
                                    // System.out.println("-Hit-");
                                    break;
                                }
                            }
                        }
                        
                        // Index Collision Check //
                        if(hitBoxArea.width >= 32 && velocity.y != 0 && yHitWallCheck == null) {
                            int xCount = hitBoxArea.width / 16;
            
                            for(int x = 0; x < xCount; x++) {
                                int xOffset = 16 + (x * 16);
                                if(x == xCount - 1) {
                                    xOffset -= 1;
                                }

                                Tile targetTile = ScreenChunk.getTile(screenChunks, (int) hitBoxArea.x + xOffset, (int) hitBoxArea.y + yOffset);
                                if(targetTile != null) {
                                    yHitWallCheck = targetTile.collisionCheck(screenChunks, this, movingDirY, 3 + x, y);
                                    if(yHitWallCheck != null) {
                                        // System.out.println("-Hit-");
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

                if(velocity.y < 0
                && yHitWallCheck == null) {
                    falling = true;
                }
            }
        }

        // Falling Check //
        if(falling) {
            onRamp = null;
            onHalfRampBottom = null;
            onHalfRampTop = null;
        }

        // Inside Ramp Check //
        Tile targetTile = ScreenChunk.getTile(screenChunks, (int) hitBoxArea.getMiddle().x, (int) hitBoxArea.y);
        if(targetTile != null) {
            if(targetTile.tileShape.length() >= 4
            && targetTile.tileShape.substring(0, 4).equals("Ramp")) {
                if(targetTile.collisionCheck(screenChunks, this, "Middle", 2, 0) != null) {
                    // System.out.println("-Hit-");
                }
            }
        }

        // Update Mob Movement Check //
        if(getClass().toString().substring(getClass().toString().lastIndexOf(".") + 1).equals("Mob")
        && (hitLevelEdge || xHitWallCheck != null)) {
            if(hitLevelEdge
            || xHitWallCheck.tileShape.contains("Square")
            || (flying && (xHitWallCheck.tileShape.contains("Ramp")))) {
                updateActionList.add("Hit Wall");
            }
        }
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

        shapeRenderer.rect((int) hitBoxArea.x, (int) hitBoxArea.y, hitBoxArea.width, hitBoxArea.height);
        
        // X & Y (Location) //
        // shapeRenderer.setColor(Color.GREEN);
        // shapeRenderer.circle(hitBoxArea.x, hitBoxArea.y, 2);

        // Facing Direction //
        shapeRenderer.setColor(Color.YELLOW);
        if(facingDirection.equals("Right")) {
            shapeRenderer.circle((int) hitBoxArea.x + hitBoxArea.width, (int) hitBoxArea.y + (hitBoxArea.height / 2), 1);
        } else {
            shapeRenderer.circle((int) hitBoxArea.x, (int) hitBoxArea.y + (hitBoxArea.height / 2), 1);
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
            shapeRenderer.rect((int) attackHitBox.x, (int) attackHitBox.y, attackHitBox.width, attackHitBox.height);
        }

        shapeRenderer.end();
    }

    public void changeSize(int num) {
        if(num == 1) {
            hitBoxArea.width = 16;
            hitBoxArea.height = 48;
        }

        else if(num == 2) {
            hitBoxArea.width += 1;
        }

        else if(num == 3) {
            hitBoxArea.height += 1;
        }
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

        dropKickBounceCheck = false;
        superJumpCheck = false;
        superJumpTimer = 0f;
        superJumpPercent = 0f;

        onRamp = null;
        onHalfRampBottom = null;
        onHalfRampTop = null;

        justLanded = false;
        justFellInRamp = null;
        middleJustFellInRamp = null;
    }

    public void superJump() {
        if(!ducking && !dropKickCheck && superJumpPercent < .05) {
            superJumpCheck = true;
            superJumpTimer = 0f;
            if(jumpCount == 0) {
                jumpCount = 1;
            }

            onRamp = null;
            onHalfRampBottom = null;
            onHalfRampTop = null;

            justLanded = false;
            justFellInRamp = null;
            middleJustFellInRamp = null;
        }
    }

    public void dash(String direction) {
        if(dashPercent < .75
        && superJumpPercent < .30) {
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

    public void land(Tile tile) {
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

    public void hitCeiling() {
        jumpTimer = jumpTimerMax;
    }

    public int getMaxJumpCount() {
        return 2;
    }

    public void updateVelocity(String targetAxis, float targetSpeed) {
        if(targetAxis.equals("X")) {
            if(facingDirection.equals("Left")) {
                velocity.x = -targetSpeed;
            } else {
                velocity.x = targetSpeed;
            }
        } else {
            velocity.y = targetSpeed;
        }
    }

    public void updateVelocity(float targetXSpeed, float targetYSpeed) {
        velocity.x = targetXSpeed;
        velocity.y = targetYSpeed;
    }

    public void reverseDirection() {
        if(facingDirection.equals("Left")) {
            facingDirection = "Right";
        } else {
            facingDirection = "Left";
        }
    }
}
