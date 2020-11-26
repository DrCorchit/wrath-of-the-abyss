package net.drcorchit.dungeonraiders.assets.animation;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import net.drcorchit.dungeonraiders.assets.Skeleton;
import net.drcorchit.dungeonraiders.utils.MathUtils;

import java.util.Map;

public class Frame {
	private final ImmutableMap<String, Float> angles;

	public Frame(JsonObject info) {
		ImmutableMap.Builder<String, Float> builder = ImmutableMap.builder();

		info.entrySet().forEach(entry -> {
			builder.put(entry.getKey(), entry.getValue().getAsFloat());
		});

		angles = builder.build();
	}

	private Frame(ImmutableMap<String, Float> angles) {
		this.angles = angles;
	}

	public Frame lerp(Frame other, float factor) {
		factor = MathUtils.clamp(0f, factor, 1f);
		ImmutableMap.Builder<String, Float> builder = ImmutableMap.builder();

		for (Map.Entry<String, Float> entry : angles.entrySet()) {
			String jointName = entry.getKey();
			float angle = entry.getValue();

			Float otherAngle = other.angles.get(jointName);

			if (otherAngle == null) {
				builder.put(jointName, angle);
			} else {
				builder.put(jointName, (float) MathUtils.lerp(angle, otherAngle, factor));
			}
		}

		for (Map.Entry<String, Float> entry : other.angles.entrySet()) {
			String jointName = entry.getKey();
			if (!angles.containsKey(jointName)) {
				builder.put(jointName, entry.getValue());
			}
		}

		return new Frame(builder.build());
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
