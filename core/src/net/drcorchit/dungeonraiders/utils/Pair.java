package net.drcorchit.dungeonraiders.utils;

import java.util.Objects;

public class Pair<K, V> {

	public final K key;
	public final V val;

	public Pair(K key, V value) {
		this.key = key;
		this.val = value;
	}

	public Pair<V, K> invert() {
		return new Pair<>(val, key);
	}

	@Override
	public String toString() {
		return String.format("[%s, %s]", key, val);
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Pair<?, ?>)) return false;
		Pair<?, ?> pair = (Pair<?, ?>) other;
		return Objects.equals(key, pair.key) && Objects.equals(val, pair.val);
	}

	@Override
	public int hashCode() {
		int xHash = key == null ? 0 : key.hashCode();
		int yHash = val == null ? 0 : val.hashCode();
		return xHash + yHash * 256;
	}
}