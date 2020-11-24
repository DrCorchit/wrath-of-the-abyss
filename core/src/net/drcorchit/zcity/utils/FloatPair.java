package net.drcorchit.zcity.utils;

public class FloatPair extends Pair<Float, Float> {


	public FloatPair(Float key, Float value) {
		super(key, value);
	}

	public FloatPair add(FloatPair other) {
		return new FloatPair(key + other.key, val + other.val);
	}

	public FloatPair add(float x, float y) {
		return new FloatPair(key + x, val + y);
	}
}
