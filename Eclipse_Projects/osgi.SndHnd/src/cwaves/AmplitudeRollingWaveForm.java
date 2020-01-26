




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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.PrintStream;
import java.util.HashMap;

import core.NonClampedCoefficient;
import core.WaveForm;

import meta.DataFormatException;
import meta.VersionBuffer;

/**
 * Node representing a waveform in which amplitude values above 1.0 on the input waveform roll over to approximately -1.0, and amplitude values below -1.0 on the input waveform roll over to approximately 1.0.
 * 
 * This is generally intended to be used on amplitude values in the range of e.g. [ -10.0 , 10.0 ]
 * 
 * @author tgreen
 *
 */
public class AmplitudeRollingWaveForm extends WaveForm {
	
	/**
	 * The input waveform.
	 */
	protected WaveForm wave;
	
	/**
	 * Constructor for persistence purposes only.
	 */
	public AmplitudeRollingWaveForm()
	{
	}

	/**
	 * Constructs the waveform.
	 * @param _wave The input waveform.
	 */
	public AmplitudeRollingWaveForm( WaveForm _wave ) {
		super();
		wave = _wave;
	}

	@Override
	public double eval(double param ) {
		double val = 0.5 * ( wave.eval( param ) ) + 50.5;
		double rval = val - (int) val;
		return( 2.0 * ( rval - 0.5 ) );
	}
	
	@Override
	public NonClampedCoefficient genClone() throws Throwable
	{
		final WaveForm wv = (WaveForm)( wave.genClone() );
		if( wv == wave )
		{
			return( this );
		}
		else
		{
			return( new AmplitudeRollingWaveForm( wv ) );
		}
	}

	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GAmplitudeRollingWaveForm wv = new GAmplitudeRollingWaveForm();
		s.put(this, wv);
		
		GWaveForm w = wave.genWave(s);
		
		wv.load(w);
		
		return( wv );
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setProperty("Wave",wave);

		out.writeObject(myv);
	}

	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			wave = (WaveForm)( myv.getProperty( "Wave" ) );
		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}

	}

}
