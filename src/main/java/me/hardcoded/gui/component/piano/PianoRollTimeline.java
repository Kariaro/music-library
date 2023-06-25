package me.hardcoded.gui.component.piano;

import me.hardcoded.gui.util.DrawUtility;
import me.hardcoded.sound.PianoSound;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PianoRollTimeline extends JPanel {
	private final PianoComponent parent;
	private final PianoRoll roll;
	private double timeBeat;
	private double timeStart;
	
	public PianoRollTimeline(PianoComponent parent, PianoRoll roll) {
		this.parent = parent;
		this.roll = roll;
		
		MouseAdapter adapter = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				moveTime(e.getPoint());
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				moveTime(e.getPoint());
			}
			
			public void moveTime(Point p) {
				int rollIndex = p.x - parent.section.getWidth();
				
				int beatWidth = parent.getStepWidth() * 4;
				double beatOffset = (rollIndex + beatWidth / 8.0) / (double) beatWidth;
				
				// Snap to steps
				timeStart = Math.max(0, beatOffset);
				timeStart = (int) (timeStart * 4) / 4.0;
				repaint();
			}
		};
		addMouseListener(adapter);
		addMouseMotionListener(adapter);
		
		setBackground(PianoColors.TimelineBackground);
	}
	
	public double getTimeStart() {
		return timeStart;
	}
	
	public void setTimeBeat(double beat) {
		timeBeat = beat;
	}
	
	public double getTimeBeat() {
		return timeBeat;
	}
	
	@Override
	protected void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		
		Graphics2D g = (Graphics2D) gr;
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.setColor(Color.darkGray);
		g.fillRect(0, 0, parent.section.getWidth(), getHeight());
		
		// Draw digits
		int width = getWidth();
		int beatWidth = parent.getStepWidth() * 4;
		
		g.setColor(Color.lightGray);
		int beatIndex = 1;
		
		Rectangle rect = new Rectangle(parent.section.getWidth(), 0, beatWidth, getHeight());
		for (int i = 0; i < width; i += beatWidth) {
			DrawUtility.drawTextAligned(g, Integer.toString(beatIndex), rect, DrawUtility.ALIGN_CENTER_LEFT);
			rect.x += beatWidth;
			beatIndex += 1;
		}
		
		int bx = (int) (beatWidth * timeStart) + parent.section.getWidth();
		int[][] polygon = {
			{ bx - 7, bx - 7, bx, bx + 7, bx + 7},
			{ 4, 10, 15, 10, 4 }
		};
		
		g.setColor(Color.yellow);
		g.drawPolygon(polygon[0], polygon[1], polygon[0].length);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(0, 21);
	}
}
