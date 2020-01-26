




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


import gredit.GNode;
import gredit.GNonClampedCoefficient;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import core.NonClampedCoefficient;

/**
 * A non-clamped coefficient with a constant value.
 * 
 * @author tgreen
 *
 */
public class ConstantNonClampedCoefficient extends NonClampedCoefficient implements Externalizable {
	
	/**
	 * The constant value.
	 */
	protected double value;

	/**
	 * Constructs the coefficient.
	 * @param _value The constant value.
	 */
	public ConstantNonClampedCoefficient(double _value ) {
		super();
		value = _value;
	}
	
	/**
	 * Constructor used for persistence only.
	 *
	 */
	public ConstantNonClampedCoefficient()
	{
	}
	
	/**
	 * Gets the constant value.
	 * @return The constant value.
	 */
	public double getValue()
	{
		return( value );
	}

	@Override
	public double eval(double non_phase_distorted_param) {
		return( value );
	}
	
	@Override
	public NonClampedCoefficient genClone()
	{
		return( this );
	}
	
	@Override
	public GNonClampedCoefficient genCoeff( HashMap<Object,GNode> s )
	{
		if( s.get( this ) != null )
		{
			return( (GNonClampedCoefficient)( s.get( this ) ) );
		}
		
		GConstantNonClampedCoefficient wv = new GConstantNonClampedCoefficient();
		s.put(this, wv);
		wv.load(value);
		return( wv );
	}
	
	/**
	* Reads the node from serial storage.
	*/
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			value = myv.getDouble("Value");
		}
		catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	/**
	* Writes the node to serial storage.
	* @serialData TBD.
	*/
	public void writeExternal(ObjectOutput out) throws IOException {
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setDouble("Value", value);

		out.writeObject(myv);
	}

}

