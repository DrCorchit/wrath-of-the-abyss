package net.drcorchit.dungeonraiders.drawing;

public interface RenderInstruction extends Comparable<RenderInstruction> {

	float getDepth();

	void draw();

	@Override
	default int compareTo(RenderInstruction other) {
		return Float.compare(getDepth(), other.getDepth());
	}
}
