package net.drcorchit.dungeonraiders.utils;

import com.badlogic.gdx.graphics.Color;
import com.google.common.collect.Iterators;
import com.google.common.net.PercentEscaper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

public class StringUtils {

	public static final PercentEscaper ESCAPER = new PercentEscaper("-_.*", false);

	public static String toString(Object obj) {
		return obj == null ? "null" : obj.toString();
	}

	public static String join(String delimiter, Object...args) {
		return join(delimiter, () -> Iterators.forArray(args));
	}

	public static <T> String join(String delimiter, Iterator<T> iter, Function<T, String> converter) {
		return join(delimiter, Utils.convert(iter, converter));
	}

	public static String join(String delimiter, Iterable<?> iter) {
		return join(delimiter, Utils.convert(iter, Object::toString).iterator());
	}

	public static String join(String delimiter, Iterator<String> iter) {
		if (!iter.hasNext()) return "";
		StringBuilder output = new StringBuilder();
		output.append(iter.next());
		while (iter.hasNext()) output.append(delimiter).append(iter.next());
		return output.toString();
	}

	public static String exceptionToString(Throwable e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}

	public static String capitalize(String in) {
		return in.substring(0, 1).toUpperCase() + in.substring(1);
	}

	public static String capitalizeSentence(String in) {
		//split on spaces or underscores, capitalize first work of every subunit
		return join(" ", Arrays.asList(in.split("[_ ]")).iterator(), StringUtils::capitalize);
	}

	public static <T> String mapToString(Map<String, T> map) {
		StringBuilder builder = new StringBuilder();
		Iterator<Map.Entry<String, T>> iter = map.entrySet().iterator();
		if (iter.hasNext()) {
			Map.Entry<String, T> entry = iter.next();
			builder.append(entry.getKey()).append(": ").append(entry.getValue());
		} else {
			return "";
		}

		while (iter.hasNext()) {
			Map.Entry<String, T> entry = iter.next();
			builder.append("\n").append(entry.getKey()).append(": ").append(entry.getValue());
		}
		return builder.toString();
	}

	public static String buildHttpParameterMap(String...keyVals) {
		if (keyVals.length % 2 == 1) {
			throw new IllegalArgumentException("Expected an even number of arguments.");
		}

		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < keyVals.length; i+=2) {
			String key = ESCAPER.escape(keyVals[i]);
			String value = ESCAPER.escape(keyVals[i+1]);
			if (builder.length() != 0) builder.append("&");
			builder.append(key).append("=").append(value);
		}
		return builder.toString();
	}

	public static Color parseColor(String in) {
		if (in.matches("#[0-9a-fA-F]{6}")) {
			float r, g, b;
			//Hexadecimal color
			r = Integer.parseInt(in.substring(1, 3), 16) / 255f;
			g = Integer.parseInt(in.substring(3, 5), 16) / 255f;
			b = Integer.parseInt(in.substring(5, 7), 16) / 255f;
			return new Color(r, g, b, 1);
		} else {
			throw new NumberFormatException(in);
		}
	}
}
