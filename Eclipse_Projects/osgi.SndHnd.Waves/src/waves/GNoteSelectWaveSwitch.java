





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







package waves;

import gredit.GNode;
import gredit.GWaveForm;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import verdantium.ProgramDirector;
import core.InstrumentTrack;
import core.IntelligentAgent;
import core.NoteDesc;
import core.WaveForm;

/**
 * Node representing a waveform that switches for one particular note.
 * 
 * @author tgreen
 *
 */
public class GNoteSelectWaveSwitch extends GWaveForm  implements Externalizable {
	
	/**
	 * Waveform used for all notes other than the selecting note.
	 */
	protected GWaveForm fullUpWave = null;
	
	/**
	 * Waveform used for the selecting note.
	 */
	protected GWaveForm noteSelectWave = null;
	
	/**
	 * The note upon which to select.
	 */
	protected NoteDesc switchDesc = null;
	
	/**
	 * Gets the note upon which to select.
	 * @return The note upon which to select.
	 */
	public NoteDesc getSwitchDesc() {
		return switchDesc;
	}

	/**
	 * Sets the note upon which to select.
	 * @param switchDesc The note upon which to select.
	 */
	public void setSwitchDesc(NoteDesc switchDesc) {
		this.switchDesc = switchDesc;
	}

	/**
	 * Constructs the node.
	 */
	public GNoteSelectWaveSwitch()
	{
		super();
	}

	@Override
	public WaveForm genWave(HashMap s) {
		if( s.get(this) != null )
		{
			return( (WaveForm)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		WaveForm w = ( IntelligentAgent.getCurrentNote() != switchDesc ) ? fullUpWave.genWave(s) : noteSelectWave.genWave(s);
		
		s.put(this, w);
		
		return( w );
	}

	@Override
	public String getName() {
		return( "Note Select Waveform Switch" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GWaveForm );
	}

	@Override
	public void performAssign(GNode in) {
		if( fullUpWave == null )
		{
			fullUpWave = (GWaveForm) in;
		}
		else
		{
			noteSelectWave = (GWaveForm) in;
		}

	}

	@Override
	public void removeChld() {
		fullUpWave = null;
		noteSelectWave = null;
	}

	public Object getChldNodes() {
		Object[] ob = { fullUpWave , noteSelectWave };
		return( ob );
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		InstrumentTrack track = (InstrumentTrack)( context.get( "InstrumentTrack" ) );
		NoteSelectWaveSwitchEditor editor = new NoteSelectWaveSwitchEditor( this , track );
		ProgramDirector.showPropertyEditor(editor, null,
			"Note Select Wave Switch Properties");
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( fullUpWave != null ) myv.setProperty("FullUpWave", fullUpWave);
		if( noteSelectWave != null ) myv.setProperty("NoteSelectWave", noteSelectWave);
		if( switchDesc != null ) myv.setProperty("SwitchDesc", switchDesc);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			fullUpWave = (GWaveForm)( myv.getProperty("FullUpWave") );
			noteSelectWave = (GWaveForm)( myv.getProperty("NoteSelectWave") );
			switchDesc = (NoteDesc)( myv.getProperty("SwitchDesc") );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}

