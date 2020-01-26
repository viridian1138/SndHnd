




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
import aazon.vect.AazonBaseImmutableVect;
import aazon.vect.AazonMutableAdditiveVect;
import aazon.vect.AazonMutableSlopeAddVect;
import aazon.vect.AazonMutableSlopeSubVect;
import aazon.vect.AazonVect;

/**
 * Curve generator for a monotone piecewise cubic curve.
 * 
 * @author tgreen
 *
 */
public class AbzonCubicMonotonePeriodicCurveGenerator extends
		AbzonCubicMonotoneCurveGenerator {
	
	/**
	 * The parameter difference going from the last interpolation point back to the first interpolation point.
	 */
	double paramOffset;

	/**
	 * Constructs the class.
	 * @param interps The interpolation points.
	 * @param generator The curve generator.
	 * @param _paramOffset The parameter difference going from the last interpolation point back to the first interpolation point.
	 */
	public AbzonCubicMonotonePeriodicCurveGenerator(Vector<? extends AazonVect> interps,
			AbzonSlopeGenerator generator, double _paramOffset) {
		super(interps, generator);
		paramOffset = _paramOffset;
	}
	
	@Override
	public AbzonPathIteratorFactory[] generateCurves()
	{
		final int sz = interps.size();
		AbzonPathIteratorFactory[] ret = new AbzonPathIteratorFactory[ sz - 1 + 2 ];
		AazonDbl[] slopes = generator.calcSlopes();
		int count;
		for( count = 0 ; count < ( sz - 1 ) ; count++ )
		{
			ret[ count ] = generateSegmentCurve( count , slopes );
		}
		
		int idx = sz - 1;
		{
			AazonVect a0 = interps.get(sz - 1);
			AazonBaseImmutableVect offset = new AazonBaseImmutableVect( -paramOffset , 0.0 );
			AazonVect b0 = AazonMutableAdditiveVect.construct( a0 , offset );
			AazonVect b3 = interps.get(0);
			AazonVect b1 = AazonMutableSlopeAddVect.construct( b0 , slopes[ sz - 1 ] , b3 );
			AazonVect b2 = AazonMutableSlopeSubVect.construct( b0 , slopes[ 0 ] , b3 );
			ret[ idx ] = AbzonSmartCubicCurveFactory.construct( b0, b1, b2, b3 );
		}
		
		idx++;
		{
			AazonVect b0 = interps.get(sz - 1);
			AazonVect a3 = interps.get(0);
			AazonBaseImmutableVect offset = new AazonBaseImmutableVect( paramOffset , 0.0 );
			AazonVect b3 = AazonMutableAdditiveVect.construct( a3 , offset );
			AazonVect b1 = AazonMutableSlopeAddVect.construct( b0 , slopes[ sz - 1 ] , b3 );
			AazonVect b2 = AazonMutableSlopeSubVect.construct( b0 , slopes[ 0 ] , b3 );
			ret[ idx ] = AbzonSmartCubicCurveFactory.construct( b0, b1, b2, b3 );
		}
		
		
		return( ret );
	}

	
}

