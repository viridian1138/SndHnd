




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
import gredit.GPhaseDistortionPacket;
import gredit.GWaveForm;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Vector;

import core.NonClampedCoefficient;
import core.PhaseDistortionPacket;
import core.WaveForm;

import meta.DataFormatException;
import meta.VersionBuffer;

/**
 * Waveform, based on a theory that strings such as guitar strings at least sometimes oscillate around a chaotic attractor in phase space, that uses Newtonian basins as a mechanism for generating chaos.
 * 
 * The Newton algorithm takes a function y = f(x), and attempts to generate an inverse solution for the x where y = 0.  As opposed to generating accurate inverses, the goal of this implementation is to produce forms of chaos with particular timbres.
 * 
 * See:  https://mathcs.clarku.edu/~djoyce/newton/examples.html
 * 
 * This is a simpler version of class Inverter that only uses a single packet.
 * 
 * This WaveForm is not guaranteed to return values in [0,1].
 * 
 * @author thorngreen
 * 
 */
public class SinglePacketInverter extends WaveForm implements Externalizable {

	/**
	 * The phase distortion packet defining the inversion.
	 */
	PhaseDistortionPacket packet;

	/**
	 * The size of the delta in along the parameter axis used for the discretized estimation of the derivative.  The delta is intentionally somewhat coarse to generate chaotic behavior in the Newtonian basins.
	 */
	protected final double slopeDelta = 1E-5;

	/**
	 * Not used.
	 */
	protected final double outputMultiplier = 1.0;

	/**
	 * Constructs the waveform.
	 * @param _ipacket The phase distortion packet defining the inversion.
	 */
	public SinglePacketInverter(PhaseDistortionPacket _ipacket) {
		super();
		packet = _ipacket;
	}
	
	/**
	 * Constructor used for persistence purposes only.
	 *
	 */
	public SinglePacketInverter()
	{
	}
	
	@Override
	public NonClampedCoefficient genClone() throws Throwable
	{
		final PhaseDistortionPacket ph = packet.genClone();
		if( ph == packet )
		{
			return( this );
		}
		else
		{
			return( new SinglePacketInverter( ph ) );
		}
	}
	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GSinglePacketInverter wv = new GSinglePacketInverter();
		s.put(this, wv);
		
		GPhaseDistortionPacket pa = packet.genPhase(s);
		
		wv.load(pa);
		
		return( wv );
	}

	/**
	 * Calculates the value of the function as part of generating a Newtonian basin.
	 * @param param The parameter value at which to estimate the derivative.
	 * @return The function value.
	 */
	protected double evalFun(double param) {
		return (PhaseDistortionWaveForm.calcPhaseDistortedParam(packet, param));
	}

	/**
	 * Calculates the numerical derivative of the function as part of generating a Newtonian basin.
	 * @param param The parameter value at which to estimate the derivative.
	 * @return The estimated derivative.
	 */
	protected double evalFunDeriv(double param) {
		double v1 = evalFun(param);
		double v2 = evalFun(param + slopeDelta);
		double ret = (v2 - v1) / slopeDelta;
		return (ret);
	}

	/**
	 * Performs one Newton iteration toward generating a Newtonian basin.
	 * @param param The previous estimate for the inverse.
	 * @return The new estimate for the inverse.
	 */
	protected double estimateSoln(double param) {
		double fun = evalFun(param);
		// double fun = evalFun(param) - param;
		double deriv = evalFunDeriv(param);
		// double deriv = evalFunDeriv(param) - 1.0;
		double soln = param - (fun - param) / deriv;
		// double soln = param - fun / deriv;
		return (soln);
	}

	/**
	 * Performs Newton iterations toward generating a Newtonian basin.
	 * @param param The initial guess for the inverse.
	 * @return The estimate for the inverse.
	 */
	protected double findSoln(double param) {
		double pPrev = -1E+6;
		double p = param;
		int count = 0;
		while ((Math.abs(p - pPrev) > 1E-6) && (count < 20)) {
			pPrev = p;
			p = estimateSoln(p);
			count++;
		}
		return (p);
	}

	@Override
	public double eval(double phase_distorted_param) {
		double deltaP = phase_distorted_param - findSoln(phase_distorted_param);
		double deltaP2 = 0.5 * ( deltaP + 1.0 );
		double remainder = deltaP2 - (int) deltaP2;
		double ret = 2.0 * remainder - 1.0;
		// double ret = outputMultiplier * (deltaP - (int) deltaP);
		return (ret);
	}

	/**
	 * Reads the node from serial storage.
	 */
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			//slopeDelta = myv.getDouble("SlopeDelta");
			//outputMultiplier = myv.getDouble("OutputMultiplier");

			packet = (PhaseDistortionPacket) (myv
					.getPropertyEx("Packet"));
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

		//myv.setDouble("SlopeDelta", slopeDelta);
		//myv.setDouble("OutputMultiplier", outputMultiplier);

		myv.setProperty("Packet", packet);
		
		//myv.setInt("PacketSize", packets.length);
		//int plen = packets.length;
		//int count;
		//for (count = 0; count < plen; count++) {
		//	myv.setProperty("Packet_" + count, packets[count]);
		//}

		out.writeObject(myv);
	}

}
