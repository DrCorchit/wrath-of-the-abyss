package net.drcorchit.dungeonraiders.entities.actors;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import net.drcorchit.dungeonraiders.entities.stages.PhysicsStage;
import net.drcorchit.dungeonraiders.utils.Vector2;

public abstract class PhysicsActor extends Actor<PhysicsStage> {

	protected final Body body;
	public final float width, height;

	public PhysicsActor(PhysicsStage stage, BodyDef.BodyType type, float x, float y, float w, float h) {
		super(stage, x, y);
		this.width = w;
		this.height = h;

		BodyDef bDef = new BodyDef();
		bDef.type = type;
		bDef.position.set(x, y);
		body = stage.world.createBody(bDef);
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(w/2, h/2);
		body.createFixture(shape, 1);
		shape.dispose();
	}

	public Vector2 getVelocity() {
		return Vector2.fromLibGDX(body.getLinearVelocity());
	}

	public abstract void actInner(float factor);

	@Override
	public void act(float factor) {
		actInner(factor);
		setPosition(Vector2.fromLibGDX(body.getPosition()));
	}
}
