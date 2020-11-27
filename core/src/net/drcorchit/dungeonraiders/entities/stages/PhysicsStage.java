package net.drcorchit.dungeonraiders.entities.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.World;
import net.drcorchit.dungeonraiders.utils.Vector2;

public class PhysicsStage extends Stage {

	public final World world;

	public PhysicsStage() {
		world = new World(new Vector2(0, -100).toLibGDX(), true);
	}

	private void updatePhysics(float factor) {
		float delta = EXPECTED_DELTA_TIME/factor;
		world.step(delta, 2, 6);
	}

	@Override
	public void act() {
		float delta = Gdx.graphics.getDeltaTime();
		//factor goes down as delta time gets shorter
		float factor = Math.min(EXPECTED_DELTA_TIME/delta, 1);
		act(factor);
	}

	@Override
	public void act(float factor) {
		updatePhysics(factor);
		super.act(factor);
	}
}