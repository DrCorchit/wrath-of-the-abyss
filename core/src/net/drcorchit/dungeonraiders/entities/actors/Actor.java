package net.drcorchit.dungeonraiders.entities.actors;

import net.drcorchit.dungeonraiders.entities.Entity;
import net.drcorchit.dungeonraiders.entities.stages.Stage;
import net.drcorchit.dungeonraiders.shapes.Circle;
import net.drcorchit.dungeonraiders.shapes.NoShape;
import net.drcorchit.dungeonraiders.shapes.Rectangle;
import net.drcorchit.dungeonraiders.shapes.Shape;
import net.drcorchit.dungeonraiders.utils.Vector;

public abstract class Actor<T extends Stage> extends Entity implements Comparable<Actor<?>> {

	//stage is generic because certain actors depend on stage-specific properties
	protected final T stage;
	private Vector lastPosition, position, viewOffset;
	private Shape viewBounds;

	public Actor(T stage, Vector initialPosition) {
		this.stage = stage;
		lastPosition = initialPosition;
		this.position = initialPosition;
		this.viewOffset = Vector.ZERO;
		this.viewBounds = NoShape.INSTANCE;
	}

	public Vector getViewPosition() {
		return getPosition().add(viewOffset);
	}

	public Vector getViewOffset() {
		return viewOffset;
	}

	public void setViewBoundsToRectangle(float x, float y, float w, float h) {
		viewOffset = new Vector(x, y);
		viewBounds = new Rectangle(this::getViewPosition, w, h);
	}

	public void setViewBoundsToCircle(float x, float y, float r) {
		viewOffset = new Vector(x, y);
		viewBounds = new Circle(this::getViewPosition, r);
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

	public Shape getViewBounds() {
		return viewBounds.move(position.add(viewOffset));
	}

	public Shape getViewBounds(Vector position) {
		return viewBounds.move(position.add(viewOffset));
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

	public abstract float getDepth();

	public boolean isInView() {
		if (viewBounds instanceof NoShape) {
			return stage.viewBounds.containsPoint(getPosition());
		} else {
			return getViewBounds().collidesWith(stage.viewBounds);
		}
	}

	public boolean isInView(Vector position) {
		if (viewBounds instanceof NoShape) {
			return stage.viewBounds.containsPoint(position);
		} else {
			return getViewBounds().collidesWith(stage.viewBounds);
		}
	}

	public abstract void draw(Vector position);

	@Override
	public void draw() {
		draw(position);
	}

	@Override
	public int compareTo(Actor other) {
		return Float.compare(getDepth(), other.getDepth());
	}
}
