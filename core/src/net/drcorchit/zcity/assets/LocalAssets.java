package net.drcorchit.zcity.assets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import net.drcorchit.zcity.utils.IOUtils;
import net.drcorchit.zcity.utils.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Stack;

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
	private com.badlogic.gdx.scenes.scene2d.ui.Skin uiSkin;

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
		uiSkin = new com.badlogic.gdx.scenes.scene2d.ui.Skin(handle);
		log.info("load", "Loaded ui skin from local resources: " + skinFile.getPath());

		File spriteFolder = IOUtils.getFileAsChildOfFolder(resourcesFolder, "sprites");
		File[] sprites = IOUtils.listImageFilesAndFolders(spriteFolder);
		Stack<String> path = new Stack<>();
		dfsForSprites(path, sprites);
	}

	private void dfsForSprites(Stack<String> path, File[] sprites) {
		for (File file : sprites) {
			path.push(file.getName());

			if (file.isDirectory()) {
				dfsForSprites(path, IOUtils.listImageFilesAndFolders(file));
			} else {
				Texture value = new Texture(file.getPath());
				String key = String.join("/", path);
				localTextures.put(key, value);
				log.info("load", "Loaded sprite from local resources: " + file.getPath());
			}

			path.pop();
		}
	}

	public com.badlogic.gdx.scenes.scene2d.ui.Skin getUISkin() {
		return uiSkin;
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
		return localTextures.get(name);
	}

	//Easy asset disposing, whenever you are done with it just dispose the manager instead of many files.
	public void dispose() {
		manager.dispose();
		uiSkin.dispose();
		for (Texture texture : localTextures.values()) {
			texture.dispose();
		}
		for (BitmapFont font : localFonts.values()) {
			font.dispose();
		}
	}
}

