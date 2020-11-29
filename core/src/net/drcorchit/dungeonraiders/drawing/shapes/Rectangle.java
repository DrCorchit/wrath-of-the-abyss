package net.drcorchit.dungeonraiders.drawing.shapes;

import com.badlogic.gdx.graphics.Color;
import net.drcorchit.dungeonraiders.DungeonRaidersGame;
import net.drcorchit.dungeonraiders.utils.MathUtils;
import net.drcorchit.dungeonraiders.utils.Vector;

import java.util.function.Supplier;

public class Rectangle extends AbstractShape {

	public final float width, height;

	public Rectangle(Supplier<Vector> locationGetter, float width, float height) {
		super(locationGetter);
		this.width = width;
		this.height = height;
	}

	@Override
	public boolean containsPoint(Vector point) {
		Vector relativePos = point.subtract(getPosition());
		float w2 = width / 2, h2 = height / 2;
		return relativePos.x > -w2 && relativePos.x < w2 && relativePos.y > -h2 && relativePos.y < h2;
	}

	@Override
	public boolean collidesWith(Shape other) {
		if (other == null || other instanceof NoShape) return false;
		if (getPosition().distance(other.getPosition()) > getMaximalRadius() + other.getMaximalRadius()) {
			//the shapes are too far away;
			return false;
		}

		if (other instanceof Rectangle) {
			return collides(this, (Rectangle) other);
		}

		if (other instanceof Circle) {
			return Circle.collides((Circle) other, this);
		}

		throw Shape.unresolvableCollision(this, other);
	}

	@Override
	public float getArea() {
		return width * height;
	}

	@Override
	public float getPerimeter() {
		return 2 * (width + height);
	}

	@Override
	public float getMinimalRadius() {
		return MathUtils.min(width/2, height/2);
	}

	@Override
	public float getMaximalRadius() {
		return (float) Math.hypot(width/2, height/2);
	}

	@Override
	public Rectangle move(Vector location) {
		return new Rectangle(() -> location, width, height);
	}

	@Override
	public Shape scale(float scale) {
		return new Rectangle(this::getPosition, width * scale, height * scale);
	}

	@Override
	public void draw(Color color) {
		//llc = lower left corner
		Vector llc = getPosition().subtract(width/2, height/2);
		DungeonRaidersGame.getDraw().drawRectangle(llc.x, llc.y, width, height, color);
	}

	public static boolean collides(Rectangle r1, Rectangle r2) {
		Vector r11 = r1.getPosition().subtract(r1.width / 2, r1.height / 2);
		Vector r12 = r1.getPosition().add(r1.width / 2, r1.height / 2);
		Vector r21 = r2.getPosition().subtract(r2.width / 2, r2.height / 2);
		Vector r22 = r2.getPosition().add(r2.width / 2, r2.height / 2);
		return (r11.x < r22.x && r12.x > r21.x && r11.y < r22.y && r12.y > r21.y);
	}

	@Override
	public String toString() {
		return "Rectangle{" +
				"width=" + width +
				", height=" + height +
				'}';
	}
}
