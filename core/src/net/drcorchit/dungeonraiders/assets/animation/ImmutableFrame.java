package net.drcorchit.dungeonraiders.assets.animation;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import net.drcorchit.dungeonraiders.assets.Skeleton;
import net.drcorchit.dungeonraiders.utils.MathUtils;

import java.util.Map;

public class ImmutableFrame implements Frame {
	private final ImmutableMap<String, Float> angles;

	public ImmutableFrame(JsonObject info) {
		ImmutableMap.Builder<String, Float> builder = ImmutableMap.builder();

		info.entrySet().forEach(entry -> {
			builder.put(entry.getKey(), entry.getValue().getAsFloat());
		});

		angles = builder.build();
	}

	public ImmutableFrame(ImmutableMap<String, Float> angles) {
		this.angles = angles;
	}

	@Override
	public ImmutableMap<String, Float> getAngles() {
		return angles;
	}
}
