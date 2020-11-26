package net.drcorchit.dungeonraiders.utils;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface KeyMap<T extends KeyMap.Keyable> extends Map<String, T> {

	T put(String key, T value);

	void putAll(@Nonnull Map<? extends String, ? extends T> m);

	T putIfAbsent(String key, T value);

	default T put(T value) {
		return put(value.key(), value);
	}

	default void putAll(Collection<? extends T> values) {
		values.forEach(this::put);
	}

	@CanIgnoreReturnValue
	default T putIfAbsent(@Nonnull T value) {
		return putIfAbsent(value.key(), value);
	}

	interface Keyable {
		String key();
	}

	static <T extends Keyable> KeyMap<T> create() {
		return new KeyMapImpl<>();
	}

	class KeyMapImpl<T extends Keyable> extends HashMap<String, T> implements KeyMap<T> {
		private KeyMapImpl() {
			//do not instantiate outside of static method
		}
	}
}
