





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
import java.util.HashMap;

import core.NonClampedCoefficient;
import core.WaveForm;


/**
 * Waveform that produces noise at lattice points using a variant of Rule 30 from the
 * book "A New Kind Of Science" byStephen Wolfram.
 * 
 * @author tgreen
 *
 */
public class NksNoiseWaveForm extends WaveForm {
	
	
	/**
	 * The number of cells in the automaton.
	 */
	protected static final int NUM_CELLS = 256;
	
	/**
	 * One less than the number of cells in the automaton.  Used as a mask.
	 */
	protected static final int NUM_CELLS_MINUS = NUM_CELLS - 1;
	
	/**
	 * The center cell of the automaton.
	 */
	protected static final int CENTER_CELL = NUM_CELLS / 2;
	
	/**
	 * Maximum double divisor.
	 */
	static final double MAX_DIVISOR = 2.0 * ( (double)( 1L << 62 ) );
	
	/**
	 * The offset for the seed values.
	 */
	protected int offset;
	
	/**
	 * The maximum number of times to run the Rule 30 generator through the initial seed.
	 */
	protected int max;
	
	/**
	 * Cells for the automaton.
	 */
	protected final boolean cellsA[] = new boolean[ NUM_CELLS ];
	
	/**
	 * Cells for the automaton.
	 */
	protected final boolean cellsB[] = new boolean[ NUM_CELLS ];
	
	
	/**
	 * Constructs the waveform.
	 * @param _offset The offset for the seed values.
	 * @param _max The maximum number of times to run the Rule 30 generator through the initial seed.
	 */
	public NksNoiseWaveForm( int _offset , int _max  )
	{
		offset = _offset;
		max = _max;
	}

	@Override
	public double eval( double w ) {
		final long lng = offset + (long)( w / 0.5 + 0.5 );
		// final long lng = offset + Double.doubleToLongBits( w );
		int count;
		
		long tmp = lng;
		cellsA[ CENTER_CELL ] = ( tmp & 1L ) != 0;
		tmp = tmp >> 1;
		int offset = 1;
		while( tmp != 0 )
		{
			cellsA[ CENTER_CELL + offset ] = ( tmp & 1L ) != 0;
			tmp = tmp >> 1;
			cellsA[ CENTER_CELL - offset ] = ( tmp & 1L ) != 0;
			tmp = tmp >> 1;
			offset++;
		}
		
		for( count = 0 ; count < max ; count++ )
		{
			executeRule30( cellsA , cellsB );
			executeRule30( cellsB , cellsA );
		}
		
		long oval = 0L;
		for( count = 0 ; count < 32 ; ++count )
		{
			executeRule30( cellsA , cellsB );
			if( cellsB[ CENTER_CELL ] ) oval = oval | 1L;
			oval = oval << 1;
			executeRule30( cellsB , cellsA );
			if( cellsA[ CENTER_CELL ] ) oval = oval | 1L;
			oval = oval << 1;
		}
		
		return( oval / MAX_DIVISOR );
	}
	
	/**
	 * Performs an execution of Rule 30.
	 * @param cellsIn The input automaton cells.
	 * @param cellsOut The output automaton cells.
	 */
	protected void executeRule30( final boolean[] cellsIn , final boolean[] cellsOut )
	{
		
		for( int count = 0 ; count < NUM_CELLS ; count++ )
		{
			final int beforeC = ( count - 1 ) & NUM_CELLS_MINUS;
			final int afterC = ( count + 1 ) & NUM_CELLS_MINUS;
			int val = 0;
			if( cellsIn[ beforeC] ) val += 1;
			if( cellsIn[ count ] ) val += 2;
			if( cellsIn[ afterC ] ) val += 4;
			cellsOut[ count ] = ( val > 0 ) && ( val <= 4 );
		}
		
	}
	
	
	@Override
	public NonClampedCoefficient genClone()
	{
		return( new RandWhiteWaveForm( offset , max ) );
	}
	
	@Override
	public GWaveForm genWave( HashMap s )
	{
		if( s.get( this ) != null )
		{
			return( (GWaveForm)( s.get( this ) ) );
		}
		
		GRandWhiteWaveForm wv = new GRandWhiteWaveForm();
		s.put(this, wv);
		
		wv.load(offset, max);
		
		return( wv );
	}

	
	public void writeExternal(ObjectOutput arg0) throws IOException {
		// TODO Auto-generated method stub

	}

	public void readExternal(ObjectInput arg0) throws IOException,
			ClassNotFoundException {
		// TODO Auto-generated method stub

	}

	
}

