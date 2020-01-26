




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







package kwaves;

import java.io.PrintStream;

import bezier.Cplx;
import bezier.CubicBezierCurve;
import bezier.QuadraticBezierCurve;
import bezier.QuarticBezierCurve;


/**
 * Tests the integration of sets of harmonics to determine if they can improve a potential match.
 * 
 * @author tgreen
 *
 */
public class GuitarAnalyzerIntegrator {
	
	/**
	 * The target harmonics to be matched.
	 */
	protected double[] targetHm;
	
	/**
	 * The target sub-harmonics (frequencies below the prescribed fundamental) to be matched.
	 */
	protected double[] targetSubHm;
	
	/**
	 * The original set of Guitar (or other instrument) harmonics.
	 */
	protected GuitarHarmonics orig;
	
	/**
	 * Gets the current minimum for the harmonic match.
	 */
	protected double currentMin;
	
	/**
	 * Whether the current minimum for the harmonic match has been calculated.
	 */
	protected boolean currentMinDefined = false;
	
	/**
	 * Whether a curve was found that improved the solution.
	 */
	protected boolean useCurve = false;
	
	/**
	 * The superposition value of the current best solution.
	 */
	protected double currentVal = 0.0;
	
	/**
	 * The multiplier parameter of the current best solution.
	 */
	protected double currentMult = 0.0;
	
	/**
	 * The parameter value of the current best solution.
	 */
	protected double currentParam = 0.0;
	
	/**
	 * The exponential power parameter for the current best solution.
	 */
	protected int currentPower = 0;
	
	/**
	 * The current set of Guitar (or other instrument) harmonics.
	 */
	protected GuitarHarmonics currentHm = null;
	
	/**
	 * Gets the initial minimum for the harmonic match.
	 */
	protected double initialMin = 0.0;
	
	
	/**
	 * Constructs the integrator.
	 * @param _targetHm The target harmonics to be matched.
	 * @param _targetSubHm The target sub-harmonics (frequencies below the prescribed fundamental) to be matched.
	 * @param _orig The original set of Guitar (or other instrument) harmonics.
	 */
	public GuitarAnalyzerIntegrator( double[] _targetHm , double[] _targetSubHm , GuitarHarmonics _orig )
	{
		targetHm = _targetHm;
		targetSubHm = _targetSubHm;
		orig = _orig;
	}
	
	
	/**
	 * Calculates the minimum superposition match for a potential set of harmonics. Stores parameters if the calculated minimum is below the current "best" minimum.
	 * @param testHarmonics The test harmonics for which to determine the potential match.
	 * @param numHarmonics The number of harmonics to contribute to the match.
	 * @param numSubHarmonics The number of sub-harmonics (frequencies below the prescribed fundamental) to contribute to the match.
	 * @param param The parameter value to be stored if the minimum is below the current "best" minimum.
	 * @param power The exponential power parameter to be stored if the minimum is below the current "best" minimum.
	 * @param ps Not used.
	 */
	public void calculateMin( GuitarHarmonics testHarmonics ,
			int numHarmonics , int numSubHarmonics , double param , int power , PrintStream ps )
	{
		if( !currentMinDefined )
		{
			calculateMin( 1.0 , testHarmonics , numHarmonics , numSubHarmonics , param , power , ps );
			
			if( !currentMinDefined )
			{
				throw( new RuntimeException( "Failed!!!!!!!!!!!!!!!!!!!!" ) );
			}
		}
		
		double mult = 0.05;
		for( mult = 0.05 ; mult < 3.1 ; mult += 0.05 )
		{
			calculateMin( mult , testHarmonics , numHarmonics , numSubHarmonics , param , power , ps );
		}
	}
	
	
	
