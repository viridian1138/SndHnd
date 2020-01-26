




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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import verdantium.ProgramDirector;
import core.ClampedCoefficient;
import core.InstrumentTrack;

/**
 * A node representing a clamped coefficient that remodulates the parameter space of an input clamped coefficient by a simple multiplication.
 * 
 * @author tgreen
 *
 */
public class GClampedCoefficientRemodulator extends GClampedCoefficient implements Externalizable {
	
	/**
	 * The input clamped coefficient.
	 */
	protected GClampedCoefficient coeff;
	
	/**
	 * The input parameter multiplier.
	 */
	protected double multiplier;

	@Override
	public ClampedCoefficient genClamped(HashMap s) {
		if( s.get(this) != null )
		{
			return( (ClampedCoefficient)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		ClampedCoefficient c1 = coeff.genClamped(s);
		
		ClampedCoefficientRemodulator wv = new ClampedCoefficientRemodulator( c1 , multiplier );
		s.put(this, wv);
		
		return( wv );
	}

	public Object getChldNodes() {
		return( coeff );
	}

	@Override
	public String getName() {
		return( "ClampedCoefficientRemodulator" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GClampedCoefficient );
	}

	@Override
	public void performAssign(GNode in) {
		coeff = (GClampedCoefficient) in;
	}

	@Override
	public void removeChld() {
		coeff = null;
	}
	
	/**
	 * Loads new values into the node.
	 * @param c The input clamped coefficient.
	 * @param m The input parameter multiplier.
	 */
	public void load( GClampedCoefficient c , double m )
	{
		coeff = c;
		multiplier = m;
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		InstrumentTrack track = (InstrumentTrack)( context.get( "InstrumentTrack" ) );
		ClampedCoefficientRemodulatorEditor editor = new ClampedCoefficientRemodulatorEditor( this , track );
		ProgramDirector.showPropertyEditor(editor, null,
			"Clamped Coefficient Remodulator Properties");
	}

	/**
	 * Gets the input parameter multiplier.
	 * @return The input parameter multiplier.
	 */
	public double getMultiplier() {
		return multiplier;
	}

	/**
	 * Sets the input parameter multiplier.
	 * @param multiplier The input parameter multiplier.
	 */
	public void setMultiplier(double multiplier) {
		this.multiplier = multiplier;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( coeff != null ) myv.setProperty("Coeff", coeff);
		myv.setDouble("Multiplier", multiplier);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			coeff = (GClampedCoefficient)( myv.getProperty("Coeff") );
			multiplier = myv.getDouble("Multiplier");

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}
