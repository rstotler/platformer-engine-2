package com.jbs.platformerengine.gamedata.entity.mob.logic.movement;

import com.jbs.platformerengine.gamedata.entity.mob.Mob;

public class Patrol extends MovementPattern {
    public void update(Mob mob) {
        mob.velocity.x = mob.moveSpeed;
        if(mob.facingDirection.equals("Left")) {
            mob.velocity.x *= -1;
        }
    }
}
