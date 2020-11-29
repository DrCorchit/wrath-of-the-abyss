package net.drcorchit.dungeonraiders.assets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import net.drcorchit.dungeonraiders.drawing.AnimatedSprite;
import net.drcorchit.dungeonraiders.drawing.ColorDrawable;

public class Styles {
	public static final float BUTTON_WIDTH = 300;
	public static final float BUTTON_HEIGHT = 75;
	public static final float TEXT_FIELD_HEIGHT = BUTTON_HEIGHT;
	public static final float TEXT_FIELD_WIDTH = 500;
	private static final com.badlogic.gdx.scenes.scene2d.ui.Skin UI_SKIN = LocalAssets.getInstance().getUISkin();

	public static final Label.LabelStyle DEFAULT, DEFAULT_BACKLESS, DEFAULT_SMALL, DEFAULT_SMALL_BACKLESS, ERROR, VERSION, ERA;
	public static final ImageButton.ImageButtonStyle IMAGE_BUTTON;
	public static final TextButton.TextButtonStyle TECH_BUTTON, TECH_BUTTON_DISCOVERED, TECH_BUTTON_REACHABLE;
	public static final TextButton.TextButtonStyle DEFAULT_BUTTON, ERROR_BUTTON, RED_BUTTON;

	public static final NinePatchDrawable DR = (NinePatchDrawable) UI_SKIN.getDrawable("default-round");
	public static final NinePatchDrawable DRL = (NinePatchDrawable) UI_SKIN.getDrawable("default-round-light");
	public static final NinePatchDrawable DRD = (NinePatchDrawable) UI_SKIN.getDrawable("default-round-dark");

	public static final TextField.TextFieldStyle POST;

	public static final ScrollPane.ScrollPaneStyle PANE_STYLE_GRAY, PANE_STYLE_BLACK;

	public static final Color ERROR_RED = new Color(1, .3f, .3f, 1);
	public static final Color TECH_BLUE_LIGHT = new Color(.45f, .71f, .96f, 1);
	public static final Color TECH_BLUE_DARK = new Color(.22f, .41f, .61f, 1);
	public static final Color TECH_GREEN_LIGHT = new Color(.6f, 1f, .4f, 1);
	public static final Color TECH_GREEN_DARK = new Color(.3f, .6f, .2f, 1);
	public static final Color COLOR_WORKED = new Color(.66f, .44f, 0, 1);

