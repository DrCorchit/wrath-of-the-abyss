package net.drcorchit.dungeonraiders;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import net.drcorchit.dungeonraiders.assets.*;
import net.drcorchit.dungeonraiders.assets.animation.AnimationState;
import net.drcorchit.dungeonraiders.assets.animation.Animations;
import net.drcorchit.dungeonraiders.assets.animation.Frame;
import net.drcorchit.dungeonraiders.entities.actors.Block;
import net.drcorchit.dungeonraiders.entities.actors.Player;
import net.drcorchit.dungeonraiders.entities.stages.PhysicsStage;
import net.drcorchit.dungeonraiders.input.KeyboardInfo;
import net.drcorchit.dungeonraiders.input.MouseInfo;
import net.drcorchit.dungeonraiders.utils.Draw;

public class DungeonRaidersGame extends ApplicationAdapter {
	private static DungeonRaidersGame instance;

	public static DungeonRaidersGame getInstance() {
		return instance;
	}

	private PolygonSpriteBatch batch;
	private PhysicsStage stage;
	private Draw draw;
	public final MouseInfo mouse;
	public final KeyboardInfo keyboard;

	public static Draw getDraw() {
		return instance.draw;
	}

	private Skeleton[] skeletons;
	private Skin[] skins;
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

		stage = new PhysicsStage();
		stage.addActor(new Player(stage, Skeletons.human_female, Skins.punk, 500, 500, 30, 220));
		stage.addActor(new Block(stage, 500, 200, 40, 40));
		mouse.setH(Gdx.graphics.getHeight());
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
