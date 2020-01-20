





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
 * A diatonic quartertone intonation in the key of A.
 * @author tgreen
 *
 */
public class DiatonicQuartertoneIntonationA extends QuartertoneIntonation {
	
	/**
	 * Constructs the intonation.
	 */
	public DiatonicQuartertoneIntonationA()
	{
	}

	@Override
	public double[] calcIntonation() {
		final double[] ret = new double[ 25 ];
		int count;
		for( count = 0 ; count < 25 ; count++ )
		{
			ret[ count ] = calcIntonationVal( count );
		}
		return( ret );
	}
	
	/**
	 * Returns the pitch ratio for a particular number of quartertone steps.
	 * @param numSteps The number of quartertone steps.
	 * @return The pitch ratio.
	 */
	public static double calcIntonationVal( final int numSteps )
	{
		final double delta = numSteps / 24.0;
		
		final double inton = Math.pow( 2.0 , delta );
		
		return( inton );
	}

	@Override
	public GIntonation genInton(HashMap s) {
		if( s.get( this ) != null )
		{
			return( (GIntonation)( s.get( this ) ) );
		}
		
		GDiatonicQuartertoneIntonationA wv = new GDiatonicQuartertoneIntonationA();
		s.put(this, wv);
		return( wv );
	}

	
}

