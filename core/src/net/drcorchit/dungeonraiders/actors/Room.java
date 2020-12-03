package net.drcorchit.dungeonraiders.actors;

import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.google.common.collect.ImmutableSet;
import net.drcorchit.dungeonraiders.assets.RoomLayout;
import net.drcorchit.dungeonraiders.assets.Sprites;
import net.drcorchit.dungeonraiders.assets.Textures;
import net.drcorchit.dungeonraiders.drawing.LightSource;
import net.drcorchit.dungeonraiders.drawing.RenderInstruction;
import net.drcorchit.dungeonraiders.drawing.RunnableRenderInstruction;
import net.drcorchit.dungeonraiders.drawing.shapes.Rectangle;
import net.drcorchit.dungeonraiders.drawing.shapes.Shape;
import net.drcorchit.dungeonraiders.drawing.shapes.Square;
import net.drcorchit.dungeonraiders.stages.DungeonStage;
import net.drcorchit.dungeonraiders.utils.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

public class Room extends AbstractActor<DungeonStage> {

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

	public int getLayerCount() {
		return layers.length;
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
		private Vector lastRoomPos;
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
			updateCellVectors(roomPos);

			grid.forEachCell(cell -> {
				if (cell != null) {
					Sprites.WHITE_TILE.drawScaled(getStage().draw.batch, cell.nearC3.x, cell.nearC3.y, nearScale, nearScale, 0);
				}
			});
		}

		public void drawLightGeometry(Vector roomPos, LightSource lightSource) {
			updateCellVectors(roomPos);
			grid.forEachCell(cell -> {
				if (cell != null) {
					cell.drawShadow(lightSource);
				}
			});
		}

		public void draw(Vector roomPos) {
			updateCellVectors(roomPos);

			grid.forEachCell(cell -> {
				if (cell != null) cell.draw();
			});
		}

		private boolean collidesWith(Shape shape) {
			for (Cell cell : grid) {
				if (cell != null && cell.shape.collidesWith(shape)) return true;
			}
			return false;
		}

		private void updateCellVectors(Vector roomPos) {
			//If true, no need to recalculate vectors
			if (roomPos.equals(lastRoomPos)) return;
			lastRoomPos = roomPos;

			grid.forEach(cell -> {
				if (cell != null) cell.updateVectors(roomPos);
			});
		}

		private class Cell {
			private final int i, j;
			private final Rectangle shape;
			private Vector relativePos, projectedPosFar, projectedPosNear;
			//These form the vertices of a cube
			private Vector farC1, farC2, farC3, farC4, nearC1, nearC2, nearC3, nearC4;

			private Cell(int i, int j, Rectangle shape) {
				this.i = i;
				this.j = j;
				this.shape = shape;
			}

			private void updateVectors(Vector roomPos) {
				relativePos = getCellLocation(i, j).subtract(getPosition()).add(roomPos);
				projectedPosFar = getStage().projectZPosition(relativePos, z);
				projectedPosNear = getStage().projectZPosition(relativePos, z + DungeonStage.BLOCK_SIZE);
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

				TextureRegion floor = getStage().floorSprite.getCurrentFrame();

				if (drawTop) {
					getStage().draw.drawPrimitive(floor, farC1, farC2, nearC1, nearC2);
				}

				if (drawLeft) {
					getStage().draw.drawPrimitive(floor, farC3, farC1, nearC3, nearC1);
				}

				if (drawRight) {
					getStage().draw.drawPrimitive(floor, farC2, farC4, nearC2, nearC4);
				}

				if (drawBottom) {
					getStage().draw.drawPrimitive(floor, nearC3, nearC4, farC3, farC4);
				}

				//if (drawTop) stage.draw.drawLine(nearC1.x, nearC1.y, farC1.x, farC1.y, 1, Color.BLACK);
				//if (drawRight) stage.draw.drawLine(nearC2.x, nearC2.y, farC2.x, farC2.y, 1, Color.BLACK);
				//if (drawLeft) stage.draw.drawLine(nearC3.x, nearC3.y, farC3.x, farC3.y, 1, Color.BLACK);
				//if (drawBottom) stage.draw.drawLine(nearC4.x, nearC4.y, farC4.x, farC4.y, 1, Color.BLACK);
			}

