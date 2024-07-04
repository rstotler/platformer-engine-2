package com.jbs.platformerengine.gamedata;

public class Rect {
    public float x;
    public float y;
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

    public Rect(Rect rect) {
        this.x = rect.x;
        this.y = rect.y;
        this.width = rect.width;
        this.height = rect.height;
    }

    public boolean rectCollide(Rect rect) {
        return x + width >= rect.x
            && x <= rect.x + rect.width
            && y + height >= rect.y
            && y <= rect.y + rect.height;
    }

    public Point getMiddle() {
        return new Point(((int) x) + (width / 2), ((int) y) + (height / 2));
    }
}
