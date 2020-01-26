




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

import aazon.AazonListener;
import aazon.vect.AazonImmutableVect;
import aazon.vect.AazonMutableVect;
import aazon.vect.AazonVect;


/**
 * Constructs an Aazon mutable double representing the slope for an interpolation point that is neither the first nor the last given mutable input vectors.
 * 
 * @author tgreen
 *
 */
public class AazonMutablePiecewiseMonotoneSlopeDbl extends AazonMutableDbl implements AazonListener {
	
	/**
	 * The point before the point for which to evaluate the slope.
	 */
	protected AazonVect p0;
	
	/**
	 * The point for which to evaluate the slope.
	 */
	protected AazonVect p1;
	
	/**
	 * The point after the point for which to evaluate the slope.
	 */
	protected AazonVect p2;
	
	/**
	 * Private constructor.
	 * @param _p0 The point before the point for which to evaluate the slope.
	 * @param _p1 The point for which to evaluate the slope.
	 * @param _p2 The point after the point for which to evaluate the slope.
	 */
	private AazonMutablePiecewiseMonotoneSlopeDbl( AazonVect _p0 , AazonVect _p1 , AazonVect _p2 )
	{
		p0 = _p0;
		p1 = _p1;
		p2 = _p2;
		
		if( p0 instanceof AazonMutableVect )
		{
			( (AazonMutableVect) p0 ).add( this );
		}
		
		if( p1 instanceof AazonMutableVect )
		{
			( (AazonMutableVect) p1 ).add( this );
		}
		
		if( p2 instanceof AazonMutableVect )
		{
			( (AazonMutableVect) p2 ).add( this );
		}
	}
	
	/**
	 * Constructs an Aazon double representing the slope for an interpolation point that is neither the first nor the last.
	 * @param _p0 The point before the point for which to evaluate the slope.
	 * @param _p1 The point for which to evaluate the slope.
	 * @param _p2 The point after the point for which to evaluate the slope.
	 * @return The constructed double.
	 */
	public static AazonDbl construct( AazonVect _p0 , AazonVect _p1 , AazonVect _p2 )
	{
		if( ( _p0 instanceof AazonImmutableVect ) && ( _p1 instanceof AazonImmutableVect ) && ( _p2 instanceof AazonImmutableVect ) )
		{
			return( AazonImmutablePiecewiseMonotoneSlopeDbl.construct( (AazonImmutableVect) _p0 , (AazonImmutableVect) _p1 , (AazonImmutableVect) _p2 ) );
		}
		return( new AazonMutablePiecewiseMonotoneSlopeDbl( _p0 , _p1 , _p2 ) );
	}

	
	/**
	 * Checks the input slope and applies corrections for monotonicity constraints.
	 * @param p1 First point to use for the monotonicity verification.
	 * @param p2 Second point to use for the monotonicity verification.
	 * @param absRawSlope The previously calculated slope.
	 * @return The slope corrected for monotonicity constraints.
	 */
	protected static double chkRawSlope( AazonVect p1 , AazonVect p2 , double absRawSlope )
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
	
	/**
	 * Handles a change in one of the arguments by firing listeners.
	 */
	public void handleListen()
	{
		fire();
	}

	
}

