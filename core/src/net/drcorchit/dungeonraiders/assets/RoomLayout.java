package net.drcorchit.dungeonraiders.assets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.drcorchit.dungeonraiders.actors.Room;
import net.drcorchit.dungeonraiders.utils.Grid;

import java.util.ArrayList;

import static net.drcorchit.dungeonraiders.utils.MathUtils.getBit;

public class RoomLayout {
	public final String name;
	public final float prevalence;
	public final EntryTags.EntryTag top, left, right, bottom;
	private final ArrayList<Grid<Boolean>> layers;

	public RoomLayout(String name, JsonObject info) {
		this.name = name;

		top = Dungeons.TOP.tags.get(info.get("top").getAsString());
		left = Dungeons.SIDES.tags.get(info.get("left").getAsString());
		right = Dungeons.SIDES.tags.get(info.get("right").getAsString());
		bottom = Dungeons.BOTTOM.tags.get(info.get("bottom").getAsString());

		if (info.has("prevalence")) {
			prevalence = info.get("prevalence").getAsFloat();
		} else {
			prevalence = 1.0f;
		}

		layers = new ArrayList<>();
		if (info.has("layer")) {
			layers.add(loadLayer(info.getAsJsonArray("layer")));
		} else if (info.has("layers")) {
			JsonArray layersInfo = info.getAsJsonArray("layers");

			for (JsonElement ele : layersInfo) {
				JsonArray rows = ele.getAsJsonArray();
				if (rows.size() != Room.SIZE) {
					String message = String.format("Dungeon row count (%d) is not equal to room size (%d)", rows.size(), Room.SIZE);
					throw new IllegalArgumentException(message);
				}
				layers.add(loadLayer(rows));
			}
		}
	}

	private static Grid<Boolean> loadLayer(JsonArray rows) {
		Grid<Boolean> layer = new Grid<>(Boolean.class, Room.SIZE, Room.SIZE);
		int j = Room.SIZE - 1;
		for (JsonElement rowInfo : rows) {
			int row = Integer.parseInt(rowInfo.getAsString(), 2);
			for (int i = 0; i < Room.SIZE; i++) {
				boolean bit = getBit(row, Room.SIZE - (i + 1));
				layer.set(i, j, bit);
			}
			j--;
		}
		return layer;
	}

	private void copyToLayer(Grid<Boolean> grid, Room.Layer layer) {
		for (int layerIndex = 0; layerIndex < layers.size(); layerIndex++) {
			grid.forEachCell((i, j) -> {
				Boolean temp = grid.get(i, j);
				if (temp != null && temp) layer.placeSquare(i, j);
			});
		}
	}

	public void copyToRoom(Room room) {
		if (layers.size() == 1) {
			Grid<Boolean> gridLayer = layers.get(0);

			for (int layerIndex = 0; layerIndex < room.getLayerCount(); layerIndex++) {
				copyToLayer(gridLayer, room.getLayer(layerIndex));
			}
		} else if (layers.size() != room.getLayerCount()) {
			throw new IllegalArgumentException("Room layer count does not match dungeon layer count!");
		} else {
			for (int layerIndex = 0; layerIndex < layers.size(); layerIndex++) {
				copyToLayer(layers.get(layerIndex), room.getLayer(layerIndex));
			}
		}
	}
}
