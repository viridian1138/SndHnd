




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







package cwaves;

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
 * Node representing a waveform consisting of piecewise exponential functions with exponentials rising from the trough to each peak.
 * 
 * @author tgreen
 *
 */
public class GRollWaveform extends GZWaveBase implements Externalizable {
	
	/**
	 * The proportion into the wave period at which to put the trough.  The wave period has a peak on each end.
	 */
	protected double atk = 0.25;
	
	/**
	 * Exponential decay rate going from the peak to a trough to the right of the peak.
	 */
	protected double powa = 3.0;
	
	/**
	 * Exponential decay rate going from the peak to a trough to the left of the peak.
	 */
	protected double powb = 2.0;

	@Override
	public WaveForm genWave(HashMap s) {
		if( s.get(this) != null )
		{
			return( (WaveForm)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		RollWaveform wv = new RollWaveform( atk , powa , powb );
		s.put(this, wv);
		
		return( wv );
	}

	@Override
	public String getName() {
		return( "Roll" );
	}
	
	/**
	 * Loads new values into the node.
	 * @param a The proportion into the wave period at which to put the trough.  The wave period has a peak on each end.
	 * @param pa Exponential decay rate going from the peak to a trough to the right of the peak.
	 * @param pb Exponential decay rate going from the peak to a trough to the left of the peak.
	 */
	public void load( double a, double pa , double pb )
	{
		atk = a;
		powa = pa;
		powb = pb;
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		InstrumentTrack track = (InstrumentTrack)( context.get( "InstrumentTrack" ) );
		RollWaveformEditor editor = new RollWaveformEditor( this , track );
		ProgramDirector.showPropertyEditor(editor, null,
			"Roll Waveform Properties");
	}

	/**
	 * Gets the proportion into the wave period at which to put the trough.  The wave period has a peak on each end.
	 * @return The proportion into the wave period at which to put the trough.  The wave period has a peak on each end.
	 */
	public double getAtk() {
		return atk;
	}

	/**
	 * Sets the proportion into the wave period at which to put the trough.  The wave period has a peak on each end.
	 * @param atk The proportion into the wave period at which to put the trough.  The wave period has a peak on each end.
	 */
	public void setAtk(double atk) {
		this.atk = atk;
	}

	/**
	 * Gets the exponential decay rate going from the peak to a trough to the right of the peak.
	 * @return The exponential decay rate going from the peak to a trough to the right of the peak.
	 */
	public double getPowa() {
		return powa;
	}

	/**
	 * Sets the exponential decay rate going from the peak to a trough to the right of the peak.
	 * @param powa The exponential decay rate going from the peak to a trough to the right of the peak.
	 */
	public void setPowa(double powa) {
		this.powa = powa;
	}

	/**
	 * Gets the exponential decay rate going from the peak to a trough to the left of the peak.
	 * @return The exponential decay rate going from the peak to a trough to the left of the peak.
	 */
	public double getPowb() {
		return powb;
	}

	/**
	 * Sets the exponential decay rate going from the peak to a trough to the left of the peak.
	 * @param powb The exponential decay rate going from the peak to a trough to the left of the peak.
	 */
	public void setPowb(double powb) {
		this.powb = powb;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setDouble("Atk", atk);
		myv.setDouble("Powa", powa);
		myv.setDouble("Powb", powb);

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
			powa = myv.getDouble("Powa");
			powb = myv.getDouble("Powb");

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}
