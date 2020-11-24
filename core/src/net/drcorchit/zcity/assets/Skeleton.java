package net.drcorchit.zcity.assets;

import com.badlogic.gdx.graphics.Color;
import net.drcorchit.zcity.ZCityGame;
import net.drcorchit.zcity.utils.Draw;
import net.drcorchit.zcity.utils.FloatPair;

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
	}

	public Joint getJoint(String name) {
		return joints.get(name);
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

		public void incrementAngle(float angle) {
			this.angle += angle;
		}

		public void setAngle(float angle) {
			this.angle = angle;
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

		public Joint addJoint(String name, float distanceFromParent, float angleFromParent) {
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
		Joint neck = output.root.addJoint("neck", 52.0385f, 92.203f);

		Joint leftShoulder = output.root.addJoint("left_shoulder", 40.792f, 78.690f);
		Joint leftElbow = leftShoulder.addJoint("left_elbow", 23, -85);
		Joint leftHand = leftElbow.addJoint("left_hand", 35, -75);

		Joint rightShoulder = output.root.addJoint("right_shoulder", 42.544f, 113.550f);
		Joint rightElbow = rightShoulder.addJoint("right_elbow", 22, -90);
		Joint rightHand = rightElbow.addJoint("right_hand", 35, -70);

		Joint leftHip = output.root.addJoint("left_hip", 12.207f, -55.008f);
		Joint leftKnee = leftHip.addJoint("left_knee", 34, -90);
		Joint leftAnkle = leftKnee.addJoint("left_ankle", 54, -92);

		Joint rightHip = output.root.addJoint("right_hip", 14.866f, -109.65f);
		Joint rightKnee = rightHip.addJoint("right_knee", 30, -102);
		Joint rightAnkle = rightKnee.addJoint("right_ankle", 55, -95);
		return output;
	}
}
