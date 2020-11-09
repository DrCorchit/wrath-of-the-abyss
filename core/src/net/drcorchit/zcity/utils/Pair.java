package net.drcorchit.zcity.utils;

import java.util.Objects;

public class Pair<K, V> {

	private final K key;
	private final V value;

	public Pair(K key, V value) {
		this.key = key;
		this.value = value;
	}

	public Pair<V, K> invert() {
		return new Pair<>(value, key);
	}

	public K key() {
		return key;
	}

	public V value() {
		return value;
	}

	@Override
	public String toString() {
		return String.format("[%s, %s]", key, value);
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Pair<?, ?>)) return false;
		Pair<?, ?> pair = (Pair<?, ?>) other;
		return Objects.equals(key, pair.key) && Objects.equals(value, pair.value);
	}

	@Override
	public int hashCode() {
		int xHash = key == null ? 0 : key.hashCode();
		int yHash = value == null ? 0 : value.hashCode();
		return xHash + yHash * 256;
	}
}