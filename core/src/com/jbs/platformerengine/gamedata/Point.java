package com.jbs.platformerengine.gamedata;

public class Point {
    public int x;
    public int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public float getDistance(Point targetPoint) {
        int xx = x - targetPoint.x;
        int yy = y - targetPoint.y;
        float distance = (float) Math.sqrt(Math.pow(xx, 2) + Math.pow(yy, 2));

        return distance;
    }
}
