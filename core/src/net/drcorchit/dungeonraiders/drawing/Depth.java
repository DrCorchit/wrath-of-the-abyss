package net.drcorchit.dungeonraiders.drawing;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

public class Depth implements Comparable<Depth> {

	private final float[] values;

	public Depth(float...values) {
		this.values = values;
	}

	@Override
	public int compareTo(@NotNull Depth other) {
		int maxSize = Math.max(values.length, other.values.length);

		for (int i = 0; i < maxSize; i++) {
			float f1 = getValue(values, i);
			float f2 = getValue(other.values, i);
			int result = Float.compare(f1, f2);
			if (result != 0) return result;
		}

		return 0;
	}

	private static float getValue(float[] values, int index) {
		return index >= values.length ? 0 : values[index];
	}
}
