




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







package cwaves;

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
 * 
 * Node representing a waveform that is in one mode if the SongData.CALC_GLOBAL_MAX_VAL global is turned on, and is in another mode if the SongData.CALC_GLOBAL_MAX_VAL global is turned off.
 * 
 * @author tgreen
 *
 */
public class GSwitchGlobalMaxWave extends GWaveForm implements Externalizable {
	
	/**
	 * Waveform to be used if the SongData.CALC_GLOBAL_MAX_VAL global is turned off.
	 */
	protected GWaveForm globalOff;
	
	/**
	 * Waveform to be used if the SongData.CALC_GLOBAL_MAX_VAL global is turned on.
	 */
	protected GWaveForm globalOn;

	@Override
	public WaveForm genWave(HashMap s) {
		if( !( SongData.CALC_GLOBAL_MAX_VAL ) )
		{
			return( globalOff.genWave(s) );
		}
		
		return( globalOn.genWave(s) );
	}

	@Override
	public String getName() {
		return( "Switch Global Max Wave" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GWaveForm );
	}

	@Override
	public void performAssign(GNode in) {
		if( globalOff == null )
		{
			globalOff = (GWaveForm) in;
		}
		else
		{
			globalOn = (GWaveForm) in;
		}
	}

	@Override
	public void removeChld() {
		globalOff = null;
		globalOn = null;
	}

	public Object getChldNodes() {
		Object[] ob = { globalOff , globalOn };
		return( ob );
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( globalOff != null ) myv.setProperty("GlobalOff", globalOff);
		if( globalOn != null ) myv.setProperty("GlobalOn", globalOn);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			globalOff = (GWaveForm)( myv.getProperty("GlobalOff") );
			globalOn = (GWaveForm)( myv.getProperty("GlobalOn") );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}

