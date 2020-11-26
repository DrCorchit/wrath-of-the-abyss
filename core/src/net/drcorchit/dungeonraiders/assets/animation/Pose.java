package net.drcorchit.dungeonraiders.assets.animation;

import com.google.gson.JsonObject;

public class Pose implements Animation {

	private final Frame frame;

	public Pose(JsonObject info) {
		frame = new Frame(info);
	}

	@Override
	public float getDefaultSpeed() {
		return 0;
	}

	@Override
	public int length() {
		return 1;
	}

	@Override
	public Frame getFrame(float index) {
		return frame;
	}
}
