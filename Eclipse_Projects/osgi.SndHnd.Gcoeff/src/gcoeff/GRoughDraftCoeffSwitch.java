





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
import core.InstrumentTrack;
import core.NonClampedCoefficientMultiCore;
import core.SongData;

/**
 * A node representing a coefficient that switches to a rough-draft instance of a wave depending on the extent to which
 * the evaluation has been globally declared to be a rough draft.
 * 
 * @author tgreen
 *
 */
public class GRoughDraftCoeffSwitch extends GNonClampedCoefficientMultiCore implements Externalizable {
	
	/**
	 * The full-up version of the wave.
	 */
	protected GNonClampedCoefficientMultiCore fullUpCoeff = null;
	
	/**
	 * The rough-draft version of the wave.
	 */
	protected GNonClampedCoefficientMultiCore roughDraftCoeff = null;
	
	/**
	 * The cutoff at which to switch to the rough draft version.
	 */
	protected double cutoffCoeff = 0.5;
	
	/**
	 * Default constructor.
	 */
	public GRoughDraftCoeffSwitch()
	{
		super();
	}
	
	/**
	 * Constructs the coefficient.
	 * @param _fullUpCoeff  The full-up version of the wave.
	 * @param _roughDraftCoeff  The rough-draft version of the wave.
	 */
	public GRoughDraftCoeffSwitch( GNonClampedCoefficientMultiCore _fullUpCoeff , GNonClampedCoefficientMultiCore _roughDraftCoeff )
	{
		fullUpCoeff = _fullUpCoeff;
		roughDraftCoeff = _roughDraftCoeff;
	}

	/**
	 * Gets the cutoff at which to switch to the rough draft version.
	 * @return The cutoff at which to switch to the rough draft version.
	 */
	public double getCutoffCoeff() {
		return cutoffCoeff;
	}

	/**
	 * Sets the cutoff at which to switch to the rough draft version.
	 * @param cutoffCoeff The cutoff at which to switch to the rough draft version.
	 */
	public void setCutoffCoeff(double cutoffCoeff) {
		this.cutoffCoeff = cutoffCoeff;
	}

	@Override
	public NonClampedCoefficientMultiCore genCoeff(HashMap s) {
		if( s.get(this) != null )
		{
			return( (NonClampedCoefficientMultiCore)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		NonClampedCoefficientMultiCore w = ( SongData.roughDraftWaveCoeff > cutoffCoeff ) ? fullUpCoeff.genCoeff(s) : roughDraftCoeff.genCoeff(s);
		
		s.put(this, w);
		
		return( w );
	}

	@Override
	public String getName() {
		return( "Rough Draft Coeffform Switch" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GNonClampedCoefficientMultiCore );
	}

	@Override
	public void performAssign(GNode in) {
		if( fullUpCoeff == null )
		{
			fullUpCoeff = (GNonClampedCoefficientMultiCore) in;
		}
		else
		{
			roughDraftCoeff = (GNonClampedCoefficientMultiCore) in;
		}

	}

	@Override
	public void removeChld() {
		fullUpCoeff = null;
		roughDraftCoeff = null;
	}

	
	public Object getChldNodes() {
		Object[] ob = { fullUpCoeff , roughDraftCoeff };
		return( ob );
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		InstrumentTrack track = (InstrumentTrack)( context.get( "InstrumentTrack" ) );
		RoughDraftCoeffSwitchEditor editor = new RoughDraftCoeffSwitchEditor( this , track );
		ProgramDirector.showPropertyEditor(editor, null,
			"Rough Draft Coeff Switch Properties");
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( fullUpCoeff != null ) myv.setProperty("FullUpCoeff", fullUpCoeff);
		if( roughDraftCoeff != null ) myv.setProperty("RoughDraftCoeff", roughDraftCoeff);
		myv.setDouble("CutoffCoeff",cutoffCoeff);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			fullUpCoeff = (GNonClampedCoefficientMultiCore)( myv.getProperty("FullUpCoeff") );
			roughDraftCoeff = (GNonClampedCoefficientMultiCore)( myv.getProperty("RoughDraftCoeff") );
			cutoffCoeff = myv.getDouble("CutoffCoeff");

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}
