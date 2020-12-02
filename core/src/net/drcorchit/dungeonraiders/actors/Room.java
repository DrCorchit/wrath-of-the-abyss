package net.drcorchit.dungeonraiders.actors;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.google.common.collect.ImmutableSet;
import net.drcorchit.dungeonraiders.assets.RoomLayout;
import net.drcorchit.dungeonraiders.assets.Sprites;
import net.drcorchit.dungeonraiders.drawing.RenderInstruction;
import net.drcorchit.dungeonraiders.drawing.RunnableRenderInstruction;
import net.drcorchit.dungeonraiders.drawing.shapes.Rectangle;
import net.drcorchit.dungeonraiders.drawing.shapes.Shape;
import net.drcorchit.dungeonraiders.drawing.shapes.Square;
import net.drcorchit.dungeonraiders.stages.DungeonStage;
import net.drcorchit.dungeonraiders.utils.Coordinate;
import net.drcorchit.dungeonraiders.utils.Grid;
import net.drcorchit.dungeonraiders.utils.MathUtils;
import net.drcorchit.dungeonraiders.utils.Vector;

import java.util.ArrayList;
import java.util.Collection;

public class Room extends Actor<DungeonStage> {

	public static final int SIZE = 20;
	public static final float PIXEL_SIZE = SIZE * DungeonStage.BLOCK_SIZE;

	public final Coordinate coordinate;
	public final ImmutableSet<String> topTags, leftTags, rightTags, bottomTags;
	private final Layer[] layers;
	private final int lastlayerIndex;

	public Room(DungeonStage stage, Coordinate coordinate) {
		super(stage, coordinateToPosition(coordinate));
		this.coordinate = coordinate;
		int numLayers = stage.getLayerCount();
		layers = new Layer[numLayers];
		lastlayerIndex = layers.length - 1;

		//initialize layers
		for (int i = 0; i < numLayers; i++) {
			layers[i] = new Layer(i);
		}

		setViewBounds(PIXEL_SIZE / 2, PIXEL_SIZE / 2, PIXEL_SIZE, PIXEL_SIZE);

		topTags = ImmutableSet.of();
		leftTags = ImmutableSet.of();
		rightTags = ImmutableSet.of();
		bottomTags = ImmutableSet.of();
	}

