package com.jbs.platformerengine.gamedata.entity.mob.attack;

import java.util.*;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.jbs.platformerengine.gamedata.Rect;
import com.jbs.platformerengine.gamedata.entity.CollidableObject;
import com.jbs.platformerengine.gamedata.entity.mob.Mob;
import com.jbs.platformerengine.gamedata.entity.mob.attack.sword.ground.*;
import com.jbs.platformerengine.gamedata.entity.mob.attack.sword.flying.*;
import com.jbs.platformerengine.screen.gamescreen.ScreenChunk;

public class AttackData {
    public int currentFrame;
    public int attackFrameLength;

    public float gravityMod;

    public int canWalkOnFrame;

    public boolean isChargeable;
    public boolean isCharging;
    public float chargePercent;

    public boolean isDropKick;

    public ArrayList<AttackHitBoxData> attackHitBoxList;
    public ArrayList<CollidableObject> hitObjectList;

    public AttackData() {
        currentFrame = 0;
        attackFrameLength = 10;

        gravityMod = 0;

        canWalkOnFrame = 9999;
        
        isChargeable = false;
        isCharging = false;
        chargePercent = 0.0f;

        isDropKick = false;

        attackHitBoxList = new ArrayList<>();
        hitObjectList = new ArrayList<>();
    }

    public static AttackData getAttackChainStartData(String targetDirection, boolean flying, String weaponType) {
        if(weaponType.equals("Sword")) {
            if(flying) {
                if(targetDirection.equals("Up")) {
                    return new SwordFlyingUpBaseAttack();
                } else if(targetDirection.equals("Down")) {
                    return new SwordFlyingDownBaseAttack();
                } else if(targetDirection.equals("None")) {
                    return new SwordFlyingBaseAttack();
                }
            }

            else {
                if(targetDirection.equals("Up")) {
                    return new SwordGroundUpBaseAttack();
                } else if(targetDirection.equals("Down")) {
                    return new SwordGroundDownBaseAttack();
                } else if(targetDirection.equals("Side")) {
                    return new SwordGroundSideBaseAttack();
                } else if(targetDirection.equals("None")) {
                    return new SwordGroundBaseAttack();
                }
            }
        }

        return new SwordGroundBaseAttack();
    }

    public boolean update(ScreenChunk[][] screenChunks, Mob thisMob) {
        if(isChargeable && isCharging) {
            if(chargePercent < 1.0) {
                chargePercent += .01;
                if(chargePercent > 1.0) {
                    chargePercent = 1.0f;
                }
            }
        }

        else if(attackFrameLength > 0) {
            currentFrame += 1;
            if(currentFrame >= attackFrameLength) {
                return true;
            }
        }

        return false;
    }

    public void renderAttackHitBoxes(ShapeRenderer shapeRenderer, Mob thisMob) {
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(102/255f, 0/255f, 0/255f, 1f);

        for(AttackHitBoxData attackHitBoxData : attackHitBoxList) {
            if(attackHitBoxData.attackFrameList.contains(currentFrame)) {
                Rect attackHitBox = attackHitBoxData.getAttackHitBox(thisMob, currentFrame);
                shapeRenderer.rect((int) attackHitBox.x, (int) attackHitBox.y, attackHitBox.width, attackHitBox.height);
            }
        }

        shapeRenderer.end();
    }
}
