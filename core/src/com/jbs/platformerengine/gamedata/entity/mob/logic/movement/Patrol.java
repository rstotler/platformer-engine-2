package com.jbs.platformerengine.gamedata.entity.mob.logic.movement;

import com.jbs.platformerengine.gamedata.entity.mob.Mob;

public class Patrol extends MovementPattern {
    public void update(Mob mob) {
        mob.updateVelocity("X", mob.moveSpeed);
    }
}
