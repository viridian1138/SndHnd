




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

/**
 * A heptic (degree 7) Bezier curve in 1-D.
 * 
 * See: https://pomax.github.io/bezierinfo/
 * 
 * Not persistent.
 * 
 * @author thorngreen
 *
 */
public class HepticBezierCurve {
	
	/**
	 * The start parameter of the curve.
	 */
	protected double startParam;
	
	/**
	 * The end parameter of the curve.
	 */
	protected double endParam;
	
	/**
	 * The heptic Bezier points of the curve.
	 */
	protected double[] bezPts = { 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 , 0.0 };

	/**
	 * Constructs the curve.
	 */
	public HepticBezierCurve() {
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
		double b5 = bezPts[ 5 ];
		double b6 = bezPts[ 6 ];
		double b7 = bezPts[ 7 ];
		
		double b10 = (1-u)*b0 + u*b1;
		double b11 = (1-u)*b1 + u*b2;
		double b12 = (1-u)*b2 + u*b3;
		double b13 = (1-u)*b3 + u*b4;
		double b14 = (1-u)*b4 + u*b5;
		double b15 = (1-u)*b5 + u*b6;
		double b16 = (1-u)*b6 + u*b7;
		
		double b20 = (1-u)*b10 + u*b11;
		double b21 = (1-u)*b11 + u*b12;
		double b22 = (1-u)*b12 + u*b13;
		double b23 = (1-u)*b13 + u*b14;
		double b24 = (1-u)*b14 + u*b15;
		double b25 = (1-u)*b15 + u*b16;
		
		double b30 = (1-u)*b20 + u*b21;
		double b31 = (1-u)*b21 + u*b22;
		double b32 = (1-u)*b22 + u*b23;
		double b33 = (1-u)*b23 + u*b24;
		double b34 = (1-u)*b24 + u*b25;
		
		double b40 = (1-u)*b30 + u*b31;
		double b41 = (1-u)*b31 + u*b32;
		double b42 = (1-u)*b32 + u*b33;
		double b43 = (1-u)*b33 + u*b34;
		
		double b50 = (1-u)*b40 + u*b41;
		double b51 = (1-u)*b41 + u*b42;
		double b52 = (1-u)*b42 + u*b43;
		
		double b60 = (1-u)*b50 + u*b51;
		double b61 = (1-u)*b51 + u*b52;
		
		double b70 = (1-u)*b60 + u*b61;
		
		return( b70 );
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
		double b5 = bezPts[ 5 ];
		double b6 = bezPts[ 6 ];
		double b7 = bezPts[ 7 ];
		
		double b10 = b1 - b0;
		double b11 = b2 - b1;
		double b12 = b3 - b2;
		double b13 = b4 - b3;
		double b14 = b5 - b4;
		double b15 = b6 - b5;
		double b16 = b7 - b6;
		
		double b20 = (1-u)*b10 + u*b11;
		double b21 = (1-u)*b11 + u*b12;
		double b22 = (1-u)*b12 + u*b13;
		double b23 = (1-u)*b13 + u*b14;
		double b24 = (1-u)*b14 + u*b15;
		double b25 = (1-u)*b15 + u*b16;
		
		double b30 = (1-u)*b20 + u*b21;
		double b31 = (1-u)*b21 + u*b22;
		double b32 = (1-u)*b22 + u*b23;
		double b33 = (1-u)*b23 + u*b24;
		double b34 = (1-u)*b24 + u*b25;
		
		double b40 = (1-u)*b30 + u*b31;
		double b41 = (1-u)*b31 + u*b32;
		double b42 = (1-u)*b32 + u*b33;
		double b43 = (1-u)*b33 + u*b34;
		
		double b50 = (1-u)*b40 + u*b41;
		double b51 = (1-u)*b41 + u*b42;
		double b52 = (1-u)*b42 + u*b43;
		
		double b60 = (1-u)*b50 + u*b51;
		double b61 = (1-u)*b51 + u*b52;
		
		double b70 = (1-u)*b60 + u*b61;
		
		return( 8.0 * b70 / ( endParam - startParam ) );
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
	 * @param bezPts The Bezier points for the curve.  It is assumed this array is of length eight.
	 */
	public void setBezPts(double[] bezPts) {
		this.bezPts = bezPts;
	}
	
	

}
