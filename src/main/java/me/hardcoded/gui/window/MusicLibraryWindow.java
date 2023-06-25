package me.hardcoded.gui.window;

import me.hardcoded.gui.component.piano.PianoComponent;

import javax.swing.*;

public class MusicLibraryWindow extends JFrame {
	public MusicLibraryWindow() {
		setTitle("Music Library");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // TODO - Saving
		
		
		setContentPane(new PianoComponent());
	}
}
