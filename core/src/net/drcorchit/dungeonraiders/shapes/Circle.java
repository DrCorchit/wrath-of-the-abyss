package net.drcorchit.dungeonraiders.shapes;

import com.badlogic.gdx.graphics.Color;
import net.drcorchit.dungeonraiders.DungeonRaidersGame;
import net.drcorchit.dungeonraiders.utils.MathUtils;
import net.drcorchit.dungeonraiders.utils.Vector;

import java.util.function.Supplier;

public class Circle extends AbstractShape {

	public final float radius;
	private final Rectangle boundingRectangle;

	public Circle(Supplier<Vector> locationGetter, float radius) {
		super(locationGetter);
		this.radius = radius;
		boundingRectangle = new Rectangle(locationGetter, 2 * radius, 2 * radius);
	}

	public Rectangle getBoundingRectangle() {
		return boundingRectangle;
	}

	@Override
	public boolean containsPoint(Vector point) {
		Vector relativePos = point.subtract(getPosition());
		return relativePos.length() < radius;
	}

	@Override
	public boolean collidesWith(Shape other) {
		if (getPosition().distance(other.getPosition()) > getMaximalRadius() + other.getMaximalRadius()) {
			//the shapes are too far away;
			return false;
		}

		if (other instanceof Circle) {
			return collides(this, (Circle) other);
		}

		if (other instanceof Rectangle) {
			return collides(this, (Rectangle) other);
		}

		throw Shape.unresolvableCollision(this, other);
	}

	@Override
	public float getArea() {
		return (float) (Math.PI * radius * radius);
	}

	@Override
	public float getPerimeter() {
		return (float) (Math.PI * radius * 2);
	}

	@Override
	public float getMinimalRadius() {
		return radius;
	}

	@Override
	public float getMaximalRadius() {
		return radius;
	}

	@Override
	public Circle move(Vector location) {
		return new Circle(() -> location, radius);
	}

	@Override
	public Shape scale(float scale) {
		return new Circle(this::getPosition, radius * scale);
	}

	@Override
	public void draw(Color color) {
		Vector position = getPosition();
		DungeonRaidersGame.getDraw().drawCircle(position.x, position.y, radius, color);
	}

	public static boolean collides(Circle c1, Circle c2) {
		return c1.getPosition().distance(c2.getPosition()) < c1.radius + c2.radius;
	}

	public static boolean collides(Circle c, Rectangle r) {
		if (!Rectangle.collides(c.boundingRectangle, r)) return false;
		//This might not be pixel perfect
		Vector cloc = c.getPosition();
		Vector rloc = r.getPosition();
		Vector rBottomLeft = new Vector(rloc.x - r.width/2, rloc.y - r.height/2);
		Vector rTopRight = new Vector(rloc.x + r.width/2, rloc.y + r.height/2);

		//possible collision point 1 -- on left or right side of rectangle,
		//and as close as possible to circle center
		Vector p1 = new Vector(cloc.x < rloc.x ? rBottomLeft.x : rTopRight.x,
				MathUtils.clamp(rBottomLeft.y, cloc.y, rTopRight.y));

		//possible collision point 2 -- on top or bottom of rectangle,
		//and as close as possible to circle center
		Vector p2 = new Vector(MathUtils.clamp(rBottomLeft.x, cloc.x, rTopRight.x),
				cloc.y < rloc.y ? rBottomLeft.y : rTopRight.y);

		return c.containsPoint(p1) || c.containsPoint(p2);
	}

	@Override
	public String toString() {
		return "Circle{" +
				"radius=" + radius +
				'}';
	}
}
