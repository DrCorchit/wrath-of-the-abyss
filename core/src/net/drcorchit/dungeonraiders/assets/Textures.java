package net.drcorchit.dungeonraiders.assets;

import com.badlogic.gdx.graphics.Texture;
import net.drcorchit.dungeonraiders.utils.Logger;
import net.drcorchit.dungeonraiders.drawing.SpriteList;

public class Textures {

	public static final Texture BLG, WHITE_POINT, WHITE_TILE, TILES, DUNGEON, BRICK, VOLCANIC, ROCK;
	public static final Texture STONE_BRICKS;

	private static final Logger log;

	static {
		log = Logger.getLogger(Textures.class);

		BLG = initTexture("badlogic.jpg");
		WHITE_POINT = initTexture("white.png");
		WHITE_TILE = initTexture("white_tile.png");
		TILES = initTexture("tiles_7_small.png");
		DUNGEON = initTexture("walls/dungeon.png");
		BRICK = initTexture("walls/brick.png");
		VOLCANIC = initTexture("walls/volcanic.png");
		ROCK = initTexture("walls/rock.png");
		STONE_BRICKS = initTexture("walls/stone_bricks.png");
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
