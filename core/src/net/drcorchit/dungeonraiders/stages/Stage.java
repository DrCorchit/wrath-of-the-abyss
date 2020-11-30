package net.drcorchit.dungeonraiders.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import net.drcorchit.dungeonraiders.DungeonRaidersGame;
import net.drcorchit.dungeonraiders.actors.Actor;
import net.drcorchit.dungeonraiders.drawing.Draw;
import net.drcorchit.dungeonraiders.drawing.RenderInstruction;
import net.drcorchit.dungeonraiders.drawing.shapes.Rectangle;
import net.drcorchit.dungeonraiders.utils.Vector;

import java.util.ArrayList;
import java.util.Collections;

public abstract class Stage {

	public static final float EXPECTED_DELTA_TIME = 1 / 60f;

	public final Draw draw;
	private final ArrayList<Actor<?>> actors = new ArrayList<>();
	private float fps;
	private Vector viewPosition;
	//vector from view pos to view center
	private final Vector viewOffset;
	public final Rectangle viewBounds;

	protected Stage() {
		this.draw = DungeonRaidersGame.getDraw();
		viewPosition = new Vector(0, 0);
		viewBounds = new Rectangle(this::getViewCenter,
				Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		viewOffset = new Vector(viewBounds.width / 2, viewBounds.height / 2);
	}

	public float getFps() {
		return fps;
	}

	public Vector getViewPosition() {
		return viewPosition;
	}

	public Vector getViewOffset() {
		return viewOffset;
	}

	public void setViewPosition(Vector v) {
		viewPosition = v;
	}

	public void setViewCenter(Vector center) {
		setViewPosition(center.subtract(viewOffset));
	}

	public Vector getViewCenter() {
		return viewPosition.add(viewOffset);
	}

	public void addActor(Actor<?> actor) {
		actors.add(actor);
	}

	public void destroyActor(Actor<?> actor) {
		actors.remove(actor);
	}

	public void act() {
		float delta = Gdx.graphics.getDeltaTime();
		float factor = Math.min(delta / EXPECTED_DELTA_TIME, 1.1f);
		fps = 1 / delta;
		DungeonRaidersGame.getInstance().debugInfoMap.put("fps", getFps());
		act(factor);
	}

	public void act(float factor) {
		actors.forEach(actor -> actor.act(factor));
		actors.forEach(Actor::updateLastPosition);
	}

	public void draw() {
		ArrayList<RenderInstruction> instructions = new ArrayList<>();

		for (Actor<?> actor : actors) {
			if (actor.isInView()) {
				Vector adjustedPos = actor.getPosition().subtract(viewPosition);
				//actor.getViewBounds(adjustedPos).draw(Color.GREEN);
				instructions.addAll(actor.draw(adjustedPos));
			}
		}

		Collections.sort(instructions);

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		draw.batch.begin();
		instructions.forEach(RenderInstruction::draw);
		draw.batch.end();
	}

	public Iterable<Actor<?>> getActors() {
		return actors;
	}
}
