





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







package waves;

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
 * A node representing a waveform producing a simple chorus effect on an input wave.
 * 
 * See <A href="https://en.wikipedia.org/wiki/Chorus_effect">https://en.wikipedia.org/wiki/Chorus_effect</A>
 * 
 * @author tgreen
 *
 */
public class GChorusWaveform extends GWaveForm  implements Externalizable {
	
	/**
	 * The input waveform.
	 */
	private GWaveForm wave;
	
	/**
	 * The maximum number of chorus levels to add.
	 */
	private int max = 1;
	
	/**
	 * The frequency increment or decrement for each chorus level.
	 */
	private double deltaPerIncr = 0.01;

	
	@Override
	public WaveForm genWave(HashMap s) {
		if( s.get(this) != null )
		{
			return( (WaveForm)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		WaveForm w = wave.genWave(s);
		
		ChorusWaveform wv = new ChorusWaveform( w , max , deltaPerIncr );
		s.put(this, wv);
		
		return( wv );
	}

	public Object getChldNodes() {
		return( wave );
	}

	@Override
	public String getName() {
		return( "Chorus" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GWaveForm );
	}

	@Override
	public void performAssign(GNode in) {
		wave = (GWaveForm) in;
	}

	@Override
	public void removeChld() {
		wave = null;
	}
	
	/**
	 * Loads new values into the node.
	 * @param w The input waveform.
	 * @param _max The maximum number of chorus levels to add.
	 * @param _deltaPerIncr The frequency increment or decrement for each chorus level.
	 */
	public void load( GWaveForm w , int _max , double _deltaPerIncr )
	{
		wave = w;
		max = _max;
		deltaPerIncr = _deltaPerIncr;
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		InstrumentTrack track = (InstrumentTrack)( context.get( "InstrumentTrack" ) );
		ChorusWaveformEditor editor = new ChorusWaveformEditor( this , track );
		ProgramDirector.showPropertyEditor(editor, null,
			"Chorus Wave Form Properties");
	}

	/**
	 * Gets the maximum number of chorus levels to add.
	 * @return The maximum number of chorus levels to add.
	 */
	public int getMax() {
		return max;
	}

	/**
	 * Sets the maximum number of chorus levels to add.
	 * @param max The maximum number of chorus levels to add.
	 */
	public void setMax(int max) {
		this.max = max;
	}
	
	/**
	 * Gets the frequency increment or decrement for each chorus level.
	 * @return The frequency increment or decrement for each chorus level.
	 */
	public double getDeltaPerIncr() {
		return deltaPerIncr;
	}

	/**
	 * Sets the frequency increment or decrement for each chorus level.
	 * @param deltaPerIncr The frequency increment or decrement for each chorus level.
	 */
	public void setDeltaPerIncr(double deltaPerIncr) {
		this.deltaPerIncr = deltaPerIncr;
	}

	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( wave != null ) myv.setProperty("Wave", wave);
		myv.setInt("Max",max);
		myv.setDouble("DeltaPerIncr",deltaPerIncr);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			wave = (GWaveForm)( myv.getProperty("Wave") );
			max = myv.getInt("Max");
			deltaPerIncr = myv.getDouble("DeltaPerIncr");

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}
