package com.jbs.platformerengine.gamedata.entity.mob.logic.movement;

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

        if(mob.flying) {
            float flyingXVelocity = new Random().nextFloat() * mob.moveSpeed;
            float flyingYVelocity = mob.moveSpeed - flyingXVelocity;
            if(mob.facingDirection.equals("Left")) {
                flyingXVelocity *= -1;
            }
            if(new Random().nextInt(2) == 0) {
                flyingYVelocity *= -1;
            }

            mob.updateVelocity(flyingXVelocity, flyingYVelocity);
        } else {
            mob.updateVelocity("X", mob.moveSpeed);
        }
    }

    public void update(Mob mob) {

        // Move //
        if(pauseTimerMax == -1) {
            walkTimer += 1;
            if(walkTimer >= walkTimerMax) {
                mob.updateVelocity(0, 0);

                pauseTimer = 0;
                pauseTimerMax = new Random().nextInt(420) + 90;
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

                if(mob.flying) {
                    float flyingXVelocity = new Random().nextFloat() * mob.moveSpeed;
                    float flyingYVelocity = mob.moveSpeed - flyingXVelocity;
                    if(mob.facingDirection.equals("Left")) {
                        flyingXVelocity *= -1;
                    }
                    if(new Random().nextInt(2) == 0) {
                        flyingYVelocity *= -1;
                    }

                    mob.updateVelocity(flyingXVelocity, flyingYVelocity);
                }
            }
        }
    }
}
