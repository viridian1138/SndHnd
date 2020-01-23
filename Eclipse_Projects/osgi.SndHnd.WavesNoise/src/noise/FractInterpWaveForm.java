





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
 * A waveform approximating a fractal interpolation as described in Chapter 4 of the book "Fractals Everywhere" by Michael F. Barnsley.
 * 
 * @author thorngreen
 *
 */
public class FractInterpWaveForm extends WaveForm {
	
	/**
	 * The interpolation points of the fractal.
	 */
	private ArrayList<FractInterpNode> nodes;
	
	/**
	 * The number of iterations over which to evaluate the fractal.
	 */
	private int iterCnt;
	
	/**
	 * Internal set of indices that are used to store the index of the interpolation point evaluated for each fractal iteration.
	 */
	private int[] iters;
	
	
	/**
	 * Constructs the waveform.
	 * @param _nodes The interpolation points of the fractal.
	 * @param _iterCnt The number of iterations over which to evaluate the fractal.
	 */
	public FractInterpWaveForm( ArrayList<FractInterpNode> _nodes , int _iterCnt )
	{
		nodes = _nodes;
		iterCnt = _iterCnt;
		iters = new int[ iterCnt ];
		
		final double x0 = nodes.get( 0 ).getX();
		final double xn = nodes.get( nodes.size() - 1 ).getX();
		
		int cnt;
		for( cnt = 1 ; cnt < nodes.size() ; cnt++ )
		{
			final double p = nodes.get( cnt - 1 ).getF() - nodes.get( cnt ).getD() * nodes.get( 0 ).getF()
					- nodes.get( cnt ).getHh() * nodes.get( 0 ).getH();
			final double q = nodes.get( cnt - 1 ).getH() - nodes.get( cnt ).getL() * nodes.get( 0 ).getF()
					- nodes.get( cnt ).getM() * nodes.get( 0 ).getH();
			final double r = nodes.get( cnt ).getF() - nodes.get( cnt ).getD() * nodes.get( nodes.size() - 1 ).getF()
					- nodes.get( cnt ).getHh() * nodes.get( nodes.size() - 1 ).getH();
			final double s = nodes.get( cnt ).getH() - nodes.get( cnt ).getL() * nodes.get( nodes.size() - 1 ).getF()
					- nodes.get( cnt ).getM() * nodes.get( nodes.size() - 1 ).getH();
			final double b = xn - x0;
			nodes.get( cnt ).setC( ( r - p ) / b );
			nodes.get( cnt ).setK( ( s - q ) / b );
			nodes.get( cnt ).setA( ( nodes.get( cnt ).getX() - nodes.get( cnt - 1 ).getX() ) / b );
			nodes.get( cnt ).setE( ( xn * nodes.get( cnt - 1 ).getX() - x0 * nodes.get( cnt ).getX() ) / b );
			nodes.get( cnt ).setfF( p - nodes.get( cnt ).getC() * x0 );
			nodes.get( cnt ).setG( q - nodes.get( cnt ).getK() * x0 );
		}
		
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
		
		return( evalA( ( 1 - u ) * x0 + u * xn ) );
		
	}
	

	/**
	 * Evaluates the fractal.
	 * @param p An X-coordinate that is within the X-coordinate interval of the interpolation points.
	 * @return The result of evaluating the fractal at p.
	 */
	public double evalA(double p) {
		
		int cnt;
		final double x0 = nodes.get( 0 ).getX();
		final double xn = nodes.get( nodes.size() - 1 ).getX();
		
		
		for( cnt = 0 ; cnt < iterCnt ; cnt++ )
		{
			int acnt;
			for( acnt = 1 ; acnt < nodes.size() ; acnt++ )
			{
				if( ( p >= nodes.get( acnt - 1 ).getX() ) && ( p <= nodes.get( acnt ).getX() ) )
				{
					iters[ cnt ] = acnt;
					double u = ( p - nodes.get( acnt - 1 ).getX() ) / 
							( nodes.get( acnt ).getX() - nodes.get( acnt - 1 ).getX() );
					p = ( 1 - u ) * x0 + u * xn;
					break;
				}
			}
		}
		
		
		
		double x = 0;
		double y = 0;
		double z = nodes.get( 0 ).getHh();
		
		for( cnt = iterCnt - 1 ; cnt >= 0 ; cnt-- )
		{
			FractInterpNode nd = nodes.get( iters[ cnt ] );
			double newx = nd.getA() * x + nd.getE();
			double newy = nd.getC() * x + nd.getD() * y + nd.getHh() * z + nd.getfF();
			double newz = nd.getK() * x + nd.getL() * y + nd.getM() * z + nd.getG();
			
			x = newx;
			y = newy;
			z = newz;
		}
		
		return( y );
	}
	
	
	/**
	 * Gets the X-coordinate at the initial interpolation point.
	 * @return The X-coordinate at the initial interpolation point.
	 */
	protected double getX0()
	{
		return( nodes.get( 0 ).getX() );
	}
	
	
	/**
	 * Gets the X-coordinate at the last interpolation point.
	 * @return The X-coordinate at the last interpolation point.
	 */
	protected double getXN()
	{
		return( nodes.get( nodes.size() - 1 ).getX() );
	}
	
	
	@Override
	public NonClampedCoefficient genClone() throws Throwable
	{
		return( new FractInterpWaveForm( nodes , iterCnt ) );
	}

	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GFractInterpWaveForm wv = new GFractInterpWaveForm();
		s.put(this, wv);
		
		wv.load(nodes, iterCnt);
		
		return( wv );
	}

	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		// TODO Auto-generated method stub !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

	}

	public void writeExternal(ObjectOutput out) throws IOException {
		// TODO Auto-generated method stub !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

	}

	
}

