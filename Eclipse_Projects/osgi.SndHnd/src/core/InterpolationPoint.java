




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







package core;


import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import meta.DataFormatException;
import meta.VersionBuffer;


/**
 * An interpolation point of a functional curve or a functional Bezier polygon.
 * 
 * @author tgreen
 *
 */
public class InterpolationPoint implements Externalizable{
	
	/**
	 * The interpolation parameter.
	 */
	protected double param;
	
	/**
	 * The interpolation value.
	 */
	protected double value;

	/**
	 * Constructor.
	 */
	public InterpolationPoint() {
		super();
	}
	
	/**
	 * Constructor.
	 * @param _param The interpolation parameter.
	 * @param _value The interpolation value.
	 */
	public InterpolationPoint( double _param , double _value )
	{
		super();
		param = _param;
		value = _value;
	}
	
	/**
	 * Copy constructor.
	 * @param in Input InterpolationPoint to be copied.
	 */
	public InterpolationPoint( InterpolationPoint in )
	{
		super();
		param = in.param;
		value = in.value;
	}
	
	/**
	 * Generates a thread-safe clone of the point (assuming nothing tries to move the point).
	 * @return The clone of the point.
	 */
	public InterpolationPoint genClone()
	{
		return( this );
	}

	/**
	 * Gets the interpolation parameter.
	 * @return The interpolation parameter.
	 */
	public double getParam() {
		return param;
	}

	/**
	 * Sets the interpolation parameter.
	 * @param param The interpolation parameter.
	 */
	public void setParam(double param) {
		this.param = param;
	}

	/**
	 * Gets the interpolation value.
	 * @return The interpolation value.
	 */
	public double getValue() {
		return value;
	}

	/**
	 * Sets the interpolation value.
	 * @param value The interpolation value.
	 */
	public void setValue(double value) {
		this.value = value;
	}
	

	/**
	* Reads the node from serial storage.
	*/
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			param = myv.getDouble("Param");
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

		myv.setDouble("Param", param);
		myv.setDouble("Value", value);

		out.writeObject(myv);
	}

}

