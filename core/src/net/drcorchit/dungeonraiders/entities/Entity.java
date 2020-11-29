package net.drcorchit.dungeonraiders.entities;

import net.drcorchit.dungeonraiders.DungeonRaidersGame;
import net.drcorchit.dungeonraiders.drawing.Draw;

public abstract class Entity {

	//convenience methods

	public final Draw getDraw() {
		return DungeonRaidersGame.getDraw();
	}
}
