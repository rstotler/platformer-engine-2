package com.jbs.platformerengine.screen;

import java.util.Random;

public class AnimatedObject {
    public String imageName;

    public int currentFrameTick;
    public int currentFrameNum;
    public int maxFrameNum;
    public int frameLength;

    public AnimatedObject(String imageName, ImageManager imageManager) {
        this.imageName = imageName;

        this.currentFrameTick = 0;
        frameLength = 1;

        maxFrameNum = 0;
        if(imageManager != null) {
            if(imageManager.breakableImage.containsKey(imageName)) {
                maxFrameNum = imageManager.breakableImage.get(imageName).get("Default").size();
                currentFrameNum = new Random().nextInt(maxFrameNum);
            } else if(imageManager.mobImage.containsKey(imageName)) {
                maxFrameNum = imageManager.mobImage.get(imageName).get("Default").size();
                currentFrameNum = new Random().nextInt(maxFrameNum);
            }
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
