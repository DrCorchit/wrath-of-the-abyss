package net.drcorchit.dungeonraiders.utils;

import com.google.common.collect.Iterators;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Grid<T> implements Iterable<T> {

	private final T[][] grid;

	public Grid(Class<T> clazz, int width, int height) {
		//height and width are intentionally reversed here.
		//this makes setRow possible with System.arraycopy()
		grid = (T[][]) Array.newInstance(clazz, height, width);
	}

	public void set(int i, int j, T value) {
		grid[i][j] = value;
	}

	public void forEachCell(Consumer<T> action) {
		forEachCell((i, j) -> action.accept(get(i, j)));
	}

	public void forEachCell(BiConsumer<Integer, Integer> action) {
		for (int j = 0; j < getHeight(); j++) {
			for (int i = 0; i < getWidth(); i++) {
				action.accept(i, j);
			}
		}
	}

	@SafeVarargs
	public final void setRow(int rowIndex, T... row) {
		if (row.length == grid[rowIndex].length) {
			System.arraycopy(row, 0, grid[rowIndex], 0, row.length);
		} else throw new IllegalArgumentException("Number of arguments does not match row size.");
	}

	@SafeVarargs
	public final void setColumn(int columnIndex, T... column) {
		if (column.length == grid.length) {
			for (int i = 0; i < column.length; i++) grid[i][columnIndex] = column[i];
		} else throw new IllegalArgumentException("Number of arguments does not match column size.");
	}

	public T get(int i, int j) {
		return grid[i][j];
	}

	public int getWidth() {
		return grid.length;
	}

	public int getHeight() {
		return grid[0].length;
	}

	@NotNull
	@Override
	public Iterator<T> iterator() {
		Iterator<T[]> rowIterator = Iterators.forArray(grid);
		Iterator<Iterator<T>> rowColumnIterator = Iterators.transform(rowIterator, Iterators::forArray);
		return Iterators.concat(rowColumnIterator);
	}
}
