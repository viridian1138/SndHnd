




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

import core.InterpolationPoint;
import core.NonClampedCoefficient;
import core.WaveForm;
import cwaves.GAnalogPhaseDistortionWaveForm;

import meta.DataFormatException;
import meta.VersionBuffer;

/**
 * Waveform for a piecewise cubic monotone unit-periodic Bezier curve.
 * 
 * @author thorngreen
 *
 */
public class BezierWaveform extends WaveForm implements Externalizable {
	
	/**
	 * Piecewise cubic monotone unit-periodic Bezier curve used to evaluate the waveform.
	 */
	protected PiecewiseCubicMonotoneUnitPeriodicBezier bez = new PiecewiseCubicMonotoneUnitPeriodicBezier();

	/**
	 * Constructor for persistence purposes only.
	 */
	public BezierWaveform() {
		super();
	}
	
	/**
	 * Constructs the waveform.
	 * @param in Not used.  Overloads the constructor parameters.
	 */
	public BezierWaveform( boolean in )
	{
		super();
		bez.getInterpolationPoints().clear();
		bez.getInterpolationPoints().add( new InterpolationPoint( 0.0 , 0.0 ) );
		bez.getInterpolationPoints().add( new InterpolationPoint( 0.25 , 1.0 ) );
		bez.getInterpolationPoints().add( new InterpolationPoint( 0.5 , 0.0 ) );
		bez.getInterpolationPoints().add( new InterpolationPoint( 0.75 , -1.0 ) );
		bez.updateAll();
	}

	@Override
	public double eval(double param) {
		return( bez.eval( param ) );
	}
	
	@Override
	public NonClampedCoefficient genClone() throws Throwable
	{
		PiecewiseCubicMonotoneUnitPeriodicBezier bz = bez.genCloneWave();
		if( bz == bez )
		{
			return( this );
		}
		else
		{
			BezierWaveform ret = new BezierWaveform();
			ret.bez = bz;
			return( ret );
		}
	}
	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GBezierWaveform wv = new GBezierWaveform();
		s.put(this, wv);
		
		GPiecewiseCubicMonotoneUnitPeriodicBezier p = bez.genBez(s);
		
		wv.load(p);
		
		return( wv );
	}
	
	/**
	* Reads the node from serial storage.
	*/
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			bez = (PiecewiseCubicMonotoneUnitPeriodicBezier)( myv.getProperty( "Bez" ) );
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
	 * Gets the piecewise cubic monotone unit-periodic Bezier curve used to evaluate the waveform.
	 * @return Piecewise cubic monotone unit-periodic Bezier curve used to evaluate the waveform.
	 */
	public PiecewiseCubicMonotoneUnitPeriodicBezier getBez() {
		return bez;
	}

	/**
	 * Sets the piecewise cubic monotone unit-periodic Bezier curve used to evaluate the waveform.
	 * @param bez Piecewise cubic monotone unit-periodic Bezier curve used to evaluate the waveform.
	 */
	public void setBez(PiecewiseCubicMonotoneUnitPeriodicBezier bez) {
		this.bez = bez;
	}

}

