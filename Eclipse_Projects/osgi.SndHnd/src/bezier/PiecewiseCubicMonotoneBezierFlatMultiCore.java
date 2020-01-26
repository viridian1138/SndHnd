




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


import java.io.IOException;
import java.io.ObjectInput;
import java.util.ArrayList;

import core.CpuInfo;
import core.InterpolationPoint;


import meta.DataFormatException;
import meta.VersionBuffer;

/**
 * Piecewise cubic Bezier curve with a zero slope extending off from each end of the piecewise domain.  The curve uses Fritsch-Carlson monotonicity constraints.
 * 
 * Written to support multi-core evaluation.
 * 
 * See:  "Curves and Surfaces for CAGD" by Gerald Farin, ISBN 978-1558607378.
 * 
 * See references section of  https://en.wikipedia.org/wiki/Monotone_cubic_interpolation
 * 
 * @author tgreen
 *
 */
public class PiecewiseCubicMonotoneBezierFlatMultiCore extends PiecewiseCubicMonotoneBezierFlatBase {
	
	
	/**
	 * The current curve segment index for each core thread.
	 */
	protected final int[] currentIndex = new int[ CpuInfo.getNumCores() ];

	
	/**
	 * Constructs the curve.
	 */
	public PiecewiseCubicMonotoneBezierFlatMultiCore() {
		super();
	}
	
	
	/**
	 * Updates all curve segments to match the interpolation points.
	 */
	public void updateAll()
	{
		
		{
			int count;
			int max = CpuInfo.getNumCores();
			for( count = 0 ; count < max ; count++ )
			{
				currentIndex[ count ] = 0;
			}
		}
		
		bezierCurves = new ArrayList<CubicBezierCurve>();
		int max = interpolationPoints.size() - 1;
		int count;
		
		for( count = 0 ; count < max ; count++ )
		{
			CubicBezierCurve curve = new CubicBezierCurve();
			InterpolationPoint strt = gPoint( count );
			InterpolationPoint end = gPoint( count + 1 );
			curve.setStartParam( strt.getParam() );
			curve.setEndParam( end.getParam() );
			bezierCurves.add( curve );
		}
		
		for( count = 0 ; count < max ; count++ )
		{
			updateCurve( count );
		}
		
	}
	
	
	/**
	 * Gets the current cubic Bezier curve segment for a particular parameter.
	 * @param param The input parameter value.
	 * @return The current curve segment.
	 */
	public CubicBezierCurve getCurrentCurve( final double param , final int core )
	{	
		while( ( param < gCurve( currentIndex[ core ] ).getStartParam() ) && ( currentIndex[ core ] > 0 ) )
		{
			( currentIndex[ core ] )--;
		}
		
		while( ( param > gCurve( currentIndex[ core ] ).getEndParam() ) && ( currentIndex[ core ] < ( bezierCurves.size() - 1 ) ) )
		{
			( currentIndex[ core ] )++;
		}
		
		return( gCurve( currentIndex[ core ] ) );
	}
	
	
	/**
	 * Evaluates the curve at a particular parameter.
	 * @param param The parameter at which to evaluate.
	 * @return The evaluated curve at the parameter.
	 */
	public final double eval( final double param , final int core )
	{
		return( getCurrentCurve( param , core ).eval( param ) );
	}
	
	
	/**
	 * Evaluates the slope of the curve at a particular parameter.
	 * @param param The parameter at which to evaluate.
	 * @param core The core thread performing the evaluation.
	 * @return The evaluated slope at the parameter.
	 */
	public final double evalDerivative( final double param , final int core )
	{
		return( getCurrentCurve( param , core ).evalDerivative( param ) );
	}
	
	
	/**
	 * Sets the interpolation points of the curve.
	 * @param interpolationPoints The interpolation points of the curve.
	 */
	public void setInterpolationPoints(ArrayList<InterpolationPoint> interpolationPoints) {
		this.interpolationPoints = interpolationPoints;
		setBezierCurves( null );
	}
	
	/**
	 * Sets the piecewise list of cubic Bezier curve segments.
	 * @param bezierCurves The piecewise list of cubic Bezier curve segemnts.
	 */
	protected void setBezierCurves(ArrayList<CubicBezierCurve> bezierCurves) {
		this.bezierCurves = bezierCurves;
		int count;
		int max = CpuInfo.getNumCores();
		for( count = 0 ; count < max ; count++ )
		{
			currentIndex[ count ] = 0;
		}
	}

	/**
	 * Reads the node from serial storage.
	 */
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			int plen = myv.getInt("InterpSize");
			interpolationPoints = new ArrayList<InterpolationPoint>(plen);
			int count;
			for (count = 0; count < plen; count++) {
				interpolationPoints.add((InterpolationPoint) (myv.getPropertyEx("Interp_" + count)));
			}
				
			int max = CpuInfo.getNumCores();
			for( count = 0 ; count < max ; count++ )
			{
				currentIndex[ count ] = 0;
			}
			updateAll();
		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}


}
