package me.hardcoded.util.desktop;

import java.io.File;

public class AppFiles {
	private AppFiles() {
		
	}
	
	public static String getFileName(File file, boolean keepExtension) {
		if (file == null) {
			throw new NullPointerException();
		}
		
		String name = file.getName();
		if (keepExtension) {
			return name;
		}
		
		int lastDot = name.lastIndexOf('.');
		if (lastDot == -1) {
			return name;
		}
		
		return name.substring(0, lastDot);
	}
}
