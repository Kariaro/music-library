package me.hardcoded.gui.component.song;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.Map;

public interface MusicLookup {
	interface Colors {
		Color ScoreText = new Color(0x6e93ac);
		Color DefaultText = new Color(0xa0afa0);
		
		Color SongBackground = new Color(0x273035); // 0x172025);
		Color SongBackgroundBright = new Color(0x2d3b43);
		Color ButtonBackground = new Color(0x4a5358);
		Color ButtonText = new Color(0xccd2d5);
		Color SongListBackground = new Color(0x1e2931);
	}
	
	interface Fonts {
		Font Serif = new Font("SansSerif Plain", Font.PLAIN, 11);
		Font SerifUnderline = Serif.deriveFont(Map.of(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON));
	}
}