	public Room(DungeonStage stage, Coordinate coordinate, RoomLayout roomLayout) {
		super(stage, coordinateToPosition(coordinate));
		this.coordinate = coordinate;
		int numLayers = stage.getLayerCount();
		layers = new Layer[numLayers];
		lastlayerIndex = layers.length - 1;

		//initialize layers
		for (int i = 0; i < numLayers; i++) {
			layers[i] = new Layer(i);
		}

		setViewBounds(PIXEL_SIZE / 2, PIXEL_SIZE / 2, PIXEL_SIZE, PIXEL_SIZE);
		//initialize layers
		roomLayout.copyToRoom(this);

		topTags = roomLayout.topTags;
		leftTags = roomLayout.leftTags;
		rightTags = roomLayout.rightTags;
		bottomTags = roomLayout.bottomTags;
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public Layer getLayer(int index) {
		return layers[MathUtils.clamp(0, index, lastlayerIndex)];
	}

	public static int getLayerIndex(float z) {
		int index = (int) Math.ceil(-z / DungeonStage.BLOCK_SIZE);
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
		ArrayList<RenderInstruction> output = new ArrayList<>();
		for (Layer layer : layers) {
			output.add(new RunnableRenderInstruction(() -> layer.draw(pos), layer.z, -1f));
		}
		return output;
	}

	public class Layer {
		public final float z, farScale, nearScale, farSize, nearSize;
		public final Grid<Cell> grid = new Grid<>(Cell.class, SIZE, SIZE);

		private Layer(int index) {
			this.z = index * -DungeonStage.BLOCK_SIZE;
			farScale = DungeonStage.getZScale(z);
			nearScale = DungeonStage.getZScale(z + DungeonStage.BLOCK_SIZE);
			farSize = DungeonStage.BLOCK_SIZE * farScale / 2;
			nearSize = DungeonStage.BLOCK_SIZE * nearScale / 2;
		}

		public Vector getCellLocation(int i, int j) {
			float x = getPosition().x + (i + .5f) * DungeonStage.BLOCK_SIZE;
			float y = getPosition().y + (j + .5f) * DungeonStage.BLOCK_SIZE;
			return new Vector(x, y);
		}

		public void placeSquare(int i, int j) {
			Rectangle r = new Square(() -> getCellLocation(i, j), DungeonStage.BLOCK_SIZE);
			grid.set(i, j, new Cell(i, j, r));
		}

		public void drawMask(Vector roomPos) {
			grid.forEachCell(cell -> {
				if (cell != null) {
					cell.updateVectors(roomPos.subtract(stage.getViewPosition()));
					Sprites.WHITE_TILE.drawScaled(stage.draw.batch, cell.nearC3.x, cell.nearC3.y, nearScale, nearScale, 0);
				}
			});
		}

		public void draw(Vector roomPos) {
			grid.forEachCell(cell -> {
				if (cell != null) {
					cell.updateVectors(roomPos);
					cell.draw();
				}
			});
		}

		private boolean collidesWith(Shape shape) {
			for (Cell cell : grid) {
				if (cell != null && cell.shape.collidesWith(shape)) return true;
			}
			return false;
		}

		private class Cell {
			private final int i, j;
			private final Shape shape;
			private Vector farC1, farC2, farC3, farC4, nearC1, nearC2, nearC3, nearC4;

			private Cell(int i, int j, Shape shape) {
				this.i = i;
				this.j = j;
				this.shape = shape;
			}

			private void updateVectors(Vector roomPos) {
				Vector relativePos = getCellLocation(i, j).subtract(getPosition()).add(roomPos);
				Vector projectedPosFar = stage.projectZPosition(relativePos, z);
				Vector projectedPosNear = stage.projectZPosition(relativePos, z + DungeonStage.BLOCK_SIZE);
				farC1 = projectedPosFar.add(-farSize, farSize);
				farC2 = projectedPosFar.add(farSize, farSize);
				farC3 = projectedPosFar.add(-farSize, -farSize);
				farC4 = projectedPosFar.add(farSize, -farSize);
				nearC1 = projectedPosNear.add(-nearSize, nearSize);
				nearC2 = projectedPosNear.add(nearSize, nearSize);
				nearC3 = projectedPosNear.add(-nearSize, -nearSize);
				nearC4 = projectedPosNear.add(nearSize, -nearSize);
			}

			private void draw() {
				boolean drawTop = farC1.y > nearC1.y && (j == grid.getHeight() - 1 || grid.get(i, j + 1) == null);
				boolean drawLeft = farC1.x < nearC1.x && (i == 0 || grid.get(i - 1, j) == null);
				boolean drawRight = farC4.x > nearC4.x && (i == grid.getWidth() - 1 || grid.get(i + 1, j) == null);
				boolean drawBottom = farC4.y < nearC4.y && (j == 0 || grid.get(i, j - 1) == null);

				TextureRegion floor = stage.floorSprite.getCurrentFrame();

				if (drawTop) {
					stage.draw.drawPrimitive(floor, farC1, farC2, nearC1, nearC2);
				}

				if (drawLeft) {
					stage.draw.drawPrimitive(floor, farC3, farC1, nearC3, nearC1);
				}

				if (drawRight) {
					stage.draw.drawPrimitive(floor, farC2, farC4, nearC2, nearC4);
				}

				if (drawBottom) {
					stage.draw.drawPrimitive(floor, nearC3, nearC4, farC3, farC4);
				}

				//if (drawTop) stage.draw.drawLine(nearC1.x, nearC1.y, farC1.x, farC1.y, 1, Color.BLACK);
				//if (drawRight) stage.draw.drawLine(nearC2.x, nearC2.y, farC2.x, farC2.y, 1, Color.BLACK);
				//if (drawLeft) stage.draw.drawLine(nearC3.x, nearC3.y, farC3.x, farC3.y, 1, Color.BLACK);
				//if (drawBottom) stage.draw.drawLine(nearC4.x, nearC4.y, farC4.x, farC4.y, 1, Color.BLACK);
			}
		}
	}

	public static Vector coordinateToPosition(Coordinate coordinate) {
		return new Vector(coordinate.x * PIXEL_SIZE, coordinate.y * PIXEL_SIZE);
	}

	public static Coordinate positionToCoordinate(Vector position) {
		int i = (int) Math.floor(position.x / PIXEL_SIZE);
		int j = (int) Math.floor(position.y / PIXEL_SIZE);
		return new Coordinate(i, j);
	}
}
