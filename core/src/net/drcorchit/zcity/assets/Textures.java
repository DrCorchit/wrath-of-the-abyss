package net.drcorchit.zcity.assets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import net.drcorchit.zcity.utils.AnimatedSprite;
import net.drcorchit.zcity.utils.IOUtils;
import net.drcorchit.zcity.utils.Logger;
import net.drcorchit.zcity.utils.SpriteList;

import java.io.File;

public class Textures {

	public static final Texture yields, globe, haze, glow, river, selectScreen, selectScreenBack;
	public static final Texture viewMap, viewTechs, viewGovernment, viewDiplomacy, viewHistory, viewEmpire;
	public static final Texture dropup, dropdown, refresh, left, right, menuArrow, background, background4k;
	public static final Texture corner, edge, wedgeFlat, wedgeRound, selected, hex, hexBorder;
	public static final Texture city, cityIcon, capitalIcon, cityIconDark, capitalIconDark;
	public static final Texture fogOfWar, fogOfWarLight, checkbox, refresh2, quit, colorPickerBackdrop;
	public static final Texture reply, collapse, expand, crosshairs, messageGreyout, white;

	private static final Logger log;

	static {
		log = Logger.getLogger(Textures.class);

		yields = initTexture("yields_back.png");
		//globe = initTexture("globe_strip150.png");
		globe = initTexture("loading_globe.png");
		//glow = initTexture("glow.png");
		glow = initTexture("glow_sheet.png");
		haze = initTexture("haze.png");
		river = initTexture("river_strip60.png");
		selectScreen = initTexture("select_screen.png");
		selectScreenBack = initTexture("select_screen_back.png");

		viewMap = initTexture("view_map.png");
		viewTechs = initTexture("view_techs.png");
		viewGovernment = initTexture("view_government.png");
		viewDiplomacy = initTexture("view_diplomacy.png");
		viewHistory = initTexture("view_history.png");
		viewEmpire = initTexture("view_empire.png");

		hex = initTexture("hex.png");
		hexBorder = initTexture("hex_border.png");
		dropup = initTexture("dropup.png");
		dropdown = initTexture("dropdown.png");
		refresh = initTexture("refresh.png");
		left = initTexture("left.png");
		right = initTexture("right.png");
		menuArrow = initTexture("menu_arrow.png");

		corner = initTexture("border_corner.png");
		edge = initTexture("border_edge.png");
		wedgeFlat = initTexture("wedge_flat.png");
		wedgeRound = initTexture("wedge_round.png");
		selected = initTexture("selected_strip54.png");

		city = initTexture("city.png");
		cityIcon = initTexture("city_icon.png");
		capitalIcon = initTexture("city_capital_icon.png");
		cityIconDark = initTexture("city_icon_dark.png");
		capitalIconDark = initTexture("city_capital_icon_dark_2.png");

		fogOfWar = initTexture("fog_of_war.png");
		fogOfWarLight = initTexture("fog_of_war_light.png");
		checkbox = initTexture("checkbox.png");
		refresh2 = initTexture("refresh2.png");
		quit = initTexture("quit.png");
		colorPickerBackdrop = initTexture("color_picker_backdrop.png");

		reply = initTexture("reply.png");
		collapse = initTexture("collapse.png");
		expand = initTexture("expand.png");
		crosshairs = initTexture("crosshair.png");
		messageGreyout = initTexture("message_greyout.png");
		white = initTexture("white.png");

		///Applications/CivPlanet.app/Contents/app/resources/planets
		File planets = IOUtils.getFileAsChildOfWorkingDir("resources/planets");
		File img = IOUtils.randomImageFromFolder(planets);
		background = new Texture(img == null ? null : img.getPath());
		
		///Applications/CivPlanet.app/Contents/app/resources/planets
		File planets4k = IOUtils.getFileAsChildOfWorkingDir("resources/planets4k");
		File img4k = IOUtils.randomImageFromFolder(planets4k);
		background4k = new Texture(img4k == null ? null : img4k.getPath());
	}

	public static TextureRegion asRegion(Texture texture) {
		return new TextureRegion(texture);
	}

	public static AnimatedSprite asSprite(Texture texture) {
		return asSprite(texture, 1, 1);
	}

	public static AnimatedSprite asSprite(Texture texture, int frames) {
		return asSprite(texture, frames, 1);
	}

	public static AnimatedSprite asSprite(Texture texture, int i, int j) {
		return new AnimatedSprite(new SpriteList(texture, i, j));
	}

	private static Texture initTexture(String name) {
		Texture output = LocalAssets.getInstance().getTexture(name);
		if (output == null) {
			log.error("initTexture", "Texture " + name + " failed to load");
		}
		return output;
	}
}
