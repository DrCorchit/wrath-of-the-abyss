package net.drcorchit.dungeonraiders.entities.stages;

import net.drcorchit.dungeonraiders.utils.Grid;

public class Room {

	private static final int ROOM_BLOCK_SIZE = 16;

	public final Grid<Boolean> blocks = new Grid<>(Boolean.class, ROOM_BLOCK_SIZE, ROOM_BLOCK_SIZE);

}
