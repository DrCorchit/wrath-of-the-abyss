package net.drcorchit.zcity.assets;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.drcorchit.zcity.utils.IOUtils;
import net.drcorchit.zcity.utils.JsonUtils;

import java.io.File;
import java.io.IOException;

public class Animations {

	private static final File ANIMATION_FOLDER = IOUtils.getFileAsChildOfWorkingDir("resources/animations");
	public static final Animation windmill = loadLoop("windmill.json");

	public interface Animation {
		default void update(Skeleton skeleton) {
			update(skeleton, 360);
		}

		void update(Skeleton skeleton, float tweening);
	}

	public static class LoopingAnimation implements Animation {
		private final ImmutableList<Frame> frames;
		private float index, speed;

		@Override
		public void update(Skeleton skeleton, float tweening) {
			int frameIndex = (int) (index % frames.size());
			Frame current = frames.get(frameIndex);
			current.apply(skeleton, tweening);
			index += speed;
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
			angles.forEach((jointName, angle) -> {
				Skeleton.Joint joint = skeleton.getJoint(jointName);
				joint.approachAngle(angle, tweening);
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
