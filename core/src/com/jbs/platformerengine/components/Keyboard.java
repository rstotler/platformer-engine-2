package com.jbs.platformerengine.components;

public class Keyboard {
    public boolean left;
    public boolean right;
    public boolean up;
    public boolean down;

    public String lastDown;
    public String lastUp;

    public Keyboard() {
        left = false;
        right = false;
        up = false;
        down = false;

        lastDown = "";
        lastUp = "";
    }

    public void keyDown(String key) {
        if(key.equals("Left")) {
            left = true;
        } else if(key.equals("Right")) {
            right = true;
        } else if(key.equals("Up")) {
            up = true;
        } else if(key.equals("Down")) {
            down = true;
        }

        lastDown = key;
    }

    public void keyUp(String key) {
        if(key.equals("Left")) {
            left = false;
        } else if(key.equals("Right")) {
            right = false;
        } else if(key.equals("Up")) {
            up = false;
        } else if(key.equals("Down")) {
            down = false;
        }

        lastUp = key;
    }
}
