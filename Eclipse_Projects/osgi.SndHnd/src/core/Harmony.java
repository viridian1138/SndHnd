




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

import greditharmon.GHarmony;

import java.util.HashMap;


/**
 * An abstract base class for a musical harmony with a certain number of parts (two-part harmony, three-part harmony, etc.).
 * Although the class is called "Harmony" it can equally well be used to create intentional dissonances 
 * (such as chords that produce suspensions).
 * 
 * See https://en.wikipedia.org/wiki/Harmony
 * 
 * Class Harmony interacts with class greditharmon.GHarmony.  The greditharmon.GHarmony class is single-threaded, mutable, and
 * editable, whereas Harmony is immutable and non-editable.
 * 
 * One sample test harmony, a basic triad, is implemented in class harmonies.TestTriad.
 * 
 * @author tgreen
 *
 */
public abstract class Harmony {
	
	/**
	 * Constructor.
	 */
	public Harmony()
	{
	}
	
	/**
	 * Calculates the harmony.
	 * @return Returns an array of pitch ratios for the ascending pitches in the harmony where the first ratio, the ratio for the tonic, is assumed to be 1.0.  Each ratio is one part of the harmony.
	 */
	public abstract double[] calcHarmony();
	
	/**
	 * Generates the corresponding GHarmony node for this harmony.
	 * @param s Map for duplicate elimination among nodes.
	 * @return The corresponding GHarmony node for this harmony.
	 */
	public abstract GHarmony genHarmony( HashMap s );

	
}


