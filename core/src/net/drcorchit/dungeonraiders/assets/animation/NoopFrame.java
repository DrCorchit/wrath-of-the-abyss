package net.drcorchit.dungeonraiders.assets.animation;

import com.google.common.collect.ImmutableMap;
import net.drcorchit.dungeonraiders.assets.Skeleton;

import java.util.Map;

public class NoopFrame implements Frame {

	public static final Frame INSTANCE = new NoopFrame();

	public static final Animation NOOP_ANIMATION = new Pose(INSTANCE);

	private NoopFrame() {

	}

	@Override
	public Map<String, Float> getAngles() {
		return ImmutableMap.of();
	}

	@Override
	public void apply(Skeleton skeleton, float tweening) {
		//No-Op
	}
}
