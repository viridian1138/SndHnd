




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


import java.util.*;
import java.util.Map.Entry;

import cwaves.CosineWaveform;
import cwaves.SineWaveform;


/**
 * Calculates the dissonance between potential parts of a musical composition.
 * 
 * Some routines adapted from MATLAB code posted to https://sethares.engr.wisc.edu/comprog.html
 * 
 * @author tgreen
 *
 */
public class DissonanceCalculator {
	
	/**
	 * The note against which to perform the dissonance calculation.
	 */
	protected NoteDesc w2;
	
	/**
	 * Offset wave number at which to calculate Fourier coefficients.  Not used.
	 */
	protected int offsetWaveNum;
	
	/**
	 * The beat number at which to perform the dissonance calculation.
	 */
	protected double beatNumber;
	
	/**
	 * Constant from MATABL code at https://sethares.engr.wisc.edu/comprog.html
	 */
	public static final double DSTAR = 0.24;
	
	/**
	 * Constant from MATABL code at https://sethares.engr.wisc.edu/comprog.html
	 */
	public static final double S1 = 0.0207;
	
	/**
	 * Constant from MATABL code at https://sethares.engr.wisc.edu/comprog.html
	 */
	public static final double S2 = 18.96;
	
	/**
	 * Constant from MATABL code at https://sethares.engr.wisc.edu/comprog.html
	 */
	public static final double A1 = -3.51;
	
	/**
	 * Constant from MATABL code at https://sethares.engr.wisc.edu/comprog.html
	 */
	public static final double A2 = -5.75;
	
	/**
	 * Constant from MATABL code at https://sethares.engr.wisc.edu/comprog.html
	 */
	public static final double C1 = 5.0;
	
	/**
	 * Constant from MATABL code at https://sethares.engr.wisc.edu/comprog.html
	 */
	public static final double C2 = -5.0;
	
	/**
	 * Sine function used to perform Fourier transforms.
	 * Note: the wavelength of the function is unity rather than 2 * PI.
	 */
	public static final SineWaveform SINE_WAVE = new SineWaveform();
	
	/**
	 * Cosine function used to perform Fourier transforms.
	 * Note: the wavelength of the function is unity rather than 2 * PI.
	 */
	public static final CosineWaveform COSINE_WAVE = new CosineWaveform();
	
	/**
	 * The number of samples to use when calculating FFourier coefficients.
	 */
	public static final int FOURIER_SAMPLE = 200;
	
	/**
	 * The number of harmonics over which to calculate Fourier coefficients..
	 */
	public static final int NUM_HARMONICS = 10;
	
	/**
	 * Map of frequencies for each note.
	 */
	protected final HashMap<NoteDesc,Double> noteFreqMap = new HashMap<NoteDesc,Double>();
	
	/**
	 * The frequencies for the rest of the surrounding composition.
	 */
	protected double[] bfi;
	
	/**
	 * The amplitudes of the Fourier coefficients for the rest of the surrounding composition.
	 */
	protected double[] ampli;
	
	/**
	 * The relative frequencies of the note for which to calculate dissonances for multiple potential pitches.
	 */
	protected double[] bfj;
	
	/**
	 * The Fourier coefficient amplitudes at the relative frequencies for the note for which to calculate dissonances for multiple potential pitches.
	 */
	protected double[] amplj;

