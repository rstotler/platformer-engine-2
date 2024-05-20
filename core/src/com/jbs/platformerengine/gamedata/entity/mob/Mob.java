package com.jbs.platformerengine.gamedata.entity.mob;

import java.util.*;

import com.jbs.platformerengine.gamedata.Point;
import com.jbs.platformerengine.gamedata.Rect;
import com.jbs.platformerengine.gamedata.entity.mob.logic.movement.*;
import com.jbs.platformerengine.gamedata.entity.player.Player;
import com.jbs.platformerengine.screen.ImageManager;

public class Mob extends Player {
    public int updateTimer;
    public int updateAnimationTimer;
    public MovementPattern movementPattern;

    public ArrayList<Player> enemyTargetList;

    public Mob(String imageName, Point location, ImageManager imageManager) {
        super(imageName, imageManager);

        updateTimer = -1;
        updateAnimationTimer = -1;
        movementPattern = null; 

        enemyTargetList = new ArrayList<>();

        facingDirection = "Left";
        if(new Random().nextInt(2) == 0) {
            facingDirection = "Right";
        }
        hitBoxArea = new Rect(location.x, location.y, 16, 48);

        loadMob(imageName);
    }

    public void loadMob(String imageName) {
        if(imageName.equals("Default")) {
            
        }
        
        else if(imageName.equals("Bat")) {
            hitBoxArea.width = 18;
            hitBoxArea.height = 12;
            flying = true;

            frameLength = 3;

            movementPattern = new Wander(this);
        }
    }

    public void updateVelocity(String targetAxis, float targetSpeed) {
        if(targetAxis.equals("X")) {
            if(facingDirection.equals("Left")) {
                velocity.x = -targetSpeed;
            } else {
                velocity.x = targetSpeed;
            }
        } else {
            velocity.y = targetSpeed;
        }
    }

    public void updateVelocity(float targetXSpeed, float targetYSpeed) {
        if(facingDirection.equals("Left")) {
            velocity.x = -targetXSpeed;
        } else {
            velocity.x = targetXSpeed;
        }

        velocity.y = targetYSpeed;
    }

    public void reverseDirection() {
        if(facingDirection.equals("Left")) {
            facingDirection = "Right";
        } else {
            facingDirection = "Left";
        }
    }
}
