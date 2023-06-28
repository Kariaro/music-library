package me.hardcoded.gui.component.border;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ScrollBarUI;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;

public class SmallScrollbar extends JScrollBar {
	public SmallScrollbar() {
		setOpaque(true);
		setBackground(new Color(0x1e2931));
		setBorder(new EmptyBorder(0, 0, 0, 0));
		
		setUI(new BasicScrollBarUI() {
			private JButton createArrowButton(int orientation) {
				return new BasicArrowButton(orientation, null, null, null, null) {
					@Override
					public Dimension getPreferredSize() {
						return new Dimension(0, 0);
					}
					
					@Override
					public Dimension getMinimumSize() {
						return new Dimension(0, 0);
					}
				};
			}
			
			@Override
			protected JButton createIncreaseButton(int orientation) {
				return createArrowButton(orientation);
			}
			
			@Override
			protected JButton createDecreaseButton(int orientation) {
				return createArrowButton(orientation);
			}
			
			@Override
			protected void configureScrollBarColors() {
				super.configureScrollBarColors();
				this.trackColor = new Color(0x1e2931);
				this.thumbColor = new Color(0x50595e);
				
				// Border
				this.thumbHighlightColor = null;
				this.thumbLightShadowColor = null;
				
				// Drop shadow
				this.thumbDarkShadowColor = this.thumbColor;
			}
			
			@Override
			protected void installDefaults() {
				super.installDefaults();
				this.incrGap = 0;
				this.decrGap = 0;
			}
			
			@Override
			protected void layoutVScrollbar(JScrollBar sb) {
				super.layoutVScrollbar(sb);
				trackRect.setBounds(0, 0, sb.getWidth(), sb.getHeight());
			}
		});
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(10, 0);
	}
}
