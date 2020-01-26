




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


import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;


import bezier.CubicBezierCurve;

/**
 * Given an input note (almost always a SampledAgent note from a digitized source),
 * estimates the shape of the note's envelope and dumps the results to a file.
 * 
 * @author tgreen
 *
 */
public class EnvelopeAnalyzer {

	/**
	 * The input note.
	 */
	protected NoteDesc inputNote;
	
	/**
	 * The number of samples over which to average each point on the estimated envelope.
	 */
	public static final int NUM_STEPS_SAMPLE = 20002;
	
	/**
	 * The number of points to be generated on the estimated envelope.
	 */
	public static final int NUM_STEPS = 1002;
	
	/**
	 * The time period over which to average each point on the estimated envelope.
	 */
	public static final double DELTA_SECS = 1.0 / 20.0;

	/**
	 * Constructor.
	 * @param _note The input note.
	 */
	public EnvelopeAnalyzer(NoteDesc _note) {
		inputNote = _note;
	}

	/**
	 * Estimates the mean of the square of the envelope size.
	 * @param startTimeSeconds The start time of the interval over which to estimate.
	 * @param endTimeSeconds The end time of the interval over which to estimate.
	 * @return The calculated mean-square.
	 */
	protected double getEnvelopeSize( final double startTimeSeconds , final double endTimeSeconds )
	{
		int count;
		double avg = 0.0;
		
		for( count = 0 ; count < NUM_STEPS_SAMPLE ; count++ )
		{
			double u = ( (double) count ) / ( NUM_STEPS_SAMPLE - 1 );
			double time = (1-u) * startTimeSeconds + u * endTimeSeconds;
			avg += evalTime( time );
		}
		
	    avg = avg / NUM_STEPS_SAMPLE;
	    
	    double delta = 0.0;
	    
	    for( count = 0 ; count < NUM_STEPS_SAMPLE ; count++ )
		{
			double u = ( (double) count ) / ( NUM_STEPS_SAMPLE - 1 );
			double time = (1-u) * startTimeSeconds + u * endTimeSeconds;
			double del = evalTime( time ) - avg;
			delta += del * del;
		}
	    
	    // double res = Math.sqrt( delta / NUM_STEPS_SAMPLE );
	    double res = delta / NUM_STEPS_SAMPLE;
	    
	    return( res );
	}
	
	/**
	 * Evaluates the WaveForm at a particular time.
	 * @param time The time at which to evaluate in elapsed seconds.
	 * @return The result of evaluating the WaveForm.
	 */
	protected double evalTime( double time )
	{
		final int core = 0;
		final double waveNumber = inputNote.getWaveNumberElapsedTimeSeconds( time , core );
		final double val = inputNote.getWaveform( core ).eval( waveNumber );
		return( val );
	}
	
	
	/**
	 * Estimates the envelope, and dumps the results to a file.
	 * @throws Throwable
	 */
	public void calculate()  throws Throwable {
		
		final int core = 0;
		
		PrintStream ps = new PrintStream( new FileOutputStream( "/users/thorngreen/OutputResults.txt" ) );
		
		inputNote.updateWaveInfo( core );
		
		double startBeatNumber = inputNote.getActualStartBeatNumber();
		double endBeatNumber = inputNote.getActualEndBeatNumber();
		
		double startSeconds = SongData.getElapsedTimeForBeatBeat( startBeatNumber , core );
		double endSeconds = SongData.getElapsedTimeForBeatBeat( endBeatNumber , core );
		
		double maxEnvelope = 0.0;
		double minEnvelope = 1E+20;
		double maxParam = 0.0;
		
		int count;
		ArrayList<InterpolationPoint> arr = new ArrayList<InterpolationPoint>();
		System.out.println( "Outputs : " );
		for( count = 0 ; count < NUM_STEPS ; count++ )
		{
			double u = ( (double) count ) / ( NUM_STEPS - 1 );
			double time = (1-u) * startSeconds + u * endSeconds;
			double val = getEnvelopeSize( time , time + DELTA_SECS );
			maxEnvelope = Math.max( val , maxEnvelope );
			if( val > 1E-10 )
			{
				minEnvelope = Math.min( minEnvelope , val );
				maxParam = u;
			}
			System.out.println( "Envelope Val " + val );
			InterpolationPoint ppt = new InterpolationPoint(u, val);
			arr.add( ppt );
		}
		
		if( maxParam < 1E-9 )
		{
			maxParam = 1.0;
		}
		
		ArrayList<InterpolationPoint> arr2 = new ArrayList<InterpolationPoint>();
		
		System.out.println( "Running Phase #2" );
		int sz = arr.size();
		for( count = 0 ; count < sz ; count++ )
		{
			InterpolationPoint pt = arr.get( count );
			if( pt.getParam() <= maxParam )
			{
				InterpolationPoint intp = new InterpolationPoint( pt.getParam() / maxParam ,
					( pt.getValue() - minEnvelope ) / ( maxEnvelope - minEnvelope ) );
				arr2.add( intp );
			}
		}
		
		maxParam = 0.0;
		ArrayList<InterpolationPoint> arr3 = new ArrayList<InterpolationPoint>();
		InterpolationPoint apt = null;
		InterpolationPoint bpt = null;
		System.out.println( "Running Phase #3" );
		sz = arr2.size();
		for( count = 0 ; count < sz ; count++ )
		{
			InterpolationPoint pt = arr2.get( count );
			if( pt.getValue() >= 0.15 )
			{
				bpt = pt;
			}
			if( pt.getValue() >= 0.1 )
			{
				apt = pt;
				arr3.add( pt );
				maxParam = pt.getParam();
			}
		}
		
		
		double mp = maxParam * 11.0 / 9.0;
		
		
		CubicBezierCurve crv = new CubicBezierCurve();
		crv.setStartParam( maxParam );
		crv.setEndParam( mp );
		
		double[] bezPts = crv.getBezPts();
		
		bezPts[ 3 ] = 0.0;
		bezPts[ 2 ] = 0.0;
		bezPts[ 0 ] = apt.getValue();
		double slope = ( bpt.getValue() - apt.getValue() ) 
			/ ( bpt.getParam() - apt.getParam() );
		double delta = slope * ( mp - maxParam );
		bezPts[ 1 ] = bezPts[ 0 ] + delta / 3.0;
		
		int MAX_CRV = 20;
		for( count = 1 ; count < MAX_CRV ; count++ )
		{
			double u = ( (double) count ) / ( MAX_CRV - 1 );
			double param = (1-u) * maxParam + u * mp;
			double ev = crv.eval( param );
			InterpolationPoint pt = new InterpolationPoint( param , ev );
			arr3.add( pt );
		}
		
		System.out.println( "Running Phase #4" );
		
		sz = arr3.size();
		for( count = 0 ; count < sz ; count++ )
		{
			InterpolationPoint pt = arr3.get( count );
			String out = "interps.add( new InterpolationPoint( " + 
				pt.getParam() / mp + " , " + pt.getValue() + " ) );";
			System.out.println( out );
			ps.println( out );
		}
		
		
		ps.close();

	}

}

