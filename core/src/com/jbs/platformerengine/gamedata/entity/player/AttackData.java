package com.jbs.platformerengine.gamedata.entity.player;

import java.util.HashMap;

public class AttackData {
    public float[] attackDecayTimerMax;
    public int[] attackFrameStart;
    public int[] attackFrameEnd;
    public int[] attackComboStartFrame;

    public int[] attackXMod;
    public int[] attackYMod;
    public int[] attackWidth;
    public int[] attackHeight;
    public int[] attackMoveWidth;
    public int[] attackMoveHeight;

    public AttackData(float[] attackDecayTimerMax, int[] attackFrameStart, int[] attackFrameEnd, int[] attackComboStartFrame, int[] attackXMod, int[] attackYMod, int[] attackWidth, int[] attackHeight, int[] attackMoveWidth, int[] attackMoveHeight) {
        this.attackDecayTimerMax = attackDecayTimerMax;
        this.attackFrameStart = attackFrameStart;
        this.attackFrameEnd = attackFrameEnd;
        this.attackComboStartFrame = attackComboStartFrame;
        
        this.attackXMod = attackXMod;
        this.attackYMod = attackYMod;
        this.attackWidth = attackWidth;
        this.attackHeight = attackHeight;
        this.attackMoveWidth = attackMoveWidth;
        this.attackMoveHeight = attackMoveHeight;
    }

    public static HashMap<String, AttackData> loadAttackData() {
        HashMap<String, AttackData> attackData = new HashMap<>();

        float[] attackDecayTimerMax = new float[] {24f, 24f, 20f};
        int[] attackFrameStart = new int[] {3, 3, 2};
        int[] attackFrameEnd = new int[] {7, 7, 7};
        int[] attackComboStartFrame = new int[] {12, 12, -1};
        int[] attackXMod = new int[] {20, 25, 25};
        int[] attackYMod = new int[] {34, 29, 40};
        int[] attackWidth = new int[] {50, 50, 30};
        int[] attackHeight = new int[] {4, 4, 6};
        int[] attackMoveWidth = new int[] {-1, -1, -1};
        int[] attackMoveHeight = new int[] {-1, -1, -40};
        AttackData attackDataSword01 = new AttackData(attackDecayTimerMax, attackFrameStart, attackFrameEnd, attackComboStartFrame, attackXMod, attackYMod, attackWidth, attackHeight, attackMoveWidth, attackMoveHeight);
        attackData.put("Sword 01", attackDataSword01);

        return attackData;
    }
}
