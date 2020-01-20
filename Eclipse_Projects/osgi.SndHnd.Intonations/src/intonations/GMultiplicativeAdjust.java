





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







package intonations;

import gredit.GNode;
import greditinton.GIntonation;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import verdantium.ProgramDirector;
import core.Intonation;

/**
 * A node representing an intonation for multiplicatively adjusting the key of an input intonation by a pitch ratio.
 * @author tgreen
 *
 */
public class GMultiplicativeAdjust extends GIntonation implements Externalizable {
	
	/**
	 * The node for the input intonation.
	 */
	private GIntonation i1 = null;
	
	/**
	 * The multiplier for the pitch ratio.
	 */
	private double multiplier = 1.0;
	
	/**
	 * Whether to use the multiplicative inverse of the multiplier.
	 * This makes it possible to e.g. divide by 3 instead of typing 0.3333333...
	 */
	private boolean invertM = false;

	/**
	 * Constructs the node.
	 */
	public GMultiplicativeAdjust() {
		super();
	}

	@Override
	public Intonation genInton(HashMap s) {
		if( s.get(this) != null )
		{
			return( (Intonation)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		final double m = invertM ? 1.0 / multiplier : multiplier;
		
		Intonation w = new MultiplicativeAdjust( i1.genInton(s) , m );
		
		s.put(this, w);
		
		return( w );
	}

	@Override
	public String getName() {
		return( "MultiplicativeAdjust" );
	}
	
	@Override
	public String[] getScaleNames()
	{
		return( i1.getScaleNames() );
	}
	
	@Override
	public String[] getPriScaleNames()
	{
		return( i1.getPriScaleNames() );
	}
	
	@Override
	public GIntonation getBaseIntonation()
	{
		return( i1.getBaseIntonation() );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GIntonation );
	}

	@Override
	public void performAssign(GNode in) {
		if( i1 == null )
		{
			i1 = (GIntonation) in;
		}

	}
	
	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		MultiplicativeAdjustEditor editor = new MultiplicativeAdjustEditor( this );
		ProgramDirector.showPropertyEditor(editor, null,
			"MultiplicativeAdjust Properties");
	}

	@Override
	public void removeChld() {
		i1 = null;
	}

	public Object getChldNodes() {
		return( i1 );
	}
	
	/**
	 * Gets the input intonation.
	 * @return The input intonation.
	 */
	public GIntonation getI1() {
		return i1;
	}

	/**
	 * Sets the input intonation.
	 * @param i1 The input intonation.
	 */
	public void setI1(GIntonation i1) {
		this.i1 = i1;
	}

	
	/**
	 * Gets the multiplier for the pitch ratio.
	 * @return The multiplier for the pitch ratio.
	 */
	public double getMultiplier() {
		return multiplier;
	}

	/**
	 * Sets the multiplier for the pitch ratio.
	 * @param multiplier The multiplier for the pitch ratio.
	 */
	public void setMultiplier(double multiplier) {
		this.multiplier = multiplier;
	}

	/**
	 * Gets whether to use the multiplicative inverse of the multiplier.
	 * @return Whether to use the multiplicative inverse of the multiplier.
	 */
	public boolean isInvertM() {
		return invertM;
	}

	/**
	 * Sets whether to use the multiplicative inverse of the multiplier.
	 * @param invertM Whether to use the multiplicative inverse of the multiplier.
	 */
	public void setInvertM(boolean invertM) {
		this.invertM = invertM;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( i1 != null ) myv.setProperty("I1", i1);
		myv.setDouble("Multiplier", multiplier);
		myv.setBoolean("InvertM", invertM);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			i1 = (GIntonation)( myv.getProperty("I1") );
			multiplier = myv.getDouble("Multiplier");
			invertM = myv.getBoolean( "InvertM" );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	
}

