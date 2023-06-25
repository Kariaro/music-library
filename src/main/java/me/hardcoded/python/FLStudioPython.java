package me.hardcoded.python;

import me.hardcoded.data.Note;

import javax.sound.sampled.Line;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FLStudioPython {
	public static List<List<FLPNote>> readProjectPatterns(File file) {
		String output = PythonRunner.run("""
			import pyflp
			project = pyflp.parse("%s")
			for pattern in project.patterns:
			    notes = []
			    for x in pattern.notes:
			    	print(x["key"], x.position, sep=',', end=';')
			    print()
			""".formatted(file.toString().replace("\\", "\\\\")));
		
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
				
				double step = Integer.parseInt(parts[0]) / 24.0;
				int note = Integer.parseInt(parts[1]);
				result.add(new FLPNote(step, note));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(Arrays.toString(array));
		}
		
		return result;
	}
	
	public static void performSearch(List<Note> notes) {
		File test = Path.of(PythonRunner.dataPath.toString(), "test").toFile();
		File[] array = test.listFiles();
		if (array != null) {
			for (File file : array) {
				var patterns = readProjectPatterns(file);
				if (patterns == null) {
					continue;
				}
				
				System.out.printf("%s -> %d\n", file.getName(), patterns.size());
			}
		}
	}
	
	public record FLPNote(double beat, int note) {};
}
