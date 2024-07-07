package com.jbs.platformerengine.gamedata.entity.mob;

import java.util.*;

import com.jbs.platformerengine.gamedata.Point;
import com.jbs.platformerengine.gamedata.entity.CombatEntity;
import com.jbs.platformerengine.gamedata.entity.mob.movement.*;
import com.jbs.platformerengine.screen.ImageManager;
import com.jbs.platformerengine.screen.gamescreen.CellCollidables;
import com.jbs.platformerengine.screen.gamescreen.GameScreen;
import com.jbs.platformerengine.screen.gamescreen.ScreenChunk;

public class Mob extends CombatEntity {
    public int updateTimer;          // Checks Against AreaTimer (Prevents Updating Same Sprite Twice)
    public int updateAnimationTimer; // Checks Against AreaTimer (Prevents Drawing Same Sprite Twice)

    public boolean isPlayer;
    public int healthPoints;

    public Mob(String imageName, Point location, ImageManager imageManager, boolean isPlayer) {
        super(imageName, imageManager, location);

        updateTimer = -1;
        updateAnimationTimer = -1;

        isPlayer = false;
        healthPoints = 2;
        
        loadMob(imageManager, imageName, isPlayer);
        displayHitBox = true;
    }

    public void loadMob(ImageManager imageManager, String imageName, boolean isPlayer) {
        
        // Randomize Facing Direction //
        if(!isPlayer) {
            facingDirection = "Left";
            if(new Random().nextInt(2) == 0) {
                facingDirection = "Right";
            }
        }
        
        imageType = "Mob";

        if(imageName.equals("Bat")) {
            hitBoxArea.width = 18;
            hitBoxArea.height = 12;
            flying = true;

            frameLength = 3;

            if(!isPlayer) {
                movementPattern = new Wander(this);
            }
        }

        else {
            hitBoxArea.width = 16;
            hitBoxArea.height = 48;
            flying = false;
            movementPattern = null;
        }

        // Reload Animated Object Data //
        if(imageManager.mobImage.containsKey(imageName)) {
            maxFrameNum = imageManager.mobImage.get(imageName).get("Default").size();
            currentFrameNum = new Random().nextInt(maxFrameNum);
        }
    }

    public void changeForm(ImageManager imageManager, boolean isPlayer) {
        if(imageName.equals("")) {
            imageName = "Bat";
        } else if(imageName.equals("Bat")) {
            imageName = "";
        }

        loadMob(imageManager, imageName, isPlayer);
    }

    public void update(ScreenChunk[][] screenChunks, HashMap<Mob, ArrayList<CellCollidables>> updateMobScreenChunkMap) {
        if(displayAfterImage) {
            updateAfterImage(this);
        }
        
        if(updateMobScreenChunkMap != null) {
            updateMovement(screenChunks, updateMobScreenChunkMap, this);
        }
        updateActionList.clear();

        // Run Acceleration //
        if(running
        && !(!flying && inAir())
        && !(flying && flyingAcceleration < 1.0)) {
            if(velocity.x != 0) {
                if(runAcceleration < runAccelerationMax) {
                    runAcceleration += .008;
                    if(runAcceleration > runAccelerationMax) {
                        runAcceleration = runAccelerationMax;
                    }
                }
                velocity.x = moveSpeed + (runMod * runAcceleration);
                if(facingDirection.equals("Left")) {
                    velocity.x *= -1;
                }
            }
        } else {
            if(velocity.x != 0) {
                if(runAcceleration > runAccelerationMin) {
                    if(!(!flying && inAir())) {
                        runAcceleration -= .015;
                        if(runAcceleration < runAccelerationMin) {
                            runAcceleration = runAccelerationMin;
                        }
                    }
                    velocity.x = moveSpeed + (runMod * runAcceleration);
                    if(facingDirection.equals("Left")) {
                        velocity.x *= -1;
                    }
                }
            } else {
                runAcceleration = runAccelerationMin;
            }
        }

        // Flying Acceleration //
        if(flying) {
            if(velocity.x != 0 || velocity.y != 0) {
                if(flyingAcceleration < 1) {
                    flyingAcceleration += .015;
                    if(flyingAcceleration > 1) {
                        flyingAcceleration = 1.0f;
                    }
                }
            } else if(flyingAcceleration > 0) {
                flyingAcceleration -= .05;
                if(flyingAcceleration < flyingAccelerationMin) {
                    flyingAcceleration = flyingAccelerationMin;
                }
            }

            velocity.x *= flyingAcceleration;
            velocity.y *= flyingAcceleration;
        }
        
        // Collision Check //
        ArrayList<CellCollidables> oldCellCollidables = GameScreen.getObjectCellCollidables(screenChunks, this);
        updateTileCollisions(screenChunks, this);

        // Update Mob Cell Collidables //
        ArrayList<CellCollidables> newCellCollidables = GameScreen.getObjectCellCollidables(screenChunks, this);
        if(!oldCellCollidables.equals(newCellCollidables)) {
            ArrayList<CellCollidables> removeFromScreenChunkList = GameScreen.updateObjectCellCollidables(screenChunks, this, oldCellCollidables, newCellCollidables);
            if(removeFromScreenChunkList.size() > 0) {
                updateMobScreenChunkMap.put(this, removeFromScreenChunkList);
            }
        }

        // Update Attacks //
        updateAttack();
        updateAttackCollidables(screenChunks, this);
    }
}
