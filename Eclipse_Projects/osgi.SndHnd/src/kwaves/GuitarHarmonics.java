




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

import core.WaveForm;
import cwaves.CosineWaveform;
import cwaves.SineWaveform;
import bezier.Cplx;

/**
 * The Fourier-transform-derived harmonics of a guitar (or other instrument).
 * 
 * @author tgreen
 *
 */
public class GuitarHarmonics {
	
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
	 * Fourier coefficients of sine-function harmonics determined by Fourier transform.
	 */
	private double[] hmsin = null;
	
	/**
	 * Fourier coefficients of cosine-function harmonics determined by Fourier transform.
	 */
	private double[] hmcos = null;
	
	/**
	 * Fourier coefficients of sine-function sub-harmonics (frequencies below the prescribed fundamental) determined by Fourier transform.
	 */
	private double[] subhmsin = null;
	
	/**
	 * Fourier coefficients of cosine-function sub-harmonics (frequencies below the prescribed fundamental) determined by Fourier transform.
	 */
	private double[] subhmcos = null;
	
	/**
	 * Fourier coefficient magnitudes of the harmonics determined by Fourier transform.
	 */
	private double[] hm = null;
	
	/**
	 * Fourier coefficient magnitudes of the sub-harmonics (frequencies below the prescribed fundamental) determined by Fourier transform.
	 */
	private double[] subhm = null;
	
	/**
	 * Gets the Fourier coefficient magnitudes of the harmonics determined by Fourier transform.
	 * @return Fourier coefficient magnitudes of the harmonics determined by Fourier transform.
	 */
	public double[] getHm()
	{
		if( hm == null )
		{
			final int s1 = hmsin.length;
			hm = new double[ s1 ];
			int count;
			for( count = 0 ; count < s1 ; count++ )
			{
				double sinTot = hmsin[ count ];
				double cosTot = hmcos[ count ];
				hm[ count ] = sinTot * sinTot + cosTot * cosTot;
			}
		}
		return( hm );
	}
	
	/**
	 * Returns the Fourier coefficient of a harmonic as a complex number with the sine-function harmonic in the real-part and a cosine-function harmonic in the imaginary part.
	 * @param cnt The number of the harmonic, starting at zero.
	 * @return The Fourier coefficient of a harmonic as a complex number with the sine-function harmonic in the real-part and a cosine-function harmonic in the imaginary part.
	 */
	public Cplx getHm( int cnt )
	{
		return( new Cplx( hmcos[ cnt ] , hmsin[ cnt ] ) );
	}
	
	/**
	 * Gets the Fourier coefficient magnitudes of the sub-harmonics (frequencies below the prescribed fundamental) determined by Fourier transform.
	 * @return Fourier coefficient magnitudes of the sub-harmonics (frequencies below the prescribed fundamental) determined by Fourier transform.
	 */
	public double[] getSubHm()
	{
		if( subhm == null )
		{
			final int s1 = subhmsin.length;
			subhm = new double[ s1 ];
			int count;
			for( count = 0 ; count < s1 ; count++ )
			{
				double sinTot = subhmsin[ count ];
				double cosTot = subhmcos[ count ];
				subhm[ count ] = sinTot * sinTot + cosTot * cosTot;
			}
		}
		return( subhm );
	}
	
	/**
	 * Returns the Fourier coefficient of a sub-harmonic (frequency below the prescribed fundamental) as a complex number with the sine-function sub-harmonic in the real-part and a cosine-function sub-harmonic in the imaginary part.
	 * @param cnt The number of the sub-harmonic, starting at zero.
	 * @return The Fourier coefficient of a sub-harmonic as a complex number with the sine-function sub-harmonic in the real-part and a cosine-function sub-harmonic in the imaginary part.
	 */
	public Cplx getSubHm( int cnt )
	{
		return( new Cplx( subhmcos[ cnt ] , subhmsin[ cnt ] ) );
	}
	
