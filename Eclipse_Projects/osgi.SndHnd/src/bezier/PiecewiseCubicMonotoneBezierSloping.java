




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
public class PiecewiseCubicMonotoneBezierSloping extends PiecewiseCubicMonotoneBezierFlat {

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
	public PiecewiseCubicMonotoneBezierSloping() {
		super();
	}
	
	/**
	 * Sets the end-slopes of the piecewise domain.
	 * @param s1 The end-slope on the left-end of the domain.
	 * @param s2 The end-clope on the right-end of the domain.
	 */
	public void setSlopes( double s1 , double s2 )
	{
		strtSlope = s1;
		endSlope = s2;
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
	
	@Override
	public PiecewiseCubicMonotoneBezierFlat genCloneWave()
	{
		PiecewiseCubicMonotoneBezierSloping pc = new PiecewiseCubicMonotoneBezierSloping();
		pc.interpolationPoints = new ArrayList<InterpolationPoint>( interpolationPoints );
		if( bezierCurves != null )
		{
			pc.bezierCurves = new ArrayList<CubicBezierCurve>( bezierCurves );
		}
		pc.currentIndex = currentIndex;
		pc.strtSlope = strtSlope;
		pc.endSlope = endSlope;
		return( pc );
	}
	
	

}

