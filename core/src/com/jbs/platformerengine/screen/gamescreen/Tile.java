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
        System.out.println(tileShape + " " + movingDir + " " + locationXIndex + " " + locationYIndex + " " + player.onHalfRampBottom + " " + player.onHalfRampTop);

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
                if(!player.justFellInRamp) {
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
                && !player.justFellInRamp) {
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
                if(player.hitBoxArea.getMiddle().x >= getLocation().x + 16
                 
                && !(player.justFellInRamp
                || (player.onRamp != null && player.onRamp == this))) {
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
    
                    if(player.velocity.y <= 0
                    && (player.hitBoxArea.y <= getLocation().y + (int) (16 * anglePercent)
                    || (player.onRamp != null || player.onHalfRampBottom != null))) {
                        if(isBottomRamp(screenChunks)
                        && player.hitBoxArea.getMiddle().x <= getLocation().x) {
                            player.hitBoxArea.y = getLocation().y;
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
                && !player.justFellInRamp) {
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
                
            }
            
            else if(movingDir.equals("Left")) {

            }
            
            else if(movingDir.equals("Middle") || movingDir.equals("Down")) {

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
                 
                && !(player.justFellInRamp
                || (player.onHalfRampBottom != null && player.onHalfRampBottom == this))) {
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
                && !player.justFellInRamp) {
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
                 
                && !(player.justFellInRamp
                || (player.onHalfRampTop != null && player.onHalfRampTop == this))) {
                    player.hitBoxArea.x = getLocation().x + 16;
                    player.velocity.x = 0;
                    return true;
                }
            }
            
            else if(movingDir.equals("Middle") || movingDir.equals("Down")) {
                if(locationYIndex == 0
                && (player.hitBoxArea.getMiddle().x >= getLocation().x
                || (player.onHalfRampTop != null || player.onRamp != null))) {
                    float locationDiff = player.hitBoxArea.getMiddle().x - getLocation().x;
                    float anglePercent = locationDiff / 16;
                    if(anglePercent > 1) {
                        anglePercent = 1.0f;
                    }
    
                    Tile leftTile = getTargetTile(screenChunks, -1, 0);
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
                && !player.justFellInRamp) {
                    player.hitBoxArea.y = getLocation().y + 8;
                    player.velocity.y = 0;
                    player.land(this);
                    return true;
                }
            }
        }

        // Ramp-Left-Half-Bottom //
        else if(tileShape.equals("Ramp-Left-Half-Bottom")) {

        }

        // Ramp-Left-Half-Top //
        else if(tileShape.equals("Ramp-Left-Half-Top")) {

        }

        // Ceiling-Ramp-Right //
        else if(tileShape.equals("Ceiling-Ramp-Right")) {

        }

        // Ceiling-Ramp-Left //
        else if(tileShape.equals("Ceiling-Ramp-Left")) {

        }

        return false;
    }

    public boolean collisionCheckOld(Player player, String movingDir, int locationXIndex, int locationYIndex) {
        // Point tileLocation = new Point(((chunkX * 80) + tileX) * 16, ((chunkY * 48) + tileY) * 16);
        // System.out.println(tileShape + " " + movingDir + " " + locationXIndex + " " + locationYIndex + " " + player.onRamp + " " + player.onHalfRampBottom + " " + player.onHalfRampTop + " " + player.inWallOnRamp + " " + player.onRampLastFrame);

        // // Square //
        // if(tileShape.equals("Square")) {
        //     if(movingDir.equals("Right")) {
        //         if(player.hitBoxArea.width < 32 && locationYIndex == 0 && (player.onRamp || player.onHalfRampTop) && !player.falling) {
        //             player.inWallOnRamp = true;
        //         } else if((locationYIndex == 0 && (player.largeHitBoxInRampRight || player.largeHitBoxInHalfRampRightTop))
        //         || (locationYIndex <= 1 && player.largeHitBoxInRampRight)) {
        //             // Do Nothing
        //         } else {
        //             player.hitBoxArea.x = tileLocation.x - player.hitBoxArea.width;
        //             player.velocity.x = 0;
        //             return true;
        //         }
        //     } else if(movingDir.equals("Left")) {
        //         if(player.hitBoxArea.width < 32 && locationYIndex == 0 && (player.onRamp || player.onHalfRampTop) && !player.falling) {
        //             player.inWallOnRamp = true;
        //         } else if((locationYIndex == 0 && (player.largeHitBoxInRampLeft || player.largeHitBoxInHalfRampLeftTop))
        //         || (locationYIndex <= 1 && player.largeHitBoxInRampLeft)) {
        //             // Do Nothing
        //         } else {
        //             player.hitBoxArea.x = tileLocation.x + 16;
        //             player.velocity.x = 0;
        //             return true;
        //         }
        //     } else if(movingDir.equals("Down")) {
        //         if(player.hitBoxArea.width >= 32 && locationYIndex <= 1
        //         && (((player.largeHitBoxInRampRight || player.largeHitBoxInHalfRampRightTop) && tileLocation.x > player.hitBoxArea.getMiddle().x)
        //         || ((player.largeHitBoxInRampLeft || player.largeHitBoxInHalfRampLeftTop) && tileLocation.x + 16 < player.hitBoxArea.getMiddle().x))) {
        //             // Do Nothing
        //         } else if(!(locationXIndex == 2 && (player.onRamp || player.onHalfRampTop))) {
        //             player.hitBoxArea.y = tileLocation.y + 16;
        //             player.velocity.y = 0;
        //             player.land(this);

        //             if(!player.justSteppedOnRamp) {
        //                 player.onRamp = false;
        //                 player.onHalfRampBottom = false;
        //                 player.onHalfRampTop = false;
        //             }

        //             return true;
        //         }
        //     } else if(movingDir.equals("Up")) {
        //         player.hitBoxArea.y = tileLocation.y - player.hitBoxArea.height;
        //         player.velocity.y = 0;
        //         player.hitCeiling();
        //         return true;
        //     }
        // }

        // // Square-Half //
        // else if(tileShape.equals("Square-Half")) {
        //     if(movingDir.equals("Right")) {
        //         if(locationYIndex == 0 && player.onHalfRampBottom && !player.falling) {
        //             player.inWallOnRamp = true;
        //         } else if(locationYIndex <= 1 && player.largeHitBoxInHalfRampRightBottom) {
        //             // Do Nothing
        //         } else {
        //             if(locationYIndex > 0
        //             || (locationYIndex == 0 && player.hitBoxArea.y < tileLocation.y + 8)) {
        //                 player.hitBoxArea.x = tileLocation.x - player.hitBoxArea.width;
        //                 player.velocity.x = 0;
        //                 return true;
        //             }
        //         }
        //     } else if(movingDir.equals("Left")) {
        //         if(locationYIndex == 0 && player.onHalfRampBottom && !player.falling) {
        //             player.inWallOnRamp = true;
        //         } else if(locationYIndex <= 1 && player.largeHitBoxInHalfRampLeftBottom) {
        //             // Do Nothing
        //         } else {
        //             if(locationYIndex > 0
        //             || (locationYIndex == 0 && player.hitBoxArea.y < tileLocation.y + 8)) {
        //                 player.hitBoxArea.x = tileLocation.x + 16;
        //                 player.velocity.x = 0;
        //                 return true;
        //             }
        //         }
        //     } else if(movingDir.equals("Down")) {
        //         if(((player.largeHitBoxInHalfRampRightBottom || player.largeHitBoxInHalfRampLeftBottom)
        //         && (player.hitBoxArea.getMiddle().x >= getLocation().x + 16 || player.hitBoxArea.getMiddle().x < getLocation().x))) {
        //             // Do Nothing
        //         } else if(player.hitBoxArea.y < tileLocation.y + 8) {
        //             player.hitBoxArea.y = tileLocation.y + 8;
        //             player.velocity.y = 0;
        //             player.land(this);

        //             if(locationXIndex == 0) {
        //                 player.onRamp = false;
        //             }
                    
        //             return true;
        //         }
        //     } else if(movingDir.equals("Up")) {
        //         player.hitBoxArea.y = tileLocation.y - player.hitBoxArea.height;
        //         player.velocity.y = 0;
        //         player.hitCeiling();
        //         return true;
        //     }
        // }

        // // Ramp-Right //
        // else if(tileShape.equals("Ramp-Right")) {
        //     if(movingDir.equals("Right")) {
        //         if(locationYIndex > 0
        //         && !player.onRamp && !player.onHalfRampTop
        //         && !(locationYIndex == 0 && player.largeHitBoxInHalfRampRightTop)
        //         && !(locationYIndex == 0 && player.largeHitBoxInRampRight)) {
        //             player.hitBoxArea.x = tileLocation.x - player.hitBoxArea.width;
        //             player.velocity.x = 0;
        //             return true;
        //         }
        //     } else if(movingDir.equals("Left")) {
        //         if(player.hitBoxArea.getMiddle().x >= tileLocation.x + 16
        //         && !(locationYIndex == 0 && player.onRamp)
        //         && !(locationYIndex == 0 && player.largeHitBoxInRampLeft) && !player.largeHitBoxInHalfRampLeftTop) {
        //             player.hitBoxArea.x = tileLocation.x + 16;
        //             player.velocity.x = 0;
        //             return true;
        //         }
        //     } else if(movingDir.equals("Middle")) {
        //         if(locationYIndex == 0 && (!player.inWallOnRamp || player.hitBoxArea.width > 26) && player.velocity.y <= 0) {
        //             float locationDiff = player.hitBoxArea.getMiddle().x - tileLocation.x;
        //             float anglePercent = locationDiff / 16;
        //             if(player.hitBoxArea.y <= tileLocation.y + (int) (16 * anglePercent)
        //             || player.onRampLastFrame) {
        //                 player.hitBoxArea.y = tileLocation.y + (int) (16 * anglePercent);
        //                 player.onRamp = true;
        //                 player.justSteppedOnRamp = true;
        //                 return true;
        //             }
        //         }
        //     } else if(movingDir.equals("Down")) {
        //         if(locationXIndex == 0) {
        //             if(player.velocity.y <= 0) {
        //                 if(!player.largeHitBoxInRampRight) {
        //                     player.onRamp = true;
        //                     player.justSteppedOnRamp = true;
        //                 }
                        
        //                 float locationDiff = player.hitBoxArea.getMiddle().x - tileLocation.x;
        //                 float anglePercent = locationDiff / 16;
        //                 if((player.hitBoxArea.y <= tileLocation.y + (int) (16 * anglePercent)
        //                 || player.onRampLastFrame)
        //                 && !player.largeHitBoxInRampRight) {
        //                     player.hitBoxArea.y = tileLocation.y + (int) (16 * anglePercent);
        //                     player.velocity.y = 0;
        //                     player.land(this);
        //                     return true;
        //                 }
        //             }
        //         } else if(locationXIndex == 1 || (player.hitBoxArea.width >= 32 && locationYIndex == 0 && locationXIndex > 2)) {
        //             if(player.hitBoxArea.getMiddle().x >= tileLocation.x + 16
        //             && !player.largeHitBoxInRampLeft) {
        //                 player.hitBoxArea.y = tileLocation.y + 16;
        //                 player.velocity.y = 0;
        //                 player.onRamp = false;
        //                 player.land(this);
        //                 return true;
        //             }
        //         } else if(locationXIndex == 2 || (player.hitBoxArea.width >= 32 && locationYIndex == 1 && locationXIndex > 2)) {
        //             if(player.hitBoxArea.getMiddle().x < tileLocation.x
        //             && player.hitBoxArea.y <= tileLocation.y
        //             && !(player.hitBoxArea.width >= 32 && (player.onRamp || (player.largeHitBoxInRampRight && locationYIndex == 0)))) {
        //                 player.hitBoxArea.y = tileLocation.y;
        //                 player.velocity.y = 0;
        //                 player.onRamp = false;
        //                 player.land(this);
        //                 return true;
        //             }
        //         }
        //     } else if(movingDir.equals("Up")) {
        //         player.hitBoxArea.y = tileLocation.y - player.hitBoxArea.height;
        //         player.velocity.y = 0;
        //         player.hitCeiling();
        //         return true;
        //     }
        // }

        // // Ramp-Left //
        // else if(tileShape.equals("Ramp-Left")) {
        //     if(movingDir.equals("Right")) {
        //         if(player.hitBoxArea.getMiddle().x < tileLocation.x
        //         && !(locationYIndex == 0 && player.onRamp)
        //         && !(locationYIndex == 0 && player.largeHitBoxInRampRight) && !player.largeHitBoxInHalfRampRightTop) {
        //             player.hitBoxArea.x = tileLocation.x - player.hitBoxArea.width;
        //             player.velocity.x = 0;
        //             return true;
        //         }
        //     } else if(movingDir.equals("Left")) {
        //         if(locationYIndex > 0
        //         && !player.onRamp && !player.onHalfRampTop
        //         && !(locationYIndex == 0 && (player.largeHitBoxInHalfRampLeftTop || player.largeHitBoxInRampLeft))) {
        //             player.hitBoxArea.x = tileLocation.x + 16;
        //             player.velocity.x = 0;
        //             return true;
        //         }
        //     } else if(movingDir.equals("Middle")) {
        //         if(locationYIndex == 0 && (!player.inWallOnRamp || player.hitBoxArea.width > 26) && player.velocity.y <= 0) {
        //             float locationDiff = 16 - (player.hitBoxArea.getMiddle().x - tileLocation.x);
        //             float anglePercent = locationDiff / 16;
        //             if(player.hitBoxArea.y <= tileLocation.y + (int) (16 * anglePercent)
        //             || player.onRampLastFrame) {
        //                 player.hitBoxArea.y = tileLocation.y + (int) (16 * anglePercent);
        //                 player.onRamp = true;
        //                 return true;
        //             }
        //         }
        //     } else if(movingDir.equals("Down")) {
        //         if(locationXIndex == 0) {
        //             if(player.velocity.y <= 0) {
        //                 if(!player.largeHitBoxInRampLeft) {
        //                     player.onRamp = true;
        //                     player.justSteppedOnRamp = true;
        //                 }

        //                 float locationDiff = 16 - (player.hitBoxArea.getMiddle().x - tileLocation.x);
        //                 float anglePercent = locationDiff / 16;
        //                 if((player.hitBoxArea.y <= tileLocation.y + (int) (16 * anglePercent)
        //                 || player.onRampLastFrame)
        //                 && !player.largeHitBoxInRampLeft) {
        //                     player.hitBoxArea.y = tileLocation.y + (int) (16 * anglePercent);
        //                     player.velocity.y = 0;
        //                     player.land(this);
        //                     return true;
        //                 }
        //             }
        //         } else if(locationXIndex == 1 || (player.hitBoxArea.width >= 32 && locationYIndex == 1 && locationXIndex > 2)) {
        //             if(player.hitBoxArea.getMiddle().x >= tileLocation.x + 16
        //             && player.hitBoxArea.y < tileLocation.y
        //             && !(player.hitBoxArea.width >= 32 && (player.onRamp || (player.largeHitBoxInRampRight && locationYIndex == 0)))) {
        //                 player.hitBoxArea.y = tileLocation.y;
        //                 player.velocity.y = 0;
        //                 player.onRamp = false;
        //                 player.land(this);
        //                 return true;
        //             }
        //         } else if(locationXIndex == 2 || (player.hitBoxArea.width >= 32 && locationYIndex == 0 && locationXIndex > 2)) {
        //             if(player.hitBoxArea.getMiddle().x < tileLocation.x
        //             && !player.largeHitBoxInRampRight) {
        //                 player.hitBoxArea.y = tileLocation.y + 16;
        //                 player.velocity.y = 0;
        //                 player.onRamp = false;
        //                 player.land(this);
        //                 return true;
        //             }
        //         }
        //     } else if(movingDir.equals("Up")) {
        //         player.hitBoxArea.y = tileLocation.y - player.hitBoxArea.height;
        //         player.velocity.y = 0;
        //         player.hitCeiling();
        //         return true;
        //     }
        // }

        // // Ramp-Right-Half-Bottom //
        // else if(tileShape.equals("Ramp-Right-Half-Bottom")) {
        //     if(movingDir.equals("Right")) {
        //         if(locationYIndex > 0
        //         && !player.onRamp
        //         && !(locationYIndex == 0 && player.largeHitBoxInHalfRampRightTop)
        //         && !(locationYIndex == 0 && player.largeHitBoxInRampRight)) {
        //             player.hitBoxArea.x = tileLocation.x - player.hitBoxArea.width;
        //             player.velocity.x = 0;
        //             return true;
        //         }
        //     } else if(movingDir.equals("Left")) {
        //         if(player.hitBoxArea.getMiddle().x >= tileLocation.x + 16
        //         && player.hitBoxArea.y < tileLocation.y + 8
        //         && !(locationYIndex == 0 && player.onHalfRampBottom)
        //         && !(locationYIndex == 0 && player.largeHitBoxInHalfRampLeftBottom)) {
        //             player.hitBoxArea.x = tileLocation.x + 16;
        //             player.velocity.x = 0;
        //             return true;
        //         }
        //     } else if(movingDir.equals("Middle")) {
        //         if(locationYIndex == 0 && player.velocity.y <= 0) {
        //             float locationDiff = player.hitBoxArea.getMiddle().x - tileLocation.x;
        //             float anglePercent = locationDiff / 16;
        //             if(player.hitBoxArea.y <= tileLocation.y + (int) (8 * anglePercent)
        //             || player.onRampLastFrame) {
        //                 player.hitBoxArea.y = tileLocation.y + (int) (8 * anglePercent);
        //                 player.onHalfRampBottom = true;
        //                 player.onHalfRampTop = false;
        //                 player.onRamp = false;
        //                 player.justSteppedOnRamp = true;
        //                 return true;
        //             }
        //         }
        //     } else if(movingDir.equals("Down")) {
        //         if(locationXIndex == 0) {
        //             if(player.velocity.y <= 0 && !player.onRamp) {
        //                 player.onHalfRampBottom = true;
        //                 player.justSteppedOnRamp = true;
                        
        //                 float locationDiff = player.hitBoxArea.getMiddle().x - tileLocation.x;
        //                 float anglePercent = locationDiff / 16;
        //                 if(player.hitBoxArea.y <= tileLocation.y + (int) (8 * anglePercent)
        //                 || player.onRampLastFrame) {
        //                     player.hitBoxArea.y = tileLocation.y + (int) (8 * anglePercent);
        //                     player.velocity.y = 0;
        //                     player.land(this);
        //                     return true;
        //                 }
        //             }
        //         } else if(locationXIndex == 1 || (player.hitBoxArea.width >= 32 && locationXIndex > 2 && player.hitBoxArea.getMiddle().x >= getLocation().x + 16)) {
        //             if(player.hitBoxArea.getMiddle().x >= tileLocation.x + 16
        //             && player.hitBoxArea.y <= tileLocation.y + 8
        //             && !player.largeHitBoxInHalfRampLeftBottom) {
        //                 player.hitBoxArea.y = tileLocation.y + 8;
        //                 player.velocity.y = 0;
        //                 player.onHalfRampBottom = false;
        //                 player.land(this);
        //                 return true;
        //             }
        //         } else if(locationXIndex == 2 || (player.hitBoxArea.width >= 32 && locationXIndex > 2 && player.hitBoxArea.getMiddle().x < getLocation().x)) {
        //             if(player.hitBoxArea.getMiddle().x < tileLocation.x
        //             && player.hitBoxArea.y <= tileLocation.y
        //             && !player.justSteppedOnRamp
        //             && !player.largeHitBoxInRampRight && !player.largeHitBoxInHalfRampRightTop) {
        //                 player.hitBoxArea.y = tileLocation.y;
        //                 player.velocity.y = 0;
        //                 player.onHalfRampBottom = false;
        //                 player.land(this);
        //                 return true;
        //             }
        //         }
        //     } else if(movingDir.equals("Up")) {
        //         player.hitBoxArea.y = tileLocation.y - player.hitBoxArea.height;
        //         player.velocity.y = 0;
        //         player.hitCeiling();
        //         return true;
        //     }
        // }

        // // Ramp-Right-Half-Top //
        // else if(tileShape.equals("Ramp-Right-Half-Top")) {
        //     if(movingDir.equals("Right")) {
        //         if(!player.onHalfRampBottom &&
        //         ((locationYIndex > 0 && !player.onRamp)
        //         || (locationYIndex == 0 && player.hitBoxArea.y < tileLocation.y + 8))
        //         && !(locationYIndex == 0 && player.largeHitBoxInHalfRampRightBottom)) {
        //             player.hitBoxArea.x = tileLocation.x - player.hitBoxArea.width;
        //             player.velocity.x = 0;
        //             return true;
        //         }
        //     } else if(movingDir.equals("Left")) {
        //         if(player.hitBoxArea.getMiddle().x >= tileLocation.x + 16
        //         && !(locationYIndex == 0 && (player.onRamp || player.onHalfRampTop))
        //         && !(locationYIndex == 0 && player.largeHitBoxInHalfRampLeftTop)
        //         && !(locationYIndex == 0 && player.largeHitBoxInRampLeft)) {
        //             player.hitBoxArea.x = tileLocation.x + 16;
        //             player.velocity.x = 0;
        //             return true;
        //         }
        //     } else if(movingDir.equals("Middle")) {
        //         if(locationYIndex == 0 && !player.inWallOnRamp && player.velocity.y <= 0) {
        //             float locationDiff = player.hitBoxArea.getMiddle().x - tileLocation.x;
        //             float anglePercent = locationDiff / 16;
        //             if(player.hitBoxArea.y <= tileLocation.y + 8 + (int) (8 * anglePercent)
        //             || player.onRampLastFrame) {
        //                 player.hitBoxArea.y = tileLocation.y + 8 + (int) (8 * anglePercent);
        //                 player.onHalfRampTop = true;
        //                 player.onHalfRampBottom = false;
        //                 player.onRamp = false;
        //                 player.justSteppedOnRamp = true;
        //                 return true;
        //             }
        //         }
        //     } else if(movingDir.equals("Down")) {
        //         if(locationXIndex == 0) {
        //             if(locationYIndex == 0 && player.velocity.y <= 0) {
        //                 player.onHalfRampTop = true;
        //                 player.justSteppedOnRamp = true;

        //                 float locationDiff = player.hitBoxArea.getMiddle().x - tileLocation.x;
        //                 float anglePercent = locationDiff / 16;
        //                 if(player.hitBoxArea.y <= tileLocation.y + 8 + (int) (8 * anglePercent)
        //                 || player.onRampLastFrame) {
        //                     player.hitBoxArea.y = tileLocation.y + 8 + (int) (8 * anglePercent);
        //                     player.onHalfRampTop = true;
        //                     player.onHalfRampBottom = false;
        //                     player.onRamp = false;
        //                     player.justSteppedOnRamp = true;
        //                     player.land(this);
        //                     return true;
        //                 }
        //             }
        //         } else if(locationXIndex == 1 || (player.hitBoxArea.width >= 32 && locationXIndex > 2 && player.hitBoxArea.getMiddle().x >= getLocation().x + 16)) {
        //             if(player.hitBoxArea.getMiddle().x >= tileLocation.x + 16
        //             && player.hitBoxArea.y <= tileLocation.y + 16
        //             && !player.largeHitBoxInHalfRampLeftTop && !player.largeHitBoxInRampLeft) {
        //                 player.hitBoxArea.y = tileLocation.y + 16;
        //                 player.velocity.y = 0;
        //                 player.onHalfRampBottom = false;
        //                 player.land(this);
        //                 return true;
        //             }
        //         } else if((locationYIndex == 0 && locationXIndex == 2) || (player.hitBoxArea.width >= 32 && locationXIndex > 2 && player.hitBoxArea.getMiddle().x < getLocation().x)) {
        //             if(player.hitBoxArea.getMiddle().x < tileLocation.x
        //             && player.hitBoxArea.y <= tileLocation.y + 8
        //             && !player.justSteppedOnRamp
        //             && !player.largeHitBoxInHalfRampRightBottom) {
        //                 player.hitBoxArea.y = tileLocation.y + 8;
        //                 player.velocity.y = 0;
        //                 player.onHalfRampBottom = false;
        //                 player.land(this);
        //                 return true;
        //             }
        //         }
        //     } else if(movingDir.equals("Up")) {
        //         player.hitBoxArea.y = tileLocation.y - player.hitBoxArea.height;
        //         player.velocity.y = 0;
        //         player.hitCeiling();
        //         return true;
        //     }
        // }

        // // Ramp-Left-Half-Bottom //
        // else if(tileShape.equals("Ramp-Left-Half-Bottom")) {
        //     if(movingDir.equals("Right")) {
        //         if(player.hitBoxArea.getMiddle().x < tileLocation.x
        //         && player.hitBoxArea.y < tileLocation.y + 8
        //         && !(locationYIndex == 0 && player.onHalfRampBottom)
        //         && !(locationYIndex == 0 && player.largeHitBoxInHalfRampRightBottom)) {
        //             player.hitBoxArea.x = tileLocation.x - player.hitBoxArea.width;
        //             player.velocity.x = 0;
        //             return true;
        //         }
        //     } else if(movingDir.equals("Left")) {
        //         if(locationYIndex > 0
        //         && !player.onRamp
        //         && !(locationYIndex == 0 && player.largeHitBoxInHalfRampLeftTop)
        //         && !(locationYIndex == 0 && player.largeHitBoxInRampLeft)) {
        //             player.hitBoxArea.x = tileLocation.x + 16;
        //             player.velocity.x = 0;
        //             return true;
        //         }
        //     } else if(movingDir.equals("Middle")) {
        //         if(locationYIndex == 0 && player.velocity.y <= 0) {
        //             float locationDiff = 16 - (player.hitBoxArea.getMiddle().x - tileLocation.x);
        //             float anglePercent = locationDiff / 16;
        //             if(player.hitBoxArea.y <= tileLocation.y + (int) (8 * anglePercent)
        //             || player.onRampLastFrame) {
        //                 player.hitBoxArea.y = tileLocation.y + (int) (8 * anglePercent);
        //                 player.onHalfRampBottom = true;
        //                 player.onHalfRampTop = false;
        //                 player.onRamp = false;
        //                 player.justSteppedOnRamp = true;
        //                 return true;
        //             }
        //         }
        //     } else if(movingDir.equals("Down")) {
        //         if(locationXIndex == 0) {
        //             if(player.velocity.y <= 0 && !player.onRamp) {
        //                 player.onHalfRampBottom = true;
        //                 player.justSteppedOnRamp = true;
                        
        //                 float locationDiff = 16 - (player.hitBoxArea.getMiddle().x - tileLocation.x);
        //                 float anglePercent = locationDiff / 16;
        //                 if(player.hitBoxArea.y <= tileLocation.y + (int) (8 * anglePercent)
        //                 || player.onRampLastFrame) {
        //                     player.hitBoxArea.y = tileLocation.y + (int) (8 * anglePercent);
        //                     player.velocity.y = 0;
        //                     player.land(this);
        //                     return true;
        //                 }
        //             }
        //         } else if(locationXIndex == 1 || (player.hitBoxArea.width >= 32 && locationXIndex > 2 && player.hitBoxArea.getMiddle().x >= getLocation().x + 16)) {
        //             if(player.hitBoxArea.getMiddle().x >= tileLocation.x + 16
        //             && player.hitBoxArea.y <= tileLocation.y
        //             && !player.justSteppedOnRamp
        //             && !player.largeHitBoxInRampLeft && !player.largeHitBoxInHalfRampLeftTop) {
        //                 player.hitBoxArea.y = tileLocation.y;
        //                 player.velocity.y = 0;
        //                 player.onHalfRampBottom = false;
        //                 player.land(this);
        //                 return true;
        //             }
        //         } else if(locationXIndex == 2 || (player.hitBoxArea.width >= 32 && locationXIndex > 2 && player.hitBoxArea.getMiddle().x < getLocation().x)) {
        //             if(player.hitBoxArea.getMiddle().x < tileLocation.x
        //             && player.hitBoxArea.y <= tileLocation.y + 8
        //             && !player.largeHitBoxInHalfRampRightBottom) {
        //                 player.hitBoxArea.y = tileLocation.y + 8;
        //                 player.velocity.y = 0;
        //                 player.onHalfRampBottom = false;
        //                 player.land(this);
        //                 return true;
        //             }
        //         }
        //     } else if(movingDir.equals("Up")) {
        //         player.hitBoxArea.y = tileLocation.y - player.hitBoxArea.height;
        //         player.velocity.y = 0;
        //         player.hitCeiling();
        //         return true;
        //     }
        // }

        // // Ramp-Left-Half-Top //
        // else if(tileShape.equals("Ramp-Left-Half-Top")) {
        //     if(movingDir.equals("Right")) {
        //         if(player.hitBoxArea.getMiddle().x < tileLocation.x
        //         && !(locationYIndex == 0 && (player.onRamp || player.onHalfRampTop))
        //         && !(locationYIndex == 0 && player.largeHitBoxInHalfRampRightTop)
        //         && !(locationYIndex == 0 && player.largeHitBoxInRampRight)) {
        //             player.hitBoxArea.x = tileLocation.x - player.hitBoxArea.width;
        //             player.velocity.x = 0;
        //             return true;
        //         }
        //     } else if(movingDir.equals("Left")) {
        //         if(!player.onHalfRampBottom &&
        //         ((locationYIndex > 0 && !player.onRamp)
        //         || (locationYIndex == 0 && player.hitBoxArea.y < tileLocation.y + 8))
        //         && !(locationYIndex == 0 && player.largeHitBoxInHalfRampLeftBottom)) {
        //             player.hitBoxArea.x = tileLocation.x + 16;
        //             player.velocity.x = 0;
        //             return true;
        //         }
        //     } else if(movingDir.equals("Middle")) {
        //         if(locationYIndex == 0 && !player.inWallOnRamp && player.velocity.y <= 0) {
        //             float locationDiff = 16 - (player.hitBoxArea.getMiddle().x - tileLocation.x);
        //             float anglePercent = locationDiff / 16;
        //             if(player.hitBoxArea.y <= tileLocation.y + 8 + (int) (8 * anglePercent)
        //             || player.onRampLastFrame) {
        //                 player.hitBoxArea.y = tileLocation.y + 8 + (int) (8 * anglePercent);
        //                 player.onHalfRampTop = true;
        //                 player.onHalfRampBottom = false;
        //                 player.onRamp = false;
        //                 player.justSteppedOnRamp = true;
        //                 return true;
        //             }
        //         }
        //     } else if(movingDir.equals("Down")) {
        //         if(locationXIndex == 0) {
        //             if(locationYIndex == 0 && player.velocity.y <= 0) {
        //                 player.onHalfRampTop = true;
        //                 player.justSteppedOnRamp = true;

        //                 float locationDiff = 16 - (player.hitBoxArea.getMiddle().x - tileLocation.x);
        //                 float anglePercent = locationDiff / 16;
        //                 if(player.hitBoxArea.y <= tileLocation.y + 8 + (int) (8 * anglePercent)
        //                 || player.onRampLastFrame) {
        //                     player.hitBoxArea.y = tileLocation.y + 8 + (int) (8 * anglePercent);
        //                     player.onHalfRampTop = true;
        //                     player.onHalfRampBottom = false;
        //                     player.onRamp = false;
        //                     player.justSteppedOnRamp = true;
        //                     player.land(this);
        //                     return true;
        //                 }
        //             }
        //         } else if(locationXIndex == 1 || (player.hitBoxArea.width >= 32 && locationXIndex > 2 && player.hitBoxArea.getMiddle().x >= getLocation().x + 16)) {
        //             if(player.hitBoxArea.getMiddle().x >= tileLocation.x + 16
        //             && player.hitBoxArea.y <= tileLocation.y + 8
        //             && !player.justSteppedOnRamp
        //             && !player.largeHitBoxInHalfRampLeftBottom) {
        //                 player.hitBoxArea.y = tileLocation.y + 8;
        //                 player.velocity.y = 0;
        //                 player.onHalfRampBottom = false;
        //                 player.land(this);
        //                 return true;
        //             }
        //         } else if((locationXIndex == 0 || locationXIndex == 2) || (player.hitBoxArea.width >= 32 && locationXIndex > 2 && player.hitBoxArea.getMiddle().x < getLocation().x)) {
        //             if(player.hitBoxArea.getMiddle().x < tileLocation.x
        //             && player.hitBoxArea.y <= tileLocation.y + 16
        //             && !player.largeHitBoxInHalfRampRightTop && !player.largeHitBoxInRampRight) {
        //                 player.hitBoxArea.y = tileLocation.y + 16;
        //                 player.velocity.y = 0;
        //                 player.onHalfRampBottom = false;
        //                 player.land(this);
        //                 return true;
        //             }
        //         }
        //     } else if(movingDir.equals("Up")) {
        //         player.hitBoxArea.y = tileLocation.y - player.hitBoxArea.height;
        //         player.velocity.y = 0;
        //         player.hitCeiling();
        //         return true;
        //     }
        // }

        // // Ceiling-Ramp-Right //
        // else if(tileShape.equals("Ceiling-Ramp-Right")) {
        //     if(movingDir.equals("Right")) {

        //     } else if(movingDir.equals("Left")) {
                
        //     } else if(movingDir.equals("Up")) {
                
        //     }
        // }

        // // Ceiling-Ramp-Left //
        // else if(tileShape.equals("Ceiling-Ramp-Left")) {
        //     if(movingDir.equals("Right")) {

        //     } else if(movingDir.equals("Left")) {
                
        //     } else if(movingDir.equals("Up")) {
                
        //     }
        // }

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

    public boolean isBottomRamp(ScreenChunk[][] screenChunks) {
        Tile targetTile = getTargetTile(screenChunks, -1, -1);
        if(targetTile != null) {
            if(targetTile.tileShape.equals("Ramp-Right")
            || targetTile.tileShape.equals("Ramp-Left")
            || targetTile.tileShape.equals("Ramp-Right-Half-Bottom")
            || targetTile.tileShape.equals("Ramp-Left-Half-Bottom")
            || targetTile.tileShape.equals("Ramp-Right-Half-Top")
            || targetTile.tileShape.equals("Ramp-Left-Half-Top")) {
                return false;
            }
        } 

        return true;
    }
}
