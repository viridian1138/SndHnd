




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







package greditcoeffn;




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
import core.NonClampedCoefficient;


/**
 * An editing pack for a group of nodes representing a non-clamped coefficient, i.e. one whose values are not necessarily encouraged to be in [0, 1].
 * 
 * @author tgreen
 *
 */
public class EditPackCoeffn implements Externalizable {
	
	/**
	 * The set of BuilderNode instances edited using the EditPack.
	 */
	private final HashSet<BuilderNode> elem = new HashSet<BuilderNode>();
	
	/**
	 * The input non-clamped coefficient of the EditPack.
	 */
	private GCoeffnIn coeffIn = new GCoeffnIn();
	
	/**
	 * The output non-clamped coefficient of the EditPack.
	 */
	private GCoeffnOut coeffOut = new GCoeffnOut();
	
	/**
	 * Constructs the EditPack.
	 */
	public EditPackCoeffn()
	{
		coeffOut.performAssign( coeffIn ); 
		AazonTransChld.initialCoords( AczonUnivAllocator.allocateUniv() , elem, null, coeffOut);
	}
	
	/**
	 * Generates an instance of the non-clamped coefficient represented by this node.
	 * @return An instance of the non-clamped coefficient represented by this node.
	 */
	public NonClampedCoefficient processCoeff( )
	{
		HashMap s = new HashMap();
		NonClampedCoefficient out = coeffOut.genCoeff(s);
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
	 * Gets the input non-clamped coefficient of the EditPack.
	 * @return The input non-clamped coefficient of the EditPack.
	 */
	public GCoeffnIn getCoeffIn() {
		return coeffIn;
	}


	/**
	 * Gets the output non-clamped coefficient of the EditPack.
	 * @return The output non-clamped coefficient of the EditPack.
	 */
	public GCoeffnOut getCoeffOut() {
		return coeffOut;
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
		myv.setProperty("CoeffIn", coeffIn);
		myv.setProperty("CoeffOut", coeffOut);

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
			coeffIn = (GCoeffnIn)( myv.getPropertyEx("CoeffIn") );
			coeffOut = (GCoeffnOut)( myv.getPropertyEx("CoeffOut") );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}
	

}


