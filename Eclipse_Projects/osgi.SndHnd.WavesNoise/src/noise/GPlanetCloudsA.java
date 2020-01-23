





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
 * Node representing a waveform approximating planet clouds as described in the book "Texturing and Modeling" by David S. Ebert et. al.
 * 
 * @author thorngreen
 *
 */
public class GPlanetCloudsA extends GWaveForm implements Externalizable {

	/**
	 * The distortion scale for the planet clouds.
	 */
	double distortionscale = 0.4;
	
	/**
	 * The H parameter of the planet clouds.
	 */
	double H = 0.3;
	
	/**
	 * The lacunarity parameter of the planet clouds.
	 */
	double lacunarity = 0.5;
	
	/**
	 * The number of octaves over which to evaluate the planet clouds.
	 */
	double octaves = 4;

	/**
	 * The input noise function from which planet clouds are generated.
	 */
	private GWaveForm chld;
	
	/**
	 * Constructs the node.
	 */
	public GPlanetCloudsA() {
	}

	@Override
	public WaveForm genWave(HashMap s) {
		if (s.get(this) != null) {
			return ((WaveForm) (s.get(this)));
		}

		s.put(this, new Integer(5));

		WaveForm w = chld.genWave(s);

		WaveForm wv = planetCloudsA(w, distortionscale, H, lacunarity, octaves);
		s.put(this, wv);

		return (wv);
	}

	/**
	 * Returns a waveform for planet clouds.
	 * @param noise The input noise function from which planet clouds are generated.
	 * @param distortionscale The distortion scale for the planet clouds.
	 * @param H The H parameter of the planet clouds.
	 * @param lacunarity The lacunarity parameter of the planet clouds.
	 * @param octaves The number of octaves over which to evaluate the planet clouds.
	 * @return The waveform for planet clouds.
	 */
	public static WaveForm planetCloudsA(WaveForm noise,
			double distortionscale, double H, double lacunarity,
			double octaves) {
		PhaseDistortionPacket distort = new PhaseDistortionPacket(noise, 1.0, distortionscale);
		FbmWaveForm fbmw = new FbmWaveForm(noise, H, lacunarity, octaves);
		PhaseDistortionWaveForm ds = new PhaseDistortionWaveForm(distort, fbmw);
		/*
		 * result = Math.max(result,0.0); result = Math.min(result,1.0);
		 */
		return (ds);
	}

	public Object getChldNodes() {
		return (chld);
	}

	@Override
	public String getName() {
		return ("Noise -- PlanetCloudsA");
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return (in instanceof GWaveForm);
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
		PlanetCloudsAEditor editor = new PlanetCloudsAEditor( this , track );
		ProgramDirector.showPropertyEditor(editor, null,
			"Planet Clouds A Properties");
	}

	/**
	 * Gets the distortion scale for the planet clouds.
	 * @return The distortion scale for the planet clouds.
	 */
	public double getDistortionscale() {
		return distortionscale;
	}

	/**
	 * Sets the distortion scale for the planet clouds.
	 * @param distortionscale The distortion scale for the planet clouds.
	 */
	public void setDistortionscale(double distortionscale) {
		this.distortionscale = distortionscale;
	}

	/**
	 * Gets the H parameter of the planet clouds.
	 * @return The H parameter of the planet clouds.
	 */
	public double getH() {
		return H;
	}

	/**
	 * Sets the H parameter of the planet clouds.
	 * @param h The H parameter of the planet clouds.
	 */
	public void setH(double h) {
		H = h;
	}

	/**
	 * Gets the lacunarity parameter of the planet clouds.
	 * @return The lacunarity parameter of the planet clouds.
	 */
	public double getLacunarity() {
		return lacunarity;
	}

	/**
	 * Sets the lacunarity parameter of the planet clouds.
	 * @param lacunarity The lacunarity parameter of the planet clouds.
	 */
	public void setLacunarity(double lacunarity) {
		this.lacunarity = lacunarity;
	}

	/**
	 * Gets the number of octaves over which to evaluate the planet clouds.
	 * @return The number of octaves over which to evaluate the planet clouds.
	 */
	public double getOctaves() {
		return octaves;
	}

	/**
	 * Sets the number of octaves over which to evaluate the planet clouds.
	 * @param octaves The number of octaves over which to evaluate the planet clouds.
	 */
	public void setOctaves(double octaves) {
		this.octaves = octaves;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( chld != null ) myv.setProperty("Chld", chld);
		myv.setDouble("DistortionScale", distortionscale);
		myv.setDouble("H", H);
		myv.setDouble("Lacunarity", lacunarity);
		myv.setDouble("Octaves", octaves);

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
			distortionscale = myv.getDouble("DistortionScale");
			H = myv.getDouble("H");
			lacunarity = myv.getDouble("Lacunarity");
			octaves = myv.getDouble("Octaves");

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}
