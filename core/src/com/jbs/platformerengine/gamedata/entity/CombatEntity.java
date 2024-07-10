package com.jbs.platformerengine.gamedata.entity;

import java.util.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.jbs.platformerengine.gamedata.Point;
import com.jbs.platformerengine.gamedata.Rect;
import com.jbs.platformerengine.gamedata.entity.mob.Mob;
import com.jbs.platformerengine.gamedata.entity.mob.attack.AttackData;
import com.jbs.platformerengine.gamedata.entity.mob.attack.AttackHitBoxData;
import com.jbs.platformerengine.gamedata.entity.mob.attack.misc.DropKick;
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

    public boolean dropKickBounceCheck;

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

    public AttackData attackData;
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

        dropKickBounceCheck = false;

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

        attackData = null;
        enemyTargetList = new ArrayList<>();

        movementPattern = null; 
        updateActionList = new ArrayList<>();
    }

    public void attack(Mob thisMob) {

        // Start New Attack Chain //
        if(attackData == null) {
            boolean leftPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT);
            boolean rightPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
            boolean upPressed = Gdx.input.isKeyPressed(Input.Keys.UP);
            boolean downPressed = Gdx.input.isKeyPressed(Input.Keys.DOWN);

            // Air Attacks //
            if(thisMob.inAir() && !thisMob.flying) {
                if(upPressed != downPressed && !leftPressed && !rightPressed) {
                    if(upPressed) {
                        attackData = AttackData.getAttackChainStartData("Up", true, thisMob.getCurrentWeaponType());
                    } else {
                        attackData = AttackData.getAttackChainStartData("Down", true, thisMob.getCurrentWeaponType());
                    }
                } else {
                    attackData = AttackData.getAttackChainStartData("None", true, thisMob.getCurrentWeaponType());
                }
            }

            // Directional Ground Attacks //
            if(leftPressed != rightPressed || upPressed != downPressed) {
                if(leftPressed != rightPressed) {
                    attackData = AttackData.getAttackChainStartData("Side", false, thisMob.getCurrentWeaponType());
                } else if(upPressed) {
                    attackData = AttackData.getAttackChainStartData("Up", false, thisMob.getCurrentWeaponType());
                } else if(downPressed) {
                    attackData = AttackData.getAttackChainStartData("Down", false, thisMob.getCurrentWeaponType());
                }
            }

            // Non-Directional Ground Attack //
            else {
                attackData = AttackData.getAttackChainStartData("None", false, thisMob.getCurrentWeaponType());
            }
        }

        // Continue Attack Chain //
        else {
            attackData.continueAttackChain(thisMob);
        }
    }

    public void releaseChargedAttack() {
        if(attackData != null && attackData.isChargeable && attackData.isCharging) {
            attackData.isCharging = false;
        }
    }

    public void updateAttackCollidables(ScreenChunk[][] screenChunks, Mob thisMob) {
        for(AttackHitBoxData attackHitBoxData : attackData.attackHitBoxList) {
            if(attackHitBoxData.attackFrameList.contains(attackData.currentFrame)
            || attackHitBoxData.attackFrameList.contains(-1)) {
                Rect attackRect = attackHitBoxData.getAttackHitBox(thisMob, attackData.currentFrame);

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
                            attackCollidableObjects(screenChunks[chunkX][chunkY].cellCollidables[cellX][cellY].breakableList, screenChunks, attackRect, thisMob);
                            attackCollidableObjects(screenChunks[chunkX][chunkY].cellCollidables[cellX][cellY].mobList, screenChunks, attackRect, thisMob);
                        }
                    }
                }
            }
        }
    }

    public <T> void attackCollidableObjects(ArrayList<T> objectList, ScreenChunk[][] screenChunks, Rect attackRect, Mob thisMob) {
        if(attackData != null) {
            String attackObjectClassName = attackData.getClass().toString().substring(attackData.getClass().toString().lastIndexOf(".") + 1);
        
            ArrayList<T> deleteObjectList = new ArrayList<>();
            for(T object : objectList) {
                String objectType = object.getClass().toString().substring(object.getClass().toString().lastIndexOf(".") + 1);
                CollidableObject collidableObject = null;
                if(objectType.equals("BreakableObject")) {
                    collidableObject = (BreakableObject) object;
                } else if(objectType.equals("Mob")) {
                    collidableObject = (Mob) object;
                }

                if(collidableObject != null
                && attackRect.rectCollide(collidableObject.hitBoxArea)
                && !attackData.hitObjectList.contains(collidableObject)) {

                    // Reduce Mob Health & Add ThisMob To Defender EnemyTargetList //
                    if(objectType.equals("Mob")) {
                        ((Mob) object).healthPoints -= 1;
                        if(!((Mob) object).enemyTargetList.contains(thisMob)) {
                            ((Mob) object).enemyTargetList.add(0, thisMob);
                        }
                    }

                    // Remove Dead Mob OR Add To Attack HitObjectList (To Prevent Multiple Hits) //
                    if(objectType.equals("BreakableObject")
                    || (objectType.equals("Mob") && ((Mob) object).healthPoints <= 0)) {
                        deleteObjectList.add(object);
                    } else if(!attackData.hitObjectList.contains(collidableObject)) {
                        attackData.hitObjectList.add(collidableObject);
                    }

                    // Drop Kick Bounce Check //
                    if(attackObjectClassName.equals("DropKick")) {
                        velocity.y = 10;
                        jumpCheck = true;
                        jumpCount = 1;
                        jumpTimer = 0;
                        dropKickBounceCheck = true;

                        if(thisMob.attackData != null) {
                            thisMob.attackData = null;
                        }
                        
                        break;
                    }
                }
            }

            for(T deleteObject : deleteObjectList) {
                GameScreen.removeObjectFromCellCollidables(screenChunks, deleteObject);
            }
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

    public void jump(Mob thisMob) {
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

        if(thisMob.attackData != null) {
            thisMob.attackData = null;
        }
    }

    public int getMaxJumpCount() {
        return 2;
    }

    public void dropKick(Mob thisMob) {
        String attackObjectClassName = "";
        if(thisMob.attackData != null) {
            attackObjectClassName = thisMob.attackData.getClass().toString().substring(thisMob.attackData.getClass().toString().lastIndexOf(".") + 1);
        }

        if(thisMob.inAir()
        && (superJumpTimer == 0 || superJumpTimer >= superJumpTimerMax)
        && !(attackObjectClassName.equals("DropKick"))) {
            attackData = new DropKick();
        }
    }

    public void superJump(Mob thisMob) {
        if(!flying
        && !ducking
        && (thisMob.attackData == null || thisMob.attackData.currentFrame >= thisMob.attackData.canWalkOnFrame)
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

    public void dash(Mob thisMob, String direction) {
        if(!flying && !ducking
        && runAcceleration <= runAccelerationMin
        && dashPercent < .75
        && superJumpPercent < .30
        && (thisMob.attackData == null || thisMob.attackData.currentFrame >= thisMob.attackData.canWalkOnFrame)) {
            dashCheck = true;
            dashTimer = 0f;
            dashDirection = direction;

            if(thisMob.attackData != null) {
                thisMob.attackData = null;
            }
        }
    }

    public void duck(boolean keyDown, Mob thisMob) {
        if(!flying
        && (attackData == null || attackData.currentFrame >= attackData.canWalkOnFrame)) {
            if(keyDown && !inAir()) {
                int duckHeightDiff = 13;
                hitBoxArea.height = 48 - duckHeightDiff;
                ducking = true;

                if(thisMob.attackData != null) {
                    thisMob.attackData = null;
                }
            }
    
            else if(ducking && !keyDown) {
                hitBoxArea.height = 48;
                ducking = false;

                if(thisMob.attackData != null) {
                    thisMob.attackData = null;
                }
            }
        }
    }

    public String getCurrentWeaponType() {
        return "Sword";
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

        if(!justLanded) {
            justLanded = true;
            attackData = null;
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
