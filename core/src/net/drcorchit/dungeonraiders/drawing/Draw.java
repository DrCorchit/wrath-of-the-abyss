package net.drcorchit.dungeonraiders.drawing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import net.drcorchit.dungeonraiders.utils.Direction;
import net.drcorchit.dungeonraiders.utils.Pair;
import net.drcorchit.dungeonraiders.utils.Vector;

import javax.annotation.Nonnull;
import java.util.List;

public class Draw {

	private static final String VERTEX_SHADER = "attribute vec4 a_position;\n" +
			"attribute vec4 a_color;\n" +
			"attribute vec2 a_texCoord;\n" +
			"uniform mat4 u_worldView;\n" +
			"varying vec4 v_color;\n" +
			"varying vec2 v_texCoords;\n" +
			"void main()\n" +
			"{\n" +
			"   v_color =  vec4(1, 1, 1, 1);\n" +
			"   v_texCoords = a_texCoord;\n" +
			"   gl_Position =  u_worldView * a_position;\n" +
			"}\n";

	private static final String FRAGMENT_SHADER = "#ifdef GL_ES\n" +
			"precision mediump float;\n" +
			"#endif\n" +
			"varying vec4 v_color;\n" +
			"varying vec2 v_texCoords;\n" +
			"uniform sampler2D u_texture;\n" +
			"void main()\n" +
			"{\n" +
			"vec4 texColor = texture2D(u_texture, v_texCoords);\n" +
			" gl_FragColor = texColor;\n" +
			"}";

	private static final ShaderProgram shader = new ShaderProgram(VERTEX_SHADER, FRAGMENT_SHADER);
	private static final Viewport view = new ScreenViewport();

	//Aids in drawing utilities
	public final PolygonSpriteBatch batch;
	private final ShapeRenderer shape;

	public Draw(PolygonSpriteBatch batch) {
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
		x += offsets.key;
		y += offsets.val;

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
		shape.rect(x + offsets.key, y + offsets.val, w, h);
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
		shape.rect(x + offsets.key, y + offsets.val, w, h);
		shape.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
		batch.begin();
	}

	public void drawPrimitive(Texture texture, Vector p1, Vector p2, Vector p3, Vector p4) {
		drawPrimitive(new TextureRegion(texture), p1, p2, p3, p4);
	}

	//FIXME: https://stackoverflow.com/questions/28290428/implementing-trapezoidal-sprites-in-libgdx
	//1, 2
	//3, 4
	public void drawPrimitive(TextureRegion region, Vector p1, Vector p2, Vector p3, Vector p4) {
		float[] vertices = new float[20];
		float bottomWidth = (p1.x - p2.x) / (p3.x - p4.x);

		float color = Color.WHITE.toFloatBits();
		int index = 0;

		vertices[index++] = p1.x;
		vertices[index++] = p1.y;
		vertices[index++] = color;
		vertices[index++] = 0;
		vertices[index++] = 0;

		vertices[index++] = p2.x;
		vertices[index++] = p2.y;
		vertices[index++] = color;
		vertices[index++] = 1;
		vertices[index++] = 0;

		vertices[index++] = p4.x;
		vertices[index++] = p4.y;
		vertices[index++] = color;
		vertices[index++] = 1;
		vertices[index++] = 1;

		vertices[index++] = p3.x;
		vertices[index++] = p3.y;
		vertices[index++] = color;
		vertices[index++] = 0;
		vertices[index++] = 1;

		batch.draw(region.getTexture(), vertices, 0, 20);
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
		y = y + offsets.val + h;

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
		float borderW = dims.key + border * 2;
		float borderH = dims.val + border * 2;
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
			maxW = Math.max(maxW, dims.key);
			maxH += dims.val;
		}
		return new Pair<>(maxW, maxH);
	}

	public void resize() {
		shape.setProjectionMatrix(batch.getProjectionMatrix());
	}
}
