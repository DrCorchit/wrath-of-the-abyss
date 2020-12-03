package net.drcorchit.dungeonraiders.drawing;

import com.badlogic.gdx.graphics.Color;
import net.drcorchit.dungeonraiders.drawing.shapes.Rectangle;
import net.drcorchit.dungeonraiders.utils.Vector;

public interface LightSource {

	void update();

	//geometric light sources interact with stage geometry
	boolean isGeometric();

	Vector getLightPosition();

	//positive
	float getLightRadius();

	//varies from 0-1
	float getLightIntensity();

	Color getLightColor();

	default Rectangle getBoundingRectangle() {
		return new Rectangle(() -> getLightPosition().subtract(getLightRadius(), getLightRadius()),
				getLightRadius() * 2, getLightRadius() * 2);
	}
}
