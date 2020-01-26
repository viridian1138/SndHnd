




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

import gredit.GNode;
import gredit.GWaveForm;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import bezier.GPiecewiseCubicMonotoneBezierFlat;
import bezier.PiecewiseCubicMonotoneBezierFlat;
import core.WaveForm;


/**
 * Node representing a waveform performing phase-distortion on an input waveform to mimic some synthesis techniques using voltage-controlled oscillators with analog input voltages.
 * 
 * The phase shifting input is represented using a piecewise Bezier curve.
 * 
 * See:  https://en.wikipedia.org/wiki/Phase_distortion_synthesis
 * 
 * @author thorngreen
 *
 */
public class GAnalogPhaseDistortionWaveForm extends GWaveForm implements Externalizable {
	
	/**
	 * The input waveform.
	 */
	GWaveForm wave;
	
	/**
	 * The phase distortion to apply to the input waveform.
	 */
	GPiecewiseCubicMonotoneBezierFlat phaseAdjust;
	
	/**
	 * Constructs the node.
	 */
	public GAnalogPhaseDistortionWaveForm()
	{
		super();
	}
	
	/**
	 * Constructs the node.
	 * @param _wave The input waveform.
	 * @param _phaseAdjust The phase distortion to apply to the input waveform.
	 */
	public GAnalogPhaseDistortionWaveForm( GWaveForm _wave , GPiecewiseCubicMonotoneBezierFlat _phaseAdjust )
	{
		wave = _wave;
		phaseAdjust = _phaseAdjust;
	}

	@Override
	public WaveForm genWave(HashMap s) {
		if( s.get(this) != null )
		{
			return( (WaveForm)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		WaveForm w = wave.genWave(s);
		PiecewiseCubicMonotoneBezierFlat pc = phaseAdjust.genBez(s);
		
		AnalogPhaseDistortionWaveForm wv = new AnalogPhaseDistortionWaveForm( w , pc );
		s.put(this, wv);
		
		return( wv );
	}

	public Object getChldNodes() {
		Object[] ob = { wave , phaseAdjust };
		return( ob );
	}

	@Override
	public String getName() {
		return("AnalogPhaseDistortion");
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( ( in instanceof GWaveForm ) || ( in instanceof GPiecewiseCubicMonotoneBezierFlat ) );
	}

	@Override
	public void performAssign(GNode in) {
		if( in instanceof GWaveForm )
		{
			wave = (GWaveForm) in;
			return;
		}
		if( in instanceof GPiecewiseCubicMonotoneBezierFlat )
		{
			phaseAdjust = (GPiecewiseCubicMonotoneBezierFlat) in;
			return;
		}

	}

	@Override
	public void removeChld() {
		wave = null;
		phaseAdjust = null;
	}
	
	/**
	 * Loads new values into the node.
	 * @param w The input waveform.
	 * @param p The phase distortion to apply to the input waveform.
	 */
	public void load( GWaveForm w , GPiecewiseCubicMonotoneBezierFlat p )
	{
		wave = w;
		phaseAdjust = p;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( wave != null ) myv.setProperty("Wave", wave);
		if( phaseAdjust != null ) myv.setProperty("PhaseAdjust", phaseAdjust);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			wave = (GWaveForm)( myv.getProperty("Wave") );
			phaseAdjust = (GPiecewiseCubicMonotoneBezierFlat)( myv.getProperty("PhaseAdjust") );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}

