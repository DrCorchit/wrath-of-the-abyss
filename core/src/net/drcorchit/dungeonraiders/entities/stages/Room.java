package net.drcorchit.dungeonraiders.entities.stages;

import com.badlogic.gdx.graphics.Color;
import net.drcorchit.dungeonraiders.entities.actors.Actor;
import net.drcorchit.dungeonraiders.shapes.Rectangle;
import net.drcorchit.dungeonraiders.shapes.Shape;
import net.drcorchit.dungeonraiders.shapes.Square;
import net.drcorchit.dungeonraiders.utils.Grid;
import net.drcorchit.dungeonraiders.utils.MathUtils;
import net.drcorchit.dungeonraiders.utils.Vector;

public class Room extends Actor<DungeonStage> {

	private static final int ROOM_BLOCK_SIZE = 16;
	private static final float SIZE = ROOM_BLOCK_SIZE * DungeonStage.BLOCK_SIZE;

	private final Layer[] layers;
	private final Rectangle rectangle;

	public Room(DungeonStage stage, float x, float y, int numLayers) {
		super(stage, x, y);
		layers = new Layer[numLayers];

		//initialize layers
		for (int i = 0; i < numLayers; i++) {
			layers[i] = new Layer(i * DungeonStage.BLOCK_SIZE);
		}

		rectangle = new Square(() -> getPosition().add(SIZE/2, SIZE/2), ROOM_BLOCK_SIZE * DungeonStage.BLOCK_SIZE);
		depth = -1;
	}

	public Rectangle getRectangle() {
		return rectangle;
	}

	public Layer getLayer(int index) {
		return layers[index];
	}

	public Layer getLayer(float z) {
		int index = (int) Math.floor(z / DungeonStage.BLOCK_SIZE);
		index = MathUtils.clamp(0, index, layers.length - 1);
		return layers[index];
	}

	@Override
	public void act(float delta) {
		//currently no-op
	}

	public class Layer {
		public final float z;
		public final Grid<Shape> blocks = new Grid<>(Shape.class, ROOM_BLOCK_SIZE, ROOM_BLOCK_SIZE);

		public Layer(float z) {
			this.z = z;
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

		public void draw(Vector pos) {
			blocks.forEachCell((i, j) -> {
				Shape shape = blocks.get(i, j);
				if (shape != null) {
					Vector relativePos = getCellLocation(i, j).subtract(getPosition()).add(pos);
					shape.draw(Color.BLUE);
					shape.move(relativePos).draw(Color.WHITE);
					Vector projectedPos = stage.projectZPosition(relativePos, z);
					Shape projectedShape = shape.move(projectedPos).scale(DungeonStage.getZScale(z));
					//projectedShape.draw(Color.WHITE);
				}
			});
		}

		public boolean collidesWith(Shape s1) {
			for (Shape s2 : blocks) {
				if (s2 != null && s1.collidesWith(s2)) return true;
			}
			return false;
		}
	}

	public void draw(Vector pos) {
		rectangle.move(pos.add(SIZE/2, SIZE/2)).draw(Color.RED);
		for (Layer layer : layers) {
			layer.draw(pos);
		}
	}
}
