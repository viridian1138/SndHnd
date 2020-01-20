





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







package intonations;

import greditinton.GIntonation;

import java.util.ArrayList;
import java.util.HashMap;

import core.DissonanceCalculator;
import core.Intonation;
import core.NoteTable;
import core.WaveForm;
import cwaves.CosineWaveform;
import cwaves.SineWaveform;

/**
 * An intonation that starts with a base pitch and puts the steps in the scale
 * at the pitches that have a minimum dissonance with the base pitch.  In
 * practice, the best harmonies are slightly separated from the point of
 * minimum dissonance, and hence there are tuning parameters to slide
 * slightly up the curve from a local minimum dissonance to its neighboring
 * local maximum dissonance.
 * 
 * To some extent this intonation is an outgrowth of the section of the 
 * Sethares book "Tuning Timbre Spectrum and Scale" where he
 * speculates that musical scales result from the pitches of minimum
 * dissonance for a particular instrument.  This class makes it possible
 * to automatically determine a scale for a non-traditional timbre.
 * 
 * @author tgreen
 *
 */
public class DissonanceIntonation extends Intonation {
	
	/**
	 * Sine function used in Fourier transform calculations.
	 */
	protected static final SineWaveform SINE_WAVE = new SineWaveform();

	/**
	 * Cosine function used in Fourier transform calculations.
	 */
	protected static final CosineWaveform COSINE_WAVE = new CosineWaveform();

	/**
	 * The number of samples to evaluate when calculating the Fourier transform.
	 */
	public static final int FOURIER_SAMPLE = 200;
	
	/**
	 * The number of harmonics to include in the Fourier transform.
	 */
	final int NUM_HARMONICS = DissonanceCalculator.NUM_HARMONICS;
	
	/**
	 * The Fourier frequencies for the waveform that stays at the base of the scale for the dissonance calculation.
	 */
	final double[] bfi = new double[ NUM_HARMONICS ];
	
	/**
	 * The Fourier amplitudes for the waveform that stays at the base of the scale for the dissonance calculation.
	 */
	final double[] ampli = new double[ NUM_HARMONICS ];
	
	/**
	 * The Fourier frequencies for the waveform that moves up the scale for the dissonance calculation.
	 */
	final double[] bfj = new double[ NUM_HARMONICS ];
	
	/**
	 * The Fourier amplitudes for the waveform that moves up the scale for the dissonance calculation.
	 */
	final double[] amplj = new double[ NUM_HARMONICS ];
	
	/**
	 * Input intonation providing an initial starting point (e.g. the number of pitches in the desired scale).
	 */
	Intonation i1;
	
	/**
	 * Input intonation providing an initial starting point (e.g. the number of pitches in the desired scale).
	 */
	GIntonation i1a;
	
	/**
	 * The waveform that stays at the base of the scale for the dissonance calculation.
	 */
	WaveForm wbase;
	
	/**
	 * The waveform that moves up the scale for the dissonance calculation.
	 */
	WaveForm wmov;
	
	/**
	 * The name of the key in which to calculate the intonation.
	 */
	String key;
	
	/**
	 * The number of the melodic interval in which to calculate the intonation.
	 */
	int melodicIntervalNumber;
	
	/**
	 * Cache holding the best pitches for the intonation found so far.
	 */
	double[] cache = null;
	
	/**
	 * Constructs the intonation.
	 * @param _i1 Input intonation providing an initial starting point (e.g. the number of pitches in the desired scale).
	 * @param _i1a Input intonation providing an initial starting point (e.g. the number of pitches in the desired scale).
	 * @param _wbase The waveform that stays at the base of the scale for the dissonance calculation.
	 * @param _wmov The waveform that moves up the scale for the dissonance calculation.
	 * @param _key The name of the key in which to calculate the intonation.
	 * @param _melodicIntervalNumber The number of the melodic interval in which to calculate the intonation.
	 */
	public DissonanceIntonation( Intonation _i1 , GIntonation _i1a , WaveForm _wbase , WaveForm _wmov , String _key , int _melodicIntervalNumber )
	{
		i1 = _i1;
		i1a = _i1a;
		wbase = _wbase;
		wmov = _wmov;
		key = _key;
		melodicIntervalNumber = _melodicIntervalNumber;
	}
	
