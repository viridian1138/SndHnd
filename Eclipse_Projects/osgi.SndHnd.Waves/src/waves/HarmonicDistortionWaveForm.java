





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








package waves;

import gredit.GWaveForm;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;


import core.NonClampedCoefficient;
import core.WaveForm;

/**
 * Waveform that adds copies of an input wave at even harmonics and/or odd harmonics.
 * 
 * @author tgreen
 *
 */
public class HarmonicDistortionWaveForm extends WaveForm {

	/**
	 * The input waveform.
	 */
	WaveForm w;
	
	/**
	 * The amplitude multiplier for the first harmonic distortion and each subsequent harmonic distortion..
	 */
	double firstHarmonicDistortion;
	
	/**
	 * The maximum number of harmonics to generate.
	 */
	int maxHarmonicNum;
	
	/**
	 * The amplitude multiplier for the first subharmonic distortion and each subsequent subharmonic distortion.
	 */
	double firstSubHarmonicDistortion;
	
	/**
	 * The maximum number of subharmonics to generate.
	 */
	int maxSubHarmonicNum;
	
	/**
	 * Whether to generate odd harmonics and/or subharmonics.
	 */
	boolean oddHarmonics;
	
	/**
	 * Whether to generate even harmonics and/or subharmonics.
	 */
	boolean evenHarmonics;
	
	/**
	 * Whether to calculate a sum or harmonic magnitudes to normalize the result.
	 */
	boolean useDivisor;
	
	/**
	 * Internally calculated sum of harmonic magnitudes used to normalize the result.
	 */
	double divisor;

	
	/**
	 * Constructs the waveform.
	 * @param _w The input waveform.
	 * @param _firstHarmonicDistortion The amplitude multiplier for the first harmonic distortion and each subsequent harmonic distortion..
	 * @param _maxHarmonicNum The maximum number of harmonics to generate.
	 * @param _firstSubHarmonicDistortion The amplitude multiplier for the first subharmonic distortion and each subsequent subharmonic distortion.
	 * @param _maxSubHarmonicNum  The maximum number of subharmonics to generate.
	 * @param _oddHarmonics Whether to generate odd harmonics and/or subharmonics.
	 * @param _evenHarmonics Whether to generate even harmonics and/or subharmonics.
	 * @param _useDivisor Whether to calculate a sum or harmonic magnitudes to normalize the result.
	 */
	public HarmonicDistortionWaveForm( WaveForm _w , 
			double _firstHarmonicDistortion , int _maxHarmonicNum , 
			double _firstSubHarmonicDistortion , int _maxSubHarmonicNum ,
			boolean _oddHarmonics , boolean _evenHarmonics , boolean _useDivisor ) {
		super();
		w = _w;
		firstHarmonicDistortion = _firstHarmonicDistortion;
		maxHarmonicNum = _maxHarmonicNum;
		firstSubHarmonicDistortion = _firstSubHarmonicDistortion;
		maxSubHarmonicNum = _maxSubHarmonicNum;
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
			return( new HarmonicDistortionWaveForm( wv , 
					firstHarmonicDistortion , maxHarmonicNum , 
					firstSubHarmonicDistortion , maxSubHarmonicNum ,
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
		
		GHarmonicDistortionWaveForm wv = new GHarmonicDistortionWaveForm();
		s.put(this, wv);
		
		GWaveForm ww = w.genWave(s);
		
		wv.load(ww,firstHarmonicDistortion , maxHarmonicNum , 
				firstSubHarmonicDistortion , maxSubHarmonicNum ,
				oddHarmonics , evenHarmonics , useDivisor);
		
		return( wv );
	}
	
	/**
	 * Initializes internal data structures.
	 */
	protected void init()
	{
		int count;
		double sum = 1.0;
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
		
		mult = 1.0;
		
		for( count = 2 ; count < maxSubHarmonicNum ; count++ )
		{
			mult = mult * firstSubHarmonicDistortion;
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
		double sum = w.eval( param );
		double mult = 1.0;
		
		for( count = 2 ; count < maxHarmonicNum ; count++ )
		{
			mult = mult * firstHarmonicDistortion;
			if( count % 2 == 0 )
			{
				if( evenHarmonics )
				{
					double strt = (int) param;
					double mup = count * param;
					double p = strt + ( mup - (int) mup );
					sum += mult * w.eval( p );
				}
			}
			else
			{
				if( oddHarmonics )
				{
					double strt = (int) param;
					double mup = count * param;
					double p = strt + ( mup - (int) mup );
					sum += mult * w.eval( p );
				}
			}
		}
		
		mult = 1.0;
		
		for( count = 2 ; count < maxSubHarmonicNum ; count++ )
		{
			mult = mult * firstSubHarmonicDistortion;
			if( count % 2 == 0 )
			{
				if( evenHarmonics )
				{
					double strt = (int) param;
					double mup = param / count;
					double p = strt + ( mup - (int) mup );
					sum += mult * w.eval( p );
				}
			}
			else
			{
				if( oddHarmonics )
				{
					double strt = (int) param;
					double mup = param / count;
					double p = strt + ( mup - (int) mup );
					sum += mult * w.eval( p );
				}
			}
		}
		
		if( useDivisor )
		{
			sum = sum / divisor;
		}
		
		return( sum );
	}

	
	/**
	 * TBD.
	 */
	public void writeExternal(ObjectOutput arg0) throws IOException {
		// TODO Auto-generated method stub
		throw( new RuntimeException("Not Supported."));
	}

	
	/**
	 * TBD.
	 */
	public void readExternal(ObjectInput arg0) throws IOException,
			ClassNotFoundException {
		// TODO Auto-generated method stub
		throw( new RuntimeException("Not Supported."));
	}

	
}

