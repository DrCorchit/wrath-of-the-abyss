package net.drcorchit.dungeonraiders.drawing;

import com.badlogic.gdx.graphics.Color;
import net.drcorchit.dungeonraiders.drawing.shapes.Rectangle;
import net.drcorchit.dungeonraiders.utils.Vector;

public interface LightSource {

	void update();

	//geometric light sources interact with stage geometry
	boolean isGeometric();

	Vector getLightPosition();

	float getLightZ();

	//positive
	float getLightRadius();

	//varies from 0-1
	float getLightIntensity();

	Color getLightColor();

	default Rectangle getBoundingRectangle() {
		float r = getLightRadius();
		return new Rectangle(this::getLightPosition, 2 * r, 2 * r);
	}
}
