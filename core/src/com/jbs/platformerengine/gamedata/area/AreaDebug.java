package com.jbs.platformerengine.gamedata.area;

import java.util.*;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jbs.platformerengine.gamedata.Rect;
import com.jbs.platformerengine.screen.ImageManager;
import com.jbs.platformerengine.screen.gamescreen.ScreenChunk;
import com.jbs.platformerengine.screen.gamescreen.Tile;

public class AreaDebug extends AreaData {
    public AreaDebug() {
        levelName = "Debug";
        size = new Rect(2, 7);
        tileSetList = new ArrayList<>(Arrays.asList("Debug"));
    }

    public void loadArea(ScreenChunk[][] screenChunks, SpriteBatch spriteBatch, ImageManager imageManager) {

        // Bottom Floor //
        for(int i = 0; i < screenChunks.length; i++) {
            for(int ii = 0; ii < screenChunks[0][0].tiles.length; ii++) {
                screenChunks[i][0].tiles[ii][0] = new Tile("Debug", "Square");
            }
        }

        // Ceiling //
        for(int i = 0; i < screenChunks[0][0].tiles.length; i++) {
            for(int ii = 10; ii < 30; ii++) {
                screenChunks[0][0].tiles[i][ii] = new Tile("Debug", "Square");
            }
        }
        for(int i = 0; i < 12; i++) {
            screenChunks[0][0].tiles[i + 9][9] = new Tile("Debug", "Square");
        }
        for(int i = 0; i < 7; i++) {
            screenChunks[0][0].tiles[i + 26][9] = new Tile("Debug", "Square");
        }
        for(int i = 0; i < 3; i++) {
            screenChunks[0][0].tiles[i + 22][10] = null;
        }

        // Ceiling Ramps //
        screenChunks[0][0].tiles[8][9] = new Tile("Debug", "Ceiling-Ramp", 1);
        screenChunks[0][0].tiles[21][9] = new Tile("Debug", "Ceiling-Ramp", 2);
        screenChunks[0][0].tiles[22][10] = new Tile("Debug", "Ceiling-Ramp", 2);
        screenChunks[0][0].tiles[24][10] = new Tile("Debug", "Ceiling-Ramp", 1);
        screenChunks[0][0].tiles[25][9] = new Tile("Debug", "Ceiling-Ramp", 1);
        screenChunks[0][0].tiles[33][9] = new Tile("Debug", "Ceiling-Ramp", 2);

        // Left Wall //
        for(int i = 1; i < 10; i++) {
            screenChunks[0][0].tiles[0][i] = new Tile("Debug", "Square");
        }

        screenChunks[0][0].tiles[3][1] = new Tile("Debug", "Ramp", 1);
        screenChunks[0][0].tiles[4][1] = new Tile("Debug", "Square");
        screenChunks[0][0].tiles[5][1] = new Tile("Debug", "Ramp", 2);
        
        screenChunks[0][0].tiles[7][1] = new Tile("Debug", "Ramp", 1);
        screenChunks[0][0].tiles[8][1] = new Tile("Debug", "Square");
        screenChunks[0][0].tiles[8][2] = new Tile("Debug", "Square");
        screenChunks[0][0].tiles[8][3] = new Tile("Debug", "Square");
        screenChunks[0][0].tiles[8][4] = new Tile("Debug", "Ramp", 2);
        screenChunks[0][0].tiles[9][1] = new Tile("Debug", "Ramp", 2);

        screenChunks[0][0].tiles[11][1] = new Tile("Debug", "Ramp", 1);
        screenChunks[0][0].tiles[12][1] = new Tile("Debug", "Square");
        screenChunks[0][0].tiles[12][2] = new Tile("Debug", "Square-Half");
        screenChunks[0][0].tiles[13][1] = new Tile("Debug", "Ramp", 2);

        screenChunks[0][0].tiles[15][1] = new Tile("Debug", "Square");
        screenChunks[0][0].tiles[15][2] = new Tile("Debug", "Square");
        screenChunks[0][0].tiles[15][3] = new Tile("Debug", "Square-Half");

        screenChunks[0][0].tiles[18][1] = new Tile("Debug", "Ramp-Bottom", 1);
        screenChunks[0][0].tiles[19][1] = new Tile("Debug", "Square-Half");
        screenChunks[0][0].tiles[20][1] = new Tile("Debug", "Ramp-Bottom", 2);

        screenChunks[0][0].tiles[23][1] = new Tile("Debug", "Ramp-Top", 1);
        screenChunks[0][0].tiles[24][1] = new Tile("Debug", "Square");
        screenChunks[0][0].tiles[24][2] = new Tile("Debug", "Square");
        screenChunks[0][0].tiles[24][3] = new Tile("Debug", "Ramp-Top", 2);
        screenChunks[0][0].tiles[25][1] = new Tile("Debug", "Ramp-Top", 2);

        screenChunks[0][0].tiles[28][1] = new Tile("Debug", "Ramp-Bottom", 1);
        screenChunks[0][0].tiles[29][1] = new Tile("Debug", "Ramp-Top", 1);
        screenChunks[0][0].tiles[30][1] = new Tile("Debug", "Ramp-Top", 2);
        screenChunks[0][0].tiles[31][1] = new Tile("Debug", "Ramp-Bottom", 2);

        screenChunks[0][0].tiles[34][1] = new Tile("Debug", "Ramp", 1);
        screenChunks[0][0].tiles[35][1] = new Tile("Debug", "Square");
        screenChunks[0][0].tiles[35][2] = new Tile("Debug", "Ramp", 1);
        screenChunks[0][0].tiles[36][2] = new Tile("Debug", "Square");
        screenChunks[0][0].tiles[36][3] = new Tile("Debug", "Ramp", 1);
        screenChunks[0][0].tiles[37][3] = new Tile("Debug", "Square");
        screenChunks[0][0].tiles[37][4] = new Tile("Debug", "Ramp", 1);
        screenChunks[0][0].tiles[36][1] = new Tile("Debug", "Square");
        screenChunks[0][0].tiles[37][1] = new Tile("Debug", "Square");
        screenChunks[0][0].tiles[37][2] = new Tile("Debug", "Square");
        
        // TestPillar //
        // for(int i = 0; i < 42; i++) {
        //     screenChunks[0][0].tiles[10][1 + i] = new Tile("Debug", "Square");
        //     screenChunks[0][0].tiles[11][1 + i] = new Tile("Debug", "Square");
        // }

        // Floor 2 Floor //
        for(int i = 0; i < screenChunks[0][1].tiles.length; i++) {
            if(i < 10 || i > 11) {
                screenChunks[0][1].tiles[i][0] = new Tile("Debug", "Square");
            }
        }
    }
}