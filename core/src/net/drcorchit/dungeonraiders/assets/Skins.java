package net.drcorchit.dungeonraiders.assets;

import com.google.gson.JsonElement;
import net.drcorchit.dungeonraiders.utils.IOUtils;
import net.drcorchit.dungeonraiders.utils.JsonUtils;

import java.io.File;
import java.io.IOException;

public class Skins {

	private static final File skinsFolder = IOUtils.getFileAsChildOfWorkingDir("resources/skins");

	public static Skin loadSkin(String path) {
		try {
			JsonElement ele = JsonUtils.parseFile(IOUtils.getFileAsChildOfFolder(skinsFolder, path));
			return new Skin(ele.getAsJsonObject());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
