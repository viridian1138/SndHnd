




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







package core;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Subclass of DissonanceCalculator for performing calculations across multiple core threads.
 * 
 * @author tgreen
 *
 */
public class DissonanceCalculatorMultiCore extends DissonanceCalculator {
	
	/**
	 * Constructor.
	 * @param notes The notes for the rest of the surrounding composition.
	 * @param _w2 The note for which to calculate dissonances for multiple potential pitches.
	 * @param _offsetWaveNum Offset wave number at which to calculate Fourier coefficients.  Not used.
	 * @param _beatNumber The beat number at which to perform the dissonance calculation.
	 */
	public DissonanceCalculatorMultiCore( ArrayList<NoteDesc> notes , NoteDesc _w2 , int _offsetWaveNum , double _beatNumber ) {
		super(notes,_w2,_offsetWaveNum,_beatNumber);
	}
	
	
	@Override
	public void findMinimums( final double fj1 , final double fj2 , final int max , final ArrayList<Double> outx ) throws Throwable
	{	
		final int NUM_CORES = CpuInfo.getNumCores();
		final Runnable[] runn = new Runnable[ NUM_CORES ];
		final boolean[] b = CpuInfo.createBool( false );
		final ArrayList<Double>[] out = CpuInfo.createArCache( outx );
		
		final int slotsPerCore = max % NUM_CORES == 0 ? max / NUM_CORES : max / NUM_CORES + 1;
		
		int ccnt;
		for( ccnt = 0 ; ccnt < NUM_CORES ; ccnt++ )
		{
			final int core = ccnt;
			runn[ core ] = new Runnable()
			{
				public void run()
				{
					System.out.println( "Started R1..." );
					int count;
					final int mmax = Math.min( max , ( core + 1 ) * slotsPerCore );
					for( count = core * slotsPerCore ; count < mmax ; count++ )
					{
						double u0 = ( (double) ( count - 1 ) ) / max;
						double u1 = ( (double) count ) / max;
						double u2 = ( (double) ( count + 1 ) ) / max;
						double freqj0 = (1-u0) * fj1 + u0 * fj2;
						double freqj1 = (1-u1) * fj1 + u1 * fj2;
						double freqj2 = (1-u2) * fj1 + u2 * fj2;
						double d0 = calcDissonance( freqj0 );
						double d1 = calcDissonance( freqj1 );
						double d2 = calcDissonance( freqj2 );
						if( ( d1 <= d0 ) && ( d1 <= d2 ) )
						{
							out[ core ].add( new Double( freqj1 ) );
						}
					}
					System.out.println( "Notify..." );
					synchronized( this )
					{
						b[ core ] = true;
						this.notify();
					}
				}
			};
		}
		

		System.out.println( "Starting Threads..." );
		
		
		CpuInfo.start( runn );
		CpuInfo.wait(runn, b);
		
		int count;
		for( count = 1 ; count < NUM_CORES ; count++ )
		{
			for( final Double d : out[ count ] )
			{
				outx.add( d );
			}
		}
	}
	
	
	@Override
	public void findMinimaAndMaxima( final double fj1 , final double fj2 , final int max , final ArrayList<Double> outMini , final ArrayList<Double> outMaxi ) throws Throwable
	{	
		final int NUM_CORES = CpuInfo.getNumCores();
		final Runnable[] runn = new Runnable[ NUM_CORES ];
		final boolean[] b = CpuInfo.createBool( false );
		final ArrayList<Double>[] outMin = CpuInfo.createArCache( outMini );
		final ArrayList<Double>[] outMax = CpuInfo.createArCache( outMaxi );
		
		final int slotsPerCore = max % NUM_CORES == 0 ? max / NUM_CORES : max / NUM_CORES + 1;
		
		int ccnt;
		for( ccnt = 0 ; ccnt < NUM_CORES ; ccnt++ )
		{
			final int core = ccnt;
			runn[ core ] = new Runnable()
			{
				public void run()
				{
					System.out.println( "Started R1..." );
					int count;
					final int mmax = Math.min( max , ( core + 1 ) * slotsPerCore );
					for( count = core * slotsPerCore ; count < mmax ; count++ )
					{
						double u0 = ( (double) ( count - 1 ) ) / max;
						double u1 = ( (double) count ) / max;
						double u2 = ( (double) ( count + 1 ) ) / max;
						double freqj0 = (1-u0) * fj1 + u0 * fj2;
						double freqj1 = (1-u1) * fj1 + u1 * fj2;
						double freqj2 = (1-u2) * fj1 + u2 * fj2;
						double d0 = calcDissonance( freqj0 );
						double d1 = calcDissonance( freqj1 );
						double d2 = calcDissonance( freqj2 );
						if( ( d1 <= d0 ) && ( d1 <= d2 ) )
						{
							outMin[ core ].add( new Double( freqj1 ) );
						}
						if( ( d1 >= d0 ) && ( d1 >= d2 ) )
						{
							outMax[ core ].add( new Double( freqj1 ) );
						}
					}
					System.out.println( "Notify..." );
					synchronized( this )
					{
						b[ core ] = true;
						this.notify();
					}
				}
			};
		}
		

		System.out.println( "Starting Threads..." );
		
		CpuInfo.start( runn );
		CpuInfo.wait(runn, b);
		
		int count;
		for( count = 1 ; count < NUM_CORES ; count++ )
		{
			for( final Double d : outMin[ count ] )
			{
				outMini.add( d );
			}
			
			for( final Double d : outMax[ count ] )
			{
				outMaxi.add( d );
			}
		}
	}
	
	
	@Override
	public void calcCurve( final double fj1 , final double fj2 , final int max , final ArrayList<InterpolationPoint> outx ) throws Throwable
	{	
		final int NUM_CORES = CpuInfo.getNumCores();
		final Runnable[] runn = new Runnable[ NUM_CORES ];
		final boolean[] b = CpuInfo.createBool( false );
		final ArrayList<InterpolationPoint>[] out = CpuInfo.createArCache( outx );
		
		final int slotsPerCore = max % NUM_CORES == 0 ? max / NUM_CORES : max / NUM_CORES + 1;
		
		int ccnt;
		for( ccnt = 0 ; ccnt < NUM_CORES ; ccnt++ )
		{
			final int core = ccnt;
			runn[ core ] = new Runnable()
			{
				public void run()
				{
					System.out.println( "Started R1..." );
					int count;
					final int mmax = Math.min( max , ( core + 1 ) * slotsPerCore );
					for( count = core * slotsPerCore ; count < mmax ; count++ )
					{
						double u1 = ( (double) count ) / max;
						double freqj1 = (1-u1) * fj1 + u1 * fj2;
						double d1 = calcDissonance( freqj1 );
						InterpolationPoint pt = new InterpolationPoint( freqj1 , d1 );
						out[ core ].add( pt );
					}
					System.out.println( "Notify..." );
					synchronized( this )
					{
						b[ core ] = true;
						this.notify();
					}
				}
			};
		}
		
		
		System.out.println( "Starting Threads..." );
		
		CpuInfo.start( runn );
		CpuInfo.wait(runn, b);
		
		int count;
		for( count = 1 ; count < NUM_CORES ; count++ )
		{
		    for( final InterpolationPoint pt : out[ count ] )
			{
				outx.add( pt );
			}
		}
	}

}


