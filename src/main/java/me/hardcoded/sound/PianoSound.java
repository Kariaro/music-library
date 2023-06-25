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
		service.schedule(() -> channels[0].noteOn(note, volume), 0, TimeUnit.MILLISECONDS);
		service.schedule(() -> channels[0].noteOff(note), millis, TimeUnit.MILLISECONDS);
	}
	
	public void stop() {
		service.shutdown();
	}
}
