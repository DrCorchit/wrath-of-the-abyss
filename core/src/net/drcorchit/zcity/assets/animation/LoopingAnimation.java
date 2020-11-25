package net.drcorchit.zcity.assets.animation;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.drcorchit.zcity.assets.animation.Animation;
import net.drcorchit.zcity.assets.animation.Frame;
import net.drcorchit.zcity.utils.MathUtils;

public class LoopingAnimation implements Animation {
	private final ImmutableList<Frame> frames;
	private final float speed;

	public LoopingAnimation(JsonObject info) {
		speed = info.get("speed").getAsFloat();

		JsonArray framesInfo = info.getAsJsonArray("frames");
		ImmutableList.Builder<Frame> builder = ImmutableList.builder();
		framesInfo.forEach(ele -> builder.add(new Frame(ele.getAsJsonObject())));
		frames = builder.build();
	}

	@Override
	public float getDefaultSpeed() {
		return speed;
	}

	@Override
	public int length() {
		return frames.size();
	}

	@Override
	public Frame getFrame(float index) {
		int adjustedIndex = (int) MathUtils.mod(index, length());
		return frames.get(adjustedIndex);
	}
}
