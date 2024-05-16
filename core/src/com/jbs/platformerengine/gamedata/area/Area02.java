package com.jbs.platformerengine.gamedata.area;

import java.util.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jbs.platformerengine.gamedata.Point;
import com.jbs.platformerengine.gamedata.Rect;
import com.jbs.platformerengine.gamedata.entity.Mob;
import com.jbs.platformerengine.screen.ImageManager;
import com.jbs.platformerengine.screen.gamescreen.GameScreen;
import com.jbs.platformerengine.screen.gamescreen.Tile;

public class Area02 extends AreaData {
    public Area02() {
        levelName = "Area02";
        size = new Rect(2, 3);
    
        defaultTileSet = "Debug";
        defaultTileName = "Square";
        defaultTileNum = 1;
    
        tileSetList = new ArrayList<>(Arrays.asList("Debug"));
        mobImageList = new ArrayList<>(Arrays.asList("Bat"));

        outside = false;

        loadScreenChunks();
    }
    
    public void loadArea(SpriteBatch spriteBatch, ImageManager imageManager) {
        createFloor("Debug", false);

        // Stone Stairs Background //
        ArrayList<Texture> stoneStairTexture = new ArrayList<>();
        stoneStairTexture.add(new Texture("images/objects/Stone-Stairs_01.png"));
        stoneStairTexture.add(new Texture("images/objects/Stone-Stairs_02.png"));
        stoneStairTexture.add(new Texture("images/objects/Stone-Stairs_03.png"));

        int tilesWidth = screenChunks[0][0].tiles.length * screenChunks.length;
        for(int x = 0; x < tilesWidth; x++) {
            int chunkX = x / screenChunks[0][0].tiles.length;
            int chunkY = 0;
            int drawX = (x % screenChunks[0][0].tiles.length) * 16;
            int drawY = 7 * 16;

            if(x > 2 && x < tilesWidth - 3) {
                int textureIndex = 1;
                if(x == 3) {
                    textureIndex = 0;
                } else if(x == tilesWidth - 4) {
                    textureIndex = 2;
                }

                screenChunks[chunkX][chunkY].frameBufferWalls.begin();
                spriteBatch.begin();
                spriteBatch.setColor(100/255f, 100/255f, 100/255f, 1);

                spriteBatch.draw(stoneStairTexture.get(textureIndex), drawX, drawY);
                if(x > 4 && x < tilesWidth - 5) {
                    if(x == 5) {
                        textureIndex = 0;
                    } else if(x == tilesWidth - 6) {
                        textureIndex = 2;
                    }
                    spriteBatch.draw(stoneStairTexture.get(textureIndex), drawX, drawY + 16);
                }

                spriteBatch.setColor(1, 1, 1, 1);
                spriteBatch.end();
                screenChunks[chunkX][chunkY].frameBufferWalls.end();
            }
        }

        // Pillars //
        HashMap<String, ArrayList<Texture>> pillarTexture = new HashMap<String, ArrayList<Texture>>();
        pillarTexture.put("Base", new ArrayList<Texture>());
        pillarTexture.get("Base").add(new Texture("images/objects/Pillar-Base_01.png"));
        pillarTexture.get("Base").add(new Texture("images/objects/Pillar-Base_02.png"));
        pillarTexture.put("Middle", new ArrayList<Texture>());
        pillarTexture.get("Middle").add(new Texture("images/objects/Pillar-Middle_01.png"));
        pillarTexture.put("Top", new ArrayList<Texture>());
        pillarTexture.get("Top").add(new Texture("images/objects/Pillar-Top_01.png"));
        pillarTexture.put("Broken-Bottom", new ArrayList<Texture>());
        pillarTexture.get("Broken-Bottom").add(new Texture("images/objects/Pillar-Broken-Bottom_01.png"));
        pillarTexture.get("Broken-Bottom").add(new Texture("images/objects/Pillar-Broken-Bottom_02.png"));
        pillarTexture.get("Broken-Bottom").add(new Texture("images/objects/Pillar-Broken-Bottom_03.png"));
        pillarTexture.put("Broken-Top", new ArrayList<Texture>());
        pillarTexture.get("Broken-Top").add(new Texture("images/objects/Pillar-Broken-Top_01.png"));
        pillarTexture.get("Broken-Top").add(new Texture("images/objects/Pillar-Broken-Top_02.png"));

        int pillarX = 7 * 16;
        int pillarY = 9 * 16;
        int pillarCount = 11;
        for(int i = 0; i < pillarCount; i++) {
            int chunkX = pillarX / Gdx.graphics.getWidth();
            int chunkY = pillarY / Gdx.graphics.getHeight();
            int drawX = pillarX % Gdx.graphics.getWidth();
            int pillarYBottom = 6;
            int pillarYTop = 6;
            int pillarSize = pillarYBottom + pillarYTop;
            String pillarType;

            if(new Random().nextInt(4) == 0) {
                pillarType = "Unbroken";
            } else {
                pillarType = "Broken No Top";
                pillarYBottom -= new Random().nextInt(pillarYBottom - 1);
                pillarYTop -= new Random().nextInt(pillarYTop / 2) + (pillarYTop / 2);
            }
            // else {
            //     pillarType = "Broken No Top";
            //     pillarYBottom -= new Random().nextInt(pillarYBottom - 1);
            // }

            screenChunks[chunkX][chunkY].frameBufferWalls.begin();
            spriteBatch.begin();
            spriteBatch.draw(pillarTexture.get("Base").get(0), drawX, pillarY);

            for(int y = 0; y < pillarYBottom; y++) {
                String pillarImageName = "Middle";
                int pillarImageNum = 0;
                if(!pillarType.equals("Unbroken") && y == pillarYBottom - 1) {
                    pillarImageName = "Broken-Bottom";
                    pillarImageNum = new Random().nextInt(pillarTexture.get("Broken-Bottom").size());
                }

                spriteBatch.draw(pillarTexture.get(pillarImageName).get(pillarImageNum), drawX, pillarY + 16 + (y * 16));
            }

            if(pillarType.equals("Unbroken") || pillarType.equals("Broken With Top")) {
                String pillarImageName = "Middle";
                int pillarImageNum = 0;
                for(int y = 0; y < pillarYTop; y++) {
                    if(!pillarType.equals("Unbroken") && y == pillarYTop - 1) {
                        pillarImageName = "Broken-Top";
                        pillarImageNum = new Random().nextInt(pillarTexture.get("Broken-Top").size());
                    }
                    spriteBatch.draw(pillarTexture.get(pillarImageName).get(pillarImageNum), drawX, pillarY + (pillarSize * 16) - (y * 16));
                }
                spriteBatch.draw(pillarTexture.get("Top").get(0), drawX, pillarY + 16 + (pillarSize * 16));
            }
            
            spriteBatch.end();
            screenChunks[chunkX][chunkY].frameBufferWalls.end();

            pillarX += 224;
        }

        // Test Mobs //
        if(!initCheck) {
            // GameScreen.addObjectToCellCollidables(screenChunks, new Mob("Default", new Point(250, 250), imageManager));
            for(int i = 0; i < 50; i++) {
                int xLoc = new Random().nextInt((screenChunks.length * Gdx.graphics.getWidth()) - 60) + 30;
                int yLoc = new Random().nextInt((screenChunks[0].length * Gdx.graphics.getHeight()) - 250) + 200;
                GameScreen.addObjectToCellCollidables(screenChunks, new Mob("Bat", new Point(xLoc, yLoc), imageManager));
            }
        }
        
        // Exit To Area01 Bridge //
        int exitYLoc = 7;
        for(int y = 0; y < 7; y++) {
            int chunkY = (exitYLoc + y) / 48;
            int tileY = (exitYLoc + y) % 48;
            int exitX = 100;
            if(GameScreen.getAreaData("Area01") != null) {
                exitX = (GameScreen.getAreaData("Area01").screenChunks.length * Gdx.graphics.getWidth()) - 32;
            }

            screenChunks[0][chunkY].tiles[0][tileY] = new Tile("Area01", new Point(exitX, 600));
        }

        // Dispose Textures //
        for(Texture texture : stoneStairTexture) {
            texture.dispose();
        }
        for(String textureName : pillarTexture.keySet()) {
            for(Texture texture : pillarTexture.get(textureName)) {
                texture.dispose();
            }
        }

        initCheck = true;
    }
}
