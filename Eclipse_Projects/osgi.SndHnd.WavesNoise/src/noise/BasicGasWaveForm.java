





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
 * A waveform for approximating a basic gas cloud as described in the book "Texturing and Modeling" by David S. Ebert et. al.
 * @author thorngreen
 *
 */
public class BasicGasWaveForm extends WaveForm {
	
	/**
	 * Input noise waveform from which to calculate the waveform.
	 */
	private WaveForm noise;
	
	/**
	 * Input parameter 0.
	 */
	double param0;
	
	/**
	 * Input parameter 1.
	 */
	double param1;
	
	/**
	 * Constructs the waveform.
	 * @param _noise Input noise waveform from which to calculate the waveform.
	 * @param _param0  Input parameter 0.
	 * @param _param1  Input parameter 1.
	 */
	public BasicGasWaveForm( WaveForm _noise , double _param0 , 
			double _param1 )
	{
		noise = _noise;
		param0 = _param0;
		param1 = _param1;
	}

	@Override
	public double eval(double p) {
		double turb = noise.eval(p);
		double val = Math.pow( Math.abs( turb * param0 ) , param1 );
		return( turb < 0.0 ? -val : val );
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
			return( new BasicGasWaveForm( wv , param0 , param1 ) );
		}
	}
	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GBasicGasWaveForm wv = new GBasicGasWaveForm();
		s.put(this, wv);
		
		GWaveForm ww = noise.genWave(s);
		
		wv.load(ww,param0,param1);
		
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

