package com.jbs.platformerengine.gamedata.entity;

import com.jbs.platformerengine.gamedata.CollidableObject;
import com.jbs.platformerengine.gamedata.Point;
import com.jbs.platformerengine.gamedata.Rect;
import com.jbs.platformerengine.screen.ImageManager;

public class BreakableObject extends CollidableObject {
    public BreakableObject(String objectName, Point spriteLocation, ImageManager imageManager) {
        super(objectName, spriteLocation, imageManager);

        loadBreakableObject(objectName);
    }

    public void loadBreakableObject(String objectName) {
        if(objectName.equals("Torch_01")) {
            hitBoxArea.width = 5;
            hitBoxArea.height = 12;
            frameLength = 3;
        }
    }
}
