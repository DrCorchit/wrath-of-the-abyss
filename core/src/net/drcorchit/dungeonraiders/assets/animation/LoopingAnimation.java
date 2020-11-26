package net.drcorchit.dungeonraiders.assets.animation;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.drcorchit.dungeonraiders.utils.MathUtils;

public class LoopingAnimation implements Animation {
	private final ImmutableList<ImmutableFrame> frames;
	private final float speed;

	public LoopingAnimation(JsonObject info) {
		speed = info.get("speed").getAsFloat();

		JsonArray framesInfo = info.getAsJsonArray("frames");
		ImmutableList.Builder<ImmutableFrame> builder = ImmutableList.builder();
		framesInfo.forEach(ele -> builder.add(new ImmutableFrame(ele.getAsJsonObject())));
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
	public ImmutableFrame getFrame(float index) {
		int adjustedIndex = (int) MathUtils.mod(index, length());
		return frames.get(adjustedIndex);
	}
}
