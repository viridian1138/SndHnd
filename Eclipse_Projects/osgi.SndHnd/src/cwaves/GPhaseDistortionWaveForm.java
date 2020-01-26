




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
import gredit.GPhaseDistortionPacket;
import gredit.GWaveForm;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Vector;

import meta.DataFormatException;
import meta.VersionBuffer;
import core.PhaseDistortionPacket;
import core.WaveForm;

/**
 * Node representing a waveform performing phase distortion synthesis on an input waveform (i.e. it phase-distorts an input waveform).
 * 
 * See:  https://en.wikipedia.org/wiki/Phase_distortion_synthesis
 * 
 * @author tgreen
 *
 */
public class GPhaseDistortionWaveForm extends GWaveForm implements Externalizable {
	
	/**
	 * The phase distortions to apply to the input waveform.
	 */
	protected Vector<GPhaseDistortionPacket> packets = new Vector<GPhaseDistortionPacket>();

	/**
	 * The input waveform.
	 */
	protected GWaveForm wave;

	@Override
	public WaveForm genWave(HashMap s) {
		if( s.get(this) != null )
		{
			return( (WaveForm)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		int sz = packets.size();
		int cnt;
		
		WaveForm w = wave.genWave(s);
		
		PhaseDistortionPacket[] pk = new PhaseDistortionPacket[ sz ];
		
		for( cnt = 0 ; cnt < sz ; cnt++ )
		{
			GPhaseDistortionPacket p = packets.get(cnt);
			pk[ cnt ] = p.genPhase(s);
		}
		
		PhaseDistortionWaveForm wv = new PhaseDistortionWaveForm( pk , w );
		s.put(this, wv);
		
		return( wv );
	}

	public Object getChldNodes() {
		Vector<GNode> v = new Vector<GNode>();
		v.add( wave );
		for( final GPhaseDistortionPacket p : packets )
		{
			v.add( p );
		}
		return( v );
	}

	@Override
	public String getName() {
		return( "PhaseDistortionWave" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( ( in instanceof GWaveForm ) || ( in instanceof GPhaseDistortionPacket ) );
	}

	@Override
	public void performAssign(GNode in) {
		if( in instanceof GWaveForm )
		{
			wave = (GWaveForm) in;
			return;
		}
		if( in instanceof GPhaseDistortionPacket )
		{
			packets.add( (GPhaseDistortionPacket) in );
		}
	}

	@Override
	public void removeChld() {
		packets.clear();
		wave = null;
	}
	
	/**
	 * Loads new values into the node.
	 * @param w The input waveform.
	 * @param p The phase distortions to apply to the input waveform.
	 */
	public void load( GWaveForm w , Vector<GPhaseDistortionPacket> p )
	{
		wave = w;
		packets = p;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setInt("PacketsSize", packets.size());
		int plen = packets.size();
		int count;
		for (count = 0; count < plen; count++) {
			myv.setProperty("Packet_" + count, packets.get(count));
		}
		if( wave != null ) myv.setProperty("Wave",wave);
		
		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);
			
			int plen = myv.getInt("PacketsSize");
			packets = new Vector<GPhaseDistortionPacket>(plen);
			int count;
			for (count = 0; count < plen; count++) {
				GPhaseDistortionPacket wave = (GPhaseDistortionPacket) (myv
						.getPropertyEx("Packet_" + count));
				packets.add(wave);
			}
			wave = (GWaveForm)( myv.getProperty( "Wave" ) );
			

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}
