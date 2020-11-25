package net.drcorchit.zcity.assets;

import com.google.gson.JsonElement;
import net.drcorchit.zcity.utils.IOUtils;
import net.drcorchit.zcity.utils.JsonUtils;

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
