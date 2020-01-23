





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
 * A waveform for fBm (fractional Brownian motion) Noise as described in the book "Texturing and Modeling" by David S. Ebert et. al.
 * @author thorngreen
 *
 */
public class FbmWaveForm extends WaveForm {
	
	/**
	 * The input noise function from which the fBm is generated.  Typically this is a lattice noise.
	 */
	WaveForm noise;
	
	/**
	 * The H parameter of the function.
	 */
	double H;
	
	/**
	 * The lacunarity of the function.
	 */
	double lacunarity;
	
	/**
	 * The number of octaves over which to evaluate the function.
	 */
	double octaves;
	
	/**
	 * Internal gain parameter used in calculating the wave.
	 */
	double q0;
	
	/**
	 * Constructs the waveform.
	 * @param _noise  The input noise function from which the fBm is generated.  Typically this is a lattice noise.
	 * @param _H  The H parameter of the function.
	 * @param _lacunarity  The lacunarity of the function.
	 * @param _octaves  The number of octaves over which to evaluate the function.
	 */
	public FbmWaveForm( WaveForm _noise , double _H , double _lacunarity , double _octaves )
	{
		noise = _noise;
		H = _H;
		lacunarity = _lacunarity;
		octaves = _octaves;
		
		q0 = Math.pow(lacunarity,-H);
	}

	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GFbmWaveForm wv = new GFbmWaveForm();
		s.put(this, wv);
		
		GWaveForm w = noise.genWave(s);
		
		wv.load(w,H, lacunarity,octaves);
		
		return( wv );
	}

	@Override
	public double eval(double p) {
		double value, remainder;
		int i;
		
		value = 0.0;
		final double pval = q0;
		double qval = 1.0;
		
		for( i = 0 ; i < octaves ; i++ )
		{
			value += noise.eval( p ) * qval;
			p *= lacunarity;
			qval *= pval;
		}
		
		remainder = octaves - (int) octaves;
		if( remainder > 1E-9 )
		{
			value += remainder * noise.eval( p ) * qval;
		}
		return( value );
	}

	@Override
	public NonClampedCoefficient genClone() throws Throwable {
		final WaveForm wv = (WaveForm)( noise.genClone() );
		if( wv == noise )
		{
			return( this );
		}
		else
		{
			return( new FbmWaveForm( wv , H , lacunarity , octaves ) );
		}
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

