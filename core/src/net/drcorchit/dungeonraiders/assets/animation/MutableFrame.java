package net.drcorchit.dungeonraiders.assets.animation;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class MutableFrame implements Frame {
	private final HashMap<String, Float> angles;

	public MutableFrame(Map<String, Float> angles) {
		this.angles = new HashMap<>(angles);
	}

	public void setAngle(String jointName, float value) {
		angles.put(jointName, value);
	}

	@Override
	public Map<String, Float> getAngles() {
		return angles;
	}
}
