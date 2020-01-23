





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
 * A node representing a waveform approximating 1/f fractal sum noise as described in the book "Texturing and Modeling" by David S. Ebert et. al.
 * @author thorngreen
 *
 */
public class GFractalSum extends GWaveForm implements Externalizable {
	
	/**
	 * The minimum frequency at which to generate the noise fractal.
	 */
	double minFreq = 1.0;
	
	/**
	 * The maximum frequency at which to generate the noise fractal.
	 */
	double maxFreq = 20.0;

	/**
	 * The input noise from which to generate the fractal.  Typically this is a lattice noise.
	 */
	private GWaveForm chld;
	
	/**
	 * Constructs the node.
	 */
	public GFractalSum()
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
		
		WaveForm wv = FractalsumWave.gen(w, minFreq, maxFreq);
		s.put(this, wv);
		
		return( wv );
	}
	
	/**
	 * Loads new values into the node.
	 * @param in The input noise from which to generate the fractal.  Typically this is a lattice noise.
	 */
	public void load( GWaveForm in )
	{
		chld = in;
	}

	public Object getChldNodes() {
		return( chld );
	}

	@Override
	public String getName() {
		return( "Noise -- Fractal Sum" );
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
		FractalSumEditor editor = new FractalSumEditor( this , track );
		ProgramDirector.showPropertyEditor(editor, null,
			"Fractal Sum Properties");
	}

	
	/**
	 * Gets the minimum frequency at which to generate the noise fractal.
	 * @return The minimum frequency at which to generate the noise fractal.
	 */
	public double getMinFreq() {
		return minFreq;
	}

	/**
	 * Sets the minimum frequency at which to generate the noise fractal.
	 * @param minFreq The minimum frequency at which to generate the noise fractal.
	 */
	public void setMinFreq(double minFreq) {
		this.minFreq = minFreq;
	}

	/**
	 * Gets the maximum frequency at which to generate the noise fractal.
	 * @return The maximum frequency at which to generate the noise fractal.
	 */
	public double getMaxFreq() {
		return maxFreq;
	}

	/**
	 * Sets the maximum frequency at which to generate the noise fractal.
	 * @param maxFreq The maximum frequency at which to generate the noise fractal.
	 */
	public void setMaxFreq(double maxFreq) {
		this.maxFreq = maxFreq;
	}
	
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( chld != null ) myv.setProperty("Chld", chld);
		myv.setDouble("MinFreq", minFreq);
		myv.setDouble("MaxFreq", maxFreq);

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
			minFreq = myv.getDouble("MinFreq");
			maxFreq = myv.getDouble("MaxFreq");

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	
	
}

