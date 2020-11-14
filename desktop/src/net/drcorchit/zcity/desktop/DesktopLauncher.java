package net.drcorchit.zcity.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import net.drcorchit.zcity.ZCityGame;
import net.drcorchit.zcity.utils.Resolution;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		Resolution res = Resolution.HD_1080;
		config.height = res.height;
		config.width = res.width;
		config.forceExit = false;
		new LwjglApplication(new ZCityGame(), config);
	}
}
