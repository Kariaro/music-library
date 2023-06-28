package me.hardcoded.gui.window;

import me.hardcoded.algorithm.midi.MelodyScore;
import me.hardcoded.gui.component.song.SongComponent;
import me.hardcoded.util.python.FLStudioLookup;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class MusicLibraryLookup {
	public JPanel songList;
	
	public MusicLibraryLookup() {
		songList = new JPanel();
		songList.setLayout(new BoxLayout(songList, BoxLayout.PAGE_AXIS));
		songList.setBorder(null);
		songList.setBackground(new Color(0x1e2931)); // new Color(0x3f484d));
		songList.setMinimumSize(new Dimension(140, 0));
		
		addSong(0, "A");
		addSong(0, "B");
		addSong(0, "C");
		addSong(0, "D");
		addSong(0, "E");
		addSong(0, "F");
		addSong(0, "G");
		addSong(0, "H");
	}
	
	public void addSong(double score, String path) {
		SongComponent songComponent = new SongComponent();
		songComponent.setBackground((songList.getComponentCount() & 1) == 0
			? MusicLookup.Colors.SongBackground
			: MusicLookup.Colors.SongBackground.brighter());
		songComponent.setScore(score);
		songComponent.setPath(path);
		
		songList.add(songComponent);
	}
	
	public void display(List<Map.Entry<MelodyScore, FLStudioLookup.FLPProject>> scores) {
		songList.removeAll();
		
		for (var entry : scores) {
			addSong(entry.getKey().getCalculatedScore(), entry.getValue().file());
		}
		
		songList.repaint();
	}
}
