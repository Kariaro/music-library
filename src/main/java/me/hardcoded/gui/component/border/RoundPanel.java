package me.hardcoded.gui.component.border;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Objects;

public class RoundPanel extends JPanel {
	private int arcWidth = 0;
	private int arcHeight = 0;
	private float borderWidth;
	private Stroke borderStroke;
	private Color borderColor = Color.black;
	
	public RoundPanel() {
		super();
		super.setOpaque(false);
		super.setBorder(null);
		this.setBorderWidth(0);
	}
	
	@Override
	protected void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		
		Graphics2D g = (Graphics2D) gr;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		int borderHalf = (int) Math.floor(this.borderWidth / 2.0f);
		int borderFull = (int) Math.floor(this.borderWidth - 1);
		int width = getWidth();
		int height = getHeight();
		g.setColor(getBackground());
		g.fillRoundRect(borderHalf, borderHalf, width - borderHalf, height - borderHalf, arcWidth, arcHeight);
		
		var currentStroke = borderStroke;
		if (currentStroke != null) {
			var previousStroke = g.getStroke();
			g.setStroke(borderStroke);
			g.setColor(borderColor);
			g.drawRoundRect(borderHalf, borderHalf, width - borderFull - 1, height - borderFull - 1, arcWidth, arcHeight);
			g.setStroke(previousStroke);
		}
	}
	
	public void setArcWidth(int arcWidth) {
		this.arcWidth = arcWidth;
	}
	
	public void setArcHeight(int arcHeight) {
		this.arcHeight = arcHeight;
	}
	
	public void setBorderWidth(float borderWidth) {
		this.borderWidth = borderWidth;
		this.borderStroke = (borderWidth == 0) ? null : new BasicStroke(borderWidth);
	}
	
	public void setBorderColor(Color color) {
		this.borderColor = Objects.requireNonNull(color, "Border color cannot be null");
	}
	
	@Override
	@Deprecated
	public void setBorder(Border border) {
		// Do nothing
	}
}
