





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
import greditinton.GIntonation;

import java.util.HashMap;

import core.Harmony;
import core.Intonation;

/**
 * A harmony with a stack of thirds going upward in pitch from a tonic.
 * This is mainly intended for western intonations.  If the intonation is non-Western, then this may not sound particularly good.
 * 
 * @author tgreen
 *
 */
public class HarmonyStackingThirds extends Harmony {
	
	/**
	 * The input intonation from which to determine what is a third.
	 */
	Intonation i1;
	
	/**
	 * The number of thirds to be stacked.
	 */
	int numThirds;
	
	/**
	 * The name of the key for the tonic of the harmony.
	 */
	String firstNoteKey;
	
	/**
	 * The intonation index of the tonic of the harmony.
	 */
	int firstNoteIndex;
	
	/**
	 * Array containing the number of intonation index offsets required to go up a third in whole steps.
	 */
	int[] offsetCalc;

	/**
	 * Constructs the harmony.
	 * @param _i1  The input intonation from which to determine what is a third.
	 * @param _numThirds The number of thirds to be stacked.
	 * @param _firstNoteKey  The name of the key for the tonic of the harmony.
	 * @param _firstNoteIndex  The intonation index of the tonic of the harmony.
	 * @param _offsetCalc Array containing the number of intonation index offsets required to go up a third in whole steps.
	 */
	public HarmonyStackingThirds( Intonation _i1 , int _numThirds , String _firstNoteKey , int _firstNoteIndex , int[] _offsetCalc ) {
		i1 = _i1;
		numThirds = _numThirds;
		firstNoteKey = _firstNoteKey;
		firstNoteIndex = _firstNoteIndex;
		offsetCalc = _offsetCalc;
	}

	@Override
	public double[] calcHarmony() {
		
		final double[] inton = i1.calcIntonation();
		final double firstInton = inton[ offsetCalc[ firstNoteIndex ] ];
		final double melodicIntervalRatio = i1.getMelodicIntervalRatio();
		
		double prevVal = 1.0;
		final double[] ret = new double[ numThirds + 1 ];
		int index = firstNoteIndex;
		int count;
		
		ret[ 0 ] = 1.0;
		
		for( count = 0 ; count < numThirds ; count++ )
		{
			index = ( index + 2 ) % ( offsetCalc.length );
			
			double val = inton[ offsetCalc[ index ] ] / firstInton;
			
			while( val < prevVal )
			{
				val = val * melodicIntervalRatio;
			}
			
			while( val > prevVal * melodicIntervalRatio )
			{
				val = val / melodicIntervalRatio;
			}
			
			ret[ count + 1 ] = val;
			
			prevVal = val;
		}
		
		return( ret );
	}

	@Override
	public GHarmony genHarmony(HashMap s) {
		if( s.get( this ) != null )
		{
			return( (GHarmony)( s.get( this ) ) );
		}
		
		GHarmonyStackingThirds wv = new GHarmonyStackingThirds();
		wv.setI1( (GIntonation)( i1.genInton(s) ) );
		wv.setNumThirds( numThirds );
		wv.setFirstNoteKey( firstNoteKey );
		
		s.put(this, wv);
		return( wv );
	}

}
