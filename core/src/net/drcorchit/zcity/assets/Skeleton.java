package net.drcorchit.zcity.assets;

import com.badlogic.gdx.graphics.Color;
import net.drcorchit.zcity.ZCityGame;
import net.drcorchit.zcity.utils.Draw;
import net.drcorchit.zcity.utils.FloatPair;
import net.drcorchit.zcity.utils.MathUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;

public class Skeleton {

	public float scale;
	@Nonnull
	public final Joint root;
	private final HashMap<String, Joint> joints;

	public Skeleton() {
		root = new Joint("root", null, 0, 0);
		joints = new HashMap<>();
		joints.put(root.name, root);
		scale = 1;
	}

	public Joint getJoint(String name) {
		Joint output = joints.get(name);
		if (output == null) {
			throw new IllegalArgumentException("No joint with that name was found in the skeleton: " + name);
		}
		return output;
	}

	public void draw(float x, float y) {
		root.draw(new FloatPair(x, y));
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

		private Joint(String name, @Nullable Joint parent, float distanceFromParent, float angleFromParent) {
			this.name = name;
			this.parent = parent;
			children = new ArrayList<>();
			this.distanceFromParent = distanceFromParent;
			this.angleFromParent = angleFromParent;
			angle = 0;
		}

		public void approachAngle(float angle, float tweening) {
			angle = (float) MathUtils.mod(angle, 360.0);
			if (angle - this.angle > 180) angle -= 360;
			else if (this.angle - angle > 180) angle += 360;

			float maxAngle = this.angle + tweening;
			float minAngle = this.angle - tweening;
			setAngle(MathUtils.clamp(minAngle, angle, maxAngle));
			System.out.printf("joint %s %.1f < %.1f < %.1f -> %.1f\n", name, minAngle, angle, maxAngle, this.angle);
		}

		public void incrementAngle(float angle) {
			setAngle(this.angle + angle);
		}

		public void setAngle(float angle) {
			this.angle = (float) MathUtils.mod(angle, 360.0);
		}

		public float getAngle() {
			return angle;
		}

		public float getAbsoluteAngle() {
			return parent == null ? angle : parent.getAbsoluteAngle() + angle;
		}

		public FloatPair getRootRelativePosition() {
			if (parent == null) {
				return new FloatPair(0f, 0f);
			} else {
				return parent.getRootRelativePosition().add(getParentRelativePosition());
			}
		}

		public FloatPair getParentRelativePosition() {
			if (parent == null) {
				return new FloatPair(0f, 0f);
			} else {
				float angle = getAbsoluteAngle() + angleFromParent - this.angle;
				float x = (float) (scale * distanceFromParent * Math.cos(Math.toRadians(angle)));
				float y = (float) (scale * distanceFromParent * Math.sin(Math.toRadians(angle)));
				return new FloatPair(x, y);
			}
		}

		public Joint addJointCartesian(String name, float parentX, float parentY) {
			float distance = (float) MathUtils.distance(0, 0, parentX, parentY);
			float angle = (float) Math.toDegrees(Math.atan2(parentY, parentX));
			return addJointPolar(name, distance, angle);
		}

		public Joint addJointPolar(String name, float distanceFromParent, float angleFromParent) {
			if (joints.containsKey(name)) {
				throw new IllegalArgumentException("A joint already exists with that name: "+name);
			}
			Joint child = new Joint(name, this, distanceFromParent, angleFromParent);
			joints.put(child.name, child);
			children.add(child);
			return child;
		}

		private void draw(FloatPair parentPos) {
			Draw draw = ZCityGame.getInstance().draw;
			FloatPair myPos = parentPos.add(getParentRelativePosition());
			Sprites.white.draw(draw.getBatch(), myPos.key, myPos.val);
			draw.drawLine(parentPos.key, parentPos.val, myPos.key, myPos.val, 2, Color.RED);
			children.forEach(child -> child.draw(myPos));
		}

		@Override
		public String toString() {
			return parent == null ? name : parent.toString() + ":" + name;
		}
	}

	public static Skeleton newFemaleSkeleton() {
		Skeleton output = new Skeleton();
		Joint neck = output.root.addJointCartesian("neck", -3, 52);

		Joint leftShoulder = output.root.addJointCartesian("left_shoulder", 10, 43);
		Joint leftElbow = leftShoulder.addJointCartesian("left_elbow", 0, -24);
		Joint leftHand = leftElbow.addJointCartesian("left_hand", 7, -34);

		Joint rightShoulder = output.root.addJointCartesian("right_shoulder", -20, 40);
		Joint rightElbow = rightShoulder.addJointCartesian("right_elbow", 0, -22);
		Joint rightHand = rightElbow.addJointCartesian("right_hand", 10, -33);

		Joint leftHip = output.root.addJointCartesian("left_hip", 7f, -11f);
		Joint leftKnee = leftHip.addJointCartesian("left_knee", 0, -35);
		Joint leftAnkle = leftKnee.addJointCartesian("left_ankle", -2, -57);

		Joint rightHip = output.root.addJointCartesian("right_hip", -7, -15);
		Joint rightKnee = rightHip.addJointCartesian("right_knee", -9, -32);
		Joint rightAnkle = rightKnee.addJointCartesian("right_ankle", -3, -56);
		return output;
	}
}
