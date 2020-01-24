





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

import gredit.GNonClampedCoefficient;
import gredit.GWaveForm;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import core.NonClampedCoefficient;
import core.WaveForm;

/**
 * 
 * Waveform that takes an input waveform and an altering function w2,
 * and evaluates the waveform at p by evaluating the input waveform at
 * approximately log2( 2 ^ p + 2 ^ ( w2( p ) ) ).  Note that there are
 * some additional cutoffs that are put in to make the final result
 * more consistent.
 * 
 * @author tgreen
 *
 */
public class FrequencyLnWaveForm extends WaveForm {
	
	/**
	 * The input waveform.
	 */
	WaveForm w1;
	
	/**
	 * The altering function.
	 */
	NonClampedCoefficient w2;
	
	/**
	 * Calculated log-transformed cutoff value.
	 */
	private double asw;
	
	/**
	 * The wave number of the final cutoff in the frequency domain.
	 */
	private static final double CUTOFF = 900.0;


	/**
	 * Constructs the waveform.
	 * @param _w1 The input waveform.
	 * @param _w2 The altering function.
	 */
	public FrequencyLnWaveForm( WaveForm _w1 , NonClampedCoefficient _w2 ) {
		w1 = _w1;
		w2 = _w2;
		
		final double p2 = CUTOFF;
		final double pw = Math.pow( 2.0, p2);
		final double pw2 = Math.log( pw ) / Math.log( 2 );
		asw = pw2;
	}

	@Override
	public double eval(double p) {
		
		double ret = 0.0;
		
		if( p > 0.0 )
		{
			final int pa = (int)( p / CUTOFF);
			final double p2 = p - CUTOFF * pa;
			final double pw = Math.pow( 2.0, p2);
			final double pww = pw + Math.pow( 2.0, w2.eval( p ) );
			final double pw2 = Math.log( pww ) / Math.log( 2 );
			
			ret += w1.eval( pw2 + pa * asw );
		}
		else
		{
			final double p2 = p;
			final double pw = Math.pow( 2.0, p2);
			final double pww = pw + Math.pow( 2.0, w2.eval( p ) );
			final double pw2 = Math.log( pww ) / Math.log( 2 );
			
			ret += w1.eval( pw2 );
		}
		
		return( ret );
	}
	
	@Override
	public NonClampedCoefficient genClone() throws Throwable
	{
		final WaveForm wv = (WaveForm)( w1.genClone() );
		final NonClampedCoefficient cl = w2.genClone();
		if( ( wv == w1 ) && ( cl == w2 ) )
		{
			return( this );
		}
		else
		{
			return( new FrequencyLnWaveForm( wv , cl ) );
		}
	}
	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GFrequencyLnWaveForm wv = new GFrequencyLnWaveForm();
		s.put(this, wv);
		
		GWaveForm a1 = w1.genWave(s);
		GNonClampedCoefficient a2 = w2.genCoeff(s);
		
		wv.load(a1,a2);
		
		return( wv );
	}

	/**
	* Reads the node from serial storage.
	*/
	public void writeExternal(ObjectOutput arg0) throws IOException {
		// TODO Auto-generated method stub
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	}

	/**
	* Writes the node to serial storage.
	* @serialData TBD.
	*/
	public void readExternal(ObjectInput arg0) throws IOException,
			ClassNotFoundException {
		// TODO Auto-generated method stub
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	}

	
}

