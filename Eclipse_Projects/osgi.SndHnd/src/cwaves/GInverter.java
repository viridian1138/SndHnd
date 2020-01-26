




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
 * Node representing a waveform, based on a theory that strings such as guitar strings at least sometimes oscillate around a chaotic attractor in phase space, that uses Newtonian basins as a mechanism for generating chaos.
 * 
 * The Newton algorithm takes a function y = f(x), and attempts to generate an inverse solution for the x where y = 0.  As opposed to generating accurate inverses, the goal of this implementation is to produce forms of chaos with particular timbres.
 * 
 * See:  https://mathcs.clarku.edu/~djoyce/newton/examples.html
 * 
 * This WaveForm is not guaranteed to return values in [0,1].
 * 
 * @author thorngreen
 * 
 */
public class GInverter extends GWaveForm implements Externalizable {
	
	/**
	 * The set of phase distortion packets defining the inversion.
	 */
	protected Vector<GPhaseDistortionPacket> gphase = new Vector<GPhaseDistortionPacket>();
	
	/**
	 * Constructs the node.
	 */
	public GInverter()
	{
	}

	@Override
	public WaveForm genWave(HashMap s) {
		if( s.get(this) != null )
		{
			return( (WaveForm)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		PhaseDistortionPacket[] pk = new PhaseDistortionPacket[ gphase.size() ];
		int count;
		
		for( count = 0 ; count < gphase.size() ; count++ )
		{
			GPhaseDistortionPacket pa = gphase.elementAt( count );
			PhaseDistortionPacket pb = pa.genPhase(s);
			pk[ count ] = pb;
		}
		
		Inverter iv = new Inverter( pk );
		
		s.put(this, iv);
		
		return( iv );
	}
	
	/**
	 * Loads new values into the node.
	 * @param pks The set of phase distortion packets defining the inversion.
	 */
	public void load( Vector<GPhaseDistortionPacket> pks )
	{
		gphase = pks;
	}

	public Object getChldNodes() {
		return( gphase );
	}

	@Override
	public String getName() {
		return( "Inverter" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GPhaseDistortionPacket );
	}

	@Override
	public void performAssign(GNode in) {
		gphase.add( (GPhaseDistortionPacket) in );
	}

	@Override
	public void removeChld() {
		gphase.clear();
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setInt("PacketsSize", gphase.size());
		int plen = gphase.size();
		int count;
		for (count = 0; count < plen; count++) {
			myv.setProperty("Packet_" + count, gphase.get(count));
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
			
			int plen = myv.getInt("PacketsSize");
			gphase = new Vector<GPhaseDistortionPacket>(plen);
			int count;
			for (count = 0; count < plen; count++) {
				GPhaseDistortionPacket wave = (GPhaseDistortionPacket) (myv
						.getPropertyEx("Packet_" + count));
				gphase.add(wave);
			}
			

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	
}

