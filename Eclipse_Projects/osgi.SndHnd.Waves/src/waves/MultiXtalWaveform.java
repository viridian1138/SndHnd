





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

public class MultiXtalWaveform extends WaveForm {
	
	private XtalWaveform[] wv;

	public MultiXtalWaveform( ArrayList<XtalWaveform> lst ) {
		final int sz = lst.size();
		wv = new XtalWaveform[ sz ];
		int cnt;
		for( cnt = 0 ; cnt < sz ; cnt++ )
		{
			wv[ cnt ] = lst.get( cnt );
		}
	}

	public GWaveForm genWave(HashMap s) {
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GMultiXtalWaveform wvx = new GMultiXtalWaveform();
		s.put(this, wvx);
		
		final int sz = wv.length;
		int cnt;
		for( cnt = 0 ; cnt < sz ; cnt++ )
		{
			GXtalWaveform gx = (GXtalWaveform)( ( wv[ cnt ] ).genWave(s) );
			wvx.performAssign( gx );
		}
		
		return( wvx );
	}

	public double eval(double p) {
		final int sz = wv.length;
		int cnt;
		double sm = 0.0;
		for( cnt = 0 ; cnt < sz ; cnt++ )
		{
			sm += wv[ cnt ].eval(p);
		}
		return( sm );
	}

	public NonClampedCoefficient genClone() throws Throwable {
		return( this );
	}

	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		throw( new RuntimeException( "NotSupported" ) );
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		throw( new RuntimeException( "NotSupported" ) );
	}

}