	/**
	 * Constructor.
	 * @param notes The notes for the rest of the surrounding composition.
	 * @param _w2 The note for which to calculate dissonances for multiple potential pitches.
	 * @param _offsetWaveNum Offset wave number at which to calculate Fourier coefficients.  Not used.
	 * @param _beatNumber The beat number at which to perform the dissonance calculation.
	 */
	public DissonanceCalculator( ArrayList<NoteDesc> notes , NoteDesc _w2 , int _offsetWaveNum , double _beatNumber ) {
		w2 = _w2;
		beatNumber = _beatNumber;
		
		bfj = new double[ NUM_HARMONICS ];
		amplj = new double[ NUM_HARMONICS ];
		
		final int core = 0; // Change for multi-core subclass !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		initFourierCoeff( notes , core );
		calcFourierCoeff( w2 , bfj , amplj , core );
	}
	
	
	/**
	 * Gets the frequency for a note.  Note: this may need some fixes in relation to portamento transitions.
	 * @param note The input note.
	 * @param core The number of the core thread.
	 * @return The frequency.
	 */
	protected double getNoteFrequency( NoteDesc note , final int core )
	{
		if( !( note.getWaveform( core ).useAutocorrelation() ) )
		{
			// Note: base frequency may not be the best to use if there is an ongoing portamento.
			return( note.getFreqAndBend().getBaseFreq() );
		}
		else
		{
			Double dbl = (Double)( noteFreqMap.get( note ) );
			if( dbl != null )
			{
				return( dbl.doubleValue() );
			}
			else
			{
				NotePitchDigitizer.setGuitarMode();
				double val = NotePitchDigitizer.calcAutoCorrelation( note , beatNumber , core);
				dbl = new Double( val );
				noteFreqMap.put( note , dbl );
				return( val );
			}
		}
	}
	
	
	/**
	 * Initializes the set of Fourier coefficients for the surrounding composition.
	 * @param notes The notes in the surrounding composition.
	 * @param core The number of the core thread.
	 */
	protected void initFourierCoeff( ArrayList<NoteDesc> notes , final int core )
	{
		TreeMap<Double,ArrayList<NoteDesc>> freqMap = new TreeMap<Double,ArrayList<NoteDesc>>();
		
		int count;
		for( count = 0 ; count < NUM_HARMONICS ; count++ )
		{
			for( final NoteDesc note : notes )
			{
				double freq = ( count + 1 ) * ( getNoteFrequency( note , core ) );
				Double freqDbl = new Double( freq );
				ArrayList<NoteDesc> vect = freqMap.get( freqDbl );
				if( vect == null )
				{
					vect = new ArrayList<NoteDesc>();
					freqMap.put( freqDbl , vect );
				}
				vect.add( note );
			}
		}
		
		count = 0;
		int sz = freqMap.keySet().size();
		bfi = new double[ sz ];
		ampli = new double[ sz ];
		for( Entry<Double,ArrayList<NoteDesc>> e : freqMap.entrySet()  )
		{
			Double freq = e.getKey();
			double fr = freq.doubleValue();
			bfi[ count ] = fr;
			ArrayList<NoteDesc> vect = e.getValue();
			ampli[ count ] = calcFourierCoeff( vect , fr , core );
			count++;
		}
		
	}
	
	/**
	 * Finds the relative minima of the dissonance curve.
	 * @param fj1 The starting frequency of the curve.
	 * @param fj2 The ending frequency of the curve.
	 * @param max The number of points at which to calculate the curve.
	 * @param out The output list of frequencies for relative minima.
	 * @throws Throwable
	 */
	public void findMinimums( double fj1 , double fj2 , int max , ArrayList<Double> out ) throws Throwable
	{
		int count;
		for( count = 0 ; count < max ; count++ )
		{
			double u0 = ( (double) ( count - 1 ) ) / max;
			double u1 = ( (double) count ) / max;
			double u2 = ( (double) ( count + 1 ) ) / max;
			double freqj0 = (1-u0) * fj1 + u0 * fj2;
			double freqj1 = (1-u1) * fj1 + u1 * fj2;
			double freqj2 = (1-u2) * fj1 + u2 * fj2;
			double d0 = calcDissonance( freqj0 );
			double d1 = calcDissonance( freqj1 );
			double d2 = calcDissonance( freqj2 );
			if( ( d1 <= d0 ) && ( d1 <= d2 ) )
			{
				out.add( new Double( freqj1 ) );
			}
		}
	}
	
	/**
	 * Finds the relative minima and relative maxima of the dissonance curve.
	 * @param fj1 The starting frequency of the curve.
	 * @param fj2 The ending frequency of the curve.
	 * @param max The number of points at which to calculate the curve.
	 * @param outMin The output list of frequencies for relative minima.
	 * @param outMax The output list of frequencies for relative maxima.
	 * @throws Throwable
	 */
	public void findMinimaAndMaxima( double fj1 , double fj2 , int max , ArrayList<Double> outMin , ArrayList<Double> outMax ) throws Throwable
	{
		int count;
		for( count = 0 ; count < max ; count++ )
		{
			double u0 = ( (double) ( count - 1 ) ) / max;
			double u1 = ( (double) count ) / max;
			double u2 = ( (double) ( count + 1 ) ) / max;
			double freqj0 = (1-u0) * fj1 + u0 * fj2;
			double freqj1 = (1-u1) * fj1 + u1 * fj2;
			double freqj2 = (1-u2) * fj1 + u2 * fj2;
			double d0 = calcDissonance( freqj0 );
			double d1 = calcDissonance( freqj1 );
			double d2 = calcDissonance( freqj2 );
			if( ( d1 <= d0 ) && ( d1 <= d2 ) )
			{
				outMin.add( new Double( freqj1 ) );
			}
			if( ( d1 >= d0 ) && ( d1 >= d2 ) )
			{
				outMax.add( new Double( freqj1 ) );
			}
		}
	}
	
