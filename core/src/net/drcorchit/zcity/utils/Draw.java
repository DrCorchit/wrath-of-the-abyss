package net.drcorchit.zcity.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;

import javax.annotation.Nonnull;
import java.util.List;

public class Draw {

	//Aids in drawing utilities
	private final Batch batch;
	private final ShapeRenderer shape;

	public Draw(Batch batch) {
		this.batch = batch;
		shape = new ShapeRenderer();
	}

	//For consistency, drawing util functions should have their args ordered like this
	//x, y, (other args...) Color

	public void drawSprite(float x, float y, @Nonnull TextureRegion region) {
		drawSpriteScaled(x, y, region.getRegionWidth(), region.getRegionHeight(), region, Direction.CENTER, Color.WHITE);
	}

	public void drawSprite(float x, float y, TextureRegion texture, Direction align, Color color) {
		drawSpriteScaled(x, y, texture.getRegionWidth(), texture.getRegionHeight(), texture, align, color);
	}

	public void drawSpriteScaled(float x, float y, float w, float h, @Nonnull TextureRegion texture) {
		drawSpriteScaled(x, y, w, h, texture, Direction.CENTER, Color.WHITE);
	}

	//Base case
	public void drawSpriteScaled(float x, float y, float w, float h, @Nonnull TextureRegion texture, Direction align, Color color) {
		Pair<Float, Float> offsets = align.getOffset(w, h);
		x += offsets.key();
		y += offsets.value();

		drawSpriteScaled(texture, x, y, 0, w, h, color);
	}

	public void drawSpriteScaled(@Nonnull TextureRegion texture, float x, float y, float angle, float w, float h, Color color) {
		Color oldColor = batch.getColor();
		batch.setColor(color);
		batch.draw(texture, x, y, 0, 0, w, h, 1, 1, angle);
		batch.setColor(oldColor);
	}

	public void drawLine(float x1, float y1, float x2, float y2, float thickness, Color color) {
		batch.end();
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shape.begin(ShapeRenderer.ShapeType.Filled);
		shape.setColor(color);
		shape.rectLine(x1, y1, x2, y2, thickness);
		shape.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		batch.begin();
	}

	public void drawArrowPolar(float x, float y, float angle, float len, float size, Color color) {
		double arrowAngle = Math.PI / 6;

		float targX = (float) (x + len * Math.cos(Math.toRadians(angle)));
		float targY = (float) (y + len * Math.sin(Math.toRadians(angle)));
		float x1 = (float) (targX - size * Math.cos(angle + arrowAngle));
		float y1 = (float) (targY - size * Math.sin(angle + arrowAngle));
		float x2 = (float) (targX - size * Math.cos(angle - arrowAngle));
		float y2 = (float) (targY - size * Math.sin(angle - arrowAngle));

		batch.end();
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shape.begin(ShapeRenderer.ShapeType.Filled);
		shape.setColor(color);
		shape.line(x, y, targX, targY);
		shape.triangle(targX, targY, x1, y1, x2, y2);
		shape.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		batch.begin();
	}

	public void drawArrowRect(float x, float y, float targX, float targY, float size, Color color) {
		double angle = Math.atan2(targY - y, targX - x);
		double arrowAngle = Math.PI / 6;

		float x1 = (float) (targX - size * Math.cos(angle + arrowAngle));
		float y1 = (float) (targY - size * Math.sin(angle + arrowAngle));
		float x2 = (float) (targX - size * Math.cos(angle - arrowAngle));
		float y2 = (float) (targY - size * Math.sin(angle - arrowAngle));

		batch.end();
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shape.begin(ShapeRenderer.ShapeType.Filled);
		shape.setColor(color);
		shape.line(x, y, targX, targY);
		shape.triangle(targX, targY, x1, y1, x2, y2);
		shape.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		batch.begin();
	}

	public void drawRectangle(float x, float y, float w, float h, Color color) {
		drawRectangle(x, y, w, h, Direction.NORTHEAST, color);
	}

	public void drawRectangle(float x, float y, float w, float h, Direction align, Color color) {
		Pair<Float, Float> offsets = align.getOffset(w, h);

		batch.end();
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shape.begin(ShapeRenderer.ShapeType.Filled);
		shape.setColor(color);
		shape.rect(x + offsets.key(), y + offsets.value(), w, h);
		shape.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		batch.begin();
	}

	public void drawCircle(float x, float y, float radius, Color color) {
		batch.end();
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shape.begin(ShapeRenderer.ShapeType.Filled);
		shape.setColor(color);
		shape.circle(x, y, radius);
		shape.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		batch.begin();
	}

	public void drawRectangleOutline(float x, float y, float w, float h, Direction align, Color color) {
		Pair<Float, Float> offsets = align.getOffset(w, h);
		batch.end();
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shape.begin(ShapeRenderer.ShapeType.Line);
		shape.setColor(color);
		shape.rect(x + offsets.key(), y + offsets.value(), w, h);
		shape.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		batch.begin();
	}

	public Pair<Float, Float> drawText(float x, float y, BitmapFont font, String text) {
		return drawText(x, y, font, text, 0, Direction.CENTER, Color.WHITE);
	}

	public Pair<Float, Float> drawText(float x, float y, BitmapFont font, String text, float width, Direction align, Color color) {
		if (font == null || text == null) return new Pair<>(0f, 0f);
		GlyphLayout layout = new GlyphLayout();
		layout.setText(font, text, color, width, align.getTextAlign(), true);
		float w = layout.width, h = layout.height;
		Pair<Float, Float> offsets = align.getOffset(w, h);
		//x = x + offsets.key();
		y = y + offsets.value() + h;

		font.draw(batch, layout, x, y);
		return new Pair<>(w, h);
	}

	//Calculates the dimensions of the text that would be draw without drawing it
	//Isn't any faster than normal, but avoids unnecessary args (font color, x/y pos, alignment etc)
	public static Pair<Float, Float> calculateDimensions(BitmapFont font, String text, float width) {
		if (font == null || text == null) return new Pair<>(0f, 0f);
		GlyphLayout layout = new GlyphLayout();
		layout.setText(font, text, Color.WHITE, width, Align.left, true);
		float w = layout.width, h = layout.height;
		return new Pair<>(w, h);
	}

	public Pair<Float, Float> drawTextBackground(float x, float y, BitmapFont font, String text, float width, float border, Direction align, Color textColor, Color backColor) {
		Pair<Float, Float> dims = calculateDimensions(font, text, width);
		float borderW = dims.key() + border * 2;
		float borderH = dims.value() + border * 2;
		drawRectangle(x, y, borderW, borderH, align, backColor);
		float textX = x + align.getHoriz() * border;
		float textY = y + align.getVert() * border;
		drawText(textX, textY, font, text, width, align, textColor);
		return dims;
	}

	public static Pair<Float, Float> calculateMaxDimensions(BitmapFont font, List<String> text, float width) {
		float maxW = 0, maxH = 0;
		for (String line : text) {
			Pair<Float, Float> dims = calculateDimensions(font, line, width);
			maxW = Math.max(maxW, dims.key());
			maxH += dims.value();
		}
		return new Pair<>(maxW, maxH);
	}

	public void resize() {
		shape.setProjectionMatrix(batch.getProjectionMatrix());
	}
}
