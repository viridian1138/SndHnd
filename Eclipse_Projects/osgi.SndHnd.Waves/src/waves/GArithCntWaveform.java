





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
 * Node representing a waveform that returns w ^ ( 2 * ( m + 1 ) ) where "w" is the value from the input waveform and "m" is the maximum iteration count.
 * 
 * @author tgreen
 *
 */
public class GArithCntWaveform extends GWaveForm  implements Externalizable {
	
	/**
	 * The input waveform.
	 */
	private GWaveForm wave;
	
	/**
	 * The maximum iteration count.
	 */
	private int max = 16;

	@Override
	public WaveForm genWave(HashMap s) {
		if( s.get(this) != null )
		{
			return( (WaveForm)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		WaveForm w = wave.genWave(s);
		
		ArithCntWaveform wv = new ArithCntWaveform( w , max );
		s.put(this, wv);
		
		return( wv );
	}

	public Object getChldNodes() {
		return( wave );
	}

	@Override
	public String getName() {
		return( "ArithCnt" );
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
	 * @param w The input waveform to be loaded.
	 * @param _max The input maximum iteration count to be loaded.
	 */
	public void load( GWaveForm w , int _max )
	{
		wave = w;
		max = _max;
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		InstrumentTrack track = (InstrumentTrack)( context.get( "InstrumentTrack" ) );
		ArithCntWaveformEditor editor = new ArithCntWaveformEditor( this , track );
		ProgramDirector.showPropertyEditor(editor, null,
			"ArithCnt Wave Form Properties");
	}

	/**
	 * Gets  the iteration max.
	 * @return The iteration max.
	 */
	public int getMax() {
		return max;
	}

	/**
	 * Sets the iteration max.
	 * @param max The iteration max.
	 */
	public void setMax(int max) {
		this.max = max;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( wave != null ) myv.setProperty("Wave", wave);
		myv.setInt("Max",max);

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

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}
