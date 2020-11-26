package net.drcorchit.dungeonraiders.entities;

import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import net.drcorchit.dungeonraiders.DungeonRaidersGame;

public abstract class Entity {

	public abstract void act(float delta);

	public  abstract void draw();

	//convenience method
	public final DungeonRaidersGame getGame() {
		return DungeonRaidersGame.getInstance();
	}

	public final PolygonSpriteBatch getBatch() {
		return DungeonRaidersGame.getDraw().getBatch();
	}
}
