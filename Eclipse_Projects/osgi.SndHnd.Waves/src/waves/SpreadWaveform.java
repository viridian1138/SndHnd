





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
 * Waveform returning an average of a set of evaluations of
 * an input noise waveform spread across multiple phases.
 * The spread between samples increases exponentially to simulate
 * an exponentially decreasing sampling probability as the phase 
 * difference increases.
 * 
 * @author tgreen
 *
 */
public class SpreadWaveform extends WaveForm {
	
	/**
	 * Multiplicative constant.
	 */
	public static final double DMULT = 1.0;
	
	/**
	 * The input noise waveform.
	 */
	WaveForm noise;
	
	/**
	 * The number of samples to be averaged.
	 */
	int sampSize;
	
	
	double alpha;
	
	/**
	 * Maximum multiplicative factor to go from one sample to the next.
	 */
	double kmax;
	
	/**
	 * The maximum phase spread of the sampling in cycle numbers.
	 */
	double num_op2;
	
	/**
	 * Constructs the waveform.
	 * @param _noise The input noise waveform.
	 * @param _sampSize The number of samples to be averaged.
	 * @param num_cycles_op2 The maximum phase spread of the sampling in cycle numbers.
	 */
	public SpreadWaveform( WaveForm _noise , final int _sampSize , final double num_cycles_op2 )
	{
		noise = _noise;
		sampSize = _sampSize;
		num_op2 = num_cycles_op2;
		kmax = Math.pow( Math.PI,  1.0 / ( DMULT * num_cycles_op2 ) );
		alpha = ( kmax - 1.0 ) / num_cycles_op2;
	}

	@Override
	public double eval( final double p ) {
		
		double dr = Math.min( 1.0 + alpha * p , kmax );
		
		WaveForm noise = this.noise;
		double tot = 0.0;
		double current = p;
		double cur_dr = dr;
		int count;
		final int samp = sampSize;
		for( count = 0 ; count < samp ; count++  )
		{
			tot += noise.eval( current - (int) current );
			
			current = current + ( cur_dr - 1.0 );
			cur_dr = cur_dr * dr;
		}
		
		return( tot / sampSize );
		// return( 10.0 * tot - (int)( 10.0 * tot ) );
	}
	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GSpreadWaveform wv = new GSpreadWaveform();
		s.put(this, wv);
		
		GWaveForm w = noise.genWave(s);
		
		wv.load(w,sampSize,num_op2);
		
		return( wv );
	}

	@Override
	public NonClampedCoefficient genClone() throws Throwable {
		WaveForm cn = (WaveForm)( noise.genClone() );
		if( noise == cn )
		{
			return( this );
		}
		return( new SpreadWaveform( cn , sampSize , num_op2 ) );
	}

	
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		// TODO Auto-generated method stub

	}

	
	public void writeExternal(ObjectOutput out) throws IOException {
		// TODO Auto-generated method stub

	}

	
}

