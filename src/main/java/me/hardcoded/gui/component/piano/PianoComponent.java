package me.hardcoded.gui.component.piano;

import me.hardcoded.data.Note;
import me.hardcoded.gui.component.border.SmallScrollbar;
import me.hardcoded.gui.component.song.EventMap;
import me.hardcoded.sound.PianoSound;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

public class PianoComponent extends JPanel {
	protected final PianoRollTimeline rollTimeline;
	protected final PianoRoll roll;
	protected final PianoKeys keys;
	protected final PianoSound sound;
	protected final Set<Integer> playNotes;
	
	protected double beatsPerMinute = 140;
	protected int stepWidth = 18;
	protected int stepHeight = 14;
	
	protected final Timer playTimer;
	protected long playTimerStart;
	
	public PianoComponent(EventMap eventMap) {
		playNotes = new HashSet<>();
		sound = new PianoSound();
		keys = new PianoKeys(this);
		roll = new PianoRoll(this, eventMap);
		rollTimeline = new PianoRollTimeline(this, roll);
		
		// Add elements
		setLayout(new BorderLayout());
		add(rollTimeline, BorderLayout.NORTH);
		setTimeTick(24);
		
		// Scroll
		JPanel rollAndKeys = new JPanel();
		rollAndKeys.setFocusable(true);
		rollAndKeys.setLayout(new BorderLayout());
		rollAndKeys.add(keys, BorderLayout.LINE_START);
		rollAndKeys.add(roll, BorderLayout.CENTER);
		
		JScrollPane pane = new JScrollPane();
		pane.setFocusable(true);
		pane.setViewportView(rollAndKeys);
		pane.setViewportBorder(null);
		pane.setBorder(null);
		pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		pane.setVerticalScrollBar(new SmallScrollbar());
		pane.getVerticalScrollBar().setUnitIncrement(16);
		add(pane, BorderLayout.CENTER);
		
		playTimer = new Timer(10, (e) -> onTick());
		
		setFocusable(true);
		
		eventMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), PianoAction.Play, () -> {
			if (playTimer.isRunning()) {
				playTimer.stop();
				roll.setTimeTick(-1);
				synchronized (playNotes) {
					playNotes.clear();
				}
				repaint();
			} else {
				play();
			}
		});
	}
	
	public int getOctaveHeight() {
		return 12 * getStepHeight();
	}
	
	public int getOctaveCount() {
		return 8;
	}
	
	public int getOctaveOffset() {
		return 1;
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
	
	public void setStepHeight(int height) {
		if (height < 4) {
			height = 4;
		}
		
		if (height > 40) {
			height = 40;
		}
		
		this.stepHeight = height;
		roll.repaint();
		keys.repaint();
		rollTimeline.repaint();
		roll.getParent().revalidate();
	}
	
	public int getStepWidth() {
		return stepWidth;
	}
	
	public int getStepHeight() {
		return stepHeight;
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
		
		// 16 beats
		if (tickIndex > 24 * 16 * 16) {
			playTimer.stop();
			playNotes.clear();
		}
		
		synchronized (playNotes) {
			playNotes.clear();
			for (var note : roll.getNotes()) {
				int noteMillis = (int) (((note.end - note.start) / ticksPerSecond) * 1000);
				if (note.start < tickIndex && note.start >= prevIndex) {
					// Play
					sound.playNote(note.note + 12, 80, noteMillis);
				}
				
				if (note.start < tickIndex && note.end > tickIndex) {
					playNotes.add(note.note);
				}
			}
		}
		
		roll.setTimeTick(tickIndex);
		roll.repaint();
		keys.repaint();
	}
	
	public java.util.List<Note> getNotes() {
		return roll.getNotes();
	}
}
