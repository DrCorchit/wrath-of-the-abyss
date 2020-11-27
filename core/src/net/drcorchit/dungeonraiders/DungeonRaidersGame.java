package net.drcorchit.dungeonraiders;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import net.drcorchit.dungeonraiders.assets.*;
import net.drcorchit.dungeonraiders.assets.animation.AnimationState;
import net.drcorchit.dungeonraiders.assets.animation.Animations;
import net.drcorchit.dungeonraiders.entities.actors.Actor;
import net.drcorchit.dungeonraiders.entities.actors.Block;
import net.drcorchit.dungeonraiders.entities.actors.Player;
import net.drcorchit.dungeonraiders.entities.stages.DungeonStage;
import net.drcorchit.dungeonraiders.input.KeyboardInfo;
import net.drcorchit.dungeonraiders.input.MouseInfo;
import net.drcorchit.dungeonraiders.utils.Draw;
import net.drcorchit.dungeonraiders.utils.Vector;

public class DungeonRaidersGame extends ApplicationAdapter {
	private static DungeonRaidersGame instance;

	public static DungeonRaidersGame getInstance() {
		return instance;
	}

	private PolygonSpriteBatch batch;
	private DungeonStage stage;
	private Draw draw;
	public final MouseInfo mouse;
	public final KeyboardInfo keyboard;

	public static Draw getDraw() {
		return instance.draw;
	}

	private Skeleton skeleton;
	private Skin skin;
	private AnimationState state;

	public DungeonRaidersGame() {
		super();
		mouse = new MouseInfo();
		keyboard = new KeyboardInfo();
		instance = this;
	}

	@Override
	public void create() {
		batch = new PolygonSpriteBatch();
		batch.enableBlending();
		draw = new Draw(batch);
		LocalAssets.getInstance().load();

		stage = new DungeonStage();
		stage.addActor(new Player(stage, Skeletons.human_female, Skins.punk, 500, 500, 40, 220));

		for (int i = 0; i < 30; i++) {
			float x = 500 + i * 40;
			stage.addActor(new Block(stage, x, 200, 40, 40));
		}
		mouse.setH(Gdx.graphics.getHeight());

		skeleton = Skeletons.human_female;
		skin = Skins.purple;
		AnimationState state = new AnimationState(Animations.jog);
		stage.addActor(new Actor(stage, 1200, 540) {
			@Override
			public void draw(Vector position) {
				skin.draw(skeleton, position);
			}

			@Override
			public void act(float delta) {
				state.getNextFrame(delta).apply(skeleton, 5f);
			}
		});
	}

	public void act() {
		mouse.update();
		keyboard.update();
		stage.act();
	}

	public void draw() {
		stage.draw();
	}

	@Override
	public void render() {
		act();
		draw();
	}

	@Override
	public void dispose() {
		batch.dispose();
	}
}
