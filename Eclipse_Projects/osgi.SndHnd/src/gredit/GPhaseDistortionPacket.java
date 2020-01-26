




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







package gredit;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import verdantium.ProgramDirector;
import core.InstrumentTrack;
import core.NonClampedCoefficient;
import core.PhaseDistortionPacket;


/**
 * A node representing a packet describing one phase distortion to be applied to a wave.
 * 
 * Class GPhaseDistortionPacket interacts with class core.PhaseDistortionPacket.  GPhaseDistortionPacket is single-threaded, mutable, and
 * editable, whereas core.PhaseDistortionPacket is immutable and non-editable.
 * 
 * @author tgreen
 *
 */
public class GPhaseDistortionPacket extends GNode implements Externalizable {

	/**
	 * The waveform distorting the phase in parameter-space.
	 */
	protected GNonClampedCoefficient waveform;
	
	/**
	 * The frequency multiplier applied to the distorting wave.
	 */
	protected double frequencyMultiplier = 2.0;
	
	/**
	 * The amplitude of the phase distortion.
	 */
	protected double amplitude = 1.0;
	
	
	/**
	 * Constructs the node.
	 */
	public GPhaseDistortionPacket() {
	}
	
	/**
	 * Loads new values into the node.
	 * @param wv The waveform distorting the phase in parameter-space.
	 * @param fr The frequency multiplier applied to the distorting wave.
	 * @param ampl The amplitude of the phase distortion.
	 */
	public void load( GNonClampedCoefficient wv , double fr , double ampl )
	{
		waveform = wv;
		frequencyMultiplier = fr;
		amplitude = ampl;
	}

	/**
	 * Generates an instance of the phase distortion represented by this node.
	 * @param s HashMap used to eliminate duplicates.
	 * @return An instance of the phase distortion represented by this node.
	 */
	public PhaseDistortionPacket genPhase(HashMap s) {
		if (s.get(this) != null) {
			return ((PhaseDistortionPacket) (s.get(this)));
		}

		s.put(this, new Integer(5));

		NonClampedCoefficient wv = waveform.genCoeff(s);
		PhaseDistortionPacket pk = new PhaseDistortionPacket(wv,
				frequencyMultiplier, amplitude);
		s.put(this, pk);
		return (pk);
	}

	@Override
	public final Object genObj(HashMap s) {
		return (genPhase(s));
	}

	@Override
	public void performAssign(GNode in) {
		waveform = (GNonClampedCoefficient) in;
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return (in instanceof GNonClampedCoefficient);
	}

	public Object getChldNodes() {
		return (waveform);
	}

	@Override
	public void removeChld() {
		waveform = null;
	}

	@Override
	public String getName() {
		return ("PhaseDistortionPacket");
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		InstrumentTrack track = (InstrumentTrack)( context.get( "InstrumentTrack" ) );
		PhaseDistortionPacketEditor editor = new PhaseDistortionPacketEditor( this , track );
		ProgramDirector.showPropertyEditor(editor, null,
			"Phase Distortion Packet Properties");
	}

	/**
	 * Gets the frequency multiplier applied to the distorting wave.
	 * @return  The frequency multiplier applied to the distorting wave.
	 */
	public double getFrequencyMultiplier() {
		return frequencyMultiplier;
	}

	/**
	 * Sets the frequency multiplier applied to the distorting wave.
	 * @param frequencyMultiplier The frequency multiplier applied to the distorting wave.
	 */
	public void setFrequencyMultiplier(double frequencyMultiplier) {
		this.frequencyMultiplier = frequencyMultiplier;
	}

	/**
	 * Gets the amplitude of the phase distortion.
	 * @return The amplitude of the phase distortion.
	 */
	public double getAmplitude() {
		return amplitude;
	}

	/**
	 * Sets the amplitude of the phase distortion packet.
	 * @param amplitude The amplitude of the phase distortion packet.
	 */
	public void setAmplitude(double amplitude) {
		this.amplitude = amplitude;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( waveform != null ) myv.setProperty("Waveform", waveform);
		myv.setDouble("FrequencyMultiplier", frequencyMultiplier);
		myv.setDouble("Amplitude", amplitude);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			waveform = (GNonClampedCoefficient)( myv.getProperty("Waveform") );
			frequencyMultiplier = myv.getDouble( "FrequencyMultiplier" );
			amplitude = myv.getDouble( "Amplitude" );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	
}

