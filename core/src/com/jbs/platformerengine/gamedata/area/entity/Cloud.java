package com.jbs.platformerengine.gamedata.area.entity;

import java.util.Random;

import com.jbs.platformerengine.gamedata.Point;

public class Cloud {
    public Point location;
    public int num;
    public int tintColor;

    public int moveDir;
    public int moveCount;
    public float moveSpeed;

    public Cloud(int xLoc, int yLoc, int moveDir) {
        location = new Point(xLoc, yLoc);
        num = new Random().nextInt(20);
        tintColor = new Random().nextInt(75) + 50;

        this.moveDir = moveDir;
        moveCount = 0;
        moveSpeed = new Random().nextFloat() * .05f;
    }

    public void update() {
        moveCount += 1;
    }

    public int getMoveMod() {
        return (int) (moveSpeed * moveCount * moveDir);
    }
}
