





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
 * Node representing a waveform for distorted fBm (fractional Brownian motion) Noise as described in the book "Texturing and Modeling" by David S. Ebert et. al.
 * Distorted fBm is also known as dist fBm.
 * @author thorngreen
 *
 */
public class GDistFbm extends GWaveForm implements Externalizable {
	
	/**
	 * The H parameter of the function.
	 */
	double H = 0.3;
	
	/**
	 * The lacunarity of the function.
	 */
	double lacunarity = 0.5;
	
	/**
	 * The number of octaves over which to evaluate the function.
	 */
	double octaves = 4;
	
	/**
	 * The amount of distortion to apply to the domain.
	 */
	double distortion = 0.4;

	/**
	 * The noise to be applied in generating the function.  Typically this would be a lattice noise.
	 */
	private GWaveForm chld;
	
	/**
	 * Constructs the node.
	 */
	public GDistFbm()
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
		
		WaveForm wv = distFbm(w, H, lacunarity, octaves, distortion);
		s.put(this, wv);
		
		return( wv );
	}
	
	/**
	 * Constructs the associated waveform.
	 * @param noise  The noise to be applied in generating the function.  Typically this would be a lattice noise.
	 * @param H  The H parameter of the function.
	 * @param lacunarity  The lacunarity of the function.
	 * @param octaves  The number of octaves over which to evaluate the function.
	 * @param distortion  The amount of distortion to apply to the domain.
	 * @return The associated waveform.
	 */
	public static WaveForm distFbm( WaveForm noise , double H , double lacunarity , double octaves , double distortion )
	{
		WaveForm f2 = GDistNoise.distNoise( noise , distortion );
		FbmWaveForm fbmw = new FbmWaveForm( f2 , H , lacunarity , octaves );
		return( fbmw );
	}

	
	public Object getChldNodes() {
		return( chld );
	}

	@Override
	public String getName() {
		return( "Noise -- Dist Fbm" );
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
		DistFbmEditor editor = new DistFbmEditor( this , track );
		ProgramDirector.showPropertyEditor(editor, null,
			"Dist Fbm Properties");
	}

	
	/**
	 * Gets the H parameter of the function.
	 * @return The H parameter of the function.
	 */
	public double getH() {
		return H;
	}

	/**
	 * Sets the H parameter of the function.
	 * @param h The H parameter of the function.
	 */
	public void setH(double h) {
		H = h;
	}

	/**
	 * Gets the lacunarity of the function.
	 * @return The lacunarity of the function.
	 */
	public double getLacunarity() {
		return lacunarity;
	}

	/**
	 * Sets the lacunarity of the function.
	 * @param lacunarity The lacunarity of the function.
	 */
	public void setLacunarity(double lacunarity) {
		this.lacunarity = lacunarity;
	}

	/**
	 * Gets the number of octaves over which to evaluate the function.
	 * @return The number of octaves over which to evaluate the function.
	 */
	public double getOctaves() {
		return octaves;
	}

	/**
	 * Sets the number of octaves over which to evaluate the function.
	 * @param octaves The number of octaves over which to evaluate the function.
	 */
	public void setOctaves(double octaves) {
		this.octaves = octaves;
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
		myv.setDouble("H", H);
		myv.setDouble("Lacunarity", lacunarity);
		myv.setDouble("Octaves", octaves);
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
			H = myv.getDouble("H");
			lacunarity = myv.getDouble("Lacunarity");
			octaves = myv.getDouble("Octaves");
			distortion = myv.getDouble("Distortion");

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	
}

