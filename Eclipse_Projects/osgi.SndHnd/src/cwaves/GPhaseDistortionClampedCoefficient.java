




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

import gredit.GClampedCoefficient;
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
import core.ClampedCoefficient;
import core.PhaseDistortionPacket;

/**
 * Node representing a clamped coefficient performing phase distortion on an input clamped coefficient (i.e. it phase-distorts an input clamped coefficient).
 * 
 * See:  https://en.wikipedia.org/wiki/Phase_distortion_synthesis
 * 
 * @author tgreen
 *
 */
public class GPhaseDistortionClampedCoefficient extends GClampedCoefficient implements Externalizable {
	
	/**
	 * The phase distortions to apply to the input clamped coefficient.
	 */
	protected Vector<GPhaseDistortionPacket> packets = new Vector<GPhaseDistortionPacket>();

	/**
	 * The input clamped coefficient.
	 */
	protected GClampedCoefficient coeff;

	@Override
	public ClampedCoefficient genClamped(HashMap s) {
		if( s.get(this) != null )
		{
			return( (ClampedCoefficient)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		int sz = packets.size();
		int cnt;
		
		ClampedCoefficient w = coeff.genClamped(s);
		
		PhaseDistortionPacket[] pk = new PhaseDistortionPacket[ sz ];
		
		for( cnt = 0 ; cnt < sz ; cnt++ )
		{
			GPhaseDistortionPacket p = packets.get(cnt);
			pk[ cnt ] = p.genPhase(s);
		}
		
		PhaseDistortionClampedCoefficient wv = new PhaseDistortionClampedCoefficient( pk , w );
		s.put(this, wv);
		
		return( wv );
	}

	public Object getChldNodes() {
		Vector<GNode> v = new Vector<GNode>();
		v.add( coeff );
		for( final GPhaseDistortionPacket p : packets )
		{
			v.add( p );
		}
		return( v );
	}

	@Override
	public String getName() {
		return( "PhaseDistortionClampedCoefficient" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( ( in instanceof GClampedCoefficient ) || ( in instanceof GPhaseDistortionPacket ) );
	}

	@Override
	public void performAssign(GNode in) {
		if( in instanceof GWaveForm )
		{
			coeff = (GClampedCoefficient) in;
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
		coeff = null;
	}
	
	/**
	 * Loads new values into the node.
	 * @param c The input clamped coefficient.
	 * @param p The phase distortions to apply to the input clamped coefficient.
	 */
	public void load( GClampedCoefficient c , Vector<GPhaseDistortionPacket> p )
	{
		coeff = c;
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
		if( coeff != null ) myv.setProperty("Coeff",coeff);
		
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
			coeff = (GClampedCoefficient)( myv.getProperty( "Coeff" ) );
			

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}
