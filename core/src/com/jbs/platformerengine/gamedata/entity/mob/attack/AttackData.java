package com.jbs.platformerengine.gamedata.entity.mob.attack;

import java.util.*;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.jbs.platformerengine.gamedata.Rect;
import com.jbs.platformerengine.gamedata.entity.mob.attack.sword.ground.*;
import com.jbs.platformerengine.gamedata.entity.mob.attack.sword.flying.*;

public class AttackData {
    public int[] attackFrameStart;
    public int[] attackFrameEnd;
    public int[] attackComboStartFrame;

    public int[] attackXMod;
    public int[] attackYMod;
    public int[] attackWidth;
    public int[] attackHeight;
    public int[] attackMoveWidth;
    public int[] attackMoveHeight;

    public int attackDecayTimer;
    public int attackDecayTimerMax;
    public ArrayList<Integer> attackFrameList;

    public AttackData() {
        attackDecayTimer = -1;
        attackDecayTimerMax = 5;
        attackFrameList = new ArrayList<>();
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
        HashMap<String, AttackData> attackData = new HashMap<>();

        float[] attackDecayTimerMax = new float[] {24f, 24f, 20f};
        int[] attackFrameStart = new int[] {3, 3, 0};
        int[] attackFrameEnd = new int[] {7, 7, 7};
        int[] attackComboStartFrame = new int[] {12, 12, -1};
        int[] attackXMod = new int[] {20, 25, 25};
        int[] attackYMod = new int[] {34, 29, 40};
        int[] attackWidth = new int[] {50, 50, 30};
        int[] attackHeight = new int[] {4, 4, 6};
        int[] attackMoveWidth = new int[] {-1, -1, -1};
        int[] attackMoveHeight = new int[] {-1, -1, -40};
        // AttackData attackDataSword01 = new AttackData(attackDecayTimerMax, attackFrameStart, attackFrameEnd, attackComboStartFrame, attackXMod, attackYMod, attackWidth, attackHeight, attackMoveWidth, attackMoveHeight);
        // attackData.put("Sword 01", attackDataSword01);

        // return attackData;
        return null;
    }

    public Rect getAttackHitBox() {
        return null;
    }

    public void renderAttackHitBox(ShapeRenderer shapeRenderer) {

    }

    public Rect getAttackHitBoxOld() {
        // float movePercent = 0f;
        // int xMove = 0;
        // int yMove = 0;
        // if(attackDecayTimer > attackData.get(getCurrentAttack()).attackFrameStart[attackCount - 1]
        // && attackDecayTimer < attackData.get(getCurrentAttack()).attackFrameEnd[attackCount - 1]) {
        //     movePercent = (attackDecayTimer - attackData.get(getCurrentAttack()).attackFrameStart[attackCount - 1] - 1) / (attackData.get(getCurrentAttack()).attackFrameEnd[attackCount - 1] - attackData.get(getCurrentAttack()).attackFrameStart[attackCount - 1] - 2);
        //     if(attackData.get(getCurrentAttack()).attackMoveWidth[attackCount - 1] != -1) {
        //         xMove = (int) (movePercent * attackData.get(getCurrentAttack()).attackMoveWidth[attackCount - 1]);
        //     }
        //     if(attackData.get(getCurrentAttack()).attackMoveHeight[attackCount - 1] != -1) {
        //         yMove = (int) (movePercent * attackData.get(getCurrentAttack()).attackMoveHeight[attackCount - 1]);
        //     }
        // }

        // int attackX = (int) hitBoxArea.x + (hitBoxArea.width / 2) + attackData.get(getCurrentAttack()).attackXMod[attackCount - 1] + xMove;
        // if(facingDirection.equals("Left")) {
        //     attackX -= attackData.get(getCurrentAttack()).attackWidth[attackCount - 1] + (attackData.get(getCurrentAttack()).attackXMod[attackCount - 1] * 2) + xMove;
        // }
        // int attackY = (int) hitBoxArea.y + attackData.get(getCurrentAttack()).attackYMod[attackCount - 1] + yMove;
        // if(ducking) {
        //     attackY -= 13;
        // }
        // int attackWidth = attackData.get(getCurrentAttack()).attackWidth[attackCount - 1];
        // int attackHeight = attackData.get(getCurrentAttack()).attackHeight[attackCount - 1];
        
        // return new Rect(attackX, attackY, attackWidth, attackHeight);
        return null;
    }

    public void renderAttackHitBoxOld(ShapeRenderer shapeRenderer) {
        // if(attackDecayTimer >= attackData.get(getCurrentAttack()).attackFrameStart[attackCount - 1]
        // && attackDecayTimer < attackData.get(getCurrentAttack()).attackFrameEnd[attackCount - 1]) {
        //     shapeRenderer.begin(ShapeType.Filled);

        //     if(attackCount == 1) {
        //         shapeRenderer.setColor(82/255f, 0/255f, 0/255f, 1f);
        //     } else if(attackCount == 2) {
        //         shapeRenderer.setColor(92/255f, 0/255f, 0/255f, 1f);
        //     } else {
        //         shapeRenderer.setColor(102/255f, 0/255f, 0/255f, 1f);
        //     }

        //     Rect attackHitBox = getAttackHitBox();
        //     shapeRenderer.rect((int) attackHitBox.x, (int) attackHitBox.y, attackHitBox.width, attackHitBox.height);
        //     shapeRenderer.end();
        // }
    }
}
