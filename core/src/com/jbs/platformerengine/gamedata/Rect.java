package com.jbs.platformerengine.gamedata;

public class Rect {
    public int x;
    public int y;
    public int width;
    public int height;

    public Rect(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rect(int width, int height) {
        x = 0;
        y = 0;
        this.width = width;
        this.height = height;
    }

    public boolean rectCollide(Rect rect) {
        return x + width >= rect.x
            && x <= rect.x + rect.width
            && y + height >= rect.y
            && y <= rect.y + rect.height;
    }

    public Point getMiddle() {
        return new Point(x + (width / 2), y + (height / 2));
    }
}
