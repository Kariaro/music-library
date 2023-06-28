package me.hardcoded.util.desktop;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class AppDesktop {
	private AppDesktop() {
		
	}
	
	public static void browseFileDirectory(File file) {
		if (file == null || !file.exists()) {
			return;
		}
		
		var desktop = Desktop.getDesktop();
		if (desktop.isSupported(Desktop.Action.BROWSE_FILE_DIR)) {
			desktop.browseFileDirectory(file);
		} else {
			// TODO - Implement this for multiple os
			try {
				if (file.isDirectory()) {
					desktop.open(file);
				} else {
					// TODO - Validate file path to not be malicious
					new ProcessBuilder("explorer.exe", "/select,\"" + file.toString() + "\"").start();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
