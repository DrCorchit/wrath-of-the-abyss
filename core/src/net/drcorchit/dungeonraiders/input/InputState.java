package net.drcorchit.dungeonraiders.input;

public enum InputState {
	UP, DOWN, PRESSED, RELEASED;

	public boolean isUp() {
		return equals(UP);
	}

	public boolean isDown() {
		return equals(DOWN);
	}

	public boolean isPressed() {
		return equals(PRESSED);
	}

	public boolean isReleased() {
		return equals(RELEASED);
	}

	public boolean isHeld() {
		return equals(DOWN) || equals(PRESSED);
	}

	public InputState nextState(boolean down) {
		if (isHeld()) {
			return down ? DOWN : RELEASED;
		} else {
			return down ? PRESSED : UP;
		}
	}

	public InputState cleared() {
		return (this == UP || this == RELEASED) ? UP : DOWN;
	}
}