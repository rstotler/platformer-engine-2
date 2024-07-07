package com.jbs.platformerengine.screen.animatedobject;

import java.util.*;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jbs.platformerengine.gamedata.Rect;
import com.jbs.platformerengine.gamedata.entity.mob.Mob;
import com.jbs.platformerengine.screen.ImageManager;

public class AnimatedObject {
    public String imageName;
    public String imageType;

    public int currentFrameTick;
    public int currentFrameNum;
    public int maxFrameNum;
    public int frameLength;

    public boolean displaySprite;
    public boolean displayAfterImage;
    public ArrayList<AfterImageFrame> afterImageFrameList;
    public int afterImageLength;

    public AnimatedObject(String imageName, ImageManager imageManager) {
        this.imageName = imageName;
        imageType = "";
        
        this.currentFrameTick = 0;
        frameLength = 1;

        displaySprite = true;
        displayAfterImage = false;
        afterImageFrameList = new ArrayList<>();
        afterImageLength = 7;
        
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

    public void updateAfterImage(Mob mob) {
        int lastFrameNumIndex = afterImageFrameList.size() - 1;

        if((mob.velocity.x != 0 || mob.velocity.y != 0 || (mob.inAir() && !mob.flying))
        && !(afterImageFrameList.size() > 0 && afterImageFrameList.get(lastFrameNumIndex).targetFrame == currentFrameNum)) {
            AfterImageFrame afterImageFrame = new AfterImageFrame(imageName, imageType, "Default", currentFrameNum, mob.hitBoxArea, mob.facingDirection);
            afterImageFrameList.add(afterImageFrame);
        }
    }

    public void renderAfterImage(ImageManager imageManager, SpriteBatch spriteBatch) {
        spriteBatch.begin();
        int srcFunc = spriteBatch.getBlendSrcFunc();
        int destFunc = spriteBatch.getBlendDstFunc();
        spriteBatch.setShader(imageManager.shaderProgramColorChannel);
        
        for(int i = 0; i < afterImageFrameList.size(); i++) {
            AfterImageFrame afterImageFrame = afterImageFrameList.get(i);
            Texture texture = null;
            if(imageManager.mobImage.containsKey(afterImageFrame.imageName)) {
                texture = imageManager.mobImage.get(afterImageFrame.imageName).get("Default").get(afterImageFrame.targetFrame);
            }

            if(texture != null) {
                int spriteX = (int) afterImageFrame.displayRect.x;
                int spriteY = (int) afterImageFrame.displayRect.y;
                
                // Center Sprite //
                spriteX = (int) afterImageFrame.displayRect.x - ((texture.getWidth() - afterImageFrame.displayRect.width) / 2);
                spriteY = (int) afterImageFrame.displayRect.y - ((texture.getHeight() - afterImageFrame.displayRect.height) / 2);

                boolean flipDirection = false;
                if(afterImageFrame.facingDirection.equals("Left")) {
                    flipDirection = true;
                }

                float alphaPercent = ((i + 1.0f) / (afterImageFrameList.size() + 1)) / 1.35f;
                float greenPercent = ((i + 0.0f) / afterImageFrameList.size()) / 1.75f;
                imageManager.shaderProgramColorChannel.setUniformf("target_r", 1.0f);
                imageManager.shaderProgramColorChannel.setUniformf("target_g", greenPercent);
                imageManager.shaderProgramColorChannel.setUniformf("target_b", 1.0f);
                imageManager.shaderProgramColorChannel.setUniformf("target_alpha", alphaPercent);
                spriteBatch.draw(texture, spriteX, spriteY, texture.getWidth(), texture.getHeight(), 0, 0, texture.getWidth(), texture.getHeight(), flipDirection, false);
            }
        }

        spriteBatch.setShader(null);
        spriteBatch.setBlendFunction(srcFunc, destFunc);
        spriteBatch.end();
    }

    public void removeAfterImage(Mob mob) {
        if(afterImageFrameList.size() > afterImageLength

        || (afterImageFrameList.size() > 0
        && mob.velocity.x == 0
        && mob.velocity.y == 0
        && currentFrameTick == frameLength - 1)) {
            afterImageFrameList.remove(0);
        }
    }
}
