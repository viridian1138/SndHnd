




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
import java.io.ObjectInput;
import java.util.ArrayList;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import core.InterpolationPoint;


/**
 * Piecewise cubic monotone Bezier curve with a zero slope extending off from each end of the piecewise domain.  The curve uses Fritsch-Carlson monotonicity constraints.
 * 
 * See:  "Curves and Surfaces for CAGD" by Gerald Farin, ISBN 978-1558607378.
 * 
 * See references section of  https://en.wikipedia.org/wiki/Monotone_cubic_interpolation
 * 
 * @author tgreen
 *
 */
public class PiecewiseCubicMonotoneBezierFlat extends PiecewiseCubicMonotoneBezierFlatBase implements Externalizable {
	
	
	/**
	 * The current curve segment index for each core thread.
	 */
	protected transient int currentIndex = 0;

	
	/**
	 * Constructs the curve.
	 */
	public PiecewiseCubicMonotoneBezierFlat() {
		super();
	}
	
	
	/**
	 * Generates a clone of the curve.
	 * @return A clone of the curve.
	 */
	public PiecewiseCubicMonotoneBezierFlat genCloneWave()
	{
		PiecewiseCubicMonotoneBezierFlat pc = new PiecewiseCubicMonotoneBezierFlat();
		pc.interpolationPoints = new ArrayList<InterpolationPoint>( interpolationPoints );
		if( bezierCurves != null )
		{
			pc.bezierCurves = new ArrayList<CubicBezierCurve>( bezierCurves );
		}
		pc.currentIndex = currentIndex;
		return( pc );
	}
	
	
	/**
	 * Generates the corresponding GPiecewiseCubicMonotoneBezierFlat for this curve.
	 * @param s Map for duplicate elimination among nodes.
	 * @return The corresponding GPiecewiseCubicMonotoneBezierFlat for this curve.
	 */
	public GPiecewiseCubicMonotoneBezierFlat genBez( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GPiecewiseCubicMonotoneBezierFlat)( s.get( this ) ) );
		}
		GPiecewiseCubicMonotoneBezierFlat bez = new GPiecewiseCubicMonotoneBezierFlat();
		bez.load( this );
		return( bez );
	}
	
	
	/**
	 * Updates all curve segments to match the interpolation points.
	 */
	public void updateAll()
	{
		currentIndex = 0;
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
	public CubicBezierCurve getCurrentCurve( double param )
	{	
		while( ( param < gCurve( currentIndex ).getStartParam() ) && ( currentIndex > 0 ) )
		{
			currentIndex--;
		}
		
		while( ( param > gCurve( currentIndex ).getEndParam() ) && ( currentIndex < ( bezierCurves.size() - 1 ) ) )
		{
			currentIndex++;
		}
		
		return( gCurve( currentIndex ) );
	}
	
	
	/**
	 * Evaluates the curve at a particular parameter.
	 * @param param The parameter at which to evaluate.
	 * @return The evaluated curve at the parameter.
	 */
	public final double eval( final double param )
	{
		return( getCurrentCurve( param ).eval( param ) );
	}
	
	
	/**
	 * Evaluates the slope of the curve at a particular parameter.
	 * @param param The parameter at which to evaluate.
	 * @return The evaluated slope at the parameter.
	 */
	public final double evalDerivative( final double param )
	{
		return( getCurrentCurve( param ).evalDerivative( param ) );
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
		currentIndex = 0;
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
				
			currentIndex = 0;
			updateAll();
		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}


}
