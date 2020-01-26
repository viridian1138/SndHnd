




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

import meta.DataFormatException;
import meta.VersionBuffer;
import bezier.GPiecewiseCubicMonotoneBezierFlat;
import bezier.PiecewiseCubicMonotoneBezierFlat;
import core.NonClampedCoefficient;
import core.WaveForm;


/**
 * Waveform performing phase-distortion on an input waveform to mimic some synthesis techniques using voltage-controlled oscillators with analog input voltages.
 * 
 * The phase shifting input is represented using a piecewise Bezier curve.
 * 
 * See:  https://en.wikipedia.org/wiki/Phase_distortion_synthesis
 * 
 * @author thorngreen
 *
 */
public class AnalogPhaseDistortionWaveForm extends WaveForm {
	
	/**
	 * The input waveform.
	 */
	WaveForm wave;
	
	/**
	 * The phase distortion to apply to the input waveform.
	 */
	PiecewiseCubicMonotoneBezierFlat phaseAdjust;

	/**
	 * Constructs the waveform.
	 * @param _wave The input waveform.
	 * @param _phaseAdjust The phase distortion to apply to the input waveform.
	 */
	public AnalogPhaseDistortionWaveForm( WaveForm _wave , PiecewiseCubicMonotoneBezierFlat _phaseAdjust ) {
		super();
		wave = _wave;
		phaseAdjust = _phaseAdjust;
	}
	
	/**
	 * Constructor for persistence purposes only.
	 */
	public AnalogPhaseDistortionWaveForm()
	{
	}

	@Override
	public double eval(double non_phase_distorted_param) {
		double phase = phaseAdjust.eval( non_phase_distorted_param );
		return( wave.eval( non_phase_distorted_param + phase ) );
	}
	
	@Override
	public NonClampedCoefficient genClone() throws Throwable
	{
		WaveForm wv = (WaveForm)( wave.genClone() );
		PiecewiseCubicMonotoneBezierFlat pc = phaseAdjust.genCloneWave();
		if( ( wv == wave ) && ( pc == phaseAdjust ) )
		{
			return( this );
		}
		else
		{
			return( new AnalogPhaseDistortionWaveForm( wv , pc ) );
		}
	}
	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GAnalogPhaseDistortionWaveForm wv = new GAnalogPhaseDistortionWaveForm();
		s.put(this, wv);
		
		GWaveForm w = wave.genWave(s);
		GPiecewiseCubicMonotoneBezierFlat p = phaseAdjust.genBez(s);
		
		wv.load(w,p);
		
		return( wv );
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	public void writeExternal(ObjectOutput out) throws IOException {
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setProperty("Wave", wave);
		myv.setProperty("PhaseAdjust", phaseAdjust);

		out.writeObject(myv);
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			wave = (WaveForm)( myv.getPropertyEx("Wave") );
			phaseAdjust = (PiecewiseCubicMonotoneBezierFlat)( myv.getPropertyEx("PhaseAdjust") );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	
}

