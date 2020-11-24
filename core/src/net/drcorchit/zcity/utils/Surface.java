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
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

public class Surface {
	private final Batch batch;
	private final FrameBuffer buffer;
	private final int width, height;
	private boolean batchWasDrawing;

	public Surface(Batch batch) {
		this(batch, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	public Surface(Batch batch, int width, int height) {
		this.batch = batch;
		this.width = width;
		this.height = height;
		buffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
	}

	public Surface clear() {
		return clear(Color.BLACK);
	}

	public Surface clear(Color color) {
		Gdx.gl.glClearColor(color.r, color.g, color.b, color.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		return this;
	}

	public Surface begin() {
		if (batch.isDrawing()) {
			batch.end();
			batchWasDrawing = true;
		} else {
			batchWasDrawing = false;
		}
		buffer.begin();
		batch.begin();
		return this;
	}

	public void end() {
		batch.end();
		buffer.end();
		if (batchWasDrawing) {
			batch.begin();
		}
	}

	@CanIgnoreReturnValue
	public Surface drawTexture(int x, int y, TextureRegion texture) {
		float w = texture.getRegionWidth();
		float h = texture.getRegionHeight();
		y = height - y;
		batch.draw(texture, x, y, 0, 0, w, h, 1, -1, 0);
		return this;
	}

	@CanIgnoreReturnValue
	public Surface drawToSelf(TextureRegion region, float x, float y, int originX, int originY, float scaleX, float scaleY, float angle, Color color) {
		float ox = originX * scaleX;
		float oy = originY * scaleY;
		if (angle != 0) {
			float radius = (float) MathUtils.distance(0, 0, ox, oy);
			float angleOfRotation = (float) Math.toRadians(angle);
			float angleToOrigin = (float) Math.atan2(oy, ox);
			ox = (float) (radius * Math.cos(angleOfRotation + angleToOrigin));
			oy = (float) (radius * Math.sin(angleOfRotation + angleToOrigin));
		}

		float w = region.getRegionWidth();
		float h = region.getRegionHeight();
		//float w = drawable.getMinWidth();
		//float h = drawable.getMinHeight();
		float actualX = x - ox;
		float actualY = this.height - y + oy;

		Color temp = batch.getColor().cpy();
		batch.setColor(color);
		//drawable.draw(batch, actualX, actualY, 0, 0, w, h, scaleX, -scaleY, -angle);
		batch.draw(region, actualX, actualY, 0, 0, width, height, scaleX, -scaleY, -angle);
		batch.setColor(temp);
		return this;
	}

	public Sprite createSprite() {
		Texture texture = buffer.getColorBufferTexture();
		Sprite sprite = new Sprite(texture, width, height);
		float yOffset = -Gdx.graphics.getHeight();
		sprite.setOrigin(0, yOffset);
		return sprite;
	}

	public void draw(float x, float y) {
		draw(x, y, null);
	}

	public void draw(float x, float y, float alpha) {
		draw(x, y, new Color(1, 1, 1, alpha));
	}

	public void draw(float x, float y, Color blend) {
		Texture texture = buffer.getColorBufferTexture();
		TextureRegion output = new TextureRegion(texture, width, height);

		if (blend != null && !blend.equals(batch.getColor())) {
			Color oldColor = batch.getColor().cpy();
			batch.setColor(blend);
			batch.draw(output, x, y + Gdx.graphics.getHeight());
			batch.setColor(oldColor);
		} else {
			batch.draw(output, x, y + Gdx.graphics.getHeight());
		}
	}
}