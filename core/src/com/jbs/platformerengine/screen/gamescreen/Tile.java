package com.jbs.platformerengine.screen.gamescreen;

import com.badlogic.gdx.Gdx;
import com.jbs.platformerengine.gamedata.Point;
import com.jbs.platformerengine.gamedata.entity.mob.Mob;

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

    public boolean moveOntoCheck;

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

    public Tile collisionCheck(ScreenChunk[][] screenChunks, Mob player, String movingDir, int locationXIndex, int locationYIndex) {
        // System.out.println(tileShape + " " + movingDir + " " + locationXIndex + " " + locationYIndex + " " + tileX + " " + tileY + " " + player.sideSpeedMiddleTileLastFrame);

        float baseFloorSpeed = -4.00f;
        if(player.flying) {
            baseFloorSpeed = 0.00f;
        }

        moveOntoCheck = false;

        float locationDiffRight = player.hitBoxArea.getMiddle().x - getLocation().x;
        float locationDiffLeft = 16 - (player.hitBoxArea.getMiddle().x - getLocation().x);
        float anglePercentRight = locationDiffRight / 16;
        float anglePercentLeft = locationDiffLeft / 16;
        if(anglePercentRight > 1) {
            anglePercentRight = 1.0f;
        }
        if(anglePercentLeft > 1) {
            anglePercentLeft = 1.0f;
        }

        // Up Collision (All But Ceiling Tiles) //
        if(movingDir.equals("Up")
        && !tileShape.contains("Ceiling")) {
            if(player.hitBoxArea.y + player.hitBoxArea.height - 1 > getLocation().y) {
                player.hitBoxArea.y = getLocation().y - player.hitBoxArea.height;
                player.velocity.y = 0;
                player.hitCeiling();
                return this;
            }
        }

        // Square //
        else if(tileShape.equals("Square")) {
            if(movingDir.equals("Right")) {
                Tile leftTile = getTargetTile(screenChunks, -1, 0);

                if(leftTile == null
                || leftTile.tileShape.equals("Square-Half")
                || leftTile.tileShape.equals("Ramp-Right-Half-Bottom")
                || leftTile.tileShape.equals("Ceiling-Ramp-Right")) {
                    player.hitBoxArea.x = getLocation().x - player.hitBoxArea.width;
                    return this;
                }
            }
            
            else if(movingDir.equals("Left")) {
                Tile rightTile = getTargetTile(screenChunks, 1, 0);

                if(rightTile == null
                || rightTile.tileShape.equals("Square-Half")
                || rightTile.tileShape.equals("Ramp-Left-Half-Bottom")
                || rightTile.tileShape.equals("Ceiling-Ramp-Left")) {
                    player.hitBoxArea.x = getLocation().x + 16;
                    return this;
                }
            }
            
            else if(movingDir.equals("Down")) {
                Tile leftTile = getTargetTile(screenChunks, -1, 0);
                Tile rightTile = getTargetTile(screenChunks, 1, 0);

                if(!(player.hitBoxArea.x < getLocation().x
                && leftTile != null
                && (leftTile.tileShape.equals("Ramp-Right")
                || leftTile.tileShape.equals("Ramp-Right-Half-Top"))
                && player.hitBoxArea.width > 16)
                
                && !(player.hitBoxArea.x + player.hitBoxArea.width >= getLocation().x + 16
                && rightTile != null
                && (rightTile.tileShape.equals("Ramp-Left")
                || rightTile.tileShape.equals("Ramp-Left-Half-Top"))
                && player.hitBoxArea.width > 16)
                
                && !(player.justFellInRamp != null
                && (player.justFellInRamp.tileShape.equals("Ramp-Right")
                || player.justFellInRamp.tileShape.equals("Ramp-Left")
                || player.justFellInRamp.tileShape.contains("Top")))
                
                && !(player.rightJustFellInRamp != null
                && (player.rightJustFellInRamp.tileShape.equals("Ramp-Right")
                || player.rightJustFellInRamp.tileShape.equals("Ramp-Left")
                || player.rightJustFellInRamp.tileShape.contains("Top")))) {
                    player.hitBoxArea.y = getLocation().y + 16;
                    player.velocity.y = baseFloorSpeed;
                    player.land(this);
                    player.onRamp = null;
                    player.onHalfRampBottom = null;
                    player.onHalfRampTop = null;
                    return this;
                }
            }
        }

        // Square-Half //
        else if(tileShape.equals("Square-Half")) {
            if(movingDir.equals("Right")) {
                Tile leftTile = getTargetTile(screenChunks, -1, 0);

                if(player.hitBoxArea.y < getLocation().y + 8
                && leftTile == null) {
                    player.hitBoxArea.x = getLocation().x - player.hitBoxArea.width;
                    return this;
                }
            }
            
            else if(movingDir.equals("Left")) {
                Tile rightTile = getTargetTile(screenChunks, 1, 0);

                if(player.hitBoxArea.y < getLocation().y + 8
                && rightTile == null) {
                    player.hitBoxArea.x = getLocation().x + 16;
                    return this;
                }
            }
            
            else if(movingDir.equals("Down")) {
                Tile leftTile = getTargetTile(screenChunks, -1, 0);
                Tile rightTile = getTargetTile(screenChunks, 1, 0);

                if(player.hitBoxArea.y <= getLocation().y + 8
                
                && !(player.hitBoxArea.x < getLocation().x
                && leftTile != null
                && leftTile.tileShape.equals("Ramp-Right-Half-Bottom")
                && player.hitBoxArea.width > 16)
                
                && !(player.hitBoxArea.x + player.hitBoxArea.width >= getLocation().x + 16
                && rightTile != null
                && rightTile.tileShape.equals("Ramp-Left-Half-Bottom")
                && player.hitBoxArea.width > 16)
                
                && !(player.justFellInRamp != null
                && player.justFellInRamp.tileShape.contains("Bottom"))
                
                && !(player.rightJustFellInRamp != null
                && player.rightJustFellInRamp.tileShape.contains("Bottom"))) {
                    player.hitBoxArea.y = getLocation().y + 8;
                    player.velocity.y = baseFloorSpeed;
                    player.land(this);
                    player.onRamp = null;
                    player.onHalfRampBottom = null;
                    player.onHalfRampTop = null;
                    return this;
                }
            }
        }

        // Ramp-Right //
        else if(tileShape.equals("Ramp-Right")) {
            if(movingDir.equals("Right")) {
                Tile bottomLeftTile = getTargetTile(screenChunks, -1, -1);
                Tile leftTile = getTargetTile(screenChunks, -1, 0);

                if(isBottomRamp(screenChunks)
                && !(bottomLeftTile != null && bottomLeftTile.tileShape.equals("Square"))
                && !(leftTile != null && leftTile.tileShape.equals("Ramp-Left"))
                && !(leftTile != null && leftTile.tileShape.equals("Ramp-Left-Half-Bottom"))
                && player.hitBoxArea.y < getLocation().y) {
                    player.hitBoxArea.x = getLocation().x - player.hitBoxArea.width;
                    return this;
                }

                else if((int) player.hitBoxArea.y <= getLocation().y + (int) (16 * anglePercentRight)) {
                    player.hitBoxArea.y = getLocation().y + (int) (16 * anglePercentRight);
                    player.velocity.y = baseFloorSpeed;
                    player.land(this);
                    player.onRamp = this;
                    player.onHalfRampBottom = null;
                    player.onHalfRampTop = null;
                    moveOntoCheck = true;
                    return this;
                }
            }
            
            else if(movingDir.equals("Left")) {
                Tile rightTile = getTargetTile(screenChunks, 1, 0);

                if(!(rightTile != null
                && (rightTile.tileShape.equals("Square")
                || rightTile.tileShape.equals("Ramp-Left")
                || rightTile.tileShape.equals("Ramp-Left-Half-Top")))
                
                && !(player.middleJustFellInRamp != null && player.middleJustFellInRamp == this)
                && !(player.justFellInRamp != null && player.justFellInRamp == this)
                && !(player.rightJustFellInRamp != null && player.rightJustFellInRamp == this)) {
                    player.hitBoxArea.x = getLocation().x + 16;
                    return this;
                }
            }
            
            else if((movingDir.equals("Down") || movingDir.equals("Middle"))
            && player.velocity.y <= 0) {
                Tile rightTile = getTargetTile(screenChunks, 1, 0);
                
                // Left Of Ramp //
                if(player.hitBoxArea.getMiddle().x < getLocation().x) {
                    if(isBottomRamp(screenChunks)
                    && player.hitBoxArea.y <= getLocation().y) {
                        player.hitBoxArea.y = getLocation().y;
                        player.velocity.y = 0;
                        player.land(this);
                        player.onRamp = this;
                        player.onHalfRampBottom = null;
                        player.onHalfRampTop = null;
                        return this;
                    }
                }

                // Right Of Ramp //
                else if(player.hitBoxArea.getMiddle().x >= getLocation().x + 16) {
                    if(isTopRamp(screenChunks)
                    
                    && !(rightTile != null
                    && (rightTile.tileShape.equals("Ramp-Left")
                    || rightTile.tileShape.equals("Ramp-Left-Half-Top")))) {
                        player.hitBoxArea.y = getLocation().y + 16;
                        player.velocity.y = 0;
                        player.land(this);
                        player.onRamp = this;
                        player.onHalfRampBottom = null;
                        player.onHalfRampTop = null;
                        return this;
                    }
                }
            
                // Middle Of Ramp //
                else if(player.hitBoxArea.getMiddle().x >= getLocation().x
                && player.hitBoxArea.getMiddle().x < getLocation().x + 16
                
                && !(!(rightTile != null && rightTile.tileShape.equals("Square"))
                && player.rightJustFellInRamp != null
                && player.rightJustFellInRamp != this
                && (player.rightJustFellInRamp.tileShape.equals("Ramp-Right")
                || player.rightJustFellInRamp.tileShape.equals("Ramp-Left")
                || player.rightJustFellInRamp.tileShape.contains("Top"))
                && player.rightJustFellInRamp.getLocation().y == getLocation().y
                && !player.rightJustFellInRamp.isContiguous(screenChunks, this))
                
                && !(!(rightTile != null && rightTile.tileShape.equals("Square"))
                && player.onRamp != null
                && player.onRamp != this
                && player.onRamp.getLocation().y == getLocation().y)) {
                    if(player.hitBoxArea.y <= getLocation().y + (int) (16 * anglePercentRight)
                    || player.onRamp != null) {
                        player.hitBoxArea.y = getLocation().y + (int) (16 * anglePercentRight);
                        player.velocity.y = baseFloorSpeed;
                        player.land(this);
                        player.onRamp = this;
                        player.onHalfRampBottom = null;
                        player.onHalfRampTop = null;
                        return this;
                    }
                }
            }
        }

        // Ramp-Left //
        else if(tileShape.equals("Ramp-Left")) {
            if(movingDir.equals("Right")) {
                Tile leftTile = getTargetTile(screenChunks, -1, 0);

                if(!(leftTile != null
                && (leftTile.tileShape.equals("Square")
                || leftTile.tileShape.equals("Ramp-Right")
                || leftTile.tileShape.equals("Ramp-Right-Half-Top")))
                
                && !(player.middleJustFellInRamp != null && player.middleJustFellInRamp == this)
                && !(player.justFellInRamp != null && player.justFellInRamp == this)
                && !(player.rightJustFellInRamp != null && player.rightJustFellInRamp == this)) {
                    player.hitBoxArea.x = getLocation().x - player.hitBoxArea.width;
                    return this;
                }
            }
            
            else if(movingDir.equals("Left")) {
                Tile bottomRightTile = getTargetTile(screenChunks, 1, -1);
                Tile rightTile = getTargetTile(screenChunks, 1, 0);

                if(isBottomRamp(screenChunks)
                && !(bottomRightTile != null && bottomRightTile.tileShape.equals("Square"))
                && !(rightTile != null && rightTile.tileShape.equals("Ramp-Right"))
                && !(rightTile != null && rightTile.tileShape.equals("Ramp-Right-Half-Bottom"))
                && player.hitBoxArea.y < getLocation().y) {
                    player.hitBoxArea.x = getLocation().x + 16;
                    player.velocity.x = 0;
                    return this;
                }

                else if((int) player.hitBoxArea.y < getLocation().y + (int) (16 * anglePercentLeft)) {
                    player.hitBoxArea.y = getLocation().y + (int) (16 * anglePercentLeft);
                    player.velocity.y = baseFloorSpeed;
                    player.land(this);
                    player.onRamp = this;
                    player.onHalfRampBottom = null;
                    player.onHalfRampTop = null;
                    moveOntoCheck = true;
                    return this;
                }
            }
            
            else if((movingDir.equals("Down") || movingDir.equals("Middle"))
            && player.velocity.y <= 0) {
                Tile leftTile = getTargetTile(screenChunks, -1, 0);

                // Left Of Ramp //
                if(player.hitBoxArea.getMiddle().x < getLocation().x) {
                    if(isTopRamp(screenChunks)
                    
                    && !(leftTile != null
                    && (leftTile.tileShape.equals("Ramp-Right")
                    || leftTile.tileShape.equals("Ramp-Right-Half-Top")))) {
                        player.hitBoxArea.y = getLocation().y + 16;
                        player.velocity.y = 0;
                        player.land(this);
                        player.onRamp = this;
                        player.onHalfRampBottom = null;
                        player.onHalfRampTop = null;
                        return this;
                    }
                }

                // Right Of Ramp //
                else if(player.hitBoxArea.getMiddle().x >= getLocation().x + 16) {
                    if(isBottomRamp(screenChunks)
                    && player.hitBoxArea.y <= getLocation().y) {
                        player.hitBoxArea.y = getLocation().y;
                        player.velocity.y = 0;
                        player.land(this);
                        player.onRamp = this;
                        player.onHalfRampBottom = null;
                        player.onHalfRampTop = null;
                        return this;
                    }
                }

                // Middle Of Ramp //
                else if(player.hitBoxArea.getMiddle().x >= getLocation().x
                && player.hitBoxArea.getMiddle().x < getLocation().x + 16
                
                && !(!(leftTile != null && leftTile.tileShape.equals("Square"))
                && player.justFellInRamp != null
                && player.justFellInRamp != this
                && (player.justFellInRamp.tileShape.equals("Ramp-Right")
                || player.justFellInRamp.tileShape.equals("Ramp-Left")
                || player.justFellInRamp.tileShape.contains("Top"))
                && player.justFellInRamp.getLocation().y == getLocation().y
                && !player.justFellInRamp.isContiguous(screenChunks, this))
                
                && !(!(leftTile != null && leftTile.tileShape.equals("Square"))
                && player.onRamp != null
                && player.onRamp != this
                && player.onRamp.getLocation().y == getLocation().y)) {
                    if(player.hitBoxArea.y <= getLocation().y + (int) (16 * anglePercentLeft)
                    || player.onRamp != null) {
                        player.hitBoxArea.y = getLocation().y + (int) (16 * anglePercentLeft);
                        player.velocity.y = baseFloorSpeed;
                        player.land(this);
                        player.onRamp = this;
                        player.onHalfRampBottom = null;
                        player.onHalfRampTop = null;
                        return this;
                    }
                }
            }
        }

        // Ramp-Right-Half-Bottom //
        else if(tileShape.equals("Ramp-Right-Half-Bottom")) {
            if(movingDir.equals("Right")) {
                Tile bottomLeftTile = getTargetTile(screenChunks, -1, -1);
                Tile leftTile = getTargetTile(screenChunks, -1, 0);

                if(isBottomRamp(screenChunks)
                && !(bottomLeftTile != null && bottomLeftTile.tileShape.equals("Square"))
                && !(leftTile != null && leftTile.tileShape.equals("Ramp-Left"))
                && !(leftTile != null && leftTile.tileShape.equals("Ramp-Left-Half-Bottom"))
                && player.hitBoxArea.y < getLocation().y) {
                    player.hitBoxArea.x = getLocation().x - player.hitBoxArea.width;
                    player.velocity.x = 0;
                    return this;
                }

                else if((int) player.hitBoxArea.y < getLocation().y + (int) (16 * anglePercentRight)) {
                    player.hitBoxArea.y = getLocation().y + (int) (8 * anglePercentRight);
                    player.velocity.y = baseFloorSpeed;
                    player.land(this);
                    player.onRamp = null;
                    player.onHalfRampBottom = this;
                    player.onHalfRampTop = null;
                    moveOntoCheck = true;
                    return this;
                }
            }
            
            else if(movingDir.equals("Left")) {
                Tile rightTile = getTargetTile(screenChunks, 1, 0);

                if(!(rightTile != null
                && (rightTile.tileShape.equals("Square-Half")
                || rightTile.tileShape.equals("Ramp-Left-Half-Bottom")
                || rightTile.tileShape.equals("Ramp-Right-Half-Top")))
                
                && player.hitBoxArea.y < getLocation().y + 8
                
                && !(player.middleJustFellInRamp != null && player.middleJustFellInRamp == this)
                && !(player.justFellInRamp != null && player.justFellInRamp == this)
                && !(player.rightJustFellInRamp != null && player.rightJustFellInRamp == this)) {
                    player.hitBoxArea.x = getLocation().x + 16;
                    return this;
                }
            }
            
            else if((movingDir.equals("Down") || movingDir.equals("Middle"))
            && player.velocity.y <= 0) {
                Tile rightTile = getTargetTile(screenChunks, 1, 0);
                
                // Left Of Ramp //
                if(player.hitBoxArea.getMiddle().x < getLocation().x) {
                    if(isBottomRamp(screenChunks)
                    && player.hitBoxArea.y <= getLocation().y) {
                        player.hitBoxArea.y = getLocation().y;
                        player.velocity.y = 0;
                        player.land(this);
                        player.onRamp = null;
                        player.onHalfRampBottom = this;
                        player.onHalfRampTop = null;
                        return this;
                    }
                }

                // Right Of Ramp //
                else if(player.hitBoxArea.getMiddle().x >= getLocation().x + 16) {
                    if(isTopRamp(screenChunks)
                    
                    && !(rightTile != null
                    && rightTile.tileShape.equals("Ramp-Left-Half-Bottom"))
                    
                    && player.hitBoxArea.y <= getLocation().y + 8) {
                        player.hitBoxArea.y = getLocation().y + 8;
                        player.velocity.y = 0;
                        player.land(this);
                        player.onRamp = null;
                        player.onHalfRampBottom = this;
                        player.onHalfRampTop = null;
                        return this;
                    }
                }

                // Middle Of Ramp //
                else if(player.hitBoxArea.getMiddle().x >= getLocation().x
                && player.hitBoxArea.getMiddle().x < getLocation().x + 16
                
                && !(!(rightTile != null && rightTile.tileShape.equals("Square-Half"))
                && player.rightJustFellInRamp != null
                && player.rightJustFellInRamp != this
                && player.rightJustFellInRamp.tileShape.contains("Bottom")
                && player.rightJustFellInRamp.getLocation().y == getLocation().y
                && !player.rightJustFellInRamp.isContiguous(screenChunks, this))
                
                && !(!(rightTile != null && rightTile.tileShape.equals("Square-Half"))
                && player.onHalfRampBottom != null
                && player.onHalfRampBottom != this
                && player.onHalfRampBottom.getLocation().y == getLocation().y)) {
                    if(player.hitBoxArea.y <= getLocation().y + (int) (8 * anglePercentRight)
                    || player.onHalfRampBottom != null) {
                        player.hitBoxArea.y = getLocation().y + (int) (8 * anglePercentRight);
                        player.velocity.y = baseFloorSpeed;
                        player.land(this);
                        player.onRamp = null;
                        player.onHalfRampBottom = this;
                        player.onHalfRampTop = null;
                        return this;
                    }
                }
            }
        }

        // Ramp-Left-Half-Bottom //
        else if(tileShape.equals("Ramp-Left-Half-Bottom")) {
            if(movingDir.equals("Right")) {
                Tile leftTile = getTargetTile(screenChunks, -1, 0);

                if(!(leftTile != null
                && (leftTile.tileShape.equals("Square-Half")
                || leftTile.tileShape.equals("Ramp-Right-Half-Bottom")
                || leftTile.tileShape.equals("Ramp-Left-Half-Top")))
                
                && player.hitBoxArea.y < getLocation().y + 8
                
                && !(player.middleJustFellInRamp != null && player.middleJustFellInRamp == this)
                && !(player.justFellInRamp != null && player.justFellInRamp == this)
                && !(player.rightJustFellInRamp != null && player.rightJustFellInRamp == this)) {
                    player.hitBoxArea.x = getLocation().x - player.hitBoxArea.width;
                    return this;
                }
            }
            
            else if(movingDir.equals("Left")) {
                Tile bottomRightTile = getTargetTile(screenChunks, 1, -1);
                Tile rightTile = getTargetTile(screenChunks, 1, 0);

                if(isBottomRamp(screenChunks)
                && !(bottomRightTile != null && bottomRightTile.tileShape.equals("Square"))
                && !(rightTile != null && rightTile.tileShape.equals("Ramp-Right"))
                && !(rightTile != null && rightTile.tileShape.equals("Ramp-Right-Half-Bottom"))
                && player.hitBoxArea.y < getLocation().y) {
                    player.hitBoxArea.x = getLocation().x + 16;
                    player.velocity.x = 0;
                    return this;
                }

                else if((int) player.hitBoxArea.y < getLocation().y + (int) (16 * anglePercentLeft)) {
                    player.hitBoxArea.y = getLocation().y + (int) (8 * anglePercentLeft);
                    player.velocity.y = baseFloorSpeed;
                    player.land(this);
                    player.onRamp = null;
                    player.onHalfRampBottom = this;
                    player.onHalfRampTop = null;
                    moveOntoCheck = true;
                    return this;
                }
            }
            
            else if((movingDir.equals("Down") || movingDir.equals("Middle"))
            && player.velocity.y <= 0) {
                Tile leftTile = getTargetTile(screenChunks, -1, 0);
                
                // Left Of Ramp //
                if(player.hitBoxArea.getMiddle().x < getLocation().x) {
                    if(isTopRamp(screenChunks)
                    
                    && !(leftTile != null
                    && leftTile.tileShape.equals("Ramp-Right-Half-Bottom"))
                    
                    && player.hitBoxArea.y <= getLocation().y + 8) {
                        player.hitBoxArea.y = getLocation().y + 8;
                        player.velocity.y = 0;
                        player.land(this);
                        player.onRamp = null;
                        player.onHalfRampBottom = this;
                        player.onHalfRampTop = null;
                        return this;
                    }
                }

                // Right Of Ramp //
                else if(player.hitBoxArea.getMiddle().x >= getLocation().x + 16) {
                    if(isBottomRamp(screenChunks)
                    && player.hitBoxArea.y <= getLocation().y) {
                        player.hitBoxArea.y = getLocation().y;
                        player.velocity.y = 0;
                        player.land(this);
                        player.onRamp = null;
                        player.onHalfRampBottom = this;
                        player.onHalfRampTop = null;
                        return this;
                    }
                }

                // Middle Of Ramp //
                else if(player.hitBoxArea.getMiddle().x >= getLocation().x
                && player.hitBoxArea.getMiddle().x < getLocation().x + 16
                
                && !(!(leftTile != null && leftTile.tileShape.equals("Square-Half"))
                && player.justFellInRamp != null
                && player.justFellInRamp != this
                && player.justFellInRamp.tileShape.contains("Bottom")
                && player.justFellInRamp.getLocation().y == getLocation().y
                && !player.justFellInRamp.isContiguous(screenChunks, this))
                
                && !(!(leftTile != null && leftTile.tileShape.equals("Square-Half"))
                && player.onHalfRampBottom != null
                && player.onHalfRampBottom != this
                && player.onHalfRampBottom.getLocation().y == getLocation().y)) {
                    if(player.hitBoxArea.y <= getLocation().y + (int) (8 * anglePercentLeft)
                    || player.onHalfRampBottom != null) {
                        player.hitBoxArea.y = getLocation().y + (int) (8 * anglePercentLeft);
                        player.velocity.y = baseFloorSpeed;
                        player.land(this);
                        player.onRamp = null;
                        player.onHalfRampBottom = this;
                        player.onHalfRampTop = null;
                        return this;
                    }
                }
            }
        }

        // Ramp-Right-Half-Top //
        else if(tileShape.equals("Ramp-Right-Half-Top")) {
            if(movingDir.equals("Right")) {
                Tile leftTile = getTargetTile(screenChunks, -1, 0);

                if(isBottomRamp(screenChunks)
                && !(leftTile != null && leftTile.tileShape.equals("Square-Half"))
                && !(leftTile != null && leftTile.tileShape.equals("Ramp-Left-Half-Top"))
                && !(leftTile != null && leftTile.tileShape.equals("Ramp-Right-Half-Bottom"))
                && player.hitBoxArea.y < getLocation().y + 8) {
                    player.hitBoxArea.x = getLocation().x - player.hitBoxArea.width;
                    player.velocity.x = 0;
                    return this;
                }

                else if((int) player.hitBoxArea.y < getLocation().y + (int) (16 * anglePercentRight)) {
                    player.hitBoxArea.y = getLocation().y + 8 + (int) (8 * anglePercentRight);
                    player.velocity.y = baseFloorSpeed;
                    player.land(this);
                    player.onRamp = null;
                    player.onHalfRampBottom = null;
                    player.onHalfRampTop = this;
                    moveOntoCheck = true;
                    return this;
                }
            }
            
            else if(movingDir.equals("Left")) {
                Tile rightTile = getTargetTile(screenChunks, 1, 0);

                if(!(rightTile != null
                && (rightTile.tileShape.equals("Square")
                || rightTile.tileShape.equals("Ramp-Left-Half-Top")
                || rightTile.tileShape.equals("Ramp-Left")))
                
                && !(player.middleJustFellInRamp != null && player.middleJustFellInRamp == this)
                && !(player.justFellInRamp != null && player.justFellInRamp == this)
                && !(player.rightJustFellInRamp != null && player.rightJustFellInRamp == this)) {
                    player.hitBoxArea.x = getLocation().x + 16;
                    return this;
                }
            }
            
            else if((movingDir.equals("Down") || movingDir.equals("Middle"))
            && player.velocity.y <= 0) {
                Tile rightTile = getTargetTile(screenChunks, 1, 0);
                
                // Left Of Ramp //
                if(player.hitBoxArea.getMiddle().x < getLocation().x) {
                    if(isBottomRamp(screenChunks)
                    && player.hitBoxArea.y <= getLocation().y + 8) {
                        player.hitBoxArea.y = getLocation().y + 8;
                        player.velocity.y = 0;
                        player.land(this);
                        player.onRamp = null;
                        player.onHalfRampBottom = null;
                        player.onHalfRampTop = this;
                        return this;
                    }
                }

                // Right Of Ramp //
                else if(player.hitBoxArea.getMiddle().x >= getLocation().x + 16) {
                    if(isTopRamp(screenChunks)
                    
                    && !(rightTile != null
                    && (rightTile.tileShape.equals("Ramp-Left")
                    || rightTile.tileShape.equals("Ramp-Left-Half-Top")))) {
                        player.hitBoxArea.y = getLocation().y + 16;
                        player.velocity.y = 0;
                        player.land(this);
                        player.onRamp = null;
                        player.onHalfRampBottom = null;
                        player.onHalfRampTop = this;
                        return this;
                    }
                }
            
                // Middle Of Ramp //
                else if(player.hitBoxArea.getMiddle().x >= getLocation().x
                && player.hitBoxArea.getMiddle().x < getLocation().x + 16
                
                && !(!(rightTile != null && rightTile.tileShape.equals("Square"))
                && player.rightJustFellInRamp != null
                && player.rightJustFellInRamp != this
                && (player.rightJustFellInRamp.tileShape.equals("Ramp-Right")
                || player.rightJustFellInRamp.tileShape.equals("Ramp-Left")
                || player.rightJustFellInRamp.tileShape.contains("Top"))
                && player.rightJustFellInRamp.getLocation().y == getLocation().y
                && !player.rightJustFellInRamp.isContiguous(screenChunks, this))
                
                && !(!(rightTile != null && rightTile.tileShape.equals("Square"))
                && player.onHalfRampTop != null
                && player.onHalfRampTop != this
                && player.onHalfRampTop.getLocation().y == getLocation().y)) {
                    if(player.hitBoxArea.y <= getLocation().y + 8 + (int) (8 * anglePercentRight)
                    || player.onHalfRampTop != null) {
                        player.hitBoxArea.y = getLocation().y + 8 + (int) (8 * anglePercentRight);
                        player.velocity.y = baseFloorSpeed;
                        player.land(this);
                        player.onRamp = null;
                        player.onHalfRampBottom = null;
                        player.onHalfRampTop = this;
                        return this;
                    }
                }
            }
        }

        // Ramp-Left-Half-Top //
        else if(tileShape.equals("Ramp-Left-Half-Top")) {
            if(movingDir.equals("Right")) {
                Tile leftTile = getTargetTile(screenChunks, -1, 0);

                if(!(leftTile != null
                && (leftTile.tileShape.equals("Square")
                || leftTile.tileShape.equals("Ramp-Right-Half-Top")
                || leftTile.tileShape.equals("Ramp-Right")))
                
                && !(player.middleJustFellInRamp != null && player.middleJustFellInRamp == this)
                && !(player.justFellInRamp != null && player.justFellInRamp == this)
                && !(player.rightJustFellInRamp != null && player.rightJustFellInRamp == this)) {
                    player.hitBoxArea.x = getLocation().x - player.hitBoxArea.width;
                    return this;
                }
            }
            
            else if(movingDir.equals("Left")) {
                Tile rightTile = getTargetTile(screenChunks, 1, 0);

                if(isBottomRamp(screenChunks)
                && !(rightTile != null && rightTile.tileShape.equals("Square-Half"))
                && !(rightTile != null && rightTile.tileShape.equals("Ramp-Right-Half-Top"))
                && !(rightTile != null && rightTile.tileShape.equals("Ramp-Left-Half-Bottom"))
                && player.hitBoxArea.y < getLocation().y + 8) {
                    player.hitBoxArea.x = getLocation().x + 16;
                    player.velocity.x = 0;
                    return this;
                }

                else if((int) player.hitBoxArea.y < getLocation().y + (int) (16 * anglePercentLeft)) {
                    player.hitBoxArea.y = getLocation().y + 8 + (int) (8 * anglePercentLeft);
                    player.velocity.y = baseFloorSpeed;
                    player.land(this);
                    player.onRamp = null;
                    player.onHalfRampBottom = null;
                    player.onHalfRampTop = this;
                    moveOntoCheck = true;
                    return this;
                }
            }
            
            else if((movingDir.equals("Down") || movingDir.equals("Middle"))
            && player.velocity.y <= 0) {
                Tile leftTile = getTargetTile(screenChunks, -1, 0);
                
                // Left Of Ramp //
                if(player.hitBoxArea.getMiddle().x < getLocation().x) {
                    if(isTopRamp(screenChunks)
                    
                    && !(leftTile != null
                    && (leftTile.tileShape.equals("Ramp-Right")
                    || leftTile.tileShape.equals("Ramp-Right-Half-Top")))) {
                        player.hitBoxArea.y = getLocation().y + 16;
                        player.velocity.y = 0;
                        player.land(this);
                        player.onRamp = null;
                        player.onHalfRampBottom = null;
                        player.onHalfRampTop = this;
                        return this;
                    }
                }

                // Right Of Ramp //
                else if(player.hitBoxArea.getMiddle().x >= getLocation().x + 16) {
                    if(isBottomRamp(screenChunks)
                    && player.hitBoxArea.y <= getLocation().y + 8) {
                        player.hitBoxArea.y = getLocation().y + 8;
                        player.velocity.y = 0;
                        player.land(this);
                        player.onRamp = null;
                        player.onHalfRampBottom = null;
                        player.onHalfRampTop = this;
                        return this;
                    }
                }
            
                // Middle Of Ramp //
                else if(player.hitBoxArea.getMiddle().x >= getLocation().x
                && player.hitBoxArea.getMiddle().x < getLocation().x + 16
                
                && !(!(leftTile != null && leftTile.tileShape.equals("Square"))
                && player.justFellInRamp != null
                && player.justFellInRamp != this
                && (player.justFellInRamp.tileShape.equals("Ramp-Left")
                || player.justFellInRamp.tileShape.equals("Ramp-Right")
                || player.justFellInRamp.tileShape.contains("Top"))
                && player.justFellInRamp.getLocation().y == getLocation().y
                && !player.justFellInRamp.isContiguous(screenChunks, this))
                
                && !(!(leftTile != null && leftTile.tileShape.equals("Square"))
                && player.onHalfRampTop != null
                && player.onHalfRampTop != this
                && player.onHalfRampTop.getLocation().y == getLocation().y)) {
                    if(player.hitBoxArea.y <= getLocation().y + 8 + (int) (8 * anglePercentLeft)
                    || player.onHalfRampTop != null) {
                        player.hitBoxArea.y = getLocation().y + 8 + (int) (8 * anglePercentLeft);
                        player.velocity.y = baseFloorSpeed;
                        player.land(this);
                        player.onRamp = null;
                        player.onHalfRampBottom = null;
                        player.onHalfRampTop = this;
                        return this;
                    }
                }
            }
        }

        // Ceiling-Ramp-Right //
        else if(tileShape.equals("Ceiling-Ramp-Right")) {
            if(movingDir.equals("Right")) {
            }
            
            else if(movingDir.equals("Left")) {
            }
            
            else if(movingDir.equals("Down") || movingDir.equals("Middle")) {
            }
        }

        // Ceiling-Ramp-Left //
        else if(tileShape.equals("Ceiling-Ramp-Left")) {
            if(movingDir.equals("Right")) {
            }
            
            else if(movingDir.equals("Left")) {
            }
            
            else if(movingDir.equals("Down") || movingDir.equals("Middle")) {
            }
        }

        return null;
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

        if(tileShape.contains("Half-Top")) {
            if(tileShape.contains("Right")) {
                targetTile = getTargetTile(screenChunks, -1, 0);
            } else {
                targetTile = getTargetTile(screenChunks, 1, 0);
            }

            if(targetTile != null
            && targetTile.tileShape.contains("Half-Bottom")) {
                return false;
            }
        }
        
        else {
            String targetRampDirection = "";
            if(tileShape.contains("Right")) {
                targetTile = getTargetTile(screenChunks, -1, -1);
                targetRampDirection = "Right";
            } else {
                targetTile = getTargetTile(screenChunks, 1, -1);
                targetRampDirection = "Left";
            }
    
            if(targetTile != null
            && targetTile.tileShape.contains("Ramp")
            && targetTile.tileShape.contains(targetRampDirection)
            && !(targetTile.tileShape.contains("Half-Bottom"))) {
                return false;
            }
        }

        return true;
    }

    public boolean isTopRamp(ScreenChunk[][] screenChunks) {
        Tile targetTile = null;

        if(tileShape.contains("Half-Bottom")) {
            if(tileShape.contains("Right")) {
                targetTile = getTargetTile(screenChunks, 1, 0);
            } else {
                targetTile = getTargetTile(screenChunks, -1, 0);
            }

            if(targetTile != null
            && targetTile.tileShape.contains("Half-Top")) {
                return false;
            }
        }

        else {
            String targetRampDirection = "";
            if(tileShape.contains("Right")) {
                targetTile = getTargetTile(screenChunks, 1, 1);
                targetRampDirection = "Right";
            } else {
                targetTile = getTargetTile(screenChunks, -1, 1);
                targetRampDirection = "Left";
            }
    
            if(targetTile != null
            && targetTile.tileShape.contains("Ramp")
            && targetTile.tileShape.contains(targetRampDirection)
            && !(targetTile.tileShape.contains("Half-Top"))) {
                return false;
            }
        }

        return true;
    }

    public boolean isContiguous(ScreenChunk[][] screenChunks, Tile targetTile) {
        Tile currentTile = this;
        Tile endTile = targetTile;
        if(targetTile.getLocation().x < getLocation().x) {
            currentTile = targetTile;
            endTile = this;
        }

        int tileCount = (endTile.getLocation().y - currentTile.getLocation().y) / 16;
        for(int i = 0; i < tileCount; i++) {
            if(isEmptyTile(screenChunks, getLocation().x + 16, getLocation().y)) {
                return false;
            } else {
                Tile nextTile = getTargetTile(screenChunks, 1, 0);

                if((currentTile == null
                && nextTile != null
                && !(nextTile.tileShape.equals("Ramp-Right")
                || nextTile.tileShape.equals("Ramp-Right-Half-Bottom")))
                    
                || (currentTile.tileShape.equals("Square")
                && (nextTile == null
                || !(nextTile.tileShape.equals("Square")
                || nextTile.tileShape.equals("Ramp-Left")
                || nextTile.tileShape.equals("Ramp-Left-Half-Top"))))

                || (currentTile.tileShape.equals("Square-Half")
                && (nextTile == null
                || !(nextTile.tileShape.equals("Square-Half")
                || nextTile.tileShape.equals("Ramp-Right-Half-Top")
                || nextTile.tileShape.equals("Ramp-Left-Half-Bottom"))))
                    
                || (currentTile.tileShape.equals("Ramp-Right")
                && (nextTile == null
                || !(nextTile.tileShape.equals("Square")
                || nextTile.tileShape.equals("Ramp-Left")
                || nextTile.tileShape.equals("Ramp-Left-Half-Top"))))
                
                || (currentTile.tileShape.equals("Ramp-Left")
                && nextTile != null
                && !(nextTile.tileShape.equals("Ramp-Right")
                || nextTile.tileShape.equals("Ramp-Right-Half-Bottom")))
                
                || (currentTile.tileShape.equals("Ramp-Right-Half-Bottom")
                && (nextTile == null
                || !(nextTile.tileShape.equals("Square-Half")
                || nextTile.tileShape.equals("Ramp-Right-Half-Top")
                || nextTile.tileShape.equals("Ramp-Left-Half-Bottom"))))
                
                || (currentTile.tileShape.equals("Ramp-Left-Half-Bottom")
                && nextTile != null
                && !(nextTile.tileShape.equals("Ramp-Right")
                || nextTile.tileShape.equals("Ramp-Right-Half-Bottom")))
                
                || (currentTile.tileShape.equals("Ramp-Right-Half-Top")
                && (nextTile == null
                || !(nextTile.tileShape.equals("Square")
                || nextTile.tileShape.equals("Ramp-Left")
                || nextTile.tileShape.equals("Ramp-Left-Half-Top"))))
                
                || (currentTile.tileShape.equals("Ramp-Left-Half-Top")
                && nextTile != null
                && !(nextTile.tileShape.equals("Square-Half")
                || nextTile.tileShape.equals("Ramp-Left-Half-Bottom")
                || nextTile.tileShape.equals("Ramp-Right-Half-Top")))) {
                    return false;
                }
            }

            currentTile = getTargetTile(screenChunks, 1, 0);
        }

        return true;
    }
}
