





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

/**
 * A node representing a sampler for applying a simple reverb described by a global text file "reverb.txt".
 * 
 * @author tgreen
 *
 */
public class GReverb  extends GNonClampedCoefficientMultiCore implements Externalizable {
	
	/**
	 * The input coefficient onto which to apply the reverb.
	 */
	GNonClampedCoefficientMultiCore chld;
	
	/**
	 * Default constructor.
	 */
	public GReverb()
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
		
		NonClampedCoefficientMultiCore wv = null;
		try
		{
			wv = new ReverbCoefficient(w);
		}
		catch( Throwable ex )
		{
			ex.printStackTrace(System.out);
			throw( new RuntimeException() );
		}
		
		s.put(this, wv);
		
		return( wv );
	}

	public Object getChldNodes() {
		return( chld );
	}

	@Override
	public String getName() {
		return( "Reverb" );
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
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( chld != null ) myv.setProperty("Chld", chld);

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

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}
