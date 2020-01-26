




//$$strtCprt
/**
* SndHnd
* 
* Copyright (C) 1992-2020 Thornton Green
* 
* This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
* published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
* of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
* You should have received a copy of the GNU General Public License along with this program; if not, 
* see <http://www.gnu.org/licenses>.
* Additional permission under GNU GPL version 3 section 7
*
*/
//$$endCprt







package core;

import javax.sound.midi.*;

/**
 * Provides a calling interface similar to a QTJ (QuickTime for Java) NoteChannel using JavaSound.
 * 
 * @author tgreen
 *
 */
public class NoteChannelEmulator {

	/**
	 * The set of available channel emulators.
	 */
	private static NoteChannelEmulator[] emulators = null;

	/**
	 * The current index into the channel emulators.
	 */
	private static int curIndex = 0;

	/**
	 * Allocates a NoteChannelEmulator.
	 * @return The allocated NoteChannelEmulator.
	 * @throws MidiUnavailableException
	 */
	public static synchronized NoteChannelEmulator allocateEmulator()
			throws MidiUnavailableException {
		if (emulators == null) {
			Synthesizer s = MidiSystem.getSynthesizer();
			s.open();
			System.out.println(s);
			MidiChannel[] channels = s.getChannels();

			Instrument[] i = s.getAvailableInstruments();

			Instrument ii = i[2];

			final int len = channels.length;

			emulators = new NoteChannelEmulator[len];

			int cnt;

			for (cnt = 0; cnt < len; cnt++) {
				if (channels[cnt] != null) {
					emulators[cnt] = new NoteChannelEmulator(channels[cnt], ii);
				}
			}

		}

		final int len = emulators.length;
		int cnt = 0;
		NoteChannelEmulator ret = null;

		while ((cnt < len) && (ret == null)) {
			int ind = (cnt + curIndex) % len;
			if (emulators[ind] != null) {
				if (!(emulators[ind].isInUse())) {
					ret = emulators[ind];
					ret.setInUse();
					curIndex = ind;
				}
			}
			cnt++;
		}

		if (ret == null) {
			throw (new RuntimeException("Fail"));
		}

		return (ret);

	}
	
	/**
	 * Sets the MIDI instrument to be used.
	 * @param i The MIDI instrument to be used.
	 */
	public static synchronized void setInstrument( Instrument i )
	{
		for( int cnt = 0 ; cnt < emulators.length ; cnt++ )
		{
			if( emulators[ cnt ] != null )
			{
				emulators[ cnt ].setIns( i );
			}
		}
	}

	
	/**
	 * The JavaSound MidiChannel in which to play.
	 */
	private MidiChannel channel;

	/**
	 * The MIDI instrument to be used.
	 */
	private Instrument i;

	/**
	 * Whether the channel is currently in use.
	 */
	private boolean inUse = false;

	/**
	 * The integer portion of the frequency of the note to be played in hertz.
	 */
	private int useFval;

	/**
	 * Constructor.
	 * @param _channel The JavaSound MidiChannel in which to play.
	 * @param ii The MIDI instrument to be used.
	 */
	private NoteChannelEmulator(MidiChannel _channel, Instrument ii) {
		channel = _channel;
		i = ii;
	}

	/**
	 * Initiates playing at a particular pitch.
	 * @param freq The frequency (in hertz) at which to start playing.
	 */
	public void play(double freq) {
		synchronized (NoteChannelEmulator.class) {
			if (inUse) {
				useFval = (int) (Math.round(freq));
				channel.setMute(false);
				channel.programChange(i.getPatch().getProgram());
				channel.setPitchBend((int) ((freq - useFval) * 4096 + 8192));
				channel.noteOn(useFval, 100); 
			}
		}
	}

	/**
	 * Stops note playing on the channel.
	 */
	public void stop() {
		synchronized (NoteChannelEmulator.class) {
			if (inUse) {
				channel.noteOff(useFval);
				inUse = false;
			}
		}
	}

	/**
	 * Gets whether the channel is currently in-use.
	 * @return Whether the channel is currently in-use.
	 */
	public boolean isInUse() {
		return (inUse);
	}

	/**
	 * Marks the channel as being in-use.
	 */
	public void setInUse() {
		inUse = true;
	}
	
	/**
	 * Sets the MIDI instrument of the channel.
	 * @param _i The MIDI instrument of the channel.
	 */
	public void setIns( Instrument _i )
	{
		i = _i;
	}

}

