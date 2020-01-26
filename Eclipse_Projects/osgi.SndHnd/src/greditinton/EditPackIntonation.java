




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







package greditinton;



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
import core.Intonation;


/**
 * An editing pack for a group of nodes representing an intonation and musical scale.
 * 
 * See https://en.wikipedia.org/wiki/intonation_(music)
 * 
 * @author tgreen
 *
 */
public class EditPackIntonation implements Externalizable {
	
	/**
	 * The set of BuilderNode instances edited using the EditPack.
	 */
	private final HashSet<BuilderNode> elem = new HashSet<BuilderNode>();
	
	/**
	 * The input intonation of the EditPack.
	 */
	private GIntonIn intonIn = new GIntonIn();
	
	/**
	 * The output intonation of the EditPack.
	 */
	private GIntonOut intonOut = new GIntonOut();
	
	/**
	 * Constructs the EditPack.
	 */
	public EditPackIntonation()
	{
		intonOut.performAssign( intonIn );
		AazonTransChld.initialCoords( AczonUnivAllocator.allocateUniv() , elem, null, intonOut);
	}
	
	/**
	 * Generates an instance of the intonation represented by this node.
	 * @param s HashMap used to eliminate duplicates.
	 * @return An instance of the intonation represented by this node.
	 */
	public Intonation processInton( HashMap s )
	{
		Intonation out = intonOut.genInton(s);
		return( out );
	}
	
	/**
	 * Gets the name of each increment on the musical scale for the intonation.
	 * @return  The name of each increment on the musical scale for the intonation.
	 */
	public String[] getScaleNames( )
	{
		return( intonOut.getScaleNames() );
	}
	
	/**
	 * Gets the names on the musical scale for the intonation which do not have an accidental.
	 * @return The names on the musical scale for the intonation which do not have an accidental.
	 */
	public String[] getPriScaleNames( )
	{
		return( intonOut.getPriScaleNames() );
	}
	
	/**
	 * Gets the base intonation that is the generator for the output intonation.  For instance, this allows the caller to determine whether the result of q GConflate is a western intonation.
	 * @return The base intonation that is the generator for the output intonation.
	 */
	public GIntonation getBaseIntonation( )
	{
		return( intonOut.getBaseIntonation() );
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
	 * Gets the input intonation of the EditPack.
	 * @return The input intonation of the EditPack.
	 */
	public GIntonIn getIntonIn() {
		return intonIn;
	}


	/**
	 * Gets the output intonation of the EditPack.
	 * @return The output intonation of the EditPack.
	 */
	public GIntonOut getIntonOut() {
		return intonOut;
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
		myv.setProperty("IntonIn", intonIn);
		myv.setProperty("IntonOut", intonOut);

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
			intonIn = (GIntonIn)( myv.getPropertyEx("IntonIn") );
			intonOut = (GIntonOut)( myv.getPropertyEx("IntonOut") );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}
	

}


