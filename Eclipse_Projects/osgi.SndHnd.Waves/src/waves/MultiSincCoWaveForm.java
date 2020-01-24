





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
 * Waveform composed of multiple sinc-co functions that is periodic.
 * 
 * @author tgreen
 *
 */
public class MultiSincCoWaveForm extends WaveForm {
	
	/**
	 * The multiplicative correction term from the SincCoWaveform class.
	 */
	public static double MULT = SincCoWaveform.MULT;
	
	/**
	 * The inverse of the multiplicative correction term from the SincCoWaveform class.
	 */
	public static double INV_MULT = 1.0 / SincCoWaveform.MULT;
	
	/**
	 * SincCoWaveform instance used to calculate the overall waveform.
	 */
	private SincCoWaveform sw;
	
	/**
	 * The relative frequency of the underlying sinc-co functions.
	 */
	private double mult;
	
	/**
	 * The notch height of the wave combination.
	 */
	private double notchHeight;

	/**
	 * Constructs the waveform.
	 * @param _mult The relative frequency of the underlying sinc-co functions.
	 * @param _notchHeight The notch height of the wave combination.
	 */
	public MultiSincCoWaveForm( double _mult , double _notchHeight ) {
		mult = _mult;
		sw = new SincCoWaveform( 1.0 /* _notchHeight */ );
		notchHeight = _notchHeight;
	}
	
	/**
	 * Given the actual input parameter between 0 and 1, returns a parameter for a smooth parameter transition where the parameter is C1 at the center of each sinc-co function.
	 * @param p The input actual parameter.
	 * @return The calculated smooth transition parameter.
	 */
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
	
	/**
	 * Returns the evaluation of the sinc-co function plus sinc-co features in the notch.
	 * @param in The paremeter at which to evaluate.
	 * @return The result of the evaluation/
	 */
	private final double evalSw( double in )
	{
		return( sw.eval( in ) 
				- ( 1.0 - notchHeight ) * sw.eval( ( 1000.0 / 4.0 ) * in ) 
				+ ( 1.0 - notchHeight ) *  ( 1.0 - notchHeight ) *  sw.eval( ( 1000.0 / 4.0 ) * ( 1000.0 / 4.0 ) * in )
				- ( 1.0 - notchHeight ) *  ( 1.0 - notchHeight ) * ( 1.0 - notchHeight ) *  sw.eval( ( 1000.0 / 4.0 ) * ( 1000.0 / 4.0 ) * ( 1000.0 / 4.0 ) * in ) );
	}

	@Override
	public final double eval(final double p) {
		final double p0 = Math.abs( p );
		final double p1 = evalTransition( p0 - (int) p0 );
		final double p12 = 1.0 - p1;
		final double d0 = evalSw( mult * p1 );
		final double d1 = evalSw( mult * p12 );
		return( p12 * d0 + p1 * d1 );
	}

	@Override
	public NonClampedCoefficient genClone() throws Throwable {
		return( this );
	}
	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GMultiSincCoWaveForm wv = new GMultiSincCoWaveForm();
		s.put(this, wv);
		wv.load(mult,notchHeight);
		return( wv );
	}

	/**
	 * TBD.
	 */
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		throw( new RuntimeException( "" ) );
		// TODO Auto-generated method stub

	}

	/**
	 * TBD.
	 */
	public void writeExternal(ObjectOutput out) throws IOException {
		throw( new RuntimeException( "" ) );
		// TODO Auto-generated method stub

	}

}

