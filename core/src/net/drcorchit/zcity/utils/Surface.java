package net.drcorchit.zcity.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

public class Surface {

	private final Batch batch;
	private final FrameBuffer buffer;
	private final int width, height;

	public Surface(Batch batch, int width, int height) {
		this.batch = batch;
		this.width = width;
		this.height = height;
		buffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
	}

	public void clear() {
		clear(Color.BLACK);
	}

	public void clear(Color color) {
		Gdx.gl.glClearColor(color.r, color.g, color.b, color.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	public void begin() {
		buffer.begin();
	}

	public void end() {
		buffer.end();
	}

	public void drawTexture(int x, int y, TextureRegion texture) {
		float w = texture.getRegionWidth();
		float h = texture.getRegionHeight();
		y = height - y;
		batch.draw(texture, x, y, 0, 0, w, h, 1, -1, 0);
	}

	public Sprite createSprite() {
		Texture texture = buffer.getColorBufferTexture();
		Sprite sprite = new Sprite(texture, width, height);
		float yOffset = -Gdx.graphics.getHeight();
		sprite.setOrigin(0, yOffset);
		return sprite;
	}

	public void draw(float x, float y) {
		Texture texture = buffer.getColorBufferTexture();
		TextureRegion output = new TextureRegion(texture, width, height);
		batch.draw(output, x, y + Gdx.graphics.getHeight());
	}
}
