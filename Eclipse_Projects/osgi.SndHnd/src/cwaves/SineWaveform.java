




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
 * Waveform for a sine function.  Uses a lookup table to improve performance.
 * 
 * @author tgreen
 *
 */
public class SineWaveform extends WaveForm implements Externalizable {
	
	/**
	 * Inverse wavelength multiplier used to initialize the lookup table.
	 */
	protected static final double SIN_INVERSE_WAVELENGTH = 2.0 * Math.PI;
	
	/**
	 * Integer value for the size of the lookup table.
	 */
	static final int N = 10000000;
	
	/**
	 * Double-precision value for the size of the lookup table.
	 */
	static final double Na = N;
	
	/**
	 * The lookup table.
	 */
	static final double[] nv = new double[ N + 2 ];
	
	/**
	 * Whether the lookup table has been initialized.
	 */
	static boolean initialized = false;

	/**
	 * Constructs the waveform.
	 */
	public SineWaveform() {
		super();
		if( !initialized )
		{
			int count;
			double max = (double) N;
			for( count = 0 ; count < N + 2 ; count++ )
			{
				nv[ count ] = Math.sin( count * SIN_INVERSE_WAVELENGTH / max );
			}
			initialized = true;
		}
	}

	@Override
	public double eval(double phase_distorted_param) {
		// return( Math.sin( SIN_INVERSE_WAVELENGTH * phase_distorted_param  ) );
		return( phase_distorted_param >= 0.0 ? 
				nv[ (int)( ( N * phase_distorted_param ) % Na ) ] :
					- nv[ (int)( ( - N * phase_distorted_param ) % Na ) ] );
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
		
		GSineWaveform wv = new GSineWaveform();
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

