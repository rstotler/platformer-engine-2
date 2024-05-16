package com.jbs.platformerengine.screen.gamescreen;

import java.util.ArrayList;

import com.jbs.platformerengine.gamedata.Point;
import com.jbs.platformerengine.gamedata.entity.BreakableObject;
import com.jbs.platformerengine.gamedata.entity.Mob;

public class CellCollidables {
    public int chunkX;
    public int chunkY;
    public Point location;

    public ArrayList<Mob> mobList;
    public ArrayList<BreakableObject> breakableList;

    public CellCollidables(int chunkX, int chunkY, int x, int y) {
        this.chunkX = chunkX;
        this.chunkY = chunkY;
        location = new Point(x, y);

        mobList = new ArrayList<>();
        breakableList = new ArrayList<>();
    }

    public String toString() {
        return "Chunk: " + chunkX + ", " + chunkY;
    }
}
