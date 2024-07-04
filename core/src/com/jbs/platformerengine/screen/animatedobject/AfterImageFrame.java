package com.jbs.platformerengine.screen.animatedobject;

import com.jbs.platformerengine.gamedata.Rect;

public class AfterImageFrame {
    public String imageName;
    public String imageType;
    public String animationName;
    public int targetFrame;

    public Rect displayRect;
    public String facingDirection;

    public AfterImageFrame(String imageName, String imageType, String animationName, int targetFrame, Rect displayRect, String facingDirection) {
        this.imageName = imageName;
        this.imageType = imageType;
        this.animationName = animationName;
        this.targetFrame = targetFrame;

        this.displayRect = new Rect(displayRect);
        this.facingDirection = facingDirection;
    }
}
