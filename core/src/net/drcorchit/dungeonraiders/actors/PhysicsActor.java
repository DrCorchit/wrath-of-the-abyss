package net.drcorchit.dungeonraiders.actors;

import net.drcorchit.dungeonraiders.stages.DungeonStage;
import net.drcorchit.dungeonraiders.utils.Vector;

public abstract class PhysicsActor<T extends DungeonStage> extends DungeonActor<T> {

	//if true, actor's velocity is fixed at zero
	private final boolean isFixed;
	//z position in the DungeonStage
	private Vector velocity;

	public PhysicsActor(T stage, boolean isFixed, Vector position) {
		super(stage, position);
		this.isFixed = isFixed;
		velocity = Vector.ZERO;
	}

	public void setVelocity(Vector velocity) {
		if (!isFixed) this.velocity = velocity;
	}

	public void addVelocity(Vector velocity) {
		setVelocity(this.velocity.add(velocity));
	}

	public Vector getVelocity() {
		return velocity;
	}

	public Vector getGravity() {
		return getStage().getGravity();
	}

	@Override
	public void act(float factor) {
		if (!isFixed) {
			if (!isGrounded()) {
				Vector gravity = getGravity().multiply(factor);
				addVelocity(gravity);
			}

			Vector gravNorm = getGravity().normalize();
			Vector antiGravNorm = gravNorm.rotateDegrees(90);
			Vector grav = velocity.project(gravNorm);
			Vector antiGrav = velocity.project(antiGravNorm);

			if (moveToContact(grav.multiply(factor))) {
				grav = Vector.ZERO;
			}

			if (moveToContact(antiGrav.multiply(factor))) {
				antiGrav = Vector.ZERO;
			}

			velocity = grav.add(antiGrav);
		}
	}

	public abstract void prePhysicsAct(float factor);

	public void postPhysicsAct(float factor) {
		//Optional, default is No-op
	}

	public boolean isGrounded() {
		return isFixed || !canMoveInDirection(getGravity());
	}
}