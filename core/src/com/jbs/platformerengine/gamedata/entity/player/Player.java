package com.jbs.platformerengine.gamedata.entity.player;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.jbs.platformerengine.components.Keyboard;
import com.jbs.platformerengine.gamedata.Point;
import com.jbs.platformerengine.gamedata.entity.mob.Mob;
import com.jbs.platformerengine.screen.ImageManager;
import com.jbs.platformerengine.screen.gamescreen.Tile;
import com.jbs.platformerengine.screen.gamescreen.ScreenChunk;
import com.jbs.platformerengine.screen.gamescreen.CellCollidables;

public class Player extends Mob {
    public Mob targetMob;
    
    public Tile changeAreaTile;

    public Player(ImageManager imageManager) {
        super("", new Point(3700, 600), imageManager, true);

        isPlayer = true;
        displayHitBox = true;
        displayAfterImage = true;

        targetMob = null;
        
        changeAreaTile = null;
    }

    public void updateInput(Keyboard keyboard) {
        boolean moveCheck = false;
        boolean turnAroundCheck = false;

        // Flying Movement //
        if(flying) {
            if(keyboard.left && !keyboard.right) {
                velocity.x = -moveSpeed;
                if(facingDirection.equals("Right")) {
                    facingDirection = "Left";
                    turnAroundCheck = true;
                }
                moveCheck = true;
            } else if(keyboard.right && !keyboard.left) {
                velocity.x = moveSpeed;
                if(facingDirection.equals("Left")) {
                    facingDirection = "Right";
                    turnAroundCheck = true;
                }
                moveCheck = true;
            } else {
                velocity.x = 0;
            }

            if(keyboard.up && !keyboard.down) {
                velocity.y = moveSpeed;
            } else if(keyboard.down && !keyboard.up) {
                velocity.y = -moveSpeed;
            } else {
                velocity.y = 0;
            }
        }

        // Non-Flying Movement //
        else {

            // Sideways Movement //
            velocity.x = 0;
            if(keyboard.left && !keyboard.right
            && (attackData == null || attackData.currentFrame >= attackData.canWalkOnFrame || inAir())) {
                if(!ducking) {
                    velocity.x = -moveSpeed;
                }
                if(facingDirection.equals("Right")) {
                    facingDirection = "Left";
                    turnAroundCheck = true;
                }
                moveCheck = true;
            } else if(!keyboard.left && keyboard.right && (attackData == null || attackData.currentFrame >= attackData.canWalkOnFrame || inAir())) {
                if(!ducking) {
                    velocity.x = moveSpeed;
                }
                if(facingDirection.equals("Left")) {
                    facingDirection = "Right";
                    turnAroundCheck = true;
                }
                moveCheck = true;
            }

            // Jump/Drop Kick //
            if(keyboard.up || dropKickBounceCheck) {
                if(((keyboard.lastDown.contains("Up") || keyboard.lastDown.contains("W")))
                && !ducking) {
                    if(jumpCount < getMaxJumpCount() && !jumpButtonPressedCheck) {
                        jump(this);
                    } else {
                        dropKick(this);
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
        }

        // Run Check //
        if((Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || (Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)))
        && moveCheck) {
            running = true;
        } else {
            running = false;
        }

        // Turn Around Check //
        if(turnAroundCheck) {
            flyingAcceleration = flyingAccelerationMin;
            runAcceleration = runAccelerationMin;
        }
        
        keyboard.lastDown.clear();
        keyboard.lastUp.clear();
    }

    public void update(ScreenChunk[][] screenChunks) {
        
        // Update Facing Direction If Targeting Mob //
        if(targetMob != null
        && !dashCheck) {
            if((facingDirection.equals("Right")
            && hitBoxArea.getMiddle().x > targetMob.hitBoxArea.getMiddle().x)
            || (facingDirection.equals("Left")
            && hitBoxArea.getMiddle().x < targetMob.hitBoxArea.getMiddle().x)) {
                reverseDirection();
            }
        }

        super.update(screenChunks, null);
    }

    public void startTargetingMob(ScreenChunk[][] screenChunks) {
        if(targetMob == null) {
            Mob closestMob = null;
            float closestDistance = 0.0f;

            for(CellCollidables targetCellCollidablesCell : getTargetMobAreaCellCollidables(screenChunks)) {
                for(Mob mob : targetCellCollidablesCell.mobList) {
                    float mobDistance = hitBoxArea.getMiddle().getDistance(mob.hitBoxArea.getMiddle());
                    
                    if(closestMob == null
                    || mobDistance < closestDistance) {
                        closestMob = mob;
                        closestDistance = mobDistance;
                    }
                }
            }
            if(closestMob != null) {
                targetMob = closestMob;
            }
        }
    }

    public void targetNextMob() {

    }

    public void stopTargetingMob() {
        if(targetMob != null) {
            targetMob = null;
        }
    }

    // Utility Functions //
    public ArrayList<CellCollidables> getTargetMobAreaCellCollidables(ScreenChunk[][] screenChunks) {
        ArrayList<CellCollidables> cellCollidablesList = new ArrayList<>();

        int areaSize = 5;
        for(int y = 0; y < areaSize; y++) {
            for(int x = 0; x < areaSize; x++) {
                int cellCollidablesStartX = hitBoxArea.getMiddle().x - ((areaSize / 2) * 64) + (x * 64);
                int cellCollidablesStartY = hitBoxArea.getMiddle().y - ((areaSize / 2) * 64) + (y * 64);
                CellCollidables targetCellCollidablesCell = ScreenChunk.getCellCollidablesCell(screenChunks, cellCollidablesStartX, cellCollidablesStartY);

                if(targetCellCollidablesCell != null) {
                    cellCollidablesList.add(targetCellCollidablesCell);
                }
            }
        }

        return cellCollidablesList;
    }
}