	@Override
	public double[] calcIntonation()
	{
		final double[] iin = i1.calcIntonation();
		final String[] scaleNames = i1a.getScaleNames();
		int count;
		int index = -1;
		for( count = 0 ; count < scaleNames.length ; count++ )
		{
			if( key.equals( scaleNames[ count ] ) )
			{
				index = count;
			}
		}
		if( index < 0 )
		{
			throw( new RuntimeException( "Bad" ) );
		}
		final double freq = NoteTable.getNoteFrequencyDefaultScale_Key( melodicIntervalNumber , index , iin );
		final double[] icomp = calcIntonationComp( freq );
		final int IL = icomp.length - 1;
		final double BASE = iin[ index ];
		final double[] ret = new double[ icomp.length ];
		
		for( count = 0 ; count < IL ; count++ )
		{
			ret[ ( count + index ) % IL ] = icomp[ count ] * BASE; 
		}
		ret[ count ] = ret[ 0 ] * ( getMelodicIntervalRatio() );
		
		for( count = 0 ; count < icomp.length ; count++ )
		{
			ret[ count ] = i1.validateIntonation( ret[ count ] , count );
		}
		
		return( ret );
	}

	/**
	 * Calculates the intonation pitches above a particular base pitch.
	 * @param freq The base pitch.
	 * @return The intonation pitches.
	 */
	public double[] calcIntonationComp( final double freq ) {
		try {
		if( cache == null )
		{
			final double[] iin = i1.calcIntonation();
			cache = new double[ iin.length ];
			
			calcFourierCoeff( wbase, 100.0, freq , bfi, ampli );
			
			calcFourierCoeff( wmov, 100.0, 1.0, bfj, amplj );

			ArrayList<Double> minima = new ArrayList<Double>();
			ArrayList<Double> maxima = new ArrayList<Double>();

			int max = 6000;
			
			System.out.println("Starting Minimum Calculations...");

			findMinimaAndMaxima(0.5 * freq, 4 * freq, max, minima, maxima);
			
			int count;
			for( count = 0 ; count < cache.length ; count++ )
			{
				cache[ count ] = -1.0;
			}
			
			boolean done = false;
			while( !done && ( minima.size() > 0 ) )
			{
				double bestDist = 1E+12;
				int bestIndex = -1;
				Double bestDbl = null;
				double bestVal = -1.0;
				for( count = 0 ; count < cache.length ; count++ )
				{
					if( cache[ count ] < 0.0 )
					{
						final double clog = Math.log( iin[ count ] );
						int cnt1;
						for( cnt1 = 0 ; cnt1 < minima.size(); cnt1++ )
						{
							final Double bd = minima.get( cnt1 );
							double pt = ( bd.doubleValue() ) / freq;
							final double dist = Math.abs( Math.log( pt ) - clog );
							if( dist < bestDist )
							{
								if( crossScan( cache , count , pt ) )
								{
									bestDist = dist;
									bestDbl = bd;
									bestIndex = count;
									bestVal = pt;
								}
							}
						}
					}
				}
				
				if( bestIndex < 0 )
				{
					for( count = 0 ; count < cache.length ; count++ )
					{
						if( cache[ count ] < 0.0 )
						{
							final double clog = Math.log( iin[ count ] );
							int cnt1;
							for( cnt1 = 0 ; cnt1 < maxima.size(); cnt1++ )
							{
								final Double bd = maxima.get( cnt1 );
								double pt = ( bd.doubleValue() ) / freq;
								final double dist = Math.abs( Math.log( pt ) - clog );
								if( dist < bestDist )
								{
									if( crossScan( cache , count , pt ) )
									{
										bestDist = dist;
										bestDbl = bd;
										bestIndex = count;
										bestVal = pt;
									}
								}
							}
						}
					}
					
					if( bestIndex >= 0 )
					{
						cache[ bestIndex ] = bestVal;
						maxima.remove( bestDbl );
					}
				}
				else
				{
					cache[ bestIndex ] = bestVal;
					minima.remove( bestDbl );
				}
				
				
				done = bestIndex < 0;
			}
			
			for( count = 0 ; count < cache.length ; count++ )
			{
				if( cache[ count ] < 0.0 )
				{
					cache[ count ] = iin[ count ];
				}
				/* System.out.println( "---" );
				System.out.println( cache[ count ] );
				System.out.println( iin[ count ] ); */
			}
			
			/* done = false;
			while( !done )
			{
				done = true;
				for( count = 0 ; count < ( cache.length - 1 ) ; count++ )
				{
					if( cache[ count + 1 ] < cache[ count ] )
					{
						double tmp = cache[ count + 1 ];
						cache[ count + 1 ] = cache[ count ];
						cache[ count ] = tmp;
						done = false;
					}
				}
			} */
			
		}
		}
		catch( Throwable ex )
		{
			ex.printStackTrace( System.out );
			throw( new RuntimeException( "Bad" ) );
		}
		
		return( cache );
	}
	
