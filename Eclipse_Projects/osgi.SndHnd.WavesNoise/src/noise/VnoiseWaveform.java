





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







package noise;

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
 * A waveform for Value Noise as described in the book "Texturing and Modeling" by David S. Ebert et. al.
 * @author thorngreen
 *
 */
public class VnoiseWaveform extends WaveForm implements Externalizable {
	
	protected static final double CR00 = -0.5;
	protected static final double CR01 = 1.5;
	protected static final double CR02 = -1.5;
	protected static final double CR03 = 0.5;
	protected static final double CR10 = 1.0;
	protected static final double CR11 = -2.5;
	protected static final double CR12 = 2.0;
	protected static final double CR13 = -0.5;
	protected static final double CR20 = -0.5;
	protected static final double CR21 = 0.0;
	protected static final double CR22 = 0.5;
	protected static final double CR23 = 0.0;
	protected static final double CR30 = 0.0;
	protected static final double CR31 = 1.0;
	protected static final double CR32 = 0.0;
	protected static final double CR33 = 0.0;
	
	
	/**
	 * Returns a value clamped between two other values.
	 * @param x The value to be clamped.
	 * @param a The low-end clamp.
	 * @param b The high-end clamp.
	 * @return The clamped result.
	 */
	public static double clamp(double x , double a, double b)
	{
		return( x < a ? a : ( x > b ? b : x ) );
	}
	
	
	/**
	 * Computes a point on a spline.
	 * @param x The domain value at which to evaluate.
	 * @param nknots The number of knots defining the spline.
	 * @param knot  The knots defining the spline.
	 * @return The calculated point on the spline.
	 */
	public static double spline( double x , int nknots , double[] knot )
	{
		int knotindex = 0;
		int span;
		int nspans = nknots - 3;
		double c0, c1, c2, c3;
		if( nspans < 1 )
		{
			throw( new RuntimeException( "Inconsistent" ) );
		}
		x = clamp(x, 0, 1) * nspans;
		span = (int) x;
		if( span >= nknots - 3 )
			span = nknots - 3;
		x -= span;
		knotindex += span;
		
		c3= CR00*knot[knotindex+0] + CR01*knot[knotindex+1] * CR02*knot[knotindex+2] + CR03*knot[knotindex+3];
		c2= CR10*knot[knotindex+0] + CR11*knot[knotindex+1] * CR12*knot[knotindex+2] + CR13*knot[knotindex+3];
		c1= CR20*knot[knotindex+0] + CR21*knot[knotindex+1] * CR22*knot[knotindex+2] + CR23*knot[knotindex+3];
		c0= CR30*knot[knotindex+0] + CR31*knot[knotindex+1] * CR32*knot[knotindex+2] + CR33*knot[knotindex+3];
		
		return( ( (c3 * x + c2) + c1 ) * x + c0 );
	}
	
	/**
	 * Returns the floor of the input value.
	 * @param x The input value.
	 * @return The floor of the input value.
	 */
	public static int floor( double x )
	{
		int ret = (int) x;
		if( x < 0 && ( x != (int)x ) )
			ret--;
		return( ret );
	}
	
	/**
	 * Input noise from which to generate the Value Noise.
	 */
	protected WaveForm inoise;
	
	/**
	 * The size of the interval over which to define the evaluation interval of the noise function.
	 */
	protected double sz;
	
	/**
	 * Storage for the set of knots involved in the noise calculation.
	 */
	protected final double[] knots = new double[ 4 ];

	/**
	 * Constructs the waveform.
	 * @param _inoise  Input noise from which to generate the Value Noise.
	 * @param _sz  The size of the interval over which to define the evaluation interval of the noise function.
	 */
	public VnoiseWaveform( WaveForm _inoise , double _sz ) {
		super();
		inoise = _inoise;
		sz = _sz;
	}
	
	
	@Override
	public double eval(double p) {
		double psz = p / sz;
		int ix = floor( psz );
		double fx = Math.abs( psz - ix );
		int i;
		for( i = -1 ; i <= 2 ; i++ )
		{
			knots[ i + 1 ] = inoise.eval( ( ix + i ) * sz );
		}
		return( spline(fx, 4 , knots) );
		
	}

	
	@Override
	public NonClampedCoefficient genClone() throws Throwable
	{
		return( new VnoiseWaveform( (WaveForm)( inoise.genClone() ) , sz ) );
	}
	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GVnoiseWaveform wv = new GVnoiseWaveform();
		s.put(this, wv);
		
		GWaveForm w = inoise.genWave(s);
		
		wv.load(w,sz);
		
		return( wv );
	}
	
	
	/**
	* Reads the node from serial storage.
	*/
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			// atk = myv.getDouble( "Atk" ); !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
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
		
		// myv.setDouble( "Atk" , atk ); !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

		out.writeObject(myv);
	}

	
}


