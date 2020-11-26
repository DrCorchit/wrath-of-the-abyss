package net.drcorchit.dungeonraiders.utils;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Random;

public class IOUtils {

	private static final File homeDir, workingDir;
	private static final Logger log;

	static {
		log = Logger.getLogger(IOUtils.class);
		String homePath = System.getProperty("user.home");
		File home = new File(homePath);
		homeDir = getFileAsChildOfFolder(home, "dungeonraiders");
		homeDir.mkdirs();

		File macAppFolder = new File("/Applications/dungeonraiders.app");
		if (OSUtils.CURRENT_OS.equals(OSUtils.OS.MAC) && macAppFolder.exists()) {
			workingDir = getFileAsChildOfFolder(macAppFolder, "Contents/app");
		} else {
			workingDir = new File(System.getProperty("user.dir"));
		}

		//user prefs, log files, mechanic cache, and remote assets are written to folders in this directory
		if (homeDir.isDirectory()) {
			log.info("init", "Home Directory: " + homeDir.getAbsolutePath());
		} else {
			log.error("init", "Home directory not found: " + homeDir.getAbsolutePath());
		}

		//local assets and ui skin are loaded from this directory (treated as read-only)
		if (workingDir.isDirectory()) {
			log.info("init", "Working Directory: " + workingDir.getAbsolutePath());
		} else {
			log.error("init", "Working directory not found: " + workingDir.getAbsolutePath());
		}
	}

	public static File getFileAsChildOfFolder(File folder, String relativePath) {
		if (!folder.exists()) {
			folder.mkdirs();
		}
		if (folder.isDirectory()) {
			String path = String.format("%s/%s", folder.getAbsolutePath(), relativePath);
			return new File(path);
		}
		String message = String.format("File %s is not a directory.", folder);
		throw new RuntimeException(message);
	}

	public static File getFileAsChildOfHome(String relativePath) {
		return getFileAsChildOfFolder(homeDir, relativePath);
	}

	public static File getFileAsChildOfWorkingDir(String relativePath) {
		return getFileAsChildOfFolder(workingDir, relativePath);
	}

	public static File[] listImageFilesAndFolders(File folder) {
		return folder.listFiles((file) -> (file.isDirectory() || file.getName().endsWith(".jpg") || file.getName().endsWith(".png")));
	}

	public static File[] listImageFiles(File folder) {
		return folder.listFiles((dir, name) -> (name.endsWith(".jpg") || name.endsWith(".png")));
	}
	
	public static File[] listFontFiles(File folder) {
		return folder.listFiles((dir, name) -> (name.endsWith(".ttf") || name.endsWith(".otf")));
	}
	
	public static File randomImageFromFolder(File folder) {
		if (!folder.isDirectory()) {
			log.warn("randomImageFromFolder", "Path does not point to a valid directory: "+ folder);
			return null;
		}
		File[] images = listImageFiles(folder);
		if (images == null) return null;
		int index = new Random().nextInt(images.length);
		return images[index];
	}

	public static boolean overwriteFile(File file, String content) {
		return overwriteFile(file, content.getBytes());
	}

	//writes to or overwrites the file
	public static boolean overwriteFile(File file, byte[] content) {
		String absPath = file.getAbsolutePath();
		try {
			if (!file.exists()) {
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
					log.info("overwriteFile", "Creating parent directory of " + absPath);
				}
				if (!file.createNewFile()) return false;
			}
			FileOutputStream f = new FileOutputStream(file);
			f.write(content);
			f.close();
			return true;
		} catch (Exception e) {
			log.error("overwriteFile", "unable to overwrite file: " + absPath, e);
			return false;
		}
	}

	@NotNull
	public static String readFileAsString(File file) throws IOException {
		if (file.exists()) {
			BufferedReader br = new BufferedReader(new FileReader(file));
			StringBuilder output = new StringBuilder();
			String s;
			while ((s = br.readLine()) != null) output.append(s).append("\n");
			br.close();
			return output.toString();
		} else {
			throw new FileNotFoundException("Not found: " + file.getAbsolutePath());
		}
	}
}
