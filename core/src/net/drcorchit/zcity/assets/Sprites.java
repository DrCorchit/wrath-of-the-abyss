package net.drcorchit.zcity.assets;

import com.badlogic.gdx.graphics.Texture;
import net.drcorchit.zcity.utils.AnimatedSprite;
import net.drcorchit.zcity.utils.SpriteList;

public class Sprites {
	public static final AnimatedSprite white = initSprite(Textures.white).asSprite();

	private static SpriteList initSprite(Texture texture) {
		return initSprite(texture, 1, 1, 0, 0);
	}

	private static SpriteList initSprite(Texture texture, int xOrigin, int yOrigin) {
		return initSprite(texture, 1, 1, xOrigin, yOrigin);
	}

	private static SpriteList initSprite(Texture texture, int hFrames, int vFrames, int originX, int originY) {
		return Textures.asSpriteList(texture, hFrames, vFrames, originX, originY);
	}
}
