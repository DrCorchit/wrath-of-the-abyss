package net.drcorchit.dungeonraiders.drawing;

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
import net.drcorchit.dungeonraiders.DungeonRaidersGame;
import net.drcorchit.dungeonraiders.utils.MathUtils;

import javax.annotation.Nullable;

public class Surface {

	private final Batch batch;
	private final FrameBuffer buffer;
	private final int width, height;
	private boolean batchWasDrawing;

	public Surface() {
		this(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	public Surface(int width, int height) {
		this.batch = DungeonRaidersGame.getDraw().batch;
		this.width = width;
		this.height = height;
		buffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
	}

	public Surface clear() {
		return clear(new Color(0, 0, 0, 0));
	}

	public Surface clear(Color color) {
		Gdx.gl.glClearColor(color.r, color.g, color.b, color.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		return this;
	}

	@CanIgnoreReturnValue
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
	public Surface drawTexture(int x, int y, Texture texture) {
		return draw(new TextureRegionDrawable(texture), x, y);
	}

	@CanIgnoreReturnValue
	public Surface drawTexture(TextureRegion texture, int x, int y) {
		return draw(new TextureRegionDrawable(texture), x, y);
	}

	public Surface draw(TextureRegionDrawable drawable, float x, float y) {
		return drawScaled(drawable, x, y, 0, 0, 1, 1, 0, Color.WHITE);
	}

	public Surface draw(AnimatedSprite sprite, float x, float y) {
		return drawScaled(sprite.getCurrentTexture(), x, y, sprite.getOriginX(), sprite.getOriginY(), 1, 1, 0, sprite.getBlend());
	}

	@CanIgnoreReturnValue
	public Surface drawScaled(TextureRegionDrawable drawable, float x, float y, float originX, float originY, float scaleX, float scaleY, float angle, Color color) {
		float ox = originX * scaleX;
		float oy = originY * scaleY;
		if (angle != 0) {
			float radius = (float) MathUtils.distance(0, 0, ox, oy);
			float angleOfRotation = (float) Math.toRadians(angle);
			float angleToOrigin = (float) Math.atan2(oy, ox);
			ox = (float) (radius * Math.cos(angleOfRotation + angleToOrigin));
			oy = (float) (radius * Math.sin(angleOfRotation + angleToOrigin));
		}

		//float w = region.getRegionWidth();
		//float h = region.getRegionHeight();
		float w = drawable.getMinWidth();
		float h = drawable.getMinHeight();
		float actualX = x - ox;
		float actualY = y - oy;

		Color temp = batch.getColor().cpy();
		batch.setColor(color);
		drawable.draw(batch, actualX, actualY, 0, 0, w, h, scaleX, scaleY, angle);
		//batch.draw(region, actualX, actualY, 0, 0, width, height, scaleX, -scaleY, -angle);
		batch.setColor(temp);
		return this;
	}

	public Sprite createSprite() {
		Texture texture = buffer.getColorBufferTexture();
		Sprite output = new Sprite(texture, width, height);
		//flips the sprite along the y axis, to correct for
		//frame buffer's inverted y axis
		output.setScale(1, -1);
		return output;
	}

	public void draw(float x, float y) {
		draw(x, y, null);
	}

	public void draw(float x, float y, float alpha) {
		draw(x, y, new Color(1, 1, 1, alpha));
	}

	public void draw(float x, float y, @Nullable Color blend) {
		Sprite temp = createSprite();
		temp.setPosition(x, y);
		if (blend != null) temp.setColor(blend);
		temp.draw(batch);
	}
}