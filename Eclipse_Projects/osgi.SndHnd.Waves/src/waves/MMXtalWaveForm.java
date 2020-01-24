





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

public class MMXtalWaveForm extends WaveForm {
	
	public static double MULT = SincWaveform.MULT;
	
	public static double INV_MULT = 1.0 / SincWaveform.MULT;
	
	private MultiXtalWaveform sw;

	public MMXtalWaveForm( MultiXtalWaveform _sw ) {
		sw = _sw;
	}
	
	final double evalTransition( final double p )
	{
		final double p2 = 1.0 - p;
		
		final double d0 = 0.0;
		final double d1 = 0.0;
		final double d2 = 1.0;
		final double d3 = 1.0;
		
		final double d01 = p2 * d0 + p * d1;
		final double d02 = p2 * d1 + p * d2;
		final double d03 = p2 * d2 + p * d3;
		
		final double d11 = p2 * d01 + p * d02;
		final double d12 = p2 * d02 + p * d03;
		
		final double d21 = p2 * d11 + p * d12;
		return( d21 );
	}
	
	private final double evalSw( double in )
	{
		return( sw.eval( in ) );
	}

	public final double eval(final double p) {
		final double p0 = Math.abs( p );
		final double p1 = evalTransition( p0 - (int) p0 );
		final double p12 = 1.0 - p1;
		final double d0 = evalSw( -1.0 + p1 );
		final double d1 = evalSw( -1.0 + p12 );
		return( p12 * d0 + p1 * d1 );
	}

	public NonClampedCoefficient genClone() throws Throwable {
		return( this );
	}
	
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GMMXtalWaveForm wv = new GMMXtalWaveForm();
		s.put(this, wv);
		
		GMultiXtalWaveform w = (GMultiXtalWaveform)( sw.genWave(s) );
		
		wv.performAssign( w );
		
		return( wv );
	}

	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		throw( new RuntimeException( "Not Supported" ) );
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		throw( new RuntimeException( "Not Supported" ) );
	}

}

