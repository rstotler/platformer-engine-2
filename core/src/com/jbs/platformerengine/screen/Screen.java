package com.jbs.platformerengine.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.jbs.platformerengine.gamedata.entity.player.Player;

public class Screen {
    protected OrthographicCamera camera;
    protected SpriteBatch spriteBatch;
    protected BitmapFont font, fontSmall;

    public Screen() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        spriteBatch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("fonts/Code_New_Roman_18.fnt"), Gdx.files.internal("fonts/Code_New_Roman_18.png"), false);
        fontSmall = new BitmapFont(Gdx.files.internal("fonts/Code_New_Roman_10.fnt"), Gdx.files.internal("fonts/Code_New_Roman_10.png"), false);
    }

    public void handleInput(Player player) {}

    public void update(Player player) {}

    public void render(Player player) {}

    public void dispose() {}
}
