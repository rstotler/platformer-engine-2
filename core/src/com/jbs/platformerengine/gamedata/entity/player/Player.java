package com.jbs.platformerengine.gamedata.entity.player;

import com.jbs.platformerengine.components.Keyboard;
import com.jbs.platformerengine.gamedata.Point;
import com.jbs.platformerengine.gamedata.entity.mob.Mob;
import com.jbs.platformerengine.screen.ImageManager;

public class Player extends Mob {
    public Player(ImageManager imageManager) {
        super("Bat", new Point(3750, 600), imageManager, true);

        displayHitBox = true;
    }

    public void updateInput(Keyboard keyboard) {
        boolean moveCheck = false;
        boolean turnAroundCheck = false;

        // Flying Movement //
        if(flying) {
            if(keyboard.left && !keyboard.right) {
                velocity.x = -moveSpeed;
                if(facingDirection.equals("Right")) {
                    facingDirection = "Left";
                    turnAroundCheck = true;
                }
                moveCheck = true;
            } else if(keyboard.right && !keyboard.left) {
                velocity.x = moveSpeed;
                if(facingDirection.equals("Left")) {
                    facingDirection = "Right";
                    turnAroundCheck = true;
                }
                moveCheck = true;
            } else {
                velocity.x = 0;
            }

            if(keyboard.up && !keyboard.down) {
                velocity.y = moveSpeed;
                moveCheck = true;
            } else if(keyboard.down && !keyboard.up) {
                velocity.y = -moveSpeed;
                moveCheck = true;
            } else {
                velocity.y = 0;
            }
        }

        // Non-Flying Movement //
        else {

            // Sideways Movement //
            velocity.x = 0;
            if(keyboard.left && !keyboard.right && (attackCount == 0 || inAir())) {
                if(!ducking) {
                    velocity.x = -moveSpeed;
                }
                if(facingDirection.equals("Right")) {
                    facingDirection = "Left";
                    turnAroundCheck = true;
                }
                moveCheck = true;
            } else if(!keyboard.left && keyboard.right && (attackCount == 0 || inAir())) {
                if(!ducking) {
                    velocity.x = moveSpeed;
                }
                if(facingDirection.equals("Left")) {
                    facingDirection = "Right";
                    turnAroundCheck = true;
                }
                moveCheck = true;
            }

            // Jump/Drop Kick //
            if(keyboard.up || dropKickBounceCheck) {
                if(((keyboard.lastDown.contains("Up") || keyboard.lastDown.contains("W")))
                && !ducking) {
                    if(jumpCount < getMaxJumpCount() && !jumpButtonPressedCheck) {
                        if(superJumpPercent < .30) {
                            jump();
                        }
                        
                    // Drop Kick (Button Press) //
                    } else {
                        if(!dropKickCheck && (superJumpTimer == 0 || superJumpTimer >= superJumpTimerMax)) {
                            dropKickCheck = true;
                        }
                    }

                } else if(jumpTimer < jumpTimerMax) {
                    jumpTimer += 1;
                }
            }
            if((keyboard.lastUp.contains("Up") || keyboard.lastUp.contains("W"))
            && dropKickBounceCheck == false) {
                jumpTimer = jumpTimerMax;
                jumpButtonPressedCheck = false;
            }

            // Super Jump //
            if(superJumpCheck) {
                if(superJumpTimer < superJumpTimerMax) {
                    superJumpTimer += 1;
                    superJumpPercent = (float) (1 - Math.sin(Math.toRadians((superJumpTimer / superJumpTimerMax) * 90)));
                    if(24 * superJumpPercent >= 1.25) {
                        velocity.y = 24 * superJumpPercent;
                    }
                }
            }

            // Dash //
            if(dashCheck) {
                if(dashTimer < dashTimerMax) {
                    dashTimer += 1;
                    dashPercent = (float) (1 - Math.sin(Math.toRadians((dashTimer / dashTimerMax) * 90)));
                    if(7 * dashPercent >= 1.25) {
                        int directionMod = 1;
                        if(dashDirection.equals("Left")) {
                            directionMod = -1;
                        }
                        velocity.x = (7 * dashPercent) * directionMod;
                    }
                } else {
                    dashCheck = false;
                }
            }
        }

        // Run Check //
        if(keyboard.shift && moveCheck) {
            running = true;
        } else {
            running = false;
        }

        // Turn Around Check //
        if(turnAroundCheck) {
            flyingAcceleration = flyingAccelerationMin;
            runAcceleration = runAccelerationMin;
        }
        
        keyboard.lastDown.clear();
        keyboard.lastUp.clear();
    }
}
