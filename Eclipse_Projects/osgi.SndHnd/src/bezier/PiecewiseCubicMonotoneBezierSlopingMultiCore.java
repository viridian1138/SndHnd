




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


import java.util.ArrayList;
import java.util.Iterator;

import core.CpuInfo;
import core.InterpolationPoint;


/**
 * Piecewise cubic Bezier curve with a linear slope extending off from each end of the piecewise domain, and uses Fritsch-Carlson monotonicity constraints.
 * 
 * See:  "Curves and Surfaces for CAGD" by Gerald Farin, ISBN 978-1558607378.
 * 
 * See references section of  https://en.wikipedia.org/wiki/Monotone_cubic_interpolation
 * 
 * @author tgreen
 *
 */
public class PiecewiseCubicMonotoneBezierSlopingMultiCore extends PiecewiseCubicMonotoneBezierFlatMultiCore {

	/**
	 * The slope at the start of the curve.
	 */
	protected double strtSlope;
	
	/**
	 * The slope at the end of the curve.
	 */
	protected double endSlope;
	
	/**
	 * Constructs the curve.
	 */
	public PiecewiseCubicMonotoneBezierSlopingMultiCore() {
		super();
	}
	
	@Override
	public double getStrtSlope()
	{
		return( strtSlope );
	}
	
	@Override
	public double getEndSlope()
	{
		return( endSlope );
	}
	
	/**
	 * Sets this curve to an approximation of the inverse function of a PiecewiseQuarticBezierSlopingMultiCore.
	 * @param quartic The PiecewiseQuarticBezierSlopingMultiCore to be approximately inverted.
	 * @param slopePoints The slopes at the interpolation points of the PiecewiseQuarticBezierSlopingMultiCore.
	 * @param crvMultiplier Curve multiplier used for units conversion.
	 * @param core The core thread executing the request.
	 */
	public void generateApproximation( PiecewiseQuarticBezierSlopingMultiCore quartic , ArrayList<InterpolationPoint> slopePoints , double crvMultiplier , final int core )
	{
		interpolationPoints = new ArrayList<InterpolationPoint>();
		
		{
			int count;
			int max = CpuInfo.getNumCores();
			for( count = 0 ; count < max ; count++ )
			{
				currentIndex[ count ] = 0;
			}
		}
		
		InterpolationPoint p0 = slopePoints.get( 0 );
		InterpolationPoint p1 = slopePoints.get( slopePoints.size() - 1 );
		strtSlope = crvMultiplier / ( p0.getValue() );
		endSlope = crvMultiplier / ( p1.getValue() );
		
		int count;
		ArrayList<InterpolationPoint> qint = quartic.getInterpolationPoints();
		int sz = qint.size();
		
		for( count = 0 ; count < ( sz - 1 ) ; count++ )
		{
			InterpolationPoint pt0 = qint.get( count );
			InterpolationPoint pt1 = qint.get( count + 1 );
			int cnt;
			for( cnt = 0 ; cnt < 10 ; cnt++ )
			{
				double u = cnt / 10.0;
				double param = ( 1 - u ) * ( pt0.getParam() ) + u * ( pt1.getParam() );
				double value = quartic.eval( param , core );
				
				// Intentionally reverse the params because the intent is to generate an inverse function.
				InterpolationPoint nxt = new InterpolationPoint( value , param );
				interpolationPoints.add( nxt );
			}
		}
		
		InterpolationPoint last = qint.get( qint.size() - 1 );
		interpolationPoints.add( new InterpolationPoint( last.getValue() , last.getParam() ) );
		
		updateAll();
	}
	
	/**
	 * Sets this curve to an approximation of the inverse function of a PiecewiseHepticBezierSlopingMultiCore.
	 * @param heptic The PiecewiseHepticBezierSlopingMultiCore to be approximately inverted.
	 * @param slopePoints The slopes at the interpolation points of the PiecewiseHepticBezierSlopingMultiCore.
	 * @param crvMultiplier Curve multiplier used for units conversion.
	 * @param core The core thread executing the request.
	 */
	public void generateApproximation( PiecewiseHepticBezierSlopingMultiCore heptic , ArrayList<InterpolationPoint> slopePoints , double crvMultiplier , final int core )
	{
		interpolationPoints = new ArrayList<InterpolationPoint>();
		
		{
			int count;
			int max = CpuInfo.getNumCores();
			for( count = 0 ; count < max ; count++ )
			{
				currentIndex[ count ] = 0;
			}
		}
		
		InterpolationPoint p0 = slopePoints.get( 0 );
		InterpolationPoint p1 = slopePoints.get( slopePoints.size() - 1 );
		strtSlope = crvMultiplier / ( p0.getValue() );
		endSlope = crvMultiplier / ( p1.getValue() );
		
		int count;
		ArrayList<InterpolationPoint> qint = heptic.getInterpolationPoints();
		int sz = qint.size();
		
		for( count = 0 ; count < ( sz - 1 ) ; count++ )
		{
			InterpolationPoint pt0 = qint.get( count );
			InterpolationPoint pt1 = qint.get( count + 1 );
			int cnt;
			for( cnt = 0 ; cnt < 10 ; cnt++ )
			{
				double u = cnt / 10.0;
				double param = ( 1 - u ) * ( pt0.getParam() ) + u * ( pt1.getParam() );
				double value = heptic.eval( param , core );
				
				// Intentionally reverse the params because the intent is to generate an inverse function.
				InterpolationPoint nxt = new InterpolationPoint( value , param );
				interpolationPoints.add( nxt );
			}
		}
		
		InterpolationPoint last = qint.get( qint.size() - 1 );
		interpolationPoints.add( new InterpolationPoint( last.getValue() , last.getParam() ) );
		
		updateAll();
	}

}
