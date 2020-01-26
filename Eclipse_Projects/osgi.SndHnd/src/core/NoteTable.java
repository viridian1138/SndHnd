




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

import java.util.HashMap;



/**
 * 
 * Table for calculating a pitch from a note on a scale.  All frequencies in hertz.
 * 
 * @author thorngreen
 */
public class NoteTable {

	
	/**
	 * The standard 440 Hz. frequency for an western 12-tone diatonic A in the fourth octave (as numbered by SndHnd).
	 */
	public static final double A4_Intl = 440;
	
	/**
	 * Gets the typical western 12-tone diatonic scale pitch.  Presumes Key of C and A4 = 440 Hz.
	 * @param melodicIntervalNumber The melodic interval number for a western melodic interval ratio.
	 * @param numSteps  The number of steps on the scale.  See constants defined in this class.
	 * @return The typical western 12-tone diatonic scale pitch. 
	 */
	public static final double getNoteFrequencyEqualTemperedScale_Intl( int melodicIntervalNumber , int numSteps )
	{
		double freq = A4_Intl;
		final int STEP_DEL = STEPS_C - 1;
		
		int melodicIntervalNumberDelta = melodicIntervalNumber - 4;
		
		if( numSteps > STEP_DEL )
		{
			melodicIntervalNumberDelta--;
		}
		
		while( melodicIntervalNumberDelta > 0 )
		{
			freq = freq * 2;
			melodicIntervalNumberDelta--;
		}
		
		while( melodicIntervalNumberDelta < 0 )
		{
			freq = freq / 2;
			melodicIntervalNumberDelta++;
		}
		
		double delta = numSteps / 12.0;
		
		freq = freq * Math.pow( 2.0 , delta );
		
		return( freq );
	}

	
	/**
	 * Gets the 12-tone diatonic pitch for a particular key defined in class KeyAndScaleData,
	 * assuming that the key in class KeyAndScaleData has been set for a western 12-tone scale.
	 * 
	 * @param melodicIntervalNumber The melodic interval number for a western melodic interval ratio.
	 * @param numSteps The number of steps on the scale.  See constants defined in this class.
	 * @return The calculated 12-tone diatonic scale pitch.
	 */
	public static final double getNoteFrequencyEqualTemperedScale_Key( int melodicIntervalNumber , int numSteps )
	{
		double freq = KeyAndScaleData.getFreq();
		
		int stepDelta = numSteps - KeyAndScaleData.getNoteSteps();
		
		int melodicIntervalNumberDelta = melodicIntervalNumber - KeyAndScaleData.getMelodicIntervalNumber();
		
		while( stepDelta < 0 )
		{
			stepDelta += 12;
			melodicIntervalNumberDelta--;
		}
		
		while( stepDelta > 12 )
		{
			stepDelta -= 12;
			melodicIntervalNumberDelta++;
		}
		
		while( melodicIntervalNumberDelta > 0 )
		{
			freq = freq * 2;
			melodicIntervalNumberDelta--;
		}
		
		while( melodicIntervalNumberDelta < 0 )
		{
			freq = freq / 2;
			melodicIntervalNumberDelta++;
		}
		
		double delta = stepDelta / 12.0;
		
		freq = freq * Math.pow( 2.0 , delta );
		
		return( freq );
	}
	
	
	/**
	 * Temporary cache of the pitch ratios in the scale for the current key and intonation.
	 */
	private static double[] ratios = null;
	
	/**
	 * Temporary cache of the melodic interval ratio in the current key and intonation.
	 */
	private static double melodicIntervalRatio = 2.0;
	
	/**
	 * Temporary cache of the scale names in the current key and intonation.
	 */
	private static String scaleNames[] = null;
	
	
	
