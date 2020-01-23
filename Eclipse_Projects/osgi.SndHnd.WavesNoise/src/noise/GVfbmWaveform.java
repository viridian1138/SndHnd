





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
import core.WaveForm;

/**
 * Node representing a waveform for Vector-Valued fBm (fractional Brownian motion) Noise as described in the book "Texturing and Modeling" by David S. Ebert et. al.
 * Vector-Valued fBm is also known as vfBm.
 * @author thorngreen
 *
 */
public class GVfbmWaveform extends GWaveForm implements Externalizable {
	
	/**
	 * The input noise function from which the vfBm is generated.  Typically this is a lattice noise.
	 */
	GWaveForm chld;
	
	/**
	 * The maximum number of octaves over which to compute the vfBm.
	 */
	double maxoctaves = 4;
	
	/**
	 * The lacunarity parameter of the vfBm.
	 */
	double lacunarity = 0.5;
	
	/**
	 * The gain parameter of the vfBm.
	 */
	double gain = 0.3;
	
	/**
	 * Constructs the node.
	 */
	public GVfbmWaveform()
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
		
		WaveForm wv = new VfbmWaveform(w, maxoctaves, lacunarity, gain);
		s.put(this, wv);
		
		return( wv );
	}
	
	/**
	 * Loads values into the node.
	 * @param _chld  The input noise function from which the vfBm is generated.  Typically this is a lattice noise.
	 * @param _maxoctaves  The maximum number of octaves over which to compute the vfBm.
	 * @param _lacunarity  The lacunarity parameter of the vfBm.
	 * @param _gain  The gain parameter of the vfBm.
	 */
	public void load( GWaveForm _chld , double _maxoctaves , double _lacunarity , double _gain )
	{
		chld = _chld;
		maxoctaves = _maxoctaves;
		lacunarity = _lacunarity;
		gain = _gain;
	}

	public Object getChldNodes() {
		return( chld );
	}

	@Override
	public String getName() {
		return( "Noise -- Vfbm WaveForm" );
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
		VfbmWaveformEditor editor = new VfbmWaveformEditor( this , track );
		ProgramDirector.showPropertyEditor(editor, null,
			"Vfbm Waveform Properties");
	}

	/**
	 * Gets the maximum number of octaves over which to compute the vfBm.
	 * @return The maximum number of octaves over which to compute the vfBm.
	 */
	public double getMaxoctaves() {
		return maxoctaves;
	}

	/**
	 * Sets the maximum number of octaves over which to compute the vfBm.
	 * @param maxoctaves The maximum number of octaves over which to compute the vfBm.
	 */
	public void setMaxoctaves(double maxoctaves) {
		this.maxoctaves = maxoctaves;
	}

	/**
	 * Gets the lacunarity parameter of the vfBm.
	 * @return The lacunarity parameter of the vfBm.
	 */
	public double getLacunarity() {
		return lacunarity;
	}

	/**
	 * Sets the lacunarity parameter of the vfBm.
	 * @param lacunarity The lacunarity parameter of the vfBm.
	 */
	public void setLacunarity(double lacunarity) {
		this.lacunarity = lacunarity;
	}

	/**
	 * Gets the gain parameter of the vfBm.
	 * @return The gain parameter of the vfBm.
	 */
	public double getGain() {
		return gain;
	}

	/**
	 * Sets the gain parameter of the vfBm.
	 * @param gain The gain parameter of the vfBm.
	 */
	public void setGain(double gain) {
		this.gain = gain;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( chld != null ) myv.setProperty("Chld", chld);
		myv.setDouble("Maxoctaves", maxoctaves);
		myv.setDouble("Lacunarity", lacunarity);
		myv.setDouble("Gain", gain);

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
			maxoctaves = myv.getDouble("Maxoctaves");
			lacunarity = myv.getDouble("Lacunarity");
			gain = myv.getDouble("Gain");

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	
}

