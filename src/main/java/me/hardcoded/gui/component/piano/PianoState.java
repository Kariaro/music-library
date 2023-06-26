package me.hardcoded.gui.component.piano;

import me.hardcoded.data.Note;

import java.util.*;

public class PianoState {
	private final LinkedList<Operation> operations = new LinkedList<>();
	private final List<Note> notes = new ArrayList<>();
	
	public synchronized void addNote(Note note) {
		operations.addFirst(new Operation(Type.Add, List.of(note)));
		normalizeOperations();
		
		notes.add(note);
	}
	
	public void addNotes(List<Note> list) {
		operations.addFirst(new Operation(Type.Add, new ArrayList<>(list)));
		normalizeOperations();
		
		notes.addAll(list);
	}
	
	public synchronized void removeNotes(Collection<Note> collections) {
		operations.addFirst(new Operation(Type.Remove, new ArrayList<>(collections)));
		normalizeOperations();
		
		notes.removeAll(collections);
	}
	
	public synchronized void undo() {
		if (operations.isEmpty()) {
			return;
		}
		
		Operation op = operations.pop();
		switch (op.type()) {
			case Remove -> notes.addAll(op.notes);
			case Add -> notes.removeAll(op.notes);
		}
	}
	
	/**
	 * This will not be thread-safe
	 */
	public List<Note> getNotes() {
		return notes;
	}
	
	/**
	 * Make sure the size of the operations list does not exceed 20
	 */
	private void normalizeOperations() {
		while (operations.size() > 20) {
			operations.removeLast();
		}
	}
	
	private enum Type {
		Remove,
		Add
	}
	
	private static record Operation(Type type, Collection<Note> notes) {}
}
