package net.drcorchit.dungeonraiders.assets.animation;

import com.google.common.collect.ImmutableMap;
import net.drcorchit.dungeonraiders.assets.Skeleton;
import net.drcorchit.dungeonraiders.utils.MathUtils;

import java.util.HashMap;
import java.util.Map;

public interface Frame {

	Map<String, Float> getAngles();

	default void apply(Skeleton skeleton, float tweening) {
		getAngles().forEach((jointName, value) -> {
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

	default MutableFrame lerp(Frame other, float factor) {
		factor = MathUtils.clamp(0f, factor, 1f);
		HashMap<String, Float> map = new HashMap<>();

		for (Map.Entry<String, Float> entry : getAngles().entrySet()) {
			String jointName = entry.getKey();
			float angle = entry.getValue();

			Float otherAngle = other.getAngles().get(jointName);

			if (otherAngle == null) {
				map.put(jointName, angle);
			} else {
				map.put(jointName, (float) MathUtils.lerp(angle, otherAngle, factor));
			}
		}

		for (Map.Entry<String, Float> entry : other.getAngles().entrySet()) {
			String jointName = entry.getKey();
			if (!getAngles().containsKey(jointName)) {
				map.put(jointName, entry.getValue());
			}
		}

		return new MutableFrame(map);
	}
}
