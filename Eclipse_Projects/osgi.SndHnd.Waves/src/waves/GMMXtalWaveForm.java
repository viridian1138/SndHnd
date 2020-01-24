





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
import core.WaveForm;

public class GMMXtalWaveForm extends GWaveForm  implements Externalizable {
	
	private GMultiXtalWaveform sw;

	public WaveForm genWave(HashMap s) {
		if( s.get(this) != null )
		{
			return( (WaveForm)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		MultiXtalWaveform w =  (MultiXtalWaveform)( sw.genWave(s) );
		
		MMXtalWaveForm wv = new MMXtalWaveForm( w );
		s.put(this, wv);
		
		return( wv );
	}

	public String getName() {
		return( "MMXtal WaveForm" );
	}

	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GMultiXtalWaveform );
	}

	public void performAssign(GNode in) {
		sw = (GMultiXtalWaveform) in;
	}

	public void removeChld() {
		sw = null;
	}

	public Object getChldNodes() {
		return( sw );
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( sw != null ) myv.setProperty("Sw", sw);

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

			sw = (GMultiXtalWaveform)( myv.getProperty("Sw") );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}
