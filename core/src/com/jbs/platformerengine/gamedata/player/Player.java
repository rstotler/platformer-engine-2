package com.jbs.platformerengine.gamedata.player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.jbs.platformerengine.components.Keyboard;
import com.jbs.platformerengine.gamedata.Rect;

public class Player {
    ShapeRenderer shapeRenderer;
    public Rect spriteArea;

    public Player() {
        shapeRenderer = new ShapeRenderer();
        spriteArea = new Rect(100, 100, 50, 100);
    }

    public void update(Keyboard keyboard) {
        int moveSpeed = 5;
        
        if(keyboard.left && !keyboard.right) {
            spriteArea.x -= moveSpeed;
        } else if(!keyboard.left && keyboard.right) {
            spriteArea.x += moveSpeed;
        }

        if(keyboard.up && !keyboard.down) {
            spriteArea.y += moveSpeed;
        } else if(!keyboard.up && keyboard.down) {
            spriteArea.y -= moveSpeed;
        }
    }

    public void render(OrthographicCamera camera) {
        
        // Area Rectangle //
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(spriteArea.x - (spriteArea.width / 2), spriteArea.y, spriteArea.width, spriteArea.height);
        
        // X & Y (Location) //
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.circle(spriteArea.x, spriteArea.y, 4);

        // X & Y (Screen Center)
        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.circle(spriteArea.x, spriteArea.y + 293, 4);

        shapeRenderer.end();
    }
}
