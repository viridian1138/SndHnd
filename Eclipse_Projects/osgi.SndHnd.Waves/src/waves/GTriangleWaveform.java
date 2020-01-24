





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
 * Node representing a waveform for a triangle wave.
 * 
 * See <A href="https://en.wikipedia.org/wiki/Triangle_wave">https://en.wikipedia.org/wiki/Triangle_wave</A>
 * 
 * @author tgreen
 *
 */
public class GTriangleWaveform extends GWaveForm  implements Externalizable {
	
	/**
	 * The length of the attack for the triangle.
	 */
	protected double atk = 0.25;
	
	/**
	 * Constructs the node.
	 */
	public GTriangleWaveform()
	{
	}

	@Override
	public WaveForm genWave(HashMap s) {
		if( s.get(this) != null )
		{
			return( (WaveForm)( s.get(this) ) );
		}
		
		TriangleWaveform wv = new TriangleWaveform(atk);
		s.put(this, wv);
		
		return( wv );
	}

	/**
	 * Loads new values into the node.
	 * @param _atk The length of the attack for the triangle.
	 */
	public void load( double _atk )
	{
		atk = _atk;
	}

	public Object getChldNodes() {
		return( null );
	}

	@Override
	public String getName() {
		return( "Triangle" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( false );
	}

	@Override
	public void performAssign(GNode in) {
		throw( new RuntimeException( "Not Supported" ) );

	}

	@Override
	public void removeChld() {
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		InstrumentTrack track = (InstrumentTrack)( context.get( "InstrumentTrack" ) );
		TriangleWaveformEditor editor = new TriangleWaveformEditor( this , track );
		ProgramDirector.showPropertyEditor(editor, null,
			"Triangle Wave Form Properties");
	}

	/**
	 * Gets the length of the attack for the triangle.
	 * @return The length of the attack for the triangle.
	 */
	public double getAtk() {
		return atk;
	}

	/**
	 * Sets the length of the attack for the triangle.
	 * @param atk The length of the attack for the triangle.
	 */
	public void setAtk(double atk) {
		this.atk = atk;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setDouble("Atk", atk);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);
			
			atk = myv.getDouble("Atk");
			

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}

