





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
 * 
 * Node representing a waveform applying a hard clipping overdrive to an input waveform.
 * 
 * See <A href="https://en.wikipedia.org/wiki/Distortion_(music)">https://en.wikipedia.org/wiki/Distortion_(music)</A>
 * 
 * @author tgreen
 *
 */
public class GHardOverdriveWaveForm extends GWaveForm  implements Externalizable {
	
	/**
	 * Input waveform.
	 */
	private GWaveForm wave;
	
	/**
	 * Multiplier to use before applying the clipping.
	 */
	private double mult;

	@Override
	public WaveForm genWave(HashMap s) {
		if( s.get(this) != null )
		{
			return( (WaveForm)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		WaveForm w = wave.genWave(s);
		
		HardOverdriveWaveForm wv = new HardOverdriveWaveForm( w , mult );
		s.put(this, wv);
		
		return( wv );
	}

	public Object getChldNodes() {
		return( wave );
	}

	@Override
	public String getName() {
		return( "HardOverdrive" );
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
	 * @param w Input waveform.
	 * @param m Multiplier to use before applying the clipping.
	 */
	public void load( GWaveForm w , double m )
	{
		wave = w;
		mult = m;
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		InstrumentTrack track = (InstrumentTrack)( context.get( "InstrumentTrack" ) );
		HardOverdriveWaveFormEditor editor = new HardOverdriveWaveFormEditor( this , track );
		ProgramDirector.showPropertyEditor(editor, null,
			"Hard Overdrive Wave Form Properties");
	}

	/**
	 * Gets the multiplier to use before applying the clipping.
	 * @return The multiplier to use before applying the clipping.
	 */
	public double getMult() {
		return mult;
	}

	/**
	 * Sets the multiplier to use before applying the clipping.
	 * @param mult The multiplier to use before applying the clipping.
	 */
	public void setMult(double mult) {
		this.mult = mult;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( wave != null ) myv.setProperty("Wave", wave);
		myv.setDouble("Mult",mult);

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
			mult = myv.getDouble("Mult");

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}

