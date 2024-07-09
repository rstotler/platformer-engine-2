package com.jbs.platformerengine.gamedata.entity.mob.attack.misc;

import java.util.ArrayList;
import java.util.Arrays;

import com.jbs.platformerengine.gamedata.entity.mob.attack.AttackData;
import com.jbs.platformerengine.gamedata.entity.mob.attack.AttackHitBoxData;

public class DropKick extends AttackData {
    public DropKick() {
        attackFrameLength = -1;

        gravityMod = -15;
        isDropKick = true;

        AttackHitBoxData attackHitBoxData = new AttackHitBoxData();
        attackHitBoxData.attackFrameList = new ArrayList<>(Arrays.asList(-1));
        attackHitBoxData.centerXLocation = true;
        attackHitBoxData.xLocationModList = new int[] {0};
        attackHitBoxData.centerYLocation = false;
        attackHitBoxData.yLocationPercentModList = new float[] {0};
        attackHitBoxData.attackWidthList = new int[] {0};
        attackHitBoxData.attackWidthPercentList = new float[] {1.0f};
        attackHitBoxData.attackHeightList = new int[] {15};

        attackHitBoxList.add(attackHitBoxData);
    }
}
