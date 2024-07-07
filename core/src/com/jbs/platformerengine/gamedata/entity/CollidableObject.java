package com.jbs.platformerengine.gamedata.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.jbs.platformerengine.gamedata.Point;
import com.jbs.platformerengine.gamedata.PointF;
import com.jbs.platformerengine.gamedata.Rect;
import com.jbs.platformerengine.gamedata.entity.mob.Mob;
import com.jbs.platformerengine.screen.ImageManager;
import com.jbs.platformerengine.screen.animatedobject.AnimatedObject;
import com.jbs.platformerengine.screen.gamescreen.ScreenChunk;
import com.jbs.platformerengine.screen.gamescreen.Tile;

public class CollidableObject extends AnimatedObject {
    public Rect hitBoxArea;
    public boolean displayHitBox;

    public PointF velocity;

    public Tile onRamp;
    public Tile onHalfRampBottom;
    public Tile onHalfRampTop;

    public Tile justFellInRamp;
    public Tile fellInRampLastFrame;
    public Tile middleJustFellInRamp;
    public Tile middleFellInRampLastFrame;
    public Tile rightJustFellInRamp;
    public Tile rightFellInRampLastFrame;
    public Tile justFellInSquareHalf;
    public Tile fellInSquareHalfLastFrame;
    public Tile sideSpeedMiddleTile;
    public Tile sideSpeedMiddleTileLastFrame;

    public CollidableObject(String imageName, ImageManager imageManager, Point location) {
        super(imageName, imageManager);

        hitBoxArea = new Rect(location.x, location.y, 0, 0);
        displayHitBox = false;

        velocity = new PointF(0, 0);

        resetRamps();
    }

