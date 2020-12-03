package net.drcorchit.dungeonraiders.actors;

import net.drcorchit.dungeonraiders.drawing.RenderInstruction;
import net.drcorchit.dungeonraiders.drawing.shapes.Rectangle;
import net.drcorchit.dungeonraiders.stages.Stage;
import net.drcorchit.dungeonraiders.utils.Vector;

import java.util.Collection;

public interface Actor<T extends Stage>  {

	T getStage();

	//remove actor from parent stage
	default void destroy() {
		getStage().destroyActor(this);
	}

	Rectangle getViewBounds();

	//returns the center of the viewBounds rectangle.
	Vector getViewPosition();

	void setViewBounds(float x, float y, float w, float h);

	default boolean isInView() {
		return getViewBounds().collidesWith(getStage().viewBounds);
	}

	Vector getLastPosition();

	void updateLastPosition();

	Vector getPosition();

	//attempt to set the location of the actor. returns whether the operation succeeded
	boolean setPosition(Vector position);

	default boolean setPositionRelative(Vector position) {
		return setPosition(getPosition().add(position));
	}

	void act(float delta);

	Collection<RenderInstruction> draw(Vector position);

	default Collection<RenderInstruction> draw() {
		return draw(getPosition());
	}
}
