package net.drcorchit.dungeonraiders.utils;

import java.util.Locale;

//Copied from https://memorynotfound.com/detect-os-name-version-java/ with modifications
public class OSUtils {

	private static final Logger log = Logger.getLogger(OSUtils.class);

	public enum OS {
		WINDOWS,
		UNIX,
		POSIX_UNIX,
		MAC,
		OTHER
	}

	public static final OS CURRENT_OS;

	static {
		OS temp;

		try {
			temp = detectOS();
			log.info("init", "Detected OS: " + temp);
		} catch (Exception e) {
			temp = OS.OTHER;
			log.error("detectOS", "Error while detecting OS", e);
		}
		CURRENT_OS = temp;
	}

	private static OS detectOS() {
		String osName = System.getProperty("os.name");
		if (osName == null) {
			throw new RuntimeException("os.name not found");
		}
		osName = osName.toLowerCase(Locale.ENGLISH);
		if (osName.contains("windows")) {
			return OS.WINDOWS;
		} else if (osName.contains("linux")
				|| osName.contains("mpe/ix")
				|| osName.contains("freebsd")
				|| osName.contains("irix")
				|| osName.contains("digital unix")
				|| osName.contains("unix")) {
			return OS.UNIX;
		} else if (osName.contains("mac os")) {
			return OS.MAC;
		} else if (osName.contains("sun os")
				|| osName.contains("sunos")
				|| osName.contains("solaris")) {
			return OS.POSIX_UNIX;
		} else if (osName.contains("hp-ux")
				|| osName.contains("aix")) {
			return OS.POSIX_UNIX;
		} else {
			return OS.OTHER;
		}
	}
}