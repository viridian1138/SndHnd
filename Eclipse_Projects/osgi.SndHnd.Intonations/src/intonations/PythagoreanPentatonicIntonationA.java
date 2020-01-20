





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
 * A pythagorean pentatonic intonation in the key of A.
 * @author tgreen
 *
 */
public class PythagoreanPentatonicIntonationA extends PentatonicIntonation {

	public static final double TUNE_0 = 1.0;
	
	public static final double TUNE_1 = 9.0 / 8.0;
	
	public static final double TUNE_2 = 81.0 / 64.0;

	public static final double TUNE_3 = 3.0 / 2.0;
	
	public static final double TUNE_4 = 27.0 / 16.0;
	
	public static final double TUNE_5 = 2.0;
	
	/**
	 * Constructs the intonation.
	 */
	public PythagoreanPentatonicIntonationA()
	{
	}

	@Override
	public double[] calcIntonation() {
		final double[] ret = {
				TUNE_0 , TUNE_1 , TUNE_2 , TUNE_3 , TUNE_4 ,
				TUNE_5 };
		return( ret );
	}
	
	/**
	 * Returns the pitch ratio for a particular number of pentatonic intonation steps.
	 * @param numSteps The number of pentatonic intonation steps.
	 * @return The pitch ratio.
	 */
	public static double calcIntonationVal( final int numSteps )
	{
		final double[] ret = {
				TUNE_0 , TUNE_1 , TUNE_2 , TUNE_3 , TUNE_4 ,
				TUNE_5 };
		return( ret[ numSteps ] );
	}

	@Override
	public GIntonation genInton(HashMap s) {
		if( s.get( this ) != null )
		{
			return( (GIntonation)( s.get( this ) ) );
		}
		
		GPythagoreanPentatonicIntonationA wv = new GPythagoreanPentatonicIntonationA();
		s.put(this, wv);
		return( wv );
	}

	
}