	/**
	 * Handles notification of an intonation and/or key change by clearing the temporary caches.
	 */
	public static void handleRatiosUpdate()
	{
		ratios = null;
		scaleNames = null;
		melodicIntervalRatio = 2.0;
	}
	
	
	/**
	 * Returns a pitch in the current (possibly non-western) key and intonation.
	 * @param melodicIntervalNumber The melodic interval number in the current key and intonation.
	 * @param numSteps The number of steps on the scale in the current key and intonation.
	 * @return The calculated pitch.
	 */
	public static double getNoteFrequencyDefaultScale_Key( int melodicIntervalNumber , int numSteps )
	{
		if( ratios == null )
		{
			final Intonation in = TestPlayer2.editPackIntonation.processInton( new HashMap() );
			ratios = in.calcIntonation();
			melodicIntervalRatio = in.getMelodicIntervalRatio();
		}
		
		return( getNoteFrequencyDefaultScale_Key( melodicIntervalNumber , numSteps , ratios ) );
	}
	
	
	/**
	 * Gets the names of the steps on the scale in the current (possibly non-western) key and intonation.
	 * @return The names of the steps on the scale.
	 */
	public static String[] getScaleNamesDefaultScale_Key( )
	{
		if( scaleNames == null )
		{
			scaleNames = TestPlayer2.editPackIntonation.getScaleNames( );
		}
		
		return( scaleNames );
	}
	
	
	/**
	 * Gets the number of the steps on the scale in the current (possibly non-western) key and intonation.
	 * @return The number of the steps on the scale.
	 */
	public static int getScaleSize()
	{
		if( ratios == null )
		{
			final Intonation in = TestPlayer2.editPackIntonation.processInton( new HashMap() );
			ratios = in.calcIntonation();
			melodicIntervalRatio = in.getMelodicIntervalRatio();
		}
		
		return( ratios.length - 1 );
	}
	
	
	/**
	 * Gets the pitch ratio for the melodic interval.  For an intonation using octaves
	 * this method always returns 2.0 as its result.  For a non-octave intonation,
	 * the value can vary.  For instance, for a Wendy Carlos beta intonation,
	 * the returned value would be a ratio ( 63.8 ) * 12 cents above unison.
	 * 
	 * Note that there is a single ratio for the entire intonation.  Using only a
	 * single ratio in an intonation is mainly a conceit to simplify the 
	 * implementation of the overall system by using logarithms.
	 * 
	 * @return The pitch ratio for the melodic interval.
	 */
	public static double getMelodicIntervalRatio()
	{
		if( ratios == null )
		{
			final Intonation in = TestPlayer2.editPackIntonation.processInton( new HashMap() );
			ratios = in.calcIntonation();
			melodicIntervalRatio = in.getMelodicIntervalRatio();
		}
		
		return( melodicIntervalRatio );
	}
	
	
	/**
	 * Returns the pitch for a note within prescribed pitch ratios for the key and intonation.
	 * @param melodicIntervalNumber The melodic interval number in the current key and intonation.
	 * @param numSteps The number of steps on the scale in the current key and intonation.
	 * @param ratios The pitch ratios in the scale.
	 * @return The frequency of the calculated pitch.
	 */
	public static double getNoteFrequencyDefaultScale_Key( int melodicIntervalNumber , int numSteps , final double[] ratios )
	{	
		double freq = KeyAndScaleData.getFreq();
		
		int stepDelta = numSteps - KeyAndScaleData.getNoteSteps();
		
		int melodicIntervalDelta = melodicIntervalNumber - KeyAndScaleData.getMelodicIntervalNumber();
		
		final double melodicIntervalRatio = getMelodicIntervalRatio();
		
		final int IL = ratios.length - 1;
		
		while( stepDelta < 0 )
		{
			stepDelta += IL;
			melodicIntervalDelta--;
		}
		
		while( stepDelta > IL )
		{
			stepDelta -= IL;
			melodicIntervalDelta++;
		}
		
		while( melodicIntervalDelta > 0 )
		{
			freq = freq * melodicIntervalRatio;
			melodicIntervalDelta--;
		}
		
		while( melodicIntervalDelta < 0 )
		{
			freq = freq / melodicIntervalRatio;
			melodicIntervalDelta++;
		}
		
		freq = freq * ratios[ stepDelta ];
		
		return( freq );
	}
	
	
	/**
	 * For a standard western diatonic 12-scale note, return the pitch of the closest note in the current (possibly non-western) key and intonation.
	 * @param noteOctave The octave number for the pitch (in the SndHnd octave numbering).
	 * @param numSteps The number of steps on the scale.  See constants defined in this class.
	 * @return The pitch of the closest note in the current (possibly non-western) key and intonation.
	 */
	public static double getCloseNoteDefaultScale_Key( final int noteOctave , final int numSteps )
	{
		final int melodicIntervalNumber = 
				(int)( ( noteOctave + numSteps / 12.0 ) * ( 2.0 / getMelodicIntervalRatio() ) );
		final int strt = Math.max( -1 , melodicIntervalNumber - 3 );
		final double freq = getNoteFrequencyEqualTemperedScale_Intl( noteOctave , numSteps );
		final double logFreq = Math.log( freq );
		int cnt;
		int step;
		double bestFreq = getNoteFrequencyDefaultScale_Key( strt , 0 );
		double logBestFreq = Math.log( bestFreq );
		final int len = ratios.length;
		for( cnt = strt ; cnt < melodicIntervalNumber + 4 ; cnt++ )
		{
			for( step = 0 ; step < len ; step++ )
			{
				double f = getNoteFrequencyDefaultScale_Key( cnt , step );
				double logF = Math.log( f );
				if( Math.abs( logF - logFreq ) < Math.abs( logBestFreq - logFreq ) )
				{
					bestFreq = f;
					logBestFreq = logF;
				}
			}
		}
		// System.out.println( Math.abs( bestFreq - freq ) );
		return( bestFreq );
	}
	
	
	
