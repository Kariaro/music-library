package me.hardcoded.gui.util;

import java.awt.*;

public class DrawUtility {
	private DrawUtility() {
		
	}
	
	// Left = 0
	// Center = 1
	// Right = 2
	private static final int ALIGN_TYPE_LEFT = 0,
							 ALIGN_TYPE_TOP = 0,
							 ALIGN_TYPE_CENTER = 1,
							 ALIGN_TYPE_RIGHT = 2,
							 ALIGN_TYPE_BOTTOM = 2;
	
	public static final int ALIGN_CENTER = 3 + 1,
							ALIGN_CENTER_LEFT = 3,
							ALIGN_CENTER_RIGHT = 3 + 2;
	
	public static void drawTextAligned(Graphics2D g, String text, Rectangle rect, int align) {
		var fm = g.getFontMetrics();
		var bounds = fm.getStringBounds(text, g);
		
		int xOffset;
		int alignHorizontal = align % 3;
		switch (alignHorizontal) { // Horizontal Alignment
			case ALIGN_TYPE_LEFT -> {
				xOffset = 0;
			}
			case ALIGN_TYPE_CENTER -> {
				xOffset = (int) ((rect.width - bounds.getWidth()) / 2.0);
			}
			case ALIGN_TYPE_RIGHT -> {
				int x = (int) (Math.round(bounds.getX()));
				xOffset = x + (int) (rect.width - bounds.getWidth());
			}
			default -> throw new UnsupportedOperationException();
		}
		
		int yOffset;
		int alignVertical = align / 3;
		switch (alignVertical) { // Vertical Alignment
			case ALIGN_TYPE_TOP -> {
				yOffset = (int) (-bounds.getY());
			}
			case ALIGN_TYPE_CENTER -> {
				yOffset = (int) ((rect.height - bounds.getHeight()) / 2.0 - bounds.getY());
			}
			case ALIGN_TYPE_BOTTOM -> {
				yOffset = (int) (rect.height - bounds.getHeight() - bounds.getY());
			}
			default -> throw new UnsupportedOperationException();
		}
		
		g.drawString(text, rect.x + xOffset, rect.y + yOffset);
	}
}