	/**
	 * Calculates the dissonance curve between the note and the rest of the surrounding composition.
	 * @param fj1 The starting frequency of the curve.
	 * @param fj2 The ending frequency of the curve.
	 * @param max The number of points at which to calculate the curve.
	 * @param out The output calculated dissonance curve.
	 * @throws Throwable
	 */
	public void calcCurve( double fj1 , double fj2 , int max , ArrayList<InterpolationPoint> out ) throws Throwable
	{
		int count;
		for( count = 0 ; count < max ; count++ )
		{
			double u1 = ( (double) count ) / max;
			double freqj1 = (1-u1) * fj1 + u1 * fj2;
			double d1 = calcDissonance( freqj1 );
			InterpolationPoint pt = new InterpolationPoint( freqj1 , d1 );
			out.add( pt );
		}
	}
	
	/**
	 * Test driver.  No longer used.
	 * @param in  Input parameters.  Not used.
	 */
	public static void main( String[] in )
	{
		/* SineWaveform s1 = new SineWaveform();
		ArrayList v1 = new ArrayList();
		NoteDesc desc = new NoteDesc();
		v1.add( desc );
		desc.getFreqAndBend().setBaseFreq( 440 );
		desc.setWaveform( new SineWaveform() );
		DissonanceCalculator calc = new DissonanceCalculator( v1 , s1 , 100 , 0.5 );
		ArrayList out = new ArrayList();
		calc.findMinimums( 440 , 1600 , 3000 , out ); */
	}
	
	/**
	 * Calculates the dissonance between the Fourier coefficients [bfi, ampli], and the Fourier coefficients [bfj, amplj] bumped up to base frequency freqj.
	 * @param freqj The desired base frequency for which to calculate the dissonance.
	 * @return The calculated dissonance.
	 */
	public double calcDissonance( double freqj )
	{
		int szj = bfj.length;
		double[] fj = new double[ szj ];
		
		int count;
		
		for( count = 0 ; count < szj ; count++ )
		{
			fj[ count ] = freqj * bfj[ count ];
		}
		
		return( calcDissonance( bfi , ampli , fj , amplj ) );
	}
	
	
	/**
	 * Calculates the dissonance.
	 * @param fi The "I" frequencies.
	 * @param ampli The "I" Fourier coefficient amplitudes.
	 * @param fj The "J" frequencies.
	 * @param amplj The "J" Fourier coefficient amplitudes.
	 * @return The calculated dissonance.
	 */
	public static double calcDissonance( double[] fi , double[] ampli ,
			double[] fj , double[] amplj )
	{
		int szi = fi.length;
		int szj = fj.length;
		int i;
		int j;
		double d = 0.0;
		for( i = 0 ; i < szi ; i++ )
		{
			for( j = 0 ; j < szj ; j++ )
			{
				double fmin = Math.min( fi[ i ] , fj[ j ] );
				double s = DSTAR / ( S1 * fmin + S2 );
				double fdif = Math.abs( fj[ j ] - fi[ i ] );
				double arg1 = A1 * s * fdif;
				double arg2 = A2 * s * fdif;
				double exp1 = Math.exp( arg1 );
				double exp2 = Math.exp( arg2 );
				double dnew = Math.min( ampli[ i ] , amplj[ j ] ) *
					( C1 * exp1 + C2 * exp2 );
				d += dnew;
			}
		}
		
		return( d );
	}
	
	/**
	 * Calculates the dissonance, throwing away any set of harmonics separated by more than a 3/2 ratio.
	 * @param fi The "I" frequencies.
	 * @param ampli The "I" Fourier coefficient amplitudes.
	 * @param fj The "J" frequencies.
	 * @param amplj The "J" Fourier coefficient amplitudes.
	 * @return The calculated dissonance.
	 */
	public static double calcDissonanceClamped( double[] fi , double[] ampli ,
			double[] fj , double[] amplj )
	{
		int szi = fi.length;
		int szj = fj.length;
		int i;
		int j;
		double d = 0.0;
		for( i = 0 ; i < szi ; i++ )
		{
			for( j = 0 ; j < szj ; j++ )
			{
				if( Math.abs( Math.max( fi[ i ] , fj[ j ] ) / Math.min( fi[ i ] , fj[ j ] ) ) < 1.5 )
				{
				double fmin = Math.min( fi[ i ] , fj[ j ] );
				double s = DSTAR / ( S1 * fmin + S2 );
				double fdif = Math.abs( fj[ j ] - fi[ i ] );
				double arg1 = A1 * s * fdif;
				double arg2 = A2 * s * fdif;
				double exp1 = Math.exp( arg1 );
				double exp2 = Math.exp( arg2 );
				double dnew = Math.min( ampli[ i ] , amplj[ j ] ) *
					( C1 * exp1 + C2 * exp2 );
				d += dnew;
				}
			}
		}
		
		return( d );
	}
	
