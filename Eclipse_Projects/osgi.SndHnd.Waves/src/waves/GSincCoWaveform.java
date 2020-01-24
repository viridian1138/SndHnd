





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

import gredit.GZWaveBase;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import verdantium.ProgramDirector;
import core.InstrumentTrack;
import core.WaveForm;

/**
 * Node representing a waveform for a variant of the sinc function using the formula cos( x ) / ( 0.5 * ( abs( x ) - PI / 4 ) )
 * 
 * @author tgreen
 *
 */
public class GSincCoWaveform extends GZWaveBase  implements Externalizable {
	
	/**
	 * The height of the undefined region near x = +/- 0.25.
	 */
	double notchHeight = 1.0;

	@Override
	public WaveForm genWave(HashMap s) {
		if( s.get(this) != null )
		{
			return( (WaveForm)( s.get(this) ) );
		}
		
		SincCoWaveform wv = new SincCoWaveform( notchHeight );
		s.put(this, wv);
		
		return( wv );
	}

	@Override
	public String getName() {
		return( "SincCo" );
	}
	
	/**
	 * Loads new values into the node.
	 * @param d The height of the undefined region near x = +/- 0.25.
	 */
	public void load( double d )
	{
		notchHeight = d;
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		InstrumentTrack track = (InstrumentTrack)( context.get( "InstrumentTrack" ) );
		SincCoWaveformEditor editor = new SincCoWaveformEditor( this , track );
		ProgramDirector.showPropertyEditor(editor, null,
			"SincCo Wave Form Properties");
	}

	/**
	 * Gets the height of the undefined region near x = +/- 0.25.
	 * @return The height of the undefined region near x = +/- 0.25.
	 */
	public double getNotchHeight() {
		return notchHeight;
	}

	/**
	 * Sets the height of the undefined region near x = +/- 0.25.
	 * @param notchHeight The height of the undefined region near x = +/- 0.25.
	 */
	public void setNotchHeight(double notchHeight) {
		this.notchHeight = notchHeight;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setDouble("NotchHeight", notchHeight);
		
		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);
			
			notchHeight = myv.getDouble("NotchHeight");
			

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}

