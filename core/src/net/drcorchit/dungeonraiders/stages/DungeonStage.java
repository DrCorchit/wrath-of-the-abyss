package net.drcorchit.dungeonraiders.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.google.common.collect.ImmutableList;
import net.drcorchit.dungeonraiders.actors.Actor;
import net.drcorchit.dungeonraiders.actors.DungeonActor;
import net.drcorchit.dungeonraiders.actors.Room;
import net.drcorchit.dungeonraiders.assets.Dungeon;
import net.drcorchit.dungeonraiders.assets.Dungeons;
import net.drcorchit.dungeonraiders.assets.Sprites;
import net.drcorchit.dungeonraiders.assets.Textures;
import net.drcorchit.dungeonraiders.drawing.AnimatedSprite;
import net.drcorchit.dungeonraiders.drawing.RenderInstruction;
import net.drcorchit.dungeonraiders.drawing.RunnableRenderInstruction;
import net.drcorchit.dungeonraiders.drawing.Surface;
import net.drcorchit.dungeonraiders.drawing.shapes.Rectangle;
import net.drcorchit.dungeonraiders.utils.Coordinate;
import net.drcorchit.dungeonraiders.utils.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class DungeonStage extends Stage {

	public static final float BLOCK_SIZE = 45;
	public static final float ACTOR_MIN_Z = -BLOCK_SIZE;
	public static final float ACTOR_MAX_Z = BLOCK_SIZE;
	//increase
	public static final float CAMERA_Z = BLOCK_SIZE * 8;
	public static final float FOREGROUND_Z = BLOCK_SIZE;
	public static final float MIDGROUND_Z = BLOCK_SIZE * -1;
	public static final float BACKGROUND_Z = BLOCK_SIZE * -4;

	//converts z value to foreshortening scale factor
	public static float getZScale(float z) {
		return CAMERA_Z / (CAMERA_Z - z);
	}

	@NotNull
	private ImmutableList<Surface> wallSurfaces, maskSurfaces;
	@NotNull
	private Vector gravity;
	private float friction;
	@NotNull
	private final HashMap<Coordinate, net.drcorchit.dungeonraiders.actors.Room> rooms;
	@NotNull
	private final Random random = new Random();
	@NotNull
	public AnimatedSprite floorSprite, backgroundSprite;
	@NotNull
	public final AnimatedSprite[] wallSprites;

	public DungeonStage(int layers) {
		super();
		gravity = new Vector(0, -.4f);
		friction = 1;
		rooms = new HashMap<>();

		//one surface for every layer
		ImmutableList.Builder<Surface> wallBuilder = ImmutableList.builderWithExpectedSize(layers);
		ImmutableList.Builder<Surface> maskBuilder = ImmutableList.builderWithExpectedSize(layers);
		for (int i = 0; i < layers; i++) {
			wallBuilder.add(new Surface());
			maskBuilder.add(new Surface());
		}
		wallSurfaces = wallBuilder.build();
		maskSurfaces = maskBuilder.build();
		floorSprite = Sprites.getSprite(Textures.TILES);
		backgroundSprite = Sprites.getSprite(Textures.STONE_BRICKS);
		wallSprites = new AnimatedSprite[getLayerCount()];
		for (int i = 0; i < getLayerCount(); i++) {
			wallSprites[i] = Sprites.getSprite(Textures.DUNGEON);
		}
	}

	public int getLayerCount() {
		return wallSurfaces.size();
	}

	public void addRoom(net.drcorchit.dungeonraiders.actors.Room room) {
		super.addActor(room);
		net.drcorchit.dungeonraiders.actors.Room oldRoom = rooms.get(room.getCoordinate());
		if (oldRoom != null) {
			oldRoom.destroy();
		}
		rooms.put(room.getCoordinate(), room);
	}

	@NotNull
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
		float scale = getZScale(z);
		//Vector center = getViewCenter();
		Vector center = getViewOffset();
		//find the vector from the view center to the location
		Vector diff = location.subtract(center);
		//multiply the diff by the zScale and add it to the original location
		return diff.multiply(scale).add(center);
	}

	public Set<net.drcorchit.dungeonraiders.actors.Room> getOverlappedRooms(DungeonActor<?> actor) {
		return getRoomCoordinates(actor.getViewBounds()).stream()
				.filter(rooms::containsKey)
				.map(rooms::get)
				.collect(Collectors.toSet());
	}

	@Override
	public void act(float factor) {
		super.act(factor);
		loadVisibleRooms();
	}

	@Override
	public void draw() {
		ArrayList<RenderInstruction> instructions = new ArrayList<>();

		for (Actor<?> actor : getActors()) {
			if (actor.isInView()) {
				Vector adjustedPos = actor.getPosition().subtract(getViewPosition());
				//actor.getViewBounds(adjustedPos).draw(Color.GREEN);
				instructions.addAll(actor.draw(adjustedPos));
			}
		}

		Set<Coordinate> cSet = getRoomCoordinates(viewBounds);
		Set<Room> roomSet = cSet.stream().map(rooms::get).collect(Collectors.toSet());

		float backgroundZ = (getLayerCount()-1) * -BLOCK_SIZE;
		float backgroundScale = getZScale(backgroundZ);
		instructions.add(new RunnableRenderInstruction(() -> {
			Vector projectedPos = projectZPosition(getViewPosition().multiply(-1), backgroundZ);
			//tile the wall surface with the wall sprite
			backgroundSprite.drawTiled(draw.batch, projectedPos.x, projectedPos.y, backgroundScale, backgroundScale);
		}, backgroundZ, -3f));

		for (int layerIndex = 0; layerIndex < getLayerCount(); layerIndex++) {
			Surface maskSurface = maskSurfaces.get(layerIndex);
			maskSurface.begin();
			maskSurface.clear();
			draw.batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);

			for (Room room : roomSet) {
				room.getLayer(layerIndex).drawMask(room.getPosition());
			}

			draw.batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			maskSurface.end();

			Surface wallSurface = wallSurfaces.get(layerIndex);
			wallSurface.begin();
			wallSurface.clear();
			float z = (layerIndex - 1) * -BLOCK_SIZE;
			float scale = getZScale(z);

			Vector projectedPos = projectZPosition(getViewPosition().multiply(-1), z);
			//tile the wall surface with the wall sprite
			wallSprites[layerIndex].drawTiled(draw.batch, projectedPos.x, projectedPos.y, scale, scale);
			//draw the mask multiplicatively
			draw.batch.setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_ZERO);
			maskSurface.createSprite().draw(draw.batch);
			draw.batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			wallSurface.end();

			RenderInstruction instruction = new RunnableRenderInstruction(() -> wallSurface.createSprite().draw(draw.batch), z, -2f);
			instructions.add(instruction);
		}

		Collections.sort(instructions);

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		draw.batch.begin();
		instructions.forEach(RenderInstruction::draw);
		draw.batch.end();
	}

	private void loadVisibleRooms() {
		for (Coordinate c : getRoomCoordinates(viewBounds)) {
			if (!rooms.containsKey(c)) {
				Dungeon[] dungeons = new Dungeon[getLayerCount()];
				for (int i = 0; i < getLayerCount(); i++) {
					dungeons[i] = Dungeons.getRandomDungeon(Dungeons.dungeons, random::nextInt);
				}
				net.drcorchit.dungeonraiders.actors.Room r = new net.drcorchit.dungeonraiders.actors.Room(this, c, dungeons);
				addRoom(r);
			}
		}
	}

	private Set<Coordinate> getRoomCoordinates(Rectangle r) {
		HashSet<Coordinate> output = new HashSet<>();
		Vector minPos = r.getPosition().subtract(r.width / 2, r.height / 2);
		Vector maxPos = minPos.add(r.width, r.height);
		Coordinate minC = net.drcorchit.dungeonraiders.actors.Room.positionToCoordinate(minPos);
		Coordinate maxC = Room.positionToCoordinate(maxPos);

		for (int i = minC.x; i <= maxC.x; i++) {
			for (int j = minC.y; j <= maxC.y; j++) {
				output.add(new Coordinate(i, j));
			}
		}

		return output;
	}
}
