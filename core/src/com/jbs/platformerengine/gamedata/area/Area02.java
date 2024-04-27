package com.jbs.platformerengine.gamedata.area;

import java.util.*;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jbs.platformerengine.gamedata.Rect;
import com.jbs.platformerengine.screen.ImageManager;
import com.jbs.platformerengine.screen.gamescreen.ScreenChunk;

public class Area02 extends AreaData {
    public Area02() {
        levelName = "Area02";
        size = new Rect(3, 3);
    
        defaultTileSet = "Debug";
        defaultTileName = "Square";
        defaultTileNum = 1;
    
        tileSetList = new ArrayList<>(Arrays.asList("Debug"));
        // animatedImageList = new ArrayList<>(Arrays.asList());
    }
    
    public void loadArea(ScreenChunk[][] screenChunks, SpriteBatch spriteBatch, ImageManager imageManager) {
        createFloor(screenChunks, "Debug", false);
    }
}
