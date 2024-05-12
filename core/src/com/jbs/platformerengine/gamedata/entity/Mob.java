package com.jbs.platformerengine.gamedata.entity;

import java.util.ArrayList;

import com.jbs.platformerengine.gamedata.entity.player.Player;

public class Mob extends Player {
    ArrayList<String> aiPatternList;

    public Mob() {
        super();

        aiPatternList = new ArrayList<>();
        aiPatternList.add("Patrol");

        facingDirection = "Left";
        hitBoxArea.x += 250;
        hitBoxArea.y += 150;
    }

    public void updateAI() {
        if(aiPatternList.contains("Patrol")) {
            if(facingDirection.equals("Left")) {
                velocity.x = -moveSpeed;
            } else {
                velocity.x = moveSpeed;
            }
        }
    }
}
