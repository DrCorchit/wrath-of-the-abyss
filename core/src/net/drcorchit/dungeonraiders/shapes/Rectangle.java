package net.drcorchit.dungeonraiders.shapes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
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
		Vector relativePos = point.subtract(getLocation());
		float w2 = width / 2, h2 = height / 2;
		return relativePos.x > -w2 && relativePos.x < w2 && relativePos.y > -h2 && relativePos.y < h2;
	}

	@Override
	public boolean collidesWith(Shape other) {
		if (getLocation().distance(other.getLocation()) > getMaximalRadius() + other.getMaximalRadius()) {
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
	public void draw(Color color) {
		//llc = lower left corner
		Vector llc = getLocation().subtract(width/2, height/2);
		DungeonRaidersGame.getDraw().drawRectangle(llc.x, llc.y, width, height, color);
	}

	public static boolean collides(Rectangle r1, Rectangle r2) {
		Vector r11 = r1.getLocation().subtract(r1.width / 2, r1.height / 2);
		Vector r12 = r1.getLocation().add(r1.width / 2, r1.height / 2);
		Vector r21 = r2.getLocation().subtract(r2.width / 2, r2.height / 2);
		Vector r22 = r2.getLocation().add(r2.width / 2, r2.height / 2);
		return (r11.x < r22.x && r12.x > r21.x && r11.y < r22.y && r12.y > r21.y);
	}

	@Override
	public Rectangle createVirtualCopyAt(Vector location) {
		return new Rectangle(() -> location, width, height);
	}

	@Override
	public String toString() {
		return "Rectangle{" +
				"width=" + width +
				", height=" + height +
				'}';
	}
}
