




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
 * This is a simpler version of class GInverter that only uses a single packet.
 * 
 * This WaveForm is not guaranteed to return values in [0,1].
 * 
 * @author thorngreen
 * 
 */
public class GSinglePacketInverter extends GWaveForm implements Externalizable {
	
	/**
	 * The phase distortion packet defining the inversion.
	 */
	GPhaseDistortionPacket packet;
	
	/**
	 * Constructs the node.
	 */
	public GSinglePacketInverter()
	{
	}

	@Override
	public WaveForm genWave(HashMap s) {
		if( s.get(this) != null )
		{
			return( (WaveForm)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		PhaseDistortionPacket pb = packet.genPhase(s);
		
		SinglePacketInverter iv = new SinglePacketInverter( pb );
		
		s.put(this, iv);
		
		return( iv );
	}
	
	/**
	 * Loads new values into the node.
	 * @param in The phase distortion packet defining the inversion.
	 */
	public void load( GPhaseDistortionPacket in )
	{
		packet = in;
	}

	public Object getChldNodes() {
		return( packet );
	}

	@Override
	public String getName() {
		return( "SinglePacketInverter" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GPhaseDistortionPacket );
	}

	@Override
	public void performAssign(GNode in) {
		packet = (GPhaseDistortionPacket) in;
	}

	@Override
	public void removeChld() {
		packet = null;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( packet != null ) myv.setProperty("Packet", packet);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			packet = (GPhaseDistortionPacket)( myv.getProperty("Packet") );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}

