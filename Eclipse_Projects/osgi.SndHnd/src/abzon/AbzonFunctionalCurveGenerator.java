




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







package abzon;

import java.util.Vector;

import aazon.dbl.AazonDbl;
import aazon.vect.AazonVect;

/**
 * Generates the piecewise curve segments for a functional curve given the interpolation points.
 * 
 * @author tgreen
 *
 */
public abstract class AbzonFunctionalCurveGenerator {
	
	/**
	 * The interpolation points.
	 */
	protected Vector<? extends AazonVect> interps;
	
	/**
	 * The slope generator for producing the slopes.
	 */
	protected AbzonSlopeGenerator generator;
	
	/**
	 * Constructs the class.
	 * @param _interps The interpolation points.
	 * @param _generator The slope generator for producing the slopes.
	 */
	public AbzonFunctionalCurveGenerator( Vector<? extends AazonVect> _interps , AbzonSlopeGenerator _generator )
	{
		interps = _interps;
		generator = _generator;
	}
	
	/**
	 * Generates one segment of the piecewise curve.
	 * @param index The index of the segment.
	 * @param slopes The slopes at the interpolation points.
	 * @return The path iterator factory for the curve segment.
	 */
	protected abstract AbzonPathIteratorFactory generateSegmentCurve(int index , AazonDbl[] slopes );
	
	/**
	 * Generates the curve segments of the piecewise curve.
	 * @return The curve segments of the piecewise curve.
	 */
	public AbzonPathIteratorFactory[] generateCurves()
	{
		final int sz = interps.size();
		AbzonPathIteratorFactory[] ret = new AbzonPathIteratorFactory[ sz - 1 ];
		AazonDbl[] slopes = generator.calcSlopes();
		int count;
		for( count = 0 ; count < ( sz - 1 ) ; count++ )
		{
			ret[ count ] = generateSegmentCurve( count , slopes );
		}
		return( ret );
	}

	
}

