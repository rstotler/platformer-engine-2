package com.jbs.platformerengine.components;

public class Keyboard {
    public boolean left;
    public boolean right;
    public boolean up;
    public boolean down;
    public boolean shift;

    public String lastDown;
    public String lastUp;

    public Keyboard() {
        left = false;
        right = false;
        up = false;
        down = false;
        shift = false;

        lastDown = "";
        lastUp = "";
    }

    public void keyDown(String key) {
        if(key.equals("Left") || key.equals("A")) {
            left = true;
        } else if(key.equals("Right") || key.equals("D")) {
            right = true;
        } else if(key.equals("Up") || key.equals("W")) {
            up = true;
        } else if(key.equals("Down") || key.equals("S")) {
            down = true;
        } else if(key.equals("L-Shift") || key.equals("R-Shift")) {
            shift = true;
        }

        lastDown = key;
    }

    public void keyUp(String key) {
        if(key.equals("Left") || key.equals("A")) {
            left = false;
        } else if(key.equals("Right") || key.equals("D")) {
            right = false;
        } else if(key.equals("Up") || key.equals("W")) {
            up = false;
        } else if(key.equals("Down") || key.equals("S")) {
            down = false;
        } else if(key.equals("L-Shift") || key.equals("R-Shift")) {
            shift = false;
        }

        lastUp = key;
    }
}
