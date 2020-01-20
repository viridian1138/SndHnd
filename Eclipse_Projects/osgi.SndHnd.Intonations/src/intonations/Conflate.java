





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
 * An intonation that given two input intonations (Intonation #1 and Intonation #2 )
 * returns a version of Intonation #1  where the key has been transposed to a 
 * designated pitch on the scale of Intonation #2.  As a practical matter, this means
 * that an intonation doesn't need a separate class declaration for every possible
 * key.  Instead, an intonation in the key of A can be conflated to any other key
 * (regardless of whether that key comes from a just intonation, a diatonic intonation,
 * or something else).
 * @author tgreen
 *
 */
public class Conflate extends Intonation {
	
	/**
	 * Intonation #1
	 */
	Intonation i1;
	
	/**
	 * Intonation #2
	 */
	Intonation i2;
	
	/**
	 * The index into the scale of Intonation #2  that defines the key.
	 */
	int ind;

	/**
	 * Constructs the intonation.
	 * @param _i1  Intonation #1
	 * @param _i2  Intonation #2
	 * @param _ind The index into the scale of Intonation #2  that defines the key.
	 */
	public Conflate( Intonation _i1 , Intonation _i2 , int _ind ) {
		i1 = _i1;
		i2 = _i2;
		ind = _ind;
	}

	@Override
	public double[] calcIntonation() {
		final double[] d1 = i1.calcIntonation();
		final double[] d2 = i2.calcIntonation();
		if( d1.length != d2.length )
		{
			throw( new RuntimeException( "Bad." ) );
		}
		final double[] ret = new double[ d1.length ];
		final int DL = d1.length - 1;
		final double dr = d2[ ind ] / d1[ 0 ];
		int count;
		for( count = 0 ; count < DL ; count++ )
		{
			final int ini = ( count + ind ) % DL;
			final double dii = d1[ count ] * dr;
			final double diii = i1.validateIntonation( dii, ini);
			ret[ ini ] = diii;
		}
		ret[ DL ] = ( getMelodicIntervalRatio() ) * ret[ 0 ];
		return( ret );
	}

	@Override
	public GIntonation genInton(HashMap s) {
		if( s.get( this ) != null )
		{
			return( (GIntonation)( s.get( this ) ) );
		}
		
		GConflate wv = new GConflate();
		wv.setI1( i1.genInton(s) );
		wv.setI2( i2.genInton(s) );
		wv.setInd( ind );
		
		s.put(this, wv);
		return( wv );
	}
	
	@Override
	public double getMelodicIntervalRatio()
	{
		return( i1.getMelodicIntervalRatio() );
	}
	
	@Override
	public double validateIntonation( final double iin , final int index )
	{
			return( i1.validateIntonation(iin, index) );
	}

	
}

