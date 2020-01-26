




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


import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;

import meta.DataFormatException;
import meta.VersionBuffer;

/**
 * 
 * A sequential non-overlapping sequence of musical notes (or samples) in a track framed by the contiguous interval of time that bounds the notes.
 * 
 * @author thorngreen
 * 
 */
public class TrackFrame implements Externalizable {

	/**
	 * The notes defining the frame.
	 */
	protected ArrayList<NoteDesc> notes = new ArrayList<NoteDesc>();

	/**
	 * The current note for each core thread.
	 */
	protected final int[] currentNoteIndex = CpuInfo.createInt( 0 );

	/**
	 * Constructor.
	 */
	public TrackFrame() {
		super();
	}

	/**
	 * Gets the notes defining the frame.
	 * @return The notes defining the frame.
	 */
	public ArrayList<NoteDesc> getNotes() {
		return notes;
	}

	/**
	 * Sets the notes defining the frame.
	 * @param notes The notes defining the frame.
	 */
	public void setNotes(ArrayList<NoteDesc> notes) {
		this.notes = notes;
		clearNoteIndexMultiCore();
	}
	
	/**
	 * Clears the current note indices used by all core threads.
	 */
	public void clearNoteIndexMultiCore()
	{
		final int max = CpuInfo.getNumCores();
		int count;
		for( count = 0 ; count < max ; count++ )
		{
			currentNoteIndex[ count ] = 0;
		}
	}

	/**
	 * Gets a particular note from the frame.
	 * @param index The input index of the note to retrieve.
	 * @return The note at the index.
	 */
	public final NoteDesc gNote(int index) {
		NoteDesc nd = notes.get(index);
		return (nd);
	}

	/**
	 * Gets the note at a particular beat number using specified start/end.
	 * @param beat_number The input beat number.
	 * @param core The number of the core thread.
	 * @return The note at the beat number.
	 */
	public NoteDesc getNoteDescStated(double beat_number, final int core) {
		if( notes.size() == 0 )
		{
			return( null );
		}
		
		while ((beat_number < gNote(currentNoteIndex[core]).startBeatNumber)
				&& (currentNoteIndex[core] > 0)) {
			( currentNoteIndex[core] )--;
		}

		while ((beat_number > gNote(currentNoteIndex[core]).endBeatNumber)
				&& (currentNoteIndex[core] < (notes.size() - 1))) {
			( currentNoteIndex[core] )++;
		}

		return (gNote(currentNoteIndex[core]));
	}
	
	/**
	 * Gets the note closest to a particular beat number using actual start/end.
	 * @param beat_number The input beat number.
	 * @param core The number of the core thread.
	 * @return The note closest to the beat number.
	 */
	public NoteDesc getNoteDescActual(double beat_number, final int core) {
		if( notes.size() == 0 )
		{
			return( null );
		}
		
		while ((beat_number < gNote(currentNoteIndex[core]).actualStartBeatNumber)
				&& (currentNoteIndex[core] > 0)) {
			( currentNoteIndex[core] )--;
		}

		while ((beat_number > gNote(currentNoteIndex[core]).actualEndBeatNumber)
				&& (currentNoteIndex[core] < (notes.size() - 1))) {
			( currentNoteIndex[core] )++;
		}

		return (gNote(currentNoteIndex[core]));
	}
	
	/**
	 * Gets the current note index.
	 * @param core The number of the core thread.
	 * @return The current note index.
	 */
	public int getCurrentNoteIndex( final int core )
	{
		return( currentNoteIndex[ core ] );
	}

	/**
	 * Reads the node from serial storage.
	 */
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			int plen = myv.getInt("NoteSize");
			notes = new ArrayList<NoteDesc>(plen);
			int count;
			for (count = 0; count < plen; count++) {
				notes.add((NoteDesc) (myv.getPropertyEx("Note_" + count)));
			}
		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	/**
	 * Writes the node to serial storage.
	 * 
	 * @serialData TBD.
	 */
	public void writeExternal(ObjectOutput out) throws IOException {
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setInt("NoteSize", notes.size());
		int plen = notes.size();
		int count;
		for (count = 0; count < plen; count++) {
			myv.setProperty("Note_" + count, notes.get(count));
		}

		out.writeObject(myv);
	}

}

