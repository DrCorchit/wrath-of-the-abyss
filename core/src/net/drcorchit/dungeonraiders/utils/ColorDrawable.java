package net.drcorchit.dungeonraiders.utils;

import com.badlogic.gdx.graphics.Color;
import net.drcorchit.dungeonraiders.assets.Textures;

public class ColorDrawable extends AnimatedSprite {

	public ColorDrawable(Color white) {
		super(new SpriteList(Textures.WHITE_POINT, 1, 1, 1, 1));
		setBlend(white);
	}
}
