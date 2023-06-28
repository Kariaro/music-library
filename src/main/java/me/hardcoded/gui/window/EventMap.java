package me.hardcoded.gui.window;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.security.PublicKey;

public class EventMap {
	private final InputMap inputMap;
	private final ActionMap actionMap;
	
	public EventMap(JComponent root) {
		this.inputMap = root.getInputMap();
		this.actionMap = root.getActionMap();
	}
	
	public void put(KeyStroke keyStroke, String name, Action action) {
		inputMap.put(keyStroke, name);
		actionMap.put(name, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				action.actionPerformed(new ActionEvent(e.getSource(), e.getID(), name, e.getWhen(), e.getModifiers()));
			}
		});
	}
}
