package me.hardcoded.main;

import me.hardcoded.gui.window.MusicLibraryWindow;
import me.hardcoded.util.python.PythonRunner;

import javax.swing.*;
import java.awt.*;

public class Main {
	public static void main(String[] args) {
		// TODO: Init this earlier
		// TODO: Add loading popup
		PythonRunner.init();
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		// JFrame.setDefaultLookAndFeelDecorated(true);
		
		// Setup window class
		Dimension size = new Dimension(640, 380);
		MusicLibraryWindow window = new MusicLibraryWindow();
		window.setSize(size);
		window.setVisible(true);
	}
}
