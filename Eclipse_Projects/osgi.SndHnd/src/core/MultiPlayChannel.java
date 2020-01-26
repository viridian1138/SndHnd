




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

import javax.sound.midi.MidiUnavailableException;


/**
 * A note channel that can be played multiple times.
 * 
 * @author tgreen
 *
 */
public class MultiPlayChannel {
	
	/**
	 * The underlying NoteChannel used for playing.
	 */
	NoteChannelEmulator nc = null;
	
	/**
	 * Initiates playing at a particular pitch.
	 * @param freq The frequency (in hertz) at which to start playing.
	 */
	public void play( double freq ) throws MidiUnavailableException
	{
		if( nc != null )
		{
			nc.stop();
		}
		nc = NoteChannelEmulator.allocateEmulator();
		nc.play(freq);
	}
	
	/**
	 * Stops playing the note.
	 */
	public void stop()
	{
		if( nc != null )
		{
			nc.stop();
			nc = null;
		}
	}
	
	@Override
	protected void finalize()
	{
		if( nc != null )
		{
			nc.stop();
			nc = null;
		}
	}
	

}

