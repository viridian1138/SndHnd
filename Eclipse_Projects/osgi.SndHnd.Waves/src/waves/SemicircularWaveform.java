





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

import gredit.GWaveForm;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;

import core.NonClampedCoefficient;
import core.WaveForm;

/**
 * A sine-like waveform consisting of a series of semicircular segments x^2 + y^2 = r^2 where y is calculated from x and r is a constant.
 * 
 * @author tgreen
 *
 */
public class SemicircularWaveform extends WaveForm {

	/**
	 * Constructs the waveform.
	 */
	public SemicircularWaveform() {
		super();
	}

	@Override
	public double eval(final double p) {
		final double fp = Math.abs( p );
		final double f = fp - (int) fp;
		final double r = Math.sqrt( 1.0 - f * f );
		return( 2.0 * r - 1.0 );
	}
	
	@Override
	public NonClampedCoefficient genClone()
	{
		return( this );
	}
	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GSemicircularWaveform wv = new GSemicircularWaveform();
		s.put(this, wv);
		return( wv );
	}

	/**
	* Reads the node from serial storage.
	*/
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);
		}
		catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	/**
	* Writes the node to serial storage.
	* @serialData TBD.
	*/
	public void writeExternal(ObjectOutput out) throws IOException {
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		out.writeObject(myv);
	}

	
}


