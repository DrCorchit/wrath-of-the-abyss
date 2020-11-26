package net.drcorchit.dungeonraiders.assets.animation;

import net.drcorchit.dungeonraiders.assets.Skeleton;

public class AnimationState {

	private Animation animation;
	private float frame;

	public void setAnimation(Animation animation) {
		if (animation != this.animation) frame = 0;
		this.animation = animation;
	}

	public void apply(Skeleton skeleton) {
		apply(skeleton, animation.getDefaultSpeed(), 360);
	}

	public void apply(Skeleton skeleton, float tweening) {
		apply(skeleton, animation.getDefaultSpeed(), tweening);
	}

	public void apply(Skeleton skeleton, float speed, float tweening) {
		tweening *= speed / animation.getDefaultSpeed();
		animation.getFrame(frame).apply(skeleton, tweening);
		frame += speed;
	}
}
