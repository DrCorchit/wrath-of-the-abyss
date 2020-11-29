package net.drcorchit.dungeonraiders.utils;

import com.google.gson.JsonObject;

import java.util.HashSet;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class Coordinate {
	public static final float HEX_RATIO = (float) Math.sqrt(3);
	public final Integer x, y;

	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Coordinate(JsonObject in) {
		x = in.get("x").getAsInt();
		y = in.get("y").getAsInt();
	}

	public Coordinate add(Coordinate other) {
		return add(other.x, other.y);
	}

	public Coordinate add(int x, int y) {
		return new Coordinate(this.x + x, this.y + y);
	}

	//Returns the correct distance, accounting for details of tiles class, in miles
	public double distance(Coordinate other, Layout layout) {
		if (Layout.HEXAGONAL.equals(layout)) {//Odd numbered rows are right-shifted by .5
			double myX = y % 2 == 0 ? x : x + .5;
			double yourX = other.y % 2 == 0 ? other.x : other.x + .5;
			//squash tiles along y axis so neighboring tiles are always 1 unit away
			return distance(myX - yourX, (y - other.y) * HEX_RATIO);
		}
		return distance(x - other.x, y - other.y);
	}

	public double angle(Coordinate other, Layout layout) {
		if (layout == Layout.HEXAGONAL) {//Odd numbered rows are right-shifted by .5
			double myX = y % 2 == 0 ? x : x + .5;
			double yourX = other.y % 2 == 0 ? other.x : other.x + .5;
			//squash tiles along y axis so neighboring tiles are always 1 unit away
			return angle(myX - yourX, (y - other.y) * HEX_RATIO);
		}
		return angle(x - other.x, y - other.y);
	}

	public HashSet<Coordinate> getWithinRange(int range, Layout layout) {
		if (range < 0) throw new IllegalArgumentException("range must be nonnegative");
		HashSet<Coordinate> output = new HashSet<>();
		for (int i = x - range; i <= x + range; i++) {
			for (int j = y - range; j <= y + range; j++) {
				Coordinate temp = new Coordinate(i, j);
				if (getRange(temp, layout) <= range) output.add(temp);
			}
		}
		return output;
	}

	public int getRange(Coordinate other, Layout layout) {
		switch (layout) {
			case CARTESIAN:
				return Utils.max(abs(x - other.x), abs(y - other.y));

			case HEXAGONAL:
				return hexRange(this, other);

			case ORTHOGONAL:
				return abs(x - other.x) + abs(y - other.y);

			default:
				throw new IllegalArgumentException();
		}
	}

	private static int hexRange(Coordinate c1, Coordinate c2) {
		int ax = c1.x - floor2(c1.y);
		int ay = c1.x + ceil2(c1.y);
		int bx = c2.x - floor2(c2.y);
		int by = c2.x + ceil2(c2.y);
		int dx = bx - ax;
		int dy = by - ay;

		return hasSameSign(dx, dy) ? max(abs(dx), abs(dy)) : abs(dx) + abs(dy);
	}

	private static int floor2(int x) {
		return ((x >= 0) ? (x >> 1) : (x - 1) / 2);
	}

	private static int ceil2(int x) {
		return ((x >= 0) ? ((x + 1) >> 1) : x / 2);
	}

	private static boolean hasSameSign(int x, int y) {
		return (x < 0 && y < 0) || (x > 0 && y > 0);
	}

	public JsonObject serialize() {
		JsonObject output = new JsonObject();
		output.addProperty("x", x);
		output.addProperty("y", y);
		return output;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Coordinate)) return false;
		Coordinate c = (Coordinate) other;
		return c.x.equals(x) && c.y.equals(y);
	}

	@Override
	public int hashCode() {
		return x + 1000 * y;
	}

	@Override
	public String toString() {
		return String.format("%d, %d", x, y);
	}

	private static double distance(double x, double y) {
		return Math.sqrt(x * x + y * y);
	}

	private static double angle(double x, double y) {
		return Math.atan2(y, x);
	}

}