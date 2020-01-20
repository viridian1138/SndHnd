





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

import greditinton.GIntonation;

import java.util.HashMap;

/**
 * Approximate diatonic intonation for the Wendy Carlos 15-tone scale in the key of A.
 * 
 * @author tgreen
 *
 */
public class DiatonicCarlosIntonation15A extends CarlosIntonation15 {
	
	/**
	 * Constructs the intonation.
	 */
	public DiatonicCarlosIntonation15A()
	{
	}

	@Override
	public double[] calcIntonation() {
		final double[] ret = new double[ 16 ];
		int count;
		for( count = 0 ; count < 16 ; count++ )
		{
			ret[ count ] = calcIntonationVal( count );
		}
		return( ret );
	}
	
	/**
	 * Returns the pitch ratio for a particular number of Carlos intonation steps.
	 * @param numSteps The number of Carlos intonation steps.
	 * @return The pitch ratio.
	 */
	public static double calcIntonationVal( final int numSteps )
	{
		final double delta = numSteps / 15.0;
		
		final double inton = Math.pow( 2.0 , delta );
		
		return( inton );
	}

	@Override
	public GIntonation genInton(HashMap s) {
		if( s.get( this ) != null )
		{
			return( (GIntonation)( s.get( this ) ) );
		}
		
		GDiatonicCarlosIntonation15A wv = new GDiatonicCarlosIntonation15A();
		s.put(this, wv);
		return( wv );
	}

	
}

