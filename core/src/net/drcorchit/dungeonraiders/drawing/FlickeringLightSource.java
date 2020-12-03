package net.drcorchit.dungeonraiders.drawing;

import com.badlogic.gdx.graphics.Color;
import net.drcorchit.dungeonraiders.utils.MathUtils;
import net.drcorchit.dungeonraiders.utils.Vector;

import java.util.Random;
import java.util.function.Supplier;

public class FlickeringLightSource implements LightSource {

	private final Supplier<Vector> position;
	private final Color color;
	private final Random random;
	private boolean geometric;
	private float hue, saturation, value, hueFlicker, maxHue, minHue;
	private float radius, flicker, scale, maxScale, minScale;

	public FlickeringLightSource(Supplier<Vector> position) {
		this.position = position;
		color = Color.WHITE.cpy();
		random = new Random();
		this.geometric = false;
		hue = 0;
		saturation = 1;
		value = 1;
		scale = 1;
	}

	public void setSaturation(float saturation) {
		this.saturation = MathUtils.clamp(0f, saturation, 1f);
		updateColor();
	}

	public void setValue(float value) {
		this.value = MathUtils.clamp(0f, value, 1f);
		updateColor();
	}

	private void updateColor() {
		float tempHue = (float) MathUtils.mod(hue, 360);
		this.color.fromHsv(tempHue, saturation, value);
	}

	public void disableHueFlicker() {
		hueFlicker = 0;
	}

	public void setHueFlicker(float hueFlicker, float minHue, float maxHue) {
		this.hueFlicker = hueFlicker;
		if (minHue > maxHue) minHue -= 360;
		this.minHue = minHue;
		this.maxHue = maxHue;
		hue = MathUtils.clamp(minHue, hue, maxHue);
	}

	public void disableFlicker() {
		flicker = 0;
	}

	public void setFlicker(float flicker, float minScale, float maxScale) {
		this.flicker = flicker;
		this.minScale = minScale;
		this.maxScale = maxScale;
		scale = MathUtils.clamp(minScale, scale, maxScale);
	}

	public void setGeometric(boolean geometric) {
		this.geometric = geometric;
	}

	@Override
	public boolean isGeometric() {
		return geometric;
	}

	@Override
	public void update() {
		if (hueFlicker != 0) {
			float delta = (random.nextFloat() * 2 - 1) * hueFlicker;
			hue = MathUtils.clamp(minHue, hue + delta, maxHue);
			updateColor();
		}

		if (flicker != 0) {
			float delta = (random.nextFloat() * 2 - 1) * flicker;
			scale = MathUtils.clamp(minScale, scale + delta, maxScale);
		}
	}

	@Override
	public Vector getLightPosition() {
		return position.get();
	}

	@Override
	public float getLightRadius() {
		return radius * scale;
	}

	public void setLightRadius(float radius) {
		this.radius = radius;
	}

	@Override
	public float getLightIntensity() {
		return color.a;
	}

	@Override
	public Color getLightColor() {
		return color;
	}
}
