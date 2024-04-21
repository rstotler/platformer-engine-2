package com.jbs.platformerengine.gamedata.entity;

import com.jbs.platformerengine.gamedata.Point;
import com.jbs.platformerengine.gamedata.Rect;
import com.jbs.platformerengine.screen.AnimatedObject;
import com.jbs.platformerengine.screen.ImageManager;

public class BreakableObject extends AnimatedObject {
    public Rect hitBoxArea;

    public BreakableObject(String objectName, Point spriteLocation, int frameLength, ImageManager imageManager) {
        super(objectName, spriteLocation, frameLength, imageManager);

        hitBoxArea = new Rect(spriteLocation.x, spriteLocation.y, 5, 5);
    }
}
