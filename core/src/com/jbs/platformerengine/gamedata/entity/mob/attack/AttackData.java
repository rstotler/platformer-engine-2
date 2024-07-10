package com.jbs.platformerengine.gamedata.entity.mob.attack;

import java.util.*;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.jbs.platformerengine.gamedata.Rect;
import com.jbs.platformerengine.gamedata.entity.CollidableObject;
import com.jbs.platformerengine.gamedata.entity.mob.Mob;
import com.jbs.platformerengine.screen.gamescreen.ScreenChunk;

public class AttackData {
    public int currentFrame;
    public int attackFrameLength;
    public int canWalkOnFrame;

    public float gravityMod;

    public boolean isChargeable;
    public boolean isCharging;
    public float chargePercent;

    public boolean isDropKick;

    public ArrayList<AttackHitBoxData> attackHitBoxList;
    public ArrayList<CollidableObject> hitObjectList;

    public LinkedHashMap<Integer, AttackData> attackChainMap;

    public AttackData() {
        currentFrame = 0;
        attackFrameLength = 10;
        canWalkOnFrame = 9999;

        gravityMod = 0;

        isChargeable = false;
        isCharging = false;
        chargePercent = 0.0f;

        isDropKick = false;

        attackHitBoxList = new ArrayList<>();
        hitObjectList = new ArrayList<>();

        attackChainMap = new LinkedHashMap<>();
    }

    public static AttackData getAttackChainStartData(String targetDirection, boolean flying, String weaponType) {
        if(weaponType.equals("Sword")) {
            if(flying) {
                if(targetDirection.equals("Up")) {
                    return new com.jbs.platformerengine.gamedata.entity.mob.attack.sword.flying.up.BaseAttack();
                } else if(targetDirection.equals("Down")) {
                    return new com.jbs.platformerengine.gamedata.entity.mob.attack.sword.flying.down.BaseAttack();
                } else if(targetDirection.equals("None")) {
                    return new com.jbs.platformerengine.gamedata.entity.mob.attack.sword.flying.base.BaseAttack();
                }
            }

            else {
                if(targetDirection.equals("Up")) {
                    return new com.jbs.platformerengine.gamedata.entity.mob.attack.sword.ground.up.BaseAttack();
                } else if(targetDirection.equals("Down")) {
                    return new com.jbs.platformerengine.gamedata.entity.mob.attack.sword.ground.down.BaseAttack();
                } else if(targetDirection.equals("Side")) {
                    return new com.jbs.platformerengine.gamedata.entity.mob.attack.sword.ground.side.BaseAttack();
                } else if(targetDirection.equals("None")) {
                    return new com.jbs.platformerengine.gamedata.entity.mob.attack.sword.ground.base.BaseAttack();
                }
            }
        }

        return new com.jbs.platformerengine.gamedata.entity.mob.attack.sword.ground.base.BaseAttack();
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
            if(attackHitBoxData.attackFrameList.contains(currentFrame)
            || attackHitBoxData.attackFrameList.contains(-1)) {
                Rect attackHitBox = attackHitBoxData.getAttackHitBox(thisMob, currentFrame);
                shapeRenderer.rect((int) attackHitBox.x, (int) attackHitBox.y, attackHitBox.width, attackHitBox.height);
            }
        }

        shapeRenderer.end();
    }

    public void continueAttackChain(Mob thisMob) {
        AttackData nextAttack = null;
        int currentAttackStartFrame = -1;
        for(Integer startFrame : attackChainMap.keySet()) {
            if(currentFrame >= startFrame
            && startFrame > currentAttackStartFrame) {
                nextAttack = attackChainMap.get(startFrame);
                currentAttackStartFrame = startFrame;
            }
        }

        if(nextAttack != null) {
            thisMob.attackData = nextAttack;
        }
    }
}
