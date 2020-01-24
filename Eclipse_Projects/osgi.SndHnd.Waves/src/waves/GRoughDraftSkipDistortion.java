





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
import core.SongData;
import core.WaveForm;

/**
 * Node for switching between a rough-draft waveform and a final-draft waveform based on the setting for skipping distortion.
 * 
 * @author tgreen
 *
 */
public class GRoughDraftSkipDistortion extends GWaveForm  implements Externalizable {
	
	/**
	 * The final-draft version of the waveform.
	 */
	protected GWaveForm fullUpWave = null;
	
	/**
	 * The rough-draft version of the waveform.
	 */
	protected GWaveForm roughDraftWave = null;
	
	/**
	 * Constructs the node.
	 */
	public GRoughDraftSkipDistortion()
	{
		super();
	}
	
	/**
	 * Constructs the node.
	 * @param _fullUpWave The final-draft version of the waveform.
	 * @param _roughDraftWave The rough-draft version of the waveform.
	 */
	public GRoughDraftSkipDistortion( GWaveForm _fullUpWave , GWaveForm _roughDraftWave )
	{
		fullUpWave = _fullUpWave;
		roughDraftWave = _roughDraftWave;
	}

    @Override
	public WaveForm genWave(HashMap s) {
		if( s.get(this) != null )
		{
			return( (WaveForm)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		boolean fullUp = true;
		if( !( SongData.ROUGH_DRAFT_MODE && SongData.skipDistortionForRoughDraft ) )
		{
			fullUp = false;
		}
		
		WaveForm w = fullUp ? fullUpWave.genWave(s) : roughDraftWave.genWave(s);
		
		s.put(this, w);
		
		return( w );
	}

    @Override
	public String getName() {
		return( "Rough Draft Skip Distortion" );
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
			roughDraftWave = (GWaveForm) in;
		}

	}

    @Override
	public void removeChld() {
		fullUpWave = null;
		roughDraftWave = null;
	}

    
	public Object getChldNodes() {
		Object[] ob = { fullUpWave , roughDraftWave };
		return( ob );
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( fullUpWave != null ) myv.setProperty("FullUpWave", fullUpWave);
		if( roughDraftWave != null ) myv.setProperty("RoughDraftWave", roughDraftWave);

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
			roughDraftWave = (GWaveForm)( myv.getProperty("RoughDraftWave") );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}

