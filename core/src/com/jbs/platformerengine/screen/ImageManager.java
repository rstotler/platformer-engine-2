package com.jbs.platformerengine.screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;

public class ImageManager {
    public HashMap<String, HashMap<String, ArrayList<Texture>>> tile;
    
    public ImageManager(List<String> tileSetList) {
        tile = new HashMap<String, HashMap<String, ArrayList<Texture>>>();
        for(FileHandle directoryHandle : Gdx.files.internal("assets/images/tiles").list()) {
            String directoryName = directoryHandle.toString().substring(directoryHandle.toString().lastIndexOf("/") + 1);
            tile.put(directoryName, new HashMap<String, ArrayList<Texture>>());
            // System.out.println("Directory: " + directoryName);

            if(tileSetList.contains(directoryName) || directoryName.equals("Debug")) {
                for(FileHandle fileHandle : Gdx.files.internal(directoryHandle.toString()).list()) {
                    if(fileHandle.toString().contains(".png")) {
                        String fileName = fileHandle.toString().substring(fileHandle.toString().lastIndexOf("/") + 1, fileHandle.toString().length() - 4);
    
                        if(fileName.contains("_")) {
                            fileName = fileName.substring(0, fileName.indexOf("_"));
                        }
    
                        if(!tile.get(directoryName).containsKey(fileName)) {
                            tile.get(directoryName).put(fileName, new ArrayList<Texture>());
                        }
                        //System.out.println("File: " + fileName);
                        
                        Texture fileTexture = new Texture(fileHandle.toString().substring(fileHandle.toString().indexOf("/") + 1));
                        tile.get(directoryName).get(fileName).add(fileTexture);
                    }
                }
            }
        }
    }
}
