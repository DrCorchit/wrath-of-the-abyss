package net.drcorchit.zcity;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.drcorchit.zcity.assets.*;
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

	private Skeleton[] skeletons;
	private Skin[] skins;
	private Texture tex;
	private TextureRegion texReg;

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

		skeletons = new Skeleton[5];
		skins = new Skin[5];
		for (int i = 0; i < 5; i++) {
			skeletons[i] = Skeletons.human_female.copy();
			skins[i] = Skins.loadSkin(i + ".json");
		}
		mouse.setH(Gdx.graphics.getHeight());
		tex = new Texture("badlogic.jpg");
		texReg = new TextureRegion(tex);
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

		//draw.drawPrimitive(texReg, new Vector2(300, 500), new Vector2(500, 500), new Vector2(0, 0), new Vector2(250, 300));

		float x = 150;
		float y = 720;
		for (int i = 0; i < 5; i++) {
			Skeleton skeleton = skeletons[i];
			skins[i].draw(skeleton, x, y);
			skeleton.animate(Animations.jogging, 5f);
			//skeleton.draw(x, y);
			x += 150;
		}

		draw.drawLine(0, mouse.y, 1920, mouse.y, 1, Color.GREEN);
		draw.drawLine(mouse.x, 0, mouse.x, 1080, 1, Color.GREEN);
		batch.end();
	}

	@Override
	public void dispose() {
		batch.dispose();
	}
}
