





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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;

import core.NonClampedCoefficient;
import core.WaveForm;

/**
 * Waveform for a "piecewise polynomial" interpolating Barnsley fractal.  From Chapter 4 of the book "Fractals Everywhere" by Michael F. Barnsley.
 * 
 * @author thorngreen
 *
 */
public class PwpFractInterpWaveForm extends WaveForm {
	
	/**
	 * Nodes representing the points on the waveform to be interpolated.
	 */
	private ArrayList<FractInterpNode> nodes;
	
	/**
	 * The number of subdivision iterations over which to evaluate the fractal.
	 */
	private int iterCnt;
	
	/**
	 * Fractal linearly interpolated from the first X-Axis coordinate to produce the final eval.
	 */
	private FractInterpWaveForm f1;
	
	/**
	 * Fractal linearly interpolated from the last X-Axis coordinate to produce the final eval.
	 */
	private FractInterpWaveForm f2;
	
	/**
	 * Constructs the waveform.
	 * @param _nodes Nodes representing the points on the waveform to be interpolated.
	 * @param _iterCnt The number of subdivision iterations over which to evaluate the fractal.
	 */
	public PwpFractInterpWaveForm( ArrayList<FractInterpNode> _nodes , int _iterCnt )
	{
		nodes = _nodes;
		iterCnt = _iterCnt;
		
		ArrayList<FractInterpNode> nodes1 = new ArrayList<FractInterpNode>();
		ArrayList<FractInterpNode> nodes2 = new ArrayList<FractInterpNode>();
		
		final double x0 = nodes.get( 0 ).getX();
		final double xn = nodes.get( nodes.size() - 1 ).getX();
		
		int count;
		for( count = 0 ; count < nodes.size() - 2 ; count++ )
		{
			nodes1.add( nodes.get( count ).clone() );
			nodes2.add( nodes.get( count + 2 ).clone() );
		}
		
		
		nodes1.get( nodes.size() - 3 ).setX( xn );
		nodes2.get( 0 ).setX( x0 );
		
		
		f1 = new FractInterpWaveForm( nodes1 , iterCnt );
		f2 = new FractInterpWaveForm( nodes2 , iterCnt );
		
	}
	
	
	@Override
	public double eval(double p) {
		final double x0 = nodes.get( 0 ).getX();
		final double xn = nodes.get( nodes.size() - 1 ).getX();
		
		double u;
		if( p >= 0 )
		{
			u = p - (int) p;
		}
		else
		{
			u = (int) p - p;
		}
		
		return( ( 1 - u ) * ( f1.eval(p) ) + u * ( f2.eval(p) ) );
		
	}
	
	
	/**
	 * Gets the X-coordinate of the first interpolation point.
	 * @return The X-coordinate of the first interpolation point.
	 */
	public double getX0()
	{
		return( nodes.get( 0 ).getX() );
	}
	
	
	/**
	 * Gets the X-coordinate of the last interpolation point.
	 * @return The X-coordinate of the last interpolation point.
	 */
	public double getXN()
	{
		return( nodes.get( nodes.size() - 1 ).getX() );
	}
	

	@Override
	public NonClampedCoefficient genClone() throws Throwable
	{
		return( new PwpFractInterpWaveForm( nodes , iterCnt ) );
	}
	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GPwpFractInterpWaveForm wv = new GPwpFractInterpWaveForm();
		s.put(this, wv);
		
		wv.load(nodes, iterCnt);
		
		return( wv );
	}

	/**
	* Reads the node from serial storage.
	*/
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		// TODO Auto-generated method stub !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

	}

	/**
	* Writes the node to serial storage.
	* @serialData TBD.
	*/
	public void writeExternal(ObjectOutput out) throws IOException {
		// TODO Auto-generated method stub !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

	}

}
