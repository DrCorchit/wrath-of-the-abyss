package net.drcorchit.dungeonraiders.entities.actors;

import com.badlogic.gdx.graphics.Color;
import net.drcorchit.dungeonraiders.assets.Skeleton;
import net.drcorchit.dungeonraiders.assets.Skin;
import net.drcorchit.dungeonraiders.assets.animation.AnimationState;
import net.drcorchit.dungeonraiders.assets.animation.MutableFrame;
import net.drcorchit.dungeonraiders.assets.animation.NoopFrame;
import net.drcorchit.dungeonraiders.entities.stages.DungeonStage;
import net.drcorchit.dungeonraiders.entities.stages.Stage;
import net.drcorchit.dungeonraiders.utils.Vector;
import org.jetbrains.annotations.NotNull;

public abstract class PuppetActor extends PhysicsActor {

	private static final float DEFAULT_TWEENING = 5;

	@NotNull
	protected final AnimationState animator;
	@NotNull
	protected final Skin skin;
	@NotNull
	protected final Skeleton skeleton;
	@NotNull
	protected MutableFrame nextFrame;

	public PuppetActor(DungeonStage stage, Skeleton skeleton, Skin skin, float x, float y, float w, float h) {
		super(stage, false, x, y);
		this.skeleton = skeleton.copy();
		this.skin = skin.copy();
		animator = new AnimationState(NoopFrame.NOOP_ANIMATION);
		nextFrame = animator.getNextFrameLerped();
		setShapeAsRectangle(0, 0, w, h);
	}

	@Override
	public void act(float factor) {
		float speed = animator.getAnimation().getDefaultSpeed() * factor;
		nextFrame = animator.getNextFrameLerped(speed);
		actInner(factor);
		super.act(factor);
		nextFrame.apply(skeleton, DEFAULT_TWEENING * factor);
		skin.update(factor);
	}

	protected void actInner(float factor) {

	}

	@Override
	public void draw(Vector position) {
		float actualScale = skeleton.scale;
		skeleton.scale = getZScale();
		//getShape().draw(Color.RED);
		skin.draw(skeleton, position);
		//skeleton.draw(position);
		skeleton.scale = actualScale;
	}
}
