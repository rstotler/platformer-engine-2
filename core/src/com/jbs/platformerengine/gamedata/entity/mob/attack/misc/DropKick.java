package com.jbs.platformerengine.gamedata.entity.mob.attack.misc;

import com.jbs.platformerengine.gamedata.entity.mob.attack.AttackData;

public class DropKick extends AttackData {
    public DropKick() {
        attackFrameLength = -1;

        gravityMod = -15;
        isDropKick = true;
    }
}
