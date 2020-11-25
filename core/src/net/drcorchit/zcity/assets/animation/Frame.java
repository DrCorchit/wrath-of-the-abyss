package net.drcorchit.zcity.assets.animation;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import net.drcorchit.zcity.assets.Skeleton;

public class Frame {
	private final ImmutableMap<String, Float> angles;

	public Frame(JsonObject info) {
		ImmutableMap.Builder<String, Float> builder = ImmutableMap.builder();

		info.entrySet().forEach(entry -> {
			builder.put(entry.getKey(), entry.getValue().getAsFloat());
		});

		angles = builder.build();
	}

	public void apply(Skeleton skeleton, float tweening) {
		angles.forEach((jointName, value) -> {
			switch (jointName) {
				case "x_offset":
					skeleton.horizontalOffset = value;
					break;
				case "y_offset":
					skeleton.verticalOffset = value;
					break;
				default:
					Skeleton.Joint joint = skeleton.getJoint(jointName);
					joint.approachAngle(value, tweening);
			}
		});
	}
}
