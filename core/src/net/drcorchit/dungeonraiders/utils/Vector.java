package net.drcorchit.dungeonraiders.utils;

import java.util.Objects;

public class Vector {

	public static final Vector ZERO = new Vector(0, 0);

	public final float x, y;

	public Vector(float x, float value) {
		this.x = x;
		this.y = value;
	}

	//convert from LibGDX mutabel vector to immutable vector
	public static Vector fromLibGDX(com.badlogic.gdx.math.Vector2 v) {
		return new Vector(v.x, v.y);
	}

	//converts this to a badlogic vector2
	public com.badlogic.gdx.math.Vector2 toLibGDX() {
		return new com.badlogic.gdx.math.Vector2(x, y);
	}

	public Vector rotateDegrees(float degrees) {
		return rotateRadians((float) Math.toRadians(degrees));
	}

	public Vector rotateRadians(float radians) {
		if (this == ZERO) return ZERO;
		float r = length();
		float theta = (float) (Math.atan2(y, x) + radians);
		return new Vector((float) (r * Math.cos(theta)), (float) (r * Math.sin(theta)));
	}

	public Vector add(float x, float y) {
		return new Vector(this.x + x, this.y + y);
	}

	public Vector add(Vector other) {
		return new Vector(x + other.x, y + other.y);
	}

	public Vector subtract(float x, float y) {
		return add(-x, -y);
	}

	public Vector subtract(Vector other) {
		return new Vector(x - other.x, y - other.y);
	}

	public Vector multiply(float scalar) {
		return new Vector(x * scalar, y * scalar);
	}

	public Vector divide(float scalar) {
		return new Vector(x / scalar, y / scalar);
	}

	public Vector normalize() {
		return divide(length());
	}

	public float dot(Vector other) {
		return x * other.x + y * other.y;
	}

	public Vector project(Vector other) {
		return other.normalize().multiply(dot(other.normalize()));
	}

	public Vector half() {
		return new Vector(x / 2, y / 2);
	}

	public float distance(Vector other) {
		return (float) Math.hypot(x - other.x, y - other.y);
	}

	public float angle(Vector other) {
		return (float) Math.toDegrees(MathUtils.angle(x, y, other.x, other.y));
	}

	public float length() {
		return (float) Math.hypot(x, y);
	}

	public Vector mirrorAroundLine(Vector reflectionOrigin, float reflectionAngle) {
		float internalAngle = reflectionOrigin.angle(this) - reflectionAngle;
		float leg = subtract(reflectionOrigin).length();
		float theta = reflectionAngle - 90;
		float r = (float) (Math.sin(Math.toRadians(internalAngle)) * leg);

		float x = (float) (this.x + 2 * r * Math.cos(Math.toRadians(theta)));
		float y = (float) (this.y + 2 * r * Math.sin(Math.toRadians(theta)));
		return new Vector(x, y);
	}

	@Override
	public String toString() {
		return String.format("[%f,%f]", x, y);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Vector)) return false;
		Vector vector = (Vector) o;
		return vector.x == x && vector.y == y;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}
}
