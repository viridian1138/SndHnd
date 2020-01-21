





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
 * As suggested in Section 18 of "Computer Graphics Principles and Practice, Third Edition" by
 * John F. Hughes et. al., sampling at a Nyquist frequency is necessary but
 * not necessarily sufficient to reproduce a particular wave.  In fact, a wave
 * sampled at the Nyquist frequency can still be severely aliased.  This 
 * is a node representing a sampler class that
 * makes small adjustments to the phase of the input wave to attempt to 
 * capture high-frequency harmonics up to the Nyquist limit.
 * 
 * MicroPhaseAdjustmentCv uses a uses a aggregated estimated value for
 * a critical point, whereas MicroPhaseAdjustment uses a single sample
 * collected at the estimated parameter of the critical point.
 * 
 * @author thorngreen
 *
 */
public class GMicroPhaseAdjustmentCv  extends GNonClampedCoefficientMultiCore implements Externalizable {
	
	/**
	 * The input wave to be sampled.
	 */
	GNonClampedCoefficientMultiCore chld;
	
	/**
	 * The number of samples to be used to determine the phase.
	 */
	protected int sampleLen = 3;
	
	/**
	 * Default constructor.
	 */
	public GMicroPhaseAdjustmentCv()
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
		
		NonClampedCoefficientMultiCore wv = new MicroPhaseAdjustmentCv(w, sampleLen);
		s.put(this, wv);
		
		return( wv );
	}

	public Object getChldNodes() {
		return( chld );
	}

	@Override
	public String getName() {
		return( "MicroPhase AdjustmentCv" );
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
		MicroPhaseAdjustmentCvEditor editor = new MicroPhaseAdjustmentCvEditor( this );
		ProgramDirector.showPropertyEditor(editor, null,
			"Micro Phase Adjustment Cv Properties");
	}

	/**
	 * Gets The number of samples to be used to determine the phase.
	 * @return The number of samples to be used to determine the phase.
	 */
	public int getSampleLen() {
		return sampleLen;
	}

	/**
	 * Sets the number of samples to be used to determine the phase.
	 * @param sampleLen The number of samples to be used to determine the phase.
	 */
	public void setSampleLen(int sampleLen) {
		this.sampleLen = sampleLen;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( chld != null ) myv.setProperty("Chld", chld);
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
			sampleLen = myv.getInt("SampleLen");

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}
