package net.drcorchit.zcity.utils;

public class Vector2 extends Pair<Float, Float> {

	public Vector2(Float key, Float value) {
		super(key, value);
	}

	public Vector2 add(Vector2 other) {
		return new Vector2(key + other.key, val + other.val);
	}

	public Vector2 add(float x, float y) {
		return new Vector2(key + x, val + y);
	}

	public Vector2 subtract(Vector2 other) {
		return new Vector2(key - other.key, val - other.val);
	}

	public float angle(Vector2 other) {
		return (float) Math.toDegrees(MathUtils.angle(key, val, other.key, other.val));
	}

	public float length() {
		return (float) Math.hypot(key, val);
	}

	public Vector2 mirrorAroundLine(Vector2 reflectionOrigin, float reflectionAngle) {
		float internalAngle = reflectionOrigin.angle(this) - reflectionAngle;
		float leg = subtract(reflectionOrigin).length();
		float theta = reflectionAngle - 90;
		float r = (float) (Math.sin(Math.toRadians(internalAngle)) * leg);

		float x = (float) (key + 2 * r * Math.cos(Math.toRadians(theta)));
		float y = (float) (val + 2 * r * Math.sin(Math.toRadians(theta)));
		return new Vector2(x, y);
	}
}
