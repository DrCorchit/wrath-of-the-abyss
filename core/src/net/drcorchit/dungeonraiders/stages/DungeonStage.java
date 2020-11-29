package net.drcorchit.dungeonraiders.stages;

import com.badlogic.gdx.graphics.Texture;
import net.drcorchit.dungeonraiders.actors.DungeonActor;
import net.drcorchit.dungeonraiders.assets.Dungeon;
import net.drcorchit.dungeonraiders.assets.Dungeons;
import net.drcorchit.dungeonraiders.assets.Textures;
import net.drcorchit.dungeonraiders.drawing.shapes.Rectangle;
import net.drcorchit.dungeonraiders.utils.Coordinate;
import net.drcorchit.dungeonraiders.utils.Vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
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
		return CAMERA_Z / (CAMERA_Z - z);
	}

	private Vector gravity;
	private float friction;
	private Texture foregroundWall = Textures.WALL;
	private Texture backgroundWall = Textures.WALL;
	private HashMap<Coordinate, Room> rooms;
	private final Random random = new Random();

	public DungeonStage() {
		super();
		gravity = new Vector(0, -.4f);
		friction = 1;
		rooms = new HashMap<>();
	}

	public void addRoom(Room room) {
		super.addActor(room);
		Room oldRoom = rooms.get(room.getCoordinate());
		if (oldRoom != null) {
			oldRoom.destroy();
		}
		rooms.put(room.getCoordinate(), room);
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

	public Set<Room> getOverlappedRooms(DungeonActor<?> actor) {
		return getRoomCoordinates(actor.getViewBounds()).stream()
				.filter(rooms::containsKey)
				.map(rooms::get)
				.collect(Collectors.toSet());
	}

	@Override
	public void act(float factor) {
		loadVisibleRooms();
		super.act(factor);
	}

	private void loadVisibleRooms() {
		for (Coordinate c : getRoomCoordinates(viewBounds)) {
			if (!rooms.containsKey(c)) {
				Dungeon d = Dungeons.getRandomDungeon(Dungeons.dungeons, random::nextInt);
				Room r = new Room(this, c, d);
				addRoom(r);
			}
		}
	}

	private Set<Coordinate> getRoomCoordinates(Rectangle r) {
		HashSet<Coordinate> output = new HashSet<>();
		Vector minPos = r.getPosition().subtract(r.width / 2, r.height / 2);
		Vector maxPos = minPos.add(r.width, r.height);
		Coordinate minC = Room.positionToCoordinate(minPos);
		Coordinate maxC = Room.positionToCoordinate(maxPos);

		for (int i = minC.x; i <= maxC.x; i++) {
			for (int j = minC.y; j <= maxC.y; j++) {
				output.add(new Coordinate(i, j));
			}
		}

		return output;
	}
}
