package net.drcorchit.dungeonraiders.drawing.shapes;

import com.badlogic.gdx.graphics.Color;
import net.drcorchit.dungeonraiders.utils.Vector;

public class NoShape implements Shape {

	public static final NoShape INSTANCE = new NoShape();

	private NoShape() {

	}

	@Override
	public Vector getPosition() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsPoint(Vector point) {
		return false;
	}

	@Override
	public boolean collidesWith(Shape other) {
		return false;
	}

	@Override
	public float getArea() {
		return 0;
	}

	@Override
	public float getPerimeter() {
		return 0;
	}

	@Override
	public float getMinimalRadius() {
		return 0;
	}

	@Override
	public float getMaximalRadius() {
		return 0;
	}

	@Override
	public Shape move(Vector offset) {
		return INSTANCE;
	}

	@Override
	public Shape scale(float scale) {
		return INSTANCE;
	}

	@Override
	public void draw(Color color) {
		//No-op
	}
}
