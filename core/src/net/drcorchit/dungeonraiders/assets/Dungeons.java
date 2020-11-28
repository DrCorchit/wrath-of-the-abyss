package net.drcorchit.dungeonraiders.assets;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.drcorchit.dungeonraiders.utils.IOUtils;
import net.drcorchit.dungeonraiders.utils.JsonUtils;
import net.drcorchit.dungeonraiders.utils.MathUtils;
import net.drcorchit.dungeonraiders.utils.Pair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class Dungeons {

	public static final ImmutableList<Dungeon> dungeons;
	public static final ImmutableMap<Pair<Boolean, Boolean>, ImmutableList<Dungeon>> dungeonsByTopEntrance;
	public static final ImmutableMap<Pair<Boolean, Boolean>, ImmutableList<Dungeon>> dungeonsByLeftEntrance;
	public static final ImmutableMap<Pair<Boolean, Boolean>, ImmutableList<Dungeon>> dungeonsByRightEntrance;
	public static final ImmutableMap<Pair<Boolean, Boolean>, ImmutableList<Dungeon>> dungeonsByBottomEntrance;

	public static Dungeon getRandomDungeon(List<Dungeon> dungeons, Supplier<Integer> randomSource) {
		return dungeons.get(MathUtils.mod(randomSource.get(), dungeons.size()));
	}

	static {
		File dungeonsFile = IOUtils.getFileAsChildOfWorkingDir("dungeons/dungeons.json");

		ImmutableList.Builder<Dungeon> builder = ImmutableList.builder();
		Map<Pair<Boolean, Boolean>, List<Dungeon>> topBuilder = new HashMap<>();
		Map<Pair<Boolean, Boolean>, List<Dungeon>> leftBuilder = new HashMap<>();
		Map<Pair<Boolean, Boolean>, List<Dungeon>> rightBuilder = new HashMap<>();
		Map<Pair<Boolean, Boolean>, List<Dungeon>> bottomBuilder = new HashMap<>();

		try {
			JsonArray dungeonsInfo = JsonUtils.parseFile(dungeonsFile).getAsJsonArray();
			for (JsonElement ele : dungeonsInfo) {
				Dungeon dungeon = new Dungeon(ele.getAsJsonObject());
				builder.add(dungeon);
				topBuilder.computeIfAbsent(dungeon.top, (pair) -> new ArrayList<>());
				topBuilder.get(dungeon.top).add(dungeon);
				leftBuilder.computeIfAbsent(dungeon.left, (pair) -> new ArrayList<>());
				leftBuilder.get(dungeon.left).add(dungeon);
				rightBuilder.computeIfAbsent(dungeon.right, (pair) -> new ArrayList<>());
				rightBuilder.get(dungeon.right).add(dungeon);
				bottomBuilder.computeIfAbsent(dungeon.bottom, (pair) -> new ArrayList<>());
				bottomBuilder.get(dungeon.bottom).add(dungeon);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		dungeons = builder.build();
		dungeonsByTopEntrance = ImmutableMap.copyOf(Maps.transformValues(topBuilder, ImmutableList::copyOf));
		dungeonsByLeftEntrance = ImmutableMap.copyOf(Maps.transformValues(leftBuilder, ImmutableList::copyOf));
		dungeonsByRightEntrance = ImmutableMap.copyOf(Maps.transformValues(rightBuilder, ImmutableList::copyOf));
		dungeonsByBottomEntrance = ImmutableMap.copyOf(Maps.transformValues(bottomBuilder, ImmutableList::copyOf));
	}
}
