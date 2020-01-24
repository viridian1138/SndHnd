





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
 * A waveform taking an input waveform to the fourth power.
 * 
 * @author tgreen
 *
 */
public class ArithQuadWaveform extends WaveForm {
	
	/**
	 * The input waveform.
	 */
	private WaveForm wave;
	
	/**
	 * Constructs the wave.
	 * @param in The input waveform.
	 */
	public ArithQuadWaveform( WaveForm in )
	{
		wave = in;
	}

	@Override
	public double eval(double p) {
		double v = wave.eval( p );
		double v2 = v * v;
		return( v2 * v2 );
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
			return( new ArithQuadWaveform( wv ) );
		}
	}
	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GArithQuadWaveform wv = new GArithQuadWaveform();
		s.put(this, wv);
		
		GWaveForm w = wave.genWave(s);
		
		wv.load(w);
		
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

