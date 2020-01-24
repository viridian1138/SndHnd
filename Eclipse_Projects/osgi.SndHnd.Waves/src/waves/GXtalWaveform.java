





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

import gredit.GZWaveBase;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import verdantium.ProgramDirector;
import core.InstrumentTrack;
import core.WaveForm;

public class GXtalWaveform extends GZWaveBase  implements Externalizable {
	
	private double offTime = 2.0;
	
	private double freq = 1.0;
	
	private double mx = -0.794968393055472;
	private double my = -0.05105815138383402;
	
	private double m1 = 2.149999572988001;
	private double m2 = 3.840718392126714;
	private double m3 = 6.283185307179586;
	
	private double nx = -0.8123554090744839;
	private double ny = -0.05485255267518204;
	
	private double n1 = -2.2410492705063394;
	private double n2 = 3.7001330489090027;
	private double n3 = 6.283185307179586;

	
	public WaveForm genWave(HashMap s) {
		if( s.get(this) != null )
		{
			return( (WaveForm)( s.get(this) ) );
		}
		
		double[] wg = new double[ 11 ];
		
		wg[ 0 ] = freq;
		
		wg[ 1 ] = mx;
		wg[ 2 ] = my;
		wg[ 3 ] = m1;
		wg[ 4 ] = m2;
		wg[ 5 ] = m3;
		
		wg[ 6 ] = nx;
		wg[ 7 ] = ny;

		wg[ 8 ] = n1;
		wg[ 9 ] = n2;
		wg[ 10 ] = n3;
		XtalWaveform xt = new XtalWaveform( wg , offTime );
		s.put(this,xt);
		
		return( xt );
	}

	public String getName() {
		return( "Xtal Waveform" );
	}
	
	public void editProperties( HashMap<String,Object> context )
	{
		InstrumentTrack track = (InstrumentTrack)( context.get( "InstrumentTrack" ) );
		XtalWaveformEditor editor = new XtalWaveformEditor( this , track );
		ProgramDirector.showPropertyEditor(editor, null,
			"Xtal Waveform Properties");
	}

	/**
	 * @return the offTime
	 */
	public double getOffTime() {
		return offTime;
	}

	/**
	 * @param offTime the offTime to set
	 */
	public void setOffTime(double offTime) {
		this.offTime = offTime;
	}

	/**
	 * @return the freq
	 */
	public double getFreq() {
		return freq;
	}

	/**
	 * @param freq the freq to set
	 */
	public void setFreq(double freq) {
		this.freq = freq;
	}

	/**
	 * @return the mx
	 */
	public double getMx() {
		return mx;
	}

	/**
	 * @param mx the mx to set
	 */
	public void setMx(double mx) {
		this.mx = mx;
	}

	/**
	 * @return the my
	 */
	public double getMy() {
		return my;
	}

	/**
	 * @param my the my to set
	 */
	public void setMy(double my) {
		this.my = my;
	}

	/**
	 * @return the m1
	 */
	public double getM1() {
		return m1;
	}

	/**
	 * @param m1 the m1 to set
	 */
	public void setM1(double m1) {
		this.m1 = m1;
	}

	/**
	 * @return the m2
	 */
	public double getM2() {
		return m2;
	}

	/**
	 * @param m2 the m2 to set
	 */
	public void setM2(double m2) {
		this.m2 = m2;
	}

	/**
	 * @return the m3
	 */
	public double getM3() {
		return m3;
	}

	/**
	 * @param m3 the m3 to set
	 */
	public void setM3(double m3) {
		this.m3 = m3;
	}

	/**
	 * @return the nx
	 */
	public double getNx() {
		return nx;
	}

	/**
	 * @param nx the nx to set
	 */
	public void setNx(double nx) {
		this.nx = nx;
	}

	/**
	 * @return the ny
	 */
	public double getNy() {
		return ny;
	}

	/**
	 * @param ny the ny to set
	 */
	public void setNy(double ny) {
		this.ny = ny;
	}

	/**
	 * @return the n1
	 */
	public double getN1() {
		return n1;
	}

	/**
	 * @param n1 the n1 to set
	 */
	public void setN1(double n1) {
		this.n1 = n1;
	}

	/**
	 * @return the n2
	 */
	public double getN2() {
		return n2;
	}

	/**
	 * @param n2 the n2 to set
	 */
	public void setN2(double n2) {
		this.n2 = n2;
	}

	/**
	 * @return the n3
	 */
	public double getN3() {
		return n3;
	}

	/**
	 * @param n3 the n3 to set
	 */
	public void setN3(double n3) {
		this.n3 = n3;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);
		
		myv.setDouble("Offtime", offTime);
		myv.setDouble("Freq", freq);

		myv.setDouble("Mx", mx);
		myv.setDouble("My", my);
		
		myv.setDouble("M1", m1);
		myv.setDouble("M2", m2);
		myv.setDouble("M3", m3);
		
		myv.setDouble("Nx", nx);
		myv.setDouble("Ny", ny);
		
		myv.setDouble("N1", n1);
		myv.setDouble("N2", n2);
		myv.setDouble("N3", n3);

		out.writeObject(myv);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);
			
			offTime = myv.getDouble("Offtime");
			freq = myv.getDouble("Freq");
			
			mx = myv.getDouble("Mx");
			my = myv.getDouble("My");
			
			m1 = myv.getDouble("M1");
			m2 = myv.getDouble("M2");
			m3 = myv.getDouble("M3");
			
			nx = myv.getDouble("Nx");
			ny = myv.getDouble("Ny");
			
			n1 = myv.getDouble("N1");
			n2 = myv.getDouble("N2");
			n3 = myv.getDouble("N3");
			

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}

