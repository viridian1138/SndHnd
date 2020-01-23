





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
 * Waveform approximating a multifractal as described in the book "Texturing and Modeling" by David S. Ebert et. al.
 * @author thorngreen
 *
 */
public class MultifractalWaveForm extends WaveForm {
	
	/**
	 * The noise to be applied in generating the multifractal.  Typically this would be a lattice noise.
	 */
	private WaveForm noise;
	
	/**
	 * The H parameter of the multifractal.
	 */
	double H; 
	
	/**
	 * The lacunarity of the multifractal.
	 */
	double lacunarity; 
	
	/**
	 * The number of octaves over which to evaluate the multifractal.
	 */
	int octaves;
	
	/**
	 * The offset parameter for the multifractal.
	 */
	double offset;
	
	/**
	 * Internal gain parameter used in calculating the wave.
	 */
	double q0;
	
	
	/**
	 * Constructs the waveform.
	 * @param _noise The noise to be applied in generating the multifractal.  Typically this would be a lattice noise.
	 * @param _H The H parameter of the multifractal.
	 * @param _lacunarity The lacunarity of the multifractal.
	 * @param _octaves The number of octaves over which to evaluate the multifractal.
	 * @param _offset The offset parameter for the multifractal.
	 */
	public MultifractalWaveForm( WaveForm _noise , double _H , 
			double _lacunarity , int _octaves , double _offset )
	{
		noise = _noise;
		H = _H;
		lacunarity = _lacunarity;
		octaves = _octaves;
		offset = _offset;
		
		q0 = Math.pow( lacunarity , -H );
	}

	@Override
	public double eval(double p) {
		double value = 1.0;
		final double pval = q0;
		double qval = 1.0;
		
		int i;
		
		for( i = 0 ; i < octaves ; i++ )
		{
			value *= ( noise.eval( p ) + offset ) * qval;
			p *= lacunarity;
			qval *= pval;
		}
		
		return( value );
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
			return( new MultifractalWaveForm( wv , H , lacunarity , octaves, offset ) );
		}
	}
	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GMultifractal wv = new GMultifractal();
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

