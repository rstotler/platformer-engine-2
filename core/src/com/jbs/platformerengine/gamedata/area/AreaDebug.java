package com.jbs.platformerengine.gamedata.area;

import java.util.*;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jbs.platformerengine.gamedata.Rect;
import com.jbs.platformerengine.screen.ImageManager;
import com.jbs.platformerengine.screen.gamescreen.Tile;

public class AreaDebug extends AreaData {
    public AreaDebug() {
        levelName = "Debug";
        size = new Rect(2, 7);

        defaultTileSet = "Debug";
        defaultTileName = "Square";
        defaultTileNum = 1;
        
        tileSetList = new ArrayList<>(Arrays.asList("Debug"));

        outside = false;

        loadScreenChunks();
    }

    public void loadArea(SpriteBatch spriteBatch, ImageManager imageManager) {

        // Bottom Floor //
        for(int i = 0; i < screenChunks.length; i++) {
            for(int ii = 0; ii < screenChunks[0][0].tiles.length; ii++) {
                screenChunks[i][0].tiles[ii][0] = new Tile("Debug", "Square", i, 0, ii, 0);
            }
        }

        // Ceiling //
        for(int i = 0; i < screenChunks[0][0].tiles.length; i++) {
            for(int ii = 10; ii < 30; ii++) {
                screenChunks[0][0].tiles[i][ii] = new Tile("Debug", "Square", 0, 0, i, ii);
            }
        }
        for(int i = 0; i < 12; i++) {
            screenChunks[0][0].tiles[i + 9][9] = new Tile("Debug", "Square", 0, 0, i + 9, 9);
        }
        for(int i = 0; i < 7; i++) {
            screenChunks[0][0].tiles[i + 26][9] = new Tile("Debug", "Square", 0, 0, i + 26, 9);
        }
        for(int i = 0; i < 3; i++) {
            screenChunks[0][0].tiles[i + 22][10] = null;
        }

        // Ceiling Ramps //
        screenChunks[0][0].tiles[8][9] = new Tile("Debug", "Ceiling-Ramp", 1, 0, 0, 8, 9);
        screenChunks[0][0].tiles[21][9] = new Tile("Debug", "Ceiling-Ramp", 2, 0, 0, 21, 9);
        screenChunks[0][0].tiles[22][10] = new Tile("Debug", "Ceiling-Ramp", 2, 0, 0, 22, 10);
        screenChunks[0][0].tiles[24][10] = new Tile("Debug", "Ceiling-Ramp", 1, 0, 0, 24, 10);
        screenChunks[0][0].tiles[25][9] = new Tile("Debug", "Ceiling-Ramp", 1, 0, 0, 25, 9);
        screenChunks[0][0].tiles[33][9] = new Tile("Debug", "Ceiling-Ramp", 2, 0, 0, 33, 9);

        // Left Wall //
        for(int i = 1; i < 10; i++) {
            screenChunks[0][0].tiles[0][i] = new Tile("Debug", "Square", 0, 0, 0, i);
        }

        screenChunks[0][0].tiles[3][1] = new Tile("Debug", "Ramp", 1, 0, 0, 3, 1);
        screenChunks[0][0].tiles[4][1] = new Tile("Debug", "Square", 0, 0, 4, 1);
        screenChunks[0][0].tiles[5][1] = new Tile("Debug", "Ramp", 2, 0, 0, 5, 1);
        
        screenChunks[0][0].tiles[7][1] = new Tile("Debug", "Ramp", 1, 0, 0, 7, 1);
        screenChunks[0][0].tiles[8][1] = new Tile("Debug", "Square", 0, 0, 8, 1);
        screenChunks[0][0].tiles[8][2] = new Tile("Debug", "Square", 0, 0, 8, 2);
        screenChunks[0][0].tiles[8][3] = new Tile("Debug", "Square", 0, 0, 8, 3);
        screenChunks[0][0].tiles[8][4] = new Tile("Debug", "Ramp", 2, 0, 0, 8, 4);
        screenChunks[0][0].tiles[9][1] = new Tile("Debug", "Ramp", 2, 0, 0, 9, 1);

        screenChunks[0][0].tiles[11][1] = new Tile("Debug", "Ramp", 1, 0, 0, 11, 1);
        screenChunks[0][0].tiles[12][1] = new Tile("Debug", "Square", 0, 0, 12, 1);
        screenChunks[0][0].tiles[12][2] = new Tile("Debug", "Square-Half", 0, 0, 12, 2);
        screenChunks[0][0].tiles[13][1] = new Tile("Debug", "Ramp", 2, 0, 0, 13, 1);

        screenChunks[0][0].tiles[15][1] = new Tile("Debug", "Square", 0, 0, 15, 1);
        screenChunks[0][0].tiles[15][2] = new Tile("Debug", "Square", 0, 0, 15, 2);
        screenChunks[0][0].tiles[15][3] = new Tile("Debug", "Square-Half", 0, 0, 15, 3);

        screenChunks[0][0].tiles[18][1] = new Tile("Debug", "Ramp-Bottom", 1, 0, 0, 18, 1);
        screenChunks[0][0].tiles[19][1] = new Tile("Debug", "Square-Half", 0, 0, 19, 1);
        screenChunks[0][0].tiles[20][1] = new Tile("Debug", "Ramp-Bottom", 2, 0, 0, 20, 1);

        screenChunks[0][0].tiles[23][1] = new Tile("Debug", "Ramp-Top", 1, 0, 0, 23, 1);
        screenChunks[0][0].tiles[24][1] = new Tile("Debug", "Square", 0, 0, 24, 1);
        screenChunks[0][0].tiles[24][2] = new Tile("Debug", "Square", 0, 0, 24, 2);
        screenChunks[0][0].tiles[24][3] = new Tile("Debug", "Ramp-Top", 2, 0, 0, 24, 3);
        screenChunks[0][0].tiles[25][1] = new Tile("Debug", "Ramp-Top", 2, 0, 0, 25, 1);

        screenChunks[0][0].tiles[28][1] = new Tile("Debug", "Ramp-Bottom", 1, 0, 0, 28, 1);
        screenChunks[0][0].tiles[29][1] = new Tile("Debug", "Ramp-Top", 1, 0, 0, 29, 1);
        screenChunks[0][0].tiles[30][1] = new Tile("Debug", "Ramp-Top", 2, 0, 0, 30, 1);
        screenChunks[0][0].tiles[31][1] = new Tile("Debug", "Ramp-Bottom", 2, 0, 0, 31, 1);

        screenChunks[0][0].tiles[34][1] = new Tile("Debug", "Ramp", 1, 0, 0, 34, 1);
        screenChunks[0][0].tiles[35][1] = new Tile("Debug", "Square", 0, 0, 35, 1);
        screenChunks[0][0].tiles[35][2] = new Tile("Debug", "Ramp", 1, 0, 0, 35, 2);
        screenChunks[0][0].tiles[36][2] = new Tile("Debug", "Square", 0, 0, 36, 2);
        screenChunks[0][0].tiles[36][3] = new Tile("Debug", "Ramp", 1, 0, 0, 36, 3);
        screenChunks[0][0].tiles[37][3] = new Tile("Debug", "Square", 0, 0, 37, 3);
        screenChunks[0][0].tiles[37][4] = new Tile("Debug", "Ramp", 1, 0, 0, 37, 4);
        screenChunks[0][0].tiles[36][1] = new Tile("Debug", "Square", 0, 0, 36, 1);
        screenChunks[0][0].tiles[37][1] = new Tile("Debug", "Square", 0, 0, 37, 1);
        screenChunks[0][0].tiles[37][2] = new Tile("Debug", "Square", 0, 0, 37, 2);
        
        // TestPillar //
        // for(int i = 0; i < 42; i++) {
        //     screenChunks[0][0].tiles[10][1 + i] = new Tile("Debug", "Square");
        //     screenChunks[0][0].tiles[11][1 + i] = new Tile("Debug", "Square");
        // }

        // Floor 2 Floor //
        for(int i = 0; i < screenChunks[0][1].tiles.length; i++) {
            if(i < 10 || i > 11) {
                screenChunks[0][1].tiles[i][0] = new Tile("Debug", "Square", 0, 1, i, 0);
            }
        }

        initCheck = true;
    }
}
