package com.jbs.platformerengine.screen.gamescreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.jbs.platformerengine.gamedata.Point;
import com.jbs.platformerengine.screen.ImageManager;

public class ScreenChunk {
    ShapeRenderer shapeRenderer;
    FrameBuffer frameBufferTiles;
    FrameBuffer frameBufferWalls;

    public Point location;
    public Tile[][] tiles;

    public ScreenChunk(int x, int y) {
        shapeRenderer = new ShapeRenderer();
        location = new Point(x, y);
        tiles = new Tile[80][48];

        frameBufferWalls = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
    }

    public void bufferTiles(OrthographicCamera camera, SpriteBatch spriteBatch, String levelName, ImageManager imageManager) {
        frameBufferTiles = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        frameBufferTiles.begin();
        spriteBatch.begin();

        Gdx.graphics.getGL20().glClearColor(0f, 0f, 0f, 0f);
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        for(int y = 0; y < tiles[0].length; y++) {
            for(int x = 0; x < tiles.length; x++) {
                if(tiles[x][y] != null) {
                    if(imageManager.tile.containsKey(levelName) && imageManager.tile.get(levelName).containsKey(tiles[x][y].type)) {
                        if(tiles[x][y].num <= imageManager.tile.get(levelName).get(tiles[x][y].type).size()) {
                            spriteBatch.draw(imageManager.tile.get(levelName).get(tiles[x][y].type).get(tiles[x][y].num - 1), x * 16, y * 16);
                        } else {
                            spriteBatch.draw(imageManager.tile.get(levelName).get(tiles[x][y].type).get(0), x * 16, y * 16);
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
}
