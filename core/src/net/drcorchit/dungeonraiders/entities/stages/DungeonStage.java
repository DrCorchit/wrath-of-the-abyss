package net.drcorchit.dungeonraiders.entities.stages;

import com.badlogic.gdx.graphics.Texture;
import net.drcorchit.dungeonraiders.assets.Textures;
import net.drcorchit.dungeonraiders.entities.actors.PhysicsActor;
import net.drcorchit.dungeonraiders.utils.Vector;

import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

public class DungeonStage extends Stage {

	public static final float BLOCK_SIZE = 45;
	public static final float ACTOR_MIN_Z = -BLOCK_SIZE;
	public static final float ACTOR_MAX_Z = BLOCK_SIZE;
	public static final float MAX_Z = BLOCK_SIZE * 4;
	public static final float CAMERA_Z = BLOCK_SIZE * 6;
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
	private HashMap<Vector, Room> rooms;

	public DungeonStage() {
		gravity = new Vector(0, -.4f);
		friction = 1;
		rooms = new HashMap<>();
	}

	public void addRoom(Room room) {
		super.addActor(room);
		rooms.put(room.getPosition(), room);
	}

	public Vector getGravity() {
		return gravity;
	}

	public float getMaxZ() {
		return ACTOR_MAX_Z;
	}

	public float getMinZ() {
		return ACTOR_MIN_Z;
	}

	public Vector projectZPosition(Vector location, float z) {
		//find the vector from the view center to the location
		Vector diff = location.subtract(getViewCenter());
		//multiply the diff by the zScale and add it to the original location
		return diff.multiply(getZScale(z)).add(location);
	}

	public Set<Room> getRelevantRooms(PhysicsActor actor) {
		return rooms.values().stream().filter(
				room -> actor.getShape().collidesWith(room.getRectangle())
		).collect(Collectors.toSet());
	}
}
