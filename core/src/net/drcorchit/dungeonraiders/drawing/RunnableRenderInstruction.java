package net.drcorchit.dungeonraiders.drawing;

public class RunnableRenderInstruction implements RenderInstruction {

	private final float depth;

	private final Runnable draw;

	public RunnableRenderInstruction(float depth, Runnable draw) {
		this.depth = depth;
		this.draw = draw;
	}

	@Override
	public float getDepth() {
		return depth;
	}

	@Override
	public void draw() {
		draw.run();
	}
}
