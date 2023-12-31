package me.hardcoded.data;

public class Note {
	public int note;   // 0 == C0, 12 == C1
	public int start;  // In ticks
	public int end;    // In ticks
	
	// 1 bar
	// 4 beat
	// 16 step
	// 384 ticks
	
	public Note(int note, int start, int end) {
		this.note = note;
		this.start = start;
		this.end = end;
	}
	
	private static final String[] NOTE_NAME = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
	
	public String getName() {
		return NOTE_NAME[note % 12] + "" + (note / 12);
	}
	
	@Override
	public String toString() {
		return "Note{name=" + getName() + " (" + note + "), start=" + start + ", end=" + end + "}";
	}
	
	/**
	 * Returns the note index from the note name
	 * 
	 * @param name the name of the note
	 */
	public static int getNoteFromName(String name) {
		// Find first non number from end
		int digitIndex = -1;
		for (int i = name.length() - 1; i >= 0; i--) {
			if (!Character.isDigit(name.charAt(i))) {
				digitIndex = i + 1;
				break;
			}
		}
		
		if (digitIndex < 0) {
			return -1;
		}
		
		try {
			int octave = Integer.parseInt(name.substring(digitIndex));
			String noteName = name.substring(0, digitIndex);
			
			for (int i = 0; i < NOTE_NAME.length; i++) {
				if (noteName.equals(NOTE_NAME[i])) {
					return octave * 12 + i;
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
		return -1;
	}
}
