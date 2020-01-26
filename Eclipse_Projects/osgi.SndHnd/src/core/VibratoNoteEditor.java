




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


/**
 * Subclass of VibratoParamsEditor for editing the vibrato parameters of a note.
 * 
 * @author tgreen
 *
 */
public class VibratoNoteEditor extends VibratoParamsEditor {
	
	/**
	 * The note to be edited.
	 */
	NoteDesc note;
	
	/**
	 * The instrument track containing the note to be edited.
	 */
	InstrumentTrack track;

	/**
	 * Constructor.
	 * @param _note The note to be edited.
	 * @param _track The instrument track containing the note to be edited.
	 */
	public VibratoNoteEditor( NoteDesc _note , InstrumentTrack _track ) {
		super( _note.getVibratoParams() );
		note = _note;
		track = _track;
	}

	@Override
	protected void handleApply()
	{
		try
		{
			final int core = 0;
			note.setUserDefinedVibrato( true );
			track.updateTrackFrames( core );
			SongListeners.updateViewPanes();
		}
		catch( Throwable ex )
		{
			ex.printStackTrace( System.out );
		}
	}

}

