package com.jbs.platformerengine.gamedata.entity;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.jbs.platformerengine.gamedata.Rect;
import com.jbs.platformerengine.gamedata.area.AreaData;
import com.jbs.platformerengine.gamedata.entity.player.Player;

public class Mob extends Player {
    ArrayList<String> aiPatternList;
    public int updateTimer;

    public Mob() {
        super();

        aiPatternList = new ArrayList<>();
        aiPatternList.add("Patrol");

        updateTimer = -1;

        facingDirection = "Left";
        hitBoxArea = new Rect(250, 50, 16, 48);
    }

    public void updateAI(AreaData areaData) {
        if(aiPatternList.contains("Patrol")) {

            // Reverse Direction //
            if((facingDirection.equals("Left") && hitBoxArea.x <= 0)
            || (facingDirection.equals("Right") && hitBoxArea.x + hitBoxArea.width >= Gdx.graphics.getWidth() * areaData.screenChunks.length)) {
                if(facingDirection.equals("Left")) {
                    facingDirection = "Right";
                } else {
                    facingDirection = "Left";
                }
            }

            // Update Velocity //
            if(facingDirection.equals("Left")) {
                velocity.x = -moveSpeed;
            } else {
                velocity.x = moveSpeed;
            }
        }
    }
}
