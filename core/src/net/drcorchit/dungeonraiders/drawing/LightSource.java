package net.drcorchit.dungeonraiders.drawing;

import com.badlogic.gdx.graphics.Color;
import net.drcorchit.dungeonraiders.utils.Vector;

public interface LightSource {

	void update();

	Vector getLightPosition();

	//positive
	float getLightRadius();

	//varies from 0-1
	float getLightIntensity();

	Color getLightColor();
}
