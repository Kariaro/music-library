package me.hardcoded.data.persistent;

import java.io.File;
import java.nio.file.Path;

public class ApplicationData {
	private static final ApplicationData instance = new ApplicationData();
	public static ApplicationData getInstance() {
		return instance;
	}
	
	private static final String NAME = "music-library-data";
	private final File dataPath;
	
	private ApplicationData() {
		dataPath = new File(System.getenv("APPDATA"), NAME);
		
		if (!dataPath.exists() && !dataPath.mkdirs()) {
			throw new RuntimeException("Failed to generate application directory '" + dataPath + "'");
		}
	}
	
	/**
	 * Returns the path relative to the application data path
	 */
	public Path get(String... path) {
		return Path.of(dataPath.toString(), path);
	}
	
	/**
	 * Ensure that the directory is generated and exists
	 * @throws NullPointerException if the directory could not be generated
	 */
	public File ensureDirectory(String... path) {
		File directory = get(path).toFile();
		
		if (!directory.exists() && !directory.mkdirs()) {
			throw new NullPointerException("Failed to generate directory");
		}
		
		return directory;
	}
}
