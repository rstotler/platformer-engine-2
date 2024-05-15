package com.jbs.platformerengine;

import com.badlogic.gdx.ApplicationAdapter;
import com.jbs.platformerengine.gamedata.entity.player.Player;
import com.jbs.platformerengine.screen.Screen;
import com.jbs.platformerengine.screen.gamescreen.GameScreen;

public class PlatformerEngine extends ApplicationAdapter {
	Screen screen;
	Player player;
	
	@Override
	public void create () {
		screen = new GameScreen();
		player = new Player("", null);
	}

	@Override
	public void render () {
		screen.handleInput(player);
		screen.update(player);
		screen.render(player);
	}
	
	@Override
	public void dispose () {
		screen.dispose();
	}
}
