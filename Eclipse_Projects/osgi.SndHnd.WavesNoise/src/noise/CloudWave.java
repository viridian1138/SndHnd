





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

import java.util.ArrayList;

import waves.ZeroWaveform;
import core.NonClampedCoefficient;
import core.PhaseDistortionPacket;
import core.WaveForm;
import cwaves.AdditiveWaveForm;
import cwaves.AmplitudeModulationWaveForm;
import cwaves.ConstantNonClampedCoefficient;
import cwaves.CosineWaveform;
import cwaves.FrequencyModulationWaveForm;
import cwaves.OffsetWaveform;
import cwaves.PhaseDistortionWaveForm;

/**
 * Class for generating a waveform approximating cloud formations as described in the book "Texturing and Modeling" by David S. Ebert et. al.
 * @author thorngreen
 *
 */
public class CloudWave {
	
	
	/**
	 * Generates a waveform approximating cloud formations.
	 * @return A waveform approximating cloud formations.
	 */
	public static WaveForm gen() {
		double i;
		NonClampedCoefficient xphase = new ConstantNonClampedCoefficient( 0.9 );
		double xfreq = 0.023;
		double amplitude = 0.3;
		final NonClampedCoefficient offset = new ConstantNonClampedCoefficient( 0.5 );
		final int nterms = 5;
		ArrayList<NonClampedCoefficient> coefficients = new ArrayList<NonClampedCoefficient>();
		ArrayList<NonClampedCoefficient> coefficientCoefficients = new ArrayList<NonClampedCoefficient>();
		ArrayList<NonClampedCoefficient> parameterCoefficients = new ArrayList<NonClampedCoefficient>();
		for( i= 0 ; i < nterms ; i++ )
		{
			FrequencyModulationWaveForm fmb = new FrequencyModulationWaveForm( new CosineWaveform() , 
					new ConstantNonClampedCoefficient( xfreq ) );
			PhaseDistortionPacket pk = new PhaseDistortionPacket( xphase , 1.0 / xfreq , 0.0 );
			PhaseDistortionPacket[] pks = { pk };
			PhaseDistortionWaveForm phc = new PhaseDistortionWaveForm( pks , fmb );
			WaveForm fa = new OffsetWaveform( phc , offset );
			coefficients.add( fa );
			coefficientCoefficients.add( new ConstantNonClampedCoefficient( amplitude ) );
			parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ) );
			
			FrequencyModulationWaveForm fma = new FrequencyModulationWaveForm( new CosineWaveform() , 
					new ConstantNonClampedCoefficient( xfreq ) );
			xphase = new AmplitudeModulationWaveForm( fma , new ConstantNonClampedCoefficient( 0.25 * 0.9 ) );
			
			xfreq = xfreq * 1.9 + i * 0.1;
			amplitude = amplitude * 0.707;
		}
		
		AdditiveWaveForm awave = new AdditiveWaveForm( new ZeroWaveform() , 
				new ConstantNonClampedCoefficient(1.0) , coefficients ,
				coefficientCoefficients , parameterCoefficients );
		return( awave );
		
	}


}

