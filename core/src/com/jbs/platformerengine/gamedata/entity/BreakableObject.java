package com.jbs.platformerengine.gamedata.entity;

import com.jbs.platformerengine.gamedata.Point;
import com.jbs.platformerengine.screen.AnimatedObject;
import com.jbs.platformerengine.screen.ImageManager;

public class BreakableObject extends AnimatedObject {
    public BreakableObject(String objectName, Point spriteLocation, int frameLength, ImageManager imageManager) {
        super(objectName, spriteLocation, frameLength, imageManager);
    }
}
