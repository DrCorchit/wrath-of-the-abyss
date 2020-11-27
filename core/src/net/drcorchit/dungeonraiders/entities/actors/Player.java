package net.drcorchit.dungeonraiders.entities.actors;

import net.drcorchit.dungeonraiders.assets.Skeleton;
import net.drcorchit.dungeonraiders.assets.Skin;
import net.drcorchit.dungeonraiders.assets.animation.Animations;
import net.drcorchit.dungeonraiders.entities.stages.PhysicsStage;
import net.drcorchit.dungeonraiders.utils.Vector2;

public class Player extends PuppetActor {

	private static final float MAX_SPEED = 10;
	private static final float STAND_ANIMATION_MAX_SPEED = .00001f;

	public Player(PhysicsStage stage, Skeleton skeleton, Skin skin, float x, float y, float w, float h) {
		super(stage, skeleton, skin, x, y, w, h);
	}

	private int horiz() {
		return getGame().keyboard.horiz;
	}

	private int vert() {
		return getGame().keyboard.vert;
	}

	@Override
	public void actInner(float factor) {
		Vector2 velocity = Vector2.fromLibGDX(body.getLinearVelocity());
		int horiz = horiz();

		switch (horiz) {
			case -1:
			case 1:
				if (velocity.x < MAX_SPEED) {
					body.applyLinearImpulse(new Vector2(body.getMass() * horiz * 10, 0).toLibGDX(), body.getPosition(), true);
				}
				break;
			default:
				body.setLinearVelocity(0, velocity.y);
				break;
		}

		if (velocity.x > MAX_SPEED) {
			//body.setLinearDamping(1);
		}

		if (horiz == -1) {
			skeleton.flipped = true;
		} else if (horiz == 1) {
			skeleton.flipped = false;
		}

		if (vert() == 1) {
			//body.applyForceToCenter(0, body.getMass(), true);
			body.setLinearVelocity(0, 100);
		}

		velocity = Vector2.fromLibGDX(body.getLinearVelocity());

		if (velocity.length() < STAND_ANIMATION_MAX_SPEED) {
			animator.setAnimation(Animations.stand);
		} else {
			animator.setAnimation(Animations.jogging);
		}
	}
}
