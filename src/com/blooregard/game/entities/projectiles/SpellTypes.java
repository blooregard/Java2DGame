package com.blooregard.game.entities.projectiles;

import com.blooregard.game.Game;
import com.blooregard.game.entities.Mob;
import com.blooregard.game.gfx.Colors;
import com.blooregard.game.gfx.Sprite;
import com.blooregard.game.level.Level;

public class SpellTypes {
	public static final SpellTypes[] spellTypes = { new SpellTypes("FIREBALL"),
			new SpellTypes("ICELANCE"), new SpellTypes("SOULSTEAL"), new SpellTypes("HOLYFIRE")};

	public static SpellTypes get(String type) {
		for (SpellTypes st : spellTypes) {
			if (st.type.equals(type)) {
				return st;
			}
		}
		return null;
	}

	private String type;

	private SpellTypes(String type) {
		this.type = type;
	}

	public Spell get(Game game, Level level, Mob mob) {
		if (type.equals("FIREBALL")) {
			return new Spell(game, level, mob, type, new Sprite[] {
					Sprite.FIREBALL_UP, Sprite.FIREBALL_RIGHT,
					Sprite.FIREBALL_DOWN, Sprite.FIREBALL_LEFT }, 3, 6, 10, 2,
					500, Colors.get(-1, 500, 530, 555));
		}
		if (type.equals("ICELANCE")) {
			return new Spell(game, level, mob, type, new Sprite[] {
					Sprite.ICELANCE_UP, Sprite.ICELANCE_RIGHT,
					Sprite.ICELANCE_DOWN, Sprite.ICELANCE_LEFT }, 3, 6, 10, 2,
					500, Colors.get(-1, 005, 003, 555));
		}
		if (type.equals("SOULSTEAL")) {
			return new Spell(game, level, mob, type, new Sprite[] {
					Sprite.SOULSTEAL_UP, Sprite.SOULSTEAL_RIGHT,
					Sprite.SOULSTEAL_DOWN, Sprite.SOULSTEAL_LEFT }, 3, 6, 10, 2,
					500, Colors.get(-1, 000, 555, 000));
		}
		if (type.equals("HOLYFIRE")) {
			return new Spell(game, level, mob, type, new Sprite[] {
					Sprite.HOLYFIRE_UP, Sprite.HOLYFIRE_RIGHT,
					Sprite.HOLYFIRE_DOWN, Sprite.HOLYFIRE_LEFT }, 3, 6, 10, 2,
					500, Colors.get(-1, 555, 500, 530));
		}
		return null;
	}

	public Spell get() {
		return get(null, null, null);
	}

	public String getType() {
		return type;
	}
}
