package net.drcorchit.dungeonraiders.stages;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import net.drcorchit.dungeonraiders.assets.Dungeon;
import net.drcorchit.dungeonraiders.assets.Sprites;
import net.drcorchit.dungeonraiders.assets.Textures;
import net.drcorchit.dungeonraiders.drawing.AnimatedSprite;
import net.drcorchit.dungeonraiders.drawing.RenderInstruction;
import net.drcorchit.dungeonraiders.drawing.RunnableRenderInstruction;
import net.drcorchit.dungeonraiders.drawing.Surface;
import net.drcorchit.dungeonraiders.drawing.shapes.Rectangle;
import net.drcorchit.dungeonraiders.drawing.shapes.Shape;
import net.drcorchit.dungeonraiders.drawing.shapes.Square;
import net.drcorchit.dungeonraiders.actors.Actor;
import net.drcorchit.dungeonraiders.actors.DungeonActor;
import net.drcorchit.dungeonraiders.utils.*;

import java.util.ArrayList;
import java.util.Collection;

public class Room extends Actor<net.drcorchit.dungeonraiders.stages.DungeonStage> {

	public static final int SIZE = 16;
	public static final float PIXEL_SIZE = SIZE * net.drcorchit.dungeonraiders.stages.DungeonStage.BLOCK_SIZE;

	private final Layer[] layers;
	private final Coordinate coordinate;
	private final int lastlayerIndex;

	public Room(net.drcorchit.dungeonraiders.stages.DungeonStage stage, Coordinate coordinate, int numLayers) {
		super(stage, coordinateToPosition(coordinate));
		this.coordinate = coordinate;
		layers = new Layer[numLayers];
		lastlayerIndex = layers.length - 1;

		//initialize layers
		for (int i = 0; i < numLayers; i++) {
			layers[i] = new Layer(i);
		}

		setViewBounds(PIXEL_SIZE / 2, PIXEL_SIZE / 2, PIXEL_SIZE, PIXEL_SIZE);
	}

