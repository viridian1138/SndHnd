





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

import core.NonClampedCoefficient;
import core.WaveForm;

/**
 * A version of a chorus effect where the parameter of each participant
 * in the chorus is effectively t_prime = ln( e^t + delta )
 * 
 * See <A href="https://en.wikipedia.org/wiki/Chorus_effect">https://en.wikipedia.org/wiki/Chorus_effect</A>
 * 
 * @author tgreen
 *
 */
public class ChorusLnWaveform extends WaveForm {
	
	/**
	 * The input waveform.
	 */
	private WaveForm wave;
	
	/**
	 * The maximum number of chorus waves to add.
	 */
	private int max;
	
	/**
	 * The increment of the parameter delta for each wave.
	 */
	private double deltaPerIncr;
	
	/**
	 * Multipliers used to normalize the parameter contributions.
	 */
	private double[] asw;
	
	/**
	 * The cutoff at which to stop performing the exponent.
	 */
	private static final double CUTOFF = 900.0;
	
	
	/**
	 * Constructs the waveform.
	 * @param in The input waveform.
	 * @param _max  The maximum number of chorus waves to add.
	 * @param _deltaPerIncr The increment of the parameter delta for each wave.
	 */
	public ChorusLnWaveform( final WaveForm in , final int _max , final double _deltaPerIncr )
	{
		wave = in;
		max = _max;
		deltaPerIncr = _deltaPerIncr;
		
		
		{
			asw = new double[ max ];
			int count;
			for( count = 0 ; count < max ; count++ )
			{
				{
					final double p2 = CUTOFF;
					final double pw = Math.pow( 2.0, p2);
					final double pww = pw + ( count + 1 ) * deltaPerIncr;
					final double pw2 = Math.log( pww ) / Math.log( 2 );
					asw[ count ] = pw2;
				}
			}
		}
		
		
		
	}

	
	@Override
	public double eval(double p) {
		double sum = wave.eval( p );
		final int max = this.max;
		int count;
		for( count = 0 ; count < max ; count++ )
		{
			{
				if( p > 0.0 )
				{
					final int pa = (int)( p / CUTOFF);
					final double p2 = p - CUTOFF * pa;
					final double pw = Math.pow( 2.0, p2);
					final double pww = pw + ( count + 1 ) * deltaPerIncr;
					final double pw2 = Math.log( pww ) / Math.log( 2 );
					
					sum += wave.eval( pw2 + pa * asw[ count ] );
				}
				else
				{
					final double p2 = p;
					final double pw = Math.pow( 2.0, p2);
					final double pww = pw + ( count + 1 ) * deltaPerIncr;
					final double pw2 = Math.log( pww ) / Math.log( 2 );
					
					sum += wave.eval( pw2 );
				}
				
				
			}
			
			/* {
			 *  final double p2 = CUTOFF * Math.abs( tri.eval( p / CUTOFF ) );
				final double pw = Math.pow( 2.0, p2);
				final double pww = pw - ( count + 1 ) * deltaPerIncr;
				final double pw2 = Math.log( pww ) / Math.log( 2 );
				sum += wave.eval( pw2 );
			} */
		}
		return( sum / ( /* 2 * */ max + 1 ) );
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
			return( new ChorusLnWaveform( wv , max , deltaPerIncr ) );
		}
	}
	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GChorusLnWaveform wv = new GChorusLnWaveform();
		s.put(this, wv);
		
		GWaveForm w = wave.genWave(s);
		
		wv.load(w,max,deltaPerIncr);
		
		return( wv );
	}

	
	public void readExternal(ObjectInput arg0) throws IOException,
			ClassNotFoundException {
		// TODO Auto-generated method stub
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	}

	public void writeExternal(ObjectOutput arg0) throws IOException {
		// TODO Auto-generated method stub
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	}
	

}