	/**
	 * Calculates the Fourier coefficients of a note.
	 * @param note The input note.
	 * @param f Output array of coefficient ratios over the base frequency at which the Fourier coefficients were calculated.
	 * @param ampl Output array of Fourier coefficients.
	 * @param core The number of the core thread.
	 */
	public static void calcFourierCoeff( NoteDesc note , double[] f , double[] ampl , final int core )
	{
		final int sz = f.length;
		int count;
		for( count = 0 ; count < sz ; count++ )
		{
			double freq = count + 1;
			f[ count ] = freq;
			ampl[ count ] = calcFourierCoeff( note , freq , core);
		}
	}
	
	/**
	 * Calculates the Fourier coefficient of a note.
	 * @param note The input note.
	 * @param freqCoeff The coefficient ratio over the base frequency at which to calculate the Fourier coefficient.
	 * @param core The number of the core thread.
	 * @return The Fourier coefficient.
	 */
	public static double calcFourierCoeff( NoteDesc note , double freqCoeff , final int core )
	{
		int count;
		double mean = 0.0;
		for( count = 0 ; count < FOURIER_SAMPLE ; count++ )
		{
			double u = ( (double) count ) / FOURIER_SAMPLE;
			WaveForm w = note.getWaveform( core );
			mean += w.eval( u + note.getSteadyStateWaveNum() );
		}
		mean = mean / FOURIER_SAMPLE;
		double cosTot = 0.0;
		double sinTot = 0.0;
		for( count = 0 ; count < FOURIER_SAMPLE ; count++ )
		{
			double u = ( (double) count ) / FOURIER_SAMPLE;
			WaveForm w = note.getWaveform( core );
			double eval = w.eval( u + note.getSteadyStateWaveNum() );
			double uf = u * freqCoeff;
			sinTot += ( eval - mean ) * SINE_WAVE.eval( uf );
			cosTot += ( eval - mean ) * COSINE_WAVE.eval( uf );
		}
		return( Math.sqrt( sinTot * sinTot + cosTot * cosTot ) );
	}
	
	/**
	 * Calculates the Fourier coefficient of a set of notes.
	 * @param notes The input set of notes.
	 * @param baseFreq The frequency at which to calculate the Fourier coefficient.
	 * @param core The number of the core thread.
	 * @return The Fourier coefficient.
	 */
	public double calcFourierCoeff( ArrayList<NoteDesc> notes , final double baseFreq , final int core )
	{
		int count;
		final int nsz = notes.size();
		double mean = 0.0;
		for( count = 0 ; count < FOURIER_SAMPLE ; count++ )
		{
			double u = ( (double) count ) / FOURIER_SAMPLE;
			int cnt;
			for( cnt = 0 ; cnt < nsz ; cnt++ )
			{
				NoteDesc note = notes.get( cnt );
				double vol = ( (Double)( note.getRefcon() ) ).doubleValue();
				double noteFreq = getNoteFrequency( note , core );
				WaveForm w = note.getWaveform( core );
				if( !( w.useAutocorrelation() ) )
				{
					mean += vol * w.eval( u + note.getSteadyStateWaveNum() );
				}
				else
				{
					double strt = SongData.getElapsedTimeForBeatBeat( beatNumber , core ) - 
						SongData.getElapsedTimeForBeatBeat( note.getActualStartBeatNumber() , core );
					mean += vol * w.eval( u / noteFreq + strt );
				}
			}
		}
		mean = mean / FOURIER_SAMPLE;
		double cosTot = 0.0;
		double sinTot = 0.0;
		for( count = 0 ; count < FOURIER_SAMPLE ; count++ )
		{
			double u = ( (double) count ) / FOURIER_SAMPLE;
			int cnt;
			for( cnt = 0 ; cnt < nsz ; cnt++ )
			{
				NoteDesc note = notes.get( cnt );
				double vol = ( (Double)( note.getRefcon() ) ).doubleValue();
				double noteFreq = getNoteFrequency( note , core );
				double freqCoeff = baseFreq / noteFreq;
				WaveForm w = note.getWaveform( core );
				double eval = 0.0;
				if( !( w.useAutocorrelation() ) )
				{
					eval = vol * w.eval( u + note.getSteadyStateWaveNum() );
				}
				else
				{
					double strt = SongData.getElapsedTimeForBeatBeat( beatNumber , core ) - 
						SongData.getElapsedTimeForBeatBeat( note.getActualStartBeatNumber() , core );
					eval = vol * w.eval( u / noteFreq + strt );
				}
				double uf = u * freqCoeff;
				sinTot += ( eval - mean ) * SINE_WAVE.eval( uf );
				cosTot += ( eval - mean ) * COSINE_WAVE.eval( uf );
			}
		}
		return( Math.sqrt( sinTot * sinTot + cosTot * cosTot ) );
	}

	
}


