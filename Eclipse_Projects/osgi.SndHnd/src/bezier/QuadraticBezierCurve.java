




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

import java.io.PrintStream;

import core.CalcCompositeCurve;

/**
 * A quadratic (degree 2) Bezier curve in 1-D.
 * 
 * See: https://pomax.github.io/bezierinfo/
 * 
 * Not persistent.
 * 
 * @author thorngreen
 *
 */
public final class QuadraticBezierCurve {
	
	/**
	 * The start parameter of the curve.
	 */
	protected double startParam;
	
	/**
	 * The end parameter of the curve.
	 */
	protected double endParam;
	
	/**
	 * The quadratic Bezier points of the curve.
	 */
	protected double[] bezPts = { 0.0 , 0.0 , 0.0 };

	/**
	 * Constructs the curve.
	 */
	public QuadraticBezierCurve() {
		super();
	}
	
	/**
	 * Evaluates the curve.
	 * @param param The parameter at which to evaluate.
	 * @return The result of the evaluation.
	 */
	public final double eval( final double param )
	{
		final double u = ( param - startParam ) / ( endParam - startParam );
		
		final double b0 = bezPts[ 0 ];
		final double b1 = bezPts[ 1 ];
		final double b2 = bezPts[ 2 ];
		
		final double b10 = (1-u)*b0 + u*b1;
		final double b11 = (1-u)*b1 + u*b2;
		
		final double b20 = (1-u)*b10 + u*b11;
		
		return( b20 );
	}
	

	/**
	 * Gets the end parameter of the curve.
	 * @return The end parameter of the curve.
	 */
	public double getEndParam() {
		return endParam;
	}

	/**
	 * Sets the end parameter of the curve.
	 * @param endParam The end parameter of the curve.
	 */
	public void setEndParam(double endParam) {
		this.endParam = endParam;
	}

	/**
	 * Gets the start parameter of the curve.
	 * @return The start parameter of the curve.
	 */
	public double getStartParam() {
		return startParam;
	}

	/**
	 * Sets the start parameter of the curve.
	 * @param startParam The start parameter of the curve.
	 */
	public void setStartParam(double startParam) {
		this.startParam = startParam;
	}

	/**
	 * Gets the Bezier points for the curve.
	 * @return The Bezier points for the curve.
	 */
	public double[] getBezPts() {
		return bezPts;
	}

	/**
	 * Sets the Bezier points of the curve.
	 * @param bezPts The Bezier points for the curve.  It is assumed this array is of length three.
	 */
	public void setBezPts(double[] bezPts) {
		this.bezPts = bezPts;
	}
	
	
}