	/**
	 * Calculates the minimum superposition match for a potential set of harmonics. Stores parameters if the calculated minimum is below the current "best" minimum.
	 * @param mult The multiplier parameter to be stored if the minimum is below the current "best" minimum.
	 * @param testHarmonics The test harmonics for which to determine the potential match.
	 * @param numHarmonics The number of harmonics to contribute to the match.
	 * @param numSubHarmonics The number of sub-harmonics (frequencies below the prescribed fundamental) to contribute to the match.
	 * @param param The parameter value to be stored if the minimum is below the current "best" minimum.
	 * @param power The exponential power parameter to be stored if the minimum is below the current "best" minimum.
	 * @param ps Not used.
	 */
	protected void calculateMin( double mult , GuitarHarmonics testHarmonics ,
			int numHarmonics , int numSubHarmonics , double param , int power , PrintStream ps )
	{
		final QuarticBezierCurve quartic = genCurve( mult , testHarmonics , numHarmonics , numSubHarmonics , ps );
		calculateMin( quartic , mult , param , power , testHarmonics , ps );
	}
	
	
	/**
	 * Finds the minimum of a quartic function by determining the roots of the cubic derivative of the quartic function. Stores parameters if the calculated minimum is below the current "best" minimum.
	 * @param quartic The quartic for which to find the minimum.
	 * @param mult The multiplier parameter to be stored if the minimum is below the current "best" minimum.
	 * @param param The parameter value to be stored if the minimum is below the current "best" minimum.
	 * @param power The exponential power parameter to be stored if the minimum is below the current "best" minimum.
	 * @param hm The set of Guitar (or other instrument) harmonics to be stored if the minimum is below the current "best" minimum.
	 * @param ps Not used.
	 */
	protected void calculateMin( QuarticBezierCurve quartic , double mult , double param , int power , GuitarHarmonics hm , PrintStream ps )
	{
		if( !currentMinDefined )
		{
			currentMin = quartic.eval( 0.0 );
			currentMinDefined = true;
			initialMin = currentMin;
			//ps.println( "Initial Min" );
			//ps.println( currentMin );
		}
		
		CubicBezierCurve cubic = quartic.generateDerivative();
		
		/* ps.println( "Cubic:" );
		ps.println( cubic.getBezPts()[ 0 ] );
		ps.println( cubic.getBezPts()[ 1 ] );
		ps.println( cubic.getBezPts()[ 2 ] );
		ps.println( cubic.getBezPts()[ 3 ] ); */
		
		Cplx[] cplx = cubic.findRoots( ps );
		final int len = cplx.length;
		int count;
		
		for( count = 0 ; count < len ; count++ )
		{
			Cplx cp = cplx[ count ];
			/* if( cp != null )
			{
				ps.println( "********" );
				ps.println( cp.getReal() );
				ps.println( cp.getIm() );
			} */
			if( ( cp != null ) && ( cp.isReal() ) )
			{
				double val = quartic.eval( cp.getReal() );
				/* ps.println( val ); */
				
				if( Math.abs( val ) < Math.abs( currentMin ) )
				{
					QuadraticBezierCurve quad = cubic.generateDerivative();
					if( quad.eval( cp.getReal() ) > 1E-7 )
					{
						currentMin = val;
						useCurve = true;
						currentVal = cp.getReal();
						currentMult = mult;
						currentParam = param;
						currentPower = power;
						currentHm = hm;
					}
					else
					{
						System.out.println( "Tossed Out Value Because Second Derivative Isn't Min." );
					}
				}
			}
		}
		
	}
	
	
	/**
	 * Returns a quartic curve approximating the degree of harmonic match versus the superposition coefficient.
	 * @param mult Superposition multiplier applied to the original harmonics.
	 * @param testHarmonics The test harmonics for which to determine the match.
	 * @param numHarmonics The number of harmonics to contribute to the match.
	 * @param numSubHarmonics The number of sub-harmonics (frequencies below the prescribed fundamental) to contribute to the match.
	 * @param ps Not used.
	 * @return The generated quartic curve.
	 */
	protected QuarticBezierCurve genCurve( double mult , GuitarHarmonics testHarmonics ,
			int numHarmonics , int numSubHarmonics , PrintStream ps )
	{
		QuarticBezierCurve ret = new QuarticBezierCurve();
		ret.setStartParam(0.0);
		ret.setEndParam(1.0);
		
		int count;
		
		for( count = 0 ; count < numHarmonics ; count++ )
		{
			final QuarticBezierCurve quar = genCurve( targetHm [ count ], orig.getHm( count ) , mult , testHarmonics.getHm( count ) , ps );
			int cta;
			for( cta = 0 ; cta < 5 ; cta++ )
			{
				ret.getBezPts()[ cta ] += quar.getBezPts()[ cta ];
			}
		}
		
		for( count = 0 ; count < numSubHarmonics ; count++ )
		{
			final QuarticBezierCurve quar = genCurve( targetSubHm[ count ] , orig.getSubHm( count ) , mult , testHarmonics.getSubHm( count ) , ps );
			int cta;
			for( cta = 0 ; cta < 5 ; cta++ )
			{
				ret.getBezPts()[ cta ] += quar.getBezPts()[ cta ];
			}
		}
		
		return( ret );
	}
	
