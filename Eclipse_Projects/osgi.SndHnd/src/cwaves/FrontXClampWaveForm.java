




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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;

import core.NonClampedCoefficient;
import core.WaveForm;

import meta.VersionBuffer;


/**
 * Waveform that takes an input waveform, and clamps the evaluation for parameter values less than zero to the evaluation of the parameter at zero.  That is to say, evaluation for parameter values less than zero is clamped to be a constant.
 * 
 * @author thorngreen
 *
 */
public class FrontXClampWaveForm extends WaveForm {
	
	
	/**
	 * The input waveform.
	 */
	protected WaveForm wave;
	
	/**
	 * Whether the instance has been initialized.
	 */
	protected boolean initialized = false;
	
	/**
	 * The value retrieved from evaluating the input waveform at zero.
	 */
	protected double val;

	
	/**
	 * Constructs the waveform.
	 * @param _wave The input waveform.
	 */
	public FrontXClampWaveForm( WaveForm _wave ) {
		super();
		wave = _wave;
	}

	@Override
	public double eval(double param) {
		if( param < 0.0 )
		{
			if( !initialized )
			{
				val = wave.eval( 0.0 );
				initialized = true;
			}
			return( val );
		}
		return( wave.eval(param));
	}
	
	@Override
	public NonClampedCoefficient genClone() throws Throwable
	{
		final WaveForm wv = (WaveForm)( wave.genClone() );
		if( wv == wave )
		{
			return( this );
		}
		else
		{
			return( new FrontXClampWaveForm( wv ) );
		}
	}

	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GFrontXClampWaveForm wv = new GFrontXClampWaveForm();
		s.put(this, wv);
		
		GWaveForm w = wave.genWave(s);
		
		wv.load(w);
		
		return( wv );
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	public void writeExternal(ObjectOutput in) throws IOException {
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);
		
		myv.setProperty( "wave" , wave );
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {		
		VersionBuffer myv = (VersionBuffer) (in.readObject());
		VersionBuffer.chkNul(myv);
		
		initialized = false;
		wave = (WaveForm)( myv.getPropertyEx( "wave" ) );
	}

}
