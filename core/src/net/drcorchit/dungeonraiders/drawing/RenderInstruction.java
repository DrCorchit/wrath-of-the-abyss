package net.drcorchit.dungeonraiders.drawing;

public interface RenderInstruction extends Comparable<RenderInstruction> {

	Depth getDepth();

	void draw();

	@Override
	default int compareTo(RenderInstruction other) {
		return getDepth().compareTo(other.getDepth());
	}
}
