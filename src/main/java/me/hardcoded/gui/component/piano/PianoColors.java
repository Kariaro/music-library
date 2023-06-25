package me.hardcoded.gui.component.piano;

import java.awt.*;

public class PianoColors {
	
	// Sharp note
	public static final int SharpNoteWidth = 44;
	public static final Color SharpNoteStart = new Color(0x292a2c);
	public static final Color SharpNoteEnd = new Color(0x4e4f51);
	public static final GradientPaint SharpNotePaint =
		new GradientPaint(0, 0, SharpNoteStart, SharpNoteWidth, 0, SharpNoteEnd);
	
	public static final Color SharpNoteSelectedStart = new Color(0xd95b00);
	public static final Color SharpNoteSelectedEnd = new Color(0xff8126);
	public static final GradientPaint SharpNoteSelectedPaint =
		new GradientPaint(0, 0, SharpNoteSelectedStart, SharpNoteWidth, 0, SharpNoteSelectedEnd);
	
	// Flat note
	public static final int FlatNoteWidth = 70;
	public static final Color FlatNoteStart = new Color(0xbcc0c7);
	public static final Color FlatNoteEnd = new Color(0xeaeef5);
	public static final GradientPaint FlatNotePaint =
		new GradientPaint(0, 0, FlatNoteStart, FlatNoteWidth, 0, FlatNoteEnd);
	
	public static final Color FlatNoteSelectedStart = new Color(0xdda266);
	public static final Color FlatNoteSelectedEnd = new Color(0xffce92);
	public static final GradientPaint FlatNoteSelectedPaint =
		new GradientPaint(0, 0, FlatNoteSelectedStart, FlatNoteWidth, 0, FlatNoteSelectedEnd);
	
	public static final Color FlatNoteLabelStart = new Color(0x9b9fa6);
	public static final Color FlatNoteLabelEnd = new Color(0xcaced5);
	public static final GradientPaint FlatNoteLabelPaint =
		new GradientPaint(0, 0, FlatNoteLabelStart, FlatNoteWidth, 0, FlatNoteLabelEnd);
	
	// Borders
	public static final Color SectionBorder = new Color(0xa2a6ac);
	
	
	// Roll
	public static final Color RollSharp = new Color(0x2e3e48);
	public static final Color RollSharpSelected = new Color(0x32424c);
	public static final Color RollFlat = new Color(0x34444e);
	public static final Color RollFlatSelected = new Color(0x384852);
	public static final Color RollLine = new Color(0x1c2c36);
	public static final Color RollLineSoft = new Color(0x2a3a44);
	public static final Color RollDarker = new Color(0x33_000000, true);
	public static final Color[] RollTime = {
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
	public static final Color RollSelectBackground = new Color(0x33_ffb870, true);
	public static final Color RollSelect = new Color(0xffb870);
	
	public static final Color RollNoteSelected = new Color(0xfa9f98);
	public static final Color RollNote = new Color(0xadddb6);
	
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
	public static final Color TimelineBackground = new Color(0x1e2931);
	
}
