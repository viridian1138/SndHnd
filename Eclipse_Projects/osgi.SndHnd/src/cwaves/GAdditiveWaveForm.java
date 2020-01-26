




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







package cwaves;


import gredit.GNode;
import gredit.GNonClampedCoefficient;
import gredit.GWaveForm;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import meta.DataFormatException;
import meta.VersionBuffer;
import core.NonClampedCoefficient;
import core.WaveForm;

/**
 * Node representing a waveform performing additive synthesis on a set of inputs.
 * 
 * See https://theproaudiofiles.com/what-is-additive-synthesis/
 * 
 * @author thorngreen
 *
 */
public class GAdditiveWaveForm extends GWaveForm implements Externalizable {
	
	/**
	 * The primary input waveform.
	 */
	GWaveForm primaryWaveForm;
	
	/**
	 * The coefficient to multiply by the primary input waveform.
	 */
	GNonClampedCoefficient primaryCoefficient;
	
	/**
	 * The packets to be added to the primary input waveform.
	 */
	Vector<GAdditivePacket> packets = new Vector<GAdditivePacket>();
	
	/**
	 * Constructs the node.
	 */
	public GAdditiveWaveForm()
	{
	}
	
	/**
	 * Loads new values into the node.
	 * @param _primaryWaveForm The primary input waveform.
	 * @param _primaryCoefficient The coefficient to multiply by the primary input waveform.
	 * @param _packets The packets to be added to the primary input waveform.
	 */
	public void load( GWaveForm _primaryWaveForm ,
			GNonClampedCoefficient _primaryCoefficient ,
			Vector<GAdditivePacket> _packets )
	{
		primaryWaveForm = _primaryWaveForm;
		primaryCoefficient = _primaryCoefficient;
		packets = _packets;
	}

	@Override
	public WaveForm genWave(HashMap s) {
		if( s.get(this) != null )
		{
			return( (WaveForm)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		WaveForm pri = primaryWaveForm.genWave(s);
		NonClampedCoefficient pric = primaryCoefficient.genCoeff(s);
		
		ArrayList<NonClampedCoefficient> coefficients = new ArrayList<NonClampedCoefficient>();
		ArrayList<NonClampedCoefficient> coefficientCoefficients = new ArrayList<NonClampedCoefficient>();
		ArrayList<NonClampedCoefficient> parameterCoefficients = new ArrayList<NonClampedCoefficient>();
		
		for( final GAdditivePacket packet : packets )
		{
			coefficients.add( packet.getCoefficient().genCoeff(s) );
			coefficientCoefficients.add( packet.getCoefficientCoefficient().genCoeff(s) );
			parameterCoefficients.add( packet.getParameterCoefficient().genCoeff(s) );
		}
		
		AdditiveWaveForm wv = new AdditiveWaveForm( pri , pric , coefficients , coefficientCoefficients , parameterCoefficients );
		s.put(this, wv);
		
		return( wv );
	}

	public Object getChldNodes() {
		Vector<GNode> v = new Vector<GNode>();
		v.add( primaryWaveForm );
		v.add( primaryCoefficient );
		for( final GAdditivePacket p : packets )
		{
			v.add( p );
		}
		return(v);
	}

	@Override
	public String getName() {
		return( "Additive" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( ( in instanceof GWaveForm ) || ( in instanceof GNonClampedCoefficient ) || ( in instanceof GAdditivePacket ) );
	}

	@Override
	public void performAssign(GNode in) {
		if( in instanceof GWaveForm )
		{
			if( primaryWaveForm == null )
			{
				primaryWaveForm = (GWaveForm) in;
			}
			else
			{
				primaryCoefficient = (GNonClampedCoefficient) in;
			}
			return;
		}
		if( in instanceof GNonClampedCoefficient )
		{
			primaryCoefficient = (GNonClampedCoefficient) in;
			return;
		}
		if( in instanceof GAdditivePacket )
		{
			packets.add( (GAdditivePacket) in );
		}

	}

	@Override
	public void removeChld() {
		primaryWaveForm = null;
		primaryCoefficient = null;
		packets.clear();
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( primaryWaveForm != null ) myv.setProperty("PrimaryWaveForm", primaryWaveForm);
		if( primaryCoefficient != null ) myv.setProperty("PrimaryCoefficient", primaryCoefficient);
		myv.setInt("PacketsSize", packets.size());
		int plen = packets.size();
		int count;
		for (count = 0; count < plen; count++) {
			myv.setProperty("Packet_" + count, packets.get(count));
		}

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			primaryWaveForm = (GWaveForm)( myv.getProperty("PrimaryWaveForm") );
			primaryCoefficient = (GNonClampedCoefficient)( myv.getProperty("PrimaryCoefficient") );
			int plen = myv.getInt("PacketsSize");
			packets = new Vector<GAdditivePacket>(plen);
			int count;
			for (count = 0; count < plen; count++) {
				GAdditivePacket wave = (GAdditivePacket) (myv
						.getPropertyEx("Packet_" + count));
				packets.add(wave);
			}

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}
