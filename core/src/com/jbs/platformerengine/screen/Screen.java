package com.jbs.platformerengine.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jbs.platformerengine.gamedata.entity.player.Player;

public class Screen {
    protected SpriteBatch spriteBatch;
    protected BitmapFont font;

    public Screen() {
        spriteBatch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("fonts/Code_New_Roman_18.fnt"), Gdx.files.internal("fonts/Code_New_Roman_18.png"), false);
    }

    public void handleInput(Player player) {}

    public void update(Player player) {}

    public void render(Player player) {}

    public void dispose() {
        spriteBatch.dispose();
    }
}
