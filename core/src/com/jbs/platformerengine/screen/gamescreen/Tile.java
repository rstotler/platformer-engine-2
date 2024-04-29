package com.jbs.platformerengine.screen.gamescreen;

import com.jbs.platformerengine.gamedata.Point;

public class Tile {
    public String tileSet;
    public String tileName;
    public int num;

    public String tileShape;

    public String changeArea;
    public Point changeLocation;

    public Tile(String tileSet, String tileName, int num){ 
        this.tileSet = tileSet;
        this.tileName = tileName;
        this.num = num;

        initTileShape();

        changeArea = "None";
    }

    public Tile(String tileSet, String tileName){ 
        this.tileSet = tileSet;
        this.tileName = tileName;
        num = 1;

        initTileShape();

        changeArea = "None";
    }

    public Tile(String changeArea, Point changeLocation) {
        tileSet = "None";
        tileName = "None";
        num = 1;
        
        tileShape = "None";

        this.changeArea = changeArea;
        this.changeLocation = changeLocation;
    }

    public void initTileShape() {
        tileShape = "Square";

        if(tileName.equals("Ceiling-Ramp")) {
            if(num == 1) {
                tileShape = "Ceiling-Ramp-Right";
            } else {
                tileShape = "Ceiling-Ramp-Left";
            }
        } else if(tileName.equals("Ramp")) {
            if(num == 1) {
                tileShape = "Ramp-Right";
            } else {
                tileShape = "Ramp-Left";
            }
        } else if(tileName.equals("Ramp-Bottom")) {
            if(num == 1) {
                tileShape = "Ramp-Right-Half-Bottom";
            } else {
                tileShape = "Ramp-Left-Half-Bottom";
            }
        } else if(tileName.equals("Ramp-Top")) {
            if(num == 1) {
                tileShape = "Ramp-Right-Half-Top";
            } else {
                tileShape = "Ramp-Left-Half-Top";
            }
        } else if(tileName.equals("Square-Half")) {
            tileShape = "Square-Half";
        }
    }
}
