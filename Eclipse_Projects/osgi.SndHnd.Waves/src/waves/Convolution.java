





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

import gredit.GNonClampedCoefficient;
import gredit.GWaveForm;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import core.NonClampedCoefficient;
import core.WaveForm;

/**
 * Waveform performing convolution reverb on an input wave.
 * 
 * See <A href="https://en.wikipedia.org/wiki/Convolution_reverb">https://en.wikipedia.org/wiki/Convolution_reverb</A>
 * 
 * @author thorngreen
 */
public class Convolution extends WaveForm implements Externalizable {
	
	/**
	 * The input waveform.
	 */
	WaveForm waveA;
	
	/**
	 * The impulse response of the reverb.
	 */
	NonClampedCoefficient impulseResponse;
	
	/**
	 * The length of the period over which to integrate the reverb.
	 */
	double length;

	/**
	 * The bias to apply to the parameter when integrating the reverb.
	 */
	double bias;
	
	/**
	 * The number of samples to use in integrating the reverb.
	 */
	int numSamples;

	/**
	 * Constructs the waveform.
	 * @param _waveA The input waveform.
	 * @param _impulseResponse The impulse response of the reverb.
	 * @param _length The length of the period over which to integrate the reverb.
	 * @param _bias The bias to apply to the parameter when integrating the reverb.
	 * @param _numSamples The number of samples to use in integrating the reverb.
	 */
	public Convolution( WaveForm _waveA, NonClampedCoefficient _impulseResponse,
			double _length, double _bias, int _numSamples ) {
		super();
		waveA = _waveA;
		impulseResponse = _impulseResponse;
		length = _length;
		bias = _bias;
		numSamples = _numSamples;
	}

	@Override
	public double eval(double p) {
		double sum = 0.0;
		int count;
		for( count = 0 ; count < numSamples ; count++ )
		{
			final double pa = length * ( (double) count ) / ( numSamples - 1 );
			final double p2 = pa + p + bias;
			sum += waveA.eval( p2 ) * impulseResponse.eval( pa );
		}
		return( sum );
	}
	
	@Override
	public NonClampedCoefficient genClone() throws Throwable
	{
		final WaveForm wvA = (WaveForm)( waveA.genClone() );
		final NonClampedCoefficient cf = (NonClampedCoefficient)( impulseResponse.genClone() );
		if( ( wvA == waveA ) && ( cf == impulseResponse ) )
		{
			return( this );
		}
		else
		{
			return( new Convolution( wvA , cf , length , bias , numSamples ) );
		}
	}
	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GConvolution wv = new GConvolution();
		s.put(this, wv);
		
		GWaveForm wA = waveA.genWave(s);
		GNonClampedCoefficient cf = impulseResponse.genCoeff(s);
		
		wv.load(wA,cf,length,bias,numSamples);
		
		return( wv );
	}
	
	public void readExternal(ObjectInput in) throws IOException,
	ClassNotFoundException {
		// TODO Auto-generated method stub
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		// TODO Auto-generated method stub
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	}

}

