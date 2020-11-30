package net.drcorchit.dungeonraiders.drawing;

public class RunnableRenderInstruction implements RenderInstruction {

	private final Runnable draw;
	private final Depth depth;

	public RunnableRenderInstruction(Runnable draw, float...depth) {
		this.draw = draw;
		this.depth = new Depth(depth);
	}

	@Override
	public Depth getDepth() {
		return depth;
	}

	@Override
	public void draw() {
		draw.run();
	}
}
