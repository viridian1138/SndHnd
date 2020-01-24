





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

import gredit.GZWaveBase;

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
 * Node representing a waveform composed of multiple sinc functions that is periodic.
 * 
 * @author tgreen
 *
 */
public class GMultiSincWaveForm extends GZWaveBase  implements Externalizable {
	
	/**
	 * The relative frequency of the underlying sinc functions.
	 */
	private double mult = 3.0;
	
	/**
	 * The notch height of the wave combination.
	 */
	private double notchHeight = 1.0;

	@Override
	public WaveForm genWave(HashMap s) {
		if( s.get(this) != null )
		{
			return( (WaveForm)( s.get(this) ) );
		}
		
		MultiSincWaveForm wv = new MultiSincWaveForm( mult , notchHeight );
		s.put(this, wv);
		
		return( wv );
	}

	@Override
	public String getName() {
		return( "MultiSinc" );
	}
	
	/**
	 * Loads new values into the node.
	 * @param m The relative frequency of the underlying sinc functions.
	 * @param n The notch height of the wave combination.
	 */
	public void load( double m , double n )
	{
		mult = m;
		notchHeight = n;
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		InstrumentTrack track = (InstrumentTrack)( context.get( "InstrumentTrack" ) );
		MultiSincWaveFormEditor editor = new MultiSincWaveFormEditor( this , track );
		ProgramDirector.showPropertyEditor(editor, null,
			"Multi Sinc Wave Form Properties");
	}

	/**
	 * Gets the relative frequency of the underlying sinc functions.
	 * @return The relative frequency of the underlying sinc functions.
	 */
	public double getMult() {
		return mult;
	}

	/**
	 * Sets the relative frequency of the underlying sinc functions.
	 * @param mult The relative frequency of the underlying sinc functions.
	 */
	public void setMult(double mult) {
		this.mult = mult;
	}

	/**
	 * Gets the notch height of the wave combination.
	 * @return The notch height of the wave combination.
	 */
	public double getNotchHeight() {
		return notchHeight;
	}

	/**
	 * Sets the notch height of the wave combination.
	 * @param notchHeight The notch height of the wave combination.
	 */
	public void setNotchHeight(double notchHeight) {
		this.notchHeight = notchHeight;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setDouble("Mult",mult);
		myv.setDouble("NotchHeight",notchHeight);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			mult = myv.getDouble("Mult");
			notchHeight = myv.getDouble("NotchHeight");

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}

