package com.jbs.platformerengine.gamedata;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.jbs.platformerengine.screen.ImageManager;
import com.jbs.platformerengine.screen.animatedobject.AnimatedObject;

public class CollidableObject extends AnimatedObject {
    public Rect hitBoxArea;
    public boolean displayHitBox;

    public CollidableObject(String imageName, ImageManager imageManager) {
        this(imageName, new Point(-1, -1), imageManager);
    }

    public CollidableObject(String objectName, Point spriteLocation, ImageManager imageManager) {
        super(objectName, imageManager);

        hitBoxArea = new Rect(spriteLocation.x, spriteLocation.y, 0, 0);
        displayHitBox = false;
    }

    public void renderHitBox(OrthographicCamera camera, ShapeRenderer shapeRenderer, String facingDirection) {
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setProjectionMatrix(camera.combined);
        
        if(getClass().toString().substring(getClass().toString().lastIndexOf(".") + 1).equals("Player")) {
            shapeRenderer.setColor(82/255f, 0/255f, 0/255f, 1f);
        } else {
            shapeRenderer.setColor(140/255f, 0/255f, 140/255f, 1f);
        }

        shapeRenderer.rect((int) hitBoxArea.x, (int) hitBoxArea.y, hitBoxArea.width, hitBoxArea.height);
        
        // Facing Direction //
        shapeRenderer.setColor(Color.YELLOW);
        if(facingDirection.equals("Right")) {
            shapeRenderer.circle((int) hitBoxArea.x + hitBoxArea.width, (int) hitBoxArea.y + (hitBoxArea.height / 2), 1);
        } else {
            shapeRenderer.circle((int) hitBoxArea.x, (int) hitBoxArea.y + (hitBoxArea.height / 2), 1);
        }

        shapeRenderer.end();
    }
}
