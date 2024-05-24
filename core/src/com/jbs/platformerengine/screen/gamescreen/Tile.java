package com.jbs.platformerengine.screen.gamescreen;

import com.jbs.platformerengine.gamedata.Point;
import com.jbs.platformerengine.gamedata.entity.player.Player;

public class Tile {
    public String tileSet;
    public String tileName;
    public int num;

    public int chunkX;
    public int chunkY;
    public int tileX;
    public int tileY;

    public String tileShape;

    public String changeArea;
    public Point changeLocation;

    public Tile(String tileSet, String tileName, int num, int chunkX, int chunkY, int tileX, int tileY){ 
        this.tileSet = tileSet;
        this.tileName = tileName;
        this.num = num;

        this.chunkX = chunkX;
        this.chunkY = chunkY;
        this.tileX = tileX;
        this.tileY = tileY;

        initTileShape();

        changeArea = "None";
    }

    public Tile(String tileSet, String tileName, int chunkX, int chunkY, int tileX, int tileY){ 
        this.tileSet = tileSet;
        this.tileName = tileName;
        num = 1;

        this.chunkX = chunkX;
        this.chunkY = chunkY;
        this.tileX = tileX;
        this.tileY = tileY;

        initTileShape();

        changeArea = "None";
    }

    public Tile(String changeArea, Point changeLocation, int chunkX, int chunkY, int tileX, int tileY) {
        tileSet = "None";
        tileName = "None";
        num = 1;

        this.chunkX = chunkX;
        this.chunkY = chunkY;
        this.tileX = tileX;
        this.tileY = tileY;
        
        tileShape = "None";

        this.changeArea = changeArea;
        this.changeLocation = changeLocation;
    }

    public void initTileShape() {
        tileShape = "Square";

        if(tileName.equals("Ceiling-Ramp")) {
            if(num == 1) {
                tileShape = "Ceiling-Ramp-Right";
            } else {
                tileShape = "Ceiling-Ramp-Left";
            }
        } else if(tileName.equals("Ramp")) {
            if(num == 1) {
                tileShape = "Ramp-Right";
            } else {
                tileShape = "Ramp-Left";
            }
        } else if(tileName.equals("Ramp-Bottom")) {
            if(num == 1) {
                tileShape = "Ramp-Right-Half-Bottom";
            } else {
                tileShape = "Ramp-Left-Half-Bottom";
            }
        } else if(tileName.equals("Ramp-Top")) {
            if(num == 1) {
                tileShape = "Ramp-Right-Half-Top";
            } else {
                tileShape = "Ramp-Left-Half-Top";
            }
        } else if(tileName.equals("Square-Half")) {
            tileShape = "Square-Half";
        }
    }

    public boolean collisionCheck(Player player, String movingDir, int locationIndex) {
        Point tileLocation = new Point(((chunkX * 80) + tileX) * 16, ((chunkY * 48) + tileY) * 16);
        //System.out.println(tileShape + " " + movingDir + " " + locationIndex);

        if(tileShape.equals("Square")) {
            if(movingDir.equals("Right")) {
                player.hitBoxArea.x = tileLocation.x - player.hitBoxArea.width;
                player.velocity.x = 0;
                return true;
            } else if(movingDir.equals("Left")) {
                player.hitBoxArea.x = tileLocation.x + 16;
                player.velocity.x = 0;
                return true;
            } else if(movingDir.equals("Down")) {
                player.hitBoxArea.y = tileLocation.y + 16;
                player.velocity.y = 0;
                player.land();
                return true;
            } else if(movingDir.equals("Up")) {
                player.hitBoxArea.y = tileLocation.y - player.hitBoxArea.height;
                player.velocity.y = 0;
                player.hitCeiling();
                return true;
            }
        }

        else if(tileShape.equals("Square-Half")) {
            if(movingDir.equals("Right")) {
                if(locationIndex > 0
                || (locationIndex == 0 && player.hitBoxArea.y < tileLocation.y + 8)) {
                    player.hitBoxArea.x = tileLocation.x - player.hitBoxArea.width;
                    player.velocity.x = 0;
                    return true;
                }
            } else if(movingDir.equals("Left")) {
                if(locationIndex > 0
                || (locationIndex == 0 && player.hitBoxArea.y < tileLocation.y + 8)) {
                    player.hitBoxArea.x = tileLocation.x + 16;
                    player.velocity.x = 0;
                    return true;
                }
            } else if(movingDir.equals("Down")) {
                if(player.hitBoxArea.y < tileLocation.y + 8) {
                    player.hitBoxArea.y = tileLocation.y + 8;
                    player.velocity.y = 0;
                    player.land();
                    return true;
                }
            } else if(movingDir.equals("Up")) {
                player.hitBoxArea.y = tileLocation.y - player.hitBoxArea.height;
                player.velocity.y = 0;
                player.hitCeiling();
                return true;
            }
        }

        else if(tileShape.equals("Ramp-Right")) {
            if(movingDir.equals("Left")) {
                if(player.hitBoxArea.getMiddle().x > tileLocation.x + 16) {
                    player.hitBoxArea.x = tileLocation.x + 16;
                    player.velocity.x = 0;
                    return true;
                }
            } else if(movingDir.equals("Right")) {
                if(locationIndex > 0) {
                    player.hitBoxArea.x = tileLocation.x - player.hitBoxArea.width;
                    player.velocity.x = 0;
                    return true;
                }
            } else if(movingDir.equals("Middle")) {
                if(locationIndex == 0) {
                    float locationDiff = player.hitBoxArea.getMiddle().x - tileLocation.x;
                    float anglePercent = locationDiff / 16;
                    if(player.hitBoxArea.y <= tileLocation.y + (int) (16 * anglePercent)) {
                        player.hitBoxArea.y = tileLocation.y + (int) (16 * anglePercent);
                        player.velocity.y = 0;
                        return true;
                    }
                }
            } else if(movingDir.equals("Down")) {
                if(locationIndex == 0) {
                    if(player.hitBoxArea.getMiddle().x >= tileLocation.x + 16) {
                        player.hitBoxArea.y = tileLocation.y + 16;
                        player.velocity.y = 0;
                        player.land();
                        return true;
                    }
                }
                else if(locationIndex == 1) {
                    if(player.hitBoxArea.getMiddle().x < tileLocation.x
                    && player.hitBoxArea.y < tileLocation.y) {
                        player.hitBoxArea.y = tileLocation.y;
                        player.velocity.y = 0;
                        player.land();
                        return true;
                    }
                }
                else if(locationIndex == 2) {
                    float locationDiff = player.hitBoxArea.getMiddle().x - tileLocation.x;
                    float anglePercent = locationDiff / 16;
                    if(player.hitBoxArea.y <= tileLocation.y + (int) (16 * anglePercent)) {
                        player.hitBoxArea.y = tileLocation.y + (int) (16 * anglePercent);
                        player.velocity.y = 0;
                        player.land();
                        return true;
                    }
                }
            } else if(movingDir.equals("Up")) {
                player.hitBoxArea.y = tileLocation.y - player.hitBoxArea.height;
                player.velocity.y = 0;
                player.hitCeiling();
                return true;
            }
        }

        return false;
    }
}
