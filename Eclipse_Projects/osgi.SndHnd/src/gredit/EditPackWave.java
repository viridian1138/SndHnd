




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







package gredit;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import meta.DataFormatException;
import meta.VersionBuffer;
import aazon.builderNode.AazonTransChld;
import aazon.builderNode.BuilderNode;
import aczon.AczonUnivAllocator;
import core.WaveForm;

/**
 * An editing pack for a group of nodes representing a waveform, which is encouraged but not required to be clamped to [-1, 1].
 * 
 * Where possible, and particularly for the typical synthesizer waveforms such as sine, square, triangle, etc., it
 * is encouraged but not required for the waveform to have a period of unity.
 * 
 * Where the waveform is a lattice noise wave for which there is no obvious repeating period, it is encouraged but
 * not required for the waveform to have a lattice spacing of unity as this often produces a wave period that
 * is within an octave of unity.
 * 
 * Where the waveform is sampled from an audio file and hence there is no obvious period, there is
 * a useAutocorrelation() method to indicate that autocorrelation should be used to determine the wave 
 * period for a particular part of the sample.
 * 
 * @author tgreen
 *
 */
public class EditPackWave implements Externalizable {
	
	/**
	 * The set of BuilderNode instances edited using the EditPack.
	 */
	private final HashSet<BuilderNode> elem = new HashSet<BuilderNode>();
	
	/**
	 * The input waveform of the EditPack.
	 */
	private GWaveIn waveIn = new GWaveIn(); 
	
	/**
	 * The output waveform of the EditPack.
	 */
	private GWaveOut waveOut = new GWaveOut();
	
	/**
	 * Constructs the EditPack.
	 */
	public EditPackWave()
	{
		waveOut.performAssign( waveIn );
		AazonTransChld.initialCoords( AczonUnivAllocator.allocateUniv() , elem, null, waveOut);
	}
	
	/**
	 * Generates an instance of the waveform represented by this node.
	 * @param s HashMap used to eliminate duplicates.
	 * @return An instance of the waveform represented by this node.
	 */
	public WaveForm processWave( WaveForm in )
	{
		waveIn.setWave(in);
		HashMap s = new HashMap();
		WaveForm out = waveOut.genWave(s);
		return( out );
	}
	
	/**
	 * Gets the set of BuilderNode instances edited using the EditPack.
	 * @return The set of BuilderNode instances edited using the EditPack.
	 */
	public HashSet<BuilderNode> getElem()
	{
		return( elem );
	}


	/**
	 * Gets the input waveform of the EditPack.
	 * @return The input waveform of the EditPack.
	 */
	public GWaveIn getWaveIn() {
		return waveIn;
	}


	/**
	 * Gets the output waveform of the EditPack.
	 * @return The output waveform of the EditPack.
	 */
	public GWaveOut getWaveOut() {
		return waveOut;
	}
	
	/**
	* Writes the node to serial storage.
	* @serialData TBD.
	*/
	public void writeExternal(ObjectOutput out) throws IOException {
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		int sz = elem.size();
		int count;
		myv.setInt( "sz", sz);
		Iterator<BuilderNode> it = elem.iterator();
		for( count = 0 ; count < sz ; count++ )
		{
			BuilderNode ob = it.next();
			myv.setProperty(  "index_" + count , ob );
		}
		myv.setProperty("WaveIn", waveIn);
		myv.setProperty("WaveOut", waveOut);

		out.writeObject(myv);
	}

	/**
	* Reads the node from serial storage.
	*/
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			elem.clear();
			int sz = myv.getInt( "sz");
			int count;
			for( count = 0 ; count < sz ; count++ )
			{
				BuilderNode el = (BuilderNode)( myv.getProperty(  "index_" + count ) );
				elem.add( el );
			}
			waveIn = (GWaveIn)( myv.getPropertyEx("WaveIn") );
			waveOut = (GWaveOut)( myv.getPropertyEx("WaveOut") );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}
	

}

