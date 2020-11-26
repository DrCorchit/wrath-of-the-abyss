package net.drcorchit.zcity;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import net.drcorchit.zcity.assets.*;
import net.drcorchit.zcity.assets.animation.Animation;
import net.drcorchit.zcity.assets.animation.Animations;
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

	private Skeleton skeleton, skeleton2;
	private Skin skin1, skin2;

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
		skeleton2 = Skeletons.human_female.copy();
		skeleton2.scale = 1f;
		//skeleton2.flipped = true;
		//skeleton.getJoint("left_shoulder").setAngle(90);
		//skeleton.getJoint("right_shoulder").setAngle(-90);

		skin1 = Skins.loadSkin("3.json");
		skin2 = Skins.loadSkin("4.json");
		mouse.setH(Gdx.graphics.getHeight());
	}

	public void act() {
		mouse.update();
		keyboard.update();
	}

	@Override
	public void render() {
		act();

		Gdx.gl.glClearColor(.2f, .2f, .2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();

		float x = 960;
		float y = 720;
		skin1.draw(skeleton, x, y);
		skin2.draw(skeleton2, x + 200, y);
		//skeleton.draw(x, y);

		draw.drawLine(0, mouse.y, 1920, mouse.y, 1, Color.GREEN);
		draw.drawLine(mouse.x, 0, mouse.x, 1080, 1, Color.GREEN);
		float angle = (float) Math.toDegrees(Math.atan2(mouse.y  -y, mouse.x - x));
		//skeleton.getJoint("right_shoulder").setAngle(angle);
		//skeleton.animate(Animations.jogging, 5f);
		//skeleton2.animate(Animations.jogging, 5f);
		batch.end();
	}

	@Override
	public void dispose() {
		batch.dispose();
	}
}
