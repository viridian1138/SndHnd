




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


import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import kwaves.OctaveDetermination;

import bezier.PiecewiseCubicMonotoneBezierFlatMultiCore;

import core.IntelligentAgent;



/**
 * Estimates pitches from sampled audio.
 * 
 * @author tgreen
 *
 */
public class NotePitchDigitizer {
	
	/**
	 * Mode to consider autocorrelation possibilities over a wide pitch range.
	 */
	public static final int AUTO_CORR_WIDEBAND = 0;
	
	/**
	 * Mode to consider autocorrelation possibilities over a tenor range.
	 */
	public static final int AUTO_CORR_TENOR = 1;
	
	/**
	 * The preferred autocorrelation mode for guitar sampling.
	 */
	protected static int AUTO_CORR_GUITAR_MODE = AUTO_CORR_WIDEBAND;
	
	/**
	 * The preferred autocorrelation mode for voice sampling.
	 */
	protected static int AUTO_CORR_VOICE_MARK_MODE = AUTO_CORR_TENOR;
	
	/**
	 * The current autocorrelation mode.
	 */
	protected static int AUTO_CORR_MODE = AUTO_CORR_WIDEBAND;
	
	/**
	 * High-end pitches (in hertz) for the various autocorrelation modes.
	 */
	protected static final double[] modeHi = { 2000.0 , NoteTable.getNoteFrequencyEqualTemperedScale_Intl(4,
			NoteTable.STEPS_G) + 2.0 };
	
	/**
	 * Low-end pitches (in hertz) for the various autocorrelation modes.
	 */
	protected static final double[] modeLo = { 45.0 , NoteTable.getNoteFrequencyEqualTemperedScale_Intl(2,
			NoteTable.STEPS_B) - 2.0 };
	
	
	/**
	 * Prints the high-end and low-end pitches (in hertz) for the various autocorrelation modes.
	 */
	public static void printAutocorFrequencies()
	{
		System.out.println( "Autocor Frequencies Tenor : " );
		System.out.println( modeLo[ AUTO_CORR_TENOR ] );
		System.out.println( modeHi[ AUTO_CORR_TENOR ] );
		System.out.println( "Autocor Frequencies Wideband : " );
		System.out.println( modeLo[ AUTO_CORR_WIDEBAND ] );
		System.out.println( modeHi[ AUTO_CORR_WIDEBAND ] );
	}
	
	
	/**
	 * Sets the high-end of the tenor autocorrelation range from the paste buffer.
	 */
	public static void setTenorHiPaste()
	{
		modeHi[ AUTO_CORR_TENOR ] = SongData.PASTE_BUFFER_FREQ;
		System.out.println( "Set To : " + SongData.PASTE_BUFFER_FREQ );
	}
	
	/**
	 * Sets the low-end of the tenor autocorrelation range from the paste buffer.
	 */
	public static void setTenorLoPaste()
	{
		modeLo[ AUTO_CORR_TENOR ] = SongData.PASTE_BUFFER_FREQ;
		System.out.println( "Set To : " + SongData.PASTE_BUFFER_FREQ );
	}
	
	/**
	 * Sets the high-end of the wideband autocorrelation range from the paste buffer.
	 */
	public static void setWidebandHiPaste()
	{
		modeHi[ AUTO_CORR_WIDEBAND ] = SongData.PASTE_BUFFER_FREQ;
		System.out.println( "Set To : " + SongData.PASTE_BUFFER_FREQ );
	}
	
