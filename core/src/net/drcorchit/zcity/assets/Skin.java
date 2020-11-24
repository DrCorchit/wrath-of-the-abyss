package net.drcorchit.zcity.assets;

import com.badlogic.gdx.graphics.g2d.Sprite;
import net.drcorchit.zcity.ZCityGame;
import net.drcorchit.zcity.utils.AnimatedSprite;
import net.drcorchit.zcity.utils.FloatPair;
import net.drcorchit.zcity.utils.Pair;

import java.util.ArrayList;

public class Skin {

	private Skeleton skeleton;
	private ArrayList<SkinSprite> sprites;

	public Skin(Skeleton skeleton) {
		this.skeleton = skeleton;
		sprites = new ArrayList<>();
	}

	public static Skin newFemaleSkin(Skeleton skeleton) {
		Skin output = new Skin(skeleton);

		output.addSprite("left_shoulder", Sprites.leftArm0);
		output.addSprite("left_elbow", Sprites.leftForearm0);
		output.addSprite("left_hip", Sprites.leftThigh0);
		output.addSprite("left_knee", Sprites.leftCalf0);

		output.addSprite("root", Sprites.torso0);
		output.addSprite("neck", Sprites.head0);

		output.addSprite("right_hip", Sprites.rightThigh0);
		output.addSprite("right_knee", Sprites.rightCalf0);
		output.addSprite("right_shoulder", Sprites.rightArm0);
		output.addSprite("right_elbow", Sprites.rightForearm0);
		return output;
	}

	public void addSprite(String jointName, AnimatedSprite sprite) {
		Skeleton.Joint target = skeleton.getJoint(jointName);
		sprites.add(new SkinSprite(sprite, target, 0, 0, 0));
	}

	private class SkinSprite {
		private final Skeleton.Joint joint;
		private final AnimatedSprite sprite;
		private final float r, theta, angle;

		private SkinSprite(AnimatedSprite sprite, Skeleton.Joint joint, float r, float theta, float angle) {
			this.sprite = sprite;
			this.joint = joint;
			this.r = r;
			this.theta = theta;
			this.angle = angle;
		}

		public void draw(float sprX, float sprY, float rotation) {
			sprite.drawScaled(ZCityGame.draw().getBatch(), sprX, sprY, skeleton.scale, skeleton.scale, rotation);
		}
	}

	public void draw(float x, float y) {
		sprites.forEach(spr -> {
			FloatPair jointPos = spr.joint.getRootRelativePosition().add(x, y);
			float jointAngle = spr.joint.getAbsoluteAngle();
			float offsetAngle = jointAngle + spr.theta;
			float sprX = (float) (jointPos.key + spr.r * Math.cos(Math.toRadians(offsetAngle)));
			float sprY = (float) (jointPos.val + spr.r * Math.sin(Math.toRadians(offsetAngle)));
			float rotation = spr.joint.getAbsoluteAngle() + spr.angle;
			spr.draw(sprX, sprY, rotation);
		});
	}
}
