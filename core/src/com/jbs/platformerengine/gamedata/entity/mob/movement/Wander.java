package com.jbs.platformerengine.gamedata.entity.mob.movement;

import java.util.Random;

import com.jbs.platformerengine.gamedata.entity.mob.Mob;

public class Wander extends MovementPattern {
    int walkTimer;
    int walkTimerMax;
    int pauseTimer;
    int pauseTimerMax;

    public Wander(Mob mob) {
        walkTimer = 0;
        walkTimerMax = new Random().nextInt(420) + 60;
        pauseTimerMax = -1;

        setVelocity(mob);
    }

    public void update(Mob mob) {

        // Move //
        if(pauseTimerMax == -1) {
            walkTimer += 1;
            if(walkTimer < walkTimerMax) {
                mob.velocity.x = xVelocity;
                if(mob.flying) {
                    mob.velocity.y = yVelocity;
                }
            }

            else {
                pauseTimer = 0;
                pauseTimerMax = new Random().nextInt(420) + 90;
                mob.updateVelocity(0, 0);
            }
        }

        // Pause //
        else {
            pauseTimer += 1;
            if(pauseTimer >= pauseTimerMax) {
                walkTimer = 0;
                walkTimerMax = new Random().nextInt(420) + 60;
                pauseTimerMax = -1;

                if(new Random().nextInt(3) == 0) {
                    mob.reverseDirection();
                }

                setVelocity(mob);
            }
        }
    }

    public void setVelocity(Mob mob) {
        if(mob.flying) {
            xVelocity = new Random().nextFloat() * mob.moveSpeed;
            yVelocity = mob.moveSpeed - xVelocity;
            
            if(new Random().nextInt(2) == 0) {
                yVelocity *= -1;
            }
        } else {
            xVelocity = mob.moveSpeed;
        }

        if(mob.facingDirection.equals("Left")) {
            xVelocity *= -1;
        }
    }
}
