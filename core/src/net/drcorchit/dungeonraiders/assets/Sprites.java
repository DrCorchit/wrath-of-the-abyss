package net.drcorchit.dungeonraiders.assets;

import com.badlogic.gdx.graphics.Texture;
import net.drcorchit.dungeonraiders.drawing.AnimatedSprite;
import net.drcorchit.dungeonraiders.drawing.SpriteList;

public class Sprites {
	public static final AnimatedSprite WHITE_POINT = initSprite(Textures.WHITE_POINT).asSprite();
	public static final AnimatedSprite WHITE_TILE = initSprite(Textures.WHITE_TILE).asSprite();

	private static SpriteList initSprite(Texture texture) {
		return initSprite(texture, 1, 1, 0, 0);
	}

	private static SpriteList initSprite(Texture texture, int xOrigin, int yOrigin) {
		return initSprite(texture, 1, 1, xOrigin, yOrigin);
	}

	private static SpriteList initSprite(Texture texture, int hFrames, int vFrames, int originX, int originY) {
		return Textures.asSpriteList(texture, hFrames, vFrames, originX, originY);
	}

	public static AnimatedSprite getSprite(Texture texture) {
		return getSprite(texture, 1, 1, 0, 0);
	}

	public static AnimatedSprite getSprite(Texture texture, int hFrames, int vFrames, int originX, int originY) {
		return new AnimatedSprite(Textures.asSpriteList(texture, hFrames, vFrames, originX, originY));
	}
}
