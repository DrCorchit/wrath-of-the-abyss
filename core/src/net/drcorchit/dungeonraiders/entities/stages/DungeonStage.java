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
	//increase
	public static final float CAMERA_Z = BLOCK_SIZE * 10;
	public static final float FOREGROUND_Z = BLOCK_SIZE;
	public static final float MIDGROUND_Z = BLOCK_SIZE * -1;
	public static final float BACKGROUND_Z = BLOCK_SIZE * -4;

	//converts z value to foreshortening scale factor
	public static float getZScale(float z) {
		return CAMERA_Z/(CAMERA_Z - z);
	}

	private Vector gravity;
	private float friction;
	private Texture foregroundWall = Textures.WALL;
	private Texture backgroundWall = Textures.WALL;
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
		Vector center = getViewCenter();
		//find the vector from the view center to the location
		Vector diff = location.subtract(center);
		//multiply the diff by the zScale and add it to the original location
		return diff.multiply(getZScale(z)).add(center);
	}

	public Set<Room> getOverlappedRooms(PhysicsActor actor) {
		return rooms.values().stream().filter(
				room -> actor.getShape().collidesWith(room.getRectangle())
		).collect(Collectors.toSet());
	}
}
