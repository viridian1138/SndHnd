




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








package core;

import gredit.GNonClampedCoefficient;
import gredit.GPhaseDistortionPacket;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;

/**
 * A packet describing one phase distortion to be applied to a wave.
 * 
 * Class PhaseDistortionPacket interacts with class gredit.GPhaseDistortionPacket.  Class gedit.GPhaseDistortionPacket is single-threaded, mutable, and
 * editable, whereas PhaseDistortionPacket is immutable and non-editable.
 * 
 * @author tgreen
 *
 */
public class PhaseDistortionPacket implements Externalizable {

	/**
	 * The waveform distorting the phase in parameter-space.
	 */
	protected NonClampedCoefficient waveform;
	
	/**
	 * The frequency multiplier applied to the distorting wave.
	 */
	protected double frequencyMultiplier;
	
	/**
	 * The amplitude of the phase distortion.
	 */
	protected double amplitude;
	
	
	/**
	 * Constructor.
	 * @param _waveform The waveform distorting the phase in parameter-space.
	 * @param _freqMult The frequency multiplier applied to the distorting wave.
	 * @param _ampl The amplitude of the phase distortion.
	 */
	public PhaseDistortionPacket( NonClampedCoefficient _waveform , double _freqMult , double _ampl )
	{
		waveform = _waveform;
		frequencyMultiplier = _freqMult;
		amplitude = _ampl;
	}
	
	/**
	 * Constructor to be used for persistence only.
	 *
	 */
	public PhaseDistortionPacket()
	{	
	}
	
	/**
	 * Produces an instance of the PhaseDistortionPacket that can be safely used on another thread.  
	 * Making the PhaseDistortionPacket immutable says some things about its
	 * thread-safety, but doesn't actually make it thread-safe.  Some internal state information can 
	 * change even if the object cannot be mutated through an interface.
	 * @return An instance of the PhaseDistortionPacket that can be used on another thread.
	 * @throws Throwable
	 */
	public PhaseDistortionPacket genClone() throws Throwable
	{
		final WaveForm wv = (WaveForm)( waveform.genClone() );
		if( wv == waveform )
		{
			return( this );
		}
		else
		{
			return( new PhaseDistortionPacket( wv , frequencyMultiplier , amplitude ) );
		}
	}
	
	/**
	 * Generates the corresponding GPhaseDistortionPacket node for this phase distortion packet.
	 * @param s Map for duplicate elimination among nodes.
	 * @return The corresponding GPhaseDistortionPacket node for this PhaseDistortionPacket.
	 */
	public GPhaseDistortionPacket genPhase( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GPhaseDistortionPacket)( s.get( this ) ) );
		}
		
		GPhaseDistortionPacket pk = new GPhaseDistortionPacket();
		
		GNonClampedCoefficient wv = waveform.genCoeff(s);
		
		pk.load(wv,frequencyMultiplier,amplitude);
		
		return( pk );
	}
	
	/**
	 * Gets the amplitude of the phase distortion.
	 * @return The amplitude of the phase distortion.
	 */
	public double getAmplitude() {
		return amplitude;
	}
	
	/**
	 * Gets the frequency multiplier applied to the distorting wave.
	 * @return The frequency multiplier applied to the distorting wave.
	 */
	public double getFrequencyMultiplier() {
		return frequencyMultiplier;
	}
	
	/**
	 * Gets the waveform distorting the phase in parameter-space.
	 * @return The waveform distorting the phase in parameter-space.
	 */
	public NonClampedCoefficient getWaveform() {
		return waveform;
	}
	
	/**
	* Reads the node from serial storage.
	*/
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			waveform = (NonClampedCoefficient)( myv.getPropertyEx("WaveForm") );
			frequencyMultiplier = myv.getDouble("FrequencyMultiplier");
			amplitude = myv.getDouble("Amplitude");
		}
		catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	/**
	* Writes the node to serial storage.
	* @serialData TBD.
	*/
	public void writeExternal(ObjectOutput out) throws IOException {
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setProperty("WaveForm", waveform);
		myv.setDouble("FrequencyMultiplier", frequencyMultiplier);
		myv.setDouble("Amplitude", amplitude);

		out.writeObject(myv);
	}
	
}

