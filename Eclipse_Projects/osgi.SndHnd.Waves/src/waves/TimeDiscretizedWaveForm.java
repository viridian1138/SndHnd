





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







package waves;


import gredit.GWaveForm;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;

import core.NonClampedCoefficient;
import core.WaveForm;
import cwaves.OffsetWaveform;

import meta.DataFormatException;
import meta.VersionBuffer;

/**
 * Waveform that takes an input wave and discretizes the time to create more of a lo-fi effect.
 * 
 * @author tgreen
 *
 */
public class TimeDiscretizedWaveForm extends WaveForm {
	
	/**
	 * The input waveform.
	 */
	protected WaveForm in;
	
	/**
	 * The number of discretizations in the wave period.
	 */
	protected double db;

	/**
	 * Constructs the waveform.
	 * @param _in The input waveform.
	 * @param _db The number of discretizations in the wave period.
	 */
	public TimeDiscretizedWaveForm( WaveForm _in , double _db ) {
		in = _in;
		db = _db;
	}

	@Override
	public double eval(double param) {
		int mult = (int)( db * param );
		return( in.eval( mult / db ) );
	}
	
	@Override
	public NonClampedCoefficient genClone() throws Throwable
	{
		final WaveForm wv = (WaveForm)( in.genClone() );
		if( wv == in )
		{
			return( this );
		}
		else
		{
			return( new TimeDiscretizedWaveForm( wv , db ) );
		}
	}
	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GTimeDiscretizedWaveForm wv = new GTimeDiscretizedWaveForm();
		s.put(this, wv);
		
		GWaveForm w = in.genWave(s);
		
		wv.load(w,db);
		
		return( wv );
	}


	public void writeExternal(ObjectOutput out) throws IOException {
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setProperty("In", in);
		myv.setDouble("Db", db);

		out.writeObject(myv);
	}

	public void readExternal(ObjectInput ii) throws IOException,
			ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (ii.readObject());
			VersionBuffer.chkNul(myv);

			in = (WaveForm)( myv.getProperty( "In" ) );
			db = myv.getDouble( "Db" );
			
		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}