    public void updateTileCollisions(ScreenChunk[][] screenChunks, Mob mob) {
        // System.out.println("---New Frame---");

        // Apply Gravity (Or Drop Kick) //
        float gravityLevel = -.7f;
        if(mob.dropKickCheck) {
            velocity.y = -15;
        }
        else if(!mob.flying && velocity.y > mob.maxFallVelocity) {
            if(mob.jumpTimer == mob.jumpTimerMax) {
                velocity.y += gravityLevel;
            } else {
                velocity.y += (gravityLevel / 10.0);
            }
        }
        
        // Update Collisions //
        PointF targetPoint = new PointF(hitBoxArea.x + velocity.x, hitBoxArea.y + velocity.y);
        PointF startPoint = new PointF(hitBoxArea.x, hitBoxArea.y);
        
        float xDistance = targetPoint.x - startPoint.x;
        float yDistance = targetPoint.y - startPoint.y;
        float slope = yDistance / xDistance;

        // Get UpdateCount, UpdateXMove, & UpdateYMove //
        int updateCount = -1;
        float updateXMove = -1;
        float updateYMove = -1;
        Tile xHitWallCheck = null;
        Tile yHitWallCheck = null;
        boolean hitLevelEdge = false;

        // Longer Left & Right //
        int updateDistance = 16;
        if(Math.abs(xDistance) >= Math.abs(yDistance)) {
            updateCount = ((int) Math.abs(xDistance) / updateDistance);
            if(xDistance % updateDistance != 0) {
                updateCount += 1;
            }

            if((int) Math.abs(xDistance) < updateDistance) {
                updateXMove = xDistance;
                updateYMove = slope * (int) xDistance;
            } else {
                updateXMove = updateDistance;
                updateYMove = Math.abs(slope) * updateDistance;

                if(xDistance < 0) {
                    updateXMove *= -1;
                }
                if(yDistance < 0) {
                    updateYMove *= -1;
                }
            }
        }
        
        // Longer Top & Bottom //
        else {
            updateCount = ((int) Math.abs(yDistance) / updateDistance);
            if(yDistance % updateDistance != 0) {
                updateCount += 1;
            }

            slope = xDistance / yDistance;

            if((int) Math.abs(yDistance) < updateDistance) {
                updateYMove = yDistance;
                updateXMove = slope * (int) yDistance;
            } else {
                updateYMove = updateDistance;
                updateXMove = Math.abs(slope) * updateDistance;

                if(yDistance < 0) {
                    updateYMove *= -1;
                }
                if(xDistance < 0) {
                    updateXMove *= -1;
                }
            }
        }
        
        if(velocity.x == 0) {
            hitBoxArea.x = (int) hitBoxArea.x;
        }
        if(velocity.y == 0) {
            hitBoxArea.y = (int) hitBoxArea.y;
        }

        // Collision Checks //
        for(int i = 0; i < updateCount; i++) {
            
            // Update X Location //
            if(i == updateCount - 1) {
                if(xHitWallCheck == null) {
                    hitBoxArea.x = startPoint.x + xDistance;
                }
            } else {
                if(xHitWallCheck == null) {
                    hitBoxArea.x += updateXMove;
                }
            }

            // Get Side Speed Middle Tile //
            sideSpeedMiddleTileLastFrame = sideSpeedMiddleTile;
            sideSpeedMiddleTile = null;
            Tile targetSpeedMiddleTile = ScreenChunk.getTile(screenChunks, (int) hitBoxArea.getMiddle().x, (int) hitBoxArea.y);
            if(targetSpeedMiddleTile != null
            && targetSpeedMiddleTile.tileShape.contains("Ramp")) {
                sideSpeedMiddleTile = targetSpeedMiddleTile;
            }
            
            // Move Player Left/Right One Tile (SideSpeedMiddleTileLastFrame Check) //
            if(sideSpeedMiddleTileLastFrame != null
            && sideSpeedMiddleTileLastFrame.getLocation().y / 16 == (int) hitBoxArea.y / 16
            && onRamp == null
            && onHalfRampBottom == null
            && onHalfRampTop == null) {
                if(velocity.x < 0
                && sideSpeedMiddleTileLastFrame.tileShape.contains("Left")
                && (sideSpeedMiddleTileLastFrame.getLocation().x / 16) > ((int) hitBoxArea.x / 16)) {
                    if(sideSpeedMiddleTile == null
                    || sideSpeedMiddleTile.getLocation().x < sideSpeedMiddleTileLastFrame.getLocation().x) {
                        hitBoxArea.x = sideSpeedMiddleTileLastFrame.getLocation().x - (hitBoxArea.width / 2);
                    }
                }

                else if(velocity.x > 0
                && sideSpeedMiddleTileLastFrame.tileShape.contains("Right")
                && (sideSpeedMiddleTileLastFrame.getLocation().x / 16) < ((int) hitBoxArea.x / 16)) {
                    if(sideSpeedMiddleTile == null
                    || sideSpeedMiddleTile.getLocation().x > sideSpeedMiddleTileLastFrame.getLocation().x) {
                        hitBoxArea.x = sideSpeedMiddleTileLastFrame.getLocation().x + 16 - (hitBoxArea.width / 2) - 1;
                    }
                }
            }

            // X Collision Check //
            if(xHitWallCheck == null) {
                int yCount = (hitBoxArea.height / 16) + 1;
                if(hitBoxArea.height % 16 != 0) {
                    yCount += 1;
                }

                String movingDirX = "";
                if(updateXMove < 0) {
                    movingDirX = "Left";
                } else if(updateXMove > 0) {
                    movingDirX = "Right";
                }

                // Screen Boundary Collision Detection //
                if(hitBoxArea.x < 0) {
                    hitBoxArea.x = 0;
                    hitLevelEdge = true;
                } else if(hitBoxArea.x + hitBoxArea.width >= screenChunks.length * Gdx.graphics.getWidth()) {
                    hitBoxArea.x = (screenChunks.length * Gdx.graphics.getWidth()) - hitBoxArea.width;
                    hitLevelEdge = true;
                }
                
                // Tile Collision Detection //
                else {
                    for(int y = 0; y < yCount; y++) {
                        int yOffset = (16 * y);
                        if(y == yCount - 1) {
                            yOffset = hitBoxArea.height - 1;
                        }
    
                        // Left Side Collision //
                        if(updateXMove < 0) {
                            Tile targetTile = ScreenChunk.getTile(screenChunks, (int) hitBoxArea.x, (int) hitBoxArea.y + yOffset);
                            if(targetTile != null) {
                                xHitWallCheck = targetTile.collisionCheck(screenChunks, mob, movingDirX, 0, y);
                                if(targetTile.moveOntoCheck) {
                                    yHitWallCheck = xHitWallCheck;
                                    xHitWallCheck = null;
                                }
                                
                                if(xHitWallCheck != null) {
                                    // System.out.println("-Hit-");
                                    break;
                                }
                            }
                        }
    
                        // Right Side Collision //
                        else if(updateXMove > 0) {
                            Tile targetTile = ScreenChunk.getTile(screenChunks, (int) hitBoxArea.x + hitBoxArea.width - 1, (int) hitBoxArea.y + yOffset);
                            if(targetTile != null) {
                                xHitWallCheck = targetTile.collisionCheck(screenChunks, mob, movingDirX, 1, y);
                                if(targetTile.moveOntoCheck) {
                                    yHitWallCheck = xHitWallCheck;
                                    xHitWallCheck = null;
                                }

                                if(xHitWallCheck != null) {
                                    // System.out.println("-Hit-");
                                    break;
                                }
                            }
                        }
    
                        // Middle Collision (Ramps) //
                        if(y == 0 && updateXMove != 0) {
                            Tile targetTile = ScreenChunk.getTile(screenChunks, (int) hitBoxArea.getMiddle().x, (int) hitBoxArea.y + yOffset);
                            if(targetTile != null) {
                                yHitWallCheck = targetTile.collisionCheck(screenChunks, mob, "Middle", 2, y);
                                if(yHitWallCheck != null) {
                                    // System.out.println("-(Non-Breaking, X) Hit-");
                                }
                            }
                        }
                    
                        // Index Collision Check //
                        if(hitBoxArea.width >= 32
                        && velocity.x != 0
                        && yHitWallCheck == null) {
                            int xCount = hitBoxArea.width / 16;
            
                            for(int x = 0; x < xCount; x++) {
                                int xOffset = 16 + (x * 16);
                                if(x == xCount - 1) {
                                    xOffset -= 1;
                                }

                                Tile targetTile = ScreenChunk.getTile(screenChunks, (int) hitBoxArea.x + xOffset, (int) hitBoxArea.y + yOffset);
                                if(targetTile != null) {
                                    yHitWallCheck = targetTile.collisionCheck(screenChunks, mob, movingDirX, 3 + x, y);
                                    if(yHitWallCheck != null) {
                                        // System.out.println("-Hit-");
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Update Y Location //
            if(yHitWallCheck == null
            && velocity.y != 0) {
                if(i == updateCount - 1) {
                    hitBoxArea.y = startPoint.y + yDistance;
                } else {
                    hitBoxArea.y += updateYMove;
                }
            }

            // Set FellInRampLastFrame Variables //
            fellInRampLastFrame = justFellInRamp;
            middleFellInRampLastFrame = middleJustFellInRamp;
            rightFellInRampLastFrame = rightJustFellInRamp;
            fellInSquareHalfLastFrame = justFellInSquareHalf;

            // JustFellInRamp, MiddleJustFellInRamp & JustFellInSquareHalf Check //
            if(velocity.y <= 0) {
                justFellInRamp = null;
                middleJustFellInRamp = null;
                rightJustFellInRamp = null;
                justFellInSquareHalf = null;

                int xCount = (hitBoxArea.width / 16) + 1;
                if(hitBoxArea.width % 16 > 0) {
                    xCount += 1;
                }
                for(int x = 0; x < xCount; x++) {
                    int xMod = x * 16;
                    if(x == xCount - 1) {
                        xMod = hitBoxArea.width - 1;
                    }
        
                    // JustFellInRamp, RightJustFellInRamp & JustFellInSquareHalf Check //
                    Tile targetTile = ScreenChunk.getTile(screenChunks, (int) hitBoxArea.x + xMod, (int) hitBoxArea.y);
                    if(targetTile != null) {
                        if(targetTile.tileShape.contains("Ramp")
                        && justFellInRamp == null) {
                            justFellInRamp = targetTile;
                        }
                        if(targetTile.tileShape.contains("Ramp")) {
                            rightJustFellInRamp = targetTile;
                        }
                        if(targetTile.tileShape.equals("Square-Half")
                        && justFellInSquareHalf == null) {
                            justFellInSquareHalf = targetTile;
                        }
                    }
                }
                if(rightJustFellInRamp != null && justFellInRamp != null
                && rightJustFellInRamp.getLocation().x < justFellInRamp.getLocation().x) {
                    rightJustFellInRamp = null;
                }

                // MiddleJustFellInRamp Check //
                Tile targetTile = ScreenChunk.getTile(screenChunks, (int) hitBoxArea.getMiddle().x, (int) hitBoxArea.y);
                if(targetTile != null
                && targetTile.tileShape.contains("Ramp")) {
                    middleJustFellInRamp = targetTile;
                }

                // Remove OnRamp If Falling From Opposite Ramp //
                if((onRamp != null && fellInRampLastFrame != null && justFellInRamp == null && (fellInRampLastFrame.tileShape.equals("Ramp-Right") || fellInRampLastFrame.tileShape.equals("Ramp-Left")))
                || (onRamp != null && rightFellInRampLastFrame != null && rightJustFellInRamp == null && (rightFellInRampLastFrame.tileShape.equals("Ramp-Right") || rightFellInRampLastFrame.tileShape.equals("Ramp-Left")))) {
                    onRamp = null;
                } else if((onHalfRampBottom != null && fellInRampLastFrame != null && justFellInRamp == null && (fellInRampLastFrame.tileShape.equals("Ramp-Right-Half-Bottom") || fellInRampLastFrame.tileShape.equals("Ramp-Left-Half-Bottom")))
                || (onHalfRampBottom != null && rightFellInRampLastFrame != null && rightJustFellInRamp == null) && (rightFellInRampLastFrame.tileShape.equals("Ramp-Right-Half-Bottom") || rightFellInRampLastFrame.tileShape.equals("Ramp-Left-Half-Bottom"))) {
                    onHalfRampBottom = null;
                } else if((onHalfRampTop != null && fellInRampLastFrame != null && justFellInRamp == null && (fellInRampLastFrame.tileShape.equals("Ramp-Right-Half-Top") || fellInRampLastFrame.tileShape.equals("Ramp-Left-Half-Top")))
                || (onHalfRampTop != null && rightFellInRampLastFrame != null && rightJustFellInRamp == null) && (rightFellInRampLastFrame.tileShape.equals("Ramp-Right-Half-Top") || rightFellInRampLastFrame.tileShape.equals("Ramp-Left-Half-Top"))) {
                    onHalfRampTop = null;
                }

                // Move Player Up One Tile (MiddleFellInRampLastFrame & FellInSquareHalfLastFrame Check) //
                if((middleFellInRampLastFrame != null || fellInSquareHalfLastFrame != null)
                && onRamp == null
                && onHalfRampBottom == null
                && onHalfRampTop == null) {
                    if(middleFellInRampLastFrame != null
                    && (middleFellInRampLastFrame.getLocation().y / 16) > ((int) hitBoxArea.y / 16)) {
                        targetTile = ScreenChunk.getTile(screenChunks, (int) hitBoxArea.getMiddle().x, (int) hitBoxArea.y);
                        if(targetTile == null
                        || targetTile.getLocation().y < middleFellInRampLastFrame.getLocation().y) {
                            hitBoxArea.y = middleFellInRampLastFrame.getLocation().y;
                        }
                    }
                    
                    else if(fellInSquareHalfLastFrame != null
                    && (fellInSquareHalfLastFrame.getLocation().y / 16) > ((int) hitBoxArea.y / 16)) {
                        targetTile = ScreenChunk.getTile(screenChunks, (int) hitBoxArea.getMiddle().x, (int) hitBoxArea.y);
                        if((targetTile == null
                        && !(Tile.isEmptyTile(screenChunks, (int) hitBoxArea.getMiddle().x, (int) hitBoxArea.y)) == true)

                        || (targetTile != null
                        && targetTile.getLocation().y < fellInSquareHalfLastFrame.getLocation().y)) {
                            hitBoxArea.y = fellInSquareHalfLastFrame.getLocation().y;
                        }
                    }
                }
            }

            // Y Collision Check //
            if(yHitWallCheck == null
            && (velocity.y != 0 || mob.flying)) {
                int yOffset = 0;
                String movingDirY = "";
                if(velocity.y > 0) {
                    yOffset = hitBoxArea.height - 1;
                    movingDirY = "Up";
                } else if(velocity.y < 0 || mob.flying) {
                    movingDirY = "Down";
                }

                // Screen Boundary Collision Detection //
                if(hitBoxArea.y < 0) {
                    hitBoxArea.y = 0;
                } else if(hitBoxArea.y + hitBoxArea.height >= screenChunks[0].length * Gdx.graphics.getHeight()) {
                    hitBoxArea.y = (screenChunks[0].length * Gdx.graphics.getHeight()) - hitBoxArea.height;
                }
                
                // Tile Collision Detection //
                else {
                    int yCount = (hitBoxArea.height / 16) + 1;
                    if(hitBoxArea.height % 16 != 0) {
                        yCount += 1;
                    }
                    for(int y = 0; y < yCount; y++) {
                        if(y == yCount - 1) {
                            yOffset = hitBoxArea.height - 1;
                        }
                        
                        // Middle Collision Check //
                        if(velocity.y != 0 || mob.flying) {
                            Tile targetTile = ScreenChunk.getTile(screenChunks, (int) hitBoxArea.getMiddle().x, (int) hitBoxArea.y + yOffset);
                            if(targetTile != null) {
                                yHitWallCheck = targetTile.collisionCheck(screenChunks, mob, movingDirY, 0, y);
                                if(yHitWallCheck != null) {
                                    // System.out.println("-Hit-");
                                    break;
                                }
                            }
                        }

                        // Left Side Collision Check //
                        if(velocity.y != 0 || mob.flying) {
                            Tile targetTile = ScreenChunk.getTile(screenChunks, (int) hitBoxArea.x, (int) hitBoxArea.y + yOffset);
                            if(targetTile != null) {
                                yHitWallCheck = targetTile.collisionCheck(screenChunks, mob, movingDirY, 1, y);
                                if(yHitWallCheck != null) {
                                    // System.out.println("-Hit-");
                                    break;
                                }
                            }
                        }

                        // Right Side Collision Check //
                        if(velocity.y != 0 || mob.flying) {
                            Tile targetTile = ScreenChunk.getTile(screenChunks, (int) hitBoxArea.x + hitBoxArea.width - 1, (int) hitBoxArea.y + yOffset);
                            if(targetTile != null) {
                                yHitWallCheck = targetTile.collisionCheck(screenChunks, mob, movingDirY, 2, y);
                                if(yHitWallCheck != null) {
                                    // System.out.println("-Hit-");
                                    break;
                                }
                            }
                        }
                        
                        // Index Collision Check //
                        if(hitBoxArea.width >= 32
                        && (velocity.y != 0 || mob.flying)
                        && yHitWallCheck == null) {
                            int xCount = hitBoxArea.width / 16;
            
                            for(int x = 0; x < xCount; x++) {
                                int xOffset = 16 + (x * 16);
                                if(x == xCount - 1) {
                                    xOffset -= 1;
                                }

                                Tile targetTile = ScreenChunk.getTile(screenChunks, (int) hitBoxArea.x + xOffset, (int) hitBoxArea.y + yOffset);
                                if(targetTile != null) {
                                    yHitWallCheck = targetTile.collisionCheck(screenChunks, mob, movingDirY, 3 + x, y);
                                    if(yHitWallCheck != null) {
                                        // System.out.println("-Hit-");
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

                // Falling Check //
                if(velocity.y < 0
                && yHitWallCheck == null
                && !mob.falling) {
                    mob.falling = true;
                    onRamp = null;
                    onHalfRampBottom = null;
                    onHalfRampTop = null;

                    mob.hitBoxArea.y -= mob.velocity.y;
                    mob.velocity.y = gravityLevel;
                }
            }
        }

        // Inside Ramp Check //
        Tile targetTile = ScreenChunk.getTile(screenChunks, (int) hitBoxArea.getMiddle().x, (int) hitBoxArea.y);
        if(targetTile != null) {
            if(targetTile.tileShape.length() >= 4
            && targetTile.tileShape.substring(0, 4).equals("Ramp")) {
                if(targetTile.collisionCheck(screenChunks, mob, "Middle", 2, 0) != null) {
                    // System.out.println("-Hit- (Inside Ramp Check)");
                }
            }
        }

        // Update Mob Movement (Hit Wall) Check //
        if(getClass().toString().substring(getClass().toString().lastIndexOf(".") + 1).equals("Mob")
        && (hitLevelEdge || xHitWallCheck != null)) {
            if(hitLevelEdge
            || xHitWallCheck.tileShape.contains("Square")
            || (mob.flying && (xHitWallCheck.tileShape.contains("Ramp")))) {
                mob.updateActionList.add("Hit Wall");
            }
        }
    
        // Reset Run & Flying Acceleration If Hit A Wall // 
        if(xHitWallCheck != null) {
            if(mob.runAcceleration > mob.runAccelerationMin) {
                mob.runAcceleration = mob.runAccelerationMin;
            }
            if(mob.flyingAcceleration > mob.flyingAccelerationMin) {
                mob.flyingAcceleration = mob.flyingAccelerationMin;
            }
        }
    }

    public void renderHitBox(OrthographicCamera camera, ShapeRenderer shapeRenderer, String facingDirection) {
        shapeRenderer.begin(ShapeType.Filled);
        shapeRenderer.setProjectionMatrix(camera.combined);
        
        if(getClass().toString().substring(getClass().toString().lastIndexOf(".") + 1).equals("Player")) {
            shapeRenderer.setColor(82/255f, 0/255f, 0/255f, 1f);
        } else {
            shapeRenderer.setColor(140/255f, 0/255f, 140/255f, 1f);
        }

        shapeRenderer.rect((int) hitBoxArea.x, (int) hitBoxArea.y, hitBoxArea.width, hitBoxArea.height);
        
        // Facing Direction //
        shapeRenderer.setColor(Color.YELLOW);
        if(facingDirection.equals("Right")) {
            shapeRenderer.circle((int) hitBoxArea.x + hitBoxArea.width, (int) hitBoxArea.y + (hitBoxArea.height / 2), 1);
        } else {
            shapeRenderer.circle((int) hitBoxArea.x, (int) hitBoxArea.y + (hitBoxArea.height / 2), 1);
        }

        shapeRenderer.end();
    }

    public void changeSize(int num) {
        if(num == 1) {
            hitBoxArea.width = 12;
            hitBoxArea.height = 12;
        }

        else if(num == 2) {
            hitBoxArea.width += 1;
        }

        else if(num == 3) {
            hitBoxArea.height += 1;
        }
    }

    public void resetRamps() {
        onRamp = null;
        onHalfRampBottom = null;
        onHalfRampTop = null;

        justFellInRamp = null;
        fellInRampLastFrame = null;
        justFellInSquareHalf = null;
        middleJustFellInRamp = null;
        middleFellInRampLastFrame = null;
        rightJustFellInRamp = null;
        rightFellInRampLastFrame = null;
        sideSpeedMiddleTile = null;
        sideSpeedMiddleTileLastFrame = null;
    }
}