	/**
	 * Sets the low-end of the wideband autocorrelation range from the paste buffer.
	 */
	public static void setWidebandLoPaste()
	{
		modeLo[ AUTO_CORR_WIDEBAND ] = SongData.PASTE_BUFFER_FREQ;
		System.out.println( "Set To : " + SongData.PASTE_BUFFER_FREQ );
	}
	
	
	/**
	 * Sets the preferred autocorrelation mode for guitar sampling.
	 * @param in The preferred autocorrelation mode for guitar sampling.
	 */
	public static void setGuitarAutocorrMode( int in )
	{
		AUTO_CORR_GUITAR_MODE = in;
	}
	
	
	/**
	 * Sets the preferred autocorrelation mode for voice sampling.
	 * @param in The preferred autocorrelation mode for voice sampling.
	 */
	public static void setVoiceAutocorrMode( int in )
	{
		AUTO_CORR_VOICE_MARK_MODE = in;
	}
	
	
	/**
	 * Sets the current autocorrelation mode to the preferred autocorrelation mode for voice sampling.
	 */
	public static void setVoiceMarkMode()
	{
		AUTO_CORR_MODE = AUTO_CORR_VOICE_MARK_MODE;
	}
	
	
	/**
	 * Sets the current autocorrelation mode to the preferred autocorrelation mode for guitar sampling.
	 */
	public static void setGuitarMode()
	{
		AUTO_CORR_MODE = AUTO_CORR_GUITAR_MODE;
	}

	
	/**
	 * Calculates the closeness of match between the wave period and the sampled audio.
	 * @param wave The WaveForm containing the sampled audio.
	 * @param strt The point at which to start sampling in elapsed seconds from the start of the sampled audio.
	 * @param max The number of samples to be used in determining the closeness.
	 * @param waveDelay The period (reciprocal in seconds) of the wave frequency.
	 * @return The closeness of match between the wave period and the sampled audio.
	 */
	protected static double calcValue(WaveForm wave, double strt, int max,
			double waveDelay) {
		int count;
		double sum = 0.0;
		double meanStrt = 0.0;
		double meanEnd = 0.0;
		final double end = strt + 5.0 * waveDelay;
		for (count = 0; count < max; count++) {
			double u = ((double) (count)) / (max - 1);
			double uv = (1 - u) * strt + u * end;
			meanStrt += wave.eval(uv);
			meanEnd += wave.eval(uv + waveDelay);
		}
		meanStrt = meanStrt / max;
		meanEnd = meanEnd / max;
		for (count = 0; count < max; count++) {
			double u = ((double) (count)) / (max - 1);
			double uv = (1 - u) * strt + u * end;
			// double delta = wave.eval(uv) - wave.eval(uv + waveDelay);
			// sum += delta * delta;
			double delta = ( wave.eval(uv) - meanStrt ) * ( wave.eval(uv + waveDelay) - meanEnd );
			sum += delta;
		}
		// return( Math.sqrt( sum / max ) );
		return( sum / max );
	}

	
	/**
	 * Calculates the best autocorrelation frequency (pitch) of the sampled audio.
	 * @param note The note containing the sampled audio.
	 * @param beatNumber The beat number at which to estimate the pitch.
	 * @param core The number of the core thread.
	 * @return The estimated pitch in hertz.
	 */
	public static double calcAutoCorrelation(NoteDesc note, double beatNumber, final int core) {
		final WaveForm wave = note.getWaveform( core );
		double bestFreq = 0.0;
		// double bestVal = 1E+16;
		double bestVal = -1E+16;
		double freq;
		double strt = SongData.getElapsedTimeForBeatBeat( beatNumber , core ) - 
			SongData.getElapsedTimeForBeatBeat(note.getActualStartBeatNumber() , core);
		int octave;
		int scaleNumber;
		for (octave = 0; octave < 10; octave++) {
			for (scaleNumber = 0; scaleNumber < 12; scaleNumber++) {
				freq = NoteTable.getNoteFrequencyDefaultScale_Key(octave,
						scaleNumber);
				if ( ( freq > modeLo[ AUTO_CORR_MODE ] ) && ( freq < modeHi[ AUTO_CORR_MODE ] ) ) {
					double waveDelay = 1.0 / freq;
					double val = calcValue(wave, strt, 53, waveDelay);
					// if (val < bestVal) {
					if( val > bestVal ) {
						System.out.println(">>> " + freq);
						bestFreq = freq;
						bestVal = val;
					}
				}
			}
		}
		int adjust;
		double bf;
		boolean tryBest = true;
		while( tryBest )
		{
			bf = bestFreq;
			tryBest = false;
			for (adjust = -9; adjust < 10; adjust++) {
				double steps = (adjust / 10.0) / 12.0;
				freq = bf * Math.pow(2.0, steps);
				double waveDelay = 1.0 / freq;
				double val = calcValue(wave, strt, 53, waveDelay);
				// if (val < bestVal) {
				if( val > bestVal ) {
					bestFreq = freq;
					bestVal = val;
				}
			}
			bf = bestFreq;
			double waveDelay = 1.0 / ( 0.5 * bf );
			double val = calcValue(wave, strt, 53, waveDelay);
			// if (val < bestVal) {
			if( val > bestVal ) {
				bestFreq = 0.5 * bf;
				bestVal = val;
				tryBest = true;
			}
			waveDelay = 1.0 / ( 2.0 * bf );
			val = calcValue(wave, strt, 53, waveDelay);
			// if (val < bestVal) {
			if( val > bestVal ) {
				bestFreq = 2.0 * bf;
				bestVal = val;
				tryBest = true;
			}
		}
		
		tryBest = true;
		while( tryBest )
		{
			bf = bestFreq;
			tryBest = false;
			for (adjust = -9; adjust < 10; adjust++) {
				double steps = (adjust / 100.0) / 12.0;
				freq = bf * Math.pow(2.0, steps);
				double waveDelay = 1.0 / freq;
				double val = calcValue(wave, strt, 53, waveDelay);
				// if (val < bestVal) {
				if( val > bestVal ) {
					bestFreq = freq;
					bestVal = val;
				}
			}
			bf = bestFreq;
			double waveDelay = 1.0 / ( 0.5 * bf );
			double val = calcValue(wave, strt, 53, waveDelay);
			// if (val < bestVal) {
			if( val > bestVal ) {
				bestFreq = 0.5 * bf;
				bestVal = val;
				tryBest = true;
			}
			waveDelay = 1.0 / ( 2.0 * bf );
			val = calcValue(wave, strt, 53, waveDelay);
			// if (val < bestVal) {
			if( val > bestVal ) {
				bestFreq = 2.0 * bf;
				bestVal = val;
				tryBest = true;
			}
		}
		
		tryBest = true;
		while( tryBest )
		{
			bf = bestFreq;
			tryBest = false;
			for (adjust = -9; adjust < 10; adjust++) {
				double steps = (adjust / 1000.0) / 12.0;
				freq = bf * Math.pow(2.0, steps);
				double waveDelay = 1.0 / freq;
				double val = calcValue(wave, strt, 53, waveDelay);
				// if (val < bestVal) {
				if( val > bestVal ) {
					bestFreq = freq;
					bestVal = val;
				}
			}
			bf = bestFreq;
			double waveDelay = 1.0 / ( 0.5 * bf );
			double val = calcValue(wave, strt, 53, waveDelay);
			// if (val < bestVal) {
			if( val > bestVal ) {
				bestFreq = 0.5 * bf;
				bestVal = val;
				tryBest = true;
			}
			waveDelay = 1.0 / ( 2.0 * bf );
			val = calcValue(wave, strt, 53, waveDelay);
			// if (val < bestVal) {
			if( val > bestVal ) {
				bestFreq = 2.0 * bf;
				bestVal = val;
				tryBest = true;
			}
		}
		
		System.out.println("Best Freq " + bestFreq);
		return (bestFreq);
	}

	
	/**
	 * Estimates the frequency (pitch) of a note using sampled audio.
	 * @param sampledNote The note containing the input sampled audio.
	 * @param note The note to receive the estimated pitch.
	 * @param agent The agent of the note to receive the estimated pitch.
	 */
	public static void determineNotePitch(NoteDesc sampledNote, NoteDesc note,
			IntelligentAgent agent ) {
		final int core = 0;
		if (sampledNote == null) {
			throw (new RuntimeException("No Sampled Note."));
		}

		if (!(sampledNote.getWaveform( core ).useAutocorrelation())) {
			throw (new RuntimeException("Not using autocorrelation!"));
		}

		double startBeatNumber = note.getStartBeatNumber();
		double endBeatNumber = note.getEndBeatNumber();

		final double secsDelta = 0.1;

		double startBeatDigitize = SongData.getBeatNumber( SongData.getElapsedTimeForBeatBeat( startBeatNumber , core ) + secsDelta , core );
		double endBeatDigitize = SongData.getBeatNumber( SongData.getElapsedTimeForBeatBeat( endBeatNumber , core ) - secsDelta , core );

		if (endBeatDigitize < startBeatDigitize) {
			startBeatDigitize = startBeatNumber;
			endBeatDigitize = endBeatNumber;
		}

		final int sample_size = 10;
		final double dblSamp = sample_size - 1;

		int count;

		TreeSet<Double> ts = new TreeSet<Double>();

		for (count = 0; count < sample_size; count++) {
			double u = count / dblSamp;
			double beatNumber = (1 - u) * startBeatDigitize + u
					* endBeatDigitize;
			double pitch = calcAutoCorrelation(sampledNote, beatNumber, core );
			pitch = ( new OctaveDetermination() ).calcBestOctave(sampledNote, beatNumber, pitch, core);
			if (( pitch > modeLo[ AUTO_CORR_MODE ] ) && ( pitch < modeHi[ AUTO_CORR_MODE ] ) ) {
				ts.add(new Double(pitch));
			}
		}

		int sz = ts.size() / 2;

		Iterator<Double> it = ts.iterator();
		Double dbl = null;

		switch (ts.size()) {

		case 0: {
			if (agent != null) {
				dbl = new Double(agent.getFrequencyA());
			} else {
				dbl = new Double(440.0);
			}
		}
			break;

		case 1: {
			dbl = it.next();
		}
			break;

		default: {
			for (count = 0; count < sz; count++) {
				dbl = it.next();
			}
		}

		}

		double finalFreq = dbl.doubleValue();

		FreqAndBend fab = note.getFreqAndBend();

		fab.setBaseFreq(finalFreq);
		fab.setWaveInfoDirty(true);
	}

	
	/**
	 * Estimates a pitch-bend interpolation point of a note using sampled audio.
	 * @param sampledNote The note containing the input sampled audio.
	 * @param note The note to receive the estimated pitch-bend.
	 * @param pt The interpolation point to receive the estimated pitch-bend.
	 * @param core The number of the core thread.
	 */
	public static void determineInterpPitch(NoteDesc sampledNote,
			NoteDesc note, InterpolationPoint pt, final int core) {
		if (sampledNote == null) {
			throw (new RuntimeException("No Sampled Note."));
		}

		if (!(sampledNote.getWaveform( core ).useAutocorrelation())) {
			throw (new RuntimeException("Not using autocorrelation!"));
		}

		double startBeatNumber = note.getActualStartBeatNumber();
		double endBeatNumber = note.getActualEndBeatNumber();

		double u = pt.getParam();
		double beatNumber = (1 - u) * startBeatNumber + u * endBeatNumber;
		double freq = calcAutoCorrelation(sampledNote, beatNumber, core);
		freq = ( new OctaveDetermination() ).calcBestOctave(sampledNote, beatNumber, freq , core );

		FreqAndBend fab = note.getFreqAndBend();

		pt.setValue(freq / (fab.getBaseFreq()));
		fab.setWaveInfoDirty(true);
		fab.setUserDefinedBend(true);
	}
	
	
	/**
	 * Estimates the distance of a pitch-bend from the base frequency.
	 * @param fa The pitch-bend value.
	 * @return The distance of a pitch-bend from the base frequency.
	 */
	protected static double fdist( double fa )
	{
		return( Math.abs( 1.0 - fa ) );
	}

	
	/**
	 * Estimates the frequency (pitch) of a note and the pitch-bend of a note using sampled audio.
	 * @param sampledNote The note containing the input sampled audio.
	 * @param note The note to receive the estimated pitch and pitch-bend.
	 */
	public static void determineFullPitch(NoteDesc sampledNote, NoteDesc note) {
		final int core = 0;
		
		if (sampledNote == null) {
			throw (new RuntimeException("No Sampled Note."));
		}

		if (!(sampledNote.getWaveform( core ).useAutocorrelation())) {
			throw (new RuntimeException("Not using autocorrelation!"));
		}

		double startBeatNumber = note.getStartBeatNumber();
		double endBeatNumber = note.getEndBeatNumber();

		final double secsDelta = 0.1;

		double startBeatDigitize = SongData.getBeatNumber( SongData.getElapsedTimeForBeatBeat( startBeatNumber , core ) + secsDelta , core );
		double endBeatDigitize = SongData.getBeatNumber( SongData.getElapsedTimeForBeatBeat( endBeatNumber , core ) - secsDelta , core );

		if (endBeatDigitize < startBeatDigitize) {
			startBeatDigitize = startBeatNumber;
			endBeatDigitize = endBeatNumber;
		}

		final int sample_size = 10;
		final double dblSamp = sample_size - 1;

		int count;

		TreeSet<Double> ts = new TreeSet<Double>();

		InterpolationPoint[] interps = new InterpolationPoint[10];

		for (count = 0; count < sample_size; count++) {
			double u = count / dblSamp;
			double beatNumber = (1 - u) * startBeatDigitize + u
					* endBeatDigitize;
			double pitch = calcAutoCorrelation(sampledNote, beatNumber, core);
			pitch = ( new OctaveDetermination() ).calcBestOctave(sampledNote, beatNumber, pitch, core);
			if ((pitch > modeLo[ AUTO_CORR_MODE ] ) && ( pitch < modeHi[ AUTO_CORR_MODE ] )) {
				ts.add(new Double(pitch));
				interps[count] = new InterpolationPoint(beatNumber, pitch);
			}
		}

		int sz = ts.size() / 2;

		Iterator<Double> it = ts.iterator();
		Double dbl = null;
		for (count = 0; count < sz; count++) {
			dbl = it.next();
		}

		if (dbl == null) {
			dbl = it.next();
		}

		double finalFreq = dbl.doubleValue();

		FreqAndBend fab = note.getFreqAndBend();

		fab.setBaseFreq(finalFreq);

		PiecewiseCubicMonotoneBezierFlatMultiCore bez = fab.getBendPerNoteU();
		ArrayList<InterpolationPoint> interpolationPts = bez.getInterpolationPoints();
		interpolationPts.clear();

		boolean firstUp = false;
		InterpolationPoint ppt = null;

		for (count = 0; count < sample_size; count++) {
			InterpolationPoint apt = interps[count];
			if (apt != null) {
				double u = (apt.getParam() - note.getActualStartBeatNumber())
						/ (note.getActualEndBeatNumber() - note
								.getActualStartBeatNumber());
				double bend = apt.getValue() / finalFreq;
				if ((u >= 0.0) && (u <= 1.0) && (bend > 0.5) && (bend < 2.0)) {
					ppt = new InterpolationPoint(u, bend);

					if (!firstUp && (ppt.getParam() > 0.0001)) {
						interpolationPts.add(new InterpolationPoint(0.0, ppt
								.getValue()));
					}
					interpolationPts.add(ppt);
					firstUp = true;
				}
				else if( (u >= 0.0) && (u <= 1.0) )
				{
					while( fdist( bend / 2.0 ) < fdist( bend ) )
					{
						bend = bend / 2.0;
					}
					while( fdist( bend * 2.0 ) < fdist( bend ) )
					{
						bend = bend * 2.0;
					}
					if( ( bend > 0.8 ) && ( bend < 1.25 ) )
					{
						ppt = new InterpolationPoint(u, bend);

						if (!firstUp && (ppt.getParam() > 0.0001)) {
							interpolationPts.add(new InterpolationPoint(0.0, ppt
									.getValue()));
						}
						interpolationPts.add(ppt);
						firstUp = true;
					}
				}
			}
		}

		if ((note.getActualEndBeatNumber() - endBeatDigitize) > 0.0001) {
			for (count = 1; count <= sample_size; count++) {
				double uv = ((double) count) / sample_size;
				double beatNumber = (1 - uv) * (endBeatDigitize) + uv
						* (note.getActualEndBeatNumber());
				double pitch = calcAutoCorrelation(sampledNote, beatNumber, core);
				pitch = ( new OctaveDetermination() ).calcBestOctave(sampledNote, beatNumber, pitch, core);
				double u = (beatNumber - note.getActualStartBeatNumber())
						/ (note.getActualEndBeatNumber() - note
								.getActualStartBeatNumber());
				double bend = pitch / finalFreq;
				if ((u >= 0.0) && (u <= 1.0) && (bend > 0.5) && (bend < 2.0)) {
					ppt = new InterpolationPoint(u, bend);
					interpolationPts.add(ppt);
				}
				else if( (u >= 0.0) && (u <= 1.0) )
				{
					while( fdist( bend / 2.0 ) < fdist( bend ) )
					{
						bend = bend / 2.0;
					}
					while( fdist( bend * 2.0 ) < fdist( bend ) )
					{
						bend = bend * 2.0;
					}
					if( ( bend > 0.8 ) && ( bend < 1.25 ) )
					{
						ppt = new InterpolationPoint(u, bend);
						interpolationPts.add(ppt);
					}
				}
			}
		}

		if ((ppt != null) && (ppt.getParam() < 0.9999)) {
			interpolationPts.add(new InterpolationPoint(1.0, ppt.getValue()));
		}

		fab.setWaveInfoDirty(true);
		fab.setUserDefinedBend(true);
	}

}

