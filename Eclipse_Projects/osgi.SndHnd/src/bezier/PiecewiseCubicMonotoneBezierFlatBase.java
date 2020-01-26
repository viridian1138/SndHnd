




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







package bezier;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectOutput;
import java.util.ArrayList;

import core.InterpolationPoint;


import meta.VersionBuffer;

/**
 * Base class for piecewise cubic monotone Bezier curves that are non-periodic.
 * 
 * See:  "Curves and Surfaces for CAGD" by Gerald Farin, ISBN 978-1558607378.
 * 
 * See references section of  https://en.wikipedia.org/wiki/Monotone_cubic_interpolation
 * 
 * @author tgreen
 *
 */
public abstract class PiecewiseCubicMonotoneBezierFlatBase implements Externalizable {
	
	/**
	 * The interpolation points of the curve.
	 */
	protected ArrayList<InterpolationPoint> interpolationPoints = new ArrayList<InterpolationPoint>();
	
	/**
	 * The piecewise list of quartic Bezier curve segments.
	 */
	protected transient ArrayList<CubicBezierCurve> bezierCurves = null;
	
	
	/**
	 * Gets the number of quartic Bezier curve segments.
	 * @return The number of quartic Bezier curve segments.
	 */
	public int getNumCurves()
	{
		return( bezierCurves.size() );
	}
	
	
	/**
	 * Apply Fritsch-Carlson Monotonicity Constraints.
	 * @param index The interpolation point index at which to check slope constraints.
	 * @param absRawSlope The absolute value of the originally estimated slope.
	 * @return The Fritsch-Carlson adjusted slope.
	 */
	protected double chkRawSlope( int index , double absRawSlope )
	{
		final InterpolationPoint p1 = gPoint( index );
		final InterpolationPoint p2 = gPoint( index + 1 );
		
		final double ptMaxRise = 3.0 * ( p2.getValue() - p1.getValue() );
		final double ptRun = p2.getParam() - p1.getParam();
		
		final double maxAbsSlope = Math.abs( ptMaxRise / ptRun );
		
	//	System.out.println( "***" );
	//	System.out.println( index );
	//	System.out.println( absRawSlope );
	//	System.out.println( maxAbsSlope );
		
		return( Math.min( absRawSlope , maxAbsSlope ) );
	}
	
	
	/**
	 * Gets the Fritsch-Carlson adjusted estimated slope estimate for a particular interpolation point.
	 * @param index The interpolation point for which to estimate the slope.
	 * @return The estimated slope.
	 */
	protected double getSlopeComp( int index )
	{
		final InterpolationPoint p0 = gPoint( index - 1 );
		final InterpolationPoint p1 = gPoint( index );
		final InterpolationPoint p2 = gPoint( index + 1 );
		
		final double slope0 = ( p1.getValue() - p0.getValue() ) / ( p1.getParam() - p0.getParam() );
		final double slope1 = ( p2.getValue() - p1.getValue() ) / ( p2.getParam() - p1.getParam() );
		
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
		
		final double absRawSlope1 = chkRawSlope( index - 1 , absRawSlope );
		final double absRawSlope2 = chkRawSlope( index , absRawSlope1 );
		
		double slope = absRawSlope2;
		if( rawSlope < 0.0 )
		{
			slope = -slope;
		}
		
		// System.out.println( "###" );
		// System.out.println( slope );
		
		return( slope );
	}
	
	
	/**
	 * Gets the Fritsch-Carlson adjusted estimated slope estimate for a particular interpolation point.
	 * @param index The interpolation point for which to estimate the slope.
	 * @return The estimated slope.
	 */
	public double getSlope( int index )
	{
		if( index == 0 )
		{
			return( getStrtSlope() );
		}
		
		if( index == ( interpolationPoints.size() - 1 ) )
		{
			return( getEndSlope() );
		}
		
		return( getSlopeComp( index ) );
	}
	
	/**
	 * Gets the slope at the start of the curve.
	 * @return The slope at the start of the curve.
	 */
	public double getStrtSlope()
	{
		return( 0.0 );
	}
	
	/**
	 * Gets The slope at the end of the curve.
	 * @return The slope at the end of the curve.
	 */
	public double getEndSlope()
	{
		return( 0.0 );
	}
	
	/**
	 * Updates one curve segment to match a change in an interpolation point.
	 * @param index The index of the curve segment to change.
	 */
	public void updateCurve( int index )
	{
		InterpolationPoint strt = gPoint( index );
		InterpolationPoint end = gPoint( index + 1 );
		double slope0 = getSlope( index );
		double slope1 = getSlope( index + 1 );
		CubicBezierCurve curve = gCurve( index );
		double[] bezPts = curve.getBezPts();
		
		bezPts[ 0 ] = strt.getValue();
		bezPts[ 3 ] = end.getValue();
		
		double deltaParam = end.getParam() - strt.getParam();
		double ptRun = ( 1.0 / 3.0 ) * deltaParam;
		
		double slopeRise0 = slope0 * ptRun;
		double slopeRise1 = slope1 * ptRun;
		
		bezPts[ 1 ] = bezPts[ 0 ] + slopeRise0;
		bezPts[ 2 ] = bezPts[ 3 ] - slopeRise1;
	}
	
	/**
	 * Gets the interpolation point at a particular index.  Indices start at zero.
	 * @param index The index.
	 * @return The interpolation point at the index.
	 */
	public InterpolationPoint gPoint( int index )
	{
		InterpolationPoint pt = interpolationPoints.get( index );
		return( pt );
	}
	
	
	/**
	 * Gets the first interpolation point.
	 * @return The first interpolation point.
	 */
	public InterpolationPoint getFirstPoint( )
	{
		return( gPoint( 0 ) );
	}
	
	
	/**
	 * Gets the last interpolation point.
	 * @return The last interpolation point.
	 */
	public InterpolationPoint getLastPoint()
	{
		return( gPoint( interpolationPoints.size() - 1 ) );
	}
	
	
	/**
	 * Updates curve segments to match a change in interpolation point.
	 * @param index The index of the interpolation point that changed.
	 */
	public void updatedPoint( int index )
	{
		int strt = Math.max( 0 , index - 2 );
		int end = Math.min( interpolationPoints.size() - 2 , index + 2 );
		
		int cnt;
		for( cnt = strt ; cnt <= end ; cnt++ )
		{
			updateCurve( cnt );
		}
	}
	
	
	/**
	 * Gets the cubic Bezier curve segment at a particular index.
	 * @param index The input index.
	 * @return The cubic Bezier curve segment at the index.
	 */
	public CubicBezierCurve gCurve( int index )
	{
		CubicBezierCurve nd = bezierCurves.get( index );
		return( nd );
	}
	
	
	/**
	 * Gets the piecewise list of cubic Bezier curve segments.
	 * @return The piecewise list of cubic Bezier curve segments.
	 */
	public ArrayList<CubicBezierCurve> getBezierCurves() {
		return bezierCurves;
	}

	/**
	 * Gets the interpolation points of the curve.
	 * @return The interpolation points of the curve.
	 */
	public ArrayList<InterpolationPoint> getInterpolationPoints() {
		return interpolationPoints;
	}

	/**
	 * Writes the node to serial storage.
	 * 
	 * @serialData TBD.
	 */
	public void writeExternal(ObjectOutput out) throws IOException {
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setInt("InterpSize", interpolationPoints.size());
		int plen = interpolationPoints.size();
		int count;
		for (count = 0; count < plen; count++) {
			myv.setProperty("Interp_" + count, interpolationPoints.get(count));
		}

		out.writeObject(myv);
	}

}
