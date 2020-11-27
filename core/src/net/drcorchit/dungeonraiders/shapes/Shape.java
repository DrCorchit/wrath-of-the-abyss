package net.drcorchit.dungeonraiders.shapes;

import com.badlogic.gdx.graphics.Color;
import net.drcorchit.dungeonraiders.utils.Vector;

public interface Shape {

	Vector getPosition();

	boolean containsPoint(Vector point);

	boolean collidesWith(Shape other);

	float getArea();

	float getPerimeter();

	float getMinimalRadius();

	//the distance from the center of the shape to its furthest point
	float getMaximalRadius();

	Shape move(Vector offset);

	Shape scale(float scale);

	void draw(Color color);

	static UnresolvableCollisionException unresolvableCollision(Shape s1, Shape s2) {
		return new UnresolvableCollisionException(s1, s2);
	}

	class UnresolvableCollisionException extends RuntimeException {

		public UnresolvableCollisionException(Shape s1, Shape s2) {
			super(String.format("Unable to resolve collision between %s and %s",
					s1.getClass(),
					s2.getClass()));
		}
	}
 }