





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
 * Node representing a waveform generating approximate white noise using a simplified hashing function.
 * @author tgreen
 *
 */
public class GHashWhiteWaveForm extends GZWaveBase implements Externalizable {

	/**
	 * Multiplier for the hashing function.
	 */
	protected double tmult = 1.7777E+5;
	
	/**
	 * Multiplier for the hashing function.
	 */
	protected double tma = 0.53147638582379454239574 / 0.5;
	
	/**
	 * Addend for the hashing function.
	 */
	protected double tmb = 0.637724401110901;
	
	/**
	 * Multiplier for the hashing function.
	 */
	protected int hashMult = 65599;
	
	
	/**
	 * Constructs the node.
	 */
	public GHashWhiteWaveForm()
	{
	}

	@Override
	public WaveForm genWave(HashMap s) {
		if( s.get(this) != null )
		{
			return( (WaveForm)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		WaveForm wv = new HashWhiteWaveForm( tmult, tma , tmb , hashMult );
		s.put(this, wv);
		
		return( wv );
	}
	
	/**
	 * Loads the contents of the node.
	 * @param _tmult  Multiplier for the hashing function.
	 * @param _tma  Multiplier for the hashing function.
	 * @param _tmb  Addend for the hashing function.
	 * @param _hashMult  Multiplier for the hashing function.
	 */
	public void load( double _tmult , double _tma , double _tmb , int _hashMult )
	{
		tmult = _tmult;
		tma = _tma;
		tmb = _tmb;
		hashMult = _hashMult;
	}

	@Override
	public String getName() {
		return( "Noise -- Hash White" );
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		InstrumentTrack track = (InstrumentTrack)( context.get( "InstrumentTrack" ) );
		HashWhiteWaveFormEditor editor = new HashWhiteWaveFormEditor( this , track );
		ProgramDirector.showPropertyEditor(editor, null,
			"Hash White Wave Form Properties");
	}

	/**
	 * Gets the multiplier for the hashing function.
	 * @return The multiplier for the hashing function.
	 */
	public double getTmult() {
		return tmult;
	}

	/**
	 * Sets the multiplier for the hashing function.
	 * @param tmult  The multiplier for the hashing function.
	 */
	public void setTmult(double tmult) {
		this.tmult = tmult;
	}

	/**
	 * Gets the multiplier for the hashing function.
	 * @return The multiplier for the hashing function.
	 */
	public double getTma() {
		return tma;
	}

	/**
	 * Sets the multiplier for the hashing function.
	 * @param tma  The multiplier for the hashing function.
	 */
	public void setTma(double tma) {
		this.tma = tma;
	}

	/**
	 * Gets the addend for the hashing function.
	 * @return The addend for the hashing function.
	 */
	public double getTmb() {
		return tmb;
	}

	/**
	 * Sets the addend for the hashing function.
	 * @param tmb The addend for the hashing function.
	 */
	public void setTmb(double tmb) {
		this.tmb = tmb;
	}

	/**
	 * Gets the multiplier for the hashing function.
	 * @return The multiplier for the hashing function.
	 */
	public int getHashMult() {
		return hashMult;
	}

	/**
	 * Sets the multiplier for the hashing function.
	 * @param hashMult  The multiplier for the hashing function.
	 */
	public void setHashMult(int hashMult) {
		this.hashMult = hashMult;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setDouble("Tmult", tmult);
		myv.setDouble("Tma", tma);
		myv.setDouble("Tmb", tmb);
		myv.setInt("HashMult", hashMult);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			tmult = myv.getDouble("Tmult");
			tma = myv.getDouble("Tma");
			tmb = myv.getDouble("Tmb");
			hashMult = myv.getInt("HashMult");

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	
}

