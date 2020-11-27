package net.drcorchit.dungeonraiders.shapes;

import net.drcorchit.dungeonraiders.utils.Vector;

import java.util.function.Supplier;

public abstract class AbstractShape implements Shape {

	private final Supplier<Vector> positionGetter;

	public AbstractShape(Supplier<Vector> positionGetter) {
		this.positionGetter = positionGetter;
	}

	@Override
	public final Vector getLocation() {
		return positionGetter.get();
	}

	public abstract Shape createVirtualCopyAt(Vector location);

	public boolean wouldCollideWith(Vector location, Shape other) {
		return createVirtualCopyAt(location).collidesWith(other);
	}
}
