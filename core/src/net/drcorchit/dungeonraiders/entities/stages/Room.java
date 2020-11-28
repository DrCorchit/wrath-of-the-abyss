package net.drcorchit.dungeonraiders.entities.stages;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import net.drcorchit.dungeonraiders.assets.Sprites;
import net.drcorchit.dungeonraiders.assets.Textures;
import net.drcorchit.dungeonraiders.entities.actors.Actor;
import net.drcorchit.dungeonraiders.shapes.Rectangle;
import net.drcorchit.dungeonraiders.shapes.Shape;
import net.drcorchit.dungeonraiders.shapes.Square;
import net.drcorchit.dungeonraiders.utils.*;

public class Room extends Actor<DungeonStage> {

	private static final int ROOM_BLOCK_SIZE = 16;
	private static final float SIZE = ROOM_BLOCK_SIZE * DungeonStage.BLOCK_SIZE;

	private final Layer[] layers;
	private final int lastlayerIndex;
	private final Rectangle rectangle;

	public Room(DungeonStage stage, Vector position, int numLayers) {
		super(stage, position);
		layers = new Layer[numLayers];
		lastlayerIndex = numLayers - 1;

		//initialize layers
		for (int i = 0; i < numLayers; i++) {
			float layerZ = -i * DungeonStage.BLOCK_SIZE;
			layers[i] = new Layer(layerZ);
		}

		rectangle = new Square(() -> getPosition().add(SIZE / 2, SIZE / 2), ROOM_BLOCK_SIZE * DungeonStage.BLOCK_SIZE);
		depth = -1;
	}

	public Rectangle getRectangle() {
		return rectangle;
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

	public class Layer {
		public AnimatedSprite floorSprite, wallSprite;
		private final Surface surface, maskSurface;
		public final float z;
		public final Grid<Shape> blocks = new Grid<>(Shape.class, ROOM_BLOCK_SIZE, ROOM_BLOCK_SIZE);

		public Layer(float z) {
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
			blocks.set(i, j, r);
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

			blocks.forEachCell((i, j) -> {
				Shape shape = blocks.get(i, j);
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

					//top
					if (farC1.y > nearC1.y) {
						getDraw().drawPrimitive(Textures.FLOOR, farC1, farC2, nearC1, nearC2);
					}

					//left
					if (farC1.x < nearC1.x) {
						getDraw().drawPrimitive(Textures.FLOOR, farC1, nearC1, farC3, nearC3);
					}

					//right
					if (farC4.x > nearC4.x) {
						getDraw().drawPrimitive(Textures.FLOOR, nearC2, farC2, nearC4, farC4);
					}

					//bottom
					if (farC4.y < nearC4.y) {
						getDraw().drawPrimitive(Textures.FLOOR, nearC3, nearC4, farC3, farC4);
					}

					//getDraw().drawPrimitive(Textures.wall, nearC1, nearC2, nearC3, nearC4);
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

			//negativeSurface.createSprite().draw(getBatch());
			surface.createSprite().draw(getBatch());
		}

		public boolean collidesWith(Shape s1) {
			for (Shape s2 : blocks) {
				if (s2 != null && s1.collidesWith(s2)) return true;
			}
			return false;
		}
	}

	public void draw(Vector pos) {
		//rectangle.move(pos.add(SIZE / 2, SIZE / 2)).draw(Color.RED);
		//draw from back to front
		for (int i = lastlayerIndex; i >= 0; i--) {
			layers[i].draw(pos);
		}
	}
}
