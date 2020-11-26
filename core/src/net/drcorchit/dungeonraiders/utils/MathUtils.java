package net.drcorchit.dungeonraiders.utils;

import javax.annotation.Nonnull;

public class MathUtils {

	private static final String[] ORDINAL_SUFFIXES = {
			"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"
	};

	public static String ordinalSuffix(int i) {
		return ORDINAL_SUFFIXES[mod(i, 10)];
	}

	public static String ordinal(int i) {
		return i + ordinalSuffix(i);
	}

	public static int mod(int value, int divisor) {
		int i = value % divisor;
		return i < 0 ? i + divisor : i;
	}

	public static double mod(double value, double divisor) {
		double i = value % divisor;
		return i < 0 ? i + divisor : i;
	}

	public static double distance(double x1, double y1, double x2, double y2) {
		double x = x2 - x1;
		double y = y2 - y1;
		return Math.sqrt(x * x + y * y);
	}

	public static double angle(double x1, double y1, double x2, double y2) {
		double x = x2 - x1;
		double y = y2 - y1;
		return Math.atan2(y, x);
	}

	public static double lerp(double p, double q, double factor) {
		return p + factor * (q - p);
	}

	public static <T extends Number> T clamp(T min, T val, T max) {
		return val.doubleValue() > max.doubleValue() ? max : val.doubleValue() < min.doubleValue() ? min : val;
	}

	public static float decelerate(float value, float decel) {
		if (value == 0) return value;
		else if (value < 0) return Math.min(0, value + decel);
		else  return Math.max(0, value - decel);
	}

	//mirrors an angle around an axis
	public static float mirrorAngle(float angle, float axis) {
		float dif = axis - angle;
		return (float) mod(angle + dif * 2, 360);
	}

	public static Vector2 mirrorPoint(Vector2 point, Vector2 origin, float reflectionAngle) {
		float internalAngle = origin.angle(point) - reflectionAngle;
		float leg = point.distance(origin);
		float theta = reflectionAngle - 90;
		float r = (float) (Math.sin(Math.toRadians(internalAngle)) * leg);
		float x = (float) (point.x + 2 * r * Math.cos(Math.toRadians(theta)));
		float y = (float) (point.y + 2 * r * Math.sin(Math.toRadians(theta)));
		return new Vector2(x, y);
	}

	@SafeVarargs
	@Nonnull
	public static <T extends Number> T max(T... nums) {
		if (nums.length == 0) throw new IllegalArgumentException("No arguments to max()");
		if (nums.length == 1) return nums[0];
		T output = nums[0];
		for (int i = 1; i < nums.length; i++) {
			T num = nums[i];
			if (num.doubleValue() > output.doubleValue()) output = num;
		}
		return output;
	}

	@SafeVarargs
	@Nonnull
	public static <T extends Number> T min(T... nums) {
		if (nums.length == 0) throw new IllegalArgumentException("No arguments to max()");
		if (nums.length == 1) return nums[0];
		T output = nums[0];
		for (int i = 1; i < nums.length; i++) {
			T num = nums[i];
			if (num.doubleValue() < output.doubleValue()) output = num;
		}
		return output;
	}
}
