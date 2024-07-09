package com.jbs.platformerengine.gamedata.entity.mob.attack;

import java.util.ArrayList;

import com.jbs.platformerengine.gamedata.Rect;
import com.jbs.platformerengine.gamedata.entity.mob.Mob;

public class AttackHitBoxData {
    public ArrayList<Integer> attackFrameList;

    public int[] xLocationModList;          // Offset By Pixel
    public float[] yLocationPercentModList; // Offset By Percentage (For Different Size Mobs)
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

        // Y Location (Percentage) //
        float yLocationPercent = 0.0f;
        if(currentFrame < yLocationPercentModList.length) {
            yLocationPercent = yLocationPercentModList[currentFrame];
        } else if(yLocationPercentModList.length > 0) {
            yLocationPercent = yLocationPercentModList[yLocationPercentModList.length - 1];
        }
        int yLocationPercentMod = (int) ((thisMob.hitBoxArea.height / 2) * yLocationPercent);
        int attackY = thisMob.hitBoxArea.getMiddle().y - (attackHeight / 2) + yLocationPercentMod;
        
        return new Rect(attackX, attackY, attackWidth, attackHeight);
    }
}
