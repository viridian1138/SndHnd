




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



import bezier.BezierCubicNonClampedCoefficientFlatMultiCore;
import bezier.PiecewiseCubicMonotoneBezierFlatMultiCore;



/**
 * Test class for evaluating tempo calculations.
 * Only used for testing purposes.
 * 
 * @author tgreen
 *
 */
public class TestTempo {

	/**
	 * Main method.
	 * @param in Input args.  Not used.
	 */
	public static void main( String[] in )
	{
		PiecewiseCubicMonotoneBezierFlatMultiCore bez = new PiecewiseCubicMonotoneBezierFlatMultiCore();
		bez.getInterpolationPoints().add( new InterpolationPoint( 0.0 , 120.0 ) );
		bez.getInterpolationPoints().add( new InterpolationPoint( SongData.measuresStore.getTotalNumberBeats( SongData.NUM_MEASURES ) , 110.0 ) );
		bez.updateAll();
		SongData.TEMPO_BEATS_PER_MINUTE_CRV = new BezierCubicNonClampedCoefficientFlatMultiCore( bez );
		SongData.handleTempoUpdate(0);
		
		System.out.println( "Tempos : " );
		System.out.println( SongData.TEMPO_BEATS_PER_MINUTE_CRV.eval( 0.0 ,0) );
		System.out.println( SongData.TEMPO_BEATS_PER_MINUTE_CRV.eval( 0.5 / 60.0 ,0) );
		System.out.println( SongData.TEMPO_BEATS_PER_MINUTE_CRV.eval( 1.0 / 60.0 ,0) );
		System.out.println( SongData.TEMPO_BEATS_PER_MINUTE_CRV.eval( 1.5 / 60.0 ,0) );
		System.out.println( SongData.TEMPO_BEATS_PER_MINUTE_CRV.eval( 2.0 / 60.0 ,0) );
		System.out.println( SongData.TEMPO_BEATS_PER_MINUTE_CRV.eval( 2.5 / 60.0 ,0) );
		System.out.println( SongData.TEMPO_BEATS_PER_MINUTE_CRV.eval( 3.0 / 60.0 ,0) );
		System.out.println( SongData.TEMPO_BEATS_PER_MINUTE_CRV.eval( 3.5 / 60.0 ,0) );
		System.out.println( SongData.TEMPO_BEATS_PER_MINUTE_CRV.eval( 4.0 / 60.0 ,0) );
		
		System.out.println( "Beat Numbers : " );
		System.out.println( SongData.BEAT_NUMBER_FUNCTION_SECONDS.eval( 0.0 , 0 ) );
		System.out.println( SongData.BEAT_NUMBER_FUNCTION_SECONDS.eval( 0.5 , 0 ) );
		System.out.println( SongData.BEAT_NUMBER_FUNCTION_SECONDS.eval( 1.0 , 0 ) );
		System.out.println( SongData.BEAT_NUMBER_FUNCTION_SECONDS.eval( 1.5 , 0 ) );
		System.out.println( SongData.BEAT_NUMBER_FUNCTION_SECONDS.eval( 2.0 , 0 ) );
		System.out.println( SongData.BEAT_NUMBER_FUNCTION_SECONDS.eval( 2.5 , 0 ) );
		System.out.println( SongData.BEAT_NUMBER_FUNCTION_SECONDS.eval( 3.0 , 0 ) );
		System.out.println( SongData.BEAT_NUMBER_FUNCTION_SECONDS.eval( 3.5 , 0 ) );
		System.out.println( SongData.BEAT_NUMBER_FUNCTION_SECONDS.eval( 4.0 , 0 ) );
		
		System.out.println( "Appx Inverse Seconds : " );
		System.out.println( SongData.APPROX_SECONDS_FUNCTION_BEAT_NUMBER.eval( 0.0 ,0) );
		System.out.println( SongData.APPROX_SECONDS_FUNCTION_BEAT_NUMBER.eval( 1.0 ,0) );
		System.out.println( SongData.APPROX_SECONDS_FUNCTION_BEAT_NUMBER.eval( 2.0 ,0) );
		System.out.println( SongData.APPROX_SECONDS_FUNCTION_BEAT_NUMBER.eval( 3.0 ,0) );
		System.out.println( SongData.APPROX_SECONDS_FUNCTION_BEAT_NUMBER.eval( 4.0 ,0) );
		System.out.println( SongData.APPROX_SECONDS_FUNCTION_BEAT_NUMBER.eval( 5.0 ,0) );
		System.out.println( SongData.APPROX_SECONDS_FUNCTION_BEAT_NUMBER.eval( 6.0 ,0) );
		System.out.println( SongData.APPROX_SECONDS_FUNCTION_BEAT_NUMBER.eval( 7.0 ,0) );
		System.out.println( SongData.APPROX_SECONDS_FUNCTION_BEAT_NUMBER.eval( 8.0 ,0) );
		
		System.out.println( "Appx Beat Numbers Slope : " );
		System.out.println( SongData.BEAT_NUMBER_FUNCTION_SECONDS.evalSlope( 0.0 , 0 ) );
		System.out.println( SongData.BEAT_NUMBER_FUNCTION_SECONDS.evalSlope( 0.5 , 0 ) );
		System.out.println( SongData.BEAT_NUMBER_FUNCTION_SECONDS.evalSlope( 1.0 , 0 ) );
		System.out.println( SongData.BEAT_NUMBER_FUNCTION_SECONDS.evalSlope( 1.5 , 0 ) );
		System.out.println( SongData.BEAT_NUMBER_FUNCTION_SECONDS.evalSlope( 2.0 , 0 ) );
		System.out.println( SongData.BEAT_NUMBER_FUNCTION_SECONDS.evalSlope( 2.5 , 0 ) );
		System.out.println( SongData.BEAT_NUMBER_FUNCTION_SECONDS.evalSlope( 3.0 , 0 ) );
		System.out.println( SongData.BEAT_NUMBER_FUNCTION_SECONDS.evalSlope( 3.5 , 0 ) );
		System.out.println( SongData.BEAT_NUMBER_FUNCTION_SECONDS.evalSlope( 4.0 , 0 ) );
		
		System.out.println( "Iter Inverse Seconds A : " );
		System.out.println( getElapsedTimeForBeatBeatA( 0.0 ) );
		System.out.println( getElapsedTimeForBeatBeatA( 1.0 ) );
		System.out.println( getElapsedTimeForBeatBeatA( 2.0 ) );
		System.out.println( getElapsedTimeForBeatBeatA( 3.0 ) );
		System.out.println( getElapsedTimeForBeatBeatA( 4.0 ) );
		System.out.println( getElapsedTimeForBeatBeatA( 5.0 ) );
		System.out.println( getElapsedTimeForBeatBeatA( 6.0 ) );
		System.out.println( getElapsedTimeForBeatBeatA( 7.0 ) );
		System.out.println( getElapsedTimeForBeatBeatA( 8.0 ) );
		
		System.out.println( "Iter Inverse Seconds Final : " );
		System.out.println( SongData.getElapsedTimeForBeatBeat( 0.0 ,0) );
		System.out.println( SongData.getElapsedTimeForBeatBeat( 1.0 ,0) );
		System.out.println( SongData.getElapsedTimeForBeatBeat( 2.0 ,0) );
		System.out.println( SongData.getElapsedTimeForBeatBeat( 3.0 ,0) );
		System.out.println( SongData.getElapsedTimeForBeatBeat( 4.0 ,0) );
		System.out.println( SongData.getElapsedTimeForBeatBeat( 5.0 ,0) );
		System.out.println( SongData.getElapsedTimeForBeatBeat( 6.0 ,0) );
		System.out.println( SongData.getElapsedTimeForBeatBeat( 7.0 ,0) );
		System.out.println( SongData.getElapsedTimeForBeatBeat( 8.0 ,0) );
	}
	

	/**
	 * Gets the elapsed time for a particular beat number.  Only used for testing purposes.
	 * @param beat_number The input beat number.
	 * @return The elapsed time in seconds.
	 */
	public static double getElapsedTimeForBeatBeatA( final double beat_number )
	{
		double appx_sec = 0.5 + SongData.APPROX_SECONDS_FUNCTION_BEAT_NUMBER.eval( beat_number ,0);
		double appx_beat = SongData.BEAT_NUMBER_FUNCTION_SECONDS.eval( appx_sec , 0 );
		double slope = SongData.BEAT_NUMBER_FUNCTION_SECONDS.evalSlope( appx_sec , 0 );
		appx_sec = appx_sec - ( appx_beat - beat_number ) / slope;
		return( appx_sec );
	}

	
}

