package me.hardcoded.sound;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PianoSound {
	private final Synthesizer synth;
	private final MidiChannel[] channels;
	private final ScheduledExecutorService service;
	private int[] notesTime = new int[256];
	
	public PianoSound() {
		service = Executors.newSingleThreadScheduledExecutor();
		try {
			synth = MidiSystem.getSynthesizer();
			synth.open();
			channels = synth.getChannels();
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public synchronized void playNote(int note, int volume, int millis) {
		int nid = ((note % notesTime.length) + notesTime.length) % notesTime.length;
		int id = ++notesTime[nid];
		
		service.schedule(() -> channels[0].noteOn(note, volume), 0, TimeUnit.MILLISECONDS);
		service.schedule(() -> {
			if (notesTime[nid] == id) {
				channels[0].noteOff(note);
			}
		}, millis, TimeUnit.MILLISECONDS);
	}
	
	public void stop() {
		service.shutdown();
	}
}
