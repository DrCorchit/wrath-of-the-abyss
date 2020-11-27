package net.drcorchit.dungeonraiders.shapes;

import net.drcorchit.dungeonraiders.utils.Vector;

import java.util.function.Supplier;

public abstract class AbstractShape implements Shape {

	private final Supplier<Vector> positionGetter;

	public AbstractShape(Supplier<Vector> positionGetter) {
		this.positionGetter = positionGetter;
	}

	@Override
	public final Vector getPosition() {
		return positionGetter.get();
	}
}
