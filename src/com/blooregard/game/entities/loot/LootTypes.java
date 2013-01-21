package com.blooregard.game.entities.loot;

import com.blooregard.game.Game;
import com.blooregard.game.gfx.Colors;
import com.blooregard.game.gfx.Sprite;
import com.blooregard.game.level.Level;

public class LootTypes {
	public static final LootTypes[] lootTypes = { new LootTypes("MANA_POTION"),
			new LootTypes("HEALTH_POTION"), new LootTypes("BOTH_POTION") };

	public static LootTypes get(String type) {
		for (LootTypes lt : lootTypes) {
			if (lt.type.equals(type)) {
				return lt;
			}
		}
		return null;
	}

	private String type;

	private LootTypes(String type) {
		this.type = type;
	}

	public Loot get(Game game, Level level, int x, int y) {
		if (type.equals("MANA_POTION")) {
			return new Loot(game, level, type, x, y, Sprite.MANA_POTION, 0, 25,
					Colors.get(-1, 544, -1, 005));
		}
		if (type.equals("HEALTH_POTION")) {
			return new Loot(game, level, type, x, y, Sprite.HEALTH_POTION, 25,
					0, Colors.get(-1, 544, -1, 050));
		}
		if (type.equals("BOTH_POTION")) {
			return new Loot(game, level, type, x, y, Sprite.BOTH_POTION, 25,
					25, Colors.get(-1, 544, -1, 055));
		}
		return null;
	}

	public Loot get() {
		return get(null, null, 0, 0);
	}

	public String getType() {
		return type;
	}
}
