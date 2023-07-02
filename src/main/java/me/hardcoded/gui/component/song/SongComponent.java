package me.hardcoded.gui.component.song;

import me.hardcoded.util.desktop.AppDesktop;
import me.hardcoded.util.desktop.AppFiles;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Locale;

public class SongComponent extends JPanel {
	private File file;
	public JLabel label;
	public JLabel score;
	
	public SongComponent() {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setBorder(new EmptyBorder(5, 5, 5, 5));
		
		int height = 15;
		score = new JLabel();
		score.setForeground(MusicLookup.Colors.ScoreText);
		score.setSize(new Dimension(100, height));
		score.setMinimumSize(new Dimension(100, height));
		score.setPreferredSize(new Dimension(100, height));
		score.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
		score.setFont(MusicLookup.Fonts.Serif);
		score.setText("not evaluated");
		
		label = new JLabel();
		label.setForeground(MusicLookup.Colors.DefaultText);
		label.setFont(MusicLookup.Fonts.SerifUnderline);
		label.setSize(new Dimension(100, height));
		label.setMinimumSize(new Dimension(100, height));
		label.setPreferredSize(new Dimension(100, height));
		label.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
		label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				File localFile = file;
				if (e.getButton() == MouseEvent.BUTTON1 && localFile != null) {
					AppDesktop.browseFileDirectory(localFile);
				}
			}
		});
		
		// Add components
		add(score);
		add(label);
	}
	
	public void setPath(String path) {
		File localFile = new File(path);
		label.setText(AppFiles.getFileName(localFile, false));
		file = localFile;
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(0, 40);
	}
	
	@Override
	public Dimension getMaximumSize() {
		return new Dimension(Integer.MAX_VALUE, 40);
	}
	
	public void setScore(double score) {
		if (Double.isFinite(score)) {
			this.score.setText(String.format(Locale.US, "Similarity %.1f", score));
		} else {
			this.score.setText("not applicable");
		}
	}
}