	/**
	 * Copy constructor.
	 * @param i2 Input GuitarHarmonics.
	 * @param i2Coeff Amplitude multiplier at which to input the harmonics.
	 */
	public GuitarHarmonics( GuitarHarmonics i2 , double i2Coeff )
	{
		final int s1 = i2.hmsin.length;
		final int s2 = i2.subhmsin.length;
		hmsin = new double[ s1 ];
		hmcos = new double[ s1 ];
		subhmsin = new double[ s2 ];
		subhmcos = new double[ s2 ];
		int count;
		for( count = 0 ; count < s1 ; count++ )
		{
			hmsin[ count ] = i2Coeff * i2.hmsin[ count ];
			hmcos[ count ] = i2Coeff * i2.hmcos[ count ];
		}
		for( count = 0 ; count < s2 ; count++ )
		{
			subhmsin[ count ] = i2Coeff * i2.subhmsin[ count ];
			subhmcos[ count ] = i2Coeff * i2.subhmcos[ count ];
		}
	}
	
	/**
	 * Constructs a composite of two GuitarHarmonics instances.
	 * @param i1 The first input GuitarHarmonics from which to construct.
	 * @param i2 The second input GuitarHarmonics from which to construct.
	 * @param i2Coeff Amplitude multiplier at which to input the second harmonics instance.
	 */
	public GuitarHarmonics( GuitarHarmonics i1 , GuitarHarmonics i2 , double i2Coeff )
	{
		final int s1 = i1.hmsin.length;
		final int s2 = i1.subhmsin.length;
		hmsin = new double[ s1 ];
		hmcos = new double[ s1 ];
		subhmsin = new double[ s2 ];
		subhmcos = new double[ s2 ];
		int count;
		for( count = 0 ; count < s1 ; count++ )
		{
			hmsin[ count ] = i1.hmsin[ count ] + i2Coeff * i2.hmsin[ count ];
			hmcos[ count ] = i1.hmcos[ count ] + i2Coeff * i2.hmcos[ count ];
		}
		for( count = 0 ; count < s2 ; count++ )
		{
			subhmsin[ count ] = i1.subhmsin[ count ] + i2Coeff * i2.subhmsin[ count ];
			subhmcos[ count ] = i1.subhmcos[ count ] + i2Coeff * i2.subhmcos[ count ];
		}
	}
	
	/**
	 * Constructs a composite of two GuitarHarmonics instances.
	 * @param i1 The first input GuitarHarmonics from which to construct.
	 * @param i1Coeff Amplitude multiplier at which to input the first harmonics instance.
	 * @param i2 The second input GuitarHarmonics from which to construct.
	 * @param i2Coeff Amplitude multiplier at which to input the second harmonics instance.
	 */
	public GuitarHarmonics( GuitarHarmonics i1 , double i1Coeff , GuitarHarmonics i2 , double i2Coeff )
	{
		final int s1 = i1.hmsin.length;
		final int s2 = i1.subhmsin.length;
		hmsin = new double[ s1 ];
		hmcos = new double[ s1 ];
		subhmsin = new double[ s2 ];
		subhmcos = new double[ s2 ];
		int count;
		for( count = 0 ; count < s1 ; count++ )
		{
			hmsin[ count ] = i1Coeff * i1.hmsin[ count ] + i2Coeff * i2.hmsin[ count ];
			hmcos[ count ] = i1Coeff * i1.hmcos[ count ] + i2Coeff * i2.hmcos[ count ];
		}
		for( count = 0 ; count < s2 ; count++ )
		{
			subhmsin[ count ] = i1Coeff * i1.subhmsin[ count ] + i2Coeff * i2.subhmsin[ count ];
			subhmcos[ count ] = i1Coeff * i1.subhmcos[ count ] + i2Coeff * i2.subhmcos[ count ];
		}
	}
	
	/**
	 * Constructs GuitarHarmonics for a WaveForm.
	 * @param in The input WaveForm from which to determine harmonics.
	 * @param steadyStateWaveNumber The wave number at which to start Fourier transforms (often zero isn't the best place to start because there may be an initial amplitude envelope before things really get going).
	 * @param samplesPerWave The number of samples to capture over each wavelength for the Fourier transform.
	 * @param numWaves The number of wavelengths over which to calculate Fourier coefficients.
	 * @param numHarmonics The number of harmonics over which to calculate Fourier coefficients.
	 * @param numSubHarmonics The number of sub-harmonics (frequencies below the prescribed fundamental) over which to calculate Fourier coefficients.
	 */
	public GuitarHarmonics( WaveForm in , double steadyStateWaveNumber , int samplesPerWave , int numWaves , int numHarmonics , int numSubHarmonics )
	{
		calcSuperHarmonics( in , steadyStateWaveNumber , samplesPerWave , numWaves , numHarmonics );
		calcSubHarmonics( in , steadyStateWaveNumber , samplesPerWave , numWaves , numSubHarmonics );
	}
	
