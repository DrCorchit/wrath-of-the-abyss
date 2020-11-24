package net.drcorchit.zcity;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import net.drcorchit.zcity.assets.LocalAssets;
import net.drcorchit.zcity.assets.Skeleton;
import net.drcorchit.zcity.assets.Skin;
import net.drcorchit.zcity.assets.Sprites;
import net.drcorchit.zcity.input.KeyboardInfo;
import net.drcorchit.zcity.input.MouseInfo;
import net.drcorchit.zcity.utils.Draw;
import net.drcorchit.zcity.utils.Surface;

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

	Texture img;
	Surface surface;
	Sprite ship = null;
	TextureAtlas atlas = new TextureAtlas();
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

		surface = new Surface(batch, 400, 400);
		img = new Texture("badlogic.jpg");

		skeleton = Skeleton.newFemaleSkeleton();
		skin = Skin.newFemaleSkin(skeleton);
		mouse.setH(Gdx.graphics.getHeight());
		//atlas.addRegion("0", new TextureRegion(img));
	}

	public void act() {
		mouse.update();
		keyboard.update();
	}

	public static void drawTextureToSurface(Texture tex, FrameBuffer surface) {
		float xScale = Gdx.graphics.getWidth() * 1.0f / surface.getWidth();
		float yScale = Gdx.graphics.getHeight() * 1.0f / surface.getHeight();
		float width = tex.getWidth() * xScale;
		float height = tex.getHeight() * yScale;
		instance.batch.draw(instance.img, 0, 0, width, height);
	}

	@Override
	public void render() {
		act();
		if (ship == null) {
			//FrameBuffer surface = new FrameBuffer(Pixmap.Format.RGBA8888, 200, 200, false);
			//surface.begin();
			//Gdx.gl.glBlendFuncSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
			//Gdx.gl.glClearColor(1, 0, 1, 1);
			//Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			//batch.begin();
			//atlas.createSprite("0").draw(batch);
			//batch.draw(img, 0, 0);
			//drawTextureToSurface(img, surface);
			//batch.end();
			//surface.end();

			//surface = new Surface(batch, 400, 400);
			surface.begin();
			surface.clear();
			//surface.drawTexture(0, 0, new TextureRegion(img));
			//TextureRegionDrawable drawable = new TextureRegionDrawable(img);
			TextureRegion drawable = new TextureRegion(img);
			surface.drawToSelf(drawable, 20, 20, 0, 0, 1, 1, 0, Color.WHITE);
			surface.drawToSelf(drawable, 20, 20, 128, 128, .5f, .5f, 45, Color.WHITE);
			surface.drawToSelf(drawable, 148, 148, 128, 128, .1f, .5f, 45, Color.WHITE);
			surface.drawToSelf(drawable, 148, 148, 128, 128, .5f, .1f, -90, Color.WHITE);
			surface.end();

			//Copy the framebuffer to a sprite
			//ship = surface.createSprite();
		}

		Gdx.gl.glClearColor(0, 1, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		//batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);
		//ship.setOriginBasedPosition(mouse.x, mouse.y);
		//ship.draw(batch);
		//surface.draw(mouse.x, mouse.y);
		//batch.draw(img, 0, 0);
		skin.draw(mouse.x + 50, mouse.y);
		skeleton.draw(mouse.x + 50, mouse.y);
		skeleton.scale = 4;
		batch.end();

		//skeleton.root.incrementAngle(1);
		skeleton.getJoint("left_shoulder").incrementAngle(.5f);
		skeleton.getJoint("left_elbow").incrementAngle(.5f);
	}

	@Override
	public void dispose() {
		batch.dispose();
		img.dispose();
	}
}
