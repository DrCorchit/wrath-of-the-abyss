package net.drcorchit.dungeonraiders.actors;

import com.badlogic.gdx.graphics.Color;
import com.google.common.collect.ImmutableList;
import net.drcorchit.dungeonraiders.assets.Textures;
import net.drcorchit.dungeonraiders.drawing.RenderInstruction;
import net.drcorchit.dungeonraiders.drawing.RunnableRenderInstruction;
import net.drcorchit.dungeonraiders.stages.DungeonStage;
import net.drcorchit.dungeonraiders.drawing.AnimatedSprite;
import net.drcorchit.dungeonraiders.utils.Vector;

import java.util.Collection;

public class Block extends PhysicsActor<DungeonStage> {

	private final AnimatedSprite sprite = Textures.asSpriteList(Textures.WALL).asSprite();
	private final float width, height;

	public Block(DungeonStage stage, float x, float y, float w, float h) {
		super(stage, true, new Vector(x, y));
		this.width = w;
		this.height = h;
		setColliderToRectangle(0, 0, w, h);
	}

	@Override
	public Collection<RenderInstruction> draw(Vector position) {
		Runnable draw = () -> getCollider().draw(Color.WHITE);
		return ImmutableList.of(new RunnableRenderInstruction(getZ(), draw));
		//sprite.draw(getBatch(), position.x - width/2, position.y - width/2, width, height);
	}

	@Override
	public void prePhysicsAct(float delta) {

	}

	@Override
	public boolean canOccupyPosition(Vector position) {
		return true;
	}
}
