package net.drcorchit.dungeonraiders.entities.actors;

import net.drcorchit.dungeonraiders.assets.Skeleton;
import net.drcorchit.dungeonraiders.assets.Skin;
import net.drcorchit.dungeonraiders.assets.animation.Animations;
import net.drcorchit.dungeonraiders.entities.stages.DungeonStage;
import net.drcorchit.dungeonraiders.input.KeyboardInfo;
import net.drcorchit.dungeonraiders.utils.MathUtils;
import net.drcorchit.dungeonraiders.utils.Vector;

public class Player extends PuppetActor {

	private static final float MAX_SPEED = 6, JUMP = 10, MOVE = .5f, FRICTION = .2f;

	private final KeyboardInfo keys = getGame().keyboard;

	public Player(DungeonStage stage, Skeleton skeleton, Skin skin, float x, float y, float w, float h) {
		super(stage, skeleton, skin, x, y, w, h);
	}

	@Override
	public void actInner(float factor) {
		int horiz = keys.horiz;
		boolean jumped = keys.space.isPressed();
		boolean grounded = isGrounded();
		float hSpeed = getVelocity().x;
		float vSpeed = getVelocity().y;

		if (horiz == -1) {
			skeleton.flipped = true;
			hSpeed -= MOVE;
		} else if (horiz == 1) {
			skeleton.flipped = false;
			hSpeed += MOVE;
		} else if (grounded) {
			//apply friction
			if (hSpeed > 0) {
				hSpeed = Math.max(0, hSpeed - FRICTION);
			} else if (hSpeed < 0) {
				hSpeed = Math.min(0, hSpeed + FRICTION);
			}
		}

		if (jumped && grounded) {
			vSpeed += JUMP;
		}

		hSpeed = MathUtils.clamp(-MAX_SPEED, hSpeed, MAX_SPEED);
		setVelocity(new Vector(hSpeed, vSpeed));

		if (grounded) {
			if (hSpeed == 0 && keys.vert == 0) {
				animator.setAnimation(Animations.stand);
			} else {
				animator.setAnimation(Animations.jog);
			}
		} else {
			animator.setAnimation(Animations.jump2);
		}

		setZRelative(keys.vert);
	}

	@Override
	public boolean collidesWith(PhysicsActor other) {
		return other instanceof Block;
	}
}
