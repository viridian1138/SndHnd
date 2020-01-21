





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
import core.NonClampedCoefficientMultiCore;
import core.SongData;

/**
 * A node representing a coefficient that switches to a rough-draft instance of a wave depending on 
 * whether there has been a global setting indicating that the evaluation is a rough draft and
 * whether there has been a global setting indicating that distortion should be skipped for rough drafts.
 * 
 * @author tgreen
 *
 */
public class GRoughDraftSkipDistortion extends GNonClampedCoefficientMultiCore implements Externalizable {
	
	/**
	 * The full-up version of the wave.
	 */
	protected GNonClampedCoefficientMultiCore fullUpCoeff = null;
	
	/**
	 * The rough-draft version of the wave.
	 */
	protected GNonClampedCoefficientMultiCore roughDraftCoeff = null;
	
	
	/**
	 * Default constructor.
	 */
	public GRoughDraftSkipDistortion()
	{
		super();
	}
	
	/**
	 * Constructs the coefficient.
	 * @param _fullUpCoeff  The full-up version of the wave.
	 * @param _roughDraftCoeff  The rough-draft version of the wave.
	 */
	public GRoughDraftSkipDistortion( GNonClampedCoefficientMultiCore _fullUpCoeff , GNonClampedCoefficientMultiCore _roughDraftCoeff )
	{
		fullUpCoeff = _fullUpCoeff;
		roughDraftCoeff = _roughDraftCoeff;
	}


	@Override
	public NonClampedCoefficientMultiCore genCoeff(HashMap s) {
		if( s.get(this) != null )
		{
			return( (NonClampedCoefficientMultiCore)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		boolean fullUp = true;
		if( !( SongData.ROUGH_DRAFT_MODE && SongData.skipDistortionForRoughDraft ) )
		{
			fullUp = false;
		}
		
		NonClampedCoefficientMultiCore w = fullUp ? fullUpCoeff.genCoeff(s) : roughDraftCoeff.genCoeff(s);
		
		s.put(this, w);
		
		return( w );
	}

	@Override
	public String getName() {
		return( "Rough Draft Skip Distortion" );
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
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( fullUpCoeff != null ) myv.setProperty("FullUpCoeff", fullUpCoeff);
		if( roughDraftCoeff != null ) myv.setProperty("RoughDraftCoeff", roughDraftCoeff);

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

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}
