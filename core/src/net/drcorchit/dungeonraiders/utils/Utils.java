package net.drcorchit.dungeonraiders.utils;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

public class Utils {

	public static <S, T> ArrayList<T> convertArray(Iterable<S> iter, Function<S, T> converter) {
		ArrayList<T> output = new ArrayList<>();
		for (T item : convert(iter, converter)) output.add(item);
		return output;
	}

	public static Iterable<String> convert(JsonArray strings) {
		return convert(strings, JsonElement::getAsString);
	}

	public static <S, T> Iterable<T> convert(Iterable<S> iterable, Function<S, T> converter) {
		return () -> convert(iterable.iterator(), converter);
	}

	public static <S, T> Iterator<T> convert(Iterator<S> iterator, Function<S, T> converter) {
		return new Iterator<T>() {
			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public T next() {
				return converter.apply(iterator.next());
			}
		};
	}
	
	public static <T extends Comparable<T>> T clamp(T min, T val, T max) {
		if (min.compareTo(max) > 0) throw new IllegalArgumentException();
		if (val.compareTo(min) < 0) return min;
		if (val.compareTo(max) > 0) return max;
		return val;
	}

	public static int sign(Number num) {
		return Double.compare(num.doubleValue(), 0);
	}

	public static double distance(double x1, double y1, double x2, double y2) {
		return Math.hypot(x1 - x2, y1 - y2);
	}

	public static <T extends Comparable<T>> T max(T...args) {
		T max = null;
		for (T arg : args) {
			max = max == null ? arg : (max.compareTo(arg) > 0 ? max : arg);
		}
		return max;
	}

	public static <T extends Comparable<T>> T min(T...args) {
		T min = null;
		for (T arg : args) {
			min = min == null ? arg : (min.compareTo(arg) < 0 ? min : arg);
		}
		return min;
	}

	public static <T> int getIntersectionSize(Collection<T> c1, Collection<T> c2) {
		int size = 0;
		if (c2.size() < c1.size()) {
			//swap args so that smaller set is c1
			Collection<T> temp = c1;
			c1 = c2;
			c2 = temp;
		}

		for (T item : c1) {
			if (c2.contains(item)) size += 1;
		}

		return size;
	}

}
