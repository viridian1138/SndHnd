





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
 * Node representing a waveform approximating a multifractal as described in the book "Texturing and Modeling" by David S. Ebert et. al.
 * @author thorngreen
 *
 */
public class GMultifractal extends GWaveForm implements Externalizable {

	/**
	 * The noise to be applied in generating the multifractal.  Typically this would be a lattice noise.
	 */
	private GWaveForm chld;
	
	/**
	 * The H parameter of the multifractal.
	 */
	double H = 0.3; 
	
	/**
	 * The lacunarity of the multifractal.
	 */
	double lacunarity = 0.5; 
	
	/**
	 * The number of octaves over which to evaluate the multifractal.
	 */
	int octaves = 4; 
	
	/**
	 * The offset parameter for the multifractal.
	 */
	double offset = 0.3;
	
	
	/**
	 * Constructs the node.
	 */
	public GMultifractal()
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
		
		WaveForm wv = new MultifractalWaveForm(w,H,lacunarity,octaves,offset);
		s.put(this, wv);
		
		return( wv );
	}
	
	/**
	 *  Loads new values into the node.
	 * @param _chld The noise to be applied in generating the multifractal.  Typically this would be a lattice noise.
	 * @param _H The H parameter of the multifractal.
	 * @param _lacunarity The lacunarity of the multifractal.
	 * @param _octaves The number of octaves over which to evaluate the multifractal.
	 * @param _offset The offset parameter for the multifractal.
	 */
	public void load( GWaveForm in , double _H , 
			double _lacunarity , int _octaves , double _offset )
	{
		chld = in;
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
		return( "Noise -- Multifractal" );
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
		MultifractalEditor editor = new MultifractalEditor( this , track );
		ProgramDirector.showPropertyEditor(editor, null,
			"Multifractal Properties");
	}

	/**
	 * Gets the H parameter of the multifractal.
	 * @return The H parameter of the multifractal.
	 */
	public double getH() {
		return H;
	}

	/**
	 * Sets the H parameter of the multifractal.
	 * @param h The H parameter of the multifractal.
	 */
	public void setH(double h) {
		H = h;
	}

	/**
	 * Gets the lacunarity of the multifractal.
	 * @return The lacunarity of the multifractal.
	 */
	public double getLacunarity() {
		return lacunarity;
	}

	/**
	 * Sets the lacunarity of the multifractal.
	 * @param lacunarity The lacunarity of the multifractal.
	 */
	public void setLacunarity(double lacunarity) {
		this.lacunarity = lacunarity;
	}

	/**
	 * Gets the number of octaves over which to evaluate the multifractal.
	 * @return The number of octaves over which to evaluate the multifractal.
	 */
	public int getOctaves() {
		return octaves;
	}

	/**
	 * Sets the number of octaves over which to evaluate the multifractal.
	 * @param octaves The number of octaves over which to evaluate the multifractal.
	 */
	public void setOctaves(int octaves) {
		this.octaves = octaves;
	}

	/**
	 * Gets the offset parameter for the multifractal.
	 * @return The offset parameter for the multifractal.
	 */
	public double getOffset() {
		return offset;
	}

	/**
	 * Sets the offset parameter for the multifractal.
	 * @param offset The offset parameter for the multifractal.
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
		myv.setInt("Octaves", octaves);
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
			octaves = myv.getInt("Octaves");
			offset = myv.getDouble("Offset");

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}
