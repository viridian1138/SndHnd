




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







package cwaves;
import gredit.GWaveForm;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import core.NonClampedCoefficient;
import core.WaveForm;

/**
 * Waveform for adding harmonic distortion to an input waveform.
 * 
 * 
 * See:  https://www.soundonsound.com/techniques/analogue-warmth
 * 
 * 
 * See:  https://www.waves.com/add-harmonic-distortion-for-analog-warmth
 * 
 * 
 * @author tgreen
 *
 */
public class HarmonicDistortionSampleWaveForm extends WaveForm {
	
	/**
	 * The input waveform.
	 */
	WaveForm w;
	
	/**
	 * Amplitude multiplier to go from the amplitude of the input waveform to the amplitude of the first harmonic distortion.  This is used to calculate the exponential decay in amplitude for distortions on all subsequent harmonics.
	 */
	double firstHarmonicDistortion;
	
	/**
	 * The maximum harmonic number for which to add harmonic distortions.
	 */
	int maxHarmonicNum;
	
	/**
	 * Whether to add odd harmonics.
	 */
	boolean oddHarmonics;
	
	/**
	 * Whether to add even harmonics.
	 */
	boolean evenHarmonics;
	
	/**
	 * Whether to use a divisor to normalize the amplitude of the resulting wave.
	 */
	boolean useDivisor;
	
	/**
	 * Divisor used to normalize the amplitude of the resulting wave.
	 */
	double divisor;
	
	/**
	 * The discretization size over which to evaluate the harmonic distortion.  This tends to low-pass filter the wave that gets added as the harmonic distortion at a higher harmonic.
	 */
	double freq = /* 98.0; */ 199.0; // For now !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

	
	/**
	 * Constructs the waveform.
	 * @param _w The input waveform.
	 * @param _firstHarmonicDistortion Amplitude multiplier to go from the amplitude of the input waveform to the amplitude of the first harmonic distortion.  This is used to calculate the exponential decay in amplitude for distortions on all subsequent harmonics.
	 * @param _maxHarmonicNum The maximum harmonic number for which to add harmonic distortions.
	 * @param _oddHarmonics Whether to add odd harmonics.
	 * @param _evenHarmonics Whether to add even harmonics.
	 * @param _useDivisor Whether to use a divisor to normalize the amplitude of the resulting wave.
	 */
	public HarmonicDistortionSampleWaveForm( WaveForm _w , double _firstHarmonicDistortion ,
			int _maxHarmonicNum , boolean _oddHarmonics , boolean _evenHarmonics , boolean _useDivisor ) {
		super();
		w = _w;
		firstHarmonicDistortion = _firstHarmonicDistortion;
		maxHarmonicNum = _maxHarmonicNum;
		oddHarmonics = _oddHarmonics;
		evenHarmonics = _evenHarmonics;
		useDivisor = _useDivisor;
		init();
	}
	
	@Override
	public NonClampedCoefficient genClone() throws Throwable
	{
		final WaveForm wv = (WaveForm)( w.genClone() );
		if( wv == w )
		{
			return( this );
		}
		else
		{
			return( new HarmonicDistortionSampleWaveForm( wv , 
					firstHarmonicDistortion , maxHarmonicNum , 
					oddHarmonics , evenHarmonics , useDivisor ) );
		}
	}
	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GHarmonicDistortionSampleWaveForm wv = new GHarmonicDistortionSampleWaveForm();
		s.put(this, wv);
		
		GWaveForm ww = w.genWave(s);
		
		wv.load(ww,firstHarmonicDistortion , maxHarmonicNum , 
				oddHarmonics , evenHarmonics , useDivisor);
		
		return( wv );
	}
	
	/**
	 * Initializes the value of the divisor member.
	 */
	protected void init()
	{
		int count;
		double sum = 0.25;
		double mult = 1.0;
		
		for( count = 2 ; count < maxHarmonicNum ; count++ )
		{
			mult = mult * firstHarmonicDistortion;
			if( count % 2 == 0 )
			{
				if( evenHarmonics )
				{
					sum += mult;
				}
			}
			else
			{
				if( oddHarmonics )
				{
					sum += mult;
				}
			}
		}
		divisor = sum;
	}

	@Override
	public double eval( double param ) {
		int count;
		double sum = 0.25 * w.eval( param );
		double mult = 1.0;
		
		for( count = 2 ; count < maxHarmonicNum ; count++ )
		{
			mult = mult * firstHarmonicDistortion;
			if( count % 2 == 0 )
			{
				if( evenHarmonics )
				{
					double strt = (int) ( param * freq );
					double mup = ( 1.0 / count ) * param * freq;
					double p = strt + ( mup - (int) mup );
					sum += mult * w.eval( p / freq );
				}
			}
			else
			{
				if( oddHarmonics )
				{
					double strt = (int) ( param * freq );
					double mup = ( 1.0 / count ) * param * freq;
					double p = strt + ( mup - (int) mup );
					sum += mult * w.eval( p / freq );
				}
			}
		}
		
		if( useDivisor )
		{
			sum = sum / divisor;
		}
		
		return( sum );
	}
	
	@Override
	public boolean useAutocorrelation()
	{
		return( true );
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	public void writeExternal(ObjectOutput arg0) throws IOException {
		// TODO Auto-generated method stub
		throw( new RuntimeException("Not Supported."));
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	public void readExternal(ObjectInput arg0) throws IOException,
			ClassNotFoundException {
		// TODO Auto-generated method stub
		throw( new RuntimeException("Not Supported."));
	}

	
}


