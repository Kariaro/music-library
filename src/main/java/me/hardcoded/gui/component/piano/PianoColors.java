package me.hardcoded.gui.component.piano;

import java.awt.*;

interface PianoColors {
	// Sharp note
	int SharpNoteWidth = 44;
	Color SharpNoteStart = new Color(0x292a2c);
	Color SharpNoteEnd = new Color(0x4e4f51);
	GradientPaint SharpNotePaint =
		new GradientPaint(0, 0, SharpNoteStart, SharpNoteWidth, 0, SharpNoteEnd);
	
	Color SharpNoteSelectedStart = new Color(0xd95b00);
	Color SharpNoteSelectedEnd = new Color(0xff8126);
	GradientPaint SharpNoteSelectedPaint =
		new GradientPaint(0, 0, SharpNoteSelectedStart, SharpNoteWidth, 0, SharpNoteSelectedEnd);
	
	// Flat note
	int FlatNoteWidth = 70;
	Color FlatNoteStart = new Color(0xbcc0c7);
	Color FlatNoteEnd = new Color(0xeaeef5);
	GradientPaint FlatNotePaint =
		new GradientPaint(0, 0, FlatNoteStart, FlatNoteWidth, 0, FlatNoteEnd);
	
	Color FlatNoteSelectedStart = new Color(0xdda266);
	Color FlatNoteSelectedEnd = new Color(0xffce92);
	GradientPaint FlatNoteSelectedPaint =
		new GradientPaint(0, 0, FlatNoteSelectedStart, FlatNoteWidth, 0, FlatNoteSelectedEnd);
	
	Color FlatNoteLabelStart = new Color(0x9b9fa6);
	Color FlatNoteLabelEnd = new Color(0xcaced5);
	GradientPaint FlatNoteLabelPaint =
		new GradientPaint(0, 0, FlatNoteLabelStart, FlatNoteWidth, 0, FlatNoteLabelEnd);
	
	// Borders
	Color SectionBorder = new Color(0xa2a6ac);
	
	
	// Roll
	Color RollSharp = new Color(0x2e3e48);
	Color RollSharpSelected = new Color(0x32424c);
	Color RollFlat = new Color(0x34444e);
	Color RollFlatSelected = new Color(0x384852);
	Color RollLine = new Color(0x1c2c36);
	Color RollLineDark = new Color(0x10202a);
	Color RollLineSoft = new Color(0x2a3a44);
	Color RollDarker = new Color(0x33_000000, true);
	Color[] RollTime = {
		new Color(0xff_b1e758, true),
		new Color(0x1e_66b62c, true),
		new Color(0x1a_66b62c, true),
		new Color(0x16_66b62c, true),
		new Color(0x13_66b62c, true),
		new Color(0x10_66b62c, true),
		new Color(0x0d_66b62c, true),
		new Color(0x0b_66b62c, true),
		new Color(0x08_66b62c, true),
		new Color(0x07_66b62c, true),
		new Color(0x05_66b62c, true),
		new Color(0x04_66b62c, true),
		new Color(0x03_66b62c, true),
		new Color(0x02_66b62c, true),
		new Color(0x01_66b62c, true),
		new Color(0x01_66b62c, true),
		new Color(0x01_66b62c, true),
	};
	Color RollSelectBackground = new Color(0x33_ffb870, true);
	Color RollSelect = new Color(0xffb870);
	
	Color RollNoteSelected = new Color(0xfa9f98);
	Color RollNote = new Color(0xadddb6);
	
	/*
	for (int i = 0; i < 16; i++) {
		int a = PianoColors.RollTime[0].getRGB();
		Color color = new Color(0x66b62c | (int) Math.max(0, 30 - i * 4 + (i * i / 7.1)) << 24, true);
		g.setColor(new Color(0x66b62c | (int) Math.max(0, 30 - i * 4 + (i * i / 7.1)) << 24, true));
		
		System.out.printf("%2d, new Color(0x%02x_%06x, true),\n", i, color.getAlpha(), color.getRGB() & 0xffffff);
		g.drawLine(timeIndex - i - 1, 0, timeIndex - i - 1, height);
	}
	*/
	
	// Roll Timeline
	Color TimelineBackground = new Color(0x1e2931);
	
}
