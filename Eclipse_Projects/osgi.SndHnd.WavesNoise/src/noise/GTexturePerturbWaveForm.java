





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
import cwaves.PhaseDistortionWaveForm;

/**
 * Node representing a waveform approximating a perturbed texture as described in the book "Texturing and Modeling" by David S. Ebert et. al.
 * @author thorngreen
 *
 */
public class GTexturePerturbWaveForm extends GWaveForm implements Externalizable {

	/**
	 * Waveform for the texture to be perturbed.
	 */
	private GWaveForm texture;
	
	/**
	 * The noise to be applied to the texture.  Typically this would be a lattice noise.
	 */
	private GWaveForm noise;
	
	/**
	 * The amplitude at which to apply the noise.
	 */
	private double noiseAmpl = 0.2;
	
	/**
	 * The frequency at which to apply the noise.
	 */
	private double noiseFreq = 1.0;
	
	
	/**
	 * Constructs the node.
	 */
	public GTexturePerturbWaveForm()
	{
	}

	
	@Override
	public WaveForm genWave(HashMap s) {
		if( s.get(this) != null )
		{
			return( (WaveForm)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		WaveForm wA = texture.genWave(s);
		WaveForm wB = noise.genWave(s);
		
		WaveForm wv = gen( wA , wB , noiseAmpl , noiseFreq );
		s.put(this, wv);
		
		return( wv );
	}
	
	/**
	 * Generates the waveform for the perturbed texture.
	 * @param texture  Waveform for the texture to be perturbed.
	 * @param noise  The noise to be applied to the texture.  Typically this would be a lattice noise.
	 * @param noiseAmpl  The amplitude at which to apply the noise.
	 * @param noiseFreq  The frequency at which to apply the noise.
	 * @return The waveform for the perturbed texture.
	 */
	public static WaveForm gen( WaveForm texture , WaveForm noise , double noiseAmpl , double noiseFreq )
	{
		PhaseDistortionPacket pk = new PhaseDistortionPacket( noise , noiseFreq , noiseAmpl );
		PhaseDistortionWaveForm pd = new PhaseDistortionWaveForm(pk, texture);
		return( pd );
	}

	public Object getChldNodes() {
		Object[] ob = { texture , noise };
		return( ob );
	}

	@Override
	public String getName() {
		return( "Noise -- Texture Perturb" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GWaveForm );
	}

	@Override
	public void performAssign(GNode in) {
		GWaveForm c = (GWaveForm) in;
		if( texture == null )
		{
			texture = c;
			return;
		}
		noise = c;

	}

	@Override
	public void removeChld() {
		texture = null;
		noise = null;
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		InstrumentTrack track = (InstrumentTrack)( context.get( "InstrumentTrack" ) );
		TexturePerturbWaveFormEditor editor = new TexturePerturbWaveFormEditor( this , track );
		ProgramDirector.showPropertyEditor(editor, null,
			"Texture Perturb Wave Form Properties");
	}

	/**
	 * Gets the amplitude at which to apply the noise.
	 * @return the noiseAmpl  The amplitude at which to apply the noise.
	 */
	public double getNoiseAmpl() {
		return noiseAmpl;
	}

	/**
	 * Sets the amplitude at which to apply the noise.
	 * @param noiseAmpl  The amplitude at which to apply the noise.
	 */
	public void setNoiseAmpl(double noiseAmpl) {
		this.noiseAmpl = noiseAmpl;
	}

	/**
	 * Gets the frequency at which to apply the noise.
	 * @return the noiseFreq  The frequency at which to apply the noise.
	 */
	public double getNoiseFreq() {
		return noiseFreq;
	}

	/**
	 * Sets the frequency at which to apply the noise.
	 * @param noiseFreq The frequency at which to apply the noise.
	 */
	public void setNoiseFreq(double noiseFreq) {
		this.noiseFreq = noiseFreq;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( texture != null ) myv.setProperty("Texture", texture);
		if( noise != null ) myv.setProperty("Noise", noise);
		myv.setDouble("NoiseAmpl", noiseAmpl);
		myv.setDouble("NoiseFreq", noiseFreq);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			texture = (GWaveForm)( myv.getProperty("Texture") );
			noise = (GWaveForm)( myv.getProperty("Noise") );
			noiseAmpl = myv.getDouble("NoiseAmpl");
			noiseFreq = myv.getDouble("NoiseFreq");

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	
}

