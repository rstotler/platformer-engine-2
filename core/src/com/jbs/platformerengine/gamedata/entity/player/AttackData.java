package com.jbs.platformerengine.gamedata.entity.player;

import java.util.HashMap;

public class AttackData {
    public float[] attackDecayTimerMax;
    public int[] attackFrameStart;
    public int[] attackFrameEnd;
    public int[] attackXMod;
    public int[] attackYMod;
    public int[] attackWidth;
    public int[] attackHeight;

    public AttackData(float[] attackDecayTimerMax, int[] attackFrameStart, int[] attackFrameEnd, int[] attackXMod, int[] attackYMod, int[] attackWidth, int[] attackHeight) {
        this.attackDecayTimerMax = attackDecayTimerMax;
        this.attackFrameStart = attackFrameStart;
        this.attackFrameEnd = attackFrameEnd;
        this.attackXMod = attackXMod;
        this.attackYMod = attackYMod;
        this.attackWidth = attackWidth;
        this.attackHeight = attackHeight;
    }

    public static HashMap<String, AttackData> loadAttackData() {
        HashMap<String, AttackData> attackData = new HashMap<>();

        float[] attackDecayTimerMax = new float[] {25f};
        int[] attackFrameStart = new int[] {3};
        int[] attackFrameEnd = new int[] {7};
        int[] attackXMod = new int[] {20};
        int[] attackYMod = new int[] {34};
        int[] attackWidth = new int[] {44};
        int[] attackHeight = new int[] {4};
        AttackData attackDataSword01 = new AttackData(attackDecayTimerMax, attackFrameStart, attackFrameEnd, attackXMod, attackYMod, attackWidth, attackHeight);
        attackData.put("Sword 01", attackDataSword01);

        return attackData;
    }
}
