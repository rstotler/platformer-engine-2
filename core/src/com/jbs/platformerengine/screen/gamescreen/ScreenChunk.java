package com.jbs.platformerengine.screen.gamescreen;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.jbs.platformerengine.gamedata.Point;
import com.jbs.platformerengine.gamedata.area.AreaData;
import com.jbs.platformerengine.gamedata.entity.BreakableObject;
import com.jbs.platformerengine.gamedata.entity.mob.Mob;
import com.jbs.platformerengine.gamedata.entity.player.Player;
import com.jbs.platformerengine.screen.ImageManager;

public class ScreenChunk {
    FrameBuffer frameBufferTiles;
    public FrameBuffer frameBufferWalls;
    public FrameBuffer frameBufferAnimation;
    public FrameBuffer frameBufferForeground;

    public Point location;
    public Tile[][] tiles;                      // 80 x 48 (16 x 16)
    public CellCollidables[][] cellCollidables; // 20 x 12 (64 x 64)

    public ArrayList<Mob> mobList;
    ArrayList<BreakableObject> breakableList;

    public ScreenChunk(int x, int y) {
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

    public void renderCellCollidables(OrthographicCamera camera, ShapeRenderer shapeRenderer, AreaData areaData, Player player, int displayCollidableCellsLevel) {
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setProjectionMatrix(camera.combined);

        ArrayList<CellCollidables> targetCellCollidablesList = player.getTargetMobAreaCellCollidables(areaData.screenChunks);

        for(int y = 0; y < 12; y++) {
            for(int x = 0; x < 20; x++) {
                CellCollidables targetCellCollidables = cellCollidables[x][y];
                if(displayCollidableCellsLevel == 2
                && targetCellCollidablesList.contains(targetCellCollidables)) {
                    shapeRenderer.setColor(0/255f, targetCellCollidables.cellColor/255f, 0/255f, 1f);
                } else {
                    shapeRenderer.setColor(0/255f, 0/255f, targetCellCollidables.cellColor/255f, 1f);
                }
                
                int xLoc = (location.x * Gdx.graphics.getWidth()) + (x * 64);
                int yLoc = (location.y * Gdx.graphics.getHeight()) + (y * 64);
                shapeRenderer.rect(xLoc, yLoc, 64, 64);
            }
        }

        shapeRenderer.end();
    }

    public void renderCellCollidablesData(OrthographicCamera camera, SpriteBatch spriteBatch, BitmapFont font) {
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        font.setColor(Color.WHITE);

        for(int y = 0; y < 12; y++) { 
            for(int x = 0; x < 20; x++) {
                CellCollidables targetCellCollidables = cellCollidables[x][y];
                int xLoc = (location.x * Gdx.graphics.getWidth()) + (x * 64) + 3;
                int yLoc = (location.y * Gdx.graphics.getHeight()) + (y * 64) + 15;
                String stringMobListSize = String.valueOf(targetCellCollidables.mobList.size());
                
                font.draw(spriteBatch, targetCellCollidables.toString(), xLoc, yLoc + 20);
                font.draw(spriteBatch, stringMobListSize, xLoc, yLoc);
            }
        }
        
        spriteBatch.end();
    }

    public void bufferAnimations(OrthographicCamera camera, SpriteBatch spriteBatch, ImageManager imageManager, ShapeRenderer shapeRenderer, int areaTimer, Player player) {
        for(BreakableObject breakableObject : breakableList) {
            if(breakableObject.displayHitBox) {
                breakableObject.renderHitBox(camera, shapeRenderer, "Right");
            }

            breakableObject.renderAnimatedObject(imageManager, spriteBatch, breakableObject.hitBoxArea, "Right", true);
            breakableObject.updateAnimation();
        }
        
        for(Mob mobObject : mobList) {
            if(mobObject.displayHitBox) {
                mobObject.renderHitBox(camera, shapeRenderer, mobObject.facingDirection);
            }

            if(mobObject.updateAnimationTimer != areaTimer) {
                mobObject.renderAnimatedObject(imageManager, spriteBatch, mobObject.hitBoxArea, mobObject.facingDirection, true);
                mobObject.updateAnimation();
                mobObject.updateAnimationTimer = areaTimer;
            }

            if(player.targetMob != null && player.targetMob == mobObject) {
                mobObject.renderTargetBox(camera, shapeRenderer);
            }

            if(mobObject.attackData != null) {
                mobObject.attackData.renderAttackHitBoxes(shapeRenderer, mobObject);
            }
        }
    }

    public void initChunk() {
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

    public static Tile getTile(ScreenChunk[][] screenChunks, int xLoc, int yLoc) {
        int chunkX = xLoc / Gdx.graphics.getWidth();
        int chunkY = yLoc / Gdx.graphics.getHeight();
        int tileX = (xLoc % Gdx.graphics.getWidth()) / 16;
        int tileY = (yLoc % Gdx.graphics.getHeight()) / 16;

        if(chunkX >= 0 && chunkX < screenChunks.length
        && chunkY >= 0 && chunkY < screenChunks[0].length
        && tileX >= 0 && tileX < screenChunks[0][0].tiles.length
        && tileY >= 0 && tileY < screenChunks[0][0].tiles[0].length) {
            return screenChunks[chunkX][chunkY].tiles[tileX][tileY];
        }

        return null;
    }

    public static CellCollidables getCellCollidablesCell(ScreenChunk[][] screenChunks, int xLoc, int yLoc) {
        int chunkX = xLoc / Gdx.graphics.getWidth();
        int chunkY = yLoc / Gdx.graphics.getHeight();
        int cellX = (xLoc % Gdx.graphics.getWidth()) / 64;
        int cellY = (yLoc % Gdx.graphics.getHeight()) / 64;

        if(chunkX >= 0 && chunkX < screenChunks.length
        && chunkY >= 0 && chunkY < screenChunks[0].length
        && cellX >= 0 && cellX < screenChunks[0][0].cellCollidables.length
        && cellY >= 0 && cellY < screenChunks[0][0].cellCollidables[0].length) {
            return screenChunks[chunkX][chunkY].cellCollidables[cellX][cellY];
        }

        return null;
    }

    public void dispose() {
        frameBufferTiles.dispose();
        frameBufferWalls.dispose();
        frameBufferAnimation.dispose();
        frameBufferForeground.dispose();
    }
}
