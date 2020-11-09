package net.drcorchit.zcity.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import net.drcorchit.zcity.utils.IOUtils;
import net.drcorchit.zcity.utils.Logger;

import java.io.File;
import java.util.HashMap;

public class LocalAssets {

	private static LocalAssets instance;

	private static final Logger log = Logger.getLogger(LocalAssets.class);

	public static LocalAssets getInstance() {
		if (instance == null) instance = new LocalAssets();
		return instance;
	}

	private final AssetManager manager;
	private final HashMap<String, Texture> localTextures;
	private final HashMap<String, BitmapFont> localFonts;
	private Skin skin;

	public LocalAssets() {
		manager = new AssetManager();
		localTextures = new HashMap<>();
		localFonts = new HashMap<>();
	}

	//Method for loading the assets into the manager
	public void load() {
		File resourcesFolder = IOUtils.getFileAsChildOfWorkingDir("resources");
		File skinFile = IOUtils.getFileAsChildOfFolder(resourcesFolder, "textures/uiskin.json");
		FileHandle handle = new FileHandle(skinFile);
		skin = new Skin(handle);
		log.info("load", "Loaded ui skin from local resources: " + skinFile.getPath());

		File spriteFolder = IOUtils.getFileAsChildOfFolder(resourcesFolder, "sprites");
		File[] sprites = IOUtils.listImageFiles(spriteFolder);
		for (File sprite : sprites) {
			String key = sprite.getName().toLowerCase();
			Texture value = new Texture(sprite.getPath());
			localTextures.put(key, value);
			log.info("load", "Loaded sprite from local resources: " + sprite.getPath());
		}
	}

	public Skin getSkin() {
		return skin;
	}

	BitmapFont getOrMakeFont(String name, String fileName, int size) {
		FreeTypeFontParameter params = new FreeTypeFontParameter();
		params.size = size;
		String relativePath = "resources/fonts/" + fileName;
		return getOrMakeFont(name, relativePath, params);
	}

	//Base case. Gets a font or makes it if absent
	private BitmapFont getOrMakeFont(String name, String relativePath, FreeTypeFontParameter params) {
		String key = name.toLowerCase();
		if (localFonts.containsKey(key)) {
			return localFonts.get(key);
		} else {
			BitmapFont font = makeFont(relativePath, params);
			localFonts.put(key, font);
			return font;
		}
	}

	private BitmapFont makeFont(String relativePath, FreeTypeFontParameter params) {
		File file = IOUtils.getFileAsChildOfWorkingDir(relativePath);
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(new FileHandle(file));
		BitmapFont value = generator.generateFont(params);
		generator.dispose();
		log.info("makeFont", "Created font from file: " + file.getPath());
		return value;
	}

	Texture getTexture(String name) {
		return localTextures.get(name.toLowerCase());
	}

	//Easy asset disposing, whenever you are done with it just dispose the manager instead of many files.
	public void dispose() {
		manager.dispose();
		skin.dispose();
		for (Texture texture : localTextures.values()) {
			texture.dispose();
		}
		for (BitmapFont font : localFonts.values()) {
			font.dispose();
		}
	}
}

