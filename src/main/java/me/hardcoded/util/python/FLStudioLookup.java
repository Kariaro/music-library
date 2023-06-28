package me.hardcoded.util.python;

import me.hardcoded.algorithm.midi.MelodyScore;
import me.hardcoded.algorithm.midi.MelodyScoreCalculator;
import me.hardcoded.algorithm.thread.ThreadHelper;
import me.hardcoded.data.Note;
import me.hardcoded.data.persistent.ApplicationData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class FLStudioLookup {
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
	
	public static FLPProject readProjectPatterns(PythonRunnerPersistent runner,  File file) {
		// String output = PythonRunner.runPath(parserFile, List.of("\"" + file + "\""));
		String output = runner.sendInput(file.toString().getBytes(StandardCharsets.UTF_8));
		
		if (output != null) {
			String[] parts = output.strip().split("\\|");
			long nanos = (long) (Double.parseDouble(parts[0]) * 1000000000L);
			double tempo = Double.parseDouble(parts[1]);
			
			return new FLPProject(
				nanos,
				file.toString(),
				tempo,
				Arrays.stream(parts, 2, parts.length).map(FLStudioLookup::transform).toList()
			);
		}
		
		return null;
	}
	
	private static List<Note> transform(String line) {
		if (line.isEmpty()) {
			return List.of();
		}
		
		List<Note> result = new ArrayList<>();
		String[] array = line.split(";");
		try {
			for (String str : array) {
				if (str.isEmpty()) {
					continue;
				}
				
				String[] parts = str.split(",");
				
				int note = Integer.parseInt(parts[0]);
				int tick = Integer.parseInt(parts[1]);
				result.add(new Note(note, tick, tick));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(Arrays.toString(array));
		}
		
		return result;
	}
	
	public static void performSearch(List<Note> notes) {
		File test = ApplicationData.getInstance().get("test").toFile();
		
		List<File> files = new ArrayList<>();
		try {
			Files.walk(test.toPath()).forEach(path -> {
				File file = path.toFile();
				if (file.isFile() && file.getName().endsWith(".flp")) {
					files.add(file);
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Map<Thread, PythonRunnerPersistent> runners = new ConcurrentHashMap<>();
		List<Map.Entry<Double, FLPProject>> list = Collections.synchronizedList(new ArrayList<>());
		ExecutorService service = Executors.newFixedThreadPool(16);
		
		long start = System.currentTimeMillis();
		try {
			List<Future<?>> futures = new ArrayList<>();
			for (File file : files) {
				futures.add(service.submit(() -> {
					var thread = Thread.currentThread();
					PythonRunnerPersistent runner = runners.get(thread);
					if (runner == null) {
						runner = new PythonRunnerPersistent();
						runner.start(parserFile, List.of());
						runners.put(thread, runner);
					}
					
					var project = readProjectPatterns(runner, file);
					if (project == null) {
						return;
					}
					
					MelodyScore score = MelodyScore.None;
					for (var item : project.patterns()) {
						if (!item.isEmpty()) {
							score = score.min(MelodyScoreCalculator.computeScore(notes, item));
						}
					}
					
					list.add(Map.entry(score.valid() ? score.score() : Double.NaN, project));
					System.out.printf("%s -> %d | score = %.4f\n", file.getName(), project.patterns().size(), score.score());
				}));
			}
			
			for (var future : futures) {
				future.get();
			}
		} catch (CancellationException | ExecutionException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			for (var runner : runners.values()) {
				runner.stop();
			}
			
			service.shutdown();
		}
		
		long elapsed = System.currentTimeMillis() - start;
		
		list.sort(Map.Entry.comparingByKey());
		
		for (var entry : list) {
			System.out.printf("%8.8f score: '%s'\n", entry.getKey(), entry.getValue().file());
		}
		System.out.printf("Took %.4f seconds\n", elapsed / 1000.0);
	}
	
	/**
	 * Searches through the specified paths and returns all loaded projects
	 */
	public static List<FLPProject> getProjects(List<Path> paths, Consumer<Float> percentageCallback) {
		List<File> files = new ArrayList<>();
		try {
			for (Path searchPath : paths) {
				Files.walk(searchPath).forEach(path -> {
					File file = path.toFile();
					if (file.isFile() && file.getName().endsWith(".flp")) {
						files.add(file);
					}
				});
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Map<Thread, PythonRunnerPersistent> runners = new ConcurrentHashMap<>();
		ThreadHelper<FLPProject> helper = new ThreadHelper<>(16);
		List<FLPProject> projects = List.of();
		AtomicInteger total = new AtomicInteger();
		try (helper) {
			for (File file : files) {
				helper.submit(() -> {
					var thread = Thread.currentThread();
					PythonRunnerPersistent runner = runners.get(thread);
					if (runner == null) {
						runner = new PythonRunnerPersistent();
						runner.start(parserFile, List.of());
						runners.put(thread, runner);
					}
					
					FLPProject project = readProjectPatterns(runner, file);
					synchronized (percentageCallback) {
						int count = total.addAndGet(1);
						percentageCallback.accept(count / (float) files.size());
					}
					
					return project;
				});
			}
			
			projects = helper.getResults();
			projects.removeIf(Objects::isNull);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			for (var runner : runners.values()) {
				runner.stop();
			}
		}
		
		percentageCallback.accept(1.0f);
		
		return projects;
	}
	
	public static List<Map.Entry<MelodyScore, FLPProject>> searchProjectsForMelody(List<FLPProject> projects, List<Note> melody, Consumer<Float> percentageCallback) {
		List<Map.Entry<MelodyScore, FLPProject>> result = List.of();
		
		// Sort projects to make sure the largest are placed first
		projects.sort(Comparator.comparingLong(list -> {
			long totalNotes = 0;
			for (var item : list.patterns()) {
				totalNotes += item.size() * (long) item.size();
			}
			
			return -totalNotes;
		}));
		
		AtomicInteger total = new AtomicInteger();
		try (ThreadHelper<Map.Entry<MelodyScore, FLPProject>> helper = new ThreadHelper<>(16)) {
			for (FLPProject project : projects) {
				helper.submit(() -> {
					MelodyScore score = MelodyScore.None;
					for (var item : project.patterns()) {
						if (!item.isEmpty()) {
							score = score.min(MelodyScoreCalculator.computeScore(melody, item));
						}
					}
						
					synchronized (percentageCallback) {
						int count = total.getAndAdd(1);
						percentageCallback.accept(count / (float) projects.size());
					}
					System.out.printf("%s -> %d | score = %.4f\n", project.file(), project.patterns().size(), score.score());
					return Map.entry(score, project);
				});
			}
			
			result = helper.getResults();
			result.sort(Comparator.comparing(item -> item.getKey().getCalculatedScore()));
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		percentageCallback.accept(1.0f);
		return result;
	}
	
	public record FLPProject(long loadingTimeNs, String file, double tempo, List<List<Note>> patterns) {}
}
