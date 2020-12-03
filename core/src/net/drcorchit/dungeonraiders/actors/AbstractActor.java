package net.drcorchit.dungeonraiders.actors;

import net.drcorchit.dungeonraiders.drawing.shapes.Rectangle;
import net.drcorchit.dungeonraiders.stages.Stage;
import net.drcorchit.dungeonraiders.utils.Vector;

public abstract class AbstractActor<T extends Stage> implements Actor<T> {

	//stage is generic because certain actors depend on stage-specific properties
	private final T stage;
	private Vector lastPosition, position, viewOffset;
	private Rectangle viewBounds;

	public AbstractActor(T stage, Vector initialPosition) {
		this.stage = stage;
		lastPosition = initialPosition;
		this.position = initialPosition;
		this.viewOffset = Vector.ZERO;
		this.viewBounds = new Rectangle(this::getPosition, 100, 100);
	}

	@Override
	public T getStage() {
		return stage;
	}

	@Override
	public Rectangle getViewBounds() {
		return viewBounds.move(position.add(viewOffset));
	}

	@Override
	public void setViewBounds(float x, float y, float w, float h) {
		viewOffset = new Vector(x, y);
		viewBounds = new Rectangle(this::getViewPosition, w, h);
	}

	@Override
	public Vector getViewPosition() {
		return getPosition().add(viewOffset);
	}

	@Override
	public Vector getLastPosition() {
		return lastPosition;
	}

	@Override
	public void updateLastPosition() {
		lastPosition = position;
	}

	@Override
	public Vector getPosition() {
		return position;
	}

	//attempt to set the location of the actor. returns whether the operation succeeded
	@Override
	public boolean setPosition(Vector position) {
		this.position = position;
		return true;
	}


}
