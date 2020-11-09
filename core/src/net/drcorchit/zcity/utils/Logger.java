package net.drcorchit.zcity.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

import javax.annotation.Nullable;
import java.io.File;
import java.util.HashMap;
import java.util.TreeMap;

import static org.apache.logging.log4j.Level.*;

public class Logger {
	private static final HashMap<Class<?>, Logger> loggers = new HashMap<>();
	private static final String format = "%d{HH:mm:ss.SSS} [%level] %logger{36} - %msg%n";
	private static final File logFolder;
	private static final Logger log;
	
	//Civplanet logger is currently a facade for this class
	private final org.apache.logging.log4j.Logger logger;
	
	static {
		String home = System.getProperty("user.home");
		String now = Long.toString(System.currentTimeMillis());
		String logFolderPath = String.format("%s/CivPlanet/logs", home);
		logFolder = new File(logFolderPath);
		String logFilePath = String.format("%s/%s.log", logFolderPath, now);
		
		ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
		LayoutComponentBuilder layout = builder.newLayout("PatternLayout");
		layout.addAttribute("pattern", format);
		
		AppenderComponentBuilder console = builder.newAppender("Console", "Console");
		console.addAttribute("target", "SYSTEM_OUT");
		console.add(layout);
		builder.add(console);
		
		AppenderComponentBuilder file = builder.newAppender("File", "File");
		file.addAttribute("fileName", logFilePath);
		file.addAttribute("immediateFlush", "true");
		file.addAttribute("append", "true");
		file.add(layout);
		builder.add(file);
		
		RootLoggerComponentBuilder root = builder.newRootLogger(INFO);
		root.add(builder.newAppenderRef("Console"));
		root.add(builder.newAppenderRef("File"));
		builder.add(root);
		Configurator.initialize(builder.build());
		
		log = getLogger(Logger.class);
		clearOldLogFiles(5);
	}
	
	private static void clearOldLogFiles(int numToKeep) {
		try {
			File[] files = logFolder.listFiles();
			if (files == null || files.length <= numToKeep) return;
			TreeMap<Long, File> newestFiles = new TreeMap<>();
			for (File file : files) {
				if (file.getName().matches("\\d+\\.log")) {
					String name = file.getName();
					long date = Long.parseLong(name.substring(0, name.length() - 4));
					newestFiles.put(date, file);
				}
			}
			
			if (newestFiles.size() <= numToKeep) return;
			int numberToDelete = newestFiles.size() - numToKeep;
			for (File file : newestFiles.values()) {
				if (file.delete()) {
					log.info("clearOldLogFiles", "Deleted old log file: " + file.getName());
					numberToDelete--;
				}
				if (numberToDelete == 0) return;
			}
		} catch (Exception e) {
			log.error("clearOldLogFile", "Error while clearing old log files", e);
		}
	}
	
	private Logger(Class<?> clazz) {
		logger = LogManager.getLogger(clazz);
		info("init", "Created logger.");
	}
	
	public void debug(String method, String message) {
		log(new LogInfo(DEBUG, method, message, null));
	}
	
	public void info(String method, String message) {
		log(new LogInfo(INFO, method, message, null));
	}
	
	public void warn(String method, String message) {
		log(new LogInfo(WARN, method, message, null));
	}
	
	public void error(String method, String message) {
		log(new LogInfo(ERROR, method, message, null));
	}
	
	public void error(String method, String message, Throwable error) {
		log(new LogInfo(ERROR, method, message, error));
	}
	
	public void fatal(String method, String message) {
		log(new LogInfo(FATAL, method, message, null));
	}
	
	public void fatal(String method, String message, Throwable error) {
		log(new LogInfo(FATAL, method, message, error));
	}
	
	private void log(LogInfo info) {
		if (info.error == null) {
			logger.log(info.level, info.getLogString());
		} else {
			logger.log(info.level, info.getLogString(), info.error);
		}
	}
	
	public static Logger getLogger(Class<?> clazz) {
		return loggers.computeIfAbsent(clazz, Logger::new);
	}
	
	private static class LogInfo {
		private final Level level;
		private final String method;
		private final String message;
		@Nullable
		private final Throwable error;
		
		private LogInfo(Level level, String method, String message, @Nullable Throwable error) {
			this.level = level;
			this.method = method;
			this.message = message;
			this.error = error;
		}
		
		public String getLogString() {
			return String.format("[%s] %s", method, message);
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append(level.name()).append(" [").append(method).append("] ").append(message);
			if (error != null) {
				builder.append(System.lineSeparator()).append(StringUtils.exceptionToString(error));
			}
			return builder.toString();
		}
	}
}
