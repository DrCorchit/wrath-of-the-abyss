package net.drcorchit.zcity.assets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.drcorchit.zcity.utils.AnimatedSprite;
import net.drcorchit.zcity.utils.IOUtils;
import net.drcorchit.zcity.utils.Logger;
import net.drcorchit.zcity.utils.SpriteList;

import java.io.File;

public class Textures {

	public static final Texture white;
	public static final Texture torso, head, leftArm, rightArm, leftThigh, rightThigh;
	public static final Texture leftForearm, rightForearm, leftCalf, rightCalf;

	private static final Logger log;

	static {
		log = Logger.getLogger(Textures.class);

		white = initTexture("white.png");
		torso = initTexture("skins/0/torso.png");
		head = initTexture("skins/0/head.png");
		leftArm = initTexture("skins/0/arm_left.png");
		rightArm = initTexture("skins/0/arm_right.png");
		leftThigh = initTexture("skins/0/thigh_left.png");
		rightThigh = initTexture("skins/0/thigh_right.png");
		leftForearm = initTexture("skins/0/forearm_left.png");
		rightForearm = initTexture("skins/0/forearm_right.png");
		leftCalf = initTexture("skins/0/calf_left.png");
		rightCalf = initTexture("skins/0/calf_right.png");
	}

	public static TextureRegion asRegion(Texture texture) {
		return new TextureRegion(texture);
	}

	public static AnimatedSprite asSprite(Texture texture) {
		return asSprite(texture, 1, 1);
	}

	public static AnimatedSprite asSprite(Texture texture, int frames) {
		return asSprite(texture, frames, 1);
	}

	public static AnimatedSprite asSprite(Texture texture, int i, int j) {
		return new AnimatedSprite(new SpriteList(texture, i, j));
	}

	private static Texture initTexture(String name) {
		Texture output = LocalAssets.getInstance().getTexture(name);
		if (output == null) {
			log.error("initTexture", "Texture " + name + " failed to load");
		}
		return output;
	}
}
