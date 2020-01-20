





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
import java.util.Iterator;
import java.util.TreeSet;


/**
 * A minor pentatonic intonation that is similar to a just intonation, but uses
 * different pitch ratios.  A just intonation has some pitch pitch ratios with an odd number
 * in the denominator, whereas this intonation only uses denominators that are powers of 2.
 * This intonation was invented by the author.
 * 
 * @author tgreen
 *
 */
public class HarmonoidPentatonicIntonationMinor extends PentatonicIntonation {

	/**
	 * Constructs the intonation.
	 */
	public HarmonoidPentatonicIntonationMinor() {
	}

	@Override
	public double[] calcIntonation() {
		
		TreeSet<Double> tm = new TreeSet<Double>();
		
		tm.add( 1.0 );
		tm.add( 2.0 );
		
		int cnt = 3;
		while( tm.size() < 6 )
		{
			double db = cnt;
			while( db >= 2.0  )
			{
				db = db / 2.0;
			}
			tm.add( db );
			cnt += 2;
		}
		
		final double[] ret = new double[ 6 ];
		
		cnt = 0;
		Iterator<Double> it = tm.iterator();
		while( it.hasNext() )
		{
			ret[ cnt ] = it.next();
			cnt++;
		}
		
		return( ret );
	}

	@Override
	public GIntonation genInton(HashMap s) {
		if( s.get( this ) != null )
		{
			return( (GIntonation)( s.get( this ) ) );
		}
		
		GHarmonoidPentatonicIntonationMinor wv = new GHarmonoidPentatonicIntonationMinor();
		s.put(this, wv);
		return( wv );
	}

	
}

