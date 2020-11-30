package net.drcorchit.dungeonraiders.assets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.drcorchit.dungeonraiders.actors.Room;
import net.drcorchit.dungeonraiders.utils.Grid;
import net.drcorchit.dungeonraiders.utils.Pair;
import static net.drcorchit.dungeonraiders.utils.MathUtils.getBit;

public class Dungeon {
	public final Pair<Boolean, Boolean> top, left, right, bottom;
	private final Grid<Boolean> tiles;

	public Dungeon(JsonObject info) {
		int entrances = Integer.parseInt(info.get("entrances").getAsString(), 2);
		top = new Pair<>(getBit(entrances, 7), getBit(entrances, 6));
		left = new Pair<>(getBit(entrances, 5), getBit(entrances, 4));
		right = new Pair<>(getBit(entrances, 3), getBit(entrances, 2));
		bottom = new Pair<>(getBit(entrances, 1), getBit(entrances, 0));

		tiles = new Grid<>(Boolean.class, Room.SIZE, Room.SIZE);

		JsonArray tilesInfo = info.getAsJsonArray("tiles");
		int j = Room.SIZE - 1;
		for (JsonElement ele : tilesInfo) {
			int row = Integer.parseInt(ele.getAsString(), 2);
			for (int i = 0; i < Room.SIZE; i++) {
				boolean bit = getBit(row, Room.SIZE - (i+1));
				tiles.set(i, j, bit);
			}
			j--;
		}
	}

	public void copyToLayer(Room.Layer layer) {
		tiles.forEachCell((i, j) -> {
			if (tiles.get(i, j)) layer.placeSquare(i, j);
		});
	}
}
