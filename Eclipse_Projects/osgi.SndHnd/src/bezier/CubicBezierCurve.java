




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
 * A cubic (degree 3) Bezier curve in 1-D.
 * 
 * See: https://pomax.github.io/bezierinfo/
 * 
 * Not persistent.
 * 
 * @author thorngreen
 *
 */
public final class CubicBezierCurve {
	
	/**
	 * The start parameter of the curve.
	 */
	protected double startParam;
	
	/**
	 * The end parameter of the curve.
	 */
	protected double endParam;
	
	/**
	 * The cubic Bezier points of the curve.
	 */
	protected double[] bezPts = { 0.0 , 0.0 , 0.0 , 0.0 };

	/**
	 * Constructs the curve.
	 */
	public CubicBezierCurve() {
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
		final double b3 = bezPts[ 3 ];
		
		final double b10 = (1-u)*b0 + u*b1;
		final double b11 = (1-u)*b1 + u*b2;
		final double b12 = (1-u)*b2 + u*b3;
		
		final double b20 = (1-u)*b10 + u*b11;
		final double b21 = (1-u)*b11 + u*b12;
		
		final double b30 = (1-u)*b20 + u*b21;
		
		return( b30 );
	}
	
	/**
	 * Evaluates the slope of the curve with respect to the parameter.
	 * @param param The parameter at which to evaluate.
	 * @return The result of the evaluation.
	 */
	public final double evalDerivative( final double param )
	{
		final double u = ( param - startParam ) / ( endParam - startParam );
		
		final double b0 = bezPts[ 1 ] - bezPts[ 0 ];
		final double b1 = bezPts[ 2 ] - bezPts[ 1 ];
		final double b2 = bezPts[ 3 ] - bezPts[ 2 ];
		
		final double b10 = (1-u)*b0 + u*b1;
		final double b11 = (1-u)*b1 + u*b2;
		
		final double b20 = (1-u)*b10 + u*b11;
		
		return( 3.0 * b20 / ( endParam - startParam ) );
	}
	
	/**
	 * Calculates a subsection of the curve.
	 * @param param0 The start parameter of the subsection.
	 * @param param1 The end parameter of the subsection.
	 * @return The subsection CubicBezierCurve.
	 */
	public CubicBezierCurve calcSubsection( double param0 , double param1 )
	{
		double u0 = ( param0 - startParam ) / ( endParam - startParam );
		double u1 = ( param1 - startParam ) / ( endParam - startParam );
		double[] section = CalcCompositeCurve.calcSubsection( bezPts , u0 , u1 );
		CubicBezierCurve ret = new CubicBezierCurve();
		ret.setBezPts( section );
		ret.setStartParam( param0 );
		ret.setEndParam( param1 );
		return( ret );
	}
	
	/**
	 * Translates the Bezier points of the curve.
	 * @param delta The amount by which to translate the Bezier points.
	 * @return The CubicBezierCurve of the translated curve.
	 */
	public CubicBezierCurve translate( double delta )
	{
		double[] trans = CalcCompositeCurve.copy( bezPts );
		CalcCompositeCurve.translateCurve( trans , delta );
		CubicBezierCurve ret = new CubicBezierCurve();
		ret.setBezPts( trans );
		ret.setStartParam( startParam );
		ret.setEndParam( endParam );
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
	 * Finds the complex roots of a monomial basis cubic polynomial.
	 * @param a The cubic term of the polynomial.
	 * @param b The quadratic term of the polynomial.
	 * @param c The linear term of the polynomial.
	 * @param d The constant term of the polynomial.
	 * @param ps Not used.
	 * @return Array of complex roots.
	 */
	protected Cplx[] findRoots( double a , double b , double c , double d , PrintStream ps )
	{	
		final double b23ac = b * b - 3 * a * c;
		final double b23ac34 = 4 * b23ac * b23ac * b23ac;
		
		final double b329abcetc = 2 * b * b * b - 9 * a * b * c + 27 * a * a * d;
		
		final Cplx sqrtTerm = Cplx.sqrt( b329abcetc * b329abcetc - b23ac34 );
		final Cplx sqrtAdd = Cplx.real( b329abcetc ).add( sqrtTerm );
		final Cplx sqrtSub = Cplx.real( b329abcetc ).sub( sqrtTerm );
		
		final Cplx sqrtAddDiv2 = sqrtAdd.divBy( 2 );
		final Cplx sqrtSubDiv2 = sqrtSub.divBy( 2 );
		
		final Cplx cubeRootAdd = sqrtAddDiv2.principalCubeRt();
		final Cplx cubeRootSub = sqrtSubDiv2.principalCubeRt();
		
		final Cplx odiv3a = Cplx.real( - 1.0 / ( 3 * a ) );
		final Cplx plusdiv6a = new Cplx( 1.0 / ( 6 * a ) , Math.sqrt( 3 ) / ( 6 * a ) );
		final Cplx subdiv6a = new Cplx( 1.0 / ( 6 * a ) , - Math.sqrt( 3 ) / ( 6 * a ) );
		final Cplx bdiv3a = Cplx.real( - b / ( 3 * a ) );
		
		final Cplx x1 = bdiv3a.add( odiv3a.mult( cubeRootAdd ) ).add( odiv3a.mult( cubeRootSub ) );
		final Cplx x2 = bdiv3a.add( plusdiv6a.mult( cubeRootAdd ) ).add( subdiv6a.mult( cubeRootSub ) );
		final Cplx x3 = bdiv3a.add( subdiv6a.mult( cubeRootAdd ) ).add( plusdiv6a.mult( cubeRootSub ) );
		
		final Cplx[] roots = { x1 , x2 , x3 };
		
		return( roots );
	}
	
	/**
	 * Finds the complex roots of the curve.
	 * @param ps Not used.
	 * @return Array of complex roots.
	 */
	public Cplx[] findRoots( PrintStream ps )
	{
		double a = bezPts[ 3 ] - 3 * bezPts[ 2 ] + 3 * bezPts[ 1 ] - bezPts[ 0 ]; 
		double b = 3 * bezPts[ 2 ] - 6 * bezPts[ 1 ] - bezPts[ 0 ];
		double c = 3 * bezPts[ 1 ] - 3 * bezPts[ 0 ];
		double d = bezPts[ 0 ];
		return( findRoots( a , b , c , d , ps ) );
	}
	
	/**
	 * Generates the quadratic Bezier curve for the first derivative with respect to the parameter.
	 * @return The quadratic Bezier curve for the first derivative with respect to the parameter.
	 */
	public QuadraticBezierCurve generateDerivative()
	{
		QuadraticBezierCurve quad = new QuadraticBezierCurve();
		quad.setStartParam(0.0); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		quad.setEndParam(1.0);
		double[] quadb = quad.getBezPts();
		quadb[ 0 ] = 3.0 * ( bezPts[ 1 ] - bezPts[ 0 ] );
		quadb[ 1 ] = 3.0 * ( bezPts[ 2 ] - bezPts[ 1 ] );
		quadb[ 2 ] = 3.0 * ( bezPts[ 3 ] - bezPts[ 2 ] );
		return( quad );
	}
	
	

}

