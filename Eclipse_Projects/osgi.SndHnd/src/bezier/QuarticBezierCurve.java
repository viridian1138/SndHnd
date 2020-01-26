




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

import core.CalcCompositeCurve;

/**
 * A quartic (degree 4) Bezier curve in 1-D.
 * 
 * See: https://pomax.github.io/bezierinfo/
 * 
 * Not persistent.
 * 
 * @author thorngreen
 *
 */
public class QuarticBezierCurve {
	
	/**
	 * The start parameter of the curve.
	 */
	protected double startParam;
	
	/**
	 * The end parameter of the curve.
	 */
	protected double endParam;
	
	/**
	 * The quartic Bezier points of the curve.
	 */
	protected double[] bezPts = { 0.0 , 0.0 , 0.0 , 0.0 , 0.0 };

	/**
	 * Constructs the curve.
	 */
	public QuarticBezierCurve() {
		super();
	}
	
	/**
	 * Evaluates the curve.
	 * @param param The parameter at which to evaluate.
	 * @return The result of the evaluation.
	 */
	public double eval( double param )
	{
		double u = ( param - startParam ) / ( endParam - startParam );
		
		double b0 = bezPts[ 0 ];
		double b1 = bezPts[ 1 ];
		double b2 = bezPts[ 2 ];
		double b3 = bezPts[ 3 ];
		double b4 = bezPts[ 4 ];
		
		double b10 = (1-u)*b0 + u*b1;
		double b11 = (1-u)*b1 + u*b2;
		double b12 = (1-u)*b2 + u*b3;
		double b13 = (1-u)*b3 + u*b4;
		
		double b20 = (1-u)*b10 + u*b11;
		double b21 = (1-u)*b11 + u*b12;
		double b22 = (1-u)*b12 + u*b13;
		
		double b30 = (1-u)*b20 + u*b21;
		double b31 = (1-u)*b21 + u*b22;
		
		double b40 = (1-u)*b30 + u*b31;
		
		return( b40 );
	}
	
	/**
	 * Evaluates the slope of the curve with respect to the parameter.
	 * @param param The parameter at which to evaluate.
	 * @return The result of the evaluation.
	 */
	public double evalSlope( double param )
	{
		double u = ( param - startParam ) / ( endParam - startParam );
		
		double b0 = bezPts[ 0 ];
		double b1 = bezPts[ 1 ];
		double b2 = bezPts[ 2 ];
		double b3 = bezPts[ 3 ];
		double b4 = bezPts[ 4 ];
		
		double b10 = b1 - b0;
		double b11 = b2 - b1;
		double b12 = b3 - b2;
		double b13 = b4 - b3;
		
		double b20 = (1-u)*b10 + u*b11;
		double b21 = (1-u)*b11 + u*b12;
		double b22 = (1-u)*b12 + u*b13;
		
		double b30 = (1-u)*b20 + u*b21;
		double b31 = (1-u)*b21 + u*b22;
		
		double b40 = (1-u)*b30 + u*b31;
		
		return( 4.0 * b40 / ( endParam - startParam ) );
	}
	
	/**
	 * Returns a subsection of the curve.
	 * @param param0 The start parameter of the subsection to be generated.
	 * @param param1 The end parameter of the subsection to be generated.
	 * @return The subsection of the curve.
	 */
	public QuarticBezierCurve calcSubsection( double param0 , double param1 )
	{
		double u0 = ( param0 - startParam ) / ( endParam - startParam );
		double u1 = ( param1 - startParam ) / ( endParam - startParam );
		double[] section = CalcCompositeCurve.calcSubsection( bezPts , u0 , u1 );
		QuarticBezierCurve ret = new QuarticBezierCurve();
		ret.setBezPts( section );
		ret.setStartParam( param0 );
		ret.setEndParam( param1 );
		return( ret );
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
	 * @param bezPts The Bezier points for the curve.  It is assumed this array is of length five.
	 */
	public void setBezPts(double[] bezPts) {
		this.bezPts = bezPts;
	}
	
	/**
	 * Generates the cubic Bezier curve for the first derivative with respect to the parameter.
	 * @return The cubic Bezier curve for the first derivative with respect to the parameter.
	 */
	public CubicBezierCurve generateDerivative()
	{
		CubicBezierCurve cub = new CubicBezierCurve();
		cub.setStartParam(0.0); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		cub.setEndParam(1.0);
		double[] cubb = cub.getBezPts();
		cubb[ 0 ] = 4.0 * ( bezPts[ 1 ] - bezPts[ 0 ] );
		cubb[ 1 ] = 4.0 * ( bezPts[ 2 ] - bezPts[ 1 ] );
		cubb[ 2 ] = 4.0 * ( bezPts[ 3 ] - bezPts[ 2 ] );
		cubb[ 3 ] = 4.0 * ( bezPts[ 4 ] - bezPts[ 3 ] );
		return( cub );
	}
	
	

}

