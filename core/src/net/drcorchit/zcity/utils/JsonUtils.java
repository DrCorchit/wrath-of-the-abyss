package net.drcorchit.zcity.utils;

import com.badlogic.gdx.graphics.Color;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterators;
import com.google.gson.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class JsonUtils {

	public static final JsonParser PARSER;
	public static final Gson GSON;
	public static final Collector<Object, JsonArray, JsonArray> ARRAY_COLLECTOR;
	public static final Collector<Map.Entry<String, Object>, JsonObject, JsonObject> OBJECT_COLLECTOR;

	static {
		GsonBuilder b = new GsonBuilder();
		b.setPrettyPrinting();
		GSON = b.create();
		PARSER = new JsonParser();

		ARRAY_COLLECTOR = new Collector<Object, JsonArray, JsonArray>() {
			@Override
			public Supplier<JsonArray> supplier() {
				return JsonArray::new;
			}

			@Override
			public BiConsumer<JsonArray, Object> accumulator() {
				return JsonUtils::serializeAny;
			}

			@Override
			public BinaryOperator<JsonArray> combiner() {
				return (arr1, arr2) -> {
					arr1.addAll(arr2);
					return arr1;
				};
			}

			@Override
			public Function<JsonArray, JsonArray> finisher() {
				return arr -> arr;
			}

			@Override
			public Set<Characteristics> characteristics() {
				return null;
			}
		};

		OBJECT_COLLECTOR = new Collector<Map.Entry<String, Object>, JsonObject, JsonObject>() {
			@Override
			public Supplier<JsonObject> supplier() {
				return JsonObject::new;
			}

			@Override
			public BiConsumer<JsonObject, Map.Entry<String, Object>> accumulator() {
				return JsonUtils::serializeAny;
			}

			@Override
			public BinaryOperator<JsonObject> combiner() {
				return (obj1, obj2) -> {
					obj2.entrySet().forEach(entry -> obj1.add(entry.getKey(), entry.getValue()));
					return obj1;
				};
			}

			@Override
			public Function<JsonObject, JsonObject> finisher() {
				return obj -> obj;
			}

			@Override
			public Set<Characteristics> characteristics() {
				return null;
			}
		};
	}

	public static void serializeAny(JsonArray arr, Object obj) {
		arr.add(serializeAny(obj));
	}

	public static void serializeAny(JsonObject info, Map.Entry<String, Object> entry) {
		info.add(entry.getKey(), serializeAny(entry.getValue()));
	}

	public static JsonElement serializeAny(Object obj) {
		if (obj instanceof JsonElement) return ((JsonElement) obj);
		else if (obj instanceof Boolean) return new JsonPrimitive((Boolean) obj);
		else if (obj instanceof Number) return new JsonPrimitive((Number) obj);
		else if (obj instanceof Character) return new JsonPrimitive((Character) obj);
		else if (obj instanceof String) return new JsonPrimitive((String) obj);
		else throw new IllegalArgumentException("Unhandled object: " + obj);
	}

	//Utils for replacing values with shorter values in an Iterable sequence of JsonObjects
	private static HashBiMap<String, String> getMinificationMappings(Iterable<? extends JsonElement> elements) {
		HashBiMap<String, String> mappings = HashBiMap.create();

		//find known keys
		NumMap<String> knownEntries = new NumMap<>();
		for (JsonElement ele : elements) {
			if (isString(ele)) {
				knownEntries.add(ele.getAsString());
			} else if (ele.isJsonObject()) {
				knownEntries.addAll(ele.getAsJsonObject().keySet());
			}
		}

		int i = 0;
		while (knownEntries.containsKey(Integer.toString(i))) i++;

		for (Pair<String, Integer> pair : knownEntries.sortedEntries()) {
			//it's not practical to abbreviate values that occur too infrequently
			if (pair.value() < 2) continue;
			//avoid abbreviating entries that are already very short
			if (pair.key().length() < 4) continue;
			mappings.put(pair.key(), Integer.toString(i++));
			while (knownEntries.containsKey(Integer.toString(i))) i++;
		}

		return mappings;
	}

	@Nonnull
	public static JsonElement get(JsonObject info, String... keys) {
		JsonElement output = info;
		for (String key : keys) {
			output = output.getAsJsonObject().get(key);
		}
		return Objects.requireNonNull(output);
	}

	@Nullable
	public static JsonElement nullSafeGet(JsonObject info, String... keys) {
		JsonElement current = info;
		for (String key : keys) {
			if (current.getAsJsonObject().has(key)) current = current.getAsJsonObject().get(key);
			else return null;
		}
		return current;
	}

	//Renames keys and string values in an iterable sequence of JsonObjects with shorter versions
	//for example { "topography":"mountain" } -> { "_0":"_1" }
	public static JsonObject compress(Iterable<? extends JsonElement> elements) {
		HashBiMap<String, String> mappings = getMinificationMappings(elements);
		elements.forEach(obj -> replace(mappings, obj));
		JsonObject output = new JsonObject();
		mappings.inverse().forEach(output::addProperty);
		return output;
	}

	//Restores keys and string values from minification, replacing abbreviations with long values
	//for example { "_0":"_1" } -> { "topography":"mountain" }
	public static void restore(JsonObject info, Iterable<? extends JsonElement> objects) {
		HashBiMap<String, String> mappings = HashBiMap.create(info.size());
		info.entrySet().forEach(entry -> mappings.put(entry.getKey(), entry.getValue().getAsString()));
		objects.forEach(ele -> replace(mappings, ele));
	}

	//replaces a single jsonObject based on a set of mappings
	private static void replace(@Nonnull BiMap<String, String> mappings, @Nonnull JsonElement ele) {
		if (ele.isJsonObject()) {
			JsonObject obj = ele.getAsJsonObject();
			//replace keys
			for (Map.Entry<String, String> entry : mappings.entrySet()) {
				if (obj.has(entry.getKey())) {
					JsonElement value = obj.get(entry.getKey());
					//remove old key
					obj.remove(entry.getKey());
					//re-insert value under new key
					obj.add(entry.getValue(), value);
				}
			}

			for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
				if (isString(entry.getValue())) {
					String s = entry.getValue().getAsString();
					if (mappings.containsKey(s)) {
						entry.setValue(new JsonPrimitive(mappings.get(s)));
					}
				}
			}
		} else if (ele.isJsonArray()) {
			JsonArray arr = ele.getAsJsonArray();
			for (int i = 0; i < arr.size(); i++) {
				if (isString(arr.get(i))) {
					String s = arr.get(i).getAsString();
					if (mappings.containsKey(s)) {
						arr.set(i, new JsonPrimitive(mappings.get(s)));
					}
				}
			}
		}
	}

	public static boolean isString(JsonElement ele) {
		return ele.isJsonPrimitive() && ele.getAsJsonPrimitive().isString();
	}

	public static Iterable<JsonElement> deepIterable(@Nonnull JsonElement ele) {
		if (ele.isJsonPrimitive() || ele.isJsonNull()) {
			return () -> Iterators.singletonIterator(ele);
		} else if (ele.isJsonObject() || ele.isJsonArray()) {
			return new JsonIterable(ele);
		} else {
			throw new IllegalArgumentException("Invalid as Json: " + ele);
		}
	}

	@Nonnull
	public static JsonElement parseFile(File file) throws JsonSyntaxException, IOException {
		return parseString(IOUtils.readFileAsString(file));
	}

	@Nonnull
	public static JsonElement parseString(@Nonnull String json) throws JsonParseException {
		return PARSER.parse(json);
	}

	public static String prettyPrint(JsonElement in) {
		return GSON.toJson(in);
	}

	public static boolean getBoolean(JsonObject in, String key, boolean def) {
		return in.has(key) ? in.get(key).getAsBoolean() : def;
	}

	public static int getInt(JsonObject in, String key, int def) {
		return in.has(key) ? in.get(key).getAsInt() : def;
	}

	public static long getLong(JsonObject info, String key, int def) {
		return info.has(key) ? info.get(key).getAsLong() : def;
	}

	public static double getDouble(JsonObject in, String key, double def) {
		return in.has(key) ? in.get(key).getAsDouble() : def;
	}

	public static Number getNumber(JsonObject in, String key, Supplier<Number> def) {
		return in.has(key) ? in.get(key).getAsNumber() : def.get();
	}

	public static String getString(JsonObject in, String key, String def) {
		return in.has(key) && !in.get(key).isJsonNull() ? in.get(key).getAsString() : def;
	}

	public static JsonArray getArray(JsonObject in, String key) {
		return in.has(key) ? in.getAsJsonArray(key) : new JsonArray();
	}

	public static JsonObject getObject(JsonObject in, String key) {
		return in.has(key) ? in.getAsJsonObject(key) : new JsonObject();
	}

	public static Color getColor(JsonObject in, String key) {
		if (!in.has(key) || in.get(key).isJsonNull()) throw new IllegalArgumentException();
		JsonElement ele = in.get(key);
		if (ele.isJsonObject()) {
			return readColor(ele.getAsJsonObject());
		} else {
			return StringUtils.parseColor(ele.getAsString());
		}
	}

	//throws a null pointer exception if in is missing "r", "g", or "b"
	public static Color readColor(JsonObject in) {
		float r = in.get("r").getAsInt() / 255f;
		float g = in.get("g").getAsInt() / 255f;
		float b = in.get("b").getAsInt() / 255f;
		float a = JsonUtils.getInt(in, "a", 255) / 255f;
		if (r < 0 || r > 1 || g < 0 || g > 1 || b < 0 || b > 1 || a < 0 || a > 1) {
			throw new IllegalArgumentException("Color value out of range!");
		} else {
			return new Color(r, g, b, a);
		}
	}

	public static HashMap<String, JsonElement> mapOfJson(JsonObject in) {
		if (in == null) throw new NullPointerException();
		HashMap<String, JsonElement> output = new HashMap<>();

		for (Map.Entry<String, JsonElement> entry : in.entrySet()) {
			output.put(entry.getKey(), entry.getValue());
		}

		return output;
	}

	public static JsonElement serializeColor(Color in) {
		int r = (int) Math.ceil(in.r * 255);
		int g = (int) Math.ceil(in.g * 255);
		int b = (int) Math.ceil(in.b * 255);
		String output = String.format("#%02x%02x%02x", r, g, b);
		return new JsonPrimitive(output);
	}

	private static class JsonIterable implements Iterable<JsonElement> {

		private final JsonElement element;

		private JsonIterable(JsonElement element) {
			this.element = element;
		}

		@Nonnull
		@Override
		public Iterator<JsonElement> iterator() {
			return new JsonIterator();
		}

		private class JsonIterator implements Iterator<JsonElement> {
			private final Stack<Iterator<JsonElement>> iters;
			private Iterable<JsonElement> nextIter;
			private JsonElement current;

			private JsonIterator() {
				iters = new Stack<>();
				current = element;
				nextIter = toIter(current);
			}

			private Iterable<JsonElement> toIter(JsonElement ele) {
				if (ele.isJsonObject()) {
					return () -> Utils.convert(ele.getAsJsonObject().entrySet(), Map.Entry::getValue).iterator();
				} else if (ele.isJsonArray()) {
					return () -> ele.getAsJsonArray().iterator();
				} else {
					return null;
				}
			}

			private void seekNext() {
				current = null;

				if (nextIter != null) {
					iters.push(nextIter.iterator());
					nextIter = null;
				}

				while (current == null && !iters.isEmpty()) {
					Iterator<JsonElement> top = iters.peek();

					if (top.hasNext()) {
						current = top.next();
						nextIter = toIter(current);
					} else {
						iters.pop();
					}
				}
			}

			@Override
			public boolean hasNext() {
				if (current == null) {
					seekNext();
					return current != null;
				}
				return true;
			}

			@Override
			public JsonElement next() {
				JsonElement next = current;
				current = null;
				return next;
			}
		}
	}
}

