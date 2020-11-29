package net.drcorchit.dungeonraiders.entities.actors;

import com.google.common.collect.ImmutableSet;
import net.drcorchit.dungeonraiders.assets.Skeleton;
import net.drcorchit.dungeonraiders.assets.Skin;
import net.drcorchit.dungeonraiders.assets.animation.AnimationState;
import net.drcorchit.dungeonraiders.assets.animation.MutableFrame;
import net.drcorchit.dungeonraiders.assets.animation.NoopFrame;
import net.drcorchit.dungeonraiders.drawing.RenderInstruction;
import net.drcorchit.dungeonraiders.drawing.RunnableRenderInstruction;
import net.drcorchit.dungeonraiders.entities.stages.DungeonStage;
import net.drcorchit.dungeonraiders.utils.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public abstract class PuppetActor<T extends DungeonStage> extends PhysicsActor<T> {

	private static final float DEFAULT_TWEENING = 5;

	@NotNull
	protected final AnimationState animator;
	@NotNull
	protected final Skin skin;
	@NotNull
	protected final Skeleton skeleton;
	protected final Vector skeletonOffset;
	@NotNull
	protected MutableFrame nextFrame;

	public PuppetActor(T stage, Skeleton skeleton, Skin skin, Vector position, Vector skeletonOffset) {
		super(stage, false, position);
		animator = new AnimationState(NoopFrame.NOOP_ANIMATION);
		this.skin = skin.copy();
		this.skeleton = skeleton.copy();
		this.skeletonOffset = skeletonOffset;
		nextFrame = animator.getNextFrameLerped();
	}

	@Override
	public void act(float factor) {
		float speed = animator.getAnimation().getDefaultSpeed() * factor;
		nextFrame = animator.getNextFrameLerped(speed);
		prePhysicsAct(factor);
		super.act(factor);
		postPhysicsAct(factor);
		nextFrame.apply(skeleton, DEFAULT_TWEENING * factor);
		skin.update(factor);
	}

	@Override
	public Collection<RenderInstruction> draw(Vector position) {
		Runnable draw = () -> {
			float originalScale = skeleton.scale;
			float projectedScale = getZScale() * originalScale;
			Vector projectedPosition = stage.projectZPosition(position.add(skeletonOffset), getZ());

			skeleton.scale = projectedScale;
			//getCollider().move(position.add(getColliderOffset())).draw(Color.YELLOW);
			skin.draw(skeleton, projectedPosition);
			//skeleton.draw(skeletonPosition);
			skeleton.scale = originalScale;
		};
		return ImmutableSet.of(new RunnableRenderInstruction(getZ(), draw));
	}
}
