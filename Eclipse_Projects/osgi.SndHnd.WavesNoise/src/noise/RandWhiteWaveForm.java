





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







package noise;


import gredit.GWaveForm;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Random;

import core.NonClampedCoefficient;
import core.WaveForm;


/**
 * Waveform that produces white noise at lattice points.  The basic principle is to produce a unique
 * seed value at each lattice point, and then use that seed in a random number generator.
 * 
 * @author tgreen
 *
 */
public class RandWhiteWaveForm extends WaveForm {
	
	/**
	 * The offset for the seed values.
	 */
	protected int offset;
	
	/**
	 * The maximum number of times to run the random number generator through the initial seed.
	 */
	protected int max;
	
	/**
	 * The random number generator.
	 */
	protected final Random rand = new Random( 11 );
	
	
	/**
	 * Constructs the waveform.
	 * @param _offset The offset for the seed values.
	 * @param _max The maximum number of times to run the random number generator through the initial seed.
	 */
	public RandWhiteWaveForm( int _offset , int _max  )
	{
		offset = _offset;
		max = _max;
	}

	@Override
	public double eval( double w ) {
		final long lng = offset + (long)( w / 0.5 + 0.5 );
		// final long lng = offset + Double.doubleToLongBits( w );
		int count;
		rand.setSeed( lng );
		double d = rand.nextDouble();
		for( count = 0 ; count < max ; count++ )
		{
			d = rand.nextDouble();
		}
		return( 2.0 * ( d - 0.5 ) );
	}
	
	@Override
	public NonClampedCoefficient genClone()
	{
		return( new RandWhiteWaveForm( offset , max ) );
	}
	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GRandWhiteWaveForm wv = new GRandWhiteWaveForm();
		s.put(this, wv);
		
		wv.load(offset, max);
		
		return( wv );
	}

	
	public void writeExternal(ObjectOutput arg0) throws IOException {
		// TODO Auto-generated method stub

	}

	public void readExternal(ObjectInput arg0) throws IOException,
			ClassNotFoundException {
		// TODO Auto-generated method stub

	}

	
}

