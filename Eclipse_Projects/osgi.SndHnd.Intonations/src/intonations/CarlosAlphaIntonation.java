





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
 * An abstract base class for an approximation of the Wendy Carlos alpha intonation.  The melodic
 * interval for this intonation is built by combining three perfect minor thirds.
 * 
 * See https://en.wikipedia.org/wiki/Alpha_scale
 * 
 * @author tgreen
 *
 */
public abstract class CarlosAlphaIntonation extends Intonation {
	
	/**
	 * Pitch ratio for a perfect minor third.
	 */
	public static final double ALPHA_MULT_INTERVAL = 6.0 / 5.0;
	
	public static final double ALPHA_MULT2_INTERVAL = ALPHA_MULT_INTERVAL * ALPHA_MULT_INTERVAL;
	
	/**
	 * The melodic interval ratio of the intonation.
	 */
	public static final double MELODIC_INTERVAL = ALPHA_MULT2_INTERVAL * ALPHA_MULT_INTERVAL;
	
	@Override
	public double getMelodicIntervalRatio()
	{
		return( MELODIC_INTERVAL );
	}
	
	/**
	 * The square root of the melodic interval ratio.
	 */
	private final static double MSQRT = Math.sqrt( MELODIC_INTERVAL );
	
	@Override
	public double validateIntonation( final double iin , final int index )
	{
		double in = iin;
		final double vval = CarlosAlphaIntonationA.calcIntonationVal( index );
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
