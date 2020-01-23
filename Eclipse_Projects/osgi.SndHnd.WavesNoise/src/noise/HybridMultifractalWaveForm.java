





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







package noise;

import gredit.GWaveForm;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import core.NonClampedCoefficient;
import core.WaveForm;

/**
 * Waveform approximating a hybrid multifractal as described in the book "Texturing and Modeling" by David S. Ebert et. al.
 * @author thorngreen
 *
 */
public class HybridMultifractalWaveForm extends WaveForm {
	
	/**
	 * The noise to be applied in generating the hybrid multifractal.  Typically this would be a lattice noise.
	 */
	private WaveForm noise;
	
	/**
	 * The H parameter of the hybrid multifractal.
	 */
	double H; 
	
	/**
	 * The lacunarity of the hybrid multifractal.
	 */
	double lacunarity; 
	
	/**
	 * The number of octaves over which to evaluate the hybrid multifractal.
	 */
	int octaves; 
	
	/**
	 * The offset parameter for the hybrid multifractal.
	 */
	double offset;
	
	/**
	 * Internal gain parameter used in calculating the wave.
	 */
	double q0;
	
	/**
	 * Constructs the waveform.
	 * @param _noise The noise to be applied in generating the hybrid multifractal.  Typically this would be a lattice noise.
	 * @param _H The H parameter of the hybrid multifractal.
	 * @param _lacunarity The lacunarity of the hybrid multifractal.
	 * @param _octaves The number of octaves over which to evaluate the hybrid multifractal.
	 * @param _offset The offset parameter for the hybrid multifractal.
	 */
	public HybridMultifractalWaveForm( WaveForm _noise , double _H , 
			double _lacunarity , int _octaves , double _offset )
	{
		noise = _noise;
		H = _H;
		lacunarity = _lacunarity;
		octaves = _octaves;
		offset = _offset;
		
		q0 = lacunarity;
	}

	@Override
	public double eval(double p) {
		int i;
		double p1 = 1.0;
		double result = ( offset + noise.eval( p ) ) * ( Math.pow( p1 , - H ) );
		double weight = result;
		p *= lacunarity;
		
		for( i = 1 ; i < octaves ; i++ )
		{
			p1 = p1 * q0;
			if( weight > 1.0 ) weight = 1.0;
			double signal = ( noise.eval( p ) + offset ) * Math.pow( p1 , - H );
			result += weight * signal;
			weight *= signal;
			p *= lacunarity;
		}
		
		p1 = p1 * q0;
		
		double remainder = octaves - (int) octaves;
		if( Math.abs( remainder ) > 1E-9 )
		{
			result += remainder * noise.eval( p ) * Math.pow( p1 , - H );
		}
		
		return( result );
	}

	@Override
	public NonClampedCoefficient genClone() throws Throwable
	{
		final WaveForm wv = (WaveForm)( noise.genClone() );
		if( wv == noise )
		{
			return( this );
		}
		else
		{
			return( new HybridMultifractalWaveForm( wv , H , lacunarity , octaves, offset ) );
		}
	}
	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GHybridMultifractalWaveForm wv = new GHybridMultifractalWaveForm();
		s.put(this, wv);
		
		GWaveForm ww = noise.genWave(s);
		
		wv.load(ww,H,lacunarity,octaves,offset);
		
		return( wv );
	}

	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		// TODO Auto-generated method stub !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

	}

	public void writeExternal(ObjectOutput out) throws IOException {
		// TODO Auto-generated method stub !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

	}

}
