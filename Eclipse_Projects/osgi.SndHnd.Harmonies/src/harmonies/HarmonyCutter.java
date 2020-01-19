





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







package harmonies;

import greditharmon.GHarmony;

import java.util.HashMap;
import java.util.Vector;

import core.Harmony;

/**
 * Harmony for removing some of the pitches from an input harmony.
 * 
 * @author tgreen
 *
 */
public class HarmonyCutter extends Harmony {
	
	/**
	 * The input harmony.
	 */
	Harmony i1;
	
	/**
	 * String indicating which pitches are to be removed.
	 */
	String ind;
	
	/**
	 * Array of booleans indicating which pitches are to be removed.
	 */
	boolean[] cuts;

	/**
	 * Constructs the harmony.
	 * @param _i1 The input harmony.
	 * @param _ind String indicating which pitches are to be removed.
	 * @param _cuts Array of booleans indicating which pitches are to be removed.
	 */
	public HarmonyCutter( Harmony _i1 , String _ind , boolean[] _cuts ) {
		i1 = _i1;
		ind = _ind;
		cuts = _cuts;
	}

	@Override
	public double[] calcHarmony() {
		
		double[] h1 = i1.calcHarmony();
		
		Vector<Double> vct = new Vector<Double>();
		int count;
		for( count = 0 ; count < cuts.length ; count++ )
		{
			if( cuts[ count ] )
			{
				vct.add( h1[ count ] );
			}
		}
		
		double[] ret = new double[ vct.size() ];
		for( count = 0 ; count < vct.size() ; count++ )
		{
			ret[ count ] = vct.elementAt( count );
		}
		
		return( ret );
	}
	
	@Override
	public GHarmony genHarmony( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GHarmony)( s.get( this ) ) );
		}
		
		GHarmonyCutter wv = new GHarmonyCutter();
		wv.setI1( i1.genHarmony(s) );
		wv.setInd( ind );
		
		s.put(this, wv);
		return( wv );
	}

}

