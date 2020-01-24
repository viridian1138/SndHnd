





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
 * Waveform generating the fuzzy logic version of an exclusive or between two input waves.
 * 
 * See <A href="https://en.wikipedia.org/wiki/Fuzzy_logic">https://en.wikipedia.org/wiki/Fuzzy_logic</A>
 * 
 * @author tgreen
 *
 */
public class XorWaveform extends WaveForm {
	
	/**
	 * The first input of the exclusive or.
	 */
	protected WaveForm wave;
	
	/**
	 * The second input of the exclusive or.
	 */
	protected WaveForm wave2;

	/**
	 * Constructs the wave.
	 * @param _wave The first input of the exclusive or.
	 * @param _wave2 The second input of the exclusive or.
	 */
	public XorWaveform( WaveForm _wave , WaveForm _wave2 ) {
		super();
		wave = _wave;
		wave2 = _wave2;
	}

	@Override
	public double eval(double p ) {
		double a0 = wave.eval( p );
		double a1 = wave2.eval( p );
		return( ( 1.0 - a0 ) * a1 + ( 1.0 - a1 ) * a0 );
	}
	
	@Override
	public NonClampedCoefficient genClone() throws Throwable
	{
		final WaveForm wv = (WaveForm)( wave.genClone() );
		final WaveForm wv2 = (WaveForm)( wave2.genClone() );
		if(  ( wv == wave ) && ( wv2 == wave2 ) )
		{
			return( this );
		}
		else
		{
			return( new XorWaveform( wv , wv2 ) );
		}
	}
	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GXorWaveform wv = new GXorWaveform();
		s.put(this, wv);
		
		GWaveForm w = wave.genWave(s);
		GWaveForm w2 = wave2.genWave(s);
		
		wv.load(w,w2);
		
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

