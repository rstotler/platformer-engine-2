package com.jbs.platformerengine.gamedata.entity.mob.attack;

import java.util.*;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.jbs.platformerengine.gamedata.Rect;
import com.jbs.platformerengine.gamedata.entity.CollidableObject;
import com.jbs.platformerengine.gamedata.entity.mob.Mob;
import com.jbs.platformerengine.gamedata.entity.mob.attack.sword.ground.*;
import com.jbs.platformerengine.gamedata.entity.mob.attack.sword.flying.*;

public class AttackData {
    public int currentFrame;
    public int attackFrameLength;

    public int canWalkOnFrame;

    public ArrayList<AttackHitBoxData> attackHitBoxList;
    public ArrayList<CollidableObject> hitObjectList;

    public AttackData() {
        currentFrame = 0;
        attackFrameLength = 10;

        canWalkOnFrame = 9999;

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

    public static HashMap<String, AttackData> loadAttackData() {
        // HashMap<String, AttackData> attackData = new HashMap<>();

        // float[] attackDecayTimerMax = new float[] {24f, 24f, 20f};
        // int[] attackFrameStart = new int[] {3, 3, 0};
        // int[] attackFrameEnd = new int[] {7, 7, 7};
        // int[] attackComboStartFrame = new int[] {12, 12, -1};
        // int[] attackXMod = new int[] {20, 25, 25};
        // int[] attackYMod = new int[] {34, 29, 40};
        // int[] attackWidth = new int[] {50, 50, 30};
        // int[] attackHeight = new int[] {4, 4, 6};
        // int[] attackMoveWidth = new int[] {-1, -1, -1};
        // int[] attackMoveHeight = new int[] {-1, -1, -40};
        // AttackData attackDataSword01 = new AttackData(attackDecayTimerMax, attackFrameStart, attackFrameEnd, attackComboStartFrame, attackXMod, attackYMod, attackWidth, attackHeight, attackMoveWidth, attackMoveHeight);
        // attackData.put("Sword 01", attackDataSword01);

        // return attackData;
        return null;
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
