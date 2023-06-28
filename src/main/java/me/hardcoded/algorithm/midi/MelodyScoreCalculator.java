package me.hardcoded.algorithm.midi;

import me.hardcoded.data.Note;

import java.util.ArrayList;
import java.util.List;

public class MelodyScoreCalculator {
	private MelodyScoreCalculator() {
		
	}
	
	private static double computeDistanceSquared(Note a, List<Note> b) {
		double lowest = Double.MAX_VALUE;
		for (var note : b) {
			double noteDistance = Math.abs(a.note - note.note) * 10; // TODO: Modulo 12 on all notes... And check modulo distance
			double beatDistance = Math.abs(a.start - note.start) / 24.0;
			
			lowest = Math.min(lowest, (noteDistance * noteDistance) + (beatDistance * beatDistance));
		}
		
		return lowest;
	}
	
	/**
	 * Calculates the score of how close the set {@code a} matches set {@code b}
	 * 
	 * @param a the pattern to find
	 * @param b the melody
	 */
	public static MelodyScore computeScore(List<Note> a, List<Note> b) {
		if (b.isEmpty() || a.isEmpty()) {
			return new MelodyScore(0, false);
		}
		
		List<Note> t = new ArrayList<>();
		for (var item : a) {
			t.add(new Note(item.note, item.start, item.start));
		}
		
		double lowestScore = Double.MAX_VALUE;
		for (int i = 0; i < b.size(); i++) {
			Note bPivot = b.get(i);
			
			// Pivot point
			for (int j = 0; j < t.size(); j++) {
				// Transform from point
				Note tPivot = t.get(j);
				
				int tickOffset = bPivot.start - tPivot.start;
				int noteOffset = bPivot.note - tPivot.note;
				
				double score = 0;
				for (int k = 0; k < t.size(); k++) {
					Note child = t.get(k);
					child.start += tickOffset;
					child.note += noteOffset;
					score += computeDistanceSquared(child, b);
				}
				
				lowestScore = Math.min(score, lowestScore);
			}
		}
		
		return new MelodyScore(lowestScore, true);
	}
}
