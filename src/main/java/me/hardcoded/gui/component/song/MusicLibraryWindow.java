package me.hardcoded.gui.component.song;

import me.hardcoded.data.Note;
import me.hardcoded.data.persistent.ApplicationData;
import me.hardcoded.gui.component.border.CustomSplitPane;
import me.hardcoded.gui.component.border.RoundButton;
import me.hardcoded.gui.component.border.SmallScrollbar;
import me.hardcoded.gui.component.piano.PianoComponent;
import me.hardcoded.util.python.FLStudioLookup;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Locale;

public class MusicLibraryWindow extends JFrame {
	private final MusicLibraryLookup scoreViewer;
	private final PianoComponent pianoComponent;
	private final JPanel rootPanel;
	
	private JLabel searchPercentageLabel;
	private boolean isSearching;
	
	public MusicLibraryWindow() {
		setTitle("Music Library");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // TODO - Saving
		
		this.rootPanel = new JPanel();
		rootPanel.setBorder(null);
		rootPanel.setLayout(new BorderLayout());
		setContentPane(rootPanel);
		
		EventMap eventMap = new EventMap(rootPanel);
		createToolBar();
		
		this.scoreViewer = new MusicLibraryLookup();
		
		pianoComponent = new PianoComponent(eventMap);
		pianoComponent.setFocusCycleRoot(true);
		
		CustomSplitPane splitPane = new CustomSplitPane();
		splitPane.setDividerLocation(150);
		splitPane.setBorder(null);
		splitPane.setRightComponent(pianoComponent);
		
		{
			JScrollPane pane = new JScrollPane();
			pane.setFocusable(true);
			pane.setViewportView(scoreViewer.songList);
			pane.setViewportBorder(null);
			pane.setBorder(null);
			pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			
			pane.setVerticalScrollBar(new SmallScrollbar());
			pane.getVerticalScrollBar().setUnitIncrement(16);
			splitPane.setLeftComponent(pane);
		}
		rootPanel.add(splitPane, BorderLayout.CENTER);
		
		eventMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "library-search", this::performSearch);
	}
	
	private void performSearch() {
		if (isSearching) {
			return;
		}
		
		isSearching = true;
		
		List<Note> notes = pianoComponent.getNotes();
		Thread thread = new Thread(() -> {
			try {
				var projects = FLStudioLookup.getProjects(List.of(ApplicationData.getInstance().get("test")), percentage -> {
					// System.out.printf("File lookup: %.2f%%\n", percentage * 100);
					searchPercentageLabel.setText(String.format(Locale.US, "Search %.2f%%", percentage * 50));
				});
				
				var scores = FLStudioLookup.searchProjectsForMelody(projects, notes, percentage -> {
					// System.out.printf("Search lookup: %.2f%%\n", percentage * 100);
					searchPercentageLabel.setText(String.format(Locale.US, "Search %.2f%%", percentage * 50 + 50));
				});
				
				SwingUtilities.invokeLater(() -> {
					scoreViewer.display(scores);
				});
			} finally {
				isSearching = false;
			}
		});
		
		thread.start();
		// FLStudioLookup.performSearch(notes);
	}
	
	private void createToolBar() {
		JPanel toolPanel = new JPanel();
		toolPanel.setLayout(new BoxLayout(toolPanel, BoxLayout.LINE_AXIS));
		toolPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		toolPanel.setBackground(MusicLookup.Colors.SongBackground);
		
		searchPercentageLabel = new JLabel("Not started");
		searchPercentageLabel.setPreferredSize(new Dimension(140, 30));
		searchPercentageLabel.setMinimumSize(new Dimension(140, 0));
		searchPercentageLabel.setMaximumSize(new Dimension(140, Integer.MAX_VALUE));
		searchPercentageLabel.setSize(new Dimension(140, 30));
		searchPercentageLabel.setForeground(Color.white);
		toolPanel.add(searchPercentageLabel);
		
		RoundButton search = new RoundButton("Search", 20);
		search.setBackground(MusicLookup.Colors.ButtonBackground);
		search.setForeground(MusicLookup.Colors.ButtonText);
		search.setPreferredSize(new Dimension(100, 24));
		search.setMaximumSize(new Dimension(100, 24));
		toolPanel.add(search);
		
		rootPanel.add(toolPanel, BorderLayout.NORTH);
	}
}
