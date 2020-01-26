




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


import java.util.ArrayList;
import java.util.Iterator;

import core.CpuInfo;
import core.InterpolationPoint;
import core.SongData;

/**
 * Piecewise hextic Bezier curve where the curve segments at either end of the parameter domain are naturally continues past the end of the parameter domain.
 * 
 * Written to support multi-core evaluation.
 * 
 * See:  "Curves and Surfaces for CAGD" by Gerald Farin, ISBN 978-1558607378.
 * 
 * @author tgreen
 *
 */
public class PiecewiseHexticBezierNaturalExtentMultiCore {
	
	/**
	 * The interpolation points of the curve.
	 */
	protected ArrayList<InterpolationPoint> interpolationPoints = new ArrayList<InterpolationPoint>();
	
	/**
	 * The piecewise list of hextic Bezier curve segments.
	 */
	protected ArrayList<HexticBezierCurve> bezierCurves = null;
	
	/**
	 * The current curve segment index for each core thread.
	 */
	protected final int[] currentIndex = new int[ CpuInfo.getNumCores() ];

	/**
	 * Constructs the curve.
	 */
	public PiecewiseHexticBezierNaturalExtentMultiCore() {
		super();
		int count;
		int max = CpuInfo.getNumCores();
		for( count = 0 ; count < max ; count++ )
		{
			currentIndex[ count ] = 0;
		}
	}
	
	/**
	 * Gets the number of hextic Bezier curve segments.
	 * @return The number of hextic Bezier curve segments.
	 */
	public int getNumCurves()
	{
		return( bezierCurves.size() );
	}
	
	/**
	 * Gets the hextic Bezier curve segment at a particular index.
	 * @param index The input index.
	 * @return The hextic Bezier curve segment at the index.
	 */
	public HexticBezierCurve gCurve( int index )
	{
		HexticBezierCurve nd = bezierCurves.get( index );
		return( nd );
	}
	
	/**
	 * Gets the current hextic Bezier curve segment for a particular parameter.
	 * @param param The input parameter value.
	 * @param core The core thread performing the evaluation.
	 * @return The current curve segment.
	 */
	public HexticBezierCurve getCurrentCurve( final double param , final int core )
	{	
		while( ( param < gCurve( currentIndex[ core ] ).getStartParam() ) && ( currentIndex[ core ] > 0 ) )
		{
			( currentIndex[ core ] )--;
		}
		
		while( ( param > gCurve( currentIndex[ core ] ).getEndParam() ) && ( currentIndex[ core ] < ( bezierCurves.size() - 1 ) ) )
		{
			( currentIndex[ core ] )++;
		}
		
		return( gCurve( currentIndex[ core ] ) );
	}
	
	/**
	 * Evaluates the curve at a particular parameter.
	 * @param param The parameter at which to evaluate.
	 * @param core The core thread performing the evaluation.
	 * @return The evaluated curve at the parameter.
	 */
	public double eval( final double param , final int core )
	{
		return( getCurrentCurve( param , core ).eval( param ) );
	}

	/**
	 * Gets the piecewise list of hextic Bezier curve segments.
	 * @return The piecewise list of hextic Bezier curve segments.
	 */
	public ArrayList<HexticBezierCurve> getBezierCurves() {
		return bezierCurves;
	}

	/**
	 * Sets the piecewise list of hextic Bezier curve segments.
	 * @param bezierCurves The piecewise list of hextic Bezier curve segemnts.
	 */
	protected void setBezierCurves(ArrayList<HexticBezierCurve> bezierCurves) {
		this.bezierCurves = bezierCurves;
		int count;
		int max = CpuInfo.getNumCores();
		for( count = 0 ; count < max ; count++ )
		{
			currentIndex[ count ] = 0;
		}
	}
	
	/**
	 * Gets the interpolation point at a particular index.  Indices start at zero.
	 * @param index The index.
	 * @return The interpolation point at the index.
	 */
	public InterpolationPoint gPoint( int index )
	{
		InterpolationPoint pt = interpolationPoints.get( index );
		return( pt );
	}
	
	/**
	 * Gets the first interpolation point.
	 * @return The first interpolation point.
	 */
	public InterpolationPoint getFirstPoint( )
	{
		return( gPoint( 0 ) );
	}
	
	/**
	 * Gets the last interpolation point.
	 * @return The last interpolation point.
	 */
	public InterpolationPoint getLastPoint()
	{
		return( gPoint( interpolationPoints.size() - 1 ) );
	}
	
	/**
	 * Returns whether there is an additional curve segment available.
	 * @param switchCubic Whether to determine based on the cubic curve iterator.
	 * @param ita The quartic curve iterator.
	 * @param itb The cubic curve iterator.
	 * @return Whether there is an additional curve segment available.
	 */
	protected static boolean hasSwitchNext( boolean switchCubic , Iterator<QuarticBezierCurve> ita , Iterator<CubicBezierCurve> itb )
	{
		if( switchCubic )
		{
			return( itb.hasNext() );
		}
		
		return( ita.hasNext() );
	}
	
