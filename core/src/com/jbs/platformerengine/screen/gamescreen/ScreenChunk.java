package com.jbs.platformerengine.screen.gamescreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.jbs.platformerengine.gamedata.Point;

public class ScreenChunk {
    ShapeRenderer shapeRenderer;
    FrameBuffer frameBufferWalls;
    public Point location;
    public Tile[][] tiles;

    public ScreenChunk(int x, int y) {
        shapeRenderer = new ShapeRenderer();
        frameBufferWalls = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        location = new Point(x, y);
        tiles = new Tile[80][48];
    }

    public void renderChunkWalls(OrthographicCamera camera, SpriteBatch spriteBatch) {
        Texture textureSquare = new Texture("images/Square.png");
        frameBufferWalls.begin();
        spriteBatch.begin();

        for(int y = 0; y < tiles[0].length; y++) {
            for(int x = 0; x < tiles.length; x++) {
                if(tiles[x][y] != null && tiles[x][y].type.equals("Square")) {
                    spriteBatch.draw(textureSquare, x * 16, y * 16);
                }
            }
        }

        spriteBatch.end();
        frameBufferWalls.end();
    }

    public void render(OrthographicCamera camera, SpriteBatch spriteBatch) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setColor(0, (10 + (location.y * 5))/255f, (10 + (location.x * 5))/255f, 0);
        shapeRenderer.rect((location.x * Gdx.graphics.getWidth()), (location.y * Gdx.graphics.getHeight()), Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        spriteBatch.draw(frameBufferWalls.getColorBufferTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 0, 0, 1, 1);
        spriteBatch.end();
    }
}
