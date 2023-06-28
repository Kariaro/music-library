package me.hardcoded.gui.component.piano;

import me.hardcoded.data.Note;

import java.util.*;

public class PianoState {
	private final LinkedList<Operation> operations = new LinkedList<>();
	private final List<Note> notes = new ArrayList<>();
	
	public synchronized void addNote(Note note) {
		operations.addFirst(new Operation(Type.Add, List.of(note), 0, 0));
		normalizeOperations();
		
		notes.add(note);
	}
	
	public void addNotes(List<Note> list) {
		operations.addFirst(new Operation(Type.Add, new ArrayList<>(list), 0, 0));
		normalizeOperations();
		
		notes.addAll(list);
	}
	
	public synchronized void removeNotes(Collection<Note> collection) {
		operations.addFirst(new Operation(Type.Remove, new ArrayList<>(collection), 0, 0));
		normalizeOperations();
		
		notes.removeAll(collection);
	}
	
	public synchronized void moveNotes(Collection<Note> collection, int tx, int ty) {
		operations.addFirst(new Operation(Type.Move, new ArrayList<>(collection), tx, ty));
		normalizeOperations();
		
		for (Note note : collection) {
			note.start += tx;
			note.end += tx;
			note.note += ty;
		}
	}
	
	public synchronized void undo() {
		if (operations.isEmpty()) {
			return;
		}
		
		Operation op = operations.pop();
		switch (op.type()) {
			case Remove -> notes.addAll(op.notes);
			case Add -> notes.removeAll(op.notes);
			case Move -> {
				for (Note note : op.notes) {
					note.start -= op.tx;
					note.end -= op.tx;
					note.note -= op.ty;
				}
			}
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
		Add,
		Move
	}
	
	private static record Operation(Type type, Collection<Note> notes, int tx, int ty) {}
}