	public static final int STEPS_A = 0;
	
	public static final int STEPS_A_SHARP = 1;
	
	public static final int STEPS_B = 2;
	
	public static final int STEPS_C = 3;
	
	public static final int STEPS_C_SHARP = 4;
	
	public static final int STEPS_D = 5;
	
	public static final int STEPS_D_SHARP = 6;
	
	public static final int STEPS_E = 7;
	
	public static final int STEPS_F = 8;
	
	public static final int STEPS_F_SHARP = 9;
	
	public static final int STEPS_G = 10;
	
	public static final int STEPS_G_SHARP = 11;
	

	
	
	/**
	 * Plays a SoundFont note at a particular pitch.
	 * @param freq The pitch of the note.
	 */
	public static void playNote( double freq )
	{
		double freqDiv = freq / 261.6; // Divide frequency by middle C frequency.
		double flog2 = Math.log( freqDiv ) / Math.log( 2 );
		double fpitch = flog2 * 12.0 + 60.0;
		try
		{
			NoteChannelEmulator em = NoteChannelEmulator.allocateEmulator();
			em.play( fpitch );
			Thread.sleep( 250 );
			//noteChannel.reset();
			em.stop();
		}
		catch( Throwable ex )
		{
			ex.printStackTrace( System.out );
		}
	}
	
	
	/**
	 * Plays SoundFont notes at two pitches simultaneously.
	 * @param freq The frequency of the first pitch to play.
	 * @param freq2 The frequency of the second pitch to play.
	 */
	public static void playNotes( double freq , double freq2 )
	{
		double freqDiv = freq / 261.6; // Divide frequency by middle C frequency.
		double flog2 = Math.log( freqDiv ) / Math.log( 2 );
		double fpitch = flog2 * 12.0 + 60.0;
		
		double freqDiv2 = freq2 / 261.6; // Divide frequency by middle C frequency.
		double flog22 = Math.log( freqDiv2 ) / Math.log( 2 );
		double fpitch2 = flog22 * 12.0 + 60.0;
		try
		{
			NoteChannelEmulator em1 = NoteChannelEmulator.allocateEmulator();
			NoteChannelEmulator em2 = NoteChannelEmulator.allocateEmulator();
			em1.play( fpitch );
			em2.play( fpitch2 );
			Thread.sleep( 250 );
			//noteChannel.reset();
			em1.stop();
			em2.stop();
		}
		catch( Throwable ex )
		{
			ex.printStackTrace( System.out );
		}
	}
	

}


