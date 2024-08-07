package com.jbs.platformerengine.screen.gamescreen;

import java.util.ArrayList;
import java.util.Random;

import com.jbs.platformerengine.gamedata.Point;
import com.jbs.platformerengine.gamedata.entity.BreakableObject;
import com.jbs.platformerengine.gamedata.entity.mob.Mob;

public class CellCollidables {
    public int chunkX;
    public int chunkY;
    public Point location;
    public int cellColor;

    public ArrayList<Mob> mobList;
    public ArrayList<BreakableObject> breakableList;

    public CellCollidables(int chunkX, int chunkY, int x, int y) {
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        location = new Point(x, y);
        cellColor = new Random().nextInt(75) + 15;

        mobList = new ArrayList<>();
        breakableList = new ArrayList<>();
    }

    public String toString() {
        int xLoc = (chunkX * 20) + location.x;
        int yLoc = (chunkY * 12) + location.y;
        return "(" + xLoc + ", " + yLoc + ")";
    }
}
