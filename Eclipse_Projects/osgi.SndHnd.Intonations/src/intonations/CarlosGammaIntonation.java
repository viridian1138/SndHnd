





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







package intonations;

import core.Intonation;

/**
 * Approximation of the Wendy Carlos gamma intonation.  This version builds the melodic
 * interval from three applications of a ( 11.733 ) * 12 cent increase (roughly halfway between a minor second and a major second).
 * This puts the total ratio for a scale step close to the quoted value
 * (see the Wikipedia article) of 35 cents.
 * 
 * See https://en.wikipedia.org/wiki/Gamma_scale
 * 
 * @author tgreen
 *
 */
public abstract class CarlosGammaIntonation extends Intonation {
	
	public static final double GAMMA_MULT_INTERVAL = Math.pow( 2.0 , 0.117333333333333333333 );
	
	public static final double GAMMA_MULT2_INTERVAL = GAMMA_MULT_INTERVAL * GAMMA_MULT_INTERVAL;
	
	/**
	 * The melodic interval ratio of the intonation.
	 */
	public static final double MELODIC_INTERVAL = GAMMA_MULT2_INTERVAL * GAMMA_MULT_INTERVAL;
	
	@Override
	public double getMelodicIntervalRatio()
	{
		return( MELODIC_INTERVAL );
	}
	
	/**
	 * Test driver.
	 */
	public static void main( String[] in  )
	{
		System.out.println( GAMMA_MULT_INTERVAL );
		System.out.println( 25.0 / 24.0 );
		System.out.println( 9.0 / 8.0 );
		System.out.println( 6.0 / 5.0 );
		System.out.println( Math.log( Math.sqrt( ( 25.0 * 9.0 ) / ( 24.0 * 8.0 ) ) ) / Math.log( 2.0 ) );
	}

	/**
	 * The square root of the melodic interval ratio.
	 */
	private final static double MSQRT = Math.sqrt( MELODIC_INTERVAL );
	
	@Override
	public double validateIntonation( final double iin , final int index )
	{
		double in = iin;
		final double vval = CarlosGammaIntonationA.calcIntonationVal( index );
		while( ( MSQRT * in ) <= vval )
		{
			in = in * MELODIC_INTERVAL;
		}
		while( ( in / MSQRT ) >= vval )
		{
			in = in / MELODIC_INTERVAL;
		}
		return( in );
	}

	
}
