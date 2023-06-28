package me.hardcoded.gui.component.piano;

import me.hardcoded.gui.util.DrawUtility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Piano layout component
 */
public class PianoKeys extends JPanel {
	private final PianoComponent parent;
	private final Set<Integer> notes;
	
	public PianoKeys(PianoComponent parent) {
		this.parent = parent;
		this.notes = new HashSet<>();
		
		setFocusable(true);
		MouseAdapter adapter = new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int idx = getNoteIndex(e.getPoint(), 0);
				synchronized (notes) {
					notes.clear();
					notes.add(idx);
				}
				
				repaint();
				parent.repaint();
			}
			
			public int prevIdx = -1;
			@Override
			public void mousePressed(MouseEvent e) {
				int idx = getNoteIndex(e.getPoint(), 0);
				parent.sound.playNote(idx + (parent.getOctaveOffset() + 1) * 12, 80, 500);
				
				prevIdx = idx;
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				if (e.getX() < 0) {
					return;
				}
				
				int idx = getNoteIndex(e.getPoint(), 0);
				synchronized (notes) {
					notes.clear();
					notes.add(idx);
				}
				
				if (prevIdx != idx) {
					parent.sound.playNote(idx + (parent.getOctaveOffset() + 1) * 12, 80, 500);
					prevIdx = idx;
				}
				
				repaint();
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				synchronized (notes) {
					notes.clear();
				}
				repaint();
				parent.roll.repaint();
			}
		};
		
		addMouseMotionListener(adapter);
		addMouseListener(adapter);
	}
	
	@Override
	protected void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		
		Graphics2D g = (Graphics2D) gr;
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		
		int octaveCount = parent.getOctaveCount();
		int octaveHeight = parent.getOctaveHeight();
		for (int i = 0; i < octaveCount; i++) {
			drawOctave(g, octaveCount - i - 1, i * octaveHeight, 1.0f);
		}
	}
	
	private static final int[] Heights = {21, 28, 28, 21, 21, 28, 21};
	private static final int[] SharpOffset = {28, 28, 42, 28, 0};
	private void drawOctave(Graphics2D g, int octave, int y, float scale) { // 70 x 98
		var oldPaint = g.getPaint();
		
		int index = octave * 12;
		
		for (int type = 0; type < 2; type++) {
			boolean paintSelected = type == 0;
			g.setPaint(paintSelected ? PianoColors.FlatNoteSelectedPaint : PianoColors.FlatNotePaint);
			
			for (int i = 0, yy = y; i < Heights.length; i++) {
				if (!paintSelected && i == Heights.length - 1) {
					g.setPaint(PianoColors.FlatNoteLabelPaint);
				}
				
				int height = Heights[i];
				int noteIdx = index + 11 - i * 2 + (i > 3 ? 1 : 0);
				if (paintSelected == isNoteHovered(noteIdx)) {
					g.fillRect(0, yy, PianoColors.FlatNoteWidth, height);
				}
				
				yy += height;
			}
		}
		
		for (int type = 0; type < 2; type++) {
			boolean paintSelected = type == 0;
			g.setPaint(paintSelected ? PianoColors.SharpNoteSelectedPaint : PianoColors.SharpNotePaint);
			
			for (int i = 0, yy = y + 14; i < 5; i++) {
				int height = 14;
				int noteIdx = index + 10 - i * 2 - (i > 2 ? 1 : 0);
				if (paintSelected == isNoteHovered(noteIdx)) {
					g.fillRect(0, yy, PianoColors.SharpNoteWidth, height);
				}
				
				yy += SharpOffset[i];
			}
		}
		
		g.setPaint(oldPaint);
		
		g.setColor(PianoColors.SectionBorder);
		g.drawRect(-1, y, PianoColors.FlatNoteWidth, 98 - 1);
		g.drawRect(-1, y + 98 - 1, PianoColors.FlatNoteWidth, 70 + 1);
		
		// Draw text
		g.setColor(Color.black);
		int octaveHeight = parent.getOctaveHeight();
		
		Rectangle rect = new Rectangle(0, y + octaveHeight - 21, PianoColors.FlatNoteWidth, 21);
		DrawUtility.drawTextAligned(g, "C" + (octave + parent.getOctaveOffset()), rect, DrawUtility.ALIGN_CENTER_RIGHT);
	}
	
	public int getNoteIndex(Point mouse, int scroll) {
		// 21, 28, 28, 21, 21, 28, 21
		if (mouse.x < 0 || mouse.x > PianoColors.FlatNoteWidth) {
			return -1;
		}
		
		int octaveHeight = parent.getOctaveHeight();
		int octaveCount = parent.getOctaveCount();
		int octaveIndex = (mouse.y + scroll) / octaveHeight;
		
		int mouseY = (((mouse.y + scroll) % octaveHeight) + octaveHeight) % octaveHeight;
		if (mouse.x <= PianoColors.SharpNoteWidth) {
			for (int i = 0, yy = 14; i < 5; i++) {
				int height = 14;
				int noteIdx = 10 - i * 2 - (i > 2 ? 1 : 0);
				if (mouseY >= yy && mouseY < yy + height) {
					return noteIdx + (octaveCount - octaveIndex - 1) * 12;
				}
				
				yy += SharpOffset[i];
			}
		}
		
		for (int i = 0, yy = 0; i < Heights.length; i++) {
			int height = Heights[i];
			int noteIdx = 11 - i * 2 + (i > 3 ? 1 : 0);
			if (mouseY >= yy && mouseY < yy + height) {
				return noteIdx + (octaveCount - octaveIndex - 1) * 12;
			}
			
			yy += height;
		}
		
		return -1;
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(70, parent.getOctaveCount() * parent.getOctaveHeight());
	}
	
	public boolean isNoteHovered(int index) {
		return notes.contains(index);
	}
}
