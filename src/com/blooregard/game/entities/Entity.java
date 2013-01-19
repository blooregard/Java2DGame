package com.blooregard.game.entities;

import java.awt.Rectangle;

import com.blooregard.game.Game;
import com.blooregard.game.gfx.Screen;
import com.blooregard.game.level.Level;

public abstract class Entity {

	public int x, y;
	protected Game game;
	protected Level level;
	
	public Entity (Game game, Level level) {
		init(game, level);
	}
	
	public final void init (Game game, Level level) {
		this.game = game;
		this.level = level;
	}

	public abstract void tick();
	
	public abstract void render(Screen screen);
	
	public abstract Rectangle getHitBox();
}
