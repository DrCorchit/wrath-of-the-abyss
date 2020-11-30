package net.drcorchit.dungeonraiders.drawing;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.google.common.collect.ImmutableList;
import net.drcorchit.dungeonraiders.drawing.AnimatedSprite;

public class SpriteList {
	private final ImmutableList<TextureRegion> frames;
	public final float originX, originY;

	public SpriteList(Sprite sprite) {
		frames = ImmutableList.of(sprite);
		originX = sprite.getOriginX();
		originY = sprite.getOriginY();
		sprite.getTexture().setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
	}

	public SpriteList(Texture texture, int framesHoriz, int framesVert, float originX, float originY) {
		if (texture == null) throw new NullPointerException();
		texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		int w = texture.getWidth() / framesHoriz;
		int h = texture.getHeight() / framesVert;

		// Use the split utility method to create a 2D array of TextureRegions. This is
		// possible because this sprite sheet contains frames of equal size and they are
		// all aligned.
		TextureRegion[][] regions = TextureRegion.split(texture, w, h);
		ImmutableList.Builder<TextureRegion> framesBuilder = ImmutableList.builder();
		for (int j = 0; j < framesVert; j++) {
			for (int i = 0; i < framesHoriz; i++) {
				framesBuilder.add(regions[j][i]);
			}
		}

		frames = framesBuilder.build();
		this.originX = originX;
		this.originY = originY;
	}

	public TextureRegion get(double index) {
		return frames.get((int) index);
	}

	public int length() {
		return frames.size();
	}

	public AnimatedSprite asSprite() {
		return new AnimatedSprite(this);
	}
}
