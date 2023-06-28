package me.hardcoded.algorithm.midi;

import me.hardcoded.data.Note;

import java.nio.file.Path;
import java.util.List;

@FunctionalInterface
public interface MelodyCallback {
	/**
	 * Returns all melodies contained within the given path
	 * 
	 * @param path
	 * @return
	 */
	List<List<Note>> get(Path path);
}
