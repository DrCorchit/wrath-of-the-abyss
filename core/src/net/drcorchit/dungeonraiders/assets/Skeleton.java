package net.drcorchit.dungeonraiders.assets;

import com.badlogic.gdx.graphics.Color;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.drcorchit.dungeonraiders.DungeonRaidersGame;
import net.drcorchit.dungeonraiders.utils.Draw;
import net.drcorchit.dungeonraiders.utils.JsonUtils;
import net.drcorchit.dungeonraiders.utils.MathUtils;
import net.drcorchit.dungeonraiders.utils.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;

public class Skeleton {

	//mirrors the skeleton around the vertical axis
	public boolean flipped;
	public float scale, horizontalOffset, verticalOffset;
	@Nonnull
	public final Joint root;
	private final HashMap<String, Joint> joints;

	public Skeleton(JsonObject info) {
		scale = JsonUtils.getFloat(info, "scale", 1);
		horizontalOffset = JsonUtils.getFloat(info, "x_offset", 0);
		horizontalOffset = JsonUtils.getFloat(info, "y_offset", 0);
		joints = new HashMap<>();
		root = recursivelyLoadJoints(info, null);
		joints.put(root.name, root);
	}

	//deep copy constructor
	private Skeleton(Skeleton other) {
		flipped = other.flipped;
		scale = other.scale;
		horizontalOffset = other.horizontalOffset;
		verticalOffset = other.horizontalOffset;
		joints = new HashMap<>();
		root = recursivelyCopyJoints(other.root, null);
		joints.put(root.name, root);
	}

	private Joint recursivelyCopyJoints(Joint base, Joint parent) {
		Joint newJoint = new Joint(base, parent);
		base.children.forEach(child -> newJoint.addJoint(recursivelyCopyJoints(child, newJoint)));
		return newJoint;
	}

	private Joint recursivelyLoadJoints(JsonObject base, Joint parent) {
		Joint newJoint = new Joint(base, parent);
		JsonArray children = JsonUtils.getArray(base, "children");
		for (JsonElement ele : children) {
			newJoint.addJoint(recursivelyLoadJoints(ele.getAsJsonObject(), newJoint));
		}
		return newJoint;
	}

	public Skeleton copy() {
		return new Skeleton(this);
	}

	public Joint getJoint(String name) {
		Joint output = joints.get(name);
		if (output == null) {
			throw new IllegalArgumentException("No joint with that name was found in the skeleton: " + name);
		}
		return output;
	}

	public void draw(Vector v) {
		root.draw(v);
	}

	public class Joint {
		private final String name;
		@Nullable
		public final Joint parent;
		//angles are stored in radians
		//parent angle is the angle from parent joint to this. helps determine positioning
		public final float distanceFromParent, angleFromParent;

		private final ArrayList<Joint> children;
		private float angle;

		private Joint(Joint other, @Nullable Joint parent) {
			this.name = other.name;
			this.parent = parent;
			children = new ArrayList<>();
			this.distanceFromParent = other.distanceFromParent;
			this.angleFromParent = other.angleFromParent;
			this.angle = other.angle;
		}

		private Joint(JsonObject info, @Nullable Joint parent) {
			this.name = parent == null ? "root" : info.get("name").getAsString();
			this.parent = parent;
			children = new ArrayList<>();
			if (info.has("x") && info.has("y")) {
				float x = info.get("x").getAsFloat();
				float y = info.get("y").getAsFloat();
				float r = (float) Math.hypot(x, y);
				float theta = (float) Math.toDegrees(Math.atan2(y, x));
				distanceFromParent = r;
				angleFromParent = theta;
			} else if (info.has("r") && info.has("theta")) {
				float r = info.get("r").getAsFloat();
				float theta = info.get("theta").getAsFloat();
				distanceFromParent = r;
				angleFromParent = theta;
			} else {
				distanceFromParent = 0;
				angleFromParent = 0;
			}
			this.angle = 0;
		}

		public void approachAngle(float angle, float tweening) {
			angle = (float) MathUtils.mod(angle, 360.0);
			if (angle - this.angle > 180) angle -= 360;
			else if (this.angle - angle > 180) angle += 360;

			float maxAngle = this.angle + tweening;
			float minAngle = this.angle - tweening;
			setAngle(MathUtils.clamp(minAngle, angle, maxAngle));
		}

		public void incrementAngle(float angle) {
			setAngle(this.angle + angle);
		}

		public void setAngle(float angle) {
			this.angle = (float) MathUtils.mod(angle, 360.0);
		}

		public float getAbsoluteAngle() {
			return parent == null ? angle : parent.getAbsoluteAngle() + angle;
		}

		public Vector getRootRelativePosition() {
			if (parent == null) {
				return getParentRelativePosition();
			} else {
				return parent.getRootRelativePosition().add(getParentRelativePosition());
			}
		}

		public Vector getParentRelativePosition() {
			float distance, angle;
			if (parent == null) {
				distance = (float) Math.hypot(horizontalOffset, verticalOffset);
				angle = (float) Math.toDegrees(Math.atan2(verticalOffset, horizontalOffset)) + this.angle;
			} else {
				distance = distanceFromParent;
				angle = getAbsoluteAngle() + angleFromParent - this.angle;
			}

			float x = (float) (scale * distance * Math.cos(Math.toRadians(angle)));
			float y = (float) (scale * distance * Math.sin(Math.toRadians(angle)));
			return new Vector(x, y);
		}

		private void addJoint(Joint child) {
			if (joints.containsKey(child.name)) {
				throw new IllegalArgumentException("A joint already exists with that name: " + name);
			}
			joints.put(child.name, child);
			children.add(child);
		}

		private void draw(Vector parentPos) {
			Draw draw = DungeonRaidersGame.getDraw();
			Vector myPos = parentPos.add(getParentRelativePosition());
			Sprites.WHITE_POINT.draw(draw.batch, myPos.x, myPos.y);
			draw.drawLine(parentPos.x, parentPos.y, myPos.x, myPos.y, 2, Color.RED);
			children.forEach(child -> child.draw(myPos));
		}

		@Override
		public String toString() {
			return parent == null ? name : parent.toString() + ":" + name;
		}
	}
}
