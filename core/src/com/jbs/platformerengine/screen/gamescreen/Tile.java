package com.jbs.platformerengine.screen.gamescreen;

public class Tile {
    public String type;
    public int num;

    public Tile(String type){ 
        this.type = type;
        num = 1;
    }

    public Tile(String type, int num) {
        this.type = type;
        this.num = num;
    }
}