	static {
		DEFAULT = new Label.LabelStyle();
		DEFAULT.background = UI_SKIN.getDrawable("default-round");
		DEFAULT.font = Fonts.ERAS_24;
		DEFAULT.fontColor = TECH_BLUE_LIGHT;

		DEFAULT_BACKLESS = new Label.LabelStyle(DEFAULT);
		DEFAULT_BACKLESS.background = null;

		DEFAULT_SMALL = new Label.LabelStyle(DEFAULT);
		DEFAULT_SMALL.font = Fonts.ERAS_16;

		DEFAULT_SMALL_BACKLESS = new Label.LabelStyle(DEFAULT_SMALL);
		DEFAULT_SMALL_BACKLESS.background = null;

		ERROR = new Label.LabelStyle();
		ERROR.background = UI_SKIN.getDrawable("default-round-dark");
		ERROR.font = Fonts.ERAS_24;
		ERROR.fontColor = ERROR_RED;

		VERSION = new Label.LabelStyle();
		VERSION.background = null;
		VERSION.font = Fonts.ERAS_16;
		VERSION.fontColor = Color.WHITE;

		ERA = new Label.LabelStyle(Fonts.EURO_36, TECH_BLUE_LIGHT);
		//TECH = new Label.LabelStyle(Fonts.EURO_20, TECH_BLUE_LIGHT);

		IMAGE_BUTTON = UI_SKIN.get(ImageButton.ImageButtonStyle.class);

		TextButton.TextButtonStyle temp = UI_SKIN.get(TextButton.TextButtonStyle.class);
		TECH_BUTTON = new TextButton.TextButtonStyle(temp);
		TECH_BUTTON.font = Fonts.ERAS_16;
		TECH_BUTTON.fontColor = TECH_BLUE_LIGHT;
		TECH_BUTTON.downFontColor = Color.WHITE;
		TECH_BUTTON.up = DR;
		TECH_BUTTON.down = DR.tint(TECH_BLUE_LIGHT);

		TECH_BUTTON_REACHABLE = new TextButton.TextButtonStyle(temp);
		TECH_BUTTON_REACHABLE.font = Fonts.ERAS_16;
		TECH_BUTTON_REACHABLE.fontColor = TECH_BLUE_LIGHT;
		TECH_BUTTON_REACHABLE.downFontColor = Color.WHITE;
		TECH_BUTTON_REACHABLE.up = DRL.tint(TECH_GREEN_LIGHT);
		TECH_BUTTON_REACHABLE.down = DRL.tint(TECH_GREEN_DARK);

		TECH_BUTTON_DISCOVERED = new TextButton.TextButtonStyle(temp);
		TECH_BUTTON_DISCOVERED.font = Fonts.ERAS_16;
		TECH_BUTTON_DISCOVERED.fontColor = TECH_BLUE_LIGHT;
		TECH_BUTTON_DISCOVERED.downFontColor = Color.WHITE;
		TECH_BUTTON_DISCOVERED.up = DRL.tint(TECH_BLUE_LIGHT);
		TECH_BUTTON_DISCOVERED.down = DRL.tint(TECH_BLUE_DARK);

		DEFAULT_BUTTON = UI_SKIN.get(TextButton.TextButtonStyle.class);

		ERROR_BUTTON = new TextButton.TextButtonStyle(DEFAULT_BUTTON);
		ERROR_BUTTON.font = Fonts.ERAS_24;
		ERROR_BUTTON.fontColor = ERROR_RED;
		ERROR_BUTTON.up = DRD;
		ERROR_BUTTON.down = DR.tint(ERROR_RED);

		RED_BUTTON = new TextButton.TextButtonStyle(DEFAULT_BUTTON);
		RED_BUTTON.fontColor = Color.WHITE;
		RED_BUTTON.up = DRL.tint(ERROR_RED);
		RED_BUTTON.down = DRL.tint(Color.MAROON);

		POST = new TextField.TextFieldStyle();
		POST.font = Fonts.ERAS_16;
		POST.fontColor = TECH_BLUE_LIGHT;
		POST.background = new NinePatchDrawable(UI_SKIN.getPatch("textfield"));
		POST.cursor = new NinePatchDrawable(UI_SKIN.getPatch("cursor"));
		POST.selection = new TextureRegionDrawable(UI_SKIN.getRegion("selection"));

		PANE_STYLE_GRAY = LocalAssets.getInstance().getUISkin().get(ScrollPane.ScrollPaneStyle.class);
		PANE_STYLE_BLACK = new ScrollPane.ScrollPaneStyle(PANE_STYLE_GRAY);
		PANE_STYLE_BLACK.background = new ColorDrawable(Color.BLACK);
	}

	public static ImageButton.ImageButtonStyle copy(ImageButton.ImageButtonStyle style, Texture texture) {
		return copy(style, new TextureRegionDrawable(texture));
	}

	public static ImageButton.ImageButtonStyle copy(ImageButton.ImageButtonStyle style, Drawable drawable) {
		ImageButton.ImageButtonStyle output = new ImageButton.ImageButtonStyle(style);
		output.imageUp = drawable;
		return output;
	}

	public static TextButton.TextButtonStyle makeStyle(BitmapFont font) {
		TextButton.TextButtonStyle style = UI_SKIN.get(TextButton.TextButtonStyle.class);
		style = new TextButton.TextButtonStyle(style);
		style.font = font;
		style.fontColor = TECH_BLUE_LIGHT;
		return style;
	}

	public static ImageButton.ImageButtonStyle makeStyle(Texture texture) {
		return makeStyle(texture, 1);
	}

	public static ImageButton.ImageButtonStyle makeStyle(Texture texture, int frames) {
		ImageButton.ImageButtonStyle style = UI_SKIN.get(ImageButton.ImageButtonStyle.class);
		ImageButton.ImageButtonStyle copy = new ImageButton.ImageButtonStyle(style);

		AnimatedSprite up = Textures.asSpriteList(texture, frames, 1, 0, 0).asSprite();
		up.setBlend(TECH_BLUE_LIGHT);
		copy.imageUp = up;

		AnimatedSprite down = Textures.asSpriteList(texture, frames, 1, 0, 0).asSprite();
		down.setBlend(Color.WHITE);
		copy.imageDown = down;
		return copy;
	}
}
