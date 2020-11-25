package net.drcorchit.zcity.assets.animation;

public interface Animation {
	float getDefaultSpeed();

	int length();

	Frame getFrame(float index);
}