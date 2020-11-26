package net.drcorchit.dungeonraiders.assets.animation;

public interface Animation {
	float getDefaultSpeed();

	int length();

	Frame getFrame(float index);

	//allows lerping between frames for smoother animations
	default MutableFrame getFrameLerped(float index) {
		int whole = (int) index;
		float part = index - whole;
		Frame f1 = getFrame(whole);
		Frame f2 = getFrame(whole + 1);
		return f1.lerp(f2, part);
	}
}