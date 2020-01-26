




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







package core;

/**
 * Common implementations for compositing of functional Bezier curves and related algorithms.
 * Algorithms are mostly implemented in a manner that is independent of the degree of the Bezier curve.
 * 
 * Note: Using Blossoms might provide a more unified approach to this, but Blossoms are also slower to execute.
 * 
 * @author tgreen
 *
 */
public class CalcCompositeCurve {

	// Test array.  No longer used.
	// public static final double[] WV_RATE_PER_SECOND_AS_FUN_OF_BEAT_NUMBER = { 1.0 , 1.5 , 0.9 , 0.9 };
	
	// Test array.  No longer used.
	// public static final double[] BEAT_NUMBER_AS_FUN_OF_SECONDS = { 0.0 , 1.1 , 1.9 , 3.0 };
	
	
	/**
	 * Computes an initial compositing generation by compositing a functional Bezier curve with a linear functional Bezier curve.
	 * See:  https://courses.lumenlearning.com/wmopen-collegealgebra/chapter/introduction-compositions-of-functions/
	 * @param A The Bezier points of the input functional Bezier curve.
	 * @param a_b0 The input first Bezier point of the linear functional Bezier curve.
	 * @param a_b1 The input second Bezier point of the linear functional Bezier curve.
	 * @param domainStart The domain start.
	 * @param domainEnd The domain end.
	 * @param rangeStart The range start.
	 * @param rangeEnd The range end.
	 * @return The Bezier points of the functional Bezier curve of the composite of curve A with the linear curve.
	 */
	public static double[] calcInitialGen( double[] A , double a_b0 , double a_b1 , double domainStart , double domainEnd ,
			double rangeStart , double rangeEnd )
	{
		int len = A.length;
		int cnt;
		double[] ret = new double[ len ];
		double delta = a_b1 - a_b0;
		for( cnt = 0 ; cnt < len ; cnt++ )
		{
			double ap = ( A[ cnt ] - domainStart ) * ( rangeEnd - rangeStart ) / ( domainEnd - domainStart ) + rangeStart;
			ret[ cnt ] = a_b0 + delta * ap;
		}
		//double b0p = ( a_b0 - domainStart ) * ( rangeEnd - rangeStart ) / ( domainEnd - domainStart ) + rangeStart;
		//double b1p = ( a_b1 - domainStart ) * ( rangeEnd - rangeStart ) / ( domainEnd - domainStart ) + rangeStart;
		//double[] ret = calcSubsection( B , b0p , b1p );
		return( ret );
	}
	
	/**
	 * Computes an initial compositing generation by compositing a functional Bezier curve with the linear initial DeCasteljau-like step of another functional Bezier curve.
	 * See:  https://courses.lumenlearning.com/wmopen-collegealgebra/chapter/introduction-compositions-of-functions/
	 * @param B Bezier points of an input functional Bezier curve.
	 * @param A Bezier points of an input functional Bezier curve.
	 * @param domainStart The dimain start.
	 * @param domainEnd The domain end.
	 * @param rangeStart The range start.
	 * @param rangeEnd The range end.
	 * @return Returns a composite of curve A with the initial DeCasteljau-like step of curve B.
	 */
	public static Object[] calcInitialGen( double[] B , double[] A , double domainStart , double domainEnd ,
			double rangeStart , double rangeEnd )
	{
		int len = B.length;
		int len1 = len - 1;
		Object[] ret = new Object[ len1 ];
		int count;
		for( count = 0 ; count < len1 ; count++ )
		{
			double[] pt = calcInitialGen( A , B[ count ] , B[ count + 1 ] , domainStart , domainEnd ,
					rangeStart , rangeEnd );
			ret[ count ] = pt;
		}
		return( ret );
	}
	
