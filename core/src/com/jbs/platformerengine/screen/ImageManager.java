package com.jbs.platformerengine.screen;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class ImageManager {
    public HashMap<String, HashMap<String, ArrayList<Texture>>> tile;
    public HashMap<String, HashMap<String, ArrayList<Texture>>> breakableImage;
    public HashMap<String, HashMap<String, ArrayList<Texture>>> mobImage;
    public HashMap<String, ArrayList<Texture>> outsideImage;

    public ShaderProgram shaderProgramColorChannel;
    
    public ImageManager(ArrayList<String> tileSetList, ArrayList<String> breakableImageList, ArrayList<String> mobImageList, boolean areaHasOutsideImages) {
        tile = new HashMap<String, HashMap<String, ArrayList<Texture>>>();
        breakableImage = new HashMap<String, HashMap<String, ArrayList<Texture>>>();
        mobImage = new HashMap<String, HashMap<String, ArrayList<Texture>>>();
        outsideImage = new HashMap<String, ArrayList<Texture>>();

        String vertexShader = Gdx.files.internal("shaders/vertex.glsl").readString();
        String fragmentShader = Gdx.files.internal("shaders/color_channel.glsl").readString();
        shaderProgramColorChannel = new ShaderProgram(vertexShader, fragmentShader);

        loadImages(tileSetList, breakableImageList, mobImageList, areaHasOutsideImages);
    }

    public void loadImages(ArrayList<String> tileSetList, ArrayList<String> breakableImageList, ArrayList<String> mobImageList, boolean areaHasOutsideImages) {

        // Tiles //
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
    
        // Breakable Object Sprites //
        for(FileHandle directoryHandle : Gdx.files.internal("assets/images/breakable").list()) {
            String breakableImageName = directoryHandle.toString().substring(directoryHandle.toString().lastIndexOf("/") + 1);

            if(breakableImageList.contains(breakableImageName)) {
                breakableImage.put(breakableImageName, new HashMap<String, ArrayList<Texture>>());

                for(FileHandle breakableHandle : Gdx.files.internal(directoryHandle.toString()).list()) {
                    String breakableName = breakableHandle.toString().substring(breakableHandle.toString().lastIndexOf("/") + 1);
                    breakableImage.get(breakableImageName).put(breakableName, new ArrayList<Texture>());

                    for(FileHandle fileHandle : Gdx.files.internal(breakableHandle.toString()).list()) {
                        if(fileHandle.toString().contains(".png")) {
                            Texture breakableTexture = new Texture(fileHandle.toString());
                            breakableImage.get(breakableImageName).get(breakableName).add(breakableTexture);
                        }
                    }                    
                }
            }
        }
    
        // Mob Sprites //
        for(FileHandle directoryHandle : Gdx.files.internal("assets/images/mob").list()) {
            String mobImageName = directoryHandle.toString().substring(directoryHandle.toString().lastIndexOf("/") + 1);

            if(mobImageList.contains(mobImageName)) {
                mobImage.put(mobImageName, new HashMap<String, ArrayList<Texture>>());

                for(FileHandle mobHandle : Gdx.files.internal(directoryHandle.toString()).list()) {
                    String mobName = mobHandle.toString().substring(mobHandle.toString().lastIndexOf("/") + 1);
                    mobImage.get(mobImageName).put(mobName, new ArrayList<Texture>());

                    for(FileHandle fileHandle : Gdx.files.internal(mobHandle.toString()).list()) {
                        if(fileHandle.toString().contains(".png")) {
                            Texture mobTexture = new Texture(fileHandle.toString());
                            mobImage.get(mobImageName).get(mobName).add(mobTexture);
                        }
                    }                    
                }
            }
        }
    
        // Outside Images //
        if(areaHasOutsideImages && outsideImage.size() == 0) {
            for(FileHandle outsideImageHandle : Gdx.files.internal("assets/images/outside").list()) {
                String outsideImageName = outsideImageHandle.toString().substring(outsideImageHandle.toString().lastIndexOf("/") + 1, outsideImageHandle.toString().lastIndexOf("_"));
                
                if(!outsideImage.containsKey(outsideImageName)) {
                    outsideImage.put(outsideImageName, new ArrayList<Texture>());
                }

                // String texturePath = outsideImageHandle.toString().substring(outsideImageHandle.toString().indexOf("/") + 1);
                Texture texture = new Texture(outsideImageHandle.toString());
                outsideImage.get(outsideImageName).add(texture);
            }
        }
    }

    public void removeImages(ArrayList<String> removeTileSetList, ArrayList<String> removeBreakableImageList, ArrayList<String> removeMobImageList, boolean areaHasOutsideImages) {
        for(String removeTileSetName : removeTileSetList) {
            if(tile.containsKey(removeTileSetName)) {
                for(String removeTileName : tile.get(removeTileSetName).keySet()) {
                    // int i = 0;
                    for(Texture texture : tile.get(removeTileSetName).get(removeTileName)) {
                        texture.dispose();
                        // System.out.println("Disposing TileSet: " + removeTileSetName + " " + removeTileName + " " + i++);
                    }
                }
                tile.remove(removeTileSetName);
            }
        }

        for(String removeBreakableImageName : removeMobImageList) {
            if(breakableImage.containsKey(removeBreakableImageName)) {
                for(String removeBreakableName : breakableImage.get(removeBreakableImageName).keySet()) {
                    // int i = 0;
                    for(Texture texture : breakableImage.get(removeBreakableImageName).get(removeBreakableName)) {
                        texture.dispose();
                        // System.out.println("Disposing Animation: " + removeBreakableImageName + " " + removeBreakableName + " " + i++);
                    }
                }
                breakableImage.remove(removeBreakableImageName);
            }
        }

        for(String removeMobImageName : removeMobImageList) {
            if(mobImage.containsKey(removeMobImageName)) {
                for(String removeMobName : mobImage.get(removeMobImageName).keySet()) {
                    // int i = 0;
                    for(Texture texture : mobImage.get(removeMobImageName).get(removeMobName)) {
                        texture.dispose();
                        // System.out.println("Disposing Animation: " + removeMobImageName + " " + removeMobName + " " + i++);
                    }
                }
                mobImage.remove(removeMobImageName);
            }
        }

        if(!areaHasOutsideImages && outsideImage.size() > 0) {
            for(String outsideImageName : outsideImage.keySet()) {
                for(int i = 0; i < outsideImage.get(outsideImageName).size(); i++) {
                    outsideImage.get(outsideImageName).get(i).dispose();
                }
            }
            outsideImage.clear();
        }
    }

    public void dispose() {
        
    }
}
