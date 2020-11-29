package net.drcorchit.dungeonraiders.stages;

import com.badlogic.gdx.graphics.Texture;
import net.drcorchit.dungeonraiders.assets.Dungeons;
import net.drcorchit.dungeonraiders.assets.Textures;
import net.drcorchit.dungeonraiders.drawing.shapes.Rectangle;
import net.drcorchit.dungeonraiders.actors.DungeonActor;
import net.drcorchit.dungeonraiders.utils.Coordinate;
import net.drcorchit.dungeonraiders.utils.Vector;

import java.util.*;
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
		rooms.put(room.getCoordinate(), room);
	}

	public void addRoom(Coordinate location) {
		if (!rooms.containsKey(location)) {
			Room room = new Room(this, location, Dungeons.getRandomDungeon(Dungeons.dungeons, random::nextInt));
			addRoom(room);
		}
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

	public Set<Room> getRoomsInView() {
		HashSet<Coordinate> output = new HashSet<>();
		Vector view = getViewPosition();
		for (float x = view.x; x < view.x + viewBounds.width; x += Room.PIXEL_SIZE) {
			for (float y = view.y; y < view.y + viewBounds.height; y += Room.PIXEL_SIZE) {
				output.add(Room.positionToCoordinate(new Vector(x, y)));
			}
		}
		return output.stream().map(rooms::get).collect(Collectors.toSet());
	}

	public Set<Room> getOverlappedRooms(DungeonActor<?> actor) {
		HashSet<Coordinate> output = new HashSet<>();
		Rectangle actorViewBounds = actor.getViewBounds();
		//we want the lower left corner of the actor's viewBounds
		Vector pos = actor.getViewPosition();
		pos = pos.subtract(actorViewBounds.width / 2, actorViewBounds.height / 2);

		//get all coordinates that overlap actor's view bounds
		for (float x = pos.x; x < pos.x + actorViewBounds.width; x += Room.PIXEL_SIZE) {
			for (float y = pos.y; y < pos.y + actorViewBounds.height; y += Room.PIXEL_SIZE) {
				output.add(Room.positionToCoordinate(new Vector(x, y)));
			}
		}

		return output.stream().map(rooms::get)
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
	}
}
