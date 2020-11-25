package net.drcorchit.zcity;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import net.drcorchit.zcity.assets.*;
import net.drcorchit.zcity.input.KeyboardInfo;
import net.drcorchit.zcity.input.MouseInfo;
import net.drcorchit.zcity.utils.Draw;

public class ZCityGame extends ApplicationAdapter {
	private static ZCityGame instance;

	public static ZCityGame getInstance() {
		return instance;
	}

	private PolygonSpriteBatch batch;
	public Draw draw;
	private final MouseInfo mouse;
	private final KeyboardInfo keyboard;

	public static Draw draw() {
		return instance.draw;
	}

	private Skeleton skeleton;
	private Skin skin;

	public ZCityGame() {
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

		skeleton = Skeletons.human_female.copy();
		skeleton.scale = 1f;
		//skeleton.getJoint("left_shoulder").setAngle(90);
		//skeleton.getJoint("right_shoulder").setAngle(-90);

		skin = Skins.loadSkin("2.json");
		mouse.setH(Gdx.graphics.getHeight());
	}

	public void act() {
		mouse.update();
		keyboard.update();
	}

	@Override
	public void render() {
		act();

		Gdx.gl.glClearColor(0, 1, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();

		float x = 960;
		float y = 540;
		skin.draw(skeleton, x, y);
		//skeleton.draw(x, y);

		float angle = (float) Math.toDegrees(Math.atan2(mouse.y  -y, mouse.x - x));
		//skeleton.getJoint("right_shoulder").setAngle(angle);
		Animations.jogging.apply(skeleton,5f);
		batch.end();
	}

	@Override
	public void dispose() {
		batch.dispose();
	}
}
