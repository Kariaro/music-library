package me.hardcoded.gui.component.piano;

import me.hardcoded.python.FLStudioPython;
import me.hardcoded.sound.PianoSound;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class PianoComponent extends JPanel {
	protected final PianoKeys section;
	protected final PianoRollTimeline rollTimeline;
	protected final PianoRoll roll;
	protected final PianoSound sound;
	
	protected double beatsPerMinute = 120;
	protected int stepWidth = 28;
	
	protected final Timer playTimer;
	protected long playTimerStart;
	
	public PianoComponent() {
		sound = new PianoSound();
		
		section = new PianoKeys(this, getOctaveCount());
		roll = new PianoRoll(this);
		rollTimeline = new PianoRollTimeline(this, roll);
		
		// Add elements
		setLayout(new BorderLayout());
		add(rollTimeline, BorderLayout.NORTH);
		// add(section, BorderLayout.LINE_START);
		// add(roll, BorderLayout.CENTER);
		setTimeBeat(100);
		
		// Scroll
		JPanel rollAndSection = new JPanel();
		rollAndSection.setFocusable(true);
		rollAndSection.setLayout(new BorderLayout());
		rollAndSection.add(section, BorderLayout.LINE_START);
		rollAndSection.add(roll, BorderLayout.CENTER);
		
		JScrollPane pane = new JScrollPane();
		pane.setFocusable(true);
		pane.setViewportView(rollAndSection);
		pane.setViewportBorder(null);
		pane.setBorder(null);
		pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		pane.getVerticalScrollBar().setUnitIncrement(16);
		add(pane, BorderLayout.CENTER);
		
		playTimer = new Timer(10, (e) -> onTick());
		
		setFocusable(true);
		
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), PianoAction.Play);
		getActionMap().put(PianoAction.Play, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (playTimer.isRunning()) {
					playTimer.stop();
					roll.setTimeBeat(-1);
					repaint();
				} else {
					play();
				}
			}
		});
		
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), PianoAction.Search);
		getActionMap().put(PianoAction.Search, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				FLStudioPython.performSearch(roll.getNotes());
			}
		});
	}
	
	public int getOctaveHeight() {
		return 98 + 70;
	}
	
	public int getOctaveCount() {
		return 7;
	}
	
	public int getOctaveOffset() {
		return 2;
	}
	
	boolean isNoteHovered(int index) {
		return section.isNoteSelected(index);
	}
	
	public void setTimeBeat(double beat) {
		roll.setTimeBeat(beat);
	}
	
	public void setStepWidth(int width) {
		if (width < 4) {
			width = 4;
		}
		if (width > 40) {
			width = 40;
		}
		
		this.stepWidth = width;
		roll.repaint();
		rollTimeline.repaint();
	}
	
	public int getStepWidth() {
		return stepWidth;
	}
	
	/**
	 * Start playing the piano timeline
	 */
	public void play() {
		play(rollTimeline.getTimeStart());
	}
	
	public void play(double offset) {
		roll.setTimeBeat(offset);
		
		double beatsPerSecond = (beatsPerMinute) / 60.0;
		long millisecondOffset = (long) ((offset / beatsPerSecond) * 1000L);
		playTimerStart = System.currentTimeMillis() - millisecondOffset;
		playTimer.restart();
	}
	
	public boolean isPlaying() {
		return playTimer.isRunning();
	}
	
	private void onTick() {
		long currentTime = System.currentTimeMillis();
		
		// 4 beats per measure
		// 1 measure = 4 beats
		// bpm / 4 = measures
		
		// 240 bpm = 240 beats / min = 60 measures / min
		// 1 messure
		
		double beatsPerSecond = (beatsPerMinute) / 60.0;
		double beatIndex = ((currentTime - playTimerStart) / 1000.0) * beatsPerSecond;
		
		double previousBeat = roll.getTimeBeat();
		roll.setTimeBeat(beatIndex);
		roll.repaint();
		
		if (beatIndex > 16) {
			playTimer.stop();
		}
		
		for (var note : roll.getNotes()) {
			int noteMillis = (int) (beatsPerSecond / 4 * (note.end - note.start) * 1000);
			if (note.start < beatIndex * 4 && note.start >= previousBeat * 4) {
				// Play
				sound.playNote(note.note + 12, 80, noteMillis);
			}
		}
	}
}
