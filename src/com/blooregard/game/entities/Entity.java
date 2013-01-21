package com.blooregard.game.entities;

import java.awt.Rectangle;
import java.util.UUID;

import com.blooregard.game.Game;
import com.blooregard.game.gfx.Screen;
import com.blooregard.game.level.Level;

public abstract class Entity {

	protected UUID uuid;
	protected Game game;
	protected Level level;
	public boolean cleanUp = false;
	public int x, y;
	
	public Entity(Game game, Level level, UUID uuid) {
		init(game, level, uuid);
	}

	public final void init(Game game, Level level, UUID uuid) {
		this.uuid = uuid;
		this.game = game;
		this.level = level;
	}

	public final UUID getUUID() {
		return this.uuid;
	}
	
	public abstract void tick();

	public abstract void render(Screen screen);
	
	public abstract String getData();

	public abstract Rectangle getHitBox();
}
