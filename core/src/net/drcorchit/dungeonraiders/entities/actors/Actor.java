package net.drcorchit.dungeonraiders.entities.actors;

import net.drcorchit.dungeonraiders.entities.Entity;
import net.drcorchit.dungeonraiders.entities.stages.Stage;
import net.drcorchit.dungeonraiders.utils.Vector;

public abstract class Actor<T extends Stage> extends Entity implements Comparable<Actor<?>> {

	//stage is generic because certain actors depend on stage-specific properties
	protected final T stage;
	protected float depth;
	private Vector lastPosition, position;

	public Actor(T stage, Vector initialPosition) {
		this.stage = stage;
		lastPosition = initialPosition;
		this.position = initialPosition;
	}

	public void destroy() {
		stage.destroyActor(this);
	}

	public Vector getLastPosition() {
		return lastPosition;
	}

	public Vector getPosition() {
		return position;
	}

	//attempt to set the location of the actor. returns whether the operation succeeded
	boolean setPosition(Vector position) {
		this.position = position;
		return true;
	}

	boolean setPositionRelative(Vector position) {
		return setPosition(this.position.add(position));
	}

	public void updateLastPosition() {
		lastPosition = position;
	}

	public abstract void draw(Vector position);

	@Override
	public void draw() {
		draw(position);
	}

	@Override
	public int compareTo(Actor other) {
		return Float.compare(depth, other.depth);
	}
}
