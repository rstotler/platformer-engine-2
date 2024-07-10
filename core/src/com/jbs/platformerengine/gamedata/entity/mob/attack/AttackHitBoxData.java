package com.jbs.platformerengine.gamedata.entity.mob.attack;

import java.util.ArrayList;

import com.jbs.platformerengine.gamedata.Rect;
import com.jbs.platformerengine.gamedata.entity.mob.Mob;

public class AttackHitBoxData {
    public ArrayList<Integer> attackFrameList;

    public boolean centerXLocation;         // Center X Location Instead Of To The Side Of Mob HitBox
    public int[] xLocationModList;          // Offset By Pixel
    public boolean centerYLocation;         // Center Y Location Instead Of To The Bottom Of Mob HitBox
    public float[] yLocationPercentModList; // Offset By Percentage (For Different Size Mobs)
    public int[] attackWidthList;           // Attack Width Is A Specific (Pixel) Size
    public float[] attackWidthPercentList;  // Attack Width Is A Percentage Of Mob HitBox
    public int[] attackHeightList;          // Attack Height Is A Specific (Pixel) Size

    public AttackHitBoxData() {
        centerXLocation = false;
        centerYLocation = true;
    }

    public Rect getAttackHitBox(Mob thisMob, int currentFrame) {
        
        // Width //
        int attackWidth = 10;
        if(attackWidthPercentList != null && attackWidthPercentList.length > 0) {
            if(currentFrame < attackWidthPercentList.length) {
                float attackWidthPercent = attackWidthPercentList[currentFrame];
                attackWidth = (int) (thisMob.hitBoxArea.width * attackWidthPercent);
            } else if(attackWidthPercentList.length > 0) {
                float attackWidthPercent = attackWidthPercentList[attackWidthPercentList.length - 1];
                attackWidth = (int) (thisMob.hitBoxArea.width * attackWidthPercent);
            }
        } else {
            if(currentFrame < attackWidthList.length) {
                attackWidth = attackWidthList[currentFrame];
            } else if(attackWidthList.length > 0) {
                attackWidth = attackWidthList[attackWidthList.length - 1];
            }
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
        if(!centerXLocation) {
            attackX = (int) thisMob.hitBoxArea.x;
            if(thisMob.facingDirection.equals("Right")) {
                attackX += thisMob.hitBoxArea.width + xLocationMod;
            } else {
                attackX -= attackWidth + xLocationMod;
            }
        } else {
            attackX = thisMob.hitBoxArea.getMiddle().x - (thisMob.hitBoxArea.width / 2);
            if(thisMob.facingDirection.equals("Right")) {
                attackX += xLocationMod;
            } else {
                attackX -= xLocationMod;
            }
        }

        // Y Location (Percentage) //
        float yLocationPercent = 0.0f;
        if(currentFrame < yLocationPercentModList.length) {
            yLocationPercent = yLocationPercentModList[currentFrame];
        } else if(yLocationPercentModList.length > 0) {
            yLocationPercent = yLocationPercentModList[yLocationPercentModList.length - 1];
        }
        int yLocationPercentMod = 0;
        int attackY = 0;
        if(!centerYLocation) {
            yLocationPercentMod = (int) (thisMob.hitBoxArea.height * yLocationPercent);
            attackY = (int) thisMob.hitBoxArea.y + yLocationPercentMod;
        } else {
            yLocationPercentMod = (int) ((thisMob.hitBoxArea.height / 2) * yLocationPercent);
            attackY = thisMob.hitBoxArea.getMiddle().y - (attackHeight / 2) + yLocationPercentMod;
        }
        
        return new Rect(attackX, attackY, attackWidth, attackHeight);
    }
}
