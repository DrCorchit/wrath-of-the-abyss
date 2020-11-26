package net.drcorchit.dungeonraiders.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Provides unmodifiable access to JsonObjects
 * <p>
 * Implementation provided by static classes do not copy the contents of passed json
 */
@FunctionalInterface
public interface UnmodifiableJson {
	enum Type {
		NULL, BOOLEAN, NUMBER, STRING, OBJECT, ARRAY
	}

	UnmodifiableJson NULL = () -> Type.NULL;

	Type getType();

	default UnmodifiableJson get(String key) {
		throw new IllegalStateException("Method get(String) is not available for " + getType().name());
	}

	default UnmodifiableJson get(int index) {
		throw new IllegalStateException("Method get(int) is not available for " + getType().name());
	}

	default boolean has(String field) {
		return false;
	}

	default boolean has(int index) {
		return false;
	}

	default Iterable<Pair<String, UnmodifiableJson>> getEntries() {
		throw new IllegalStateException("Method getEntries() is not available for " + getType().name());
	}

	default Iterable<Pair<Integer, UnmodifiableJson>> getElements() {
		throw new IllegalStateException("Method getElements() is not available for " + getType().name());
	}

	default int size() {
		return 1;
	}

	default boolean asBoolean() {
		throw new IllegalStateException("Method asBoolean() is not available for " + getType().name());
	}

	default Number asNumber() {
		throw new IllegalStateException("Method asNumber() is not available for " + getType().name());
	}

	default String asString() {
		throw new IllegalStateException("Method asString() is not available for " + getType().name());
	}

	static UnmodifiableJson of(JsonElement in) {
		if (in == null || in.isJsonNull()) return NULL;
		else if (in.isJsonPrimitive()) {
			return of(in.getAsJsonPrimitive());
		} else if (in.isJsonObject()) {
			return of(in.getAsJsonObject());
		} else if (in.isJsonArray()) {
			return of(in.getAsJsonArray());
		} else {
			throw new IllegalArgumentException();
		}
	}

	static UnmodifiableJson of(JsonPrimitive in) {
		if (in == null || in.isJsonNull()) return NULL;
		else if (in.isBoolean()) {
			boolean val = in.getAsBoolean();

			return new UnmodifiableJson() {
				@Override
				public Type getType() {
					return Type.BOOLEAN;
				}

				@Override
				public boolean asBoolean() {
					return val;
				}

				@Override
				public String toString() {
					return Boolean.toString(val);
				}
			};
		} else if (in.isNumber()) {
			Number val = in.getAsNumber();
			boolean isInt = val.intValue() == val.doubleValue();

			return new UnmodifiableJson() {
				@Override
				public Type getType() {
					return Type.NUMBER;
				}

				@Override
				public Number asNumber() {
					return in.getAsNumber();
				}

				@Override
				public String toString() {
					return isInt ? Integer.toString(val.intValue()) : Double.toString(val.doubleValue());
				}
			};
		} else if (in.isString()) {
			String val = in.getAsString();

			return new UnmodifiableJson() {
				@Override
				public Type getType() {
					return Type.STRING;
				}

				@Override
				public String asString() {
					return val;
				}

				@Override
				public String toString() {
					return val;
				}
			};
		} else {
			throw new IllegalArgumentException();
		}
	}

	static UnmodifiableJson of(JsonObject in) {
		if (in == null || in.isJsonNull()) return NULL;
		return new UnmodifiableJson() {
			@Override
			public Type getType() {
				return Type.OBJECT;
			}

			@Override
			public UnmodifiableJson get(@Nonnull String key) {
				if (!in.has(key)) {
					throw new NoSuchElementException("No such entry:" + key);
				}
				return of(in.get(key));
			}

			@Override
			public boolean has(String key) {
				return in.has(key);
			}

			@Override
			public int size() {
				return in.size();
			}

			@Override
			public Iterable<Pair<String, UnmodifiableJson>> getEntries() {
				return () -> new Iterator<Pair<String, UnmodifiableJson>>() {
					Iterator<Map.Entry<String, JsonElement>> inner = in.entrySet().iterator();

					@Override
					public boolean hasNext() {
						return inner.hasNext();
					}

					@Override
					public Pair<String, UnmodifiableJson> next() {
						Map.Entry<String, JsonElement> next = inner.next();
						return new Pair<>(next.getKey(), of(next.getValue()));
					}
				};
			}
		};
	}

	static UnmodifiableJson of(JsonArray in) {
		if (in == null || in.isJsonNull()) return NULL;
		return new UnmodifiableJson() {
			@Override
			public Type getType() {
				return Type.ARRAY;
			}

			@Override
			public UnmodifiableJson get(int index) {
				return of(in.get(index));
			}

			@Override
			public boolean has(int index) {
				return index >= 0 && index < in.size();
			}

			@Override
			public int size() {
				return in.size();
			}

			@Override
			public Iterable<Pair<Integer, UnmodifiableJson>> getElements() {
				return () -> new Iterator<Pair<Integer, UnmodifiableJson>>() {
					int index = 0;

					@Override
					public boolean hasNext() {
						return index < size();
					}

					@Override
					public Pair<Integer, UnmodifiableJson> next() {
						UnmodifiableJson next = get(index++);
						return new Pair<>(index, next);
					}
				};
			}
		};
	}
}
