





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
import core.Intonation;

/**
 * A harmony that stacks pitches from an input harmony upward to a 
 * successive melodic interval.  An example of this would be
 * octave stacking on a typical intonation using octaves.
 * 
 * @author tgreen
 *
 */
public class HarmonyMelodicIntervalDoubling extends Harmony {
	
	/**
	 * The input harmony to be stacked.
	 */
	private Harmony i1;
	
	/**
	 * The input intonation from which to get the melodic interval ratio.
	 */
	private Intonation i2;
	
	/**
	 * String listing which indices of the harmony should be used.
	 */
	private String ind;
	
	/**
	 * The number of melodic intervals to jump upward when stacking.
	 */
	private int noteMelodicInterval;
	
	/**
	 * The pitch ratios from the input harmony to be used.
	 */
	private boolean[] mults;

	/**
	 * Constructs the harmony.
	 * @param _i1 The input harmony to be stacked.
	 * @param _i2 The input intonation from which to get the melodic interval ratio.
	 * @param _ind String listing which indices of the harmony should be used.
	 * @param _noteMelodicInterval The number of melodic intervals to jump upward when stacking.
	 * @param _mults The pitch ratios from the input harmony to be used.
	 */
	public HarmonyMelodicIntervalDoubling( Harmony _i1 , Intonation _i2 , String _ind , int _noteMelodicInterval , boolean[] _mults ) {
		i1 = _i1;
		i2 = _i2;
		ind = _ind;
		noteMelodicInterval = _noteMelodicInterval;
		mults = _mults;
	}

	@Override
	public double[] calcHarmony() {
		
		double[] h1 = i1.calcHarmony();
		final double melr = i2.getMelodicIntervalRatio();
		final double mult = Math.pow( melr , noteMelodicInterval );
		HashSet<Double> cache = new HashSet<Double>();
		
		Vector<Double> vct = new Vector<Double>();
		int count;
		for( count = 0 ; count < h1.length ; count++ )
		{
			vct.add( h1[ count ] );
			cache.add( h1[ count ] );
		}
		
		
		for( count = 0 ; count < mults.length ; count++ )
		{
			if( mults[ count ] )
			{
				final double hm = h1[ count ] * mult;
				if( !( cache.contains( hm ) ) )
				{
					vct.add( hm );
				}
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
		
		GHarmonyMelodicIntervalDoubling wv = new GHarmonyMelodicIntervalDoubling();
		wv.setI1( i1.genHarmony(s) );
		wv.setI2( i2.genInton(s) );
		wv.setInd( ind );
		wv.setNoteMelodicInterval(noteMelodicInterval);
		
		s.put(this, wv);
		return( wv );
	}

}

