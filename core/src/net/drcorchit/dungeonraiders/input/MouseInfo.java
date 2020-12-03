package net.drcorchit.dungeonraiders.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import net.drcorchit.dungeonraiders.utils.MathUtils;
import net.drcorchit.dungeonraiders.utils.Vector;

import java.util.function.BiPredicate;

public class MouseInfo {
	public class ButtonInfo {
		public final String name;
		public int x, y;
		public InputState state = InputState.UP;

		private ButtonInfo(String name) {
			this.name = name;
		}

		private void update(int button) {
			boolean down = Gdx.input.isButtonPressed(button);
			state = state.nextState(down);

			if (state.equals(InputState.PRESSED)) {
				x = MouseInfo.this.x;
				y = MouseInfo.this.y;
			}
		}

		public void clear() {
			if (state != state.cleared()) {
				state = state.cleared();
			}
		}

		public Vector getPosition() {
			return new Vector(x, y);
		}

		public boolean heldWithin(int x, int y, int r) {
			return heldWithin((x1, y1) -> MathUtils.distance(x, y, x1, y1) <= r);
		}

		public boolean heldWithin(int x, int y, int w, int h) {
			return heldWithin((x1, y1) -> x1 >= x && x1 <= x + w && y1 >= y && y1 <= y + h);
		}

		public boolean heldWithin(BiPredicate<Integer, Integer> bounds) {
			return state.isDown() && bounds.test(x, y) &&
					bounds.test(MouseInfo.this.x, MouseInfo.this.y);
		}

		public boolean releasedWithin(int x, int y, int r) {
			return releasedWithin((x1, y1) -> MathUtils.distance(x, y, x1, y1) <= r);
		}

		public boolean releasedWithin(int x, int y, int w, int h) {
			return releasedWithin((x1, y1) -> x1 >= x && x1 <= x + w && y1 >= y && y1 <= y + h);
		}

		public boolean releasedWithin(BiPredicate<Integer, Integer> bounds) {
			return state.isReleased() && bounds.test(x, y) &&
					bounds.test(MouseInfo.this.x, MouseInfo.this.y);
		}
	}

	private int h;
	public int x, y;
	public final ButtonInfo left, right;

	public MouseInfo() {
		left = new ButtonInfo("left");
		right = new ButtonInfo("right");
	}

	public Vector getPosition() {
		return new Vector(x, y);
	}

	public void setH(int h) {
		this.h = h;
	}

	public void update() {
		x = Gdx.input.getX();
		y = h - Gdx.input.getY();

		left.update(Input.Buttons.LEFT);
		right.update(Input.Buttons.RIGHT);
	}

	public void clear() {
		left.clear();
		right.clear();
	}

	public boolean anyPressed() {
		return left.state.isPressed() || right.state.isPressed();
	}

	public boolean within(Actor actor) {
		return within(actor.getX(), actor.getY(), actor.getWidth(), actor.getHeight());
	}

	public boolean within(float x, float y, float w, float h) {
		return this.x > x && this.y > y && this.x < x + w && this.y < y + h;
	}

	@Override
	public String toString() {
		return String.format("Mouse@%d, %d", x, y);
	}
}
