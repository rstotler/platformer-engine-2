package com.jbs.platformerengine.screen;

import java.util.Random;

import com.jbs.platformerengine.gamedata.Point;

public class AnimatedObject {
    public String imageName;
    public Point spriteLocation;

    public int currentFrameTick;
    public int currentFrameNum;
    public int maxFrameNum;
    public int frameLength;

    public AnimatedObject() {}

    public AnimatedObject(String imageName, Point spriteLocation, int frameLength, ImageManager imageManager) {
        this.imageName = imageName;
        this.spriteLocation = spriteLocation;

        this.currentFrameTick = 0;
        this.frameLength = frameLength;

        maxFrameNum = 0;
        if(imageManager.animatedImage.containsKey(imageName)) {
            maxFrameNum = imageManager.animatedImage.get(imageName).get("Default").size();
            currentFrameNum = new Random().nextInt(maxFrameNum);
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
