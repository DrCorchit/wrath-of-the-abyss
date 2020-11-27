package net.drcorchit.dungeonraiders.entities.actors;

import net.drcorchit.dungeonraiders.entities.stages.DungeonStage;
import net.drcorchit.dungeonraiders.shapes.Circle;
import net.drcorchit.dungeonraiders.shapes.NoShape;
import net.drcorchit.dungeonraiders.shapes.Rectangle;
import net.drcorchit.dungeonraiders.shapes.Shape;
import net.drcorchit.dungeonraiders.utils.MathUtils;
import net.drcorchit.dungeonraiders.utils.Vector;

public abstract class PhysicsActor extends Actor<DungeonStage> {

	//if true, actor's velocity is fixed at zero
	private final boolean isFixed;
	//z position in the DungeonStage
	private float z;
	private Shape shape;
	private Vector velocity;

	public PhysicsActor(DungeonStage stage, boolean isFixed, float x, float y) {
		super(stage, x, y);
		this.isFixed = isFixed;
		z = 0;
		this.shape = NoShape.INSTANCE;
		velocity = Vector.ZERO;
	}

	//shapes, by design, are immutable.
	public Shape getShape() {
		return shape;
	}

	public void setShapeAsRectangle(float x, float y, float w, float h) {
		shape = new Rectangle(() -> getPosition().add(x, y), w, h);
	}

	public void setShapeAsCircle(float x, float y, float r) {
		shape = new Circle(() -> getPosition().add(x, y), r);
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

	public void setZRelative(float amount) {
		setZ(z + amount);
	}

	public void setZ(float z) {
		this.z = MathUtils.clamp(stage.getMinZ(), z, stage.getMaxZ());
	}

	public float getZ() {
		return z;
	}

	public float getZScale() {
		return DungeonStage.getZScale(z);
	}

	@Override
	public void act(float factor) {
		if (!isFixed) {
			if (!isGrounded()) {
				Vector gravity = stage.getGravity().multiply(factor);
				addVelocity(gravity);

				boolean stopped = !moveToContact(velocity.multiply(factor));

				if (stopped) {
					Vector gravComponent = velocity.project(gravity);

					if (isGrounded()) {
						//set the velocity in the gravity direction to 0
						velocity.subtract(gravComponent);
					} else {
						setVelocity(gravComponent);
					}
				}
			} else {
				moveToContact(velocity.multiply(factor));
			}
		}
	}

	public boolean isGrounded() {
		return isFixed || !canMoveInDirection(stage.getGravity());
	}

	//returns whether collision between the actors should be considered
	//example:
	//players and enemies both collide with blocks
	//players collide with enemies, but not with allies
	//blocks collide with nothing
	public abstract boolean collidesWith(PhysicsActor other);

	@Override
	boolean setPosition(Vector position) {
		if (canOccupyPosition(position)) {
			super.setPosition(position);
			return true;
		}
		return false;
	}

	public boolean canOccupyPosition(Vector position) {
		if (shape == NoShape.INSTANCE) return true;

		for (Actor actor : stage.getActors()) {
			if (actor instanceof PhysicsActor) {
				PhysicsActor sa = (PhysicsActor) actor;
				if (collidesWith(sa)) {
					boolean temp = shape.wouldCollideWith(position, sa.shape);
					if (temp) return false;
				}
			}
		}
		return true;
	}

	public boolean canMoveInDirection(Vector direction) {
		return canOccupyPosition(getPosition().add(direction.normalize()));
	}

	//returns true if the full range of motion was completed.
	public boolean moveToContact(Vector direction) {
		if (direction.length() == 0) return true;
		if (shape.getMinimalRadius() > direction.length()) {
			return moveToContactBinary(direction);
		} else {
			Vector step = direction.normalize().multiply(shape.getMinimalRadius());
			double numSteps = direction.length() / shape.getMinimalRadius();
			float lastStepLength = (float) MathUtils.fractionalPart(numSteps);
			Vector lastStep = step.multiply(lastStepLength);

			//break the movement down into smaller steps of (at most) length = shape.getMinimalRadius()
			for (int i = 0; i < numSteps; i++) {
				if (!setPositionRelative(step)) {
					moveToContactBinary(step);
					return false;
				}
			}

			return moveToContactBinary(lastStep);
		}
	}

	//returns true if the full range of motion was completed.
	private boolean moveToContactBinary(Vector direction) {
		if (direction.length() < 0.5f) return true;
		if (setPositionRelative(direction)) {
			return true;
		} else {
			Vector half = direction.half();
			//try to move halfway
			setPositionRelative(half);
			//try to move the remaining half (or the first half)
			moveToContact(half);
			return false;
		}
	}
}