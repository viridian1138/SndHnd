





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
 * Approximate intonation for the Wendy Carlos 15-tone scale.
 * 
 * @author tgreen
 *
 */
public abstract class CarlosIntonation15 extends Intonation {
	
	/**
	 * The square root of the melodic interval ratio.
	 */
	private final static double MSQRT = Math.sqrt( 2.0 );
	
	@Override
	public double getMelodicIntervalRatio()
	{
		return( 2.0 );
	}
	
	@Override
	public double validateIntonation( final double iin , final int index )
	{
		double in = iin;
		final double vval = DiatonicCarlosIntonation15A.calcIntonationVal( index );
		while( ( MSQRT * in ) <= vval )
		{
			in = in * 2.0;
		}
		while( ( in / MSQRT ) >= vval )
		{
			in = in / 2.0;
		}
		return( in );
	}

	
}

