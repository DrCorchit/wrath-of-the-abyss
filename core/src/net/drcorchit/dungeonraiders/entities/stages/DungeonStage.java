package net.drcorchit.dungeonraiders.entities.stages;

import com.badlogic.gdx.graphics.Texture;
import net.drcorchit.dungeonraiders.assets.Textures;
import net.drcorchit.dungeonraiders.utils.Vector;

import java.util.HashMap;

public class DungeonStage extends Stage {

	private static final float Z_SCALE = 1.25f;
	private static final float BLOCK_SIZE = 45;
	private static final float PLAYER_MIN_Z = -BLOCK_SIZE;
	private static final float PLAYER_MAX_Z = BLOCK_SIZE;
	private static final float MAX_Z = BLOCK_SIZE * 4;
	private static final float CAMERA_Z = BLOCK_SIZE * 6;
	private static final float FOREGROUND_SCALE = getZScale(2 * BLOCK_SIZE);
	private static final float MIDGROUND_SCALE_BEGIN = getZScale(BLOCK_SIZE);
	private static final float MIDGROUND_SCALE_END = getZScale(-BLOCK_SIZE);
	private static final float BACKGROUND_SCALE = getZScale(-2 * BLOCK_SIZE);

	//converts z value to foreshortening scale factor
	public static float getZScale(float z) {
		return CAMERA_Z/(CAMERA_Z - z);
	}

	private Vector gravity;
	private float friction;
	private Texture foregroundWall = Textures.wall;
	private Texture backgroundWall = Textures.wall;
	private Vector cameraPos;
	private HashMap<Vector, Room> rooms;

	public DungeonStage() {
		gravity = new Vector(0, -.1f);
		friction = 1;
	}

	public Vector getGravity() {
		return gravity;
	}

	public float getMaxZ() {
		return PLAYER_MAX_Z;
	}

	public float getMinZ() {
		return PLAYER_MIN_Z;
	}
}
