package net.drcorchit.dungeonraiders.entities.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import net.drcorchit.dungeonraiders.entities.Entity;
import net.drcorchit.dungeonraiders.entities.actors.Actor;
import net.drcorchit.dungeonraiders.shapes.Rectangle;
import net.drcorchit.dungeonraiders.utils.Vector;

import java.util.ArrayList;
import java.util.Collections;

public abstract class Stage extends Entity {

	public static final float VIEW_BUFFER = 100;
	public static final float EXPECTED_DELTA_TIME = 1 / 60f;

	private final ArrayList<Actor<?>> actors = new ArrayList<>();
	private Vector viewPosition, viewDims;

	protected Stage() {
		viewPosition = new Vector(0, 0);
		viewDims = new Vector(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	public Vector getViewPosition() {
		return viewPosition;
	}

	public void centerViewOn(Vector v) {
		viewPosition = v.subtract(viewDims.multiply(.5f));
	}

	public Vector getViewCenter() {
		return viewPosition.add(viewDims.multiply(.5f));
	}

	public void addActor(Actor<?> actor) {
		actors.add(actor);
	}

	public void destroyActor(Actor<?> actor) {
		actors.remove(actor);
	}

	public void act() {
		float delta = Gdx.graphics.getDeltaTime();
		float factor = Math.min(delta/EXPECTED_DELTA_TIME, 1.1f);
		float fps = 1/delta;
		if (Math.abs(delta/EXPECTED_DELTA_TIME) > 1.05f) {
			System.out.println("FPS: " + fps + " factor: "+factor);
		}
		act(factor);
	}

	@Override
	public void act(float factor) {
		actors.forEach(actor -> actor.act(factor));
		actors.forEach(Actor::updateLastPosition);
		Collections.sort(actors);
	}

	@Override
	public void draw() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		getBatch().begin();
		for (Actor<?> actor : actors) {
			if (isActorInView(actor)) {
				Vector adjustedPos = actor.getPosition().subtract(viewPosition);
				actor.draw(adjustedPos);
			}
		}
		getBatch().end();
	}

	public Iterable<Actor<?>> getActors() {
		return actors;
	}

	//TODO make this an instance method of Actor
	public boolean isActorInView(Actor<?> actor) {
		return new Rectangle(this::getViewCenter, viewDims.x + VIEW_BUFFER, viewDims.y + VIEW_BUFFER).containsPoint(actor.getPosition());
	}
}
