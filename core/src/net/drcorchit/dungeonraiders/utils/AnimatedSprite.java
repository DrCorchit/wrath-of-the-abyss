package net.drcorchit.dungeonraiders.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import javax.annotation.Nonnull;

/**
 * Handles locally-stored sprites possibly having multiple frames/animation
 * Implements BaseDrawable but sets borders to 0
 */
public class AnimatedSprite extends BaseDrawable {
	private float speed, index, originX, originY;
	private final Color blend;
	private SpriteList frames;

	public AnimatedSprite(@Nonnull SpriteList frames) {
		this.frames = frames;
		speed = frames.length() < 2 ? 0 : 1;
		index = 0;
		blend = Color.WHITE.cpy();
		resetDims();
		originX = frames.originX;
		originY = frames.originY;
	}

	public void resetDims() {
		setLeftWidth(0);
		setRightWidth(0);
		setTopHeight(0);
		setBottomHeight(0);
		if (size() == 0) {
			setMinWidth(0);
			setMinHeight(0);
		} else {
			setMinWidth(frames.get(0).getRegionWidth());
			setMinHeight(frames.get(0).getRegionHeight());
		}
	}

	public float getOriginX() {
		return originX;
	}

	public float getOriginY() {
		return originY;
	}

	public Color getBlend() {
		return blend;
	}

	private TextureRegion getNextFrame() {
		TextureRegion output = frames.get((int) index);
		index += speed;
		if (index < 0) {
			index += frames.length();
		} else if (index >= frames.length()) {
			index -= frames.length();
		}

		return output;
	}

	public void draw(Batch batch, float x, float y) {
		draw(batch, x, y, originX, originY, getMinWidth(), getMinHeight(), 1, 1, 0);
	}

	public void draw(Batch batch, float x, float y, float rotation) {
		draw(batch, x, y, originX, originY, getMinWidth(), getMinHeight(), 1, 1, rotation);
	}

	@Override
	public void draw(Batch batch, float x, float y, float width, float height) {
		drawStretched(batch, x, y, width, height, 0);
	}

	public void drawStretched(Batch batch, float x, float y, float width, float height, float rotation) {
		float xOff = originX * (width / getMinWidth());
		float yOff = originY * (height / getMinHeight());

		draw(batch, x, y, xOff, yOff, width, height, 1, 1, rotation);
	}

	public void drawScaled(Batch batch, float x, float y, float xScale, float yScale, float rotation) {
		draw(batch, x, y, originX, originY, getMinWidth(), getMinHeight(), xScale, yScale, rotation);
	}

	public void draw(Batch batch, float x, float y, float originX, float originY, float width, float height, float scaleX,
					 float scaleY, float rotation) {
		Color temp = batch.getColor().cpy();
		batch.setColor(blend);
		getCurrentTexture().draw(batch, x - originX, y - originY, originX, originY, width, height, scaleX, scaleY, rotation);
		batch.setColor(temp);
	}

	public void drawTiled(Batch batch, float x, float y) {
		TextureRegion region = getCurrentFrame();
		float w = region.getRegionWidth();
		float h = region.getRegionHeight();
		float leftX = (float) (MathUtils.mod(x, w) - w);
		float bottomY = (float) (MathUtils.mod(y, h) - h);
		float rightX = Gdx.graphics.getWidth();
		float topY = Gdx.graphics.getHeight();

		for (float texX = leftX; texX < rightX; texX += w) {
			for (float texY = bottomY; texY < topY; texY += h) {
				batch.draw(region, texX, texY);
			}
		}
	}

	public void setOffset(float xOffset, float yOffset) {
		this.originX = xOffset;
		this.originY = yOffset;
	}

	public int size() {
		return frames.length();
	}

	public void setOffsetCentered() {
		setOffset(getMinWidth() / 2, getMinHeight() / 2);
	}

	public void setBlend(Color color) {
		blend.set(color);
	}

	public void setAlpha(float alpha) {
		blend.a = alpha;
	}

	public float getIndex() {
		return index;
	}

	public void setIndex(float index) {
		this.index = index;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public SpriteList getFrames() {
		return frames;
	}

	public void setFrames(SpriteList frames) {
		this.frames = frames;
		index = 0;
	}

	public TextureRegion getCurrentFrame() {
		return frames.get(index);
	}

	public TextureRegionDrawable getCurrentTexture() {
		return new TextureRegionDrawable(frames.get(index));
	}

	public void updateFrame(float factor) {
		index += speed * factor;
		index = (float) MathUtils.mod(index, frames.length());
	}
}
