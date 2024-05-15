package com.jbs.platformerengine.screen.gamescreen;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.jbs.platformerengine.gamedata.Point;
import com.jbs.platformerengine.gamedata.entity.BreakableObject;
import com.jbs.platformerengine.gamedata.entity.Mob;
import com.jbs.platformerengine.screen.ImageManager;

public class ScreenChunk {
    ShapeRenderer shapeRenderer;
    FrameBuffer frameBufferTiles;
    public FrameBuffer frameBufferWalls;
    public FrameBuffer frameBufferAnimation;
    public FrameBuffer frameBufferForeground;

    public Point location;
    public Tile[][] tiles;                      // 80 x 48
    public CellCollidables[][] cellCollidables; // 20 x 12

    public ArrayList<Mob> mobList;
    ArrayList<BreakableObject> breakableList;

    public ScreenChunk(int x, int y) {
        shapeRenderer = new ShapeRenderer();
        location = new Point(x, y);
        tiles = new Tile[80][48];

        cellCollidables = new CellCollidables[20][12];
        for(int yIndex = 0; yIndex < cellCollidables[0].length; yIndex++) {
            for(int xIndex = 0; xIndex < cellCollidables.length; xIndex++) {
                cellCollidables[xIndex][yIndex] = new CellCollidables(x, y, xIndex, yIndex);
            }
        }

        mobList = new ArrayList<>();
        breakableList = new ArrayList<>();

        frameBufferWalls = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        frameBufferAnimation = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        frameBufferForeground = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
    }

    public void bufferTiles(SpriteBatch spriteBatch, ImageManager imageManager) {
        if(frameBufferTiles != null) {
            frameBufferTiles.dispose();
        }
        frameBufferTiles = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        frameBufferTiles.begin();
        spriteBatch.begin();

        Gdx.graphics.getGL20().glClearColor(0f, 0f, 0f, 0f);
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        for(int y = 0; y < tiles[0].length; y++) {
            for(int x = 0; x < tiles.length; x++) {
                if(tiles[x][y] != null) {
                    if(imageManager.tile.containsKey(tiles[x][y].tileSet) && imageManager.tile.get(tiles[x][y].tileSet).containsKey(tiles[x][y].tileName)) {
                        if(tiles[x][y].num <= imageManager.tile.get(tiles[x][y].tileSet).get(tiles[x][y].tileName).size()) {
                            spriteBatch.draw(imageManager.tile.get(tiles[x][y].tileSet).get(tiles[x][y].tileName).get(tiles[x][y].num - 1), x * 16, y * 16);
                        } else {
                            spriteBatch.draw(imageManager.tile.get(tiles[x][y].tileSet).get(tiles[x][y].tileName).get(0), x * 16, y * 16);
                        }
                    }
                }
            }
        }

        spriteBatch.end();
        frameBufferTiles.end();
    }

    public void renderTiles(OrthographicCamera camera, SpriteBatch spriteBatch) {
        // shapeRenderer.setProjectionMatrix(camera.combined);
        // shapeRenderer.begin(ShapeType.Filled);
        // shapeRenderer.setColor(0, (10 + (location.y * 5))/255f, (10 + (location.x * 5))/255f, 0);
        // shapeRenderer.rect((location.x * Gdx.graphics.getWidth()), (location.y * Gdx.graphics.getHeight()), Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        // shapeRenderer.end();

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        spriteBatch.draw(frameBufferTiles.getColorBufferTexture(), location.x * Gdx.graphics.getWidth(), location.y * Gdx.graphics.getHeight(), Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1, 1);
        spriteBatch.end();
    }

    public void bufferAnimations(OrthographicCamera camera, SpriteBatch spriteBatch, ImageManager imageManager) {
        for(BreakableObject breakableObject : breakableList) {
            
            // Sprite //
            Texture objectTexture = imageManager.breakableImage.get(breakableObject.imageName).get("Default").get(breakableObject.currentFrameNum);
            int spriteX = breakableObject.hitBoxArea.x - ((objectTexture.getWidth() - breakableObject.hitBoxArea.width) / 2);
            int spriteY = breakableObject.hitBoxArea.y - ((objectTexture.getHeight() - breakableObject.hitBoxArea.height) / 2);
            
            spriteBatch.begin();
            spriteBatch.draw(objectTexture, spriteX, spriteY);
            spriteBatch.end();

            // Hit Box Outline //
            // shapeRenderer.setProjectionMatrix(camera.combined);
            // shapeRenderer.begin(ShapeType.Filled);
            // shapeRenderer.setColor(140/255f, 0/255f, 140/255f, 1f);
            // shapeRenderer.rect(breakableObject.hitBoxArea.x, breakableObject.hitBoxArea.y, breakableObject.hitBoxArea.width, breakableObject.hitBoxArea.height);
            // shapeRenderer.end();

            breakableObject.updateAnimation();
        }
        
        for(Mob mobObject : mobList) {

            // Sprite //
            if(imageManager.mobImage.containsKey(mobObject.imageName)) {
                Texture mobTexture = imageManager.mobImage.get(mobObject.imageName).get("Default").get(mobObject.currentFrameNum);
                int spriteX = mobObject.hitBoxArea.x - ((mobTexture.getWidth() - mobObject.hitBoxArea.width) / 2);
                int spriteY = mobObject.hitBoxArea.y - ((mobTexture.getHeight() - mobObject.hitBoxArea.height) / 2);

                spriteBatch.begin();
                spriteBatch.draw(mobTexture, spriteX, spriteY);
                spriteBatch.end();
                
                mobObject.updateAnimation();
            }

            // Hit Box Outline //
            if(!imageManager.mobImage.containsKey(mobObject.imageName)) {
                mobObject.render(camera);
            }
        }
    }

    public void initChunk() {
        shapeRenderer = new ShapeRenderer();
        frameBufferWalls = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        frameBufferAnimation = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        frameBufferForeground = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        
        frameBufferWalls.begin();
        Gdx.graphics.getGL20().glClearColor(0f, 0f, 0f, 0f);
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
        frameBufferWalls.end();

        frameBufferAnimation.begin();
        Gdx.graphics.getGL20().glClearColor(0f, 0f, 0f, 0f);
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
        frameBufferAnimation.end();

        frameBufferForeground.begin();
        Gdx.graphics.getGL20().glClearColor(0f, 0f, 0f, 0f);
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
        frameBufferForeground.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
        frameBufferTiles.dispose();
        frameBufferWalls.dispose();
        frameBufferAnimation.dispose();
        frameBufferForeground.dispose();
    }
}
