




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


import cwaves.SquareWaveform;
import core.NoteDesc;
import core.SongData;
import core.WaveForm;
import cwaves.CosineWaveform;
import cwaves.SineWaveform;

/**
 * Give a "best" pitch within some octave, estimates the best octave in which to place the pitch.
 * 
 * @author tgreen
 *
 */
public class OctaveDetermination {
	
	/**
	 * Sine function used to perform Fourier transforms.
	 * Note: the wavelength of the function is unity rather than 2 * PI.
	 */
	protected static final SineWaveform SINE_WAVE = new SineWaveform();

	/**
	 * Cosine function used to perform Fourier transforms.
	 * Note: the wavelength of the function is unity rather than 2 * PI.
	 */
	protected static final CosineWaveform COSINE_WAVE = new CosineWaveform();
	
	/**
	 * The number of Fourier samples to collect.
	 */
	public static final int FOURIER_SAMPLE = 6400;

	/**
	 * Number of Fourier coefficients for harmonics.
	 */
	public static final int NUM_HARMONICS = 20;
	
	/**
	 * Number of Fourier coefficients for sub-harmonics (frequencies below the prescribed fundamental).
	 */
	public static final int NUM_SUB_HARMONICS = 4;
	
	/**
	 * Number of waves over which to sample Fourier coefficients.
	 */
	public static final int NUM_WAVES = 15;
	
	/**
	 * Time at which to start sampling the base waveform definition that is used for comparison against the input waveform.
	 */
	public static final double DELAY_TIME_SECS = 0.75;
	
	/**
	 * The beat number at which to start Fourier transforms (often zero isn't the best place to start because there may be an initial amplitude envelope before things really get going).
	 */
	private double beatNumber;
	
	/**
	 * The current frequency to test against the waveform to determine closeness of match.
	 */
	private double baseFreq;
	
	
	/**
	 * Calculates the Fourier coefficient of a harmonic.
	 * @param note The NoteDesc for which to calculate the Fourier coefficient.
	 * @param noteFreq The frequency of the harmonic.
	 * @param core The number of the core thread.
	 * @return The Fourier coefficient for the harmonic.
	 */
	private double calcFourierSuperCoeff(NoteDesc note, final double noteFreq, final int core) {
		final WaveForm w = note.getWaveform(core);
		final double strt = !(w.useAutocorrelation()) ? 0.0 : SongData.getElapsedTimeForBeatBeat(beatNumber,core) 
				- SongData.getElapsedTimeForBeatBeat(note.getActualStartBeatNumber(),core);
		int count;
		double mean = 0.0;
		final int max = FOURIER_SAMPLE * NUM_WAVES;
		for (count = 0; count < max; count++) {
			double u = ((double) count) / FOURIER_SAMPLE;
			if (!(w.useAutocorrelation())) {
				mean += w.eval(u + note.getSteadyStateWaveNum());
			} else {
				mean += w.eval(u / noteFreq + strt);
			}
		}
		mean = mean / max;
		double cosTot = 0.0;
		double sinTot = 0.0;
		for (count = 0; count < max; count++) {
			double u = ((double) count) / FOURIER_SAMPLE;
			double freqCoeff = baseFreq / noteFreq;
			double eval = 0.0;
			if (!(w.useAutocorrelation())) {
				eval = w.eval(u + note.getSteadyStateWaveNum());
			} else {
				eval = w.eval(u / noteFreq + strt);
			}
			double uf = u * freqCoeff;
			sinTot += ( eval - mean ) * SINE_WAVE.eval(uf);
			cosTot += ( eval - mean ) * COSINE_WAVE.eval(uf);
		}
		return (Math.sqrt(sinTot * sinTot + cosTot * cosTot));
	}
	
	
	/**
	 * Calculates the Fourier coefficient of a sub-harmonic.
	 * @param note The NoteDesc for which to calculate the Fourier coefficient.
	 * @param noteFreq The frequency of the sub-harmonic.
	 * @param hmc Divisor to apply the the number of waves to get the correct number for a sub-harmonic.
	 * @param core The number of the core thread.
	 * @return The Fourier coefficient for the sub-harmonic (frequency below the prescribed fundamental).
	 */
	private double calcFourierSubCoeff(NoteDesc note, final double noteFreq , final int hmc, final int core) {
		final WaveForm w = note.getWaveform(core);
		final double strt = !(w.useAutocorrelation()) ? 0.0 : SongData.getElapsedTimeForBeatBeat(beatNumber,core) 
				- SongData.getElapsedTimeForBeatBeat(note.getActualStartBeatNumber(),core);
		int count;
		double mean = 0.0;
		final int numWaves = ( NUM_WAVES / hmc ) * hmc;
		final int max = FOURIER_SAMPLE * numWaves;
		for (count = 0; count < max; count++) {
			double u = ((double) count) / FOURIER_SAMPLE;
			if (!(w.useAutocorrelation())) {
				mean += w.eval(u + note.getSteadyStateWaveNum());
			} else {
				mean += w.eval(u / noteFreq + strt);
			}
		}
		mean = mean / max;
		double cosTot = 0.0;
		double sinTot = 0.0;
		for (count = 0; count < max; count++) {
			double u = ((double) count) / FOURIER_SAMPLE;
			double freqCoeff = baseFreq / noteFreq;
			double eval = 0.0;
			if (!(w.useAutocorrelation())) {
				eval = w.eval(u + note.getSteadyStateWaveNum());
			} else {
				eval = w.eval(u / noteFreq + strt);
			}
			double uf = u * freqCoeff;
			sinTot += ( eval - mean ) * SINE_WAVE.eval(uf);
			cosTot += ( eval - mean ) * COSINE_WAVE.eval(uf);
		}
		return (Math.sqrt(sinTot * sinTot + cosTot * cosTot));
	}
	
