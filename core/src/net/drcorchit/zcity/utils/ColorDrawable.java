package net.drcorchit.zcity.utils;

import com.badlogic.gdx.graphics.Color;
import net.drcorchit.zcity.assets.Textures;

public class ColorDrawable extends AnimatedSprite {

	public ColorDrawable(Color white) {
		super(new SpriteList(Textures.white, 1, 1));
		setBlend(white);
	}
}
