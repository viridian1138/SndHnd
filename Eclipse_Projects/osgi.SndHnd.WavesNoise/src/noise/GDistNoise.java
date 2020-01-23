





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

import gredit.GNode;
import gredit.GWaveForm;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import verdantium.ProgramDirector;
import core.InstrumentTrack;
import core.PhaseDistortionPacket;
import core.WaveForm;
import cwaves.ConstantNonClampedCoefficient;
import cwaves.OffsetWaveform;
import cwaves.PhaseDistortionWaveForm;

/**
 * A node representing a waveform for distorted noise as described in the book "Texturing and Modeling" by David S. Ebert et. al.
 * @author thorngreen
 *
 */
public class GDistNoise extends GWaveForm implements Externalizable {
	
	/**
	 * The amount of distortion to apply to the domain.
	 */
	double distortion;

	/**
	 * The input noise function from which the distorted noise is generated.
	 */
	private GWaveForm chld;
	
	/**
	 * Constructs the node.
	 */
	public GDistNoise()
	{
	}

	@Override
	public WaveForm genWave(HashMap s) {
		if( s.get(this) != null )
		{
			return( (WaveForm)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		WaveForm w = chld.genWave(s);
		
		WaveForm wv = distNoise(w, distortion);
		s.put(this, wv);
		
		return( wv );
	}
	
	/**
	 * Constructs the associated waveform.
	 * @param in  The input noise function from which the distorted noise is generated.
	 * @param distortion  The amount of distortion to apply to the domain.
	 * @return The associated waveform.
	 */
	public static WaveForm distNoise( WaveForm in , double distortion )
	{
		OffsetWaveform off = new OffsetWaveform( in , new ConstantNonClampedCoefficient( 3.33 ) );
		PhaseDistortionPacket pack = new PhaseDistortionPacket( off , 1.0 , distortion );
		PhaseDistortionPacket[] pk = { pack };
		 PhaseDistortionWaveForm pd = new  PhaseDistortionWaveForm( pk , in );
		 return( pd );
	}

	public Object getChldNodes() {
		return( chld );
	}

	@Override
	public String getName() {
		return( "Noise -- Dist Noise" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GWaveForm );
	}

	@Override
	public void performAssign(GNode in) {
		chld = (GWaveForm) in;

	}

	@Override
	public void removeChld() {
		chld = null;
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		InstrumentTrack track = (InstrumentTrack)( context.get( "InstrumentTrack" ) );
		DistNoiseEditor editor = new DistNoiseEditor( this , track );
		ProgramDirector.showPropertyEditor(editor, null,
			"Dist Noise Properties");
	}

	/**
	 * Gets the amount of distortion to apply to the domain.
	 * @return The amount of distortion to apply to the domain.
	 */
	public double getDistortion() {
		return distortion;
	}

	/**
	 * Sets the amount of distortion to apply to the domain.
	 * @param distortion The amount of distortion to apply to the domain.
	 */
	public void setDistortion(double distortion) {
		this.distortion = distortion;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( chld != null ) myv.setProperty("Chld", chld);
		myv.setDouble("Distortion", distortion);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			chld = (GWaveForm)( myv.getProperty("Chld") );
			distortion = myv.getDouble("Distortion");

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	
}

