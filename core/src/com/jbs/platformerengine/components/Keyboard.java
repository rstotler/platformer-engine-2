package com.jbs.platformerengine.components;

import java.util.ArrayList;

public class Keyboard {
    public boolean left;
    public boolean right;
    public boolean up;
    public boolean down;
    public boolean shift;

    public ArrayList<String> lastDown;
    public ArrayList<String> lastUp;

    public Keyboard() {
        left = false;
        right = false;
        up = false;
        down = false;
        shift = false;

        lastDown = new ArrayList<>();
        lastUp = new ArrayList<>();
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

        lastDown.add(key);
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

        lastUp.add(key);
    }
}
