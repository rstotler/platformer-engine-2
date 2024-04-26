package com.jbs.platformerengine.screen.gamescreen;

import java.util.ArrayList;

import com.jbs.platformerengine.gamedata.entity.BreakableObject;
import com.jbs.platformerengine.gamedata.entity.Mob;

public class CellCollidables {
    public int chunkX;
    public int chunkY;

    public ArrayList<Mob> mobList;
    public ArrayList<BreakableObject> breakableList;

    public CellCollidables(int chunkX, int chunkY) {
        this.chunkX = chunkX;
        this.chunkY = chunkY;

        mobList = new ArrayList<>();
        breakableList = new ArrayList<>();
    }
}
