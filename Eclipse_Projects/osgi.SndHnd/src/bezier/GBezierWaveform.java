




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







package bezier;

import gredit.GNode;
import gredit.GWaveForm;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import core.WaveForm;

/**
 * Node representing a waveform for a piecewise cubic monotone unit-periodic Bezier curve.
 * 
 * @author tgreen
 *
 */
public class GBezierWaveform extends GWaveForm implements Externalizable {
	
	/**
	 * Piecewise cubic Bezier curve for a wave that is periodic across the unit domain, and uses Fritsch-Carlson monotonicity constraints.
	 */
	protected GPiecewiseCubicMonotoneUnitPeriodicBezier bez;

	
	public Object getChldNodes() {
		return( bez );
	}

	@Override
	public String getName() {
		return( "Bezier" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GPiecewiseCubicMonotoneUnitPeriodicBezier );
	}

	@Override
	public void performAssign(GNode in) {
		bez = (GPiecewiseCubicMonotoneUnitPeriodicBezier) in;
	}

	@Override
	public void removeChld() {
		bez = null;
	}
	
	/**
	 * Loads new values into the node.
	 * @param in Piecewise cubic Bezier curve for a wave that is periodic across the unit domain, and uses Fritsch-Carlson monotonicity constraints.
	 */
	public void load( GPiecewiseCubicMonotoneUnitPeriodicBezier in )
	{
		bez = in;
	}
	
	@Override
	public WaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (WaveForm)( s.get( this ) ) );
		}
		
		BezierWaveform wv = new BezierWaveform();
		s.put(this, wv);
		
		PiecewiseCubicMonotoneUnitPeriodicBezier p = bez.genBez(s);
		
		wv.setBez( p );
		
		return( wv );
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( bez != null ) myv.setProperty("Bez", bez);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			bez = (GPiecewiseCubicMonotoneUnitPeriodicBezier)( myv.getProperty("Bez") );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}

