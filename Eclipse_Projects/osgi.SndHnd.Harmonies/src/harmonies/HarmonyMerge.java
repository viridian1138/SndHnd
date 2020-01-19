





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
import java.util.HashSet;
import java.util.Vector;

import core.Harmony;

/**
 * Harmony that is a merge of the pitches from two input harmonies.
 * 
 * @author tgreen
 *
 */
public class HarmonyMerge extends Harmony {
	
	/**
	 * Input harmony to be merged.
	 */
	Harmony i1;
	
	/**
	 * Input harmony to be merged.
	 */
	Harmony i2;

	/**
	 * Constructs the harmony.
	 * @param _i1 Input harmony to be merged.
	 * @param _i2 Input harmony to be merged.
	 */
	public HarmonyMerge( Harmony _i1 , Harmony _i2 ) {
		i1 = _i1;
		i2 = _i2;
	}

	
	@Override 
	public double[] calcHarmony() {
		
		double[] h1 = i1.calcHarmony();
		double[] h2 = i2.calcHarmony();
		HashSet<Double> cache = new HashSet<Double>();
		
		Vector<Double> vct = new Vector<Double>();
		int count;
		for( count = 0 ; count < h1.length ; count++ )
		{
			vct.add( h1[ count ] );
			cache.add( h1[ count ] );
		}
		
		
		for( count = 1 ; count < h2.length ; count++ )
		{
			if( !( cache.contains( h2[ count ] ) ) )
			{
				vct.add( h2[ count ] );
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
		
		GHarmonyMerge wv = new GHarmonyMerge();
		wv.setI1( i1.genHarmony(s) );
		wv.setI2( i2.genHarmony(s) );
		
		s.put(this, wv);
		return( wv );
	}

}

