





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







package gcoeff;

import gredit.GNode;
import greditcoeff.GNonClampedCoefficientMultiCore;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import verdantium.ProgramDirector;
import core.NonClampedCoefficientMultiCore;

/**
 * A node representing a simple (not necessarily good) low-pass filter.
 * 
 * The evaluation interval is basically the inverse of the cutoff frequency for the filter.
 * 
 * @author tgreen
 *
 */
public class GLowPassFilter  extends GNonClampedCoefficientMultiCore implements Externalizable {
	
	/**
	 * The wave to be filtered.
	 */
	GNonClampedCoefficientMultiCore chld;
	
	/**
	 * The evaluation interval half-length.
	 */
	protected double intervalHalfLength = 1.0 / 3.2;
	
	/**
	 * The number of sampling points to use in the filtering.
	 */
	protected int sampleLen = 3;
	
	/**
	 * Default constructor.
	 */
	public GLowPassFilter()
	{
	}

	@Override
	public NonClampedCoefficientMultiCore genCoeff(HashMap s) {
		if( s.get(this) != null )
		{
			return( (NonClampedCoefficientMultiCore)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		NonClampedCoefficientMultiCore w = chld.genCoeff(s);
		
		NonClampedCoefficientMultiCore wv = new LowPassFilterCoeffMultiCore(w, intervalHalfLength, sampleLen);
		s.put(this, wv);
		
		return( wv );
	}

	public Object getChldNodes() {
		return( chld );
	}

	@Override
	public String getName() {
		return( "Low-Pass Filter" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GNonClampedCoefficientMultiCore );
	}

	@Override
	public void performAssign(GNode in) {
		chld = (GNonClampedCoefficientMultiCore) in;

	}

	@Override
	public void removeChld() {
		chld = null;
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		LowPassFilterEditor editor = new LowPassFilterEditor( this );
		ProgramDirector.showPropertyEditor(editor, null,
			"Low-Pass Filter Properties");
	}

	/**
	 * Gets the evaluation interval half-length.
	 * @return The evaluation interval half-length.
	 */
	public double getIntervalHalfLength() {
		return intervalHalfLength;
	}

	/**
	 * Sets the evaluation interval half-length.
	 * @param intervalHalfLength The evaluation interval half-length.
	 */
	public void setIntervalHalfLength(double intervalHalfLength) {
		this.intervalHalfLength = intervalHalfLength;
	}

	/**
	 * Gets the number of sampling points to use in the filtering.
	 * @return The number of sampling points to use in the filtering.
	 */
	public int getSampleLen() {
		return sampleLen;
	}

	/**
	 * Sets the number of sampling points to use in the filtering.
	 * @param sampleLen The number of sampling points to use in the filtering.
	 */
	public void setSampleLen(int sampleLen) {
		this.sampleLen = sampleLen;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( chld != null ) myv.setProperty("Chld", chld);
		myv.setDouble("IntervalHalfLength", intervalHalfLength);
		myv.setInt("SampleLen", sampleLen);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			chld = (GNonClampedCoefficientMultiCore)( myv.getProperty("Chld") );
			intervalHalfLength = myv.getDouble("IntervalHalfLength");
			sampleLen = myv.getInt("SampleLen");

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}
