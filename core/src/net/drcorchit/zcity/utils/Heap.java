package net.drcorchit.zcity.utils;

import com.google.common.annotations.VisibleForTesting;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;

public class Heap<E> {

	public static final int MAX = 1;
	public static final int MIN = -1;

	private final int type;
	private final ArrayList<HeapNode> nodes;

	private class HeapNode {
		private double priority;
		private E value;

		private HeapNode(E value, double priority) {
			this.value = value;
			this.priority = priority;
		}

		private E getValue() {
			return value;
		}

		private double getPriority() {
			return priority;
		}

		public String toString() {
			return value.toString();
		}
	}

	public Heap(int type) {
		if (type != MAX && type != MIN) {
			throw new IllegalArgumentException("Undefined heap type: must be max (1) or min (-1)");
		}
		this.type = type;
		nodes = new ArrayList<>();
	}

	public boolean add(E ele, double priority) {
		nodes.add(new HeapNode(ele, priority));
		heapifyUp(nodes.size() - 1);
		return true;
	}

	public E peek() {
		return nodes.get(0).value;
	}

	public E remove() {
		E output = nodes.get(0).value;
		remove(output);
		return output;
	}

	public boolean remove(Object o) {
		int pos = indexOf(o);
		if (pos < 0) return false;
		//give this node the worst priority
		nodes.get(pos).priority = worstPriority();
		swap(pos, size() - 1);
		heapifyDown(pos);

		nodes.remove(nodes.size()-1);
		return true;
	}

	public boolean update(E entry, double newPriority) {
		int pos = indexOf(entry);
		if (pos < 0) return false;
		double oldPriority = nodes.get(pos).priority;
		nodes.get(pos).value = entry;
		nodes.get(pos).priority = newPriority;
		if (hasHigherPriority(oldPriority, newPriority)) heapifyUp(pos);
		else heapifyDown(pos);

		return true;
	}

	//updates entry iff newPriority moves it closer to position 0
	//Results in a heapify up operation
	public boolean updateIfHigher(E entry, double newPriority) {
		if (!contains(entry) || hasHigherPriority(getPriority(entry), newPriority)) return false;
		return update(entry, newPriority);
	}

	//updates entry iff newPriority moves it closer to position size-1
	//Results in a heapify down operation
	public boolean updateIfLower(E entry, double newPriority) {
		if (!contains(entry) || !hasHigherPriority(getPriority(entry), newPriority)) return false;
		return update(entry, newPriority);
	}

	public void put(E entry, double priority) {
		if (!update(entry, priority)) add(entry, priority);
	}

	@CanIgnoreReturnValue
	public boolean putIfHigher(E entry, double priority) {
		return contains(entry) ? updateIfHigher(entry, priority) : add(entry, priority);
	}

	@CanIgnoreReturnValue
	public boolean putIfLower(E entry, double priority) {
		return contains(entry) ? updateIfLower(entry, priority) : add(entry, priority);
	}

	@Nonnull
	public Iterator<E> iterator() {
		return Utils.convert(nodes, HeapNode::getValue).iterator();
	}

	public boolean add(E e) {
		return add(e, worstPriority());
	}

	public boolean contains(Object o) {
		if (o == null) return false;
		for (HeapNode node : nodes) if (node.value.equals(o)) return true;
		return false;
	}

	private int indexOf(@Nonnull Object o) {
		for (int i = 0; i < size(); i++) if (nodes.get(i).value.equals(o)) return i;
		return -1;
	}

	public double getPriority(E entry) {
		int index = indexOf(entry);
		return index == -1 ? worstPriority() : nodes.get(index).priority;
	}

	public int size() {
		return nodes.size();
	}

	public boolean isEmpty() {
		return nodes.isEmpty();
	}

	public void clear() {
		nodes.clear();
	}

	//detects errors in the heap
	@VisibleForTesting
	public boolean check() {
		for (int i = 0; i < size(); i++) {
			int left = left(i);
			int right = right(i);
			if (left >= size()) continue;
			if (!hasHigherOrSamePriority(i, left)) return false;
			if (right < size() && !hasHigherOrSamePriority(i, right)) return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return nodes.toString();
	}

	////////////////////////////
	// PRIVATE METHODS FOLLOW //
	////////////////////////////
	private void heapify() {
		ArrayList<HeapNode> temp = new ArrayList<>(nodes);
		nodes.clear();
		for (int i = 0; i < temp.size(); i++) {
			nodes.add(temp.get(i));
			heapifyUp(i);
		}
	}

	private void heapifyUp(int pos) {
		int parentPos = parent(pos);

		while (parentPos != -1) {
			if (hasHigherPriority(pos, parentPos)) {
				swap(parentPos, pos);
				pos = parentPos;
				parentPos = parent(pos);
			} else {
				return;
			}
		}
	}

	private void heapifyDown(int pos) {
		int lPos = left(pos);
		int rPos = right(pos);
		E p = nodes.get(pos).value;

		if (lPos >= nodes.size()) {
			//No children, heapify complete
			return;
		} else if (rPos >= nodes.size()) {
			//Edge case: one child left
			if (hasHigherPriority(lPos, pos)) swap(pos, lPos);
			return;
		}

		if (hasHigherPriority(lPos, pos) && hasHigherOrSamePriority(lPos, rPos)) {
			swap(pos, lPos);
			heapifyDown(lPos);
		} else if (hasHigherPriority(rPos, pos) && hasHigherOrSamePriority(rPos, lPos)) {
			swap(pos, rPos);
			heapifyDown(rPos);
		}
		//Both children are larger, so continue
	}

	private double worstPriority() {
		return type == MIN ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
	}

	private boolean hasHigherOrSamePriority(int index1, int index2) {
		return hasHigherOrSamePriority(nodes.get(index1).priority, nodes.get(index2).priority);
	}

	private boolean hasHigherPriority(int index1, int index2) {
		return hasHigherPriority(nodes.get(index1).priority, nodes.get(index2).priority);
	}

	private boolean hasHigherOrSamePriority(double d1, double d2) {
		return d1 == d2 || hasHigherPriority(d1, d2);
	}

	private boolean hasHigherPriority(double d1, double d2) {
		return Double.compare(d1, d2) == type;
	}

	private void swap(int index1, int index2) {
		nodes.set(index1, nodes.set(index2, nodes.get(index1)));
	}

	private static int parent(int pos) {
		return (pos - 1) / 2;
	}

	private static int left(int pos) {
		return pos * 2 + 1;
	}

	private static int right(int pos) {
		return pos * 2 + 2;
	}
}