package net.drcorchit.dungeonraiders.entities.actors;

import net.drcorchit.dungeonraiders.entities.stages.DungeonStage;
import net.drcorchit.dungeonraiders.entities.stages.Room;
import net.drcorchit.dungeonraiders.shapes.Circle;
import net.drcorchit.dungeonraiders.shapes.NoShape;
import net.drcorchit.dungeonraiders.shapes.Rectangle;
import net.drcorchit.dungeonraiders.shapes.Shape;
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
	public float getDepth() {
		return -z;
	}

	//returns whether collision between the actors should be considered
	//example:
	//players and enemies both collide with blocks
	//players collide with enemies, but not with allies
	//blocks collide with nothing
	public abstract boolean collidesWith(DungeonActor<?> other);

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
			int layerIndex = Room.getLayerIndex(z);
			Room.Layer layer = room.getLayer(layerIndex);
			if (layer.collidesWith(collider.move(position.add(colliderOffset)))) return false;
		}
		return true;
	}

	public boolean canMoveInDirection(Vector direction) {
		Vector testPos = getPosition().add(direction.normalize().multiply(.01f));
		return canOccupyPosition(testPos);
	}

	//returns true if the full range of motion was completed.
	public boolean moveToContact(Vector direction) {
		if (direction.length() == 0) return true;

		if (collider.getMinimalRadius() > direction.length()) {
			return moveToContactBinary(direction);
		} else {
			Vector step = direction.normalize().multiply(collider.getMinimalRadius());
			double numSteps = direction.length() / collider.getMinimalRadius();
			float lastStepLength = (float) MathUtils.fractionalPart(numSteps);

			//break the movement down into smaller steps of (at most) length = shape.getMinimalRadius()
			for (int i = 0; i < numSteps; i++) {
				if (!setPositionRelative(step)) {
					if (!moveToContactBinary(step)) return false;
				}
			}

			Vector lastStep = step.multiply(lastStepLength);
			return moveToContactBinary(lastStep);
		}
	}

	//returns true if the full range of motion was completed.
	public boolean moveToContactZ(float amount) {
		if (amount == 0) return true;

		float targetZ = z + amount;
		if (targetZ < stage.getMinZ() || targetZ > stage.getMaxZ()) {
			targetZ = amount > 0 ? stage.getMaxZ() : stage.getMinZ();
			moveToContactZ(targetZ - z);
			return false;
		}

		int currentIndex = Room.getLayerIndex(z);
		int endIndex = Room.getLayerIndex(z + amount);

		if (currentIndex == endIndex) {
			return setZRelative(amount);
		} else {
			float step = DungeonStage.BLOCK_SIZE * Math.signum(amount);
			boolean temp = setZRelative(step);

			if (temp) {
				return moveToContactZ(amount - step);
			} else {
				moveToContactZBinary(amount);
				return false;
			}
		}
	}

	private boolean moveToContactBinary(Vector direction) {
		if (direction.length() < 0.01f) return true;
		if (setPositionRelative(direction)) {
			return true;
		} else {
			Vector half = direction.half();
			//try to move halfway
			setPositionRelative(half);
			//try to move the remaining half (or the first half)
			moveToContactBinary(half);
			return false;
		}
	}

	private boolean moveToContactZBinary(float amount) {
		if (amount < 1f) return true;
		if (setZRelative(amount)) {
			return true;
		} else {
			//try to move halfway
			setZRelative(amount / 2);
			//try to move the remaining half (or the first half)
			moveToContactZBinary(amount / 2);
			return false;
		}
	}

}
