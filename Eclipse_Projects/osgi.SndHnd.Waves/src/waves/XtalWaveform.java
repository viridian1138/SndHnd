





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
import java.util.HashMap;

import core.NonClampedCoefficient;
import core.WaveForm;
import cwaves.CosineWaveform;
import cwaves.SineWaveform;

public class XtalWaveform extends WaveForm {
	
	private SineWaveform sine = new SineWaveform();
	
	private CosineWaveform cosine = new CosineWaveform();
	
	private double offTime;
	
	private double freq;
	
	private double mx;
	private double my;
	
	private double m1;
	private double m2;
	private double m3;
	
	private double nx;
	private double ny;
	
	private double n1;
	private double n2;
	private double n3;
	
	private double imx;
	private double imy;
	
	private double inx;
	private double iny;
	

	public XtalWaveform( double[] wg , double offsetTime ) {
		offTime = offsetTime;
		freq = wg[ 0 ];
		double off0 = cosine.eval( offsetTime / freq );
		double off1 = sine.eval( offsetTime / freq );
		
		double mx  = wg[ 1 ];
		double my  = wg[ 2 ];
		this.imx = mx;
		this.imy = my;
		m1 = wg[ 3 ];
		m2 = wg[ 4 ];
		m3 = wg[ 5 ];
		
		double nx = wg[ 6 ];
		double ny = wg[ 7 ];
		this.inx = nx;
		this.iny = ny;
		n1 = wg[ 8 ];
		n2 = wg[ 9 ];
		n3 = wg[ 10 ];
		
		this.mx = mx * off0 - my * off1;
		this.my = mx * off1 + my * off0;
		
		this.nx = nx * off0 - ny * off1;
		this.ny = nx * off1 + ny * off0;
	}

	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GXtalWaveform wv = new GXtalWaveform();
		s.put(this, wv);
		
		wv.setOffTime(offTime);
		wv.setFreq(freq);
		wv.setMx(imx);
		wv.setMy(imy);
		wv.setM1(m1);
		wv.setM2(m2);
		wv.setM3(m3);
		wv.setNx(inx);
		wv.setNy(iny);
		wv.setN1(n1);
		wv.setN2(n2);
		wv.setN3(n3);
		
		return( wv );
	}

	public double eval(double p) {
		
		double dmx = m1;
		double dmy = m2 + m3 * ( p + offTime );
		double dmsq = dmx * dmx + dmy * dmy;
		
		double dnx = n1;
		double dny = n2 + n3 * ( p + offTime );
		double dnsq = dnx * dnx + dny * dny;
		
		if( Math.abs( dnsq ) < 1E-30 )
		{
			dnsq = 1E-30;
		}
		
		
		double kmx0 = ( dmx ) / dmsq;
		double kmy0 = - ( dmy ) / dmsq;
		
		double knx0 = ( dnx ) / dnsq;
		double kny0 = - ( dny ) / dnsq;
		
		double kmx1 = kmx0 * kmx0 - kmy0 * kmy0;
		double kmy1 = kmx0 * kmy0 + kmx0 * kmy0;
		
		double knx1 = knx0 * knx0 - kny0 * kny0;
		double kny1 = knx0 * kny0 + knx0 * kny0;
		
		
		double kmx = kmx0 * ( 2.0 * Math.PI * freq ) - kmx1;
		double kmy = kmy0 * ( 2.0 * Math.PI * freq ) - kmy1;
		
		double knx = knx0 * ( 2.0 * Math.PI * freq ) - knx1;
		double kny = kny0 * ( 2.0 * Math.PI * freq ) - kny1;
		
		
		double multxm = kmx * mx - kmy * my;
		double multym = kmx * my + kmy * mx;
		
		
		double multxn = knx * nx - kny * ny;
		double multyn = knx * ny + kny * nx;
		
		
		double multx = ( multxm + multxn ) * ( p + offTime );
		double multy = ( multym + multyn ) * ( p + offTime );
		
		
		double wx = cosine.eval( freq * p );
		double wy = sine.eval( freq * p );
		
		double ret = multx * wx - multy * wy;
		
		//ret = Math.min( ret , 1E+2 * m1 );
		//ret = Math.max( ret , -1E+2 * m1 );
		
		return( ret );
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

