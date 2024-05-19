package com.jbs.platformerengine.gamedata.entity;

import java.util.*;

import com.jbs.platformerengine.gamedata.Point;
import com.jbs.platformerengine.gamedata.Rect;
import com.jbs.platformerengine.gamedata.area.AreaData;
import com.jbs.platformerengine.gamedata.entity.player.Player;
import com.jbs.platformerengine.screen.ImageManager;

public class Mob extends Player {
    public int updateTimer;
    public int updateAnimationTimer;
    String movementPattern;

    public ArrayList<Player> enemyTargetList;

    public Mob(String imageName, Point location, ImageManager imageManager) {
        super(imageName, imageManager);

        updateTimer = -1;
        updateAnimationTimer = -1;
        movementPattern = "";

        enemyTargetList = new ArrayList<>();

        facingDirection = "Left";
        if(new Random().nextInt(2) == 0) {
            facingDirection = "Right";
        }
        hitBoxArea = new Rect(location.x, location.y, 16, 48);

        loadMob(imageName);
    }

    public void loadMob(String imageName) {
        if(imageName.equals("Bat")) {
            hitBoxArea.width = 18;
            hitBoxArea.height = 12;
            flying = true;

            frameLength = 3;

            //movementPattern = "Patrol";
        }
    }

    public void updateAI(AreaData areaData) {
        if(!enemyTargetList.isEmpty()) {
            trackTarget();
        }

        else if(movementPattern.equals("Patrol")) {
            if(facingDirection.equals("Left")) {
                velocity.x = -moveSpeed;
            } else {
                velocity.x = moveSpeed;
            }

            if(updateActionList.contains("Hit Wall")) {
                reverseDirection();
                velocity.x = 0;
            }
        }
    }

    public void reverseDirection() {
        if(facingDirection.equals("Left")) {
            facingDirection = "Right";
        } else {
            facingDirection = "Left";
        }
    }

    public void trackTarget() {
        Player target = enemyTargetList.get(0);

        // Flying //
        if(flying) { 
            int targetX = target.hitBoxArea.x + (target.hitBoxArea.width / 2);
            int targetY = target.hitBoxArea.y + (target.hitBoxArea.height / 2);
            int localX = hitBoxArea.x + (hitBoxArea.width / 2);
            int localY = hitBoxArea.y + (hitBoxArea.height / 2);
            float xDistance = Math.abs(targetX - localX);
            float yDistance = Math.abs(targetY - localY);
            float xMove = 0;
            float yMove = 0;

            if(xDistance <= moveSpeed && yDistance <= moveSpeed) {
                velocity.x = 0;
                velocity.y = 0;
            } else {
                float angle = (float) Math.toDegrees(Math.atan2(yDistance, xDistance));
                float anglePercent = angle / 90.0f;
                yMove = moveSpeed * anglePercent;
                xMove = moveSpeed - yMove;

                if(localX > targetX) {
                    xMove *= -1;
                }
                if(localY > targetY) {
                    yMove *= -1;
                }
            }

            velocity.x = xMove;
            velocity.y = yMove;
        }
        
        // Non-Flying //
        else {
            if((hitBoxArea.x + (hitBoxArea.width / 2)) < target.hitBoxArea.x) {
                facingDirection = "Right";
                velocity.x = moveSpeed;
            } else if((hitBoxArea.x + (hitBoxArea.width / 2)) > target.hitBoxArea.x + target.hitBoxArea.width) {
                facingDirection = "Left";
                velocity.x = -moveSpeed;
            } else {
                velocity.x = 0;
                return;
            }
        }
    }
}
