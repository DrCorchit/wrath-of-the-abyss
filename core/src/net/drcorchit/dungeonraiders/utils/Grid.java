package net.drcorchit.dungeonraiders.utils;

import java.lang.reflect.Array;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Grid<T> {

	private final T[][] grid;

	public Grid(Class<T> clazz, int width, int height) {
		//height and width are intentionally reversed here.
		//this makes setRow possible with System.arraycopy()
		grid = (T[][]) Array.newInstance(clazz, height, width);
	}

	public void set(int i, int j, T value) {
		grid[i][j] = value;
	}

	public void forEach(Consumer<T> action) {
		forEach((i, j) -> action.accept(get(i, j)));
	}

	public void forEach(BiConsumer<Integer, Integer> action) {
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
}
