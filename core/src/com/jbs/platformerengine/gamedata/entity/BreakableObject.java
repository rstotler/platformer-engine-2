package com.jbs.platformerengine.gamedata.entity;

import com.jbs.platformerengine.gamedata.CollidableObject;
import com.jbs.platformerengine.gamedata.Point;
import com.jbs.platformerengine.gamedata.Rect;
import com.jbs.platformerengine.screen.ImageManager;

public class BreakableObject extends CollidableObject {
    public BreakableObject(String objectName, Point spriteLocation, int frameLength, ImageManager imageManager) {
        super(objectName, spriteLocation, frameLength, imageManager);

        // Load Breakable Object Hit Box Size //
        hitBoxArea = new Rect(spriteLocation.x, spriteLocation.y, 5, 12);
    }
}
