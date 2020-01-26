




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
import verdantium.ProgramDirector;
import core.InstrumentTrack;
import core.NonClampedCoefficient;

/**
 * Node representing a non-clamped coefficient with a constant value.
 * 
 * @author tgreen
 *
 */
public class GConstantNonClampedCoefficient extends GNonClampedCoefficient implements Externalizable {
	
	/**
	 * The constant value.
	 */
	double val = 0.5;
	
	/**
	 * Constructs the node.
	 */
	public GConstantNonClampedCoefficient()
	{
	}
	
	/**
	 * Constructs the node.
	 * @param _val The constant value.
	 */
	public GConstantNonClampedCoefficient( double _val )
	{
		val = _val;
	}

	@Override
	public NonClampedCoefficient genCoeff(HashMap s) {
		if( s.get(this) != null )
		{
			return( (NonClampedCoefficient)( s.get(this) ) );
		}
		
		ConstantNonClampedCoefficient wv = new ConstantNonClampedCoefficient( val );
		s.put(this, wv);
		
		return( wv );
	}

	public Object getChldNodes() {
		return null;
	}

	@Override
	public String getName() {
		return( "ConstantNonClampedCoefficient" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return false;
	}

	@Override
	public void performAssign(GNode in) {
		throw( new RuntimeException( "NotSupported" ) );
	}

	@Override
	public void removeChld() {
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		InstrumentTrack track = (InstrumentTrack)( context.get( "InstrumentTrack" ) );
		ConstantNonClampedCoefficientEditor editor = new ConstantNonClampedCoefficientEditor( this , track );
		ProgramDirector.showPropertyEditor(editor, null,
			"Constant Non Clamped Coefficient Properties");
	}
	
	/**
	 * Loads new values into the node.
	 * @param _val The constant value.
	 */
	public void load( double _val )
	{
		val = _val;
	}

	/**
	 * Sets the constant value.
	 * @param val The constant value.
	 */
	public double getVal() {
		return val;
	}

	/**
	 * Sets the constant value.
	 * @param val The constant value.
	 */
	public void setVal(double val) {
		this.val = val;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setDouble("Val", val);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			val = myv.getDouble("Val");

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}
