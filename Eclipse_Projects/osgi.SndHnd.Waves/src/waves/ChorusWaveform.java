





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
 * Waveform producing a simple chorus effect on an input wave.
 * 
 * See <A href="https://en.wikipedia.org/wiki/Chorus_effect">https://en.wikipedia.org/wiki/Chorus_effect</A>
 * 
 * @author tgreen
 *
 */
public class ChorusWaveform extends WaveForm {
	
	/**
	 * The input waveform.
	 */
	private WaveForm wave;
	
	/**
	 * The maximum number of chorus levels to add.
	 */
	private int max;
	
	/**
	 * The frequency increment or decrement for each chorus level.
	 */
	private double deltaPerIncr;
	
	/**
	 * Constructs the waveform.
	 * @param in  The input waveform.
	 * @param _max The maximum number of chorus levels to add.
	 * @param _deltaPerIncr The frequency increment or decrement for each chorus level.
	 */
	public ChorusWaveform( WaveForm in , int _max , double _deltaPerIncr )
	{
		wave = in;
		max = _max;
		deltaPerIncr = _deltaPerIncr;
	}

	@Override
	public double eval(double p) {
		double sum = wave.eval( p );
		final int max = this.max;
		int count;
		for( count = 0 ; count < max ; count++ )
		{
			sum += wave.eval( p * ( 1 + ( count + 1 ) * deltaPerIncr ) );
			sum += wave.eval( p * ( 1 - ( count + 1 ) * deltaPerIncr ) );
		}
		return( sum / ( 2 * max + 1 ) );
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
			return( new ChorusWaveform( wv , max , deltaPerIncr ) );
		}
	}
	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GChorusWaveform wv = new GChorusWaveform();
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

