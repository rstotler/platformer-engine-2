package com.jbs.platformerengine.gamedata.entity.mob.attack.sword.ground.base;

import java.util.ArrayList;
import java.util.Arrays;

import com.jbs.platformerengine.gamedata.entity.mob.attack.AttackData;
import com.jbs.platformerengine.gamedata.entity.mob.attack.AttackHitBoxData;

public class BaseAttack extends AttackData {
    public BaseAttack() {
        attackFrameLength = 50;
        canWalkOnFrame = 25;

        AttackHitBoxData attackHitBoxData = new AttackHitBoxData();
        attackHitBoxData.attackFrameList = new ArrayList<>(Arrays.asList(3, 4, 5, 6));
        attackHitBoxData.xLocationModList = new int[] {10};
        attackHitBoxData.yLocationPercentModList = new float[] {.5f};
        attackHitBoxData.attackWidthList = new int[] {40};
        attackHitBoxData.attackHeightList = new int[] {4};
        attackHitBoxList.add(attackHitBoxData);

        // Attack Chain(s) //
        attackChainMap.put(10, new com.jbs.platformerengine.gamedata.entity.mob.attack.sword.ground.base.chain01.Chain01_A());
        attackChainMap.put(25, new com.jbs.platformerengine.gamedata.entity.mob.attack.sword.ground.base.chain02.Chain02_A());
    }
}
