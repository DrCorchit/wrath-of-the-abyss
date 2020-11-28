package net.drcorchit.dungeonraiders.entities.actors;

import com.badlogic.gdx.graphics.Color;
import net.drcorchit.dungeonraiders.assets.Textures;
import net.drcorchit.dungeonraiders.entities.stages.DungeonStage;
import net.drcorchit.dungeonraiders.utils.AnimatedSprite;
import net.drcorchit.dungeonraiders.utils.Vector;

public class Block extends PhysicsActor {

	private final AnimatedSprite sprite = Textures.asSpriteList(Textures.WALL).asSprite();
	private final float width, height;

	public Block(DungeonStage stage, float x, float y, float w, float h) {
		super(stage, true, new Vector(x, y));
		this.width = w;
		this.height = h;
		setColliderToRectangle(0, 0, w, h);
	}

	@Override
	public void draw(Vector position) {
		getCollider().draw(Color.WHITE);
		//sprite.draw(getBatch(), position.x - width/2, position.y - width/2, width, height);
	}

	@Override
	public void act(float delta) {

	}

	@Override
	public boolean collidesWith(DungeonActor other) {
		return true;
	}

	@Override
	public boolean canOccupyPosition(Vector position) {
		return true;
	}
}
