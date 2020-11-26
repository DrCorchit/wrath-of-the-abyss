package net.drcorchit.dungeonraiders.entities.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.BodyDef;
import net.drcorchit.dungeonraiders.DungeonRaidersGame;
import net.drcorchit.dungeonraiders.assets.Skeleton;
import net.drcorchit.dungeonraiders.assets.Skin;
import net.drcorchit.dungeonraiders.assets.animation.AnimationState;
import net.drcorchit.dungeonraiders.assets.animation.MutableFrame;
import net.drcorchit.dungeonraiders.assets.animation.NoopFrame;
import net.drcorchit.dungeonraiders.entities.stages.PhysicsStage;
import net.drcorchit.dungeonraiders.utils.Vector2;
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

	public PuppetActor(PhysicsStage stage, Skeleton skeleton, Skin skin, float x, float y, float w, float h) {
		super(stage, BodyDef.BodyType.DynamicBody, x, y, w, h);
		this.skeleton = skeleton.copy();
		this.skin = skin.copy();
		animator = new AnimationState(NoopFrame.NOOP_ANIMATION);
		nextFrame = animator.getNextFrameLerped();
	}

	@Override
	public void act(float factor) {
		float speed = animator.getAnimation().getDefaultSpeed() * factor;
		nextFrame = animator.getNextFrameLerped(speed);
		actInner(factor);
		nextFrame.apply(skeleton, DEFAULT_TWEENING * factor);
		skin.update(factor);
		super.act(factor);
	}

	public abstract void actInner(float factor);

	@Override
	public void draw(Vector2 position) {
		DungeonRaidersGame.getDraw().drawRectangle(position.x - width/2, position.y - height/2, width, height, Color.RED);
		skin.draw(skeleton, position);
	}
}
