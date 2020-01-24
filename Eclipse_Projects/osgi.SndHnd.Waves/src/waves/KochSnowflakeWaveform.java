





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







package waves;


import gredit.GWaveForm;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import core.NonClampedCoefficient;
import core.WaveForm;


/**
 * Fractal waveform based on a Koch snowflake.  Adapted from the book "The Fractal Geometry of Nature" by Benoit Mandelbrot.
 * 
 * See <A href="https://en.wikipedia.org/wiki/Koch_snowflake">https://en.wikipedia.org/wiki/Koch_snowflake</A>
 * 
 * @author tgreen
 *
 */
public class KochSnowflakeWaveform extends WaveForm implements Externalizable {
	
	/**
	 * The proportion of the iteration edge at which to start the triangle for the next iteration.
	 */
	protected double cutStart;
	
	/**
	 * The proportion of the iteration edge at which to end the triangle for the next iteration.
	 */
	protected double cutEnd;
	
	/**
	 * Displacement at the high-parameter end of the triangle for the next iteration.
	 */
	protected double displacementMultiplierHi;
	
	/**
	 * Displacement at the low-parameter end of the triangle for the next iteration.
	 */
	protected double displacementMultiplierLo;
	
	/**
	 * The smallest allowed delta in the parameter before evaluation stops.
	 */
	protected double minDelta;

	/**
	 * Decade multiplier from the offset of one iteration to the offset of the next iteration.
	 */
	protected double decadeMultiplier = 0.95;
	
	/**
	 * Temporary evaluation parameter value.
	 */
	protected double evalX;
	
	

	/**
	 * Constructs the waveform.
	 * @param _cutStart The proportion of the iteration edge at which to start the triangle for the next iteration.
	 * @param _cutEnd   The proportion of the iteration edge at which to end the triangle for the next iteration.
	 * @param _displacementMultiplierHi Displacement at the high-parameter end of the triangle for the next iteration.
	 * @param _displacementMultiplierLo Displacement at the low-parameter end of the triangle for the next iteration.
	 * @param _minDelta The smallest allowed delta in the parameter before evaluation stops.
	 * @param _decadeMultiplier Decade multiplier from the offset of one iteration to the offset of the next iteration.
	 */
	public KochSnowflakeWaveform(
			double _cutStart, double _cutEnd, double _displacementMultiplierHi , 
			double _displacementMultiplierLo , 
			double _minDelta , double _decadeMultiplier ) {
		super();
		cutStart = _cutStart;
		cutEnd = _cutEnd;
		displacementMultiplierHi = _displacementMultiplierHi;
		displacementMultiplierLo = _displacementMultiplierLo;
		minDelta = _minDelta;
		decadeMultiplier = _decadeMultiplier;
	}

	@Override
	public double eval(double phase_distorted_param) {
		
		double pdel = phase_distorted_param - (int)( phase_distorted_param );
		
		if( pdel > 0.5 )
		{
			double ind = 2.0 * ( pdel - 0.5 );
			evalX = ind;
			if( ind < 0.5 )
				return( - handleGeneral( 0.0 , 0.0 , 0.5 , 1.0 , 1.0 , 1 ) );
			return( - handleGeneral( 0.5 , 1.0 , 1.0 , 0.0 , 1.0 , 1 ) );
		}
		else
		{
			double ind = 2.0 * pdel;
			evalX = ind;
			if( ind < 0.5 )
				return( handleGeneral( 0.0 , 0.0 , 0.5 , 1.0 , 1.0 , 1 ) );
			return( handleGeneral( 0.5 , 1.0 , 1.0 , 0.0 , 1.0 , 1 ) );
		}
	}
	
