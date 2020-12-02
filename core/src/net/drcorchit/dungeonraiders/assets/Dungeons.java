package net.drcorchit.dungeonraiders.assets;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import net.drcorchit.dungeonraiders.utils.IOUtils;
import net.drcorchit.dungeonraiders.utils.JsonUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

public class Dungeons {

	private static final float MAX;
	public static final ImmutableList<RoomLayout> ROOM_LAYOUTS;

	public static RoomLayout getRandomDungeon(Predicate<RoomLayout> rule, Random random) {
		float max = 0;
		List<RoomLayout> candidates = new ArrayList<>();
		for (RoomLayout layout : ROOM_LAYOUTS) {
			if (rule.test(layout)) {
				max += layout.prevalence;
				candidates.add(layout);
			}
		}

		if (candidates.isEmpty()) {
			candidates = ROOM_LAYOUTS;
			max = MAX;
		}

		float target = random.nextFloat() * max;

		for (RoomLayout layout : candidates) {
			target -= layout.prevalence;
			if (target <= 0) {
				return layout;
			}
		}
		//this should never happen
		return candidates.get(0);
	}

	static {
		File dungeonsFolder = IOUtils.getFileAsChildOfWorkingDir("resources/dungeons");
		ImmutableList.Builder<RoomLayout> builder = ImmutableList.builder();
		float tempMax = 0;

		try {
			for (File file : Objects.requireNonNull(dungeonsFolder.listFiles((dir, name) -> name.endsWith(".json")))) {
				JsonObject info = JsonUtils.parseFile(file).getAsJsonObject();
				String name = file.getName().replaceAll(".json", "");
				RoomLayout roomLayout = new RoomLayout(name, info);
				tempMax += roomLayout.prevalence;
				builder.add(roomLayout);

			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		MAX = tempMax;
		ROOM_LAYOUTS = builder.build();
	}
}
