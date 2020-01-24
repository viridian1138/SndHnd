





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

/**
 * Node representing a fractal waveform based on a Koch snowflake.  Adapted from the book "The Fractal Geometry of Nature" by Benoit Mandelbrot.
 * 
 * See <A href="https://en.wikipedia.org/wiki/Koch_snowflake">https://en.wikipedia.org/wiki/Koch_snowflake</A>
 * 
 * @author tgreen
 *
 */
public class GKochSnowflakeWaveform extends GZWaveBase  implements Externalizable {
	
	/**
	 * The proportion of the iteration edge at which to start the triangle for the next iteration.
	 */
	protected double cutStart;
	
	/**
	 * The proportion of the iteration edge at which to end the triangle for the next iteration.
	 */
	protected double cutEnd;
	
	/**
	 * Displacement at the high-parameter end of the triangle for the next iteration.
	 */
	protected double displacementMultiplierHi;
	
	/**
	 * Displacement at the low-parameter end of the triangle for the next iteration.
	 */
	protected double displacementMultiplierLo;
	
	/**
	 * The smallest allowed delta in the parameter before evaluation stops.
	 */
	protected double minDelta;
	
	/**
	 * Decade multiplier from the offset of one iteration to the offset of the next iteration.
	 */
	protected double decadeMultiplier = 0.95;
	

	@Override
	public WaveForm genWave(HashMap s) {
		if( s.get(this) != null )
		{
			return( (WaveForm)( s.get(this) ) );
		}
		
		KochSnowflakeWaveform wv = new KochSnowflakeWaveform(
				cutStart, cutEnd, displacementMultiplierHi , 
				displacementMultiplierLo , 
				minDelta , decadeMultiplier );
		s.put(this, wv);
		
		return( wv );
	}

	@Override
	public String getName() {
		return( "Koch Snowflake" );
	}
	
	/**
	 * Loads new values into the node.
	 * @param _cutStart The proportion of the iteration edge at which to start the triangle for the next iteration.
	 * @param _cutEnd The proportion of the iteration edge at which to end the triangle for the next iteration.
	 * @param _displacementMultiplierHi Displacement at the high-parameter end of the triangle for the next iteration.
	 * @param _displacementMultiplierLo Displacement at the low-parameter end of the triangle for the next iteration.
	 * @param _minDelta The smallest allowed delta in the parameter before evaluation stops.
	 * @param _decadeMultiplier Decade multiplier from the offset of one iteration to the offset of the next iteration.
	 */
	public void load(
			double _cutStart, double _cutEnd, double _displacementMultiplierHi , 
			double _displacementMultiplierLo , 
			double _minDelta , double _decadeMultiplier ) {
		cutStart = _cutStart;
		cutEnd = _cutEnd;
		displacementMultiplierHi = _displacementMultiplierHi;
		displacementMultiplierLo = _displacementMultiplierLo;
		minDelta = _minDelta;
		decadeMultiplier = _decadeMultiplier;
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		InstrumentTrack track = (InstrumentTrack)( context.get( "InstrumentTrack" ) );
		KochSnowflakeEditor editor = new KochSnowflakeEditor( this , track );
		ProgramDirector.showPropertyEditor(editor, null,
			"Koch Snowflake Properties");
	}

	/**
	 * Gets the proportion of the iteration edge at which to start the triangle for the next iteration.
	 * @return The proportion of the iteration edge at which to start the triangle for the next iteration.
	 */
	public double getCutStart() {
		return cutStart;
	}

	/**
	 * Sets the proportion of the iteration edge at which to start the triangle for the next iteration.
	 * @param cutStart The proportion of the iteration edge at which to start the triangle for the next iteration.
	 */
	public void setCutStart(double cutStart) {
		this.cutStart = cutStart;
	}

	/**
	 * Gets the proportion of the iteration edge at which to end the triangle for the next iteration.
	 * @return The proportion of the iteration edge at which to end the triangle for the next iteration.
	 */
	public double getCutEnd() {
		return cutEnd;
	}

	/**
	 * Sets the proportion of the iteration edge at which to end the triangle for the next iteration.
	 * @param cutEnd The proportion of the iteration edge at which to end the triangle for the next iteration.
	 */
	public void setCutEnd(double cutEnd) {
		this.cutEnd = cutEnd;
	}

	/**
	 * Gets the displacement at the high-parameter end of the triangle for the next iteration.
	 * @return Displacement at the high-parameter end of the triangle for the next iteration.
	 */
	public double getDisplacementMultiplierHi() {
		return displacementMultiplierHi;
	}

	/**
	 * Sets the displacement at the high-parameter end of the triangle for the next iteration.
	 * @param displacementMultiplierHi Displacement at the high-parameter end of the triangle for the next iteration.
	 */
	public void setDisplacementMultiplierHi(double displacementMultiplierHi) {
		this.displacementMultiplierHi = displacementMultiplierHi;
	}

	/**
	 * Gets the displacement at the low-parameter end of the triangle for the next iteration.
	 * @return Displacement at the low-parameter end of the triangle for the next iteration.
	 */
	public double getDisplacementMultiplierLo() {
		return displacementMultiplierLo;
	}

	/**
	 * Sets the displacement at the low-parameter end of the triangle for the next iteration.
	 * @param displacementMultiplierLo Displacement at the low-parameter end of the triangle for the next iteration.
	 */
	public void setDisplacementMultiplierLo(double displacementMultiplierLo) {
		this.displacementMultiplierLo = displacementMultiplierLo;
	}

	/**
	 * Gets the smallest allowed delta in the parameter before evaluation stops.
	 * @return The smallest allowed delta in the parameter before evaluation stops.
	 */
	public double getMinDelta() {
		return minDelta;
	}

	/**
	 * Sets the smallest allowed delta in the parameter before evaluation stops.
	 * @param minDelta The smallest allowed delta in the parameter before evaluation stops.
	 */
	public void setMinDelta(double minDelta) {
		this.minDelta = minDelta;
	}

	/**
	 * Gets the decade multiplier from the offset of one iteration to the offset of the next iteration.
	 * @return Decade multiplier from the offset of one iteration to the offset of the next iteration.
	 */
	public double getDecadeMultiplier() {
		return decadeMultiplier;
	}

	/**
	 * Sets the decade multiplier from the offset of one iteration to the offset of the next iteration.
	 * @param decadeMultiplier Decade multiplier from the offset of one iteration to the offset of the next iteration.
	 */
	public void setDecadeMultiplier(double decadeMultiplier) {
		this.decadeMultiplier = decadeMultiplier;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setDouble("CutStart",cutStart);
		myv.setDouble("CutEnd",cutEnd);
		myv.setDouble("DisplacementMultiplierHi",displacementMultiplierHi);
		myv.setDouble("DisplacementMultiplierLo",displacementMultiplierLo);
		myv.setDouble("MinDelta",minDelta);
		myv.setDouble("DecadeMultiplier",decadeMultiplier);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			cutStart = myv.getDouble("CutStart");
			cutEnd = myv.getDouble("CutEnd");
			displacementMultiplierHi = myv.getDouble("DisplacementMultiplierHi");
			displacementMultiplierLo = myv.getDouble("DisplacementMultiplierLo");
			minDelta = myv.getDouble("MinDelta");
			decadeMultiplier = myv.getDouble("DecadeMultiplier");

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	
}