	public Room(net.drcorchit.dungeonraiders.stages.DungeonStage stage, Coordinate coordinate, Dungeon... dungeons) {
		this(stage, coordinate, dungeons.length);
		//initialize layers
		for (int i = 0; i < dungeons.length; i++) {
			dungeons[i].copyToLayer(layers[i]);
		}
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public Layer getLayer(int index) {
		return layers[MathUtils.clamp(0, index, lastlayerIndex)];
	}

	public static int getLayerIndex(float z) {
		int index = (int) Math.ceil(-z / net.drcorchit.dungeonraiders.stages.DungeonStage.BLOCK_SIZE);
		return Math.max(0, index);
	}

	@Override
	public void act(float delta) {
		//currently no-op
	}

	public boolean collidesWith(DungeonActor<?> actor, Vector position, float z) {
		Layer layer = getLayer(getLayerIndex(z));
		return layer.collidesWith(actor.getCollider().move(position.add(actor.getColliderOffset())));
	}

	public Collection<RenderInstruction> draw(Vector pos) {
		//rectangle.move(pos.add(SIZE / 2, SIZE / 2)).draw(Color.RED);
		//draw from back to front
		ArrayList<RenderInstruction> output = new ArrayList<>();

		for (int i = lastlayerIndex; i >= 0; i--) {
			Layer layer = layers[i];
			output.add(new RunnableRenderInstruction(layer.z, () -> layer.draw(pos)));
		}
		return output;
	}

	public class Layer {
		public AnimatedSprite floorSprite, wallSprite;
		private final net.drcorchit.dungeonraiders.drawing.Surface surface, maskSurface;
		public final float z;
		public final Grid<Shape> grid = new Grid<>(Shape.class, SIZE, SIZE);

		private Layer(int index) {
			this.z = index * -net.drcorchit.dungeonraiders.stages.DungeonStage.BLOCK_SIZE;
			floorSprite = Sprites.getSprite(Textures.FLOOR);
			wallSprite = Sprites.getSprite(Textures.WALL);
			surface = new net.drcorchit.dungeonraiders.drawing.Surface();
			maskSurface = new Surface();
		}

		private Layer(int index, Dungeon dungeon) {
			this(index);
			dungeon.copyToLayer(this);
		}

		public Vector getCellLocation(int i, int j) {
			float x = getPosition().x + (i + .5f) * net.drcorchit.dungeonraiders.stages.DungeonStage.BLOCK_SIZE;
			float y = getPosition().y + (j + .5f) * net.drcorchit.dungeonraiders.stages.DungeonStage.BLOCK_SIZE;
			return new Vector(x, y);
		}

		public void placeSquare(int i, int j) {
			Rectangle r = new Square(() -> getCellLocation(i, j), net.drcorchit.dungeonraiders.stages.DungeonStage.BLOCK_SIZE);
			grid.set(i, j, r);
		}

		public void draw(Vector roomPos) {
			float farScale = net.drcorchit.dungeonraiders.stages.DungeonStage.getZScale(z);
			float nearScale = net.drcorchit.dungeonraiders.stages.DungeonStage.getZScale(z + net.drcorchit.dungeonraiders.stages.DungeonStage.BLOCK_SIZE);
			float farSize = net.drcorchit.dungeonraiders.stages.DungeonStage.BLOCK_SIZE * nearScale / 2;
			float nearSize = net.drcorchit.dungeonraiders.stages.DungeonStage.BLOCK_SIZE * farScale / 2;

			surface.begin();
			surface.clear();
			Vector projectedPos = stage.projectZPosition(roomPos, z + net.drcorchit.dungeonraiders.stages.DungeonStage.BLOCK_SIZE);
			wallSprite.drawTiled(stage.draw.batch, projectedPos.x, projectedPos.y);
			surface.end();

			maskSurface.begin();
			maskSurface.clear();
			maskSurface.end();

			grid.forEachCell((i, j) -> {
				Shape shape = grid.get(i, j);
				if (shape != null) {
					Vector relativePos = getCellLocation(i, j).subtract(getPosition()).add(roomPos);
					//shape.move(relativePos).draw(Color.BLUE);
					Vector projectedPosFar = stage.projectZPosition(relativePos, z);
					Vector projectedPosNear = stage.projectZPosition(relativePos, z + DungeonStage.BLOCK_SIZE);
					//there 8 corners of the cube:
					//1 2
					//3 4
					Vector farC1 = projectedPosFar.add(-farSize, farSize);
					Vector farC2 = projectedPosFar.add(farSize, farSize);
					Vector farC3 = projectedPosFar.add(-farSize, -farSize);
					Vector farC4 = projectedPosFar.add(farSize, -farSize);
					Vector nearC1 = projectedPosNear.add(-nearSize, nearSize);
					Vector nearC2 = projectedPosNear.add(nearSize, nearSize);
					Vector nearC3 = projectedPosNear.add(-nearSize, -nearSize);
					Vector nearC4 = projectedPosNear.add(nearSize, -nearSize);

					boolean drawTop = farC1.y > nearC1.y && (j == grid.getHeight() - 1 || grid.get(i, j + 1) == null);
					boolean drawLeft = farC1.x < nearC1.x && (i == 0 || grid.get(i - 1, j) == null);
					boolean drawRight = farC4.x > nearC4.x && (i == grid.getWidth() - 1 || grid.get(i + 1, j) == null);
					boolean drawBottom = farC4.y < nearC4.y && (j == 0 || grid.get(i, j - 1) == null);

					//*
					if (drawTop) {
						stage.draw.drawPrimitive(Textures.FLOOR, farC1, farC2, nearC1, nearC2);
					}

					if (drawLeft) {
						stage.draw.drawPrimitive(Textures.FLOOR, farC1, nearC1, farC3, nearC3);
					}

					if (drawRight) {
						stage.draw.drawPrimitive(Textures.FLOOR, nearC2, farC2, nearC4, farC4);
					}

					if (drawBottom) {
						stage.draw.drawPrimitive(Textures.FLOOR, nearC3, nearC4, farC3, farC4);
					}
					//*/

					if (drawTop) stage.draw.drawLine(nearC1.x, nearC1.y, farC1.x, farC1.y, 1, Color.BLACK);
					if (drawRight) stage.draw.drawLine(nearC2.x, nearC2.y, farC2.x, farC2.y, 1, Color.BLACK);
					if (drawLeft) stage.draw.drawLine(nearC3.x, nearC3.y, farC3.x, farC3.y, 1, Color.BLACK);
					if (drawBottom) stage.draw.drawLine(nearC4.x, nearC4.y, farC4.x, farC4.y, 1, Color.BLACK);

					maskSurface.begin();
					stage.draw.batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);
					Sprites.WHITE_TILE.drawScaled(stage.draw.batch, nearC3.x, nearC3.y, nearScale, nearScale, 0);
					stage.draw.batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
					maskSurface.end();
				}
			});

			surface.begin();
			stage.draw.batch.setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_ZERO);
			maskSurface.createSprite().draw(stage.draw.batch);
			stage.draw.batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			surface.end();
			surface.createSprite().draw(stage.draw.batch);
		}

		private boolean collidesWith(Shape s1) {
			for (Shape s2 : grid) {
				if (s2 != null && s1.collidesWith(s2)) return true;
			}
			return false;
		}
	}

	public static Vector coordinateToPosition(Coordinate coordinate) {
		return new Vector(coordinate.x * PIXEL_SIZE, coordinate.y * PIXEL_SIZE);
	}

	public static Coordinate positionToCoordinate(Vector position) {
		int i = (int) (position.x / PIXEL_SIZE);
		int j = (int) (position.y / PIXEL_SIZE);
		return new Coordinate(i, j);
	}
}
