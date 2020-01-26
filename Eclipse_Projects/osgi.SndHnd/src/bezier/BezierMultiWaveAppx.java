




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







package bezier;


import gredit.GWaveForm;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import core.NonClampedCoefficient;
import core.WaveForm;

import meta.DataFormatException;
import meta.VersionBuffer;

/**
 * Waveform for a piecewise cubic Bezier curve intended to approximate a waveform using interpolation points sampled from the waveform, is periodic over an interval covering multiple waveform periods, and uses Fritsch-Carlson monotonicity constraints.
 * 
 * @author thorngreen
 *
 */
public class BezierMultiWaveAppx extends WaveForm implements Externalizable {
	
	/**
	 * Piecewise cubic Bezier curve intended to approximate a waveform using interpolation points sampled from the waveform, is periodic over an interval covering multiple waveform periods, and uses Fritsch-Carlson monotonicity constraints.
	 */
	protected PiecewiseCubicMonotoneBezierMultiWaveAppx bez = null;

	/**
	 * Constructor used for persistence purposes only.
	 */
	public BezierMultiWaveAppx() {
		super();
	}
	
	/**
	 * Constructs the waveform.
	 * @param _orig The original waveform from which to build the cubic Bezier approximation.
	 * @param _numWaves Number of unit periods over which to make the curve periodic.
	 * @param roughDraftBezSamplesPerWave The number of interpolation points to collect for each unit period.
	 */
	public BezierMultiWaveAppx( WaveForm _orig , int _numWaves , double roughDraftBezSamplesPerWave )
	{
		super();
		bez = new PiecewiseCubicMonotoneBezierMultiWaveAppx( _orig , _numWaves , roughDraftBezSamplesPerWave );
	}

	@Override
	public double eval(double param) {
		return( bez.eval( param ) );
	}
	
	@Override
	public NonClampedCoefficient genClone() throws Throwable
	{
		PiecewiseCubicMonotoneBezierMultiWaveAppx bz = bez.genCloneWave();
		if( bz == bez )
		{
			return( this );
		}
		else
		{
			BezierMultiWaveAppx ret = new BezierMultiWaveAppx();
			ret.bez = bz;
			return( ret );
		}
	}
	
	/**
	* Reads the node from serial storage.
	*/
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			bez = (PiecewiseCubicMonotoneBezierMultiWaveAppx)( myv.getProperty( "Bez" ) );
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
		
		myv.setProperty( "Bez" , bez );

		out.writeObject(myv);
	}

	/**
	 * Gets the piecewise cubic Bezier curve intended to approximate a waveform using interpolation points sampled from the waveform, is periodic over an interval covering multiple waveform periods, and uses Fritsch-Carlson monotonicity constraints.
	 * @return Piecewise cubic Bezier curve intended to approximate a waveform using interpolation points sampled from the waveform, is periodic over an interval covering multiple waveform periods, and uses Fritsch-Carlson monotonicity constraints.
	 */
	public PiecewiseCubicMonotoneBezierMultiWaveAppx getBez() {
		return bez;
	}

	/**
	 * Sets the piecewise cubic Bezier curve intended to approximate a waveform using interpolation points sampled from the waveform, is periodic over an interval covering multiple waveform periods, and uses Fritsch-Carlson monotonicity constraints.
	 * @param bez Piecewise cubic Bezier curve intended to approximate a waveform using interpolation points sampled from the waveform, is periodic over an interval covering multiple waveform periods, and uses Fritsch-Carlson monotonicity constraints.
	 */
	public void setBez(PiecewiseCubicMonotoneBezierMultiWaveAppx bez) {
		this.bez = bez;
	}

	@Override
	public GWaveForm genWave(HashMap s) {
		throw( new RuntimeException( "Not Supported For BezierMultiWaveAppx" ) );
	}

}

