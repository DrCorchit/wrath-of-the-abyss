package net.drcorchit.dungeonraiders.entities;

import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import net.drcorchit.dungeonraiders.DungeonRaidersGame;
import net.drcorchit.dungeonraiders.utils.Draw;

public abstract class Entity {

	//convenience methods

	public final Draw getDraw() {
		return DungeonRaidersGame.getDraw();
	}
}
