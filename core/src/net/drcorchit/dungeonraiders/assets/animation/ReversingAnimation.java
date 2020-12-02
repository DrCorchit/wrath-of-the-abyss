package net.drcorchit.dungeonraiders.assets.animation;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.drcorchit.dungeonraiders.utils.MathUtils;

public class ReversingAnimation implements Animation {
	private final ImmutableList<ImmutableFrame> frames;
	private final float speed;

	public ReversingAnimation(JsonObject info) {
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
		return frames.size() * 2;
	}

	@Override
	public ImmutableFrame getFrame(float index) {
		int adjustedIndex = (int) MathUtils.mod(index, length());

		if (adjustedIndex >= frames.size()) {
			adjustedIndex = length() - (adjustedIndex + 1);
		}
		return frames.get(adjustedIndex);
	}
}
