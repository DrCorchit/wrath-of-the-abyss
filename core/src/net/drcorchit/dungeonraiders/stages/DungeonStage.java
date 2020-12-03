package net.drcorchit.dungeonraiders.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.google.common.collect.ImmutableList;
import net.drcorchit.dungeonraiders.DungeonRaidersGame;
import net.drcorchit.dungeonraiders.actors.Actor;
import net.drcorchit.dungeonraiders.actors.DungeonActor;
import net.drcorchit.dungeonraiders.actors.HasLightSources;
import net.drcorchit.dungeonraiders.actors.Room;
import net.drcorchit.dungeonraiders.assets.Dungeons;
import net.drcorchit.dungeonraiders.assets.RoomLayout;
import net.drcorchit.dungeonraiders.assets.Sprites;
import net.drcorchit.dungeonraiders.assets.Textures;
import net.drcorchit.dungeonraiders.drawing.*;
import net.drcorchit.dungeonraiders.drawing.shapes.Rectangle;
import net.drcorchit.dungeonraiders.utils.Coordinate;
import net.drcorchit.dungeonraiders.utils.MathUtils;
import net.drcorchit.dungeonraiders.utils.Utils;
import net.drcorchit.dungeonraiders.utils.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DungeonStage extends Stage {

	public static final float BLOCK_SIZE = 45;
	public static final float ACTOR_MIN_Z = -BLOCK_SIZE;
	public static final float ACTOR_MAX_Z = BLOCK_SIZE;
	//increase this to decrease the "depthiness" of the game
	//if an object's z position increases to this size, it's zscale becomes infinte
	public static final float CAMERA_Z = BLOCK_SIZE * 10;

	//converts z value to foreshortening scale factor
	public static float getZScale(float z) {
		return CAMERA_Z / (CAMERA_Z - z);
	}

	private final Surface lightTempSurface;
	@NotNull
	private final ImmutableList<Surface> wallSurfaces, maskSurfaces, lightSurfaces;
	@NotNull
	private Vector gravity;
	private float friction;
	@NotNull
	private final HashMap<Coordinate, Room> rooms;
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

		//init surfaces, one surface for every layer
		//n layers -> 3*n surfaces
		ImmutableList.Builder<Surface> wallBuilder = ImmutableList.builderWithExpectedSize(layers);
		ImmutableList.Builder<Surface> maskBuilder = ImmutableList.builderWithExpectedSize(layers);
		ImmutableList.Builder<Surface> lightBuilder = ImmutableList.builderWithExpectedSize(layers);
		for (int i = 0; i < layers; i++) {
			wallBuilder.add(new Surface());
			maskBuilder.add(new Surface());
			lightBuilder.add(new Surface());

		}
		wallSurfaces = wallBuilder.build();
		maskSurfaces = maskBuilder.build();
		lightSurfaces = lightBuilder.build();
		lightTempSurface = new Surface();

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

	public Set<Room> getOverlappedRooms(DungeonActor<?> actor) {
		return getRoomCoordinates(actor.getViewBounds()).stream()
				.filter(rooms::containsKey)
				.map(rooms::get)
				.collect(Collectors.toSet());
	}

	@Override
	public void act(float factor) {
		super.act(factor);
		loadVisibleRooms();
		DungeonRaidersGame.getInstance().debugInfoMap.put("rooms", rooms.size());
	}

	private void drawLighting() {

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

		float backgroundZ = (getLayerCount() - 1) * -BLOCK_SIZE;
		float backgroundScale = getZScale(backgroundZ);
		instructions.add(new RunnableRenderInstruction(() -> {
			Vector projectedPos = projectZPosition(getViewPosition().multiply(-1), backgroundZ);
			//tile the wall surface with the wall sprite
			backgroundSprite.drawTiled(draw.batch, projectedPos.x, projectedPos.y, backgroundScale, backgroundScale);
		}, backgroundZ, -4f));

		for (int layerIndex = 0; layerIndex < getLayerCount(); layerIndex++) {
			//Setup wall masks
			Surface maskSurface = maskSurfaces.get(layerIndex);
			maskSurface.begin();
			maskSurface.clear();
			draw.batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);

			for (Room room : roomSet) {
				Vector roomViewPos = room.getPosition().subtract(getViewPosition());
				room.getLayer(layerIndex).drawMask(roomViewPos);
			}

			draw.batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			maskSurface.end();
			//End wall masks

			//Setup wall surfaces
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
			//end wall surfaces
			//add render instruction to draw wall
			RenderInstruction wall = new RunnableRenderInstruction(() ->
					wallSurface.createSprite().draw(draw.batch), z, -3f);
			instructions.add(wall);

			//Setup light surfaces
			AnimatedSprite lightSprite = Sprites.getSprite(Textures.LIGHT);
			lightSprite.setOffsetCentered();
			Surface lightSurface = lightSurfaces.get(layerIndex);
			lightSurface.begin();
			lightSurface.clear(new Color(.4f, .4f, .4f, 1f));
			lightSurface.end();

			for (Actor<?> actor : getActors()) {
				if (actor instanceof HasLightSources) {
					Collection<LightSource> lightSources = ((HasLightSources) actor).getLightSources();
					for (LightSource lightSource : lightSources) {
						lightSource.update();
						lightTempSurface.begin();
						lightTempSurface.clear();
						draw.batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);
						lightSprite.setBlend(lightSource.getLightColor());
						lightSprite.setAlpha(MathUtils.clamp(0f, lightSource.getLightIntensity(), 1f));
						Vector lightPos = projectZPosition(lightSource.getLightPosition().subtract(getViewPosition()), z);
						float lightScale = 2 * lightSource.getLightRadius() / lightSprite.getMinWidth();
						lightSprite.drawScaled(draw.batch, lightPos.x, lightPos.y, lightScale, lightScale, 0);

						if (lightSource.isGeometric()) {
							for (Room room : roomSet) {
								Vector roomViewPos = room.getPosition().subtract(getViewPosition());
								room.getLayer(layerIndex).drawLightGeometry(roomViewPos, lightSource);
							}
						}

						lightTempSurface.end();

						//draw lightTempSurface to lightSurface additively
						lightSurface.begin();
						draw.batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
						lightTempSurface.createSprite().draw(draw.batch);
						draw.batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
						lightSurface.end();
					}
				}
			}

			//Let there be light!
			RenderInstruction light = new RunnableRenderInstruction(() -> {
				draw.batch.setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_ZERO);
				lightSurface.createSprite().draw(draw.batch);
				draw.batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			}, z, -2f);
			instructions.add(light);
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
				//DungeonTile dungeonTile = Dungeons.getRandomDungeon(Dungeons.DUNGEON_TILES, random::nextInt);
				RoomLayout roomLayout = getRandomDungeon(c);

				Room r = new Room(this, c, roomLayout);
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

	public RoomLayout getRandomDungeon(Coordinate c) {
		Room top = rooms.get(new Coordinate(c.x, c.y + 1));
		Room left = rooms.get(new Coordinate(c.x - 1, c.y));
		Room right = rooms.get(new Coordinate(c.x + 1, c.y));
		Room bottom = rooms.get(new Coordinate(c.x, c.y - 1));

		Predicate<RoomLayout> rule = (layout) -> {
			if (top != null && Utils.getIntersectionSize(top.bottomTags, layout.topTags) == 0) return false;
			if (left != null && Utils.getIntersectionSize(left.rightTags, layout.leftTags) == 0) return false;
			if (right != null && Utils.getIntersectionSize(right.leftTags, layout.rightTags) == 0) return false;
			if (bottom != null && Utils.getIntersectionSize(bottom.topTags, layout.bottomTags) == 0) return false;
			return true;
		};

		return Dungeons.getRandomDungeon(rule, random);
	}
}