	/**
	 * Constructor.
	 * @param _hmsin Fourier coefficients of sine-function harmonics determined by Fourier transform.
	 * @param _hmcos Fourier coefficients of cosine-function harmonics determined by Fourier transform.
	 * @param _subhmsin Fourier coefficients of sine-function sub-harmonics (frequencies below the prescribed fundamental) determined by Fourier transform.
	 * @param _subhmcos Fourier coefficients of cosine-function sub-harmonics (frequencies below the prescribed fundamental) determined by Fourier transform.
	 */
	public GuitarHarmonics( double[] _hmsin , double[] _hmcos , double[] _subhmsin , double[] _subhmcos )
	{
		hmsin = _hmsin;
		hmcos = _hmcos;
		subhmsin = _subhmsin;
		subhmcos = _subhmcos;
	}
	
	/**
	 * Constructs GuitarHarmonics for a zero-waveform.
	 * @param numHarmonics Number of Fourier coefficients for harmonics.
	 * @param numSubHarmonics Number of Fourier coefficients for sub-harmonics (frequencies below the prescribed fundamental).
	 */
	public GuitarHarmonics( int numHarmonics , int numSubHarmonics )
	{
		hmsin = new double[ numHarmonics ];
		hmcos = new double[ numHarmonics ];
		subhmsin = new double[ numSubHarmonics ];
		subhmcos = new double[ numSubHarmonics ];
		int count;
		for( count = 0 ; count < numHarmonics ; count++ )
		{
			hmsin[ count ] = 0.0;
			hmcos[ count ] = 0.0;
		}
		for( count = 0 ; count < numSubHarmonics ; count++ )
		{
			subhmsin[ count ] = 0.0;
			subhmcos[ count ] = 0.0;
		}
	}
	
	/**
	 * Gets enough samples of a WaveForm to capture harmonics and sub-harmonics.
	 * @param in The input WaveForm.
	 * @param steadyStateWaveNumber The wave number at which to start Fourier transforms (often zero isn't the best place to start because there may be an initial amplitude envelope before things really get going).
	 * @param samplesPerWave The number of samples to capture from each wavelength.
	 * @param numWaves The number of wavelengths over which to sample.
	 * @return The calculated array of samples.
	 */
	private double[] calcSuperSampleArray( final WaveForm in , final double steadyStateWaveNumber , final int samplesPerWave , final int numWaves )
	{
		double[] dbl = new double[ samplesPerWave ];
		final int max = samplesPerWave * numWaves;
		int count;
		for( count = 0 ; count < max ; count++ )
		{
			int index = count % samplesPerWave;
			double u = steadyStateWaveNumber + ( (double) count ) / samplesPerWave;
			dbl[ index ] += in.eval( u );
		}
		for( count = 0 ; count < samplesPerWave ; count++ )
		{
			dbl[ count ] = dbl[ count ] / numWaves;
		}
		return( dbl );
	}
	
	/**
	 * Calculates the mean over a set of samples.
	 * @param hma The input set of samples.
	 * @param samplesPerWave The number of samples over which to calculate the mean.
	 * @return The calculated mean.
	 */
	private double calcSampleMean( final double[] hma , final int samplesPerWave )
	{
		int count;
		double tot = 0.0;
		for( count = 0 ; count < samplesPerWave ; count++ )
		{
			tot += hma[ count ];
		}
		return( tot / samplesPerWave );
	}
	
	/**
	 * Calculates a Fourier-transform-derived harmonic of a set of samples.
	 * @param hma The input set of samples.
	 * @param sampleMean The calculated mean over the set of samples (used to de-bias the samples).
	 * @param samplesPerWave The number of samples over which to calculate the harmonic.
	 * @param freqCoeff The frequency coefficient over which to evaluate the samples.
	 * @param out Output Fourier coefficients where the sine coefficient is in index zero, and the cosine coefficient is in index one.
	 */
	private void calcSuperHarmonic( double[] hma , double sampleMean , int samplesPerWave , int freqCoeff , double[] out )
	{
		int count;
		double cosTot = 0.0;
		double sinTot = 0.0;
		for( count = 0 ; count < samplesPerWave ; count++ )
		{
			double u = ( (double) count ) / samplesPerWave;
			double eval = hma[ count ];
			double uf = u * freqCoeff;
			sinTot += ( eval - sampleMean ) * SINE_WAVE.eval( uf );
			cosTot += ( eval - sampleMean ) * COSINE_WAVE.eval( uf );
		}
		out[ 0 ] = sinTot;
		out[ 1 ] = cosTot;
	}
	
