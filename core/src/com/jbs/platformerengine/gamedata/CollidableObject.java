package com.jbs.platformerengine.gamedata;

import com.jbs.platformerengine.screen.AnimatedObject;
import com.jbs.platformerengine.screen.ImageManager;

public class CollidableObject extends AnimatedObject {
    public Rect hitBoxArea;

    public CollidableObject() {
        super();
    }

    public CollidableObject(String objectName, Point spriteLocation, int frameLength, ImageManager imageManager) {
        super(objectName, spriteLocation, frameLength, imageManager);
    }
}
