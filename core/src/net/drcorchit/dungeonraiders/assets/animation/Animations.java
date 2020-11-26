package net.drcorchit.dungeonraiders.assets.animation;

import com.google.gson.JsonElement;
import net.drcorchit.dungeonraiders.utils.IOUtils;
import net.drcorchit.dungeonraiders.utils.JsonUtils;

import java.io.File;
import java.io.IOException;

public class Animations {

	private static final File ANIMATION_FOLDER = IOUtils.getFileAsChildOfWorkingDir("resources/animations");
	public static final Animation stand = loadPose("stand.json");
	public static final Animation jump = loadPose("jump.json");
	public static final Animation jogging = loadLoop("jogging.json");

	private static LoopingAnimation loadLoop(String file) {
		try {
			JsonElement info = JsonUtils.parseFile(IOUtils.getFileAsChildOfFolder(ANIMATION_FOLDER, file));
			return new LoopingAnimation(info.getAsJsonObject());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static Pose loadPose(String file) {
		try {
			JsonElement info = JsonUtils.parseFile(IOUtils.getFileAsChildOfFolder(ANIMATION_FOLDER, file));
			return new Pose(info.getAsJsonObject());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
