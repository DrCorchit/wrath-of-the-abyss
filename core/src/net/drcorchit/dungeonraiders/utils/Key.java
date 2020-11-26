package net.drcorchit.dungeonraiders.utils;

import javax.annotation.Nonnull;

public final class Key implements CharSequence {

	@Nonnull
	private final String value;

	public Key(@Nonnull String in) {
		//remove non-word characters
		in = in.replaceAll("\\W+", "");
		in = in.trim();
		//convert spaces to underscore _
		in = in.replaceAll(" ", "_");
		//capitalize all
		in = in.toUpperCase();
		value = in;
	}

	@Override
	public int length() {
		return value.length();
	}

	@Override
	public char charAt(int index) {
		return value.charAt(index);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return value.subSequence(start, end);
	}

	@Nonnull
	@Override
	public String toString() {
		return value;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Key)) return false;
		Key k = (Key) other;
		return value.equals(k.value);
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}
}
