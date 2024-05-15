package com.jbs.platformerengine.gamedata.entity;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.jbs.platformerengine.gamedata.Point;
import com.jbs.platformerengine.gamedata.Rect;
import com.jbs.platformerengine.gamedata.area.AreaData;
import com.jbs.platformerengine.gamedata.entity.player.Player;
import com.jbs.platformerengine.screen.ImageManager;

public class Mob extends Player {
    public int updateTimer;
    ArrayList<String> aiPatternList;

    public Mob(String imageName, Point location, ImageManager imageManager) {
        super(imageName, imageManager);

        updateTimer = -1;
        aiPatternList = new ArrayList<>();

        facingDirection = "Left";
        hitBoxArea = new Rect(location.x, location.y, 16, 48);

        loadMob(imageName);
    }

    public void loadMob(String imageName) {
        if(imageName.equals("Bat")) {
            hitBoxArea.width = 18;
            hitBoxArea.height = 12;
            flying = true;

            frameLength = 4;

            aiPatternList.add("Patrol");
        }
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
