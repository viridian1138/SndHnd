





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
import java.util.ArrayList;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import core.WaveForm;

public class GMultiXtalWaveform extends GWaveForm  implements Externalizable {
	
	private ArrayList<GXtalWaveform> waves = new ArrayList<GXtalWaveform>();

	public WaveForm genWave(HashMap s) {
		if( s.get(this) != null )
		{
			return( (WaveForm)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		ArrayList<XtalWaveform> lst = new ArrayList<XtalWaveform>();
		
		for( GXtalWaveform gx : waves )
		{
			XtalWaveform xv = (XtalWaveform)( gx.genWave(s) );
			lst.add( xv );
		}
		
		MultiXtalWaveform wv = new MultiXtalWaveform(lst);
		s.put(this, wv);
		
		return( wv );
	}

	public String getName() {
		return( "MultiXtal Waveform" );
	}

	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GXtalWaveform );
	}

	public void performAssign(GNode in) {
		waves.add( (GXtalWaveform) in );
	}

	public void removeChld() {
		waves = new ArrayList<GXtalWaveform>();
	}

	public Object getChldNodes() {
		return( waves );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setInt("WavesSize", waves.size());
		int plen = waves.size();
		int count;
		for (count = 0; count < plen; count++) {
			myv.setProperty("Wave_" + count, waves.get(count));
		}
		
		out.writeObject(myv);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);
			
			int plen = myv.getInt("WavesSize");
			waves = new ArrayList<GXtalWaveform>(plen);
			int count;
			for (count = 0; count < plen; count++) {
				GXtalWaveform wave = (GXtalWaveform) (myv
						.getPropertyEx("Wave_" + count));
				waves.add(wave);
			}
			

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}

