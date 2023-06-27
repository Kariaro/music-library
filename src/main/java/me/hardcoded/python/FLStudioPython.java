package me.hardcoded.python;

import me.hardcoded.data.Note;
import me.hardcoded.data.persistent.ApplicationData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.temporal.ValueRange;
import java.util.*;

public class FLStudioPython {
	private static final File parserFile;
	private static final PythonRunnerPersistent persistent;
	static {
		// Setup runner script directory
		ApplicationData.getInstance().ensureDirectory("venv", "Custom");
		parserFile = ApplicationData.getInstance().get("venv", "Custom", "flp-parser.py").toFile();
		
		// Setup runner script
		try (FileOutputStream fs = new FileOutputStream(parserFile)) {
			fs.write("""
			import pyflp, sys, time
			for line in sys.stdin:
			    start_time = time.time()
			    try:
			        project = pyflp.parse(line.strip())
			    except:
			        print('ERROR')
			        continue
			    
			    data_string = ''
			    for pattern in project.patterns:
			        note_data = []
			        for x in pattern.notes:
			            note_data.append('%d,%d' % (x["key"], x.position))
			        data_string += ';'.join(note_data) + '|'
			    
			    total_time = time.time() - start_time
			    print(
			        total_time,
			        project.tempo,
			        data_string,
			        sep='|',
			        end='\\n',
			        flush=True
			    )
			""".getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		persistent = new PythonRunnerPersistent();
		persistent.start(parserFile, List.of());
	}
	
	public static FLPProject readProjectPatterns(File file) {
		// String output = PythonRunner.runPath(parserFile, List.of("\"" + file + "\""));
		String output = persistent.sendInput(file.toString().getBytes(StandardCharsets.UTF_8));
		
		if (output != null) {
			String[] parts = output.strip().split("\\|");
			long nanos = (long) (Double.parseDouble(parts[0]) * 1000000000L);
			double tempo = Double.parseDouble(parts[1]);
			
			return new FLPProject(
				nanos,
				file.toString(),
				tempo,
				Arrays.stream(parts, 2, parts.length).map(FLStudioPython::transform).toList()
			);
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
		List<Map.Entry<Double, FLPProject>> list = new ArrayList<>();
		
		try {
			Files.walk(test.toPath()).forEach(path -> {
				File file = path.toFile();
				if (file.isFile() && file.getName().endsWith(".flp")) {
					var project = readProjectPatterns(file);
					if (project == null) {
						return;
					}
					
					// System.out.println(project);
					
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
					for (var item : project.patterns()) {
						if (!item.isEmpty()) {
							score = Math.min(score, computeScore(notes, item));
						}
					}
					
					list.add(Map.entry(score, project));
					System.out.printf("%s -> %d | score = %.4f\n", file.getName(), project.patterns().size(), score);
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		list.sort(Map.Entry.comparingByKey());
		
		for (var entry : list) {
			System.out.printf("%8.8f score: '%s'\n", entry.getKey(), entry.getValue().file());
		}
	}
	
	private static double computeDistanceSquared(FLPNote a, List<FLPNote> b) {
		double lowest = Double.MAX_VALUE;
		for (var note : b) {
			double noteDistance = Math.abs(a.note - note.note) * 10; // TODO: Modulo 12 on all notes... And check modulo distance
			double beatDistance = Math.abs(a.tick - note.tick) / 24.0;
			
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
	
	public record FLPProject(long loadingTimeNs, String file, double tempo, List<List<FLPNote>> patterns) {}
	
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
