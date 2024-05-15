package com.jbs.platformerengine.gamedata;

import com.jbs.platformerengine.screen.AnimatedObject;
import com.jbs.platformerengine.screen.ImageManager;

public class CollidableObject extends AnimatedObject {
    public Rect hitBoxArea;

    public CollidableObject(String imageName, ImageManager imageManager) {
        this(imageName, new Point(-1, -1), imageManager);
    }

    public CollidableObject(String objectName, Point spriteLocation, ImageManager imageManager) {
        super(objectName, imageManager);

        hitBoxArea = new Rect(spriteLocation.x, spriteLocation.y, 0, 0);
    }
}
