





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
 * Node representing a waveform approximating a heterogeneous procedural terrain function as described in the book "Texturing and Modeling" by David S. Ebert et. al.
 * @author thorngreen
 *
 */
public class GHeteroTerrainWaveForm extends GWaveForm implements Externalizable {
	
	/**
	 * The noise to be applied in generating the function.  Typically this would be a lattice noise.
	 */
	GWaveForm chld;
	
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
	 * The offset parameter for the function.
	 */
	double offset = 0.1;
	
	/**
	 * Constructs the node.
	 */
	public GHeteroTerrainWaveForm()
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
		
		WaveForm wv = new HeteroTerrainWaveForm(w, H, lacunarity, octaves, offset);
		s.put(this, wv);
		
		return( wv );
	}
	
	/**
	 *  Loads new values into the node.
	 * @param _chld The noise to be applied in generating the function.  Typically this would be a lattice noise.
	 * @param _H The H parameter of the function.
	 * @param _lacunarity The lacunarity of the function.
	 * @param _octaves The number of octaves over which to evaluate the function.
	 * @param _offset The offset parameter for the function.
	 */
	public void load( GWaveForm _chld , double _H , double _lacunarity , double _octaves , double _offset )
	{
		chld = _chld;
		H = _H;
		lacunarity = _lacunarity;
		octaves = _octaves;
		offset = _offset;
	}

	public Object getChldNodes() {
		return( chld );
	}

	@Override
	public String getName() {
		return( "Noise -- HeteroTerrain WaveForm" );
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
		HeteroTerrainWaveFormEditor editor = new HeteroTerrainWaveFormEditor( this , track );
		ProgramDirector.showPropertyEditor(editor, null,
			"Hetero Terrain Properties");
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
	 * Gets the offset parameter for the function.
	 * @return The offset parameter for the function.
	 */
	public double getOffset() {
		return offset;
	}

	/**
	 * Sets the offset parameter for the function.
	 * @param offset The offset parameter for the function.
	 */
	public void setOffset(double offset) {
		this.offset = offset;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( chld != null ) myv.setProperty("Chld", chld);
		myv.setDouble("H", H);
		myv.setDouble("Lacunarity", lacunarity);
		myv.setDouble("Octaves", octaves);
		myv.setDouble("Offset", offset);

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
			offset = myv.getDouble("Offset");

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}
