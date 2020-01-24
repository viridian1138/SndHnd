





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
import java.io.PrintStream;
import java.util.HashMap;

import core.NonClampedCoefficient;
import core.WaveForm;

/**
 * Waveform performing a bitcrush effect on an input wave in the form of a resolution reduction.
 * 
 * See <A href="https://en.wikipedia.org/wiki/Bitcrusher">https://en.wikipedia.org/wiki/Bitcrusher</A>
 * 
 * @author tgreen
 *
 */
public class BitcrushWaveform extends WaveForm {
	
	/**
	 * The input waveform.
	 */
	protected WaveForm wave;
	
	/**
	 * The multiplier to apply before performing quantization.
	 */
	protected long multiplier;

	/**
	 * Constructs the waveform.
	 * @param _wave The input waveform.
	 * @param _mult The multiplier to apply before performing quantization.
	 */
	public BitcrushWaveform( WaveForm _wave , long _mult ) {
		super();
		wave = _wave;
		multiplier = _mult;
	}

	@Override
	public double eval(double param ) {
		long val = (long)( multiplier * ( wave.eval( param ) + 2.0 ) );
		return( ( (double) val ) / multiplier - 2.0 );
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
			return( new BitcrushWaveform( wv , multiplier ) );
		}
	}
	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GBitcrushWaveform wv = new GBitcrushWaveform();
		s.put(this, wv);
		
		GWaveForm w = wave.genWave(s);
		
		wv.load(w,multiplier);
		
		return( wv );
	}

	public void writeExternal(ObjectOutput arg0) throws IOException {
		// TODO Auto-generated method stub

	}

	public void readExternal(ObjectInput arg0) throws IOException,
			ClassNotFoundException {
		// TODO Auto-generated method stub

	}
	
	/**
	 * Gets the multiplier corresponding to a certain number of bits of bitcrush.
	 * @param numBits The input number of bits.
	 * @return The calculated multiplier.
	 */
	public static long getBitcrushMult( int numBits )
	{
		final long strt = 1;
		return( strt << ( numBits - 1 ) );
	}

	
}

