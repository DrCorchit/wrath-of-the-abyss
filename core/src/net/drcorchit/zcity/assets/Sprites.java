package net.drcorchit.zcity.assets;

import com.badlogic.gdx.graphics.Texture;
import net.drcorchit.zcity.utils.AnimatedSprite;

public class Sprites {
	public static final AnimatedSprite white = initSprite(Textures.white, 0, 0);
	public static final AnimatedSprite torso0 = initSprite(Textures.torso, 21, 12);
	public static final AnimatedSprite head0 = initSprite(Textures.head, 18, 20);
	public static final AnimatedSprite leftArm0 = initSprite(Textures.leftArm, 6, 24);
	public static final AnimatedSprite leftForearm0 = initSprite(Textures.leftForearm, 2, 41);
	public static final AnimatedSprite rightArm0 = initSprite(Textures.rightArm, 4, 24);
	public static final AnimatedSprite rightForearm0 = initSprite(Textures.rightForearm, 1, 41);
	public static final AnimatedSprite leftThigh0 = initSprite(Textures.leftThigh, 8, 39);
	public static final AnimatedSprite leftCalf0 = initSprite(Textures.leftCalf, 9, 62);
	public static final AnimatedSprite rightThigh0 = initSprite(Textures.rightThigh, 14, 32);
	public static final AnimatedSprite rightCalf0 = initSprite(Textures.rightCalf, 10, 63);

	private static AnimatedSprite initSprite(Texture texture, int xOrigin, int yOrigin) {
		return initSprite(texture, 1, 1, xOrigin, yOrigin);
	}

	private static AnimatedSprite initSprite(Texture texture, int hFrames, int vFrames, int xOrigin, int yOrigin) {
		AnimatedSprite spr = Textures.asSprite(texture, hFrames, vFrames);
		spr.setOffset(xOrigin, yOrigin);
		return spr;
	}

}
