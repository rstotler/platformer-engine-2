package com.jbs.platformerengine.screen;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;

public class ImageManager {
    public HashMap<String, HashMap<String, ArrayList<Texture>>> tile;
    public HashMap<String, HashMap<String, ArrayList<Texture>>> animatedImage;
    
    public ImageManager(ArrayList<String> tileSetList, ArrayList<String> animatedImageList) {
        loadImages(tileSetList, animatedImageList);
    }

    public void loadImages(ArrayList<String> tileSetList, ArrayList<String> animatedImageList) {

        // Tiles //
        tile = new HashMap<String, HashMap<String, ArrayList<Texture>>>();
        for(FileHandle directoryHandle : Gdx.files.internal("assets/images/tiles").list()) {
            String tileSetName = directoryHandle.toString().substring(directoryHandle.toString().lastIndexOf("/") + 1);
            
            if(tileSetList.contains(tileSetName)) {
                tile.put(tileSetName, new HashMap<String, ArrayList<Texture>>());
                // System.out.println("Tileset: " + tileSetName);
                
                for(FileHandle fileHandle : Gdx.files.internal(directoryHandle.toString()).list()) {
                    if(fileHandle.toString().contains(".png")) {
                        String fileName = fileHandle.toString().substring(fileHandle.toString().lastIndexOf("/") + 1, fileHandle.toString().length() - 4);
    
                        if(fileName.contains("_")) {
                            fileName = fileName.substring(0, fileName.lastIndexOf("_"));
                        }
    
                        if(!tile.get(tileSetName).containsKey(fileName)) {
                            tile.get(tileSetName).put(fileName, new ArrayList<Texture>());
                        }
                        // System.out.println("File: " + fileName);
                        
                        Texture fileTexture = new Texture(fileHandle.toString().substring(fileHandle.toString().indexOf("/") + 1));
                        tile.get(tileSetName).get(fileName).add(fileTexture);
                    }
                }
            }
        }
    
        // Animated Sprites //
        animatedImage = new HashMap<String, HashMap<String, ArrayList<Texture>>>();
        for(FileHandle directoryHandle : Gdx.files.internal("assets/images/animated").list()) {
            String animatedImageName = directoryHandle.toString().substring(directoryHandle.toString().lastIndexOf("/") + 1);

            if(animatedImageList.contains(animatedImageName)) {
                animatedImage.put(animatedImageName, new HashMap<String, ArrayList<Texture>>());

                for(FileHandle animationHandle : Gdx.files.internal(directoryHandle.toString()).list()) {
                    String animationName = animationHandle.toString().substring(animationHandle.toString().lastIndexOf("/") + 1);
                    animatedImage.get(animatedImageName).put(animationName, new ArrayList<Texture>());

                    for(FileHandle fileHandle : Gdx.files.internal(animationHandle.toString()).list()) {
                        if(fileHandle.toString().contains(".png")) {
                            Texture animationTexture = new Texture(fileHandle.toString());
                            animatedImage.get(animatedImageName).get(animationName).add(animationTexture);
                        }
                    }                    
                }
            }
        }
    }

    public void removeImages(ArrayList<String> removeTileSetList, ArrayList<String> removeAnimatedImageList) {
        for(String removeTileSetName : removeTileSetList) {
            if(tile.containsKey(removeTileSetName)) {
                for(String removeTileName : tile.get(removeTileSetName).keySet()) {
                    // int i = 0;
                    for(Texture texture : tile.get(removeTileSetName).get(removeTileName)) {
                        texture.dispose();
                        // System.out.println("Disposing TileSet: " + removeTileSetName + " " + removeTileName + " " + i++);
                    }
                }
            }
        }

        for(String removeAnimatedImageName : removeAnimatedImageList) {
            if(animatedImage.containsKey(removeAnimatedImageName)) {
                for(String removeAnimationName : animatedImage.get(removeAnimatedImageName).keySet()) {
                    // int i = 0;
                    for(Texture texture : animatedImage.get(removeAnimatedImageName).get(removeAnimationName)) {
                        texture.dispose();
                        // System.out.println("Disposing Animation: " + removeAnimatedImageName + " " + removeAnimationName + " " + i++);
                    }
                }
            }
        }
    }
}
