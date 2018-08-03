package de.lemonpie.beddocontrol.midi;

import javax.sound.midi.MidiMessage;

public interface MidiListener
{

	void onMidiAction(MidiMessage message);

}
