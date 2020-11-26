package net.drcorchit.dungeonraiders.assets.animation;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class AnimationState {

	@NotNull
	private Animation animation;
	private float frameIndex;

	public AnimationState(@Nonnull Animation animation) {
		this.animation = animation;
	}

	@NotNull
	public Animation getAnimation() {
		return animation;
	}

	public void setAnimation(@NotNull Animation animation) {
		if (animation != this.animation) frameIndex = 0;
		this.animation = animation;
	}

	public Frame getNextFrame() {
		return getNextFrame(animation.getDefaultSpeed());
	}

	public Frame getNextFrame(float speed) {
		Frame output = animation.getFrame(frameIndex);
		frameIndex += speed;
		return output;
	}

	public MutableFrame getNextFrameLerped() {
		return getNextFrameLerped(animation.getDefaultSpeed());
	}

	public MutableFrame getNextFrameLerped(float speed) {
		MutableFrame output = animation.getFrameLerped(frameIndex);
		frameIndex += speed;
		return output;
	}
}
