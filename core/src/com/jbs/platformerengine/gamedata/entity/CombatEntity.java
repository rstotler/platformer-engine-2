package com.jbs.platformerengine.gamedata.entity;

import java.util.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.jbs.platformerengine.gamedata.Point;
import com.jbs.platformerengine.gamedata.Rect;
import com.jbs.platformerengine.gamedata.entity.mob.Mob;
import com.jbs.platformerengine.gamedata.entity.mob.attack.AttackData;
import com.jbs.platformerengine.gamedata.entity.mob.movement.MovementPattern;
import com.jbs.platformerengine.screen.ImageManager;
import com.jbs.platformerengine.screen.gamescreen.CellCollidables;
import com.jbs.platformerengine.screen.gamescreen.GameScreen;
import com.jbs.platformerengine.screen.gamescreen.ScreenChunk;
import com.jbs.platformerengine.screen.gamescreen.Tile;

public class CombatEntity extends CollidableObject {
    public int moveSpeed;
    public float runMod;
    public String facingDirection;

    public boolean jumpCheck;
    public boolean jumpButtonPressedCheck;
    public int jumpTimerMax;
    public int jumpTimer;
    public int jumpCount;
    public float maxFallVelocity;
    
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

    public boolean ducking;
    public boolean falling;
    public boolean justLanded;

    public boolean running;
    public float runAccelerationMin;
    public float runAccelerationMax;
    public float runAcceleration;
    public boolean flying;
    public float flyingAccelerationMin;
    public float flyingAcceleration;

    public ArrayList<Mob> enemyTargetList;

    public MovementPattern movementPattern;
    public ArrayList<String> updateActionList; // Hit Wall

    public CombatEntity(String imageName, ImageManager imageManager, Point location) {
        super(imageName, imageManager, location);

        moveSpeed = 2;
        runMod = 2.50f;
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

        ducking = false;
        falling = false;
        justLanded = false;

        running = false;
        runAccelerationMin = 0f;
        runAccelerationMax = 4.00f;
        runAcceleration = runAccelerationMin;
        flying = false;
        flyingAccelerationMin = .25f;
        flyingAcceleration = flyingAccelerationMin;

        movementPattern = null; 
        updateActionList = new ArrayList<>();

        enemyTargetList = new ArrayList<>();
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

    public void updateAttackCollidables(ScreenChunk[][] screenChunks, Mob thisMob) {
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
                        attackCollidableObject(screenChunks[chunkX][chunkY].cellCollidables[cellX][cellY].breakableList, screenChunks, attackRect, null);
                        attackCollidableObject(screenChunks[chunkX][chunkY].cellCollidables[cellX][cellY].mobList, screenChunks, attackRect, thisMob);
                    }
                }
            }
        }
    }

    public <T> void attackCollidableObject(ArrayList<T> objectList, ScreenChunk[][] screenChunks, Rect attackRect, Mob thisMob) {
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

                        if(!((Mob) object).enemyTargetList.contains(thisMob)) {
                            ((Mob) object).enemyTargetList.add(0, thisMob);
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

    public void renderAttackHitBox(ShapeRenderer shapeRenderer) {
        if(attackDecayTimer >= attackData.get(getCurrentAttack()).attackFrameStart[attackCount - 1]
        && attackDecayTimer < attackData.get(getCurrentAttack()).attackFrameEnd[attackCount - 1]) {
            shapeRenderer.begin(ShapeType.Filled);

            if(attackCount == 1) {
                shapeRenderer.setColor(82/255f, 0/255f, 0/255f, 1f);
            } else if(attackCount == 2) {
                shapeRenderer.setColor(92/255f, 0/255f, 0/255f, 1f);
            } else {
                shapeRenderer.setColor(102/255f, 0/255f, 0/255f, 1f);
            }

            Rect attackHitBox = getAttackHitBox();
            shapeRenderer.rect((int) attackHitBox.x, (int) attackHitBox.y, attackHitBox.width, attackHitBox.height);
            shapeRenderer.end();
        }
    }

    public void updateMovement(ScreenChunk[][] screenChunks, HashMap<Mob, ArrayList<CellCollidables>> updateMobScreenChunkMap, Mob thisMob) {

        // Update Movement Pattern //
        if(!enemyTargetList.isEmpty()) {
            MovementPattern.trackTarget(thisMob);
        } else {
            if(movementPattern != null) {
                if(updateActionList.contains("Hit Wall")) {
                    reverseDirection();
                    velocity.x *= -1;
                    movementPattern.xVelocity *= -1;
                    flyingAcceleration = flyingAccelerationMin;
                }
                movementPattern.update(thisMob);
            }
        }

        // Update Facing Direction //
        if(velocity.x < 0 && facingDirection.equals("Right")) {
            facingDirection = "Left";
        } else if(velocity.x > 0 && facingDirection.equals("Left")) {
            facingDirection = "Right";
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

    public int getMaxJumpCount() {
        return 2;
    }

    public void dropKick() {
        dropKickCheck = true;
        hitObjectList.clear();
    }

    public void superJump() {
        if(!flying
        && !ducking
        && !dropKickCheck
        && superJumpPercent < .05) {
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
        if(!flying
        && runAcceleration <= runAccelerationMin
        && dashPercent < .75
        && superJumpPercent < .30) {
            dashCheck = true;
            dashTimer = 0f;
            dashDirection = direction;
        }
    }

    public void duck(boolean keyDown) {
        if(!flying) {
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

    public void reverseDirection() {
        if(facingDirection.equals("Left")) {
            facingDirection = "Right";
        } else {
            facingDirection = "Left";
        }
    }
}