package me.hardcoded.main;

import me.hardcoded.gui.window.MusicLibraryWindow;
import me.hardcoded.python.FLStudioPython;
import me.hardcoded.python.PythonRunner;

import java.awt.*;
import java.io.File;

public class Main {
	public static void main(String[] args) {
		for (var list : FLStudioPython.readProjectPatterns(new File("C:\\Users\\Admin\\AppData\\Local\\Temp\\music-library-data\\BeatStep_2022_07_06.flp"))) {
			System.out.println(list);
		}
		
		// Setup window class
		Dimension size = new Dimension(640, 380);
		MusicLibraryWindow window = new MusicLibraryWindow();
		window.setSize(size);
		window.setVisible(true);
	}
}
