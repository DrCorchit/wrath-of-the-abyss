package net.drcorchit.dungeonraiders.entities.actors;

import net.drcorchit.dungeonraiders.DungeonRaidersGame;
import net.drcorchit.dungeonraiders.assets.Skeleton;
import net.drcorchit.dungeonraiders.assets.Skin;
import net.drcorchit.dungeonraiders.assets.animation.Animations;
import net.drcorchit.dungeonraiders.entities.stages.DungeonStage;
import net.drcorchit.dungeonraiders.entities.stages.Room;
import net.drcorchit.dungeonraiders.input.KeyboardInfo;
import net.drcorchit.dungeonraiders.utils.MathUtils;
import net.drcorchit.dungeonraiders.utils.Vector;

public class Player extends PuppetActor {

	private static final float MAX_H_SPEED = 6, MAX_V_SPEED = 20, JUMP = 10, MOVE = .5f, FRICTION = .2f;

	private final KeyboardInfo keys = getGame().keyboard;
	private Vector cameraOffset;

	public Player(DungeonStage stage,
				  Skeleton skeleton,
				  Skin skin,
				  Vector position,
				  Vector skeletonOffset) {
		super(stage,
				skeleton,
				skin,
				position,
				skeletonOffset);
		this.cameraOffset = Vector.ZERO;
	}

	public Vector getCameraOffset() {
		return cameraOffset;
	}

	public void setCameraOffset(Vector cameraOffset) {
		this.cameraOffset = cameraOffset;
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

		hSpeed = MathUtils.clamp(-MAX_H_SPEED, hSpeed, MAX_H_SPEED);
		vSpeed = MathUtils.clamp(-MAX_V_SPEED, vSpeed, MAX_V_SPEED);
		setVelocity(new Vector(hSpeed, vSpeed));

		//move in z direction
		boolean movedZ = keys.vert != 0 && moveToContactZ(-keys.vert);

		if (grounded) {
			if (hSpeed == 0 && !movedZ) {
				animator.setAnimation(Animations.stand);
			} else {
				animator.setAnimation(Animations.jog);
			}
		} else {
			animator.setAnimation(Animations.jump2);
		}

		//set camera
		stage.setViewCenter(getPosition().add(cameraOffset));
		DungeonRaidersGame.getInstance().debugInfoMap.put("player z", getZ());
		int zLayer = Room.getLayerIndex(getZ());
		DungeonRaidersGame.getInstance().debugInfoMap.put("z layers", zLayer);
	}

	@Override
	public boolean collidesWith(DungeonActor other) {
		return other instanceof Room.Layer;
	}
}
