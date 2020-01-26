




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
import core.NonClampedCoefficient;
import core.PhaseDistortionPacket;

/**
 * Clamped coefficient performing phase distortion on an input clamped coefficient (i.e. it phase-distorts an input clamped coefficient).
 * 
 * See:  https://en.wikipedia.org/wiki/Phase_distortion_synthesis
 * 
 * @author tgreen
 *
 */
public class PhaseDistortionClampedCoefficient extends ClampedCoefficient implements Externalizable {

	/**
	 * The phase distortions to apply to the input clamped coefficient.
	 */
	protected PhaseDistortionPacket[] packets = null;

	/**
	 * The input clamped coefficient.
	 */
	protected ClampedCoefficient coeff = null;

	/**
	 * Constructs the clamped coefficient.
	 * @param _packets The phase distortions to apply to the input clamped coefficient.
	 * @param _coeff The input clamped coefficient.
	 */
	public PhaseDistortionClampedCoefficient(PhaseDistortionPacket[] _packets,
			ClampedCoefficient _coeff) {
		super();
		if (_packets == null) {
			_packets = new PhaseDistortionPacket[0];
		}
		packets = _packets;
		coeff = _coeff;
	}
	
	/**
	 * Constructor used for persistence purposes only.
	 *
	 */
	public PhaseDistortionClampedCoefficient()
	{
		
	}
	
	@Override
	public NonClampedCoefficient genClone() throws Throwable
	{
		final ClampedCoefficient cl = (ClampedCoefficient)( coeff.genClone() );
		final int len = packets.length;
		final PhaseDistortionPacket[] pk = new PhaseDistortionPacket[ len ];
		int count;
		for( count = 0 ; count < len ; count++ )
		{
			pk[ count ] = packets[ count ].genClone();
		}
		boolean match = true;
		count = 0;
		while( ( count < len ) && match )
		{
			match = match && ( pk[ count ] == packets[ count ] );
			count++;
		}
		if( match && ( cl == coeff ) )
		{
			return( this );
		}
		else
		{
			return( new PhaseDistortionClampedCoefficient( pk , cl ) );
		}
	}
	
	@Override
	public GClampedCoefficient genClamped( HashMap<Object,GNode> s )
	{
		if( s.get( this ) != null )
		{
			return( (GClampedCoefficient)( s.get( this ) ) );
		}
		
		GPhaseDistortionClampedCoefficient wv = new GPhaseDistortionClampedCoefficient();
		s.put(this, wv);
		
		GClampedCoefficient w = coeff.genClamped(s);
		
		Vector<GPhaseDistortionPacket> pks = new Vector<GPhaseDistortionPacket>();
		int cnt;
		final int len = packets.length;
		for( cnt = 0 ; cnt < len ; cnt++ )
		{
			pks.add( packets[ cnt ].genPhase(s) );
		}
		
		wv.load(w,pks);
		
		return( wv );
	}

	/**
	 * Calculates the amount of phase distortion to be applied.
	 * @param packets The phase distortions to apply to the input clamped coefficient.
	 * @param non_phase_distorted_param The parameter value at which to evaluate.
	 * @return The amount of phase distortion to be applied.
	 */
	protected static double calcPhaseDistortedParam(
			PhaseDistortionPacket[] packets, double non_phase_distorted_param) {
		double param = non_phase_distorted_param;
		int len = packets.length;
		int count;
		for (count = 0; count < len; count++) {
			PhaseDistortionPacket packet = packets[count];
			double delta = (packet.getAmplitude())
					* packet.getWaveform().eval(
							(packet.getFrequencyMultiplier())
									* non_phase_distorted_param);
			param += delta;
		}
		return (param);
	}

	@Override
	public double eval(final double non_phase_distorted_param) {
		double param = calcPhaseDistortedParam(packets,
				non_phase_distorted_param);
		return (coeff.eval(param));
	}
	
	/**
	 * Reads the node from serial storage.
	 */
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			coeff = (ClampedCoefficient) (myv.getPropertyEx("Coeff"));
			int plen = myv.getInt("PacketSize");
			packets = new PhaseDistortionPacket[plen];
			int count;
			for (count = 0; count < plen; count++) {
				packets[count] = (PhaseDistortionPacket) (myv
						.getPropertyEx("Packet_" + count));
			}
		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	/**
	 * Writes the node to serial storage.
	 * 
	 * @serialData TBD.
	 */
	public void writeExternal(ObjectOutput out) throws IOException {
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setProperty("Coeff", coeff);
		myv.setInt("PacketSize", packets.length);
		int plen = packets.length;
		int count;
		for (count = 0; count < plen; count++) {
			myv.setProperty("Packet_" + count, packets[count]);
		}

		out.writeObject(myv);
	}

}
