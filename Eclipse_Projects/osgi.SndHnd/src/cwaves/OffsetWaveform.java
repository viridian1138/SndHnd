




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
import gredit.GNonClampedCoefficient;
import gredit.GWaveForm;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import core.NonClampedCoefficient;
import core.WaveForm;

/**
 * Waveform that adds an offset to the amplitude of the input waveform.
 * 
 * @author tgreen
 *
 */
public class OffsetWaveform extends WaveForm implements Externalizable {
	
	/**
	 * The input waveform.
	 */
	WaveForm wave;
	
	/**
	 * The coefficient to offset the amplitude.
	 */
	NonClampedCoefficient offset;

	/**
	 * Constructs the waveform.
	 * @param _wave The input waveform.
	 * @param _offset The coefficient to offset the amplitude.
	 */
	public OffsetWaveform( WaveForm _wave , NonClampedCoefficient _offset ) {
		super();
		wave = _wave;
		offset = _offset;
	}

	@Override
	public double eval(double p) {
		return( wave.eval(p) + offset.eval(p) );
	}
	
	@Override
	public NonClampedCoefficient genClone() throws Throwable
	{
		final WaveForm wv = (WaveForm)( wave.genClone() );
		final NonClampedCoefficient off = offset.genClone();
		if( ( wv == wave ) && ( off == offset ) )
		{
			return( this );
		}
		else
		{
			return( new OffsetWaveform( wv , off ) );
		}
	}
	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GOffsetWaveform wv = new GOffsetWaveform();
		s.put(this, wv);
		
		GWaveForm w = wave.genWave(s);
		GNonClampedCoefficient off = offset.genCoeff(s);
		
		wv.load(w,off);
		
		return( wv );
	}
	
	/**
	* Reads the node from serial storage.
	*/
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);
			// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			
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
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		

		out.writeObject(myv);
	}

}

