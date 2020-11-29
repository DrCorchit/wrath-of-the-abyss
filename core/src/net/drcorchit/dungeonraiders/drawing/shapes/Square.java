package net.drcorchit.dungeonraiders.drawing.shapes;

import net.drcorchit.dungeonraiders.utils.Vector;

import java.util.function.Supplier;

public class Square extends Rectangle {
	public Square(Supplier<Vector> positionGetter, float size) {
		super(positionGetter, size, size);
	}
}
