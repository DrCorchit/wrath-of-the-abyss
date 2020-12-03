package net.drcorchit.dungeonraiders.utils;

import com.badlogic.gdx.utils.Align;
import com.google.common.collect.ImmutableList;
import net.drcorchit.dungeonraiders.drawing.shapes.Rectangle;

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
		return horiz;
	}

	public int getVert() {
		return vert;
	}

	public Pair<Float, Float> getOffset(float w, float h) {
		float x = (horiz - 1) * w / 2;
		float y = (vert - 1) * h / 2;

		return new Pair<>(x, y);
	}

	//By extrapolating the sides of the rectangle, space is divided into 8 regions,
	//corresponding to the 8 directions. The region inside the rectangle does not correspond
	//to any direction, however
	public static Direction getRectangleDirection(Rectangle rect, Vector point) {
		Vector pos = rect.getPosition();
		final boolean above = point.y > pos.y + rect.height / 2;
		final boolean below = point.y < pos.y - rect.height / 2;

		if (point.x < pos.x - rect.width / 2) {
			if (below) {
				return SOUTHWEST;
			} else if (above) {
				return NORTHWEST;
			} else {
				return WEST;
			}
		} else if (point.x > pos.x + rect.width / 2) {
			if (below) {
				return SOUTHEAST;
			} else if (above) {
				return NORTHEAST;
			} else {
				return EAST;
			}
		} else {
			if (below) {
				return SOUTH;
			} else if (above) {
				return NORTH;
			} else {
				return CENTER;
			}
		}
	}
}
