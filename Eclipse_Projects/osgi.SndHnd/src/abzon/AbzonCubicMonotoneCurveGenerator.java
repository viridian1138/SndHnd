




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
import aazon.vect.AazonMutableSlopeAddVect;
import aazon.vect.AazonMutableSlopeSubVect;
import aazon.vect.AazonVect;

/**
 * Abzon curve generator for a monotone cubic functional curve.
 * 
 * @author tgreen
 *
 */
public class AbzonCubicMonotoneCurveGenerator extends
		AbzonFunctionalCurveGenerator {

	/**
	 * Constructs the class.
	 * @param interps The interpolation points.
	 * @param generator The curve generator.
	 */
	public AbzonCubicMonotoneCurveGenerator(Vector<? extends AazonVect> interps,
			AbzonSlopeGenerator generator) {
		super(interps, generator);
	}

	@Override
	protected AbzonPathIteratorFactory generateSegmentCurve(int index , AazonDbl[] slopes ) {
		AazonVect b0 = interps.get(index);
		AazonVect b3 = interps.get(index+1);
		AazonVect b1 = AazonMutableSlopeAddVect.construct( b0 , slopes[ index ] , b3 );
		AazonVect b2 = AazonMutableSlopeSubVect.construct( b0 , slopes[ index + 1 ] , b3 );
		AbzonPathIteratorFactory fac = AbzonSmartCubicCurveFactory.construct( b0, b1, b2, b3 );
		return( fac );
	}

	
}

