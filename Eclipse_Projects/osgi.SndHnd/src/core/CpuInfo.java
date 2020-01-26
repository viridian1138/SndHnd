




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
import java.util.Random;

public class CpuInfo {
	
	/*
	 * For an explanation of this counting scheme, see:
	 * 
	 * http://blogs.intel.com/research/2008/08/how_to_count_cores.php
	 * 
	 */
	
	static final int NUM_CPUS = 1;
	
	static final int NUM_CORES_PER_CPU = 12;
	
	static final int THREADS_PER_CORE = 1;
	
	static final int SIMD_ISA_WIDTH = 4;
	
	/**
	 * Number of "cores" for which to allocate threads.
	 * See http://blogs.intel.com/research/2008/08/how_to_count_cores.php
	 * 
	 * Note: even though Intel docs. suggest one should count all of the ISA
	 * width, and CPU utilization statistics seem to support this, actual
	 * application performance for the Intel Core 2 Duo seems to be best when
	 * the number of threads is equal to the number of cores (or at least within
	 * margin for error compared to allicating one thread per ISA).
	 */
	static final int NUM_CORES_FOR_THREADING = NUM_CPUS * NUM_CORES_PER_CPU; // * THREADS_PER_CORE * SIMD_ISA_WIDTH;
	
	// static final int NUM_CORES_FOR_THREADING = 32;
	
	/**
	 * Number of "cores" for which to allocate threads.
	 * See http://blogs.intel.com/research/2008/08/how_to_count_cores.php
	 * @return Number of "cores" for which to allocate threads.
	 */
	public static int getNumCores()
	{
		return( NUM_CORES_FOR_THREADING );
	}
	
// Only used for evaluating best number of cores.
//	public static void setNumCores( int in ) !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//	{
//		NUM_CORES_FOR_THREADING = in;
//	}
	
//	public static void resetNumCores() !!!!!!!!!!!!!!!!!!!!!!!!!!!
//	{
//		NUM_CORES_FOR_THREADING = 12; // NUM_CPUS * NUM_CORES_PER_CPU * THREADS_PER_CORE * SIMD_ISA_WIDTH;
//	}
	
	public static double[] createDbl( final double val )
	{
		final int numCores = getNumCores();
		final double[] ret = new double[ numCores ];
		int count;
		for( count = 0 ; count < numCores ; count++ )
		{
			ret[ count ] = val;
		}
		return( ret );
	}
	
	public static int[] createInt( final int val )
	{
		final int numCores = getNumCores();
		final int[] ret = new int[ numCores ];
		int count;
		for( count = 0 ; count < numCores ; count++ )
		{
			ret[ count ] = val;
		}
		return( ret );
	}
	
	public static boolean[] createBool( final boolean val )
	{
		final int numCores = getNumCores();
		final boolean[] ret = new boolean[ numCores ];
		int count;
		for( count = 0 ; count < numCores ; count++ )
		{
			ret[ count ] = val;
		}
		return( ret );
	}
	
	public static Random[] createRand( final int seed )
	{
		final int numCores = getNumCores();
		final Random[] ret = new Random[ numCores ];
		int count;
		for( count = 0 ; count < numCores ; count++ )
		{
			ret[ count ] = new Random( seed + count );
		}
		return( ret );
	}
	
	public static ArrayList[] createArCache( final ArrayList ls )
	{
		final int numCores = getNumCores();
		final ArrayList[] ret = new ArrayList[ numCores ];
		int count;
		ret[ 0 ] = ls;
		for( count = 1 ; count < numCores ; count++ )
		{
			ret[ count ] = new ArrayList();
		}
		return( ret );
	}
	
	public static double avgDiff( long[] b , long a )
	{
		final int numCores = getNumCores();
		double ret = 0.0;
		int count;
		for( count = 0 ; count < numCores ; count++ )
		{
			ret += b[ count ] - a;
		}
		return( ret / numCores );
	}
	
	public static double max( final double[] in )
	{
		final int numCores = getNumCores();
		double ret = in[ 0 ];
		int count;
		for( count = 1 ; count < numCores ; count++ )
		{
			ret = Math.max( ret , in[ count ] );
		}
		return( ret );
	}
	
	public static int sum( final int[] in )
	{
		final int numCores = getNumCores();
		int ret = in[ 0 ];
		int count;
		for( count = 1 ; count < numCores ; count++ )
		{
			ret += in[ count ];
		}
		return( ret );
	}
	
	public static void start( final Runnable[] r )
	{
		final int numCores = getNumCores();
		int count;
		for( count = 0 ; count < numCores ; count++ )
		{
			( new Thread( r[ count ] ) ).start();
		}
	}
	
	public static void wait( final Runnable[] runn, final boolean[] fini ) throws Throwable
	{
		final int numCores = getNumCores();
		int count;
		for( count = 0 ; count < numCores ; count++ )
		{
			if( !( fini[ count ] ) )
			{
				synchronized( runn[ count ] )
				{
					while( !( fini[ count ] ) )
					{
						( runn[ count ] ).wait();
					}
				}
			}
		}
	}

	
}