	/**
	 * Generates a complex quartic function that models the closeness of match of a harmonic versus a function parameter describing the degree of superposition applied the proposed waveform to add.
	 * @param tar The target harmonic value to attempt to match.
	 * @param orig The original harmonic value before application of the function.
	 * @param mult Superposition multiplier applied to the original harmonics.
	 * @param testHarmonics The calculated harmonics of the function to be tested.
	 * @param ps Not used.
	 * @return The generated quartic function.
	 */
	protected QuarticBezierCurve genCurve( double tar , Cplx orig , double mult , Cplx testHarmonics , PrintStream ps )
	{
		final Cplx origMult = orig.mult( Cplx.real( mult ) );
		final Cplx b0_cplx = origMult;
		final Cplx b1_cplx = b0_cplx.add( testHarmonics );
		
		final double b0i = ( b0_cplx.getIm() ) * ( b0_cplx.getIm() );
		final double b1i = ( b0_cplx.getIm() ) * ( b1_cplx.getIm() );
		final double b2i = ( b1_cplx.getIm() ) * ( b1_cplx.getIm() );
		
		final double b0r = ( b0_cplx.getReal() ) * ( b0_cplx.getReal() );
		final double b1r = ( b0_cplx.getReal() ) * ( b1_cplx.getReal() );
		final double b2r = ( b1_cplx.getReal() ) * ( b1_cplx.getReal() );
		
		final double[] quadratic = { tar * tar - b0i - b0r , tar * tar - b1i - b1r , tar * tar - b2i - b2r };
		final double[] quadratic_bern = {  1.0 * quadratic[ 0 ] , 2.0 * quadratic[ 1 ] , 1.0 * quadratic[ 2 ] };
		
		final double[] quartic_bern = { 0.0 , 0.0 , 0.0 , 0.0 , 0.0 };
		int counta;
		int countb;
		for( counta = 0 ; counta < 3 ; counta++ )
		{
			for( countb = 0 ; countb < 3 ; countb++ )
			{
				quartic_bern[ counta + countb ] += quadratic_bern[ counta ] * quadratic_bern[ countb ];
			}
		}
		
		final double[] quartic = { quartic_bern[ 0 ] / 1.0 , quartic_bern[ 1 ] / 4.0 , quartic_bern[ 2 ] / 6.0 , quartic_bern[ 3 ] / 4.0 , quartic_bern[ 4 ] / 1.0 }; 
		QuarticBezierCurve quar = new QuarticBezierCurve();
		quar.setBezPts( quartic );
		quar.setStartParam(0.0);
		quar.setEndParam(1.0);
		
		return( quar );
	}



	/**
	 * Returns whether a curve was found that improved the solution.
	 * @return Whether a curve was found that improved the solution.
	 */
	public boolean isUseCurve() {
		return useCurve;
	}



	/**
	 * Gets the superposition value of the current best solution.
	 * @return The superposition value of the current best solution.
	 */
	public double getCurrentVal() {
		return currentVal;
	}



	/**
	 * Gets the multiplier parameter of the current best solution.
	 * @return The multiplier parameter of the current best solution.
	 */
	public double getCurrentMult() {
		return currentMult;
	}



	/**
	 * Gets the parameter value of the current best solution.
	 * @return The parameter value of the current best solution.
	 */
	public double getCurrentParam() {
		return currentParam;
	}
	
	
	/**
	 * Gets the exponential power parameter for the current best solution.
	 * @return The exponential power parameter for the current best solution.
	 */
	public int getCurrentPower() {
		return currentPower;
	}



	/**
	 * Gets the "waveform type" parameter for the current best solution.
	 * @return The "waveform type" parameter for the current best solution.
	 */
	public GuitarHarmonics getCurrentHm() {
		return currentHm;
	}



	/**
	 * Gets the current minimum for the harmonic match.
	 * @return The current minimum for the harmonic match.
	 */
	public double getCurrentMin() {
		return currentMin;
	}



	/**
	 * Gets the initial minimum for the harmonic match.
	 * @return The initial minimum for the harmonic match.
	 */
	public double getInitialMin() {
		return initialMin;
	}

	
}

