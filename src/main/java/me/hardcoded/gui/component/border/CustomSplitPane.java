package me.hardcoded.gui.component.border;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class CustomSplitPane extends JSplitPane {
	public CustomSplitPane() {
		super(JSplitPane.HORIZONTAL_SPLIT);
		setContinuousLayout(true);
		
		setUI(new BasicSplitPaneUI() {
			@Override
			public BasicSplitPaneDivider createDefaultDivider() {
				// TODO: Implement custom colors
				return super.createDefaultDivider();
			}
		});
	}
}
