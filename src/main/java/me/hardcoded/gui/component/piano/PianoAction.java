package me.hardcoded.gui.component.piano;

interface PianoAction {
	String Play = "play";
	String RollDeleteSelection = "roll-delete-selection";
	String RollUndo = "roll-undo";
	String RollSelectAll = "roll-select-all";
	
	String RollMoveSelectionOctaveDown = "roll-move-selection-octave-down";
	String RollMoveSelectionOctaveUp = "roll-move-selection-octave-up";
	String RollMoveSelectionDown = "roll-move-selection-down";
	String RollMoveSelectionUp = "roll-move-selection-up";
	String RollMoveSelectionLeft = "roll-move-selection-left";
	String RollMoveSelectionRight = "roll-move-selection-right";
}
