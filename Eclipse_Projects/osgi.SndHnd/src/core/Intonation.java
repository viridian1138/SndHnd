




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







package core;

import greditinton.GIntonation;

import java.util.HashMap;

/**
 * An intonation in a potentially non-western scale.
 * 
 * @author tgreen
 *
 */
public abstract class Intonation {
	
	/**
	 * Constructs the intonation.
	 */
	public Intonation()
	{
	}
	
	/**
	 * Calculates the intonation.
	 * @return The pitch ratios on the musical scale of the intonation.
	 */
	public abstract double[] calcIntonation();
	
	/**
	 * Generates the corresponding GIntonation node for this intonation.
	 * @param s Map for duplicate elimination among nodes.
	 * @return The corresponding GIntonation node for this intonation.
	 */
	public abstract GIntonation genInton( HashMap s );
	
	/**
	 * Takes a pitch in the intonation, and validates the pitch into the correct melodic interval to be put on the scale.
	 * @param val The base pitch to be melodic-interval-shifted to produce the validated pitch to be put in the scale.
	 * @param index The index on the scale for which to produce the pitch defining the melodic interval of the scale.
	 * @return The validated pitch to be put in the scale.
	 */
	public abstract double validateIntonation( double val , int index );
	
	/**
	 * Gets the pitch ratio for the melodic interval.  For an intonation using octaves
	 * this method always returns 2.0 as its result.  For a non-octave intonation,
	 * the value can vary.  For instance, for a Wendy Carlos beta intonation,
	 * the returned value would be a ratio ( 63.8 ) * 12 cents above unison.
	 * 
	 * Note that there is a single ratio for the entire intonation.  Using only a
	 * single ratio in an intonation is mainly a conceit to simplify the 
	 * implementation of the overall system by using logarithms.
	 * 
	 * @return The pitch ratio for the melodic interval.
	 */
	public abstract double getMelodicIntervalRatio();

	
}


