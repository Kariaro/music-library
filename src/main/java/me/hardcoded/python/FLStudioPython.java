package me.hardcoded.python;

import me.hardcoded.data.Note;
import me.hardcoded.data.persistent.ApplicationData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FLStudioPython {
	private static final File parserFile;
	static {
		// Setup runner script directory
		ApplicationData.getInstance().ensureDirectory("venv", "Custom");
		parserFile = ApplicationData.getInstance().get("venv", "Custom", "flp-parser.py").toFile();
		
		// Setup runner script
		try (FileOutputStream fs = new FileOutputStream(parserFile)) {
			fs.write("""
			import pyflp, sys
			project = pyflp.parse(sys.argv[1])
			for pattern in project.patterns:
			    notes = []
			    for x in pattern.notes:
			    	print(x["key"], x.position, sep=',', end=';')
			    print()
			""".getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static List<List<FLPNote>> readProjectPatterns(File file) {
		String output = PythonRunner.runPath(parserFile, List.of("\"" + file + "\""));
		
		if (output != null) {
			return output.lines().map(FLStudioPython::transform).toList();
		}
		
		return null;
	}
	
	private static List<FLPNote> transform(String line) {
		if (line.isEmpty()) {
			return List.of();
		}
		
		List<FLPNote> result = new ArrayList<>();
		String[] array = line.split(";");
		try {
			for (String str : array) {
				if (str.isEmpty()) {
					continue;
				}
				
				String[] parts = str.split(",");
				
				int note = Integer.parseInt(parts[0]);
				int tick = Integer.parseInt(parts[1]);
				result.add(new FLPNote(tick, note));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(Arrays.toString(array));
		}
		
		return result;
	}
	
	public static void performSearch(List<Note> notes) {
		File test = ApplicationData.getInstance().get("test").toFile();
		File[] array = test.listFiles();
		if (array != null) {
			for (File file : array) {
				var patterns = readProjectPatterns(file);
				if (patterns == null) {
					continue;
				}
				
				/*
				if (file.getName().equals("untitled.flp")) {
					for (var item : patterns) {
						if (!item.isEmpty()) {
							computeScoreDebug(notes, item);
						}
					}
				}
				*/
				
				double score = Double.MAX_VALUE;
				for (var item : patterns) {
					if (!item.isEmpty()) {
						score = Math.min(score, computeScore(notes, item));
					}
				}
				
				System.out.printf("%s -> %d | score = %.4f\n", file.getName(), patterns.size(), score);
			}
		}
	}
	
	private static double computeDistanceSquared(FLPNote a, List<FLPNote> b) {
		double lowest = Double.MAX_VALUE;
		for (var note : b) {
			double noteDistance = Math.abs(a.note - note.note) * 10;
			double beatDistance = Math.abs(a.tick - note.tick) * 10;
			
			lowest = Math.min(lowest, (noteDistance * noteDistance) + (beatDistance * beatDistance));
		}
		
		return lowest;
	}
	
	private static double computeScore(List<Note> a, List<FLPNote> b) {
		if (b.isEmpty() || a.isEmpty()) {
			return Double.MAX_VALUE;
		}
		
		List<FLPNote> t = new ArrayList<>();
		for (var item : a) {
			t.add(new FLPNote(item.start, item.note));
		}
		
		double lowestScore = Double.MAX_VALUE;
		for (int i = 0; i < b.size(); i++) {
			FLPNote bPivot = b.get(i);
			
			// Pivot point
			for (int j = 0; j < t.size(); j++) {
				// Transform from point
				FLPNote tPivot = t.get(j);
				
				int tickOffset = bPivot.tick - tPivot.tick;
				int noteOffset = bPivot.note - tPivot.note;
				
				double score = 0;
				for (int k = 0; k < t.size(); k++) {
					FLPNote child = t.get(k);
					child.tick += tickOffset;
					child.note += noteOffset;
					score += computeDistanceSquared(child, b);
				}
				
				lowestScore = Math.min(score, lowestScore);
			}
		}
		
		return lowestScore;
	}
	
	public static class FLPNote {
		int tick;
		int note;
		
		FLPNote(int tick, int note) {
			this.tick = tick;
			this.note = note;
		}
		
		@Override
		public String toString() {
			return "FLPNote{tick=" + tick + ", note=" + note + "}";
		}
	}
}
