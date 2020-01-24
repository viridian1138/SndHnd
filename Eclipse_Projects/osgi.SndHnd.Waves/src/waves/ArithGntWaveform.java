





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
 * 
 * A waveform that returns w ^ ( 2 ^ ( m + 1 ) ) where "w" is the value from the input waveform and "m" is the maximum iteration count.
 * 
 * @author tgreen
 *
 */
public class ArithGntWaveform extends WaveForm {
	
	/**
	 * The input waveform.
	 */
	private WaveForm wave;
	
	/**
	 * The maximum iteration count.
	 */
	private int max;
	
	/**
	 * Constructs the waveform.
	 * @param in The input waveform.
	 * @param _max The maximum iteration count.
	 */
	public ArithGntWaveform( WaveForm in , int _max )
	{
		wave = in;
		max = _max;
	}

	@Override
	public double eval(double p) {
		final double v = wave.eval( p );
		final double v2 = v * v;
		final int max = this.max;
		double vv = v2;
		int count;
		for( count = 0 ; count < max ; count++ )
		{
			vv = vv * vv;
		}
		return( vv );
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
			return( new ArithGntWaveform( wv , max ) );
		}
	}
	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GArithGntWaveform wv = new GArithGntWaveform();
		s.put(this, wv);
		
		GWaveForm w = wave.genWave(s);
		
		wv.load(w,max);
		
		return( wv );
	}
	
	/**
	 * Gets the maximum iteration count.
	 * @return The maximum iteration count.
	 */
	public int getMax()
	{
		return( max );
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

