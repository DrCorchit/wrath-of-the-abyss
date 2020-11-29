package net.drcorchit.dungeonraiders.entities.actors;

import net.drcorchit.dungeonraiders.entities.stages.DungeonStage;
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

	public void cancelVelocity() {
		setVelocity(Vector.ZERO);
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

	@Override
	public void act(float factor) {
		if (!isFixed) {
			float initialY = getPosition().y;

			if (!isGrounded()) {
				Vector gravity = stage.getGravity().multiply(factor);
				addVelocity(gravity);
			}

			Vector gravNorm = stage.getGravity().normalize();
			Vector antiGravNorm = gravNorm.rotateDegrees(90);
			Vector grav = velocity.project(gravNorm);
			Vector antiGrav = velocity.project(antiGravNorm);


			float dif = getPosition().y - initialY;

			if (!moveToContact(grav.multiply(factor))) {
				grav = Vector.ZERO;
			}

			float dif2 = getPosition().y - initialY;

			if (!moveToContact(antiGrav.multiply(factor))) {
				antiGrav = Vector.ZERO;
			}

			velocity = grav.add(antiGrav);

			float dif3 = getPosition().y - initialY;

			System.out.printf("grav: %f vel.y :%f dif1: %f, dif2: %f, dif3: %f, factor: %f\n",
					grav.y, velocity.y, dif, dif2, dif3, factor);
		}
	}

	public abstract void prePhysicsAct(float factor);

	public void postPhysicsAct(float factor) {
		//Optional, default is No-op
	}

	public boolean isGrounded() {
		return isFixed || !canMoveInDirection(stage.getGravity());
	}
}