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

    public boolean collisionCheck(Player player, String movingDir, int locationXIndex, int locationYIndex) {
        Point tileLocation = new Point(((chunkX * 80) + tileX) * 16, ((chunkY * 48) + tileY) * 16);
        // System.out.println(tileShape + " " + movingDir + " " + locationXIndex + " " + locationYIndex + " " + player.onRamp + " " + player.onHalfRampBottom + " " + player.onHalfRampTop);

        // Square //
        if(tileShape.equals("Square")) {
            if(movingDir.equals("Right")) {
                if(locationYIndex == 0 && (player.onRamp || player.onHalfRampTop) && !player.falling) {
                    player.inWallOnRamp = true;
                } else {
                    player.hitBoxArea.x = tileLocation.x - player.hitBoxArea.width;
                    player.velocity.x = 0;
                    return true;
                }
            } else if(movingDir.equals("Left")) {
                if(locationYIndex == 0 && player.onRamp && !player.falling) {
                    player.inWallOnRamp = true;
                } else {
                    player.hitBoxArea.x = tileLocation.x + 16;
                    player.velocity.x = 0;
                    return true;
                }
            } else if(movingDir.equals("Down")) {
                if(!(locationXIndex == 2 && (player.onRamp || player.onHalfRampTop))) {
                    player.hitBoxArea.y = tileLocation.y + 16;
                    player.velocity.y = 0;
                    player.land(this);

                    if(!player.justSteppedOnRamp) {
                        player.onRamp = false;
                        player.onHalfRampBottom = false;
                        player.onHalfRampTop = false;
                    }

                    return true;
                }
            } else if(movingDir.equals("Up")) {
                player.hitBoxArea.y = tileLocation.y - player.hitBoxArea.height;
                player.velocity.y = 0;
                player.hitCeiling();
                return true;
            }
        }

        // Square-Half //
        else if(tileShape.equals("Square-Half")) {
            if(movingDir.equals("Right")) {
                if(locationYIndex == 0 && player.onHalfRampBottom && !player.falling) {
                    player.inWallOnRamp = true;
                } else {
                    if(locationYIndex > 0
                    || (locationYIndex == 0 && player.hitBoxArea.y < tileLocation.y + 8)) {
                        player.hitBoxArea.x = tileLocation.x - player.hitBoxArea.width;
                        player.velocity.x = 0;
                        return true;
                    }
                }
            } else if(movingDir.equals("Left")) {
                if(locationYIndex == 0 && player.onHalfRampBottom && !player.falling) {
                    player.inWallOnRamp = true;
                } else {
                    if(locationYIndex > 0
                    || (locationYIndex == 0 && player.hitBoxArea.y < tileLocation.y + 8)) {
                        player.hitBoxArea.x = tileLocation.x + 16;
                        player.velocity.x = 0;
                        return true;
                    }
                }
            } else if(movingDir.equals("Down")) {
                if(player.hitBoxArea.y < tileLocation.y + 8) {
                    player.hitBoxArea.y = tileLocation.y + 8;
                    player.velocity.y = 0;
                    player.land(this);

                    if(locationXIndex == 0) {
                        player.onRamp = false;
                    }
                    
                    return true;
                }
            } else if(movingDir.equals("Up")) {
                player.hitBoxArea.y = tileLocation.y - player.hitBoxArea.height;
                player.velocity.y = 0;
                player.hitCeiling();
                return true;
            }
        }

        // Ramp-Right //
        else if(tileShape.equals("Ramp-Right")) {
            if(movingDir.equals("Right")) {
                if(locationYIndex > 0
                && !player.onRamp && !player.onHalfRampTop) {
                    player.hitBoxArea.x = tileLocation.x - player.hitBoxArea.width;
                    player.velocity.x = 0;
                    return true;
                }
            } else if(movingDir.equals("Left")) {
                if(player.hitBoxArea.getMiddle().x >= tileLocation.x + 16
                && !(locationYIndex == 0 && player.onRamp)) {
                    player.hitBoxArea.x = tileLocation.x + 16;
                    player.velocity.x = 0;
                    return true;
                }
            } else if(movingDir.equals("Middle")) {
                if(locationYIndex == 0 && !player.inWallOnRamp && player.velocity.y <= 0) {
                    float locationDiff = player.hitBoxArea.getMiddle().x - tileLocation.x;
                    float anglePercent = locationDiff / 16;
                    if(player.hitBoxArea.y <= tileLocation.y + (int) (16 * anglePercent)
                    || player.onRampLastFrame) {
                        player.hitBoxArea.y = tileLocation.y + (int) (16 * anglePercent);
                        player.onRamp = true;
                        player.justSteppedOnRamp = true;
                        return true;
                    }
                }
            } else if(movingDir.equals("Down")) {
                if(locationXIndex == 0) {
                    if(player.velocity.y <= 0) {
                        player.onRamp = true;
                        player.justSteppedOnRamp = true;
                        
                        float locationDiff = player.hitBoxArea.getMiddle().x - tileLocation.x;
                        float anglePercent = locationDiff / 16;
                        if(player.hitBoxArea.y <= tileLocation.y + (int) (16 * anglePercent)
                        || player.onRampLastFrame) {
                            player.hitBoxArea.y = tileLocation.y + (int) (16 * anglePercent);
                            player.velocity.y = 0;
                            player.land(this);
                            return true;
                        }
                    }
                } else if(locationXIndex == 1) {
                    if(player.hitBoxArea.getMiddle().x >= tileLocation.x + 16) {
                        player.hitBoxArea.y = tileLocation.y + 16;
                        player.velocity.y = 0;
                        player.onRamp = false;
                        player.land(this);
                        return true;
                    }
                } else if(locationXIndex == 2) {
                    if(player.hitBoxArea.getMiddle().x < tileLocation.x
                    && player.hitBoxArea.y <= tileLocation.y) {
                        player.hitBoxArea.y = tileLocation.y;
                        player.velocity.y = 0;
                        player.onRamp = false;
                        player.land(this);
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

        // Ramp-Left //
        else if(tileShape.equals("Ramp-Left")) {
            if(movingDir.equals("Right")) {
                if(player.hitBoxArea.getMiddle().x < tileLocation.x
                && !(locationYIndex == 0 && player.onRamp)) {
                    player.hitBoxArea.x = tileLocation.x - player.hitBoxArea.width;
                    player.velocity.x = 0;
                    return true;
                }
            } else if(movingDir.equals("Left")) {
                if(locationYIndex > 0
                && !player.onRamp && !player.onHalfRampTop) {
                    player.hitBoxArea.x = tileLocation.x + 16;
                    player.velocity.x = 0;
                    return true;
                }
            } else if(movingDir.equals("Middle")) {
                if(locationYIndex == 0 && !player.inWallOnRamp && player.velocity.y <= 0) {
                    float locationDiff = 16 - (player.hitBoxArea.getMiddle().x - tileLocation.x);
                    float anglePercent = locationDiff / 16;
                    if(player.hitBoxArea.y <= tileLocation.y + (int) (16 * anglePercent)
                    || player.onRampLastFrame) {
                        player.hitBoxArea.y = tileLocation.y + (int) (16 * anglePercent);
                        player.onRamp = true;
                        return true;
                    }
                }
            } else if(movingDir.equals("Down")) {
                if(locationXIndex == 0) {
                    if(player.velocity.y <= 0) {
                        player.onRamp = true;
                        player.justSteppedOnRamp = true;

                        float locationDiff = 16 - (player.hitBoxArea.getMiddle().x - tileLocation.x);
                        float anglePercent = locationDiff / 16;
                        if(player.hitBoxArea.y <= tileLocation.y + (int) (16 * anglePercent)
                        || player.onRampLastFrame) {
                            player.hitBoxArea.y = tileLocation.y + (int) (16 * anglePercent);
                            player.velocity.y = 0;
                            player.land(this);
                            return true;
                        }
                    }
                } else if(locationXIndex == 1) {
                    if(player.hitBoxArea.getMiddle().x >= tileLocation.x + 16
                    && player.hitBoxArea.y < tileLocation.y) {
                        player.hitBoxArea.y = tileLocation.y;
                        player.velocity.y = 0;
                        player.onRamp = false;
                        player.land(this);
                        return true;
                    }
                } else if(locationXIndex == 2) {
                    if(player.hitBoxArea.getMiddle().x < tileLocation.x) {
                        player.hitBoxArea.y = tileLocation.y + 16;
                        player.velocity.y = 0;
                        player.onRamp = false;
                        player.land(this);
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

        // Ramp-Right-Half-Bottom //
        else if(tileShape.equals("Ramp-Right-Half-Bottom")) {
            if(movingDir.equals("Right")) {
                if(locationYIndex > 0
                && !player.onRamp) {
                    player.hitBoxArea.x = tileLocation.x - player.hitBoxArea.width;
                    player.velocity.x = 0;
                    return true;
                }
            } else if(movingDir.equals("Left")) {
                if(player.hitBoxArea.getMiddle().x >= tileLocation.x + 16
                && player.hitBoxArea.y < tileLocation.y + 8
                && !(locationYIndex == 0 && player.onHalfRampBottom)) {
                    player.hitBoxArea.x = tileLocation.x + 16;
                    player.velocity.x = 0;
                    return true;
                }
            } else if(movingDir.equals("Middle")) {
                if(locationYIndex == 0 && player.velocity.y <= 0) {
                    float locationDiff = player.hitBoxArea.getMiddle().x - tileLocation.x;
                    float anglePercent = locationDiff / 16;
                    if(player.hitBoxArea.y <= tileLocation.y + (int) (8 * anglePercent)) {
                        player.hitBoxArea.y = tileLocation.y + (int) (8 * anglePercent);
                        player.onHalfRampBottom = true;
                        player.onHalfRampTop = false;
                        player.onRamp = false;
                        player.justSteppedOnRamp = true;
                        return true;
                    }
                }
            } else if(movingDir.equals("Down")) {
                if(locationXIndex == 0) {
                    if(player.velocity.y <= 0 && !player.onRamp) {
                        player.onHalfRampBottom = true;
                        player.justSteppedOnRamp = true;
                        
                        float locationDiff = player.hitBoxArea.getMiddle().x - tileLocation.x;
                        float anglePercent = locationDiff / 16;
                        if(player.hitBoxArea.y <= tileLocation.y + (int) (8 * anglePercent)) {
                            player.hitBoxArea.y = tileLocation.y + (int) (8 * anglePercent);
                            player.velocity.y = 0;
                            player.land(this);
                            return true;
                        }
                    }
                } else if(locationXIndex == 1) {
                    if(player.hitBoxArea.getMiddle().x >= tileLocation.x + 16
                    && player.hitBoxArea.y <= tileLocation.y + 8) {
                        player.hitBoxArea.y = tileLocation.y + 8;
                        player.velocity.y = 0;
                        player.onHalfRampBottom = false;
                        player.land(this);
                        return true;
                    }
                } else if(locationXIndex == 2) {
                    if(player.hitBoxArea.getMiddle().x < tileLocation.x
                    && player.hitBoxArea.y <= tileLocation.y
                    && !player.justSteppedOnRamp) {
                        player.hitBoxArea.y = tileLocation.y;
                        player.velocity.y = 0;
                        player.onHalfRampBottom = false;
                        player.land(this);
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

        // Ramp-Right-Half-Top //
        else if(tileShape.equals("Ramp-Right-Half-Top")) {
            if(movingDir.equals("Right")) {
                if(!player.onHalfRampBottom &&
                ((locationYIndex > 0 && !player.onRamp)
                || (locationYIndex == 0 && player.hitBoxArea.y < tileLocation.y + 8))) {
                    player.hitBoxArea.x = tileLocation.x - player.hitBoxArea.width;
                    player.velocity.x = 0;
                    return true;
                }
            } else if(movingDir.equals("Left")) {
                if(player.hitBoxArea.getMiddle().x >= tileLocation.x + 16
                && !(locationYIndex == 0 && (player.onRamp || player.onHalfRampTop))) {
                    player.hitBoxArea.x = tileLocation.x + 16;
                    player.velocity.x = 0;
                    return true;
                }
            } else if(movingDir.equals("Middle")) {
                if(locationYIndex == 0 && !player.inWallOnRamp && player.velocity.y <= 0) {
                    float locationDiff = player.hitBoxArea.getMiddle().x - tileLocation.x;
                    float anglePercent = locationDiff / 16;
                    if(player.hitBoxArea.y <= tileLocation.y + 8 + (int) (8 * anglePercent)) {
                        player.hitBoxArea.y = tileLocation.y + 8 + (int) (8 * anglePercent);
                        player.onHalfRampTop = true;
                        player.onHalfRampBottom = false;
                        player.onRamp = false;
                        player.justSteppedOnRamp = true;
                        return true;
                    }
                }
            } else if(movingDir.equals("Down")) {
                if(locationXIndex == 0) {
                    if(locationYIndex == 0 && player.velocity.y <= 0) {
                        float locationDiff = player.hitBoxArea.getMiddle().x - tileLocation.x;
                        float anglePercent = locationDiff / 16;
                        if(player.hitBoxArea.y <= tileLocation.y + 8 + (int) (8 * anglePercent)) {
                            player.hitBoxArea.y = tileLocation.y + 8 + (int) (8 * anglePercent);
                            player.onHalfRampTop = true;
                            player.onHalfRampBottom = false;
                            player.onRamp = false;
                            player.justSteppedOnRamp = true;
                            player.land(this);
                            return true;
                        }
                    }
                } else if(locationXIndex == 1) {
                    if(player.hitBoxArea.getMiddle().x >= tileLocation.x + 16
                    && player.hitBoxArea.y <= tileLocation.y + 16) {
                        player.hitBoxArea.y = tileLocation.y + 16;
                        player.velocity.y = 0;
                        player.onHalfRampBottom = false;
                        player.land(this);
                        return true;
                    }
                } else if(locationXIndex == 2) {
                    if(player.hitBoxArea.getMiddle().x < tileLocation.x
                    && player.hitBoxArea.y <= tileLocation.y + 8
                    && !player.justSteppedOnRamp) {
                        player.hitBoxArea.y = tileLocation.y + 8;
                        player.velocity.y = 0;
                        player.onHalfRampBottom = false;
                        player.land(this);
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

        // Ramp-Left-Half-Bottom //
        else if(tileShape.equals("Ramp-Left-Half-Bottom")) {
            if(movingDir.equals("Right")) {
                if(player.hitBoxArea.getMiddle().x < tileLocation.x
                && player.hitBoxArea.y < tileLocation.y + 8
                && !(locationYIndex == 0 && player.onHalfRampBottom)) {
                    player.hitBoxArea.x = tileLocation.x - player.hitBoxArea.width;
                    player.velocity.x = 0;
                    return true;
                }
            } else if(movingDir.equals("Left")) {
                if(locationYIndex > 0
                && !player.onRamp) {
                    player.hitBoxArea.x = tileLocation.x + 16;
                    player.velocity.x = 0;
                    return true;
                }
            } else if(movingDir.equals("Middle")) {
                if(locationYIndex == 0 && player.velocity.y <= 0) {
                    float locationDiff = 16 - (player.hitBoxArea.getMiddle().x - tileLocation.x);
                    float anglePercent = locationDiff / 16;
                    if(player.hitBoxArea.y <= tileLocation.y + (int) (8 * anglePercent)) {
                        player.hitBoxArea.y = tileLocation.y + (int) (8 * anglePercent);
                        player.onHalfRampBottom = true;
                        player.onHalfRampTop = false;
                        player.onRamp = false;
                        player.justSteppedOnRamp = true;
                        return true;
                    }
                }
            } else if(movingDir.equals("Down")) {
                if(locationXIndex == 0) {
                    if(player.velocity.y <= 0 && !player.onRamp) {
                        player.onHalfRampBottom = true;
                        player.justSteppedOnRamp = true;
                        
                        float locationDiff = 16 - (player.hitBoxArea.getMiddle().x - tileLocation.x);
                        float anglePercent = locationDiff / 16;
                        if(player.hitBoxArea.y <= tileLocation.y + (int) (8 * anglePercent)) {
                            player.hitBoxArea.y = tileLocation.y + (int) (8 * anglePercent);
                            player.velocity.y = 0;
                            player.land(this);
                            return true;
                        }
                    }
                } else if(locationXIndex == 1) {
                    if(player.hitBoxArea.getMiddle().x >= tileLocation.x + 16
                    && player.hitBoxArea.y <= tileLocation.y
                    && !player.justSteppedOnRamp) {
                        player.hitBoxArea.y = tileLocation.y;
                        player.velocity.y = 0;
                        player.onHalfRampBottom = false;
                        player.land(this);
                        return true;
                    }
                } else if(locationXIndex == 2) {
                    if(player.hitBoxArea.getMiddle().x < tileLocation.x
                    && player.hitBoxArea.y <= tileLocation.y + 8) {
                        player.hitBoxArea.y = tileLocation.y + 8;
                        player.velocity.y = 0;
                        player.onHalfRampBottom = false;
                        player.land(this);
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

        // Ramp-Left-Half-Top //
        else if(tileShape.equals("Ramp-Left-Half-Top")) {
            if(movingDir.equals("Right")) {
                if(player.hitBoxArea.getMiddle().x < tileLocation.x
                && !(locationYIndex == 0 && (player.onRamp || player.onHalfRampTop))) {
                    player.hitBoxArea.x = tileLocation.x - player.hitBoxArea.width;
                    player.velocity.x = 0;
                    return true;
                }
            } else if(movingDir.equals("Left")) {
                if(!player.onHalfRampBottom &&
                ((locationYIndex > 0 && !player.onRamp)
                || (locationYIndex == 0 && player.hitBoxArea.y < tileLocation.y + 8))) {
                    player.hitBoxArea.x = tileLocation.x + 16;
                    player.velocity.x = 0;
                    return true;
                }
            } else if(movingDir.equals("Middle")) {
                if(locationYIndex == 0 && !player.inWallOnRamp && player.velocity.y <= 0) {
                    float locationDiff = 16 - (player.hitBoxArea.getMiddle().x - tileLocation.x);
                    float anglePercent = locationDiff / 16;
                    if(player.hitBoxArea.y <= tileLocation.y + 8 + (int) (8 * anglePercent)) {
                        player.hitBoxArea.y = tileLocation.y + 8 + (int) (8 * anglePercent);
                        player.onHalfRampTop = true;
                        player.onHalfRampBottom = false;
                        player.onRamp = false;
                        player.justSteppedOnRamp = true;
                        return true;
                    }
                }
            } else if(movingDir.equals("Down")) {
                if(locationXIndex == 0) {
                    if(locationYIndex == 0 && player.velocity.y <= 0) {
                        float locationDiff = 16 - (player.hitBoxArea.getMiddle().x - tileLocation.x);
                        float anglePercent = locationDiff / 16;
                        if(player.hitBoxArea.y <= tileLocation.y + 8 + (int) (8 * anglePercent)) {
                            player.hitBoxArea.y = tileLocation.y + 8 + (int) (8 * anglePercent);
                            player.onHalfRampTop = true;
                            player.onHalfRampBottom = false;
                            player.onRamp = false;
                            player.justSteppedOnRamp = true;
                            return true;
                        }
                    }
                } else if(locationXIndex == 1) {
                    if(player.hitBoxArea.getMiddle().x >= tileLocation.x + 16
                    && player.hitBoxArea.y <= tileLocation.y + 8
                    && !player.justSteppedOnRamp) {
                        player.hitBoxArea.y = tileLocation.y + 8;
                        player.velocity.y = 0;
                        player.onHalfRampBottom = false;
                        player.land(this);
                        return true;
                    }
                } else if(locationXIndex == 2) {
                    if(player.hitBoxArea.getMiddle().x < tileLocation.x
                    && player.hitBoxArea.y <= tileLocation.y + 16) {
                        player.hitBoxArea.y = tileLocation.y + 16;
                        player.velocity.y = 0;
                        player.onHalfRampBottom = false;
                        player.land(this);
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

        // Ceiling-Ramp-Right //
        else if(tileShape.equals("Ceiling-Ramp-Right")) {
            if(movingDir.equals("Right")) {

            } else if(movingDir.equals("Left")) {
                
            } else if(movingDir.equals("Up")) {
                
            }
        }

        // Ceiling-Ramp-Left //
        else if(tileShape.equals("Ceiling-Ramp-Left")) {
            if(movingDir.equals("Right")) {

            } else if(movingDir.equals("Left")) {
                
            } else if(movingDir.equals("Up")) {
                
            }
        }

        return false;
    }
}
