package me.hardcoded.gui.window;

import java.awt.*;

public interface MusicLookup {
	interface Colors {
		Color ScoreText = new Color(0x6e93ac);
		Color DefaultText = new Color(0xa0afa0);
		
		Color SongBackground = new Color(0x273035); // 0x172025);
		Color ButtonBackground = new Color(0x4a5358);
		Color ButtonText = new Color(0xccd2d5);
	}
	
	interface Fonts {
		Font SerifFont = new Font("SansSerif Plain", Font.PLAIN, 11);
	}
}
