





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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import core.ClampedCoefficient;
import core.NonClampedCoefficient;
import core.WaveForm;
import cwaves.GAdditivePacket;

/**
 * Waveform for Granular Synthesis similar to work by Xenakis.  The class returns the composite of
 * grains from two input waveforms defined by a set of compositing parameters.
 * 
 * See <A href="https://en.wikipedia.org/wiki/Granular_synthesis">https://en.wikipedia.org/wiki/Granular_synthesis</A>
 * 
 * @author thorngreen
 */
public class GranularSynth extends WaveForm implements Externalizable {
	
	/**
	 * First waveform from which to select grains.
	 */
	WaveForm waveA;
	
	/**
	 * Second waveform from which to select grains.
	 */
	WaveForm waveB;
	
	/**
	 * Coefficient for selecting grains from one waveform versus the other.
	 */
	NonClampedCoefficient coeff;
	
	/**
	 * Multiplier coefficient for selecting grains from one waveform versus the other.
	 */
	NonClampedCoefficient coeffCoeff;
	
	/**
	 * Parameter coefficient for selecting grains from one waveform versus the other.
	 */
	NonClampedCoefficient paramCoeff;

	/**
	 * Constructs the WaveForm.
	 * @param _waveA First waveform from which to select grains.
	 * @param _waveB Second waveform from which to select grains.
	 * @param _coeff Coefficient for selecting grains from one waveform versus the other.
	 * @param _coeffCoeff Multiplier coefficient for selecting grains from one waveform versus the other.
	 * @param _paramCoeff Parameter coefficient for selecting grains from one waveform versus the other.
	 */
	public GranularSynth(WaveForm _waveA , WaveForm _waveB , NonClampedCoefficient _coeff ,
			NonClampedCoefficient _coeffCoeff , NonClampedCoefficient _paramCoeff ) {
		super();
		waveA = _waveA;
		waveB = _waveB;
		coeff = _coeff;
		coeffCoeff = _coeffCoeff;
		paramCoeff = _paramCoeff;
	}

	@Override
	public double eval(double non_phase_distorted_param) {
		final double wA = waveA.eval(non_phase_distorted_param);
		final double wB = waveB.eval(non_phase_distorted_param);
		final double u = 
				coeff.eval( non_phase_distorted_param * paramCoeff.eval( non_phase_distorted_param ) ) 
				* coeffCoeff.eval( non_phase_distorted_param );
		return( (1-u) * wA + u * wB );
	}
	
	@Override
	public NonClampedCoefficient genClone() throws Throwable
	{
		final WaveForm wvA = (WaveForm)( waveA.genClone() );
		final WaveForm wvB = (WaveForm)( waveB.genClone() );
		final NonClampedCoefficient cf = (NonClampedCoefficient)( coeff.genClone() );
		final NonClampedCoefficient cfcf = (NonClampedCoefficient)( coeffCoeff.genClone() );
		final NonClampedCoefficient pcf = (NonClampedCoefficient)( paramCoeff.genClone() );
		if( ( wvA == waveA ) && ( wvB == waveB ) && ( cf == coeff ) && ( cfcf == coeffCoeff ) && ( pcf == paramCoeff ) )
		{
			return( this );
		}
		else
		{
			return( new GranularSynth( wvA , wvB , cf , cfcf , pcf ) );
		}
	}
	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GGranularSynth wv = new GGranularSynth();
		s.put(this, wv);
		
		GWaveForm wA = waveA.genWave(s);
		GWaveForm wB = waveB.genWave(s);
		
		GAdditivePacket packet = new GAdditivePacket();
		packet.load( coeff.genCoeff(s) , coeffCoeff.genCoeff(s) , paramCoeff.genCoeff(s) );
		
		wv.load(wA,wB,packet);
		
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

