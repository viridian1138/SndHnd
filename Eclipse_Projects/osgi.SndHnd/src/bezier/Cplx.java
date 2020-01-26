




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

/**
 * Class representing a complex number.
 * 
 * @author tgreen
 *
 */
public class Cplx {
	
	/**
	 * The real component of the complex number.
	 */
	double real;
	
	/**
	 * The imaginary component of the complex number.
	 */
	double im;
	
	/**
	 * Constructs the complex number.
	 * @param _real The real component of the complex number.
	 * @param _im The imaginary component of the complex number.
	 */
	public Cplx( double _real , double _im )
	{
		real = _real;
		im = _im;
	}
	
	/**
	 * Gets the real component of the complex number.
	 * @return The real component of the complex number.
	 */
	public double getReal()
	{
		return( real );
	}
	
	/**
	 * Gets the imaginary component of the complex number.
	 * @return The imaginary component of the complex number.
	 */
	public double getIm()
	{
		return( im );
	}
	
	/**
	 * Constructs the complex number as the square root of a value.
	 * @param val The input value.
	 * @return The constructed complex number.
	 */
	public static Cplx sqrt( double val )
	{
		if( val < 0.0 )
		{
			return( new Cplx( 0.0 , Math.sqrt( Math.abs( val ) ) ) );
		}
		return( new Cplx( Math.sqrt( val ) , 0.0 ) );
	}
	
	/**
	 * Constructs the complex number.
	 * @param val The real component of the complex number.
	 * @return The constructed complex number.
	 */
	public static Cplx real( double val )
	{
		return( new Cplx( val , 0.0 ) );
	}
	
	/**
	 * Adds this complex number to the one in the parameter.
	 * @param b The complex number to add.
	 * @return The result of the addition.
	 */
	public Cplx add( Cplx b )
	{
		return( new Cplx( real + b.real , im + b.im ) );
	}
	
	/**
	 * Subtracts the complex number in the parameter from this complex number.
	 * @param b The complex number to subtract.
	 * @return The result of the subtraction.
	 */
	public Cplx sub( Cplx b )
	{
		return( new Cplx( real - b.real , im - b.im ) );
	}
	
	/**
	 * Divides this complex number by the value in the parameter.
	 * @param val The value by which to divide.
	 * @return The result of the division.
	 */
	public Cplx divBy( double val )
	{
		return( new Cplx( real / val , im / val ) );
	}
	
	/**
	 * multiplies this complex number to the one in the parameter.
	 * @param b The complex number to multiply.
	 * @return The result of the multiplication.
	 */
	public Cplx mult( Cplx b )
	{
		return( new Cplx( real * b.real - im * b.im , real * b.im + im * b.real ) );
	}
	
	/**
	 * Returns the logarithm of the complex number.
	 * @return The logarithm of the complex number.
	 */
	public Cplx ln()
	{
		final double abs = real * real + im * im;
		final double theta = Math.atan2( im , real );
		return( new Cplx( Math.log(abs) , theta ) );
	}
	
	/**
	 * Returns the square-root of the complex number.
	 * @return The square-root of the complex number.
	 */
	public Cplx sqrt()
	{
		final double abs = real * real + im * im;
		final double theta = Math.atan2( im , real );
		final double sqrtAbs = Math.sqrt( abs );
		return( new Cplx( sqrtAbs * Math.cos( theta / 2.0 ) , sqrtAbs * Math.sin( theta / 2.0 ) ) );
	}
	
	/**
	 * Returns the principal cube-root of the complex number.
	 * @return The principal cube-root of the complex number.
	 */
	public Cplx principalCubeRt()
	{
		final double abs = Math.sqrt( real * real + im * im );
		final double theta = Math.atan2( im , real );
		final double cubeRtAbs = Math.pow( abs , 1.0 / 3.0 );
		return( new Cplx( cubeRtAbs * Math.cos( theta / 3.0 ) , cubeRtAbs * Math.sin( theta / 3.0 ) ) );
	}
	
	/**
	 * Returns whether the imaginary component of the number is close to zero.
	 * @return Whether the imaginary component of the number is close to zero.
	 */
	public boolean isReal()
	{
		return( Math.abs( im ) < 1E-150 );
	}
	
	/**
	 * Debug-prints the complex number.
	 * @param str Prefix string to print.
	 * @param ps The stream to which to print the complex number.
	 */
	public void print( String str , PrintStream ps )
	{
		ps.println( "]]]]]]]]" );
		ps.println( str );
		ps.println( real );
		ps.println( im );
	}

	
}

