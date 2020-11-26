package net.drcorchit.dungeonraiders.entities.actors;

import net.drcorchit.dungeonraiders.entities.Entity;
import net.drcorchit.dungeonraiders.entities.stages.Stage;
import net.drcorchit.dungeonraiders.utils.MathUtils;
import net.drcorchit.dungeonraiders.utils.Vector2;

public abstract class Actor<T extends Stage> extends Entity implements Comparable<Actor<?>> {

	protected final T stage;
	protected float depth;
	private Vector2 lastPosition, position;

	public Actor(T stage, float x, float y) {
		this.stage = stage;
		lastPosition = new Vector2(x, y);
		position = lastPosition;
	}

	public void destroy() {
		stage.destroyActor(this);
	}

	public Vector2 getDirectionVector() {
		return position.subtract(lastPosition);
	}

	public Vector2 getLastPosition() {
		return lastPosition;
	}

	public Vector2 getPosition() {
		return position;
	}

	void setPosition(Vector2 position) {
		this.position = position;
	}

	void setPosition(float x, float y) {
		setPosition(new Vector2(x, y));
	}

	public void updateLastPosition() {
		lastPosition = position;
	}

	public abstract void draw(Vector2 position);

	@Override
	public void draw() {
		draw(position);
	}

	@Override
	public int compareTo(Actor other) {
		return Float.compare(depth, other.depth);
	}
}
