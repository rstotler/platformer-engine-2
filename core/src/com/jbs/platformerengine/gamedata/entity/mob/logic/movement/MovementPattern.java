package com.jbs.platformerengine.gamedata.entity.mob.logic.movement;

import com.jbs.platformerengine.gamedata.entity.mob.Mob;

public class MovementPattern {
    public float xVelocity;
    public float yVelocity;

    public MovementPattern() {
        xVelocity = 0;
        yVelocity = 0;
    }

    public void update(Mob mob) {}

    public static void trackTarget(Mob mob) {
        Mob target = mob.enemyTargetList.get(0);

        // Flying //
        if(mob.flying) { 
            float targetX = target.hitBoxArea.x + (target.hitBoxArea.width / 2);
            float targetY = target.hitBoxArea.y + (target.hitBoxArea.height / 2);
            float localX = mob.hitBoxArea.x + (mob.hitBoxArea.width / 2);
            float localY = mob.hitBoxArea.y + (mob.hitBoxArea.height / 2);
            float xDistance = Math.abs(targetX - localX);
            float yDistance = Math.abs(targetY - localY);
            float xMove = 0;
            float yMove = 0;

            if(xDistance <= mob.moveSpeed && yDistance <= mob.moveSpeed) {
                mob.velocity.x = 0;
                mob.velocity.y = 0;
            } else {
                float angle = (float) Math.toDegrees(Math.atan2(yDistance, xDistance));
                float anglePercent = angle / 90.0f;
                yMove = mob.moveSpeed * anglePercent;
                xMove = mob.moveSpeed - yMove;

                if(localX > targetX) {
                    xMove *= -1;
                }
                if(localY > targetY) {
                    yMove *= -1;
                }
            }

            mob.velocity.x = xMove;
            mob.velocity.y = yMove;
        }
        
        // Non-Flying //
        else {
            if((mob.hitBoxArea.x + (mob.hitBoxArea.width / 2)) < target.hitBoxArea.x) {
                mob.velocity.x = mob.moveSpeed;
            } else if((mob.hitBoxArea.x + (mob.hitBoxArea.width / 2)) > target.hitBoxArea.x + target.hitBoxArea.width) {
                mob.velocity.x = -mob.moveSpeed;
            } else {
                mob.velocity.x = 0;
                return;
            }
        }
    }
}
