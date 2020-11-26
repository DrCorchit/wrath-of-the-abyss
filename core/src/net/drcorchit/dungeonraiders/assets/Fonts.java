package net.drcorchit.dungeonraiders.assets;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class Fonts {

	public static final BitmapFont MONO_24, TREB_16, EURO_36, EURO_20, ERAS_16, ERAS_24, ERAS_36;

	static {
		//Used in game select screen, and for the city name label
		//MONO_36 = initFont("monofur-36", "monof55.ttf", 36);
		MONO_24 = initFont("monofur-24", "monof55.ttf", 24);
		//MONO_20 = initFont("monofur-18", "monof55.ttf", 20);

		//Currently used only to draw tile yield numbers
		TREB_16 = initFont("trebuchet", "trebuc.ttf", 16);

		//Used for tech overview screen
		EURO_36 = initFont("eurostile-36", "eurostile.ttf", 36);
		EURO_20 = initFont("eurostile-20", "eurostile.ttf", 20);

		//Used everywhere else
		ERAS_16 = initFont("eras-small", "erasmd.ttf", 16);
		ERAS_24 = initFont("eras-medium", "erasmd.ttf", 24);
		ERAS_36 = initFont("eras-large", "erasmd.ttf", 36);
	}
	
	private static BitmapFont initFont(String name, String path, int size) {
		return LocalAssets.getInstance().getOrMakeFont(name, path, size);
	}

	public static BitmapFont getDefaultFont() {
		return ERAS_16;
	}
}
