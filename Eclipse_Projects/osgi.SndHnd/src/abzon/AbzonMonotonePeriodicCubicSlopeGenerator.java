




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
import aazon.dbl.AazonMutablePiecewiseMonotoneSlopeDbl;
import aazon.vect.AazonBaseImmutableVect;
import aazon.vect.AazonMutableAdditiveVect;
import aazon.vect.AazonVect;

/**
 * Slope generator for a monotone piecewise cubic curve.
 * 
 * @author tgreen
 *
 */
public class AbzonMonotonePeriodicCubicSlopeGenerator extends
		AbzonMonotoneCubicSlopeGenerator {

	/**
	 * The parameter difference going from the last interpolation point back to the first interpolation point.
	 */
	double paramOffset;
	
	/**
	 * Constructs the class.
	 * @param interp The interpolation points.
	 * @param _paramOffset The parameter difference going from the last interpolation point back to the first interpolation point.
	 */
	public AbzonMonotonePeriodicCubicSlopeGenerator(Vector<? extends AazonVect> interp, double _paramOffset) {
		super(interp);
		paramOffset = _paramOffset;
	}
	
	@Override
	protected AazonDbl calcFinalSlope(AazonVect vct0, AazonVect vct1) {
		final int sz = interp.size();
		if( sz < 1 )
		{
			throw( new RuntimeException( "Inconsistent" ) );
		}
		AazonVect act = (AazonVect)( interp.get( 0 ) );
		AazonBaseImmutableVect offset = new AazonBaseImmutableVect( paramOffset , 0.0 );
		AazonVect rct = AazonMutableAdditiveVect.construct( act , offset );
		return( AazonMutablePiecewiseMonotoneSlopeDbl.construct( vct0 , vct1 , rct ) );
	}

	@Override
	protected AazonDbl calcInitialSlope(AazonVect vct0, AazonVect vct1) {
		final int sz = interp.size();
		if( sz < 1 )
		{
			throw( new RuntimeException( "Inconsistent" ) );
		}
		AazonVect act = (AazonVect)( interp.get( sz - 1 ) );
		AazonBaseImmutableVect offset = new AazonBaseImmutableVect( -paramOffset , 0.0 );
		AazonVect rct = AazonMutableAdditiveVect.construct( act , offset );
		return( AazonMutablePiecewiseMonotoneSlopeDbl.construct( rct , vct0 , vct1 ) );
	}

	
}

