import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

public final class SoundProvider {

	private static SoundProvider instance = null;
	private final ExecutorService playerPool = Executors.newCachedThreadPool();
	private Sequencer midiSequencer;
	private Sequence midiSequence;

	protected SoundProvider() {
		try {
			this.midiSequencer = MidiSystem.getSequencer();
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
	}

	public static SoundProvider getInstance() {
		return instance = ((instance == null) ? new SoundProvider() : instance);
	}

	public void fadeMidi(final boolean mute) {
		if (midiSequencer == null)
			return;

		try {
			for (int i = 0; i < 16; i++)
				midiSequencer.setTrackMute(i, mute);
		} catch (Exception _ex) {
			_ex.printStackTrace();
		}
	}

	public void stopMidi() {
		if (midiSequencer == null)
			return;

		try {
			fadeMidi(true);
			if (midiSequencer != null && midiSequencer.isOpen()) {
				midiSequencer.stop();
			}
		} catch (Exception _ex) {
			_ex.printStackTrace();
		}
	}

	public void playMIDI(final byte[] data) {
		if (midiSequencer == null)
			return;

		if (!midiSequencer.isOpen())
			try {
				midiSequencer.open();
			} catch (MidiUnavailableException e1) {
				e1.printStackTrace();
			}
		playerPool.execute(new Runnable() {

			@Override
			public void run() {
				try {
					if (midiSequencer.isRunning()) {
						try {
							fadeMidi(true);
							Thread.sleep(1200);
							midiSequencer.stop();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					midiSequence = MidiSystem.getSequence(new BufferedInputStream(new ByteArrayInputStream(data)));
					midiSequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
					midiSequencer.setSequence(midiSequence);
				} catch (Exception e) {
					e.printStackTrace();
				}
				fadeMidi(false);
				midiSequencer.start();
			}
		});
	}
}