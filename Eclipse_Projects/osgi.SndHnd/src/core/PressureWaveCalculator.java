




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

public class PressureWaveCalculator {
	
	protected static final double INTERVAL_HALF_LENGTH = /* 1.0 / 3.2 */ ( 1.0 / 58.0 ) * 2.0;

	protected static final int SAMPLE_LEN = 25;
	
	
	public static double calcPressureWaveValueRaw( TrackFrame tr , double elapsed_time_seconds )
	{
		final int core = 0;
		double beat_number = SongData.getBeatNumber( elapsed_time_seconds , core );
		NoteDesc desc = tr.getNoteDescStated( beat_number , core );
		int noteIndex = tr.getCurrentNoteIndex( core ) + 1;
		
		if( beat_number < desc.getStartBeatNumber() )
		{
			noteIndex--;
		}
		
		if( noteIndex == 0 )
		{
			double max = - getNoteIndexWv( 1 );
			
			double startTimeSeconds = 0.0;
			
			double endTimeSeconds = SongData.getElapsedTimeForBeatBeat( desc.getStartBeatNumber() , core ); 
			
			double u = ( elapsed_time_seconds - startTimeSeconds ) /
				( endTimeSeconds - startTimeSeconds );
			
			double ret = 0.0 * (1-u) + u * max;
			
			return( ret );
		}
		
		if( noteIndex == ( tr.getNotes().size() ) )
		{
			double max = - getNoteIndexWv( noteIndex - 1 );
			
			double startTimeSeconds = SongData.getElapsedTimeForBeatBeat( desc.getStartBeatNumber() , core );
			
			double endTimeSeconds = SongData.getElapsedTimeForBeatBeat( SongData.measuresStore.getTotalNumberBeats( SongData.NUM_MEASURES )  , core);
			
			double u = ( elapsed_time_seconds - startTimeSeconds ) /
				( endTimeSeconds - startTimeSeconds );
			
			double ret = (1-u) * max + 0.0 * u;
			
			return( ret );
		}
		
		return( getNoteIndexWv( noteIndex ) );
	}
	
	
	protected static double getNoteIndexWv( int noteIndex )
	{
		return( ( noteIndex % 2 ) * 2 - 1 );
	}
	

	protected static double evalLowPassFilter(TrackFrame tr , double param) {
		double intervalStart = param - INTERVAL_HALF_LENGTH;
		double intervalEnd = param + INTERVAL_HALF_LENGTH;
		
		double b0 = calcApproxB0( tr , SAMPLE_LEN , intervalStart , intervalEnd );
		double b2 = calcApproxB2( tr , SAMPLE_LEN , intervalStart , intervalEnd );
		double b1 = calcApproxB1( tr , SAMPLE_LEN + 2 , b0 , b2 , intervalStart , intervalEnd );
		
		double u = 0.5;
		
		double b10 = ( 1 - u ) * b0 + u * b1;
		double b11 = ( 1 - u ) * b1 + u * b2;
		
		double b20 = ( 1 - u ) * b10 + u * b11;
		
		return( b20 );
	}

	
	protected static double calcApproxB0( TrackFrame tr , int len , double intervalStart , double intervalEnd )
	{
		double tot = 0.0;
		double totParam = 0.0;
		int count;
		for( count = 0 ; count < len ; count++ )
		{
			double uv = ( (double) count ) / ( len - 1 );
			double u = ( 1 - uv ) * ( -1.0 ) + ( uv ) * ( 1.0 );
			double param = ( 1 - u ) * intervalStart + u * intervalEnd;
			double multp = 1.0 - Math.abs( u );
			tot += calcPressureWaveValueRaw( tr , param ) * multp;
			totParam += multp;
		}
		return( tot / totParam );
	}
	
	protected static double calcApproxB2( TrackFrame tr , int len , double intervalStart , double intervalEnd )
	{
		double tot = 0.0;
		double totParam = 0.0;
		int count;
		for( count = 0 ; count < len ; count++ )
		{
			double uv = ( (double) count ) / ( len - 1 );
			double u = ( 1 - uv ) * ( 0.0 ) + ( uv ) * ( 2.0 );
			double param = ( 1 - u ) * intervalStart + u * intervalEnd;
			double multp = 1.0 - Math.abs( 1.0 - u );
			tot += calcPressureWaveValueRaw( tr , param ) * multp;
			totParam += multp;
		}
		return( tot / totParam );
	}

	protected static double calcApproxB1(TrackFrame tr, int len, double b0, double b2, double intervalStart, double intervalEnd ) {
		int count = 0;
		double ptTotal = 0.0;

		for (count = 1; count < ( len - 1 ); count++) {
			double uv = ( (double) count ) / ( len - 1 );
			
			double cnstb10 = (1 - uv) * b0;
			double multb10 = uv;

			double cnstb11 = (uv) * b2;
			double multb11 = (1 - uv);

			double cnstb20 = (1 - uv) * cnstb10 + (uv) * cnstb11;
			double multb20 = (1 - uv) * multb10 + (uv) * multb11;
			
			double u = (1-uv) * intervalStart + uv * intervalEnd;
			double eval = calcPressureWaveValueRaw( tr , u );

			ptTotal += ( eval - cnstb20 ) / multb20;
		}

		return (ptTotal / len);
	}
	
	
	public static double calcPressureWaveValue( TrackFrame tr , double elapsed_time_seconds )
	{
		double orig = calcPressureWaveValueRaw( tr , elapsed_time_seconds );
		double lowPass = evalLowPassFilter( tr , elapsed_time_seconds );
		double delta = orig - lowPass;
		
		if( delta > 1.0 )
		{
			delta = 1.0;
		}
		
		if( delta < -1.0 )
		{
			delta = -1.0;
		}
		
		/* if( Math.abs( orig ) > 0.98 )
		{
			System.out.println( delta + " " + orig );
		}*/
		
		return( delta );
	}

	
}
