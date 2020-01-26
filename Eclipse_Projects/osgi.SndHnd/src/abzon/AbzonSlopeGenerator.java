




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
 * Abstract class for estimating (or is that generating?) the interpolation-point slopes of a piecewise curve given the interpolation points.
 * 
 * @author tgreen
 *
 */
public abstract class AbzonSlopeGenerator {
	
	/**
	 * The interpolation points of a piecewise cubic curve.
	 */
	protected Vector<? extends AazonVect> interp;
	
	/**
	 * Constructs the class.
	 * @param _interp The interpolation points of a piecewise cubic curve.
	 */
	public AbzonSlopeGenerator( Vector<? extends AazonVect> _interp )
	{
		interp = _interp;
	}
	
	/**
	 * Calculates the slopes at the interpolation points.
	 * @return The slopes at the interpolation points.
	 */
	public AazonDbl[] calcSlopes()
	{
		final int sz = interp.size();
		AazonDbl[] slopes = new AazonDbl[ sz ];
		int count;
		for( count = 0 ; count < sz ; count++ )
		{
			slopes[ count ] = calcSlope( count );
		}
		return( slopes );
	}
	
	/**
	 * Calculates the slope at an interpolation point.
	 * @param index The index of the interpolation point.
	 * @return The slope at the interpolation point.
	 */
	protected AazonDbl calcSlope( int index )
	{
		if( index == 0 )
		{
			AazonVect vct0 = interp.get( 0 );
			AazonVect vct1 = interp.get( 1 );
			return( calcInitialSlope( vct0 , vct1 ) );
		}
		final int sz = interp.size();
		if( index == ( sz - 1 ) )
		{
			AazonVect vct0 = interp.get( sz - 2 );
			AazonVect vct1 = interp.get( sz - 1 );
			return( calcInitialSlope( vct0 , vct1 ) );
		}
		AazonVect vct0 = interp.get( index - 1 );
		AazonVect vct1 = interp.get( index );
		AazonVect vct2 = interp.get( index + 1 );
		return( calcMidSlope( vct0 , vct1 , vct2 ) );
	}
	
	/**
	 * Calculates the slope at the initial interpolation point.
	 * @param vct0 The vector to the initial interpolation point.
	 * @param vct1 The vector to the second interpolation point.
	 * @return The calculated slope.
	 */
	protected abstract AazonDbl calcInitialSlope( AazonVect vct0 , AazonVect vct1 );
	
	/**
	 * Calculates the slope at the final interpolation point.
	 * @param vct0 The vector to the second-to-last interpolation point.
	 * @param vct1 The vector to the final interpolation point.
	 * @return The calculated slope.
	 */
	protected abstract AazonDbl calcFinalSlope( AazonVect vct0 , AazonVect vct1 );
	
	/**
	 * Calculates the slope at an interpolation point that is neither the first nor the last.
	 * @param vct0 The vector for the interpolation point before the one for which to calculate the slope.
	 * @param vct1 The vector for the interpolation point for which to calculate the slope.
	 * @param vct2 The vector for the interpolation point after the one for which to calculate the slope.
	 * @return The calculated slope.
	 */
	protected abstract AazonDbl calcMidSlope( AazonVect vct0 , AazonVect vct1 , AazonVect vct2 );

}

