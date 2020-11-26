package net.drcorchit.dungeonraiders.assets;

import com.google.gson.JsonElement;
import net.drcorchit.dungeonraiders.utils.IOUtils;
import net.drcorchit.dungeonraiders.utils.JsonUtils;

import java.io.File;
import java.io.IOException;

public class Skeletons {

	private static final File skeletonsFolder = IOUtils.getFileAsChildOfWorkingDir("resources/skeletons");

	public static final Skeleton human_female = loadSkeleton("human.json");

	private static Skeleton loadSkeleton(String path) {
		try {
			JsonElement ele = JsonUtils.parseFile(IOUtils.getFileAsChildOfFolder(skeletonsFolder, path));
			return new Skeleton(ele.getAsJsonObject());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
