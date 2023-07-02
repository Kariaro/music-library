package me.hardcoded.gui.component.piano;

import me.hardcoded.gui.util.DrawUtility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
			public int prevIdx = -1;
			
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
			
			@Override
			public void mousePressed(MouseEvent e) {
				int idx = getNoteIndex(e.getPoint(), 0);
				parent.sound.playNote(idx + 12, 80, 500);
				
				prevIdx = idx;
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				if (e.getX() < 0 || e.getX() >= PianoColors.FlatNoteWidth) {
					return;
				}
				
				int idx = getNoteIndex(e.getPoint(), 0);
				synchronized (notes) {
					notes.clear();
					notes.add(idx);
				}
				
				if (prevIdx != idx) {
					parent.sound.playNote(idx + 12, 80, 500);
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
		
		int stepHeight = parent.getStepHeight();
		int octaveCount = parent.getOctaveCount();
		int octaveHeight = parent.getOctaveHeight();
		for (int i = 0; i < octaveCount; i++) {
			drawOctave(g, octaveCount + parent.getOctaveOffset() - i - 1, i * octaveHeight, stepHeight);
		}
	}  
	
	private static final float[] Heights = {0, 1.5f, 3.5f, 5.5f, 7, 8.5f, 10.5f, 12};
	private static final int[] SharpOffset = {2, 2, 3, 2, 0};
	private void drawOctave(Graphics2D g, int octave, int y, int stepHeight) {
		var oldPaint = g.getPaint();
		
		int index = octave * 12;
		
		for (int type = 0; type < 2; type++) {
			boolean paintSelected = type == 0;
			g.setPaint(paintSelected ? PianoColors.FlatNoteSelectedPaint : PianoColors.FlatNotePaint);
			
			int lastY = 0;
			for (int i = 0; i < Heights.length - 1; i++) {
				if (!paintSelected && i == Heights.length - 2) {
					g.setPaint(PianoColors.FlatNoteLabelPaint);
				}
				
				float nextY = Heights[i + 1] * stepHeight;
				int noteIdx = index + 11 - i * 2 + (i > 3 ? 1 : 0);
				if (paintSelected == isHighlighted(noteIdx)) {
					g.fillRect(0, y + lastY, PianoColors.FlatNoteWidth, Math.round(nextY - lastY));
				}
				
				lastY += Math.round(nextY - lastY);
			}
		}
		
		for (int type = 0; type < 2; type++) {
			boolean paintSelected = type == 0;
			g.setPaint(paintSelected ? PianoColors.SharpNoteSelectedPaint : PianoColors.SharpNotePaint);
			
			for (int i = 0, yy = y + stepHeight; i < 5; i++) {
				int noteIdx = index + 10 - i * 2 - (i > 2 ? 1 : 0);
				if (paintSelected == isHighlighted(noteIdx)) {
					g.fillRect(0, yy, PianoColors.SharpNoteWidth, stepHeight);
				}
				
				yy += SharpOffset[i] * stepHeight;
			}
		}
		
		g.setPaint(oldPaint);
		
		g.setColor(PianoColors.SectionBorder);
		g.drawRect(-1, y, PianoColors.FlatNoteWidth, 7 * stepHeight - 1);
		g.drawRect(-1, y + 7 * stepHeight - 1, PianoColors.FlatNoteWidth, 5 * stepHeight + 1);
		
		// Draw text
		g.setColor(Color.black);
		int octaveHeight = parent.getOctaveHeight();
		
		int rectHeight = (int) ((21 / 14.0) * stepHeight);
		Rectangle rect = new Rectangle(0, y + octaveHeight - rectHeight, PianoColors.FlatNoteWidth, rectHeight);
		DrawUtility.drawTextAligned(g, "C" + (octave + parent.getOctaveOffset()), rect, DrawUtility.ALIGN_CENTER_RIGHT);
	}
	
	public int getNoteIndex(Point mouse, int scroll) {
		if (mouse.x < 0 || mouse.x > PianoColors.FlatNoteWidth) {
			return -1;
		}
		
		int stepHeight = parent.getStepHeight();
		int octaveHeight = parent.getOctaveHeight();
		int octaveCount = parent.getOctaveCount() + parent.getOctaveOffset();
		int octaveIndex = (mouse.y + scroll) / octaveHeight;
		
		int mouseY = (((mouse.y + scroll) % octaveHeight) + octaveHeight) % octaveHeight;
		if (mouse.x <= PianoColors.SharpNoteWidth) {
			for (int i = 0, yy = stepHeight; i < 5; i++) {
				int noteIdx = 10 - i * 2 - (i > 2 ? 1 : 0);
				if (mouseY >= yy && mouseY < yy + stepHeight) {
					return noteIdx + (octaveCount - octaveIndex - 1) * 12;
				}
				
				yy += SharpOffset[i] * stepHeight;
			}
		}
		
		
		int lastY = 0;
		for (int i = 0; i < Heights.length - 1; i++) {
			float nextY = Heights[i + 1] * stepHeight;
			int noteIdx = 11 - i * 2 + (i > 3 ? 1 : 0);
			if (mouseY >= lastY && mouseY <= Math.ceil(nextY)) {
				return noteIdx + (octaveCount - octaveIndex - 1) * 12;
			}
			
			lastY += Math.round(nextY - lastY);
		}
		
		return -1;
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(70, parent.getOctaveCount() * parent.getOctaveHeight());
	}
	
	public boolean isHighlighted(int index) {
		if (notes.contains(index)) {
			return true;
		}
		
		synchronized (parent.playNotes) {
			return parent.playNotes.contains(index);
		}
	}
}
