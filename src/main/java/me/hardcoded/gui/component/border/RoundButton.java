package me.hardcoded.gui.component.border;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RoundButton extends RoundPanel {
	private final JLabel label;
	private ActionListener actionListener;
	private Color normalBackground;
	
	public RoundButton(String text, int arc) {
		super();
		setLayout(new BorderLayout());
		normalBackground = getBackground();
		
		label = new JLabel();
		label.setText(text);
		label.setHorizontalTextPosition(SwingConstants.CENTER);
		label.setVerticalTextPosition(SwingConstants.CENTER);
		label.setVerticalAlignment(SwingConstants.CENTER);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		add(label, BorderLayout.CENTER);
		
		setArcHeight(arc);
		setArcWidth(arc);
		
		MouseAdapter mouseAdapter = new MouseAdapter() {
			public boolean isHovered;
			public boolean isClicked;
			
			@Override
			public void mousePressed(MouseEvent e) {
				updateColor(isClicked = true, isHovered, false);
				
				int modifiers = 0;
				var currentEvent = EventQueue.getCurrentEvent();
				if (currentEvent instanceof InputEvent) {
					modifiers = ((InputEvent)currentEvent).getModifiersEx();
				} else if (currentEvent instanceof ActionEvent) {
					modifiers = ((ActionEvent)currentEvent).getModifiers();
				}
				
				ActionListener listener = actionListener;
				if (listener != null) {
					listener.actionPerformed(
						new ActionEvent(
							this,
							ActionEvent.ACTION_PERFORMED,
							null,
							EventQueue.getMostRecentEventTime(),
							modifiers));
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				updateColor(isClicked = false, isHovered, false);
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				updateColor(isClicked, isHovered = true, false);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				updateColor(isClicked, isHovered = false, false);
			}
		};
		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);
	}
	
	private void updateColor(boolean clicked, boolean hovered, boolean selected) {
		if (clicked || selected) {
			super.setBackground(normalBackground);
		} else if (hovered) {
			super.setBackground(normalBackground.darker());
		} else {
			super.setBackground(normalBackground);
		}
	}
	
	public void setText(String text) {
		label.setText(text);
	}
	
	public void setActionListener(ActionListener listener) {
		actionListener = listener;
	}
	
	@Override
	public void setForeground(Color fg) {
		super.setForeground(fg);
		
		// This is called earlier than the label is instantiated
		if (label != null) {
			label.setForeground(fg);
		}
	}
	
	@Override
	public void setBackground(Color bg) {
		super.setBackground(bg);
		normalBackground = bg;
	}
}
