package me.hardcoded.gui.component.piano;

import me.hardcoded.data.Note;
import me.hardcoded.gui.window.EventMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;

public class PianoRoll extends JPanel {
	private final PianoComponent parent;
	private final PianoState state;
	
	private final Set<Note> selectedNotes = new HashSet<>();
	private final Rectangle selectRect = new Rectangle();
	private int timeTick;
	
	public PianoRoll(PianoComponent parent, EventMap eventMap) {
		this.parent = parent;
		this.state = new PianoState();
		setBackground(PianoColors.RollFlat);
		
		state.addNotes(List.of(
			new Note(Note.getNoteFromName("A3"),  0 * 24,  2 * 24),
			new Note(Note.getNoteFromName("B3"),  2 * 24,  4 * 24),
			new Note(Note.getNoteFromName("C4"),  4 * 24,  6 * 24),
			new Note(Note.getNoteFromName("D4"),  6 * 24,  8 * 24),
			new Note(Note.getNoteFromName("E4"),  8 * 24, 10 * 24),
			new Note(Note.getNoteFromName("A4"), 10 * 24, 12 * 24),
			new Note(Note.getNoteFromName("E4"), 12 * 24, 14 * 24),
			new Note(Note.getNoteFromName("D4"), 16 * 24, 18 * 24),
			new Note(Note.getNoteFromName("G4"), 18 * 24, 20 * 24),
			new Note(Note.getNoteFromName("E4"), 20 * 24, 22 * 24),
			new Note(Note.getNoteFromName("D4"), 24 * 24, 26 * 24),
			new Note(Note.getNoteFromName("G4"), 26 * 24, 28 * 24),
			new Note(Note.getNoteFromName("E4"), 28 * 24, 30 * 24),
			new Note(Note.getNoteFromName("C4"), 30 * 24, 32 * 24),
			new Note(Note.getNoteFromName("F4"), 32 * 24, 34 * 24),
			new Note(Note.getNoteFromName("D4"), 34 * 24, 36 * 24),
			new Note(Note.getNoteFromName("F4"), 36 * 24, 38 * 24),
			new Note(Note.getNoteFromName("C5"), 38 * 24, 40 * 24),
			new Note(Note.getNoteFromName("B4"), 40 * 24, 42 * 24),
			new Note(Note.getNoteFromName("A4"), 42 * 24, 44 * 24),
			new Note(Note.getNoteFromName("G4"), 44 * 24, 46 * 24),
			new Note(Note.getNoteFromName("A4"), 46 * 24, 48 * 24),
			new Note(Note.getNoteFromName("E4"), 48 * 24, 50 * 24)));
		
		setFocusable(true);
		
		eventMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), PianoAction.RollDeleteSelection, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				synchronized (state) {
					state.removeNotes(selectedNotes);
					selectedNotes.clear();
					repaint();
				}
			}
		});
		
		eventMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK), PianoAction.RollSelectAll, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				synchronized (state) {
					selectedNotes.clear();
					selectedNotes.addAll(state.getNotes());
					repaint();
				}
			}
		});
		
		eventMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), PianoAction.RollUndo, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				synchronized (state) {
					state.undo();
					repaint();
				}
			}
		});
		
		// Move action
		addMoveActions(eventMap);
		
		MouseAdapter adapter = new MouseAdapter() {
			private int mouseButton;
			private boolean controlStart;
			private Point mouseStart;
			
			@Override
			public void mousePressed(MouseEvent e) {
				mouseButton = e.getButton();
				controlStart = e.isControlDown();
				mouseStart = e.getPoint();
				
				if (!controlStart) {
					select(e.getPoint(), mouseButton == MouseEvent.BUTTON1);
				}
				
				synchronized (state) {
					selectedNotes.clear();
				}
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				if (controlStart) {
					// TODO - Control is select
					if (mouseButton == MouseEvent.BUTTON1) {
						Point next = e.getPoint();
						selectRect.setBounds(
							Math.min(mouseStart.x, next.x),
							Math.min(mouseStart.y, next.y),
							Math.abs(mouseStart.x - next.x),
							Math.abs(mouseStart.y - next.y)
						);
						repaint();
					}
				} else {
					select(e.getPoint(), mouseButton == MouseEvent.BUTTON1);
				}
			}
			
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.isControlDown()) {
					parent.setStepWidth(parent.getStepWidth() - e.getWheelRotation());
					repaint();
				} else {
					getParent().dispatchEvent(e);
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				lastPlayIdxX = -1;
				if (!selectRect.isEmpty()) {
					int stepWidth = parent.getStepWidth();
					synchronized (state) {
						selectedNotes.clear();
						
						for (var note : state.getNotes()) {
							int xx = note.start * stepWidth / 24;
							int yy = 14 * ((parent.getOctaveCount() + parent.getOctaveOffset()) * 12 - note.note - 1);
							
							int height = 14;
							int width = (note.end - note.start) * stepWidth / 24; // in steps
							
							if (selectRect.intersects(xx, yy, width, height)) {
								selectedNotes.add(note);
							}
						}
					}
				}
				
				selectRect.setBounds(0, 0, 0, 0);
				repaint();
			}
			
			private int lastPlayIdx = 0;
			private int lastPlayIdxX = -1;
			private void select(Point p, boolean create) {
				if (p.y < 0 || p.y >= getHeight() || p.x < 0 || p.x >= getWidth()) {
					return;
				}
				
				int noteX = (p.x / parent.getStepWidth()) * 24;
				int noteY = p.y / 14;
				int idx = (parent.getOctaveCount() + parent.getOctaveOffset()) * parent.getOctaveHeight() / 14 - noteY - 1;
				if (idx < parent.getLowestNote() || idx > parent.getHighestNote() || noteX < 0) {
					return;
				}
				
				synchronized (state) {
					Predicate<Note> filter = item -> item.note == idx && item.start < noteX + 1 && item.end > noteX;
					if (create) {
						Note a = new Note(idx, noteX, noteX + 24);
						
						if (!parent.isPlaying()) {
							if (lastPlayIdx != idx || lastPlayIdxX != noteX) {
								parent.sound.playNote(idx + 12, 50, 500);
								lastPlayIdx = idx;
								lastPlayIdxX = noteX;
							}
						}
						
						if (state.getNotes().stream().noneMatch(filter)) {
							state.addNote(a);
						}
					} else {
						state.removeNotes(state.getNotes().stream().filter(filter).toList());
					}
				}
				repaint();
			}
		};
		
		addMouseListener(adapter);
		addMouseMotionListener(adapter);
		addMouseWheelListener(adapter);
	}
	
	private void addMoveActions(EventMap eventMap) {
		AbstractAction action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int tx, ty;
				switch (e.getActionCommand()) {
					case PianoAction.RollMoveSelectionOctaveDown -> { tx = 0; ty = -12; }
					case PianoAction.RollMoveSelectionOctaveUp -> { tx = 0; ty = 12; }
					case PianoAction.RollMoveSelectionDown -> { tx = 0; ty = -1; }
					case PianoAction.RollMoveSelectionUp -> { tx = 0; ty = 1; }
					case PianoAction.RollMoveSelectionLeft -> { tx = -24; ty = 0; }
					case PianoAction.RollMoveSelectionRight -> { tx = 24; ty = 0; }
					default -> { return; }
				}
				
				synchronized (state) {
					if (isNotesInside(selectedNotes, tx, ty)) {
						state.moveNotes(selectedNotes, tx, ty);
						repaint();
					}
				}
			}
		};
		
		eventMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.CTRL_DOWN_MASK), PianoAction.RollMoveSelectionOctaveUp, action);
		eventMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.CTRL_DOWN_MASK), PianoAction.RollMoveSelectionOctaveDown, action);
		eventMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.SHIFT_DOWN_MASK), PianoAction.RollMoveSelectionUp, action);
		eventMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.SHIFT_DOWN_MASK), PianoAction.RollMoveSelectionDown, action);
		eventMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.SHIFT_DOWN_MASK), PianoAction.RollMoveSelectionLeft, action);
		eventMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.SHIFT_DOWN_MASK), PianoAction.RollMoveSelectionRight, action);
	}
	
	private boolean isNotesInside(Collection<Note> notes, int tx, int ty) {
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;
		
		for (Note note : notes) {
			minX = Math.min(minX, note.start);
			maxX = Math.max(maxX, note.end);
			
			minY = Math.min(minY, note.note);
			maxY = Math.max(maxY, note.note);
		}
		
		if (minX + tx < 0) {
			return false;
		}
		
		if (maxY + ty >= (parent.getOctaveOffset() + parent.getOctaveCount()) * 12) {
			return false;
		}
		
		if (minY + ty < (parent.getOctaveOffset()) * 12) {
			return false;
		}
		
		return true;
	}
	
	@Override
	protected void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		
		Graphics2D g = (Graphics2D) gr;
		
		int stepWidth = parent.getStepWidth();
		int octaveHeight = parent.getOctaveHeight();
		int octaveStart = parent.getOctaveCount();
		for (int i = 0; i < parent.getOctaveCount(); i++) {
			drawOctave(g, octaveStart - i - 1, i * octaveHeight, stepWidth);
		}
		
		// Paint notes
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		paintNotes(g, 0, stepWidth);
		
		// Paint gizmos
		paintGizmos(g);
	}
	
	private static final int[] FlatOffset = {0, 28, 56, 84, 98, 126, 154};
	private static final int[] SharpOffset = {28, 28, 42, 28, 0};
	private void drawOctave(Graphics2D g, int octave, int y, int stepWidth) {
		int index = octave * 12;
		
		int width = getWidth();
		int height = getHeight();
		
		g.setColor(PianoColors.RollFlatSelected);
		for (int i = 0; i < FlatOffset.length; i++) {
			int noteIdx = index + 11 - i * 2 + (i > 3 ? 1 : 0);
			if (parent.keys.isNoteHovered(noteIdx)) {
				g.fillRect(0, y + FlatOffset[i], width, 14);
			}
		}
		
		for (int type = 0; type < 2; type++) {
			boolean paintSelected = type == 0;
			g.setColor(paintSelected ? PianoColors.RollSharpSelected : PianoColors.RollSharp);
			
			for (int i = 0, yy = y + 14; i < 5; i++) {
				int noteIdx = index + 10 - i * 2 - (i > 2 ? 1 : 0);
				if (paintSelected == parent.keys.isNoteHovered(noteIdx)) {
					g.fillRect(0, yy, width, 14);
				}
				
				yy += SharpOffset[i];
			}
		}
		
		g.setColor(PianoColors.RollLineSoft);
		for (int i = 0; i < 12; i++) {
			int yy = y + (14 * i);
			g.drawLine(0, yy, width, yy);
		}
		
		for (int i = 0; i < width; i += stepWidth) {
			g.drawLine(i, 0, i, height);
		}
		
		int beatWidth = stepWidth * 4;
		g.setColor(PianoColors.RollLine);
		for (int i = 0; i < width; i += beatWidth) {
			g.drawLine(i, 0, i, height);
		}
		
		int barWidth = stepWidth * 16;
		g.setColor(PianoColors.RollLineDark);
		for (int i = 0; i < width; i += barWidth) {
			g.drawLine(i, 0, i, height);
		}
		
		g.setColor(PianoColors.RollDarker);
		for (int i = 4 * barWidth; i < width; i += 8 * barWidth) {
			g.fillRect(i, y, 4 * barWidth, 14 * 12);
		}
		
		if (timeTick >= 0) {
			int timeIndex = (timeTick * stepWidth / 24);
			for (int i = 0; i < PianoColors.RollTime.length; i++) {
				g.setColor(PianoColors.RollTime[i]);
				g.drawLine(timeIndex - i, 0, timeIndex - i, height);
			}
		}
	}
	
	protected void paintNotes(Graphics2D g, int y, int stepWidth) {
		synchronized (state) {
			for (var note : state.getNotes()) {
				int xx = (int) (note.start / 24.0 * stepWidth);
				int yy = y + 14 * ((parent.getOctaveCount() + parent.getOctaveOffset()) * 12 - note.note - 1);
				
				int height = 14;
				int width = (int) (((note.end - note.start) / 24.0) * stepWidth); // in steps
				
				if (selectRect.intersects(xx, yy, width, height) || selectedNotes.contains(note)) {
					g.setColor(PianoColors.RollNoteSelected);
				} else {
					g.setColor(PianoColors.RollNote);
				}
				g.fillRoundRect(xx, yy, width, height, 4, 4);
			}
		}
	}
	
	private void paintGizmos(Graphics2D g) {
		if (!selectRect.isEmpty()) {
			g.setColor(PianoColors.RollSelectBackground);
			g.fillRoundRect(selectRect.x, selectRect.y, selectRect.width, selectRect.height, 15, 15);
			
			var oldStroke = g.getStroke();
			g.setStroke(new BasicStroke(3));
			g.setColor(PianoColors.RollSelect);
			g.drawRoundRect(selectRect.x, selectRect.y, selectRect.width, selectRect.height, 15, 15);
			g.setStroke(oldStroke);
		}
	}
	
	public void setTimeTick(int tick) {
		timeTick = tick;
	}
	
	public int getTimeTick() {
		return timeTick;
	}
	
	public List<Note> getNotes() {
		List<Note> copy;
		synchronized (state) {
			copy = new ArrayList<>(state.getNotes());
		}
		
		return copy;
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(0, parent.getOctaveCount() * parent.getOctaveHeight());
	}
}
