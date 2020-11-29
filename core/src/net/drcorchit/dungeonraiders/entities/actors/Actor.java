package net.drcorchit.dungeonraiders.entities.actors;

import net.drcorchit.dungeonraiders.drawing.RenderInstruction;
import net.drcorchit.dungeonraiders.entities.Entity;
import net.drcorchit.dungeonraiders.entities.stages.Stage;
import net.drcorchit.dungeonraiders.drawing.shapes.Rectangle;
import net.drcorchit.dungeonraiders.utils.Vector;

import java.util.Collection;

public abstract class Actor<T extends Stage> extends Entity {

	//stage is generic because certain actors depend on stage-specific properties
	protected final T stage;
	private Vector lastPosition, position, viewOffset;
	private Rectangle viewBounds;

	public Actor(T stage, Vector initialPosition) {
		this.stage = stage;
		lastPosition = initialPosition;
		this.position = initialPosition;
		this.viewOffset = Vector.ZERO;
		this.viewBounds = new Rectangle(this::getPosition, 100, 100);
	}

	public Vector getViewPosition() {
		return getPosition().add(viewOffset);
	}

	public void setViewBounds(float x, float y, float w, float h) {
		viewOffset = new Vector(x, y);
		viewBounds = new Rectangle(this::getViewPosition, w, h);
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

	public Rectangle getViewBounds() {
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

	public boolean isInView() {
		return getViewBounds().collidesWith(stage.viewBounds);
	}

	public abstract void act(float delta);

	public abstract Collection<RenderInstruction> draw(Vector position);

	public Collection<RenderInstruction> draw() {
		return draw(position);
	}
}