	/**
	 * Calculates Fourier coefficients samples from a NoteDes.
	 * @param note The input NoteDesc.
	 * @param ampl Output Fourier coefficients for the harmonics.
	 * @param ampl_sub Output Fourier coefficients for the sub-harmonics (frequencies below the prescribed fundamental).
	 * @param core The number of the core thread.
	 */
	private void calcFourierCoeff(NoteDesc note, double[] ampl, double[] ampl_sub, final int core) {
		int sz = ampl.length;
		int sz_sub = ampl_sub.length;
		int count;
		for (count = 0; count < sz; count++) {
			double freq = count + 1;
			ampl[count] = calcFourierSuperCoeff(note, freq,core);
		}
		for (count = 0; count < sz_sub; count++) {
			double freq = 1.0 / ( count + 2 );
			ampl_sub[count] = calcFourierSubCoeff(note, freq, count + 2,core);
		}
	}
	
	
	/**
	 * Constructs an OctaveDetermination instance.
	 */
	public OctaveDetermination()
	{
	}
	
	
	/**
	 * Determines the best octave for a particular note, and returns the matching frequency.
	 * @param note The input NoteDesc from which to sample for frequency.
	 * @param strtBeatNumber 
	 * @param inputBestFreq The best match for the frequency in some octave.
	 * @param core The number of the core thread.
	 * @return The best frequency match for the note.
	 */
	public double calcBestOctave( NoteDesc note, double strtBeatNumber , double inputBestFreq , final int core )
	{
		beatNumber = strtBeatNumber;
		double bestFreq = inputBestFreq;
		double freq = inputBestFreq;
		double bestVal = testFreq( note , strtBeatNumber , freq , core );
		while( freq > 5.0 )
		{
			freq = freq / 2.0;
			double val = testFreq( note , strtBeatNumber , freq , core );
			if( val > bestVal )
			{
				bestVal = val;
				bestFreq = freq;
			}
		}
		freq = inputBestFreq;
		while( freq < 50000.0 )
		{
			freq = freq * 2.0;
			double val = testFreq( note , strtBeatNumber , freq , core );
			if( val > bestVal )
			{
				bestVal = val;
				bestFreq = freq;
			}
		}
		System.out.println("Best Freq With Octave " + bestFreq);
		return( bestFreq );
	}
	
	
	/**
	 * Tests how close an input frequency is to the actual frequency of the sampled harmonics.
	 * @param note Input NoteDesc to test.
	 * @param strtBeatNumber The beat number at which to start Fourier transforms (often zero isn't the best place to start because there may be an initial amplitude envelope before things really get going).
	 * @param inFreq The frequency against which to test.
	 * @param core The number of the core thread.
	 * @return A number that is intended to increase as inFreq gets closer to the actual frequency in the waveform generated by the NoteDesc.
	 */
	private double testFreq( NoteDesc note , final double strtBeatNumber , double inFreq , final int core )
	{
		baseFreq = inFreq;
		PrintStream ps = System.out;
		
		final double WF_SAMP = Math.max( 10.0 , (int)( DELAY_TIME_SECS * inFreq ) );
		
		final GuitarHarmonics hh = new GuitarHarmonics( new SquareWaveform( 0.25 ) , WF_SAMP , FOURIER_SAMPLE , NUM_WAVES , NUM_HARMONICS , NUM_SUB_HARMONICS );
		
		final double[] smplCoeffs = new double[NUM_HARMONICS];
		final double[] smplCoeffs_sub = new double[ NUM_SUB_HARMONICS ];
		calcFourierCoeff(note, smplCoeffs, smplCoeffs_sub,core);
		
		final GuitarAnalyzerIntegrator integ = new GuitarAnalyzerIntegrator( smplCoeffs , smplCoeffs_sub , new GuitarHarmonics( NUM_HARMONICS , NUM_SUB_HARMONICS ) );
		
		integ.calculateMin(hh, NUM_HARMONICS , NUM_SUB_HARMONICS , 0.5 , -20 , ps );
		
		double ret = Math.abs( integ.getCurrentVal() );
		
		System.out.println( ">>> " + inFreq + " " + ret );
		
		return( ret );
	}

}

