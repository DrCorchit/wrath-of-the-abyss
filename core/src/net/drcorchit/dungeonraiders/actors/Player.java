package net.drcorchit.dungeonraiders.actors;

import net.drcorchit.dungeonraiders.DungeonRaidersGame;
import net.drcorchit.dungeonraiders.assets.Skeleton;
import net.drcorchit.dungeonraiders.assets.Skin;
import net.drcorchit.dungeonraiders.assets.animation.Animations;
import net.drcorchit.dungeonraiders.stages.DungeonStage;
import net.drcorchit.dungeonraiders.input.KeyboardInfo;
import net.drcorchit.dungeonraiders.utils.MathUtils;
import net.drcorchit.dungeonraiders.utils.Vector;

import java.util.TreeMap;

public class Player extends PuppetActor<DungeonStage> {

	private static final float MAX_H_SPEED = 9, MAX_V_SPEED = 20;
	private static final float JUMP = 12, MOVE = .4f, Z_SPEED = 2, FRICTION = .15f;

	private final KeyboardInfo keys = DungeonRaidersGame.getInstance().keyboard;
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
	public void prePhysicsAct(float factor) {
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
			hSpeed = MathUtils.decelerate(hSpeed, FRICTION);
		}

		if (jumped) { // && grounded
			vSpeed += JUMP;
		}

		hSpeed = MathUtils.clamp(-MAX_H_SPEED, hSpeed, MAX_H_SPEED);
		vSpeed = MathUtils.clamp(-MAX_V_SPEED, vSpeed, MAX_V_SPEED);
		setVelocity(new Vector(hSpeed, vSpeed));

		//move in z direction
		boolean movedZ = keys.vert != 0 && moveToContactZ(-keys.vert * Z_SPEED);

		if (grounded) {
			if (hSpeed == 0 && !movedZ) {
				animator.setAnimation(Animations.stand);
			} else {
				animator.setAnimation(Animations.jog);
			}
		} else {
			animator.setAnimation(Animations.jump2);
		}
	}

	@Override
	public void postPhysicsAct(float factor) {
		//set camera
		stage.setViewCenter(getPosition().add(cameraOffset));
		TreeMap<String, Object> map = DungeonRaidersGame.getInstance().debugInfoMap;
		int zLayer = Room.getLayerIndex(getZ());
		map.put("velocity", getVelocity());
		map.put("x", getPosition().x);
		map.put("y", getPosition().y);
		map.put("z", getZ());
		map.put("z layer", zLayer);
	}
}