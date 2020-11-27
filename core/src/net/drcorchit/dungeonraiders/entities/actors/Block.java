package net.drcorchit.dungeonraiders.entities.actors;

import com.badlogic.gdx.physics.box2d.BodyDef;
import net.drcorchit.dungeonraiders.assets.Textures;
import net.drcorchit.dungeonraiders.entities.stages.PhysicsStage;
import net.drcorchit.dungeonraiders.utils.AnimatedSprite;
import net.drcorchit.dungeonraiders.utils.Vector2;

public class Block extends PhysicsActor {

	AnimatedSprite sprite = Textures.asSpriteList(Textures.wall).asSprite();

	public Block(PhysicsStage stage, float x, float y, float w, float h) {
		super(stage, BodyDef.BodyType.StaticBody, x, y, w, h);
	}

	@Override
	public void actInner(float factor) {
		sprite.updateFrame(factor);
	}

	@Override
	public void draw(Vector2 position) {
		sprite.draw(getBatch(), position.x - width/2, position.y - width/2, width, height);
	}
}
