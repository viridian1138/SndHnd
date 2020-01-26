




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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import verdantium.ProgramDirector;
import core.InstrumentTrack;
import core.NonClampedCoefficient;
import core.WaveForm;

/**
 * Node representing a non-clamped coefficient implementing a low-pass filter on an input non-clamped coefficient.
 * 
 * Note: this is still a work in progress.
 * 
 * See:  https://beausievers.com/synth/synthbasics/#lowpasshighpass
 * 
 * @author tgreen
 *
 */
public class GLowPassFilterCoeff extends GNonClampedCoefficient implements Externalizable {
	
	/**
	 * The input non-clamped coefficient.
	 */
	protected GNonClampedCoefficient wave;
	
	/**
	 * The half-length of the interval (i.e. the periodicity/cutoff) to be filtered.
	 */
	protected double intervalHalfLength;
	
	/**
	 * The number of samples used to perform the filtering.
	 */
	protected int sampleLen;

	@Override
	public NonClampedCoefficient genCoeff(HashMap s) {
		if( s.get(this) != null )
		{
			return( (WaveForm)( s.get(this) ) );
		}
		
		s.put(this, new Integer(5));
		
		NonClampedCoefficient w = wave.genCoeff(s);
		
		LowPassFilterCoeff wv = new LowPassFilterCoeff( w , intervalHalfLength , sampleLen );
		s.put(this, wv);
		
		return( wv );
	}

	public Object getChldNodes() {
		return( wave );
	}

	@Override
	public String getName() {
		return( "LowPassFilterCoeff" );
	}

	@Override
	public boolean isAssignCompatible(GNode in) {
		return( in instanceof GNonClampedCoefficient );
	}

	@Override
	public void performAssign(GNode in) {
		wave = (GNonClampedCoefficient) in;
	}

	@Override
	public void removeChld() {
		wave = null;
	}
	
	/**
	 * Loads new values into the node.
	 * @param w The input non-clamped coefficient.
	 * @param i The half-length of the interval (i.e. the periodicity/cutoff) to be filtered.
	 * @param s The number of samples used to perform the filtering.
	 */
	public void load( GNonClampedCoefficient w , double i , int s )
	{
		wave = w;
		intervalHalfLength = i;
		sampleLen = s;
	}
	
	@Override
	public void editProperties( HashMap<String,Object> context )
	{
		InstrumentTrack track = (InstrumentTrack)( context.get( "InstrumentTrack" ) );
		LowPassFilterCoeffEditor editor = new LowPassFilterCoeffEditor( this , track );
		ProgramDirector.showPropertyEditor(editor, null,
			"Low Pass Filter Coeff Properties");
	}

	/**
	 * Gets the half-length of the interval (i.e. the periodicity/cutoff) to be filtered.
	 * @return The half-length of the interval (i.e. the periodicity/cutoff) to be filtered.
	 */
	public double getIntervalHalfLength() {
		return intervalHalfLength;
	}

	/**
	 * Sets the half-length of the interval (i.e. the periodicity/cutoff) to be filtered.
	 * @param intervalHalfLength The half-length of the interval (i.e. the periodicity/cutoff) to be filtered.
	 */
	public void setIntervalHalfLength(double intervalHalfLength) {
		this.intervalHalfLength = intervalHalfLength;
	}

	/**
	 * Gets the number of samples used to perform the filtering.
	 * @return The number of samples used to perform the filtering.
	 */
	public int getSampleLen() {
		return sampleLen;
	}

	/**
	 * Sets the number of samples used to perform the filtering.
	 * @param sampleLen The number of samples used to perform the filtering.
	 */
	public void setSampleLen(int sampleLen) {
		this.sampleLen = sampleLen;
	}
	
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		if( wave != null ) myv.setProperty("Wave", wave);
		myv.setDouble("IntervalHalfLength", intervalHalfLength);
		myv.setInt("SampleLen", sampleLen);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			wave = (GNonClampedCoefficient)( myv.getProperty("Wave") );
			intervalHalfLength = myv.getDouble( "IntervalHalfLength" );
			sampleLen = myv.getInt( "SampleLen" );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}

