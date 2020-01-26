




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







package aazon.dbl;

import aazon.vect.AazonBaseImmutableVect;
import aazon.vect.AazonImmutableVect;


/**
 * Constructs an Aazon immutable double representing the slope for an interpolation point that is neither the first nor the last given immutable input vectors.
 * 
 * @author tgreen
 *
 */
public class AazonImmutablePiecewiseMonotoneSlopeDbl extends AazonImmutableDbl {
	
	/**
	 * The point before the point for which to evaluate the slope.
	 */
	protected AazonImmutableVect p0;
	
	/**
	 * The point for which to evaluate the slope.
	 */
	protected AazonImmutableVect p1;
	
	/**
	 * The point after the point for which to evaluate the slope.
	 */
	protected AazonImmutableVect p2;
	
	/**
	 * Private constructor.
	 * @param _p0 The point before the point for which to evaluate the slope.
	 * @param _p1 The point for which to evaluate the slope.
	 * @param _p2 The point after the point for which to evaluate the slope.
	 */
	private AazonImmutablePiecewiseMonotoneSlopeDbl( AazonImmutableVect _p0 , AazonImmutableVect _p1 , AazonImmutableVect _p2 )
	{
		p0 = _p0;
		p1 = _p1;
		p2 = _p2;
	}
	
	/**
	 * Constructs an Aazon immutable double representing the slope for an interpolation point that is neither the first nor the last given immutable input vectors.
	 * @param _p0 The point before the point for which to evaluate the slope.
	 * @param _p1 The point for which to evaluate the slope.
	 * @param _p2 The point after the point for which to evaluate the slope.
	 * @return The constructed double.
	 */
	public static AazonImmutableDbl construct( AazonImmutableVect _p0 , AazonImmutableVect _p1 , AazonImmutableVect _p2 )
	{
		if( ( _p0 instanceof AazonBaseImmutableVect ) && ( _p1 instanceof AazonBaseImmutableVect ) && ( _p2 instanceof AazonBaseImmutableVect ) )
		{
			final double slope0 = ( _p1.getY() - _p0.getY() ) / ( _p1.getX() - _p0.getX() );
			final double slope1 = ( _p2.getY() - _p1.getY() ) / ( _p2.getX() - _p1.getX() );
			
			// System.out.println( "&&&&&&&&&&&&&" );
			// System.out.println( slope0 );
			// System.out.println( slope1 );
			
			if( ( slope0 * slope1 ) <= 0.0 )
			{
				// System.out.println( "Returned Zero." );
				return( new AazonBaseImmutableDbl( 0.0 ) );
			}
			
			final double rawSlope = ( slope0 + slope1 ) / 2.0;
			final double absRawSlope = Math.abs( rawSlope );
			
			final double absRawSlope1 = chkRawSlope( _p0 , _p1 , absRawSlope );
			final double absRawSlope2 = chkRawSlope( _p1 , _p2 , absRawSlope1 );
			
			double slope = absRawSlope2;
			if( rawSlope < 0.0 )
			{
				slope = -slope;
			}
			
			// System.out.println( "###" );
			// System.out.println( slope );
			
			return( new AazonBaseImmutableDbl( slope ) );
		}
		return( new AazonImmutablePiecewiseMonotoneSlopeDbl( _p0 , _p1 , _p2 ) );
	}
	
	
	/**
	 * Checks the input slope and applies corrections for monotonicity constraints.
	 * @param p1 First point to use for the monotonicity verification.
	 * @param p2 Second point to use for the monotonicity verification.
	 * @param absRawSlope The previously calculated slope.
	 * @return The slope corrected for monotonicity constraints.
	 */
	protected static double chkRawSlope( AazonImmutableVect p1 , AazonImmutableVect p2 , double absRawSlope )
	{	
		final double ptMaxRise = 3.0 * ( p2.getY() - p1.getY() );
		final double ptRun = p2.getX() - p1.getX();
		
		final double maxAbsSlope = Math.abs( ptMaxRise / ptRun );
		
	//	System.out.println( "***" );
	//	System.out.println( index );
	//	System.out.println( absRawSlope );
	//	System.out.println( maxAbsSlope );
		
		return( Math.min( absRawSlope , maxAbsSlope ) );
	}
	
	
	/**
	 * Gets the slope estimate before applying monotonicity constraints.
	 * @return The slope estimate before applying monotonicity constraints.
	 */
	protected double getSlopeComp( )
	{	
		final double slope0 = ( p1.getY() - p0.getY() ) / ( p1.getX() - p0.getX() );
		final double slope1 = ( p2.getY() - p1.getY() ) / ( p2.getX() - p1.getX() );
		
		// System.out.println( "&&&&&&&&&&&&&" );
		// System.out.println( slope0 );
		// System.out.println( slope1 );
		
		if( ( slope0 * slope1 ) <= 0.0 )
		{
			// System.out.println( "Returned Zero." );
			return( 0.0 );
		}
		
		final double rawSlope = ( slope0 + slope1 ) / 2.0;
		final double absRawSlope = Math.abs( rawSlope );
		
		final double absRawSlope1 = chkRawSlope( p0 , p1 , absRawSlope );
		final double absRawSlope2 = chkRawSlope( p1 , p2 , absRawSlope1 );
		
		double slope = absRawSlope2;
		if( rawSlope < 0.0 )
		{
			slope = -slope;
		}
		
		// System.out.println( "###" );
		// System.out.println( slope );
		
		return( slope );
	}

	@Override
	public double getX() {
		return( getSlopeComp() );
	}

	
}