	/**
	 * Scans the current tentative intonation scale to verify that a particular pitch
	 * can fit pitch-wise at a particular index in the intonation.
	 * @param arr The tentative intonation.
	 * @param bestIndex The preliminary index at which to place the pitch.
	 * @param bestVal The preliminary pitch.
	 * @return True if the insertion would be valid.  False otherwise.
	 */
	protected boolean crossScan( final double[] arr , final int bestIndex , final double bestVal )
	{
		int count;
		for( count = 0 ; count < bestIndex ; count++ )
		{
			if( ( arr[ count ] > 0.0 ) && ( arr[ count ] > bestVal ) )
			{
				return( false );
			}
		}
		for( count = bestIndex + 1 ; count < arr.length ; count++ )
		{
			if( ( arr[ count ] > 0.0 ) && ( arr[ count ] < bestVal ) )
			{
				return( false );
			}
		}
		return( true );
	}

	
	@Override
	public GIntonation genInton(HashMap s) {
		throw( new RuntimeException( "NotSupported !!!!!!!!!!!!!!!!!!!!!!!!!!" ) );
	}
	
	@Override
	public double getMelodicIntervalRatio()
	{
		return( i1.getMelodicIntervalRatio() );
	}

	@Override
	public double validateIntonation(double val, int index) {
		return( i1.validateIntonation(val, index) );
	}
	
	/**
	 * Finds the dissonance minima and maxima in a frequency range.
	 * @param fj1 The starting pitch of the frequency range.
	 * @param fj2 The ending pitch of the frequency range.
	 * @param max The number of pitches to evaluate in the range.
	 * @param outMin The dissonance minima pitches to be returned to the caller.
	 * @param outMax The dissonance maxima pitches to be returned to the caller.
	 * @throws Throwable
	 */
	public void findMinimaAndMaxima( double fj1 , double fj2 , int max, ArrayList<Double> outMin , ArrayList<Double> outMax ) throws Throwable
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
	 * Calculates the amount of dissonance for a particular frequency versus the base frequency.
	 * @param freqj The input frequency.
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
		
		return( DissonanceCalculator.calcDissonance( bfi , ampli , fj , amplj ) );
	}
	
	/**
	 * Calculates Fourier coefficients for a wave.
	 * @param w The waveform for which to calculate the coefficients.
	 * @param steadyStateWaveNumber The waveform for which to calculate the coefficient.
	 * @param fbase Base frequency coefficient versus the base frequency of 1.0.
	 * @param freqa Fourier frequency coefficients to be returned to caller.
	 * @param ampl Fourier coefficient amplitudes to be returned to caller.
	 */
	public void calcFourierCoeff(WaveForm w, double steadyStateWaveNumber, double fbase , double[] freqa, double[] ampl ) {
		int sz = freqa.length;
		int count;
		for (count = 0; count < sz; count++) {
			double freq = count + 1;
			freqa[count] = fbase * freq;
			ampl[count] = calcFourierCoeff(w, steadyStateWaveNumber, freq);
		}
	}
	
	/**
	 * Calculates a Fourier coefficient for a wave.
	 * @param w The waveform for which to calculate the coefficient.
	 * @param steadyStateWaveNum The wave number at which to start the evaluation.
	 * @param freqCoeff The frequency coefficient versus the base frequency of 1.0.
	 * @return The calculated Fourier coefficient.
	 */
	public static double calcFourierCoeff( WaveForm w , double steadyStateWaveNum , double freqCoeff )
	{
		int count;
		double mean = 0.0;
		for( count = 0 ; count < FOURIER_SAMPLE ; count++ )
		{
			double u = ( (double) count ) / FOURIER_SAMPLE;
			mean += w.eval( u + steadyStateWaveNum );
		}
		mean = mean / FOURIER_SAMPLE;
		double cosTot = 0.0;
		double sinTot = 0.0;
		for( count = 0 ; count < FOURIER_SAMPLE ; count++ )
		{
			double u = ( (double) count ) / FOURIER_SAMPLE;
			double eval = w.eval( u + steadyStateWaveNum );
			double uf = u * freqCoeff;
			sinTot += ( eval - mean ) * SINE_WAVE.eval( uf );
			cosTot += ( eval - mean ) * COSINE_WAVE.eval( uf );
		}
		return( Math.sqrt( sinTot * sinTot + cosTot * cosTot ) );
	}

	
}

