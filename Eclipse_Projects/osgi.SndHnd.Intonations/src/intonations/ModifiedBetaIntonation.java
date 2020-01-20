





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
 * Modified approximation of the Wendy Carlos Beta intonation.  This version builds the melodic
 * interval from three applications of a 29 / 25 ratio (essentially a minor third).
 * 
 * See https://en.wikipedia.org/wiki/Beta_scale
 * 
 * @author tgreen
 *
 */
public abstract class ModifiedBetaIntonation extends Intonation {
	
	public static final double BETA_MULT_INTERVAL = 29.0 / 25.0;
	
	public static final double BETA_MULT2_INTERVAL = BETA_MULT_INTERVAL * BETA_MULT_INTERVAL;
	
	/**
	 * The melodic interval ratio of the intonation.
	 */
	public static final double MELODIC_INTERVAL = BETA_MULT2_INTERVAL * BETA_MULT_INTERVAL;
	
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
		System.out.println( BETA_MULT_INTERVAL );
		System.out.println( 29.0 / 25.0 );
		System.out.println( 28.0 / 25.0 );
	}
	
	// 116 / 100
	
	// 58 / 50
	
	//  29 / 25
	
	/**
	 * The square root of the melodic interval ratio.
	 */
	private final static double MSQRT = Math.sqrt( MELODIC_INTERVAL );
	
	@Override
	public double validateIntonation( final double iin , final int index )
	{
		double in = iin;
		final double vval = ModifiedBetaIntonationA.calcIntonationVal( index );
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