	/**
	 * Evaluates the part of the edge of a snowflake iteration containing the offset triangle.
	 * @param x1 The start parameter of the edge.
	 * @param y1 The start function value of the edge.
	 * @param x2 The end parameter of the edge.
	 * @param y2 The end function value of the edge.
	 * @param ival The parameter at which to evaluate.
	 * @param itnum The iteration number.
	 * @return The evaluated snowflake at the parameter.
	 */
	protected double handleDisplacement( double x1 , double y1 , double x2 , double y2 , double ival , int itnum )
	{	
		double delX = x2 - x1;
		double delY = y2 - y1;
		
		double vectX = -delY;
		double vectY = delX;
		
		double maxReach = Math.min( Math.abs( delX / vectX ) , Math.abs( delY / vectY ) );
		
		double reachLo = 0.5 * maxReach * displacementMultiplierLo;
		double reachHi = 0.5 * maxReach * displacementMultiplierHi;
		
		double dispXLo = ( x1 + x2 ) / 2.0 - reachLo * vectX;
		double dispXHi = ( x1 + x2 ) / 2.0 + reachHi * vectX;
		double dispY = ( y1 + y2 ) / 2.0;
		// double u = Math.random();
		//double uLo = 0.5 - 0.5 * displacementMultiplierLo;
		//double uHi = 0.5 + 0.5 * displacementMultiplierHi;
		// double uLo = Math.random();
		// double uHi = Math.random();
		double uLo = ival;
		double uHi = ival;
		double dispYLo = (1-uLo) *  dispY + uLo * 0.0;
		double dispYHi = (1-uHi) * dispY + uHi * 1.0;
		
		if( evalX < dispXLo )
			return( handleGeneral( x1 , y1 , dispXLo , dispYLo , handleDecadeMultiply( ival , itnum ) , itnum++ ) );
		
		if( evalX < dispXHi )
			return( handleGeneral( dispXLo , dispYLo , dispXHi , dispYHi , handleDecadeMultiply( ival , itnum ) , itnum++ ) );
		
		return( handleGeneral( dispXHi , dispYHi , x2 , y2, handleDecadeMultiply( ival , itnum ) , itnum++ ) );
	}
	
	/**
	 * General routine for evaluating along one edge of a snowflake iteration.
	 * @param x1 The start parameter of the edge.
	 * @param y1 The start function value of the edge.
	 * @param x2 The end parameter of the edge.
	 * @param y2 The end function value of the edge.
	 * @param ival The parameter at which to evaluate.
	 * @param itnum The iteration number.
	 * @return The evaluated snowflake at the parameter.
	 */
	protected double handleGeneral( double x1 , double y1 , double x2 , double y2 , double ival , int itnum )
	{	
		if( /* ( ( y2 - y1 ) < minDelta ) || */ ( ( x2 - x1 ) < minDelta ) )
		{
			return( ( y1 + y2 ) / 2.0 );
		}
		
		double cut1x = ( 1 - cutStart ) * x1 + cutStart * x2;
		double cut2x = ( 1 - cutEnd ) * x1 + cutEnd * x2;
		
		double cut1y = ( 1 - cutStart ) * y1 + cutStart * y2;
		double cut2y = ( 1 - cutEnd ) * y1 + cutEnd * y2;
		
		if( evalX < cut1x )
			return( handleGeneral( x1 , y1 , cut1x , cut1y , handleDecadeMultiply( ival , itnum ) , itnum + 1 ) );
		
		if( evalX < cut2x )
			return( handleDisplacement( cut1x , cut1y , cut2x , cut2y , ival , itnum ) );
		
		return( handleGeneral( cut2x , cut2y , x2 , y2 , handleDecadeMultiply( ival , itnum ) , itnum + 1 ) );
		
	}
	
	/**
	 * Performs a decade multiply to get the offset amount for the next iteration.
	 * @param dec The offset amount for the previous iteration.
	 * @param itnum The iteration number.
	 * @return The offset amount for the next iteration.
	 */
	protected double handleDecadeMultiply( double dec , int itnum )
	{
		int count;
		double ret = dec;
		/* for( count = 0 ; count < itnum ; count++ )
		{
			ret = ret * decadeMultiplier;
		} */
		ret = ret * decadeMultiplier;
		return( ret );
	}
	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GKochSnowflakeWaveform wv = new GKochSnowflakeWaveform();
		wv.load(
				cutStart, cutEnd, displacementMultiplierHi , 
				displacementMultiplierLo , 
				minDelta , decadeMultiplier );
		s.put(this, wv);
		return( wv );
	}
	
	@Override
	public NonClampedCoefficient genClone()
	{
		return( this );
	}
	
	/**
	* Reads the node from serial storage.
	*/
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			cutStart = myv.getDouble( "CutStart" );
			cutEnd = myv.getDouble( "CutEnd" );
			displacementMultiplierHi = myv.getDouble( "DisplacementMultiplierHi" );
			displacementMultiplierLo = myv.getDouble( "DisplacementMultiplierLo" );
			minDelta = myv.getDouble( "MinDelta" );
			decadeMultiplier = myv.getDouble( "DecadeMultiplier" );
			
		}
		catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	/**
	* Writes the node to serial storage.
	* @serialData TBD.
	*/
	public void writeExternal(ObjectOutput out) throws IOException {
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);
		
		myv.setDouble( "CutStart" , cutStart );
		myv.setDouble( "CutEnd" , cutEnd );
		myv.setDouble( "DisplacementMultiplierHi" , displacementMultiplierHi );
		myv.setDouble( "DisplacementMultiplierLo" , displacementMultiplierLo );
		myv.setDouble( "MinDelta" , minDelta );
		myv.setDouble( "DecadeMultiplier" , decadeMultiplier );

		out.writeObject(myv);
	}
	

}

