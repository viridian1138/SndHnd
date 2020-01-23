





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
 * A node representing a waveform for approximating a basic gas cloud as described in the book "Texturing and Modeling" by David S. Ebert et. al.
 * @author thorngreen
 *
 */
public class GBasicGasWaveForm extends GWaveForm  implements Externalizable {

	/**
	 * Input noise waveform from which to calculate the waveform.
	 */
	private GWaveForm chld;
	
	/**
	 * Input parameter 0.
	 */
	double param0 = 0.5;
	
	/**
	 * Input parameter 1.
	 */
	double param1 = 0.5;
	
	/**
	 * Constructs the node.
	 */
	public GBasicGasWaveForm()
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
		
		WaveForm wv = new BasicGasWaveForm(w,param0,param1);
		s.put(this, wv);
		
		return( wv );
	}
	
	/**
	 * Loads new values into the node.
	 * @param in  Input noise waveform from which to calculate the waveform.
	 * @param _param0  Input parameter 0.
	 * @param _param1  Input parameter 1.
	 */
	public void load( GWaveForm in , double _param0 , double _param1 )
	{
		chld = in;
		param0 = _param0;
		param1 = _param1;
	}

	public Object getChldNodes() {
		return( chld );
	}

	@Override
	public String getName() {
		return( "Noise -- BasicGas" );
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
		BasicGasWaveFormEditor editor = new BasicGasWaveFormEditor( this , track );
		ProgramDirector.showPropertyEditor(editor, null,
			"Basic Gas Wave Form Properties");
	}

	/**
	 * Gets input parameter 0.
	 * @return Input parameter 0.
	 */
	public double getParam0() {
		return param0;
	}

	/**
	 * Sets Input parameter 0.
	 * @param param0 Input parameter 0.
	 */
	public void setParam0(double param0) {
		this.param0 = param0;
	}

	/**
	 * Gets Input parameter 1.
	 * @return Input parameter 1.
	 */
	public double getParam1() {
		return param1;
	}

	/**
	 * Sets Input parameter 1.
	 * @param param1 Input parameter 1.
	 */
	public void setParam1(double param1) {
		this.param1 = param1;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( chld != null ) myv.setProperty("Chld", chld);
		myv.setDouble("Param0", param0);
		myv.setDouble("Param1", param1);

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
			param0 = myv.getDouble("Param0");
			param1 = myv.getDouble("Param1");

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	
}

