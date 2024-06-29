package com.jbs.platformerengine.screen;

import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jbs.platformerengine.gamedata.Rect;

public class AnimatedObject {
    public String imageName;
    public String imageType;

    public int currentFrameTick;
    public int currentFrameNum;
    public int maxFrameNum;
    public int frameLength;

    public AnimatedObject(String imageName, ImageManager imageManager) {
        this.imageName = imageName;
        imageType = "";

        this.currentFrameTick = 0;
        frameLength = 1;

        maxFrameNum = 0;
        if(imageManager != null) {
            if(imageManager.breakableImage.containsKey(imageName)) {
                maxFrameNum = imageManager.breakableImage.get(imageName).get("Default").size();
                currentFrameNum = new Random().nextInt(maxFrameNum);
                imageType = "BreakableObject";
            } else if(imageManager.mobImage.containsKey(imageName)) {
                maxFrameNum = imageManager.mobImage.get(imageName).get("Default").size();
                currentFrameNum = new Random().nextInt(maxFrameNum);
                imageType = "Mob";
            }
        }
    }

    public void renderAnimatedObject(ImageManager imageManager, SpriteBatch spriteBatch, Rect hitBoxArea, String facingDirection, boolean centerSprite) {
        Texture texture = null;
        if(imageType.equals("BreakableObject")
        && imageManager.breakableImage.containsKey(imageName)) {
            texture = imageManager.breakableImage.get(imageName).get("Default").get(currentFrameNum);
        }
        else if(imageType.equals("Mob")
        && imageManager.mobImage.containsKey(imageName)) {
            texture = imageManager.mobImage.get(imageName).get("Default").get(currentFrameNum);
        }
        
        if(texture !=  null) {
            int spriteX = (int) hitBoxArea.x;
            int spriteY = (int) hitBoxArea.y;
            if(centerSprite) {
                spriteX = (int) hitBoxArea.x - ((texture.getWidth() - hitBoxArea.width) / 2);
                spriteY = (int) hitBoxArea.y - ((texture.getHeight() - hitBoxArea.height) / 2);
            }
            
            boolean flipDirection = false;
            if(facingDirection.equals("Left")) {
                flipDirection = true;
            }
    
            spriteBatch.begin();
            spriteBatch.draw(texture, spriteX, spriteY, texture.getWidth(), texture.getHeight(), 0, 0, texture.getWidth(), texture.getHeight(), flipDirection, false);
            spriteBatch.end();
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
