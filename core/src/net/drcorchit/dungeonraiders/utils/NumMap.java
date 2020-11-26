package net.drcorchit.dungeonraiders.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class NumMap<T> extends HashMap<T, Integer> {

	public Integer add(T item) {
		Integer num = super.get(item);
		num = num == null ? 1 : num+1;
		return super.put(item, num);
	}

	public void addAll(Collection<T> items) {
		items.forEach(this::add);
	}

	@Override
	public Integer get(Object key) {
		if (containsKey(key)) {
			return super.get(key);
		} else {
			return 0;
		}
	}

	public Iterable<Pair<T, Integer>> sortedEntries() {
		return PairIterator::new;
	}

	private class PairIterator implements Iterator<Pair<T, Integer>> {

		private final Heap<T> heap;

		private PairIterator() {
			heap = new Heap<>(Heap.MAX);
			forEach(heap::add);
		}

		@Override
		public boolean hasNext() {
			return !heap.isEmpty();
		}

		@Override
		public Pair<T, Integer> next() {
			T val = heap.remove();
			return new Pair<>(val, NumMap.this.get(val));
		}
	}
}
