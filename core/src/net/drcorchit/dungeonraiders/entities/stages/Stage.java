package net.drcorchit.dungeonraiders.entities.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import net.drcorchit.dungeonraiders.entities.Entity;
import net.drcorchit.dungeonraiders.entities.actors.Actor;

import java.util.ArrayList;
import java.util.Collections;

public abstract class Stage extends Entity {

	public static final float EXPECTED_DELTA_TIME = 1 / 30f;

	private final ArrayList<Actor<?>> actors = new ArrayList<>();

	public void addActor(Actor<?> actor) {
		actors.add(actor);
	}

	public void destroyActor(Actor<?> actor) {
		actors.remove(actor);
	}

	public void act() {
		float delta = Gdx.graphics.getDeltaTime();
		System.out.println(delta);
		float factor = Math.min(EXPECTED_DELTA_TIME/delta, 1);
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
		actors.forEach(Actor::draw);
		getBatch().end();
	}
}
