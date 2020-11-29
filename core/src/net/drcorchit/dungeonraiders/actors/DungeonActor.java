package net.drcorchit.dungeonraiders.actors;

import net.drcorchit.dungeonraiders.drawing.shapes.Circle;
import net.drcorchit.dungeonraiders.drawing.shapes.NoShape;
import net.drcorchit.dungeonraiders.drawing.shapes.Rectangle;
import net.drcorchit.dungeonraiders.drawing.shapes.Shape;
import net.drcorchit.dungeonraiders.stages.DungeonStage;
import net.drcorchit.dungeonraiders.stages.Room;
import net.drcorchit.dungeonraiders.utils.MathUtils;
import net.drcorchit.dungeonraiders.utils.Vector;

public abstract class DungeonActor<T extends DungeonStage> extends Actor<T> {

	private float z;
	private Vector colliderOffset;
	private Shape collider;

	public DungeonActor(T stage, Vector position) {
		super(stage, position);
		z = 0;
		colliderOffset = Vector.ZERO;
		collider = NoShape.INSTANCE;
	}

	public Shape getCollider() {
		return collider;
	}

	public Vector getColliderPosition() {
		return getPosition().add(colliderOffset);
	}

	public Vector getColliderOffset() {
		return colliderOffset;
	}

	public void setColliderToRectangle(float x, float y, float w, float h) {
		colliderOffset = new Vector(x, y);
		collider = new Rectangle(this::getColliderPosition, w, h);
	}

	public void setColliderToCircle(float x, float y, float r) {
		colliderOffset = new Vector(x, y);
		collider = new Circle(this::getColliderPosition, r);
	}

	public boolean setZRelative(float amount) {
		return setZ(z + amount);
	}

	public boolean setZ(float z) {
		if (canOccupyPosition(getPosition(), z)) {
			this.z = z;
			return true;
		} else {
			return false;
		}
	}

	public float getZ() {
		return z;
	}

	public float getZScale() {
		return DungeonStage.getZScale(z);
	}

	@Override
	boolean setPosition(Vector position) {
		if (canOccupyPosition(position)) {
			super.setPosition(position);
			return true;
		}
		return false;
	}

	public boolean canOccupyPosition(Vector position) {
		return canOccupyPosition(position, z);
	}

	public boolean canOccupyPosition(Vector position, float z) {
		if (collider == NoShape.INSTANCE) return true;

		if (z < stage.getMinZ() || z > stage.getMaxZ()) {
			return false;
		}

		for (Room room : stage.getOverlappedRooms(this)) {
			if (room.collidesWith(this, position, z)) return false;
		}
		return true;
	}

	public boolean canMoveInDirection(Vector direction) {
		Vector testPos = getPosition().add(direction.normalize().multiply(.01f));
		return canOccupyPosition(testPos);
	}

	//returns true if the object made contact (met an obstruction)
	public boolean moveToContact(Vector direction) {
		if (direction.length() == 0) return false;

		if (collider.getMinimalRadius() > direction.length()) {
			return moveToContactBinary(direction);
		} else {
			Vector step = direction.normalize().multiply(collider.getMinimalRadius());
			double ratio = direction.length() / collider.getMinimalRadius();
			int numSteps = (int) ratio;
			float lastStepLength = (float) MathUtils.fractionalPart(ratio);

			//break the movement down into smaller steps of (at most) length = shape.getMinimalRadius()
			for (int i = 0; i < numSteps; i++) {
				if (!setPositionRelative(step)) {
					moveToContactBinary(step);
					return true;
				}
			}

			Vector lastStep = step.multiply(lastStepLength);
			return moveToContactBinary(lastStep);
		}
	}

	//returns true if the object made contact (met an obstruction)
	public boolean moveToContactZ(float amount) {
		if (amount == 0) return true;

		float targetZ = z + amount;
		if (targetZ < stage.getMinZ() || targetZ > stage.getMaxZ()) {
			targetZ = amount > 0 ? stage.getMaxZ() : stage.getMinZ();
			moveToContactZ(targetZ - z);
			return false;
		}

		if (amount < DungeonStage.BLOCK_SIZE) {
			return setZRelative(amount);
		} else {
			float step = DungeonStage.BLOCK_SIZE * Math.signum(amount);

			if (setZRelative(step)) {
				return moveToContactZ(amount - step);
			} else {
				moveToContactZBinary(amount);
				return false;
			}
		}
	}

	private boolean moveToContactBinary(Vector direction) {
		if (direction.length() < 0.01f) return false;
		if (setPositionRelative(direction)) {
			return false;
		} else {
			Vector half = direction.half();
			//try to move halfway
			setPositionRelative(half);
			//try to move the remaining half (or the first half)
			moveToContactBinary(half);
			return true;
		}
	}

	private boolean moveToContactZBinary(float amount) {
		if (amount < .1f) return false;
		if (setZRelative(amount)) {
			return false;
		} else {
			//try to move halfway
			setZRelative(amount / 2);
			//try to move the remaining half (or the first half)
			moveToContactZBinary(amount / 2);
			return true;
		}
	}

}
