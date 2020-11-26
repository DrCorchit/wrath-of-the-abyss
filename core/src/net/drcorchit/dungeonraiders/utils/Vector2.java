package net.drcorchit.dungeonraiders.utils;

public class Vector2 {

	public final float x, y;

	public Vector2(float x, float value) {
		this.x = x;
		this.y = value;
	}

	//convert from LibGDX mutabel vector to immutable vector
	public static Vector2 fromLibGDX(com.badlogic.gdx.math.Vector2 v) {
		return new Vector2(v.x, v.y);
	}

	//converts this to a badlogic vector2
	public com.badlogic.gdx.math.Vector2 toLibGDX() {
		return new com.badlogic.gdx.math.Vector2(x, y);
	}

	public Vector2 add(Vector2 other) {
		return new Vector2(x + other.x, y + other.y);
	}

	public Vector2 add(float x, float y) {
		return new Vector2(this.x + x, this.y + y);
	}

	public Vector2 subtract(Vector2 other) {
		return new Vector2(x - other.x, y - other.y);
	}

	public float distance(Vector2 other) {
		return (float) Math.hypot(x - other.x, y - other.y);
	}

	public float angle(Vector2 other) {
		return (float) Math.toDegrees(MathUtils.angle(x, y, other.x, other.y));
	}

	public float length() {
		return (float) Math.hypot(x, y);
	}

	public Vector2 mirrorAroundLine(Vector2 reflectionOrigin, float reflectionAngle) {
		float internalAngle = reflectionOrigin.angle(this) - reflectionAngle;
		float leg = subtract(reflectionOrigin).length();
		float theta = reflectionAngle - 90;
		float r = (float) (Math.sin(Math.toRadians(internalAngle)) * leg);

		float x = (float) (this.x + 2 * r * Math.cos(Math.toRadians(theta)));
		float y = (float) (this.y + 2 * r * Math.sin(Math.toRadians(theta)));
		return new Vector2(x, y);
	}
}
