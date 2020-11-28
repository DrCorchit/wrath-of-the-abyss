package net.drcorchit.dungeonraiders.entities.stages;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import net.drcorchit.dungeonraiders.assets.Sprites;
import net.drcorchit.dungeonraiders.assets.Textures;
import net.drcorchit.dungeonraiders.entities.actors.DungeonActor;
import net.drcorchit.dungeonraiders.entities.actors.PhysicsActor;
import net.drcorchit.dungeonraiders.shapes.Rectangle;
import net.drcorchit.dungeonraiders.shapes.Shape;
import net.drcorchit.dungeonraiders.shapes.Square;
import net.drcorchit.dungeonraiders.utils.*;

public class Room extends DungeonActor<DungeonStage> {

	private static final int ROOM_BLOCK_SIZE = 16;
	private static final float SIZE = ROOM_BLOCK_SIZE * DungeonStage.BLOCK_SIZE;

	private final Layer[] layers;
	private final int lastlayerIndex;

	public Room(DungeonStage stage, Vector position, int numLayers) {
		super(stage, position);
		layers = new Layer[numLayers];
		lastlayerIndex = numLayers - 1;

		//initialize layers
		for (int i = 0; i < numLayers; i++) {
			float layerZ = -i * DungeonStage.BLOCK_SIZE;
			layers[i] = new Layer(layerZ);
		}

		setViewBoundsToRectangle(SIZE / 2, SIZE / 2, SIZE, SIZE);
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

	@Override
	public float getDepth() {
		return -10000;
	}

	@Override
	public boolean collidesWith(DungeonActor<?> other) {
		return false;
	}

	public void draw(Vector pos) {
		//rectangle.move(pos.add(SIZE / 2, SIZE / 2)).draw(Color.RED);
		//draw from back to front
		for (int i = lastlayerIndex; i >= 0; i--) {
			layers[i].draw(pos);
		}
	}

	public class Layer extends DungeonActor<DungeonStage> {
		public AnimatedSprite floorSprite, wallSprite;
		private final Surface surface, maskSurface;
		public final float z;
		public final Grid<Shape> grid = new Grid<>(Shape.class, ROOM_BLOCK_SIZE, ROOM_BLOCK_SIZE);

		public Layer(float z) {
			super(Room.this.stage, Room.this.getPosition());
			this.z = z;
			floorSprite = Sprites.getSprite(Textures.FLOOR);
			wallSprite = Sprites.getSprite(Textures.WALL);
			surface = new Surface();
			maskSurface = new Surface();
		}

		public Vector getCellLocation(int i, int j) {
			float x = getPosition().x + (i + .5f) * DungeonStage.BLOCK_SIZE;
			float y = getPosition().y + (j + .5f) * DungeonStage.BLOCK_SIZE;
			return new Vector(x, y);
		}

		public void placeSquare(int i, int j) {
			Rectangle r = new Square(() -> getCellLocation(i, j), DungeonStage.BLOCK_SIZE);
			grid.set(i, j, r);
		}

		@Override
		public void act(float factor) {
			//No-op
		}

		@Override
		public float getDepth() {
			return -z;
		}

		@Override
		public boolean collidesWith(DungeonActor<?> other) {
			return other instanceof PhysicsActor;
		}

		public void draw(Vector roomPos) {
			float farScale = DungeonStage.getZScale(z);
			float nearScale = DungeonStage.getZScale(z + DungeonStage.BLOCK_SIZE);
			float farSize = DungeonStage.BLOCK_SIZE * nearScale / 2;
			float nearSize = DungeonStage.BLOCK_SIZE * farScale / 2;

			surface.begin();
			surface.clear();
			Vector projectedPos = stage.projectZPosition(roomPos, z + DungeonStage.BLOCK_SIZE);
			wallSprite.drawTiled(getBatch(), projectedPos.x, projectedPos.y);
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

					boolean drawTop = farC1.y > nearC1.y && (j == grid.getHeight()-1 || grid.get(i, j+1) == null);
					boolean drawLeft = farC1.x < nearC1.x && (i == 0 || grid.get(i-1, j) == null);
					boolean drawRight = farC4.x > nearC4.x && (i == grid.getWidth()-1 || grid.get(i+1, j) == null);
					boolean drawBottom = farC4.y < nearC4.y && (j == 0 || grid.get(i, j-1) == null);

					//*
					if (drawTop) {
						getDraw().drawPrimitive(Textures.FLOOR, farC1, farC2, nearC1, nearC2);
					}

					if (drawLeft) {
						getDraw().drawPrimitive(Textures.FLOOR, farC1, nearC1, farC3, nearC3);
					}

					if (drawRight) {
						getDraw().drawPrimitive(Textures.FLOOR, nearC2, farC2, nearC4, farC4);
					}

					if (drawBottom) {
						getDraw().drawPrimitive(Textures.FLOOR, nearC3, nearC4, farC3, farC4);
					}
					//*/

					if (drawTop) getDraw().drawLine(nearC1.x, nearC1.y, farC1.x, farC1.y, 1, Color.BLACK);
					if (drawRight) getDraw().drawLine(nearC2.x, nearC2.y, farC2.x, farC2.y, 1, Color.BLACK);
					if (drawLeft) getDraw().drawLine(nearC3.x, nearC3.y, farC3.x, farC3.y, 1, Color.BLACK);
					if (drawBottom) getDraw().drawLine(nearC4.x, nearC4.y, farC4.x, farC4.y, 1, Color.BLACK);

					maskSurface.begin();
					getBatch().setBlendFunction(GL20.GL_ONE, GL20.GL_ZERO);
					Sprites.WHITE_TILE.drawScaled(getBatch(), nearC3.x, nearC3.y, nearScale, nearScale, 0);
					getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
					maskSurface.end();
				}
			});

			surface.begin();
			getBatch().setBlendFunction(GL20.GL_DST_COLOR, GL20.GL_ZERO);
			maskSurface.createSprite().draw(getBatch());
			getBatch().setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
			surface.end();
			surface.createSprite().draw(getBatch());
		}

		@Deprecated
		public boolean collidesWith(Shape s1) {
			for (Shape s2 : grid) {
				if (s2 != null && s1.collidesWith(s2)) return true;
			}
			return false;
		}
	}
}