	/**
	 * Calculates a subsequent compositing generation by essentially performing a subsequent DeCasteljau-like step on the input curves.
	 * See:  https://courses.lumenlearning.com/wmopen-collegealgebra/chapter/introduction-compositions-of-functions/
	 * @param u The parameter values to use with each pair of curves in the step.
	 * @param b0 Bezier points of input functional Bezier curve.
	 * @param b1 Bezier points of input functional Bezier curve.  It is assumed that b1 has the same degree as b0.
	 * @return Returns the Bezier points of a degree-raised Bezier curve by performing a DeCasteljau-like step on b0 and b1.
	 */
	public static double[] calcSubsequentGen( double[] u , double[] b0 , double[] b1 )
	{
		int ulen = u.length;
		int blen = b0.length;
		int flen = ( ( ulen - 1 ) + ( blen - 1 ) ) + 1;
		double[] ret = new double[ flen ];
		int count;
		int count1;
		for( count = 0 ; count < ulen ; count++ )
		{
			double ui = u[ count ];
			double ui1 = 1.0 - ui; // Affine transform on ui points.
			for( count1 = 0 ; count1 < blen ; count1++ )
			{
				ret[ count + count1 ] += ui * b0[ count1 ] + ui1 * b1[ count1 ];
			}
		}
		return( ret );
	}
	
	/**
	 * Calculates a subsequent compositing generation by essentially performing a subsequent DeCasteljau-like step on the input curves.
	 * See:  https://courses.lumenlearning.com/wmopen-collegealgebra/chapter/introduction-compositions-of-functions/
	 * @param b0 Bezier points of input functional Bezier curve.
	 * @param b1 Bezier points of input functional Bezier curve.  It is assumed that b1 has the same degree as b0.
	 * @return Returns the Bezier points of a degree-raised Bezier curve by performing a DeCasteljau-like step on b0 and b1.
	 */
	public static double[] calcSubsequentGen( double[] b0 , double[] b1 )
	{
		int len = b0.length;
		double dlen = len;
		double[] ret = new double[ len + 1 ];
		int cnt;
		for( cnt = 1 ; cnt < len ; cnt++ )
		{
			double u = cnt / dlen;
			double r = (1-u) * b0[ cnt - 1 ] + u * b1[ cnt ];
			ret[ cnt ] = r;
		}
		
		ret[ 0 ] = b0[ 0 ];
		ret[ len ] = b1[ len - 1 ];
		return( ret );
	}
	
	/**
	 * Calculates a subsequent compositing generation by essentially performing a subsequent DeCasteljau-like step on the input curves.
	 * See:  https://courses.lumenlearning.com/wmopen-collegealgebra/chapter/introduction-compositions-of-functions/
	 * @param b An array of input Bezier points for functional Bezier curves.  This is essentially a Bezier point array of Bezier point arrays.
	 * @return The result of performing one DeCasteljau-like step.
	 */
	public static Object[] calcSubsequentGen( Object[] b )
	{
		int len = b.length;
		int len1 = len - 1;
		Object[] ret = new Object[ len1 ];
		int count;
		for( count = 0 ; count < len1 ; count++ )
		{
			double[] pt = calcSubsequentGen( (double[])( b[ count ] ) , (double[])( b[ count + 1 ] ) );
			ret[ count ] = pt;
		}
		return( ret );
	}
	
	/**
	 * Computes a functional Bezier curve that is the algebraic composite of two functional Bezier curves.
	 * See:  https://courses.lumenlearning.com/wmopen-collegealgebra/chapter/introduction-compositions-of-functions/
	 * @param A The Bezier points of the input functional Bezier curve.
	 * @param B The Bezier points of the input functional Bezier curve.
	 * @param domainStart The start domain of the curve.
	 * @param domainEnd The end domain of the curve.
	 * @param rangeStart The start range of the curve.
	 * @param rangeEnd The end range of the curve.
	 * @return The Bezier points of the resulting functional Bezier curve.
	 */
	public static double[] genCompositeAB( double[] A , double[] B , double domainStart , double domainEnd ,
			double rangeStart , double rangeEnd )
	{
		Object[] rv = calcInitialGen( B , A , domainStart , domainEnd , rangeStart , rangeEnd );
		while( rv.length > 1 )
		{
			rv = calcSubsequentGen( rv );
		}
		return( (double[])( rv[ 0 ] ) );
	}
	
