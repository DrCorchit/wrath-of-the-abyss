package net.drcorchit.dungeonraiders.assets;

import com.badlogic.gdx.graphics.Texture;
import net.drcorchit.dungeonraiders.utils.Logger;
import net.drcorchit.dungeonraiders.utils.SpriteList;

public class Textures {

	public static final Texture white, wall;

	private static final Logger log;

	static {
		log = Logger.getLogger(Textures.class);

		white = initTexture("white.png");
		wall = initTexture("walls/dungeon.png");
	}

	public static SpriteList asSpriteList(Texture texture) {
		return asSpriteList(texture, 1, 1, 0, 0);
	}

	public static SpriteList asSpriteList(Texture texture, int framesHoriz, int framesVert, float originX, float originY) {
		return new SpriteList(texture, framesHoriz, framesVert, originX, originY);
	}

	private static Texture initTexture(String name) {
		Texture output = LocalAssets.getInstance().getTexture(name);
		if (output == null) {
			log.error("initTexture", "Texture " + name + " failed to load");
		}
		return output;
	}
}
