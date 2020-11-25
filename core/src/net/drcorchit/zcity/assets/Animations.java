package net.drcorchit.zcity.assets;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.drcorchit.zcity.utils.IOUtils;
import net.drcorchit.zcity.utils.JsonUtils;
import net.drcorchit.zcity.utils.MathUtils;

import java.io.File;
import java.io.IOException;

public class Animations {

	private static final File ANIMATION_FOLDER = IOUtils.getFileAsChildOfWorkingDir("resources/animations");
	public static final Animation windmill = loadLoop("windmill.json");
	public static final Animation jogging = loadLoop("jogging.json");

	public interface Animation {
		float getDefaultSpeed();

		default Frame update() {
			return update(getDefaultSpeed());
		}

		Frame update(float speed);

		default void apply(Skeleton skeleton, float tweening) {
			apply(skeleton, getDefaultSpeed(), tweening);
		}

		default void apply(Skeleton skeleton, float speed, float tweening) {
			update(speed).apply(skeleton, tweening * (speed / getDefaultSpeed()));
		}
	}

	public static class LoopingAnimation implements Animation {
		private final ImmutableList<Frame> frames;
		private final float speed;
		private float index;

		@Override
		public float getDefaultSpeed() {
			return speed;
		}

		@Override
		public Frame update(float speed) {
			int frameIndex = (int) (index % frames.size());
			index = (float) MathUtils.mod(index + speed, frames.size());
			return frames.get(frameIndex);
		}

		public LoopingAnimation(JsonObject info) {
			index = 0;
			speed = info.get("speed").getAsFloat();

			JsonArray framesInfo = info.getAsJsonArray("frames");
			ImmutableList.Builder<Frame> builder = ImmutableList.builder();
			framesInfo.forEach(ele -> builder.add(new Frame(ele.getAsJsonObject())));
			frames = builder.build();
		}
	}

	private static class Frame {
		private final ImmutableMap<String, Float> angles;

		private Frame(JsonObject info) {
			ImmutableMap.Builder<String, Float> builder = ImmutableMap.builder();

			info.entrySet().forEach(entry -> {
				builder.put(entry.getKey(), entry.getValue().getAsFloat());
			});

			angles = builder.build();
		}

		private void apply(Skeleton skeleton, float tweening) {
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

	private static LoopingAnimation loadLoop(String file) {
		try {
			JsonElement info = JsonUtils.parseFile(IOUtils.getFileAsChildOfFolder(ANIMATION_FOLDER, file));
			return new LoopingAnimation(info.getAsJsonObject());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
