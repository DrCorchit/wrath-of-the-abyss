package net.drcorchit.dungeonraiders.assets;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import net.drcorchit.dungeonraiders.utils.IOUtils;
import net.drcorchit.dungeonraiders.utils.JsonUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Skins {

	private static final File skinsFolder = IOUtils.getFileAsChildOfWorkingDir("resources/skins");

	public static final ArrayList<Skin> SKINS = new ArrayList<>();
	public static final Skin base = loadSkin("base.json");
	public static final Skin punk = loadSkin("0.json");
	public static final Skin vegas = loadSkin("1.json");
	public static final Skin regal = loadSkin("2.json");
	public static final Skin harness = loadSkin("3.json");
	public static final Skin purple = loadSkin("4.json");
	public static final Skin gown = loadSkin("5.json");
	public static final Skin goth = loadSkin("6.json");
	public static final Skin lifeguard = loadSkin("7.json");

	public static Skin loadSkin(String path) {
		try {
			JsonElement ele = JsonUtils.parseFile(IOUtils.getFileAsChildOfFolder(skinsFolder, path));
			Skin output = new Skin(ele.getAsJsonObject());
			SKINS.add(output);
			return output;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
