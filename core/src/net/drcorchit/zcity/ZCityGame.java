package net.drcorchit.zcity;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import net.drcorchit.zcity.input.KeyboardInfo;
import net.drcorchit.zcity.input.MouseInfo;

public class ZCityGame extends ApplicationAdapter {
	PolygonSpriteBatch batch;
	Texture img;
	Sprite ship = null;
	TextureAtlas atlas = new TextureAtlas();
	private final MouseInfo mouse;
	private final KeyboardInfo keyboard;

	public ZCityGame() {
		super();
		mouse = new MouseInfo();
		keyboard = new KeyboardInfo();
	}

	@Override
	public void create() {
		batch = new PolygonSpriteBatch();
		batch.enableBlending();
		img = new Texture("badlogic.jpg");
		atlas.addRegion("0", new TextureRegion(img));
	}

	public void act() {
		mouse.update();
		keyboard.update();
	}

	@Override
	public void render() {
		act();
		if (ship == null) {
			FrameBuffer surface = new FrameBuffer(Pixmap.Format.RGBA8888, 200, 200, false);
			surface.begin();
			//Gdx.gl.glBlendFuncSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE_MINUS_SRC_ALPHA);
			Gdx.gl.glClearColor(1, 0, 1, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			batch.begin();
			atlas.createSprite("0").draw(batch);
			batch.end();
			surface.end();

			//Copy the framebuffer to a sprite
			ship = new Sprite(surface.getColorBufferTexture());
		}

		Gdx.gl.glClearColor(.5f, 1, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		//batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);
		ship.setPosition(mouse.x, mouse.y);
		ship.draw(batch);
		batch.end();
	}

	@Override
	public void dispose() {
		batch.dispose();
		img.dispose();
	}
}
