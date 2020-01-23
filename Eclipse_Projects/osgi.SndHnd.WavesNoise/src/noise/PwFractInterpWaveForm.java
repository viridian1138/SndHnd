





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
 * Waveform for a "piecewise linear" interpolating Barnsley fractal.  From Chapter 4 of the book "Fractals Everywhere" by Michael F. Barnsley.
 * 
 * @author thorngreen
 *
 */
public class PwFractInterpWaveForm extends WaveForm {
	
	/**
	 * Nodes representing the points on the waveform to be interpolated.
	 */
	private ArrayList<FractInterpNode> nodes;
	
	/**
	 * The number of subdivision iterations over which to evaluate the fractal.
	 */
	private int iterCnt;
	
	/**
	 * Fractals that are polynomial-interpolated to produce the final eval. 
	 */
	private PwpFractInterpWaveForm[] f1;
	
	/**
	 * The floating current index.  Essentially this is cache memory.
	 */
	private int curIndex = 0;
	
	
	/**
	 * Constructs the waveform.
	 * @param _nodes Nodes representing the points on the waveform to be interpolated.
	 * @param _iterCnt The number of subdivision iterations over which to evaluate the fractal.
	 */
	public PwFractInterpWaveForm( ArrayList<FractInterpNode> _nodes , int _iterCnt )
	{
		nodes = _nodes;
		iterCnt = _iterCnt;
		
		final int nsz = ( nodes.size() - 1 ) / 5;
		
		f1 = new PwpFractInterpWaveForm[ nsz ];
		
		int count;
		
		for( count = 0 ; count < nsz ; count++ )
		{
			ArrayList<FractInterpNode> nodes1 = new ArrayList<FractInterpNode>();
			final int base = 5 * count;
			
			int countA;
			for( countA = 0 ; countA < 6 ; countA++ )
			{
				nodes1.add( nodes.get( base + countA ).clone() );
			}
			
			f1[ count ] = new PwpFractInterpWaveForm( nodes1 , iterCnt );
		}
		
		
	}
	
	
	/**
	 * Gets the index into the interpolation point array given the x-axis parameter value.
	 * @param p The x-axis parameter value.
	 * @return The matching index.
	 */
	private int getIndex( double p )
	{
		if( ( p >= f1[ curIndex ].getX0() ) && ( p <= f1[ curIndex ].getXN() ) )
		{
			return( curIndex );
		}
		
		if( p < f1[ 0 ].getX0() )
		{
			return( 0 );
		}
		
		if( p > f1[ f1.length - 1 ].getXN() )
		{
			return( f1.length - 1 );
		}
		
		while( p > f1[ curIndex ].getXN() )
		{
			curIndex++;
		}
		
		while( p < f1[ curIndex ].getX0() )
		{
			curIndex--;
		}
		
		return( curIndex );
	}
	
	
	@Override
	public double eval(double p) {
		curIndex = getIndex( p );
		return( f1[ curIndex ].eval(p) );
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
		
		GPwFractInterpWaveForm wv = new GPwFractInterpWaveForm();
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