			public void drawShadow(LightSource lightSource) {
				/*
				This handles drawing shadows for a single block
				This code is complicated, but all it does is draw the shadow for
				a rectangle as cast by a single light source.

				argument0 -> block id
				argument1 -> light source id

				source_x, source_y -> coordinates of light source
				box_x, box_y, box_size, box_half -> size of a box around the light source.
			    points outside this box are too far to be handled by the lighting engine
				block_x, block_y, block_w, block_h -> size of the rectangle that obstructs the light

				p1, p2 -> points on the block
				p3, p4 -> points on the outer rectangle
				a1, a2 -> angles from p1 to p3 and p2 to p4
				xq, yq -> queues of x and y coorinates
				*/
				float boxSize = lightSource.getLightRadius();
				Rectangle box = lightSource.getBoundingRectangle();
				float blockX = projectedPosNear.x;
				float blockY = projectedPosNear.y;
				float blockWidth = shape.width;
				float blockHeight = shape.height;

				//The block is outside the light square
				if (!box.containsPoint(projectedPosNear)) return;

				//fit the block until it is inside the box
				//if the block is large than the box, weird stuff happens
				if (box.getX() > blockX) {
					blockWidth += blockX - box.getX();
					blockX = box.getX();
				}

				if (box.getX() + boxSize < blockX + blockWidth) {
					blockWidth = (box.getX() + boxSize) - blockX;
				}

				if (box.getY() > blockY) {
					blockHeight += blockY - box.getY();
					blockY = box.getY();
				}

				if (box.getY() + boxSize < blockY + blockHeight) {
					blockHeight = (box.getY() + boxSize) - blockY;
				}

				Vector blockPos = new Vector(blockX, blockY);
				Rectangle block = new Rectangle(() -> blockPos, blockWidth, blockHeight);

				Vector p1, p2, p3, p4, p5 = null;

				Direction blockToLightDir = Direction.getRectangleDirection(block, box.getPosition());
				switch (blockToLightDir) {
					case NORTH:
						p1 = block.getPosition().add(block.width, block.height);
						p2 = block.getPosition().add(0, block.height);
						break;
					case NORTHEAST:
						p1 = block.getPosition().add(block.width, 0);
						p2 = block.getPosition().add(0, block.height);
						break;
					case EAST:
						p1 = block.getPosition().add(block.width, 0);
						p2 = block.getPosition().add(block.width, block.height);
						break;
					case SOUTHEAST:
						p1 = block.getPosition();
						p2 = block.getPosition().add(block.width, block.height);
						break;
					case SOUTH:
						p1 = block.getPosition();
						p2 = block.getPosition().add(block.width, 0);
						break;
					case SOUTHWEST:
						p1 = block.getPosition().add(0, block.height);
						p2 = block.getPosition().add(block.width, 0);
						break;
					case WEST:
						p1 = block.getPosition();
						p2 = block.getPosition().add(0, block.height);
						break;
					case NORTHWEST:
						p1 = block.getPosition().add(block.width, block.height);
						p2 = block.getPosition();
						break;
					default:
						//the light is in the box. Nothing to draw.
						return;
				}

				float a1 = box.getPosition().subtract(p1).angle(Vector.ZERO);
				float a2 = box.getPosition().subtract(p2).angle(Vector.ZERO);
				//a1 = point_direction(source_x, source_y, p1x, p1y);
				//a2 = point_direction(source_x, source_y, p2x, p2y);
				p3 = box.getPosition();
				p4 = box.getPosition();

				if (a1 > 45 && a1 <= 135) {
					p3 = p3.add((float) (Math.tan(Math.toRadians(a1 - 90)) * boxSize), -boxSize);
				} else if (a1 > 135 && a1 <= 225) {
					p3 = p3.add(-boxSize, (float) (Math.tan(Math.toDegrees(a1)) * boxSize));
				} else if (a1 > 225 && a1 <= 315) {
					p3 = p3.add((float) (Math.tan(Math.toDegrees(a1 - 270)) * boxSize), boxSize);
				} else {
					p3 = p3.add(boxSize, (float) (Math.tan(Math.toDegrees(a1)) * boxSize));
				}

				if (a2 > 45 && a2 <= 135) {
					p4 = p4.add((float) (Math.tan(Math.toRadians(a2 - 90)) * boxSize), -boxSize);
				} else if (a2 > 135 && a2 <= 225) {
					p4 = p4.add(-boxSize, (float) (Math.tan(Math.toDegrees(a2)) * boxSize));
				} else if (a2 > 225 && a2 <= 315) {
					p4 = p4.add((float) (Math.tan(Math.toDegrees(a2 - 270)) * boxSize), boxSize);
				} else {
					p4 = p4.add(boxSize, (float) (Math.tan(Math.toDegrees(a2)) * boxSize));
				}

				//add an extra point if the two angles enclose one of the corners
				if (MathUtils.angleBetween(a1, 45, a2)) {
					p5 = box.getPosition().add(boxSize, boxSize);
				} else if (MathUtils.angleBetween(a1, 135, a2)) {
					p5 = box.getPosition().add(-boxSize, boxSize);
				} else if (MathUtils.angleBetween(a1, 225, a2)) {
					p5 = box.getPosition().add(-boxSize, -boxSize);
				} else if (MathUtils.angleBetween(a1, 315, a2)) {
					p5 = box.getPosition().add(boxSize, -boxSize);
				}

				float[] vertices;
				short[] triangles;
				if (p5 == null) {
					vertices = new float[4 * 2];
					triangles = new short[2 * 3];
				} else {
					vertices = new float[5 * 2];
					triangles = new short[3 * 3];
				}

				vertices[0] = p1.x;
				vertices[1] = p1.y;
				vertices[2] = p2.x;
				vertices[3] = p2.y;
				vertices[4] = p3.x;
				vertices[5] = p3.y;
				vertices[6] = p4.x;
				vertices[7] = p4.y;

				triangles[0] = 0;
				triangles[1] = 1;
				triangles[2] = 2;
				triangles[3] = 1;
				triangles[4] = 2;
				triangles[5] = 3;

				if (p5 != null) {
					vertices[8] = p5.x;
					vertices[9] = p5.y;
					triangles[6] = 2;
					triangles[7] = 3;
					triangles[8] = 4;
				}

				PolygonRegion pr = new PolygonRegion(Sprites.WHITE_POINT.getCurrentFrame(), vertices, triangles);
				//getStage().draw.batch.draw(pr, 0, 0);
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