	/**
	 * Builds a function of frequency per second as an algebraic composite of a beat number per second function and a frequency per beat number function.
	 * See:  https://courses.lumenlearning.com/wmopen-collegealgebra/chapter/introduction-compositions-of-functions/
	 * @param beatNumberGlobalPerSeconds The beat number per second function.
	 * @param freqPerBeatNumberNote The frequency per beat number function.
	 * @param startBeatNumberForNoteGlobal The beat number at which a given note starts.
	 * @param core The number of the cure thread.
	 * @return The composite function of frequency per second.
	 */
	public static PiecewiseHexticBezierNaturalExtentMultiCore buildHexticCompositeCurveComp( PiecewiseQuarticBezierSlopingMultiCore beatNumberGlobalPerSeconds , 
			PiecewiseCubicMonotoneBezierFlatMultiCore freqPerBeatNumberNote , double startBeatNumberForNoteGlobal , final int core )
	{
		PiecewiseHexticBezierNaturalExtentMultiCore ret = new PiecewiseHexticBezierNaturalExtentMultiCore();
		ret.setBezierCurves( new ArrayList<HexticBezierCurve>() );
		
		Iterator<QuarticBezierCurve> ita = beatNumberGlobalPerSeconds.getBezierCurves().iterator();
		Iterator<CubicBezierCurve> itb = freqPerBeatNumberNote.getBezierCurves().iterator();
		
		double startSeconds = SongData.getElapsedTimeForBeatBeat( startBeatNumberForNoteGlobal , core );
		
		QuarticBezierCurve qua = ita.next();
		CubicBezierCurve cub = itb.next();
		
		while( ( startSeconds > qua.getEndParam() ) && ( ita.hasNext() ) )
		{
			qua = ita.next();
		}
		
		double curSeconds = startSeconds;
		double curBeatGlobal = startBeatNumberForNoteGlobal;
		boolean switchCubic = false;
		boolean cutStart = false;
		
		double endSeconds = qua.getEndParam();
		double endQuarValBeatNumber = SongData.getBeatNumber( endSeconds , core );
		switchCubic = false;
		if( endQuarValBeatNumber > cub.getEndParam() )
		{
			switchCubic = true;
			endSeconds = SongData.getElapsedTimeForBeatBeat( cub.getEndParam() , core );
		}
		cutStart = false;
		double startQuarValBeatNumber = SongData.getBeatNumber( startSeconds , core );
		if( startQuarValBeatNumber < cub.getStartParam() )
		{
			cutStart = true;
			startSeconds = SongData.getElapsedTimeForBeatBeat( cub.getStartParam() , core );
		}
		
		QuarticBezierCurve quap = qua;
		if( cutStart || switchCubic )
		{
			quap = qua.calcSubsection( startSeconds , endSeconds );
		}
		
		HexticBezierCurve hex = HexticBezierCurve.buildCompositeCurve( quap , cub );
		ret.getBezierCurves().add( hex );
		
		while( hasSwitchNext( switchCubic , ita , itb ) )
		{	
			if( switchCubic )
			{
				cub = itb.next();
				curBeatGlobal = cub.getStartParam();
				curSeconds = SongData.getElapsedTimeForBeatBeat( curBeatGlobal , core );
				while( ( curSeconds > qua.getEndParam() ) && ( ita.hasNext() ) )
				{
					qua = ita.next();
				}
			}
			else
			{
				qua = ita.next();
				curSeconds = qua.getStartParam();
				curBeatGlobal = SongData.getBeatNumber( curSeconds , core );
				while( ( curBeatGlobal > cub.getEndParam() ) && ( itb.hasNext() ) )
				{
					cub = itb.next();
				}
			}
			
			endSeconds = qua.getEndParam();
			endQuarValBeatNumber = SongData.getBeatNumber( endSeconds , core );
			switchCubic = false;
			if( endQuarValBeatNumber > cub.getEndParam() )
			{
				switchCubic = true;
				endSeconds = SongData.getElapsedTimeForBeatBeat( cub.getEndParam() , core );
			}
			cutStart = false;
			startSeconds = qua.getStartParam();
			startQuarValBeatNumber = SongData.getBeatNumber( startSeconds , core );
			if( startQuarValBeatNumber < cub.getStartParam() )
			{
				cutStart = true;
				startSeconds = SongData.getElapsedTimeForBeatBeat( cub.getStartParam() , core );
			}
			
			quap = qua;
			if( cutStart || switchCubic )
			{
				quap = qua.calcSubsection( startSeconds , endSeconds );
			}
			
			hex = HexticBezierCurve.buildCompositeCurve( quap , cub );
			ret.getBezierCurves().add( hex );
		}
		
		hex = null;
		
		Iterator<HexticBezierCurve> it = ret.getBezierCurves().iterator();
		while( it.hasNext() )
		{
			hex = it.next();
			InterpolationPoint pt = new InterpolationPoint( hex.getStartParam() , ( hex.getBezPts() )[ 0 ] );
			ret.getInterpolationPoints().add( pt );
		}
		
		if( hex != null )
		{
			InterpolationPoint pt = new InterpolationPoint( hex.getEndParam() , ( hex.getBezPts() )[ 6 ] );
			ret.getInterpolationPoints().add( pt );
		}
		
		return( ret );
	}
	
	/**
	 * Gets the interpolation points of the curve.
	 * @return The interpolation points of the curve.
	 */
	public ArrayList<InterpolationPoint> getInterpolationPoints() {
		return interpolationPoints;
	}


}

