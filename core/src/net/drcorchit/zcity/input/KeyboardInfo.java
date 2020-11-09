package net.drcorchit.zcity.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import net.drcorchit.zcity.utils.Utils;

public class KeyboardInfo {
	public static final int A = 0, B = 1, C = 2, D = 3, E = 4, F = 5, G = 6, H = 7, I = 8, J = 9, K = 10, L = 11, M = 12;
	public static final int N = 13, O = 14, P = 15, Q = 16, R = 17, S = 18, T = 19, U = 20, V = 21, W = 22, X = 23, Y = 24, Z = 25;
	public static final ImmutableBiMap<Integer, String> KEY_NAMES;

	static {
		ImmutableBiMap.Builder<Integer, String> namesBuilder = ImmutableBiMap.builder();

		for (int i = 0; i < 10; i++) {
			namesBuilder.put(i + 7, Integer.toString(i));
		}

		for (int i = 0; i < 26; i++) {
			String chr = Character.valueOf((char) (i + 65)).toString();
			namesBuilder.put(i + 29, chr);
		}

		namesBuilder.put(Input.Keys.ESCAPE, "escape");
		namesBuilder.put(Input.Keys.TAB, "tab");
		namesBuilder.put(Input.Keys.SPACE, "space");
		namesBuilder.put(Input.Keys.BACKSPACE, "backspace");
		namesBuilder.put(Input.Keys.ENTER, "enter");
		namesBuilder.put(Input.Keys.SHIFT_LEFT, "left_shift");
		namesBuilder.put(Input.Keys.SHIFT_RIGHT, "right_shift");
		namesBuilder.put(Input.Keys.CONTROL_LEFT, "left_control");
		namesBuilder.put(Input.Keys.CONTROL_RIGHT, "right_control");
		namesBuilder.put(Input.Keys.ALT_LEFT, "left_alt");
		namesBuilder.put(Input.Keys.ALT_RIGHT, "right_alt");
		namesBuilder.put(Input.Keys.LEFT, "left");
		namesBuilder.put(Input.Keys.RIGHT, "right");
		namesBuilder.put(Input.Keys.UP, "up");
		namesBuilder.put(Input.Keys.DOWN, "down");
		namesBuilder.put(Input.Keys.PLUS, "plus");
		namesBuilder.put(Input.Keys.MINUS, "minus");
		KEY_NAMES = namesBuilder.build();
	}

	public class KeyInfo {
		final int key;
		private InputState state;

		private KeyInfo(int key) {
			this.key = key;
			state = InputState.UP;
		}

		private void update() {
			boolean down = Gdx.input.isKeyPressed(key);

			state = state.nextState(down);

			if (state.equals(InputState.PRESSED)) {
				anyPressed = true;
			} else if (state.equals(InputState.RELEASED)) {
				anyReleased = true;
			}
		}

		public InputState getState() {
			return state;
		}

		public boolean isUp() {
			return state.isUp();
		}

		public boolean isDown() {
			return state.isDown();
		}

		public boolean isPressed() {
			return state.isPressed();
		}

		public boolean isReleased() {
			return state.isReleased();
		}

		public boolean isHeld() {
			return state.isHeld();
		}

		@Override
		public String toString() {
			return KEY_NAMES.get(key) + ": " + state.name();
		}
	}

	public int vert, horiz;
	public boolean anyPressed, anyReleased;
	public final KeyInfo[] numbers, letters;
	public KeyInfo esc, tab, space, backspace, enter, leftShift, rightShift, leftCtrl, rightCtrl, leftAlt, rightAlt;
	public KeyInfo left, right, up, down, plus, minus;
	public final ImmutableList<KeyInfo> all;

	public KeyboardInfo() {
		numbers = new KeyInfo[10];
		for (int i = 0; i < 10; i++) {
			numbers[i] = new KeyInfo(i + 7);
		}

		letters = new KeyInfo[26];
		for (int i = 0; i < 26; i++) {
			letters[i] = new KeyInfo(i + 29);
		}

		esc = new KeyInfo(Input.Keys.ESCAPE);
		tab = new KeyInfo(Input.Keys.TAB);
		space = new KeyInfo(Input.Keys.SPACE);
		backspace = new KeyInfo(Input.Keys.BACKSPACE);
		enter = new KeyInfo(Input.Keys.ENTER);
		leftShift = new KeyInfo(Input.Keys.SHIFT_LEFT);
		rightShift = new KeyInfo(Input.Keys.SHIFT_RIGHT);
		leftCtrl = new KeyInfo(Input.Keys.CONTROL_LEFT);
		rightCtrl = new KeyInfo(Input.Keys.CONTROL_RIGHT);
		leftAlt = new KeyInfo(Input.Keys.ALT_LEFT);
		rightAlt = new KeyInfo(Input.Keys.ALT_RIGHT);

		left = new KeyInfo(Input.Keys.LEFT);
		right = new KeyInfo(Input.Keys.RIGHT);
		up = new KeyInfo(Input.Keys.UP);
		down = new KeyInfo(Input.Keys.DOWN);
		plus = new KeyInfo(Input.Keys.PLUS);
		minus = new KeyInfo(Input.Keys.MINUS);

		ImmutableList.Builder<KeyInfo> allBuilder = ImmutableList.builder();
		allBuilder.add(numbers).add(letters);
		allBuilder.add(esc, tab, space, backspace, enter, leftShift, rightShift, leftCtrl, rightCtrl, leftAlt, rightAlt);
		allBuilder.add(left, right, up, down, plus, minus);
		all = allBuilder.build();
	}

	public void update() {

		anyPressed = false;
		anyReleased = false;

		//update all key states
		all.forEach(KeyInfo::update);

		horiz = 0;
		vert = 0;
		if (letters[D].state.isHeld()) horiz++;
		if (right.state.isHeld()) horiz++;
		if (letters[A].state.isHeld()) horiz--;
		if (left.state.isHeld()) horiz--;
		if (letters[W].state.isHeld()) vert++;
		if (up.state.isHeld()) vert++;
		if (letters[S].state.isHeld()) vert--;
		if (down.state.isHeld()) vert--;

		horiz = Utils.clamp(-1, horiz, 1);
		vert = Utils.clamp(-1, vert, 1);
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("vert: ").append(vert).append(" horiz: ").append(horiz).append("\n");
		for (KeyInfo info : all) {
			if (!info.state.equals(InputState.UP)) {
				b.append(info.toString()).append("\n");
			}
		}
		return b.toString();
	}
}
