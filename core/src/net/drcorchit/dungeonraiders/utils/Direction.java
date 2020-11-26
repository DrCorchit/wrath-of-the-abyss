package net.drcorchit.dungeonraiders.utils;

import com.badlogic.gdx.utils.Align;
import com.google.common.collect.ImmutableList;

public enum Direction {
	NORTHWEST(Align.right, -1, 1),
	NORTH(Align.center, 0, 1),
	NORTHEAST(Align.left, 1, 1),
	EAST(Align.left, 1, 0),
	SOUTHEAST(Align.left, 1, -1),
	SOUTH(Align.center, 0, -1),
	SOUTHWEST(Align.right, -1, -1),
	WEST(Align.right, -1, 0),
	CENTER(Align.center, 0, 0);

	private final int align, vert, horiz;

	Direction(int align, int horiz, int vert) {
		this.align = align;
		this.horiz = horiz;
		this.vert = vert;
	}

	public static final ImmutableList<Direction> HEX_DIRS = ImmutableList.of(EAST, NORTHEAST, NORTHWEST, WEST, SOUTHWEST, SOUTHEAST);

	public int getTextAlign() {
		return align;
	}

	public int getHoriz() {
		return  horiz;
	}

	public int getVert() {
		return vert;
	}

	public Pair<Float, Float> getOffset(float w, float h) {
		float x = (horiz - 1) * w / 2;
		float y = (vert - 1) * h / 2;

		return new Pair<>(x, y);
	}
}
