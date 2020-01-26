




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
import gredit.GWaveForm;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import core.NonClampedCoefficient;
import core.WaveForm;

/**
 * A waveform consisting of piecewise exponential functions with exponentials rising from the trough to each peak.
 * 
 * @author tgreen
 *
 */
public class RollWaveform extends WaveForm implements Externalizable {
	
	/**
	 * The proportion into the wave period at which to put the trough.  The wave period has a peak on each end.
	 */
	protected double atk;
	
	/**
	 * Exponential decay rate going from the peak to a trough to the right of the peak.
	 */
	protected double powa;
	
	/**
	 * Exponential decay rate going from the peak to a trough to the left of the peak.
	 */
	protected double powb;
	
	/**
	 * Calculated divisor used to set the peak of the wave for a trough to the right of the peak.
	 */
	protected double iva;
	
	/**
	 * Calculated divisor used to set the peak of the wave for a trough to the left of the peak..
	 */
	protected double ivb;

	/**
	 * Constructs the waveform.
	 * @param _atk The proportion into the wave period at which to put the trough.  The wave period has a peak on each end.
	 * @param _powa Exponential decay rate going from the peak to a trough to the right of the peak.
	 * @param _powb Exponential decay rate going from the peak to a trough to the left of the peak.
	 */
	public RollWaveform( double _atk , double _powa , double _powb) {
		super();
		atk = _atk;
		powa = _powa;
		powb = _powb;
		iva = Math.pow( Math.E , - powa ) - 1.0;
		ivb = Math.pow( Math.E , - powb ) - 1.0;
	}
	
	@Override
	public double eval(double param) {
		return( param >= 0.0 ? evalPos( param ) : evalMinus( param ) );
	}

	/**
	 * Evaluates the waveform for positive parameter values.
	 * @param param The input parameter value.
	 * @return The evaluated waveform value.
	 */
	protected final double evalPos(double param) {
		int pval = (int) param;
		double pdelta = param - pval;
		if( pdelta < atk )
		{
			double dr = ( Math.pow( Math.E, - powa * pdelta / atk ) - 1.0 ) / iva;
			return( 2.0 * dr - 1.0 );
		}
		else
		{
			double dr = ( Math.pow( Math.E, - powb * ( pdelta - atk ) / ( 1.0 - atk ) ) - 1.0 ) / ivb;
			return( - 2.0 * dr + 1.0 );
		}
	}
	
	/**
	 * Evaluates the waveform for negative parameter values.
	 * @param param The input parameter value.
	 * @return The evaluated waveform value.
	 */
	protected final double evalMinus(double param) {
		int pval = (int) param;
		double pdelta = pval - param;
		if( pdelta < atk )
		{
			double dr = ( Math.pow( Math.E, - powa * pdelta / atk ) - 1.0 ) / iva;
			return( 2.0 * dr - 1.0 );
		}
		else
		{
			double dr = ( Math.pow( Math.E, - powb * ( pdelta - atk ) / ( 1.0 - atk ) ) - 1.0 ) / ivb;
			return( - 2.0 * dr + 1.0 );
		}
	}
	
	@Override
	public NonClampedCoefficient genClone() throws Throwable
	{
		return( this );
	}
	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GRollWaveform wv = new GRollWaveform();
		s.put(this, wv);
		
		wv.load(atk,powa,powb);
		
		return( wv );
	}
	
	
	/**
	* Reads the node from serial storage.
	*/
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			atk = myv.getDouble( "Atk" );
		}
		catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	/**
	* Writes the node to serial storage.
	* @serialData TBD.
	*/
	public void writeExternal(ObjectOutput out) throws IOException {
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);
		
		myv.setDouble( "Atk" , atk );
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

		out.writeObject(myv);
	}

}

