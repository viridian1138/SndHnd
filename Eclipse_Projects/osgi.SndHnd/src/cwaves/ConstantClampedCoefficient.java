




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


import gredit.GClampedCoefficient;
import gredit.GNode;
import gredit.GWaveForm;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import core.ClampedCoefficient;
import core.NonClampedCoefficient;

/**
 * A clamped coefficient with a constant value.
 * 
 * @author tgreen
 *
 */
public class ConstantClampedCoefficient extends ClampedCoefficient {

	/**
	 * The constant value.
	 */
	protected double value;

	/**
	 * Constructs the coefficient.
	 * @param _value The constant value.
	 */
	public ConstantClampedCoefficient(double _value ) {
		super();
		value = _value;
	}
	
	/**
	 * Constructor used for persistence only.
	 *
	 */
	public ConstantClampedCoefficient()
	{
	}

	@Override
	public double eval(double non_phase_distorted_param) {
		double u = non_phase_distorted_param;
		
		if( u < 0.0 || u > 1.0 )
		{
			return( 0.0 );
		}
		
		return( value );
	}
	
	@Override
	public NonClampedCoefficient genClone()
	{
		return( this );
	}
	
	@Override
	public GClampedCoefficient genClamped( HashMap<Object,GNode> s )
	{
		if( s.get( this ) != null )
		{
			return( (GClampedCoefficient)( s.get( this ) ) );
		}
		
		GConstantClampedCoefficient wv = new GConstantClampedCoefficient();
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
