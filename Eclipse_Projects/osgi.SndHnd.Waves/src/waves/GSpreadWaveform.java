





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
 * Node representing a waveform returning an average of a set of evaluations of
 * an input noise waveform spread across multiple phases.
 * The spread between samples increases exponentially to simulate
 * an exponentially decreasing sampling probability as the phase 
 * difference increases.
 * 
 * @author tgreen
 *
 */
public class GSpreadWaveform extends GWaveForm  implements Externalizable {
	
	/**
	 * The input noise waveform.
	 */
	GWaveForm chld;
	
	/**
	 * The number of samples to be averaged.
	 */
	int sampSize = 20;
	
	/**
	 * The maximum phase spread of the sampling in cycle numbers.
	 */
	double num_op2 = 25.0;
	
	/**
	 * Constructs the node.
	 */
	public GSpreadWaveform()
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
		
		WaveForm wv = new SpreadWaveform(w, sampSize, num_op2);
		s.put(this, wv);
		
		return( wv );
	}
	
	/**
	 * Loads new values into the node.
	 * @param _chld The input noise waveform.
	 * @param _sampSize The number of samples to be averaged.
	 * @param num_cycles_op2 The maximum phase spread of the sampling in cycle numbers.
	 */
	public void load( GWaveForm _chld , final int _sampSize , final double num_cycles_op2 )
	{
		chld = _chld;
		sampSize = _sampSize;
		num_op2 = num_cycles_op2;
	}

	public Object getChldNodes() {
		return( chld );
	}

	@Override
	public String getName() {
		return( "Spread WaveForm" );
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
		SpreadWaveformEditor editor = new SpreadWaveformEditor( this , track );
		ProgramDirector.showPropertyEditor(editor, null,
			"Spread Wave Form Properties");
	}

	/**
	 * Gets the number of samples to be averaged.
	 * @return The number of samples to be averaged.
	 */
	public int getSampSize() {
		return sampSize;
	}

	/**
	 * Sets the number of samples to be averaged.
	 * @param sampSize The number of samples to be averaged.
	 */
	public void setSampSize(int sampSize) {
		this.sampSize = sampSize;
	}

	/**
	 * Gets the maximum phase spread of the sampling in cycle numbers.
	 * @return The maximum phase spread of the sampling in cycle numbers.
	 */
	public double getNum_op2() {
		return num_op2;
	}

	/**
	 * Sets the maximum phase spread of the sampling in cycle numbers.
	 * @param num_op2 The maximum phase spread of the sampling in cycle numbers.
	 */
	public void setNum_op2(double num_op2) {
		this.num_op2 = num_op2;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( chld != null ) myv.setProperty("Chld", chld);
		myv.setInt("SampSize", sampSize);
		myv.setDouble("NumOp2",num_op2);

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
			sampSize = myv.getInt("SampSize");
			num_op2 = myv.getDouble("NumOp2");

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}

