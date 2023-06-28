package me.hardcoded.gui.component.piano;

import me.hardcoded.data.Note;
import me.hardcoded.gui.component.border.SmallScrollbar;
import me.hardcoded.gui.window.EventMap;
import me.hardcoded.sound.PianoSound;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class PianoComponent extends JPanel {
	protected final PianoRollTimeline rollTimeline;
	protected final PianoRoll roll;
	protected final PianoKeys keys;
	protected final PianoSound sound;
	
	protected double beatsPerMinute = 140;
	protected int stepWidth = 18;
	
	protected final Timer playTimer;
	protected long playTimerStart;
	
	public PianoComponent(EventMap eventMap) {
		sound = new PianoSound();
		
		keys = new PianoKeys(this);
		roll = new PianoRoll(this, eventMap);
		rollTimeline = new PianoRollTimeline(this, roll);
		
		// Add elements
		setLayout(new BorderLayout());
		add(rollTimeline, BorderLayout.NORTH);
		// add(section, BorderLayout.LINE_START);
		// add(roll, BorderLayout.CENTER);
		setTimeTick(24);
		
		// Scroll
		JPanel rollAndSection = new JPanel();
		rollAndSection.setFocusable(true);
		rollAndSection.setLayout(new BorderLayout());
		rollAndSection.add(keys, BorderLayout.LINE_START);
		rollAndSection.add(roll, BorderLayout.CENTER);
		
		JScrollPane pane = new JScrollPane();
		pane.setFocusable(true);
		pane.setViewportView(rollAndSection);
		pane.setViewportBorder(null);
		pane.setBorder(null);
		pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		pane.setVerticalScrollBar(new SmallScrollbar());
		pane.getVerticalScrollBar().setUnitIncrement(16);
		add(pane, BorderLayout.CENTER);
		
		playTimer = new Timer(10, (e) -> onTick());
		
		setFocusable(true);
		
		eventMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), PianoAction.Play, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (playTimer.isRunning()) {
					playTimer.stop();
					roll.setTimeTick(-1);
					repaint();
				} else {
					play();
				}
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
	
	/**
	 * Returns the lowest note visible in this piano
	 */
	public int getLowestNote() {
		return getOctaveOffset() * 12;
	}
	
	/**
	 * Returns the highest note visible in this piano
	 */
	public int getHighestNote() {
		return (getOctaveOffset() + getOctaveCount()) * 12 - 1;
	}
	
	public void setTimeTick(int tick) {
		roll.setTimeTick(tick);
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
		play(rollTimeline.getTimeTickStart());
	}
	
	public void play(int tickOffset) {
		roll.setTimeTick(tickOffset);
		
		double ticksPerSecond = ((beatsPerMinute) / 60.0) * 4.0 * 24.0;
		long millisecondOffset = (long) ((tickOffset / ticksPerSecond) * 1000L);
		playTimerStart = System.currentTimeMillis() - millisecondOffset;
		playTimer.restart();
	}
	
	public boolean isPlaying() {
		return playTimer.isRunning();
	}
	
	private void onTick() {
		long currentTime = System.currentTimeMillis();
		double ticksPerSecond = ((beatsPerMinute) / 60.0) * 4.0 * 24.0;
		int tickIndex = (int) (((currentTime - playTimerStart) / 1000.0) * ticksPerSecond);
		
		int prevIndex = roll.getTimeTick();
		roll.setTimeTick(tickIndex);
		roll.repaint();
		
		// 16 beats
		if (tickIndex > 24 * 16 * 16) {
			playTimer.stop();
		}
		
		for (var note : roll.getNotes()) {
			int noteMillis = (int) (((note.end - note.start) / ticksPerSecond) * 1000);
			if (note.start < tickIndex && note.start >= prevIndex) {
				// Play
				sound.playNote(note.note + 12, 80, noteMillis);
			}
		}
	}
	
	public java.util.List<Note> getNotes() {
		return roll.getNotes();
	}
}