	/**
	 * Computes the functional Bezier curve for the wave number as a function of seconds by integration.
	 * @param wv_rate_per_second_as_fun_of_seconds The input Bezier points for input functional Bezier cure of the wave rate as a function of seconds.
	 * @return The Bezier points for the functional Bezier curve for the integral of the input wave rate curve.
	 */
	public static double[] getWvNumberAsFunOfSeconds( double[] wv_rate_per_second_as_fun_of_seconds )
	{
		return( integrateCurve( wv_rate_per_second_as_fun_of_seconds ) );
	}
	
	/**
	 * Translates a functional Bezier curve.
	 * @param crv The Bezier points of the functional Bezier curve.  The translation is applied directly to the points.
	 * @param trans The translation factor.
	 */
	public static void translateCurve( double[] crv , double trans )
	{
		int sz = crv.length;
		int count;
		for( count = 0 ; count < sz ; count++ )
		{
			crv[ count ] = crv[ count ] + trans;
		}
	}
	
	/**
	 * Scales a functional Bezier curve.
	 * @param crv The Bezier points of the functional Bezier curve.  The scaling is applied directly to the points.
	 * @param scale The scaling factor.
	 */
	public static void scaleCurve( double[] crv , double scale )
	{
		int sz = crv.length;
		int count;
		for( count = 0 ; count < sz ; count++ )
		{
			crv[ count ] = crv[ count ] * scale;
		}
	}
	
	/**
	 * Computes the functional Bezier curve that is the integral of the input curve..
	 * @param curve The input Bezier points for input functional Bezier curve to be integrated.
	 * @return The Bezier points for the functional Bezier curve for the integral of the input curve.
	 */
	public static double[] integrateCurve( double[] curve )
	{
		final int len = curve.length;
		final double div = len;
		int count;
		final double[] integCurve = new double[ len + 1 ];
		integCurve[ 0 ] = 0.0;
		for( count = 0 ; count < len ; count++ )
		{
			integCurve[ count + 1 ] = integCurve[ count ] + ( curve[ count ] ) / div;
		}
		return( integCurve );
	}
	
	/**
	 * Computes one iteration of the DeCasteljau algorithm on a functional Bezier curve.
	 * @param curve The Bezier points of the input functional Bezier curve.
	 * @param u The parameter at which to execute the DeCasteljau algorithm.
	 * @return The array of results from computing one iteration of the DeCasteljau algorithm.
	 */
	public static double[] iterOnce( double[] curve , double u )
	{
		final int len = curve.length;
		final double[] ret = new double[ len - 1 ];
		int count;
		for( count = 0 ; count < len - 1 ; count++ )
		{
			ret[ count ] = (1-u) * curve[ count ] + u * curve[ count + 1 ];
		}
		return( ret );
	}
	
	/**
	 * Calculates a subsection of a functional Bezier curve.
	 * @param curve The Bezier points of the input functional Bezier curve.
	 * @param u0 The input parameter value of the start of the subsection.
	 * @param u1 The input parameter value of the end of the subsection.
	 * @return The Bezier points of the functional Bezier curve of the subsection.
	 */
	public static double[] calcSubsection( double[] curve , double u0 , double u1 )
	{
		int len = curve.length;
		double[] ret = new double[ len ];
		int count;
		for( count = 0 ; count < len ; count++ )
		{
			int cnt;
			double[] tmp = curve;
			for( cnt = 0 ; cnt < count ; cnt++ )
			{
				tmp = iterOnce( tmp , u1 );
			}
			while( tmp.length > 1 )
			{
				tmp = iterOnce( tmp , u0 );
			}
			ret[ count ] = tmp[ 0 ];
		}
		return( ret );
	}
	
	/**
	 * Returns a copy of the Bezier points of a functional Bezier curve.
	 * @param curve The input Bezier points.
	 * @return A copy of the input Bezier points.
	 */
	public static double[] copy( double[] curve )
	{
		int len = curve.length;
		int count;
		double[] ret = new double[ len ];
		for( count = 0 ; count < len ; count++ )
		{
			ret[ count ] = curve[ count ];
		}
		return( ret );
	}
	
	
}

