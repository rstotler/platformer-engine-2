package com.jbs.platformerengine.gamedata.entity.mob.attack;

import java.util.ArrayList;

import com.jbs.platformerengine.gamedata.Rect;
import com.jbs.platformerengine.gamedata.entity.mob.Mob;

public class AttackHitBoxData {
    public ArrayList<Integer> attackFrameList;

    public int[] xLocationModList;
    public int[] yLocationModList;
    public int[] attackWidthList;
    public int[] attackHeightList;

    public Rect getAttackHitBox(Mob thisMob, int currentFrame) {
        
        // Width //
        int attackWidth = 10;
        if(currentFrame < attackWidthList.length) {
            attackWidth = attackWidthList[currentFrame];
        } else if(attackWidthList.length > 0) {
            attackWidth = attackWidthList[attackWidthList.length - 1];
        }

        // Height //
        int attackHeight = 10;
        if(currentFrame < attackHeightList.length) {
            attackHeight = attackHeightList[currentFrame];
        } else if(attackHeightList.length > 0) {
            attackHeight = attackHeightList[attackHeightList.length - 1];
        }

        // X Location //
        int xLocationMod = 0;
        if(currentFrame < xLocationModList.length) {
            xLocationMod = xLocationModList[currentFrame];
        } else if(xLocationModList.length > 0) {
            xLocationMod = xLocationModList[xLocationModList.length - 1];
        }
        int attackX = 0;
        if(thisMob.facingDirection.equals("Right")) {
            attackX = (int) thisMob.hitBoxArea.x + thisMob.hitBoxArea.width + xLocationMod;
        } else {
            attackX = (int) thisMob.hitBoxArea.x - attackWidth - xLocationMod;
        }

        // Y Location //
        int yLocationMod = 0;
        if(currentFrame < yLocationModList.length) {
            yLocationMod = yLocationModList[currentFrame];
        } else if(yLocationModList.length > 0) {
            yLocationMod = yLocationModList[yLocationModList.length - 1];
        }
        int attackY = thisMob.hitBoxArea.getMiddle().y - (attackHeight / 2) + yLocationMod;
        
        return new Rect(attackX, attackY, attackWidth, attackHeight);
    }
}