	/**
	 * Calculates a Fourier-transform-derived sub-harmonic (frequencies below the prescribed fundamental) of a set of samples.
	 * @param hma The input set of samples.
	 * @param sampleMean The calculated mean over the set of samples (used to de-bias the samples).
	 * @param samplesPerWave The number of samples over which to calculate the sub-harmonic.
	 * @param freqCoeff The frequency coefficient over which to evaluate the samples.
	 * @param out Output Fourier coefficients where the sine coefficient is in index zero, and the cosine coefficient is in index one.
	 */
	private void calcSubHarmonic( double[] hma , double sampleMean , int samplesPerWave , double freqCoeff , double[] out )
	{
		int count;
		double cosTot = 0.0;
		double sinTot = 0.0;
		for( count = 0 ; count < samplesPerWave ; count++ )
		{
			double u = ( (double) count ) / samplesPerWave;
			double eval = hma[ count ];
			double uf = u * freqCoeff;
			sinTot += ( eval - sampleMean ) * SINE_WAVE.eval( uf );
			cosTot += ( eval - sampleMean ) * COSINE_WAVE.eval( uf );
		}
		out[ 0 ] = sinTot;
		out[ 1 ] = cosTot;
	}
	
	/**
	 * Calculates the Fourier-transform-derived harmonics of a WaveForm.
	 * @param in The input WaveForm.
	 * @param steadyStateWaveNumber The wave number at which to start Fourier transforms (often zero isn't the best place to start because there may be an initial amplitude envelope before things really get going).
	 * @param samplesPerWave The number of samples to capture over each wavelength for the Fourier transform.
	 * @param inumWaves The number of wavelengths over which to calculate Fourier coefficients.
	 * @param numHarmonics The number of harmonics over which to calculate Fourier coefficients.
	 */
	private void calcSuperHarmonics( WaveForm in , double steadyStateWaveNumber , int samplesPerWave , int numWaves , int numHarmonics )
	{
		final double[] oc = new double[ 2 ];
		final double[] hma = calcSuperSampleArray( in , steadyStateWaveNumber , samplesPerWave , numWaves );
		final double sampMean = calcSampleMean( hma , samplesPerWave );
		int count;
		hmsin = new double[ numHarmonics ];
		hmcos = new double[ numHarmonics ];
		for( count = 0 ; count < numHarmonics ; count++ )
		{
			calcSuperHarmonic( hma , sampMean , samplesPerWave , count + 1 , oc );
			hmsin[ count ] = oc[ 0 ];
			hmcos[ count ] = oc[ 1 ];
		}
	}
	
	/**
	 * Calculates the Fourier-transform-derived sub-harmonics (frequencies below the prescribed fundamental) of a WaveForm.
	 * @param in The input WaveForm.
	 * @param steadyStateWaveNumber The wave number at which to start Fourier transforms (often zero isn't the best place to start because there may be an initial amplitude envelope before things really get going).
	 * @param samplesPerWave The number of samples to capture over each wavelength for the Fourier transform.
	 * @param inumWaves The number of wavelengths over which to calculate Fourier coefficients.
	 * @param numHarmonics The number of sub-harmonics over which to calculate Fourier coefficients.
	 */
	private void calcSubHarmonics( WaveForm in , double steadyStateWaveNumber , int samplesPerWave , int inumWaves , int numHarmonics )
	{
		final double[] oc = new double[ 2 ];
		int count;
		subhmsin = new double[ numHarmonics ];
		subhmcos = new double[ numHarmonics ];
		for( count = 0 ; count < numHarmonics ; count++ )
		{
			final int hmc = count + 2;
			final int numWaves = ( inumWaves / hmc ) * hmc;
			final double[] hma = calcSuperSampleArray( in , steadyStateWaveNumber , samplesPerWave , numWaves );
			final double sampMean = calcSampleMean( hma , samplesPerWave );
			final double freqCoeff = 1.0 / hmc;
			calcSubHarmonic( hma , sampMean , samplesPerWave , freqCoeff , oc );
			subhmsin[ count ] = oc[ 0 ];
			subhmcos[ count ] = oc[ 1 ];
		}
	}

	
}

