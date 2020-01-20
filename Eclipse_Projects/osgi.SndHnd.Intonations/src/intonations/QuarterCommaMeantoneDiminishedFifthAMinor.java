





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

import core.WesternIntonation;

/**
 * A quarter comma meantone diminished fifth intonation for a western 12-tone scale in the key of A-Minor.
 * 
 * See https://www.revolvy.com/main/index.php?s=Quarter-comma-meantone&item_type=topic
 * 
 * @author tgreen
 *
 */
public class QuarterCommaMeantoneDiminishedFifthAMinor extends WesternIntonation {
	
	public static final double TUNE_0 = 1.0;
	
	public static final double TUNE_1 = 1.0700;
	
	public static final double TUNE_2 = 1.1180;

	public static final double TUNE_3 = 1.1963;
	
	public static final double TUNE_4 = 1.2500;
	
	public static final double TUNE_5 = 1.3375;
	
	public static final double TUNE_6 = 1.4311;
	
	public static final double TUNE_7 = 1.4953;
	
	public static final double TUNE_8 = 1.6000;
	
	public static final double TUNE_9 = 1.6719;
	
	public static final double TUNE_10 = 1.7889;
	
	public static final double TUNE_11 = 1.8692;
	
	public static final double TUNE_12 = 2.0;
	
	/**
	 * Constructs the intonation.
	 */
	public QuarterCommaMeantoneDiminishedFifthAMinor()
	{
	}

	@Override
	public double[] calcIntonation() {
		final double[] ret = {
				TUNE_0 , TUNE_1 , TUNE_2 , TUNE_3 , TUNE_4 ,
				TUNE_5 , TUNE_6 , TUNE_7 , TUNE_8 , TUNE_9 ,
				TUNE_10 , TUNE_11 , TUNE_12 };
		return( ret );
	}

	@Override
	public GIntonation genInton(HashMap s) {
		if( s.get( this ) != null )
		{
			return( (GIntonation)( s.get( this ) ) );
		}
		
		GJustIntonationAMinor wv = new GJustIntonationAMinor();
		s.put(this, wv);
		return( wv );
	}

	
}

