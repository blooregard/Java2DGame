package com.blooregard.game.entities.loot;

import java.awt.Rectangle;
import java.util.UUID;

import com.blooregard.game.Game;
import com.blooregard.game.entities.Entity;
import com.blooregard.game.gfx.Screen;
import com.blooregard.game.gfx.Sprite;
import com.blooregard.game.level.Level;

public class Loot extends Entity {

	private int xMin = 0, xMax = 7, yMin = 0, yMax = 7;
	private long creationTime = System.currentTimeMillis();

	protected String name;
	protected Sprite sprite;
	protected int addHealth;
	protected int addMana;
	protected int color;
	
	public Loot(Game game, Level level, String name, int x, int y,
			Sprite sprite, int addHealth, int addMana, int color) {
		super(game, level, UUID.randomUUID());
		this.name = name;
		this.x = x;
		this.y = y;
		this.sprite = sprite;
		this.addHealth = addHealth;
		this.addMana = addMana;
		this.color = color;
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(Screen screen) {
		// TODO Auto-generated method stub
		//screen.render(x, y, sprite.width, sprite.height, sprite.pixels, 1);
		screen.render(x, y, 0 + 21 * 32, color, 0, 1);
	}

	@Override
	public String getData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rectangle getHitBox() {
		return new Rectangle(x + xMin, y + yMin, xMax - xMin + 1, yMax - yMin
				+ 1);
	}

}
