package com.jbs.platformerengine.screen.gamescreen;

import com.badlogic.gdx.Gdx;
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

    public boolean collisionCheck(ScreenChunk[][] screenChunks, Player player, String movingDir, int locationXIndex, int locationYIndex) {
        System.out.println(tileShape + " " + movingDir + " " + locationXIndex + " " + locationYIndex + " " + (player.onHalfRampBottom != null) + " " + (player.onHalfRampTop != null) + " " + (player.justFellInRamp != null) + " " + (player.rightJustFellInRamp != null));

        // Up Collision (All But Ceiling Tiles) //
        if(movingDir.equals("Up")
        && !(tileShape.equals("Ceiling-Ramp-Right")
        || tileShape.equals("Ceiling-Ramp-Left"))) {
            player.hitBoxArea.y = getLocation().y - player.hitBoxArea.height;
            player.velocity.y = 0;
            player.hitCeiling();
            return true;
        }

        // Square //
        else if(tileShape.equals("Square")) {
            if(movingDir.equals("Right")) {
                if(getTargetTile(screenChunks, -1, 0) == null) {
                    player.hitBoxArea.x = getLocation().x - player.hitBoxArea.width;
                    player.velocity.x = 0;
                    return true;
                }
            }
            
            else if(movingDir.equals("Left")) {
                if(getTargetTile(screenChunks, 1, 0) == null) {
                    player.hitBoxArea.x = getLocation().x + 16;
                    player.velocity.x = 0;
                    return true;
                }
            }
            
            else if(movingDir.equals("Down")) {
                if(player.justFellInRamp == null) {
                    player.hitBoxArea.y = getLocation().y + 16;
                    player.velocity.y = 0;
                    player.onRamp = null;
                    player.onHalfRampBottom = null;
                    player.onHalfRampTop = null;
                    player.land(this);
                    return true;
                }
            }
        }

        // Square-Half //
        else if(tileShape.equals("Square-Half")) {
            if(movingDir.equals("Right")) {
                if(player.hitBoxArea.y < getLocation().y + 8
                && getTargetTile(screenChunks, -1, 0) == null) {
                    player.hitBoxArea.x = getLocation().x - player.hitBoxArea.width;
                    player.velocity.x = 0;
                    return true;
                }
            }

            else if(movingDir.equals("Left")) {
                if(player.hitBoxArea.y < getLocation().y + 8
                && getTargetTile(screenChunks, 1, 0) == null) {
                    player.hitBoxArea.x = getLocation().x + 16;
                    player.velocity.x = 0;
                    return true;
                }
            }

            else if(movingDir.equals("Down")) {
                if(player.hitBoxArea.y <= getLocation().y + 8
                
                && !(player.justFellInRamp != null
                && (player.justFellInRamp.tileShape.equals("Ramp-Right-Half-Bottom")
                || player.justFellInRamp.tileShape.equals("Ramp-Left-Half-Bottom")))) {
                    player.hitBoxArea.y = getLocation().y + 8;
                    player.velocity.y = 0;
                    player.onRamp = null;
                    player.onHalfRampBottom = null;
                    player.onHalfRampTop = null;
                    player.land(this);
                    return true;
                }
            }
        }

        // Ramp-Right //
        else if(tileShape.equals("Ramp-Right")) {
            if(movingDir.equals("Right")) {
                Tile bottomLeftTile = getTargetTile(screenChunks, -1, -1);

                if(locationYIndex > 0
                && isBottomRamp(screenChunks)
                
                && !(bottomLeftTile != null
                && (bottomLeftTile.tileShape.equals("Square")
                || bottomLeftTile.tileShape.equals("Ramp-Right")
                || bottomLeftTile.tileShape.equals("Ramp-Right-Half-Top")))) {
                    player.hitBoxArea.x = getLocation().x - player.hitBoxArea.width;
                    player.velocity.x = 0;
                    return true;
                }
            }

            else if(movingDir.equals("Left")) {
                Tile rightTile = getTargetTile(screenChunks, 1, 0);

                if(player.hitBoxArea.getMiddle().x >= getLocation().x + 16
                && !(player.onRamp != null && player.onRamp == this)
                
                && !(rightTile != null
                && rightTile.tileShape.equals("Ramp-Left"))
                
                && !((rightTile != null
                && rightTile.tileShape.equals("Square"))
                && ((player.justFellInRamp != null
                && player.justFellInRamp.tileShape.equals("Ramp-Left")
                && player.justFellInRamp.getLocation().y == this.getLocation().y)
                || (player.onRamp != null
                && player.onRamp.tileShape.equals("Ramp-Left")
                && player.onRamp.getLocation().y == this.getLocation().y)))) {
                    player.hitBoxArea.x = getLocation().x + 16;
                    player.velocity.x = 0;
                    return true;
                }
            }

            else if(movingDir.equals("Middle") || movingDir.equals("Down")) {
                if(locationYIndex == 0
                && (player.hitBoxArea.getMiddle().x >= getLocation().x
                || (player.onRamp != null || player.onHalfRampBottom != null))) {
                    float locationDiff = player.hitBoxArea.getMiddle().x - getLocation().x;
                    float anglePercent = locationDiff / 16;
                    if(anglePercent > 1) {
                        anglePercent = 1.0f;
                    }
    
                    Tile rightTile = getTargetTile(screenChunks, 1, 0);
                    if(player.velocity.y <= 0
                    && (player.hitBoxArea.y <= getLocation().y + (int) (16 * anglePercent)
                    || (player.onRamp != null || player.onHalfRampBottom != null))
                    
                    && !(rightTile != null
                    && rightTile.tileShape.equals("Ramp-Left")
                    && player.hitBoxArea.getMiddle().x >= getLocation().x + 16)) {
                        if(isBottomRamp(screenChunks)
                        && player.hitBoxArea.getMiddle().x <= getLocation().x) {
                            player.hitBoxArea.y = getLocation().y;
                        } else if(!(rightTile != null && rightTile.tileShape.equals("Square"))
                        && ((player.justFellInRamp != null && player.justFellInRamp != this)
                        || (player.rightJustFellInRamp != null && player.rightJustFellInRamp != this)
                        || (player.middleJustFellInRamp != null && player.middleJustFellInRamp != this))) {
                            player.hitBoxArea.y = getLocation().y + 16;
                        } else {
                            player.hitBoxArea.y = getLocation().y + (int) (16 * anglePercent);
                        }

                        player.onRamp = this;
                        player.onHalfRampBottom = null;
                        player.onHalfRampTop = null;
                        player.velocity.y = 0;
                        player.land(this);
                        return true;
                    }
                }
                
                else if(player.hitBoxArea.getMiddle().x <= getLocation().x
                && player.hitBoxArea.y <= getLocation().y
                && isBottomRamp(screenChunks)
                && player.justFellInRamp == null) {
                    player.hitBoxArea.y = getLocation().y;
                    player.velocity.y = 0;
                    player.land(this);
                    return true;
                }
            }
        }

        // Ramp-Left //
        else if(tileShape.equals("Ramp-Left")) {
            if(movingDir.equals("Right")) {
                Tile leftTile = getTargetTile(screenChunks, -1, 0);
                
                if(player.hitBoxArea.getMiddle().x < getLocation().x
                && !(player.onRamp != null && player.onRamp == this)
                
                && !(leftTile != null
                && leftTile.tileShape.equals("Ramp-Right"))
                
                && !((leftTile != null
                && leftTile.tileShape.equals("Square"))
                && ((player.justFellInRamp != null
                && player.justFellInRamp.tileShape.equals("Ramp-Right")
                && player.justFellInRamp.getLocation().y == this.getLocation().y)
                || (player.onRamp != null
                && player.onRamp.tileShape.equals("Ramp-Right")
                && player.onRamp.getLocation().y == this.getLocation().y)))) {
                    player.hitBoxArea.x = getLocation().x - player.hitBoxArea.width;
                    player.velocity.x = 0;
                    return true;
                }
            }
            
            else if(movingDir.equals("Left")) {
                Tile bottomRightTile = getTargetTile(screenChunks, 1, -1);

                if(locationYIndex > 0
                && isBottomRamp(screenChunks)
                
                && !(bottomRightTile != null
                && (bottomRightTile.tileShape.equals("Square")
                || bottomRightTile.tileShape.equals("Ramp-Left")
                || bottomRightTile.tileShape.equals("Ramp-Left-Half-Top")))) {
                    player.hitBoxArea.x = getLocation().x + 16;
                    player.velocity.x = 0;
                    return true;
                }
            }
            
            else if(movingDir.equals("Middle") || movingDir.equals("Down")) {
                if(locationYIndex == 0
                && (player.hitBoxArea.getMiddle().x < getLocation().x + 16
                || (player.onRamp != null || player.onHalfRampBottom != null))) {
                    float locationDiff = 16 - (player.hitBoxArea.getMiddle().x - getLocation().x);
                    float anglePercent = locationDiff / 16;
                    if(anglePercent > 1) {
                        anglePercent = 1.0f;
                    }
    
                    Tile leftTile = getTargetTile(screenChunks, -1, 0);
                    if(player.velocity.y <= 0
                    && (player.hitBoxArea.y <= getLocation().y + (int) (16 * anglePercent)
                    || (player.onRamp != null || player.onHalfRampBottom != null))
                    
                    && !(leftTile != null
                    && leftTile.tileShape.equals("Ramp-Right")
                    && player.hitBoxArea.getMiddle().x < getLocation().x)) {
                        if(isBottomRamp(screenChunks)
                        && player.hitBoxArea.getMiddle().x >= getLocation().x + 16) {
                            player.hitBoxArea.y = getLocation().y;
                        } else if(!(leftTile != null && leftTile.tileShape.equals("Square"))
                        && ((player.justFellInRamp != null && player.justFellInRamp != this)
                        || (player.rightJustFellInRamp != null && player.rightJustFellInRamp != this)
                        || (player.middleJustFellInRamp != null && player.middleJustFellInRamp != this))) {
                            player.hitBoxArea.y = getLocation().y + 16;
                        } else {
                            player.hitBoxArea.y = getLocation().y + (int) (16 * anglePercent);
                        }

                        player.onRamp = this;
                        player.onHalfRampBottom = null;
                        player.onHalfRampTop = null;
                        player.velocity.y = 0;
                        player.land(this);
                        return true;
                    }
                }
                
                else if(player.hitBoxArea.getMiddle().x >= getLocation().x + 16
                && player.hitBoxArea.y <= getLocation().y
                && isBottomRamp(screenChunks)
                && player.justFellInRamp == null) {
                    player.hitBoxArea.y = getLocation().y;
                    player.velocity.y = 0;
                    player.land(this);
                    return true;
                }
            }
        }

        // Ramp-Right-Half-Bottom //
        else if(tileShape.equals("Ramp-Right-Half-Bottom")) {
            if(movingDir.equals("Right")) {
                Tile bottomLeftTile = getTargetTile(screenChunks, -1, -1);

                if(locationYIndex > 0
                && isBottomRamp(screenChunks)
                
                && !(bottomLeftTile != null
                && (bottomLeftTile.tileShape.equals("Square")))) {
                    player.hitBoxArea.x = getLocation().x - player.hitBoxArea.width;
                    player.velocity.x = 0;
                    return true;
                }
            }
            
            else if(movingDir.equals("Left")) {
                if(player.hitBoxArea.y < getLocation().y + 8
                && player.hitBoxArea.getMiddle().x >= getLocation().x + 16
                && !(player.onHalfRampBottom != null && player.onHalfRampBottom == this)) {
                    player.hitBoxArea.x = getLocation().x + 16;
                    player.velocity.x = 0;
                    return true;
                }
            }
            
            else if(movingDir.equals("Middle") || movingDir.equals("Down")) {
                if(locationYIndex == 0
                && (player.hitBoxArea.getMiddle().x >= getLocation().x
                || (player.onHalfRampBottom != null || player.onHalfRampTop != null))) {
                    float locationDiff = player.hitBoxArea.getMiddle().x - getLocation().x;
                    float anglePercent = locationDiff / 16;
                    if(anglePercent > 1) {
                        anglePercent = 1.0f;
                    }
    
                    if(player.velocity.y <= 0
                    && (player.hitBoxArea.y <= getLocation().y + (int) (8 * anglePercent)
                    
                    || (player.onHalfRampBottom != null
                    || (player.onHalfRampTop != null && player.hitBoxArea.getMiddle().x <= player.onHalfRampTop.getLocation().x)))) {
                        if(isBottomRamp(screenChunks)
                        && player.hitBoxArea.getMiddle().x <= getLocation().x) {
                            player.hitBoxArea.y = getLocation().y;
                        } else {
                            player.hitBoxArea.y = getLocation().y + (int) (8 * anglePercent);
                        }

                        player.onHalfRampBottom = this;
                        player.onHalfRampTop = null;
                        player.onRamp = null;
                        player.velocity.y = 0;
                        player.land(this);
                        return true;
                    }
                }

                else if(player.hitBoxArea.getMiddle().x <= getLocation().x
                && player.hitBoxArea.y <= getLocation().y
                && isBottomRamp(screenChunks)
                && player.justFellInRamp == null) {
                    player.onHalfRampBottom = this;
                    player.onHalfRampTop = null;
                    player.onRamp = null;
                    player.hitBoxArea.y = getLocation().y;
                    player.velocity.y = 0;
                    player.land(this);
                    return true;
                }
            }
        }

        // Ramp-Right-Half-Top //
        else if(tileShape.equals("Ramp-Right-Half-Top")) {
            if(movingDir.equals("Right")) {
                if((locationYIndex > 0 || player.hitBoxArea.y < getLocation().y + 8)
                && isBottomRamp(screenChunks)
                && getTargetTile(screenChunks, -1, 0) == null) {
                    player.hitBoxArea.x = getLocation().x - player.hitBoxArea.width;
                    player.velocity.x = 0;
                    return true;
                }
            }
            
            else if(movingDir.equals("Left")) {
                if(player.hitBoxArea.getMiddle().x >= getLocation().x + 16
                && !(player.onHalfRampTop != null && player.onHalfRampTop == this)) {
                    player.hitBoxArea.x = getLocation().x + 16;
                    player.velocity.x = 0;
                    return true;
                }
            }
            
            else if(movingDir.equals("Middle") || movingDir.equals("Down")) {
                Tile leftTile = getTargetTile(screenChunks, -1, 0);

                if(locationYIndex == 0
                && (player.hitBoxArea.getMiddle().x >= getLocation().x
                || (player.onHalfRampTop != null || player.onRamp != null))) {
                    float locationDiff = player.hitBoxArea.getMiddle().x - getLocation().x;
                    float anglePercent = locationDiff / 16;
                    if(anglePercent > 1) {
                        anglePercent = 1.0f;
                    }
    
                    if(player.velocity.y <= 0
                    && (player.hitBoxArea.y <= getLocation().y + 8 + (int) (8 * anglePercent)
                    || (player.onHalfRampTop != null || player.onRamp != null))
                    
                    && !(player.hitBoxArea.getMiddle().x <= getLocation().x
                    && leftTile != null
                    && leftTile.tileShape.equals("Ramp-Right-Half-Bottom"))) {
                        if(isBottomRamp(screenChunks)
                        && player.hitBoxArea.getMiddle().x <= getLocation().x) {
                            player.hitBoxArea.y = getLocation().y + 8;
                        } else {
                            player.hitBoxArea.y = getLocation().y + 8 + (int) (8 * anglePercent);
                        }

                        player.onHalfRampTop = this;
                        player.onHalfRampBottom = null;
                        player.onRamp = null;
                        player.velocity.y = 0;
                        player.land(this);
                        return true;
                    }
                }

                else if(player.hitBoxArea.getMiddle().x <= getLocation().x
                && player.hitBoxArea.y <= getLocation().y
                && isBottomRamp(screenChunks)
                && player.justFellInRamp == null
                
                && !(leftTile != null
                && leftTile.tileShape.equals("Ramp-Right-Half-Bottom"))) {
                    player.hitBoxArea.y = getLocation().y + 8;
                    player.velocity.y = 0;
                    player.land(this);
                    return true;
                }
            }
        }

        // Ramp-Left-Half-Bottom //
        else if(tileShape.equals("Ramp-Left-Half-Bottom")) {
            if(movingDir.equals("Right")) {
                if(player.hitBoxArea.y < getLocation().y + 8
                && player.hitBoxArea.getMiddle().x < getLocation().x
                && !(player.onHalfRampBottom != null && player.onHalfRampBottom == this)) {
                    player.hitBoxArea.x = getLocation().x - player.hitBoxArea.width;
                    player.velocity.x = 0;
                    return true;
                }
            }

            else if(movingDir.equals("Left")) {
                Tile bottomRightTile = getTargetTile(screenChunks, 1, -1);

                if(locationYIndex > 0
                && isBottomRamp(screenChunks)
                
                && !(bottomRightTile != null
                && (bottomRightTile.tileShape.equals("Square")))) {
                    player.hitBoxArea.x = getLocation().x + 16;
                    player.velocity.x = 0;
                    return true;
                }
            }
        
            else if(movingDir.equals("Middle") || movingDir.equals("Down")) {
                if(locationYIndex == 0
                && (player.hitBoxArea.getMiddle().x < getLocation().x + 16
                || (player.onHalfRampBottom != null || player.onHalfRampTop != null))) {
                    float locationDiff = 16 - (player.hitBoxArea.getMiddle().x - getLocation().x);
                    float anglePercent = locationDiff / 16;
                    if(anglePercent > 1) {
                        anglePercent = 1.0f;
                    }
    
                    if(player.velocity.y <= 0
                    && (player.hitBoxArea.y <= getLocation().y + (int) (8 * anglePercent)
                    
                    || (player.onHalfRampBottom != null
                    || (player.onHalfRampTop != null && player.hitBoxArea.getMiddle().x > player.onHalfRampTop.getLocation().x + 16)))) {
                        if(isBottomRamp(screenChunks)
                        && player.hitBoxArea.getMiddle().x >= getLocation().x + 16) {
                            player.hitBoxArea.y = getLocation().y;
                        } else {
                            player.hitBoxArea.y = getLocation().y + (int) (8 * anglePercent);
                        }

                        player.onHalfRampBottom = this;
                        player.onHalfRampTop = null;
                        player.onRamp = null;
                        player.velocity.y = 0;
                        player.land(this);
                        return true;
                    }
                }

                else if(player.hitBoxArea.getMiddle().x >= getLocation().x + 16
                && player.hitBoxArea.y <= getLocation().y
                && isBottomRamp(screenChunks)
                && player.justFellInRamp == null) {
                    player.hitBoxArea.y = getLocation().y;
                    player.velocity.y = 0;
                    player.land(this);
                    return true;
                }
            }
        }

        // Ramp-Left-Half-Top //
        else if(tileShape.equals("Ramp-Left-Half-Top")) {
            if(movingDir.equals("Right")) {
                if(player.hitBoxArea.getMiddle().x < getLocation().x
                && !(player.onHalfRampTop != null && player.onHalfRampTop == this)) {
                    player.hitBoxArea.x = getLocation().x - player.hitBoxArea.width;
                    player.velocity.x = 0;
                    return true;
                }
            }

            else if(movingDir.equals("Left")) {
                if((locationYIndex > 0 || player.hitBoxArea.y < getLocation().y + 8)
                && isBottomRamp(screenChunks)
                && getTargetTile(screenChunks, 1, 0) == null) {
                    player.hitBoxArea.x = getLocation().x + 16;
                    player.velocity.x = 0;
                    return true;
                }
            }
        
            else if(movingDir.equals("Middle") || movingDir.equals("Down")) {
                Tile rightTile = getTargetTile(screenChunks, 1, 0);

                if(locationYIndex == 0
                && (player.hitBoxArea.getMiddle().x < getLocation().x + 16
                || (player.onHalfRampTop != null || player.onRamp != null))) {
                    float locationDiff = 16 - (player.hitBoxArea.getMiddle().x - getLocation().x);
                    float anglePercent = locationDiff / 16;
                    if(anglePercent > 1) {
                        anglePercent = 1.0f;
                    }
    
                    if(player.velocity.y <= 0
                    && (player.hitBoxArea.y <= getLocation().y + 8 + (int) (8 * anglePercent)
                    || (player.onHalfRampTop != null || player.onRamp != null))
                    
                    && !(player.hitBoxArea.getMiddle().x > getLocation().x + 16
                    && rightTile != null
                    && rightTile.tileShape.equals("Ramp-Left-Half-Bottom"))) {
                        if(isBottomRamp(screenChunks)
                        && player.hitBoxArea.getMiddle().x >= getLocation().x + 16) {
                            player.hitBoxArea.y = getLocation().y + 8;
                        } else {
                            player.hitBoxArea.y = getLocation().y + 8 + (int) (8 * anglePercent);
                        }

                        player.onHalfRampTop = this;
                        player.onHalfRampBottom = null;
                        player.onRamp = null;
                        player.velocity.y = 0;
                        player.land(this);
                        return true;
                    }
                }

                else if(player.hitBoxArea.getMiddle().x >= getLocation().x + 16
                && player.hitBoxArea.y <= getLocation().y
                && isBottomRamp(screenChunks)
                && player.justFellInRamp == null
                
                && !(rightTile != null
                && rightTile.tileShape.equals("Ramp-Left-Half-Bottom"))) {
                    player.hitBoxArea.y = getLocation().y + 8;
                    player.velocity.y = 0;
                    player.land(this);
                    return true;
                }
            }
        }

        // Ceiling-Ramp-Right //
        else if(tileShape.equals("Ceiling-Ramp-Right")) {

        }

        // Ceiling-Ramp-Left //
        else if(tileShape.equals("Ceiling-Ramp-Left")) {

        }

        return false;
    }

    public Point getLocation() {
        int xLoc = (chunkX * Gdx.graphics.getWidth()) + (tileX * 16);
        int yLoc = (chunkY * Gdx.graphics.getHeight()) + (tileY * 16);
        return new Point(xLoc, yLoc);
    }

    public Tile getTargetTile(ScreenChunk[][] screenChunks, int xMod, int yMod) {
        Point targetTileLocation = getLocation();
        targetTileLocation.x += (xMod * 16);
        targetTileLocation.y += (yMod * 16);

        int chunkX = targetTileLocation.x / Gdx.graphics.getWidth();
        int chunkY = targetTileLocation.y / Gdx.graphics.getHeight();
        int tileX = (targetTileLocation.x % Gdx.graphics.getWidth()) / 16;
        int tileY = (targetTileLocation.y % Gdx.graphics.getHeight()) / 16;

        if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length
        && tileX >= 0 && tileX < screenChunks[0][0].tiles.length && tileY >= 0 && tileY < screenChunks[0][0].tiles[0].length
        && screenChunks[chunkX][chunkY].tiles[tileX][tileY] != null) {
            return screenChunks[chunkX][chunkY].tiles[tileX][tileY];
        }

        return null;
    }

    public static boolean isEmptyTile(ScreenChunk[][] screenChunks, int xLoc, int yLoc) {
        int chunkX = xLoc / Gdx.graphics.getWidth();
        int chunkY = yLoc / Gdx.graphics.getHeight();
        int tileX = (xLoc % Gdx.graphics.getWidth()) / 16;
        int tileY = (yLoc % Gdx.graphics.getHeight()) / 16;

        if(chunkX >= 0 && chunkX < screenChunks.length && chunkY >= 0 && chunkY < screenChunks[0].length
        && tileX >= 0 && tileX < screenChunks[0][0].tiles.length && tileY >= 0 && tileY < screenChunks[0][0].tiles[0].length
        && screenChunks[chunkX][chunkY].tiles[tileX][tileY] == null) {
            return true;
        }

        return false;
    }

    public boolean isBottomRamp(ScreenChunk[][] screenChunks) {
        Tile targetTile = null;
        if(tileShape.contains("Right")) {
            targetTile = getTargetTile(screenChunks, -1, -1);
        } else {
            targetTile = getTargetTile(screenChunks, 1, -1);
        }

        if(targetTile != null) {
            if(targetTile.tileShape.length() >= 4
            && targetTile.tileShape.substring(0, 4).equals("Ramp")) {
                return false;
            }
        } 

        return true;
    }
}
