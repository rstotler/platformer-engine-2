package com.jbs.platformerengine.screen;

import com.jbs.platformerengine.gamedata.Point;

public class AnimatedObject {
    public String imageName;
    public Point spriteLocation;

    public int currentFrameNum;
    public int currentFrameTick;
    public int maxFrameNum;
    public int frameLength;

    public AnimatedObject(String objectName, Point spriteLocation, int frameLength, ImageManager imageManager) {
        this.imageName = objectName;
        this.spriteLocation = spriteLocation;

        this.currentFrameNum = 0;
        this.currentFrameTick = 0;
        this.frameLength = frameLength;

        this.maxFrameNum = 0;
        if(imageManager.animatedImage.containsKey(objectName)) {
            this.maxFrameNum = imageManager.animatedImage.get(objectName).get("Default").size();
        }
    }

    public void updateAnimation() {
        if(maxFrameNum > 0) {
            currentFrameTick += 1;
            if(currentFrameTick >= frameLength) {
                currentFrameTick = 0;
                currentFrameNum += 1;
                if(currentFrameNum >= maxFrameNum) {
                    currentFrameNum = 0;
                }
            }
        }
    }
}
