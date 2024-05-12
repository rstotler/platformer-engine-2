package com.jbs.platformerengine.gamedata.area;

import java.util.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jbs.platformerengine.gamedata.Point;
import com.jbs.platformerengine.gamedata.Rect;
import com.jbs.platformerengine.gamedata.entity.Mob;
import com.jbs.platformerengine.screen.ImageManager;
import com.jbs.platformerengine.screen.gamescreen.GameScreen;
import com.jbs.platformerengine.screen.gamescreen.Tile;

public class Area02 extends AreaData {
    public Area02() {
        levelName = "Area02";
        size = new Rect(2, 3);
    
        defaultTileSet = "Debug";
        defaultTileName = "Square";
        defaultTileNum = 1;
    
        tileSetList = new ArrayList<>(Arrays.asList("Debug"));
        // animatedImageList = new ArrayList<>(Arrays.asList());

        outside = false;

        loadScreenChunks();
    }
    
    public void loadArea(SpriteBatch spriteBatch, ImageManager imageManager) {
        createFloor("Debug", false);

        // Exit To Area01 Bridge //
        int exitYLoc = 7;
        for(int y = 0; y < 7; y++) {
            int chunkY = (exitYLoc + y) / 48;
            int tileY = (exitYLoc + y) % 48;
            int exitX = (screenChunks.length * Gdx.graphics.getWidth()) - 32;
            screenChunks[0][chunkY].tiles[0][tileY] = new Tile("Area01", new Point(exitX, 600));
        }

        // Test Mobs //
        if(!initCheck) {
            Mob testMob = new Mob();
            GameScreen.addObjectToCellCollidables(screenChunks, testMob);
        }
        
        initCheck = true;
    }
}
