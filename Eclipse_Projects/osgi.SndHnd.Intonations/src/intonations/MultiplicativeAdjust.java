





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

import core.Intonation;

/**
 * An intonation for multiplicatively adjusting the key of an input intonation by a pitch ratio.
 * @author tgreen
 *
 */
public class MultiplicativeAdjust extends Intonation {
	
	/**
	 * The input intonation.
	 */
	Intonation i1;
	
	/**
	 * The multiplier for the pitch ratio.
	 */
	double multiplier;

	/**
	 * Constructs the intonation.
	 * @param _i1 The input intonation.
	 * @param _multiplier The multiplier for the pitch ratio.
	 */
	public MultiplicativeAdjust( Intonation _i1 , double _multiplier ) {
		i1 = _i1;
		multiplier = _multiplier;
	}
	
	@Override
	public double getMelodicIntervalRatio()
	{
		return( i1.getMelodicIntervalRatio() );
	}

	@Override
	public double[] calcIntonation() {
		final double[] d1 = i1.calcIntonation();
		final double[] ret = new double[ d1.length ];
		int count;
		for( count = 0 ; count < d1.length ; count++ )
		{
			final double dii = d1[ count ] * multiplier;
			final double diii = i1.validateIntonation( dii, count);
			ret[ count ] = diii;
		}
		ret[ d1.length - 1 ] = ( getMelodicIntervalRatio() ) * ret[ 0 ];
		return( ret );
	}

	@Override
	public GIntonation genInton(HashMap s) {
		if( s.get( this ) != null )
		{
			return( (GIntonation)( s.get( this ) ) );
		}
		
		GMultiplicativeAdjust wv = new GMultiplicativeAdjust();
		wv.setI1( i1.genInton(s) );
		wv.setMultiplier( multiplier );
		
		s.put(this, wv);
		return( wv );
	}
	
	@Override
	public double validateIntonation( final double iin , final int index )
	{
			return( i1.validateIntonation(iin, index) );
	}

	
}

