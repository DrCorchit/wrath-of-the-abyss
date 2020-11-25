package net.drcorchit.zcity.assets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.drcorchit.zcity.ZCityGame;
import net.drcorchit.zcity.utils.AnimatedSprite;
import net.drcorchit.zcity.utils.JsonUtils;
import net.drcorchit.zcity.utils.MathUtils;
import net.drcorchit.zcity.utils.Vector2;

import java.util.ArrayList;

public class Skin {

	private final ArrayList<SkinSprite> sprites;

	public Skin() {
		sprites = new ArrayList<>();
	}

	public Skin(JsonObject info) {
		this();
		JsonArray sprites = JsonUtils.getArray(info, "sprites");
		for (JsonElement ele : sprites) {
			JsonObject sprite = ele.getAsJsonObject();
			String jointName = sprite.get("joint").getAsString();
			String texturePath = sprite.get("texture").getAsString();
			float x = JsonUtils.getFloat(sprite, "x", 0);
			float y = JsonUtils.getFloat(sprite, "y", 0);
			float angle = JsonUtils.getFloat(sprite, "angle", 0);
			Color blend = JsonUtils.getColor(sprite, "blend", Color.WHITE);

			Texture texture = LocalAssets.getInstance().getTexture(texturePath);
			if (texture == null) {
				throw new NullPointerException("Could not load texture: " + texturePath);
			}
			AnimatedSprite animatedSprite = Textures.asSpriteList(texture).asSprite();
			animatedSprite.setOffset(x, y);
			animatedSprite.setBlend(blend);
			addSprite(jointName, animatedSprite, angle);
		}
	}

	public void addSprite(String jointName, AnimatedSprite sprite) {
		addSprite(jointName, sprite, 0);
	}

	public void addSprite(String jointName, AnimatedSprite sprite, float angle) {
		sprites.add(new SkinSprite(sprite, jointName, angle));
	}

	public void draw(Skeleton skeleton, float x, float y) {
		draw(skeleton, new Vector2(x, y));
	}

	public void draw(Skeleton skeleton, Vector2 position) {
		sprites.forEach(spr -> {
			spr.sprite.updateFrame();
			Skeleton.Joint joint = skeleton.getJoint(spr.jointName);
			Vector2 jointPos = joint.getRootRelativePosition().add(position);
			float rotation = joint.getAbsoluteAngle() + spr.angle;
			float xScale = skeleton.scale;
			float yScale = skeleton.scale;

			if (skeleton.flipped) {
				jointPos = jointPos.mirrorAroundLine(position, 90);
				rotation = MathUtils.mirrorAroundAxis(rotation, 0);
				xScale *= -1;
			}

			spr.draw(jointPos.key, jointPos.val, xScale, yScale, rotation);
		});
	}

	private static class SkinSprite {
		private final String jointName;
		private final AnimatedSprite sprite;
		private final float angle;

		private SkinSprite(AnimatedSprite sprite, String jointName, float angle) {
			this.sprite = sprite;
			this.jointName = jointName;
			this.angle = angle;
		}

		public void draw(float sprX, float sprY, float xScale, float yScale, float rotation) {
			sprite.drawScaled(ZCityGame.draw().getBatch(), sprX, sprY, xScale, yScale, rotation);
		}
	}
}
