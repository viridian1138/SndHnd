




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

import bezier.PiecewiseCubicMonotoneBezierFlat;
import bezier.PiecewiseCubicMonotoneBezierFlatMultiCore;
import bezier.PiecewiseHepticBezierSlopingMultiCore;
import bezier.PiecewiseHexticBezierNaturalExtentMultiCore;

/**
 * Frequency (pitch) and pitch-bend for a musical note.
 * 
 * @author tgreen
 *
 */
public class FreqAndBend {
	
	/**
	 * Ihe curve of the pitch-bend ratios of the note defined over the "affine" [0,1] parameter over the extent of beats in the note.
	 */
	protected PiecewiseCubicMonotoneBezierFlatMultiCore bendPerNoteU = new PiecewiseCubicMonotoneBezierFlatMultiCore();
	
	/**
	 * Ihe calculated curve of the frequency (in hertz) of the note defined over the "affine" [0,1] parameter over the extent of beats in the note.
	 */
	protected PiecewiseCubicMonotoneBezierFlatMultiCore freqPerNoteU = new PiecewiseCubicMonotoneBezierFlatMultiCore();
	
	// protected PiecewiseQuarticBezierSloping wavePerNoteU = new PiecewiseQuarticBezierSloping();
	
	/**
	 * Curve describing the wave parameter (number of wave periods) versus elapsed time in seconds.
	 */
	protected PiecewiseHepticBezierSlopingMultiCore wavePerElapsedSeconds = new PiecewiseHepticBezierSlopingMultiCore();
	
	
	/**
	 * The base frequency (pitch) of the note in hertz.
	 */
	protected double baseFreq;
	
	/**
	 * The calculated initial frequency (pitch) of the note in hertz.
	 * This can be different from the base pitch because e.g. the note may have an initial slide into the base pitch.
	 */
	protected double initialFreq;
	
	/**
	 * The calculated final frequency (pitch) of the note in hertz.
	 */
	protected double finalFreq;
	
	/**
	 * The calculated final wave number of the note.
	 */
	protected double finalWaveNumber;
	
	// protected double finalSeconds;
	
	/**
	 * Whether the pitch-bend of the note is user-defined (as opposed to agent-defined).
	 */
	protected boolean userDefinedBend = false;
	
	
	/**
	 * Whether the wave information (wavePerElapsedSeconds member) of the note is dirty.
	 */
	protected boolean waveInfoDirty = true;
	
	/**
	 * Whether the information required to visually render the pitch-bend of the note is dirty.
	 */
	protected boolean waveDisplayDirty = true;

	
	/**
	 * Constructor.
	 */
	public FreqAndBend() {
		super();
	}
	
	
	/**
	 * Updates the wave information (wavePerElapsedSeconds member) of the note.
	 * @param startBeatNumberActual The actual beat number of the start of the note.
	 * @param endBeatNumberActual The actual beat number of the end of the note.
	 * @param core The number of the core thread.
	 */
	public void updateWaveInfo( /* double multiplier */ 
			double startBeatNumberActual , double endBeatNumberActual , final int core )
	{
		if( waveInfoDirty )
		{
			synchronized( this )
			{
				if( waveInfoDirty )
				{
					int count;
					PiecewiseCubicMonotoneBezierFlatMultiCore freqPerGlobalBeatNumber = new PiecewiseCubicMonotoneBezierFlatMultiCore();
					int sz = bendPerNoteU.getInterpolationPoints().size();
					freqPerNoteU.getInterpolationPoints().clear();
					freqPerGlobalBeatNumber.getInterpolationPoints().clear();
					for( count = 0 ; count < sz ; count++ )
					{
						InterpolationPoint intp = bendPerNoteU.getInterpolationPoints().get( count );
						InterpolationPoint ptp = new InterpolationPoint( intp.getParam() , intp.getValue() * baseFreq );
						InterpolationPoint ptpa = new InterpolationPoint( 
								( intp.getParam() ) * ( endBeatNumberActual - startBeatNumberActual ) + startBeatNumberActual , intp.getValue() * baseFreq );
						freqPerNoteU.getInterpolationPoints().add( ptp );
						freqPerGlobalBeatNumber.getInterpolationPoints().add( ptpa );
					}
				
					freqPerNoteU.updateAll();
					freqPerGlobalBeatNumber.updateAll();
				
					PiecewiseHexticBezierNaturalExtentMultiCore hextic = SongData.buildHexticCompositeCurve( 
							freqPerGlobalBeatNumber , startBeatNumberActual , core );
			
					// wavePerNoteU.integrateCurve( freqPerNoteU , multiplier );
			
					wavePerElapsedSeconds.integrateCurve( hextic  , 1.0 );
			
					initialFreq = freqPerNoteU.gCurve( 0 ).getBezPts()[ 0 ];
					finalFreq = freqPerNoteU.gCurve( freqPerNoteU.getNumCurves() - 1 ).getBezPts()[ 3 ];
					// finalWaveNumber = wavePerNoteU.gCurve( wavePerNoteU.getNumCurves() - 1 ).getBezPts()[ 4 ];
					finalWaveNumber = wavePerElapsedSeconds.gCurve( wavePerElapsedSeconds.getNumCurves() - 1 ).getBezPts()[ 7 ];
					// finalSeconds = freqPerNoteU.gCurve( freqPerNoteU.getNumCurves() - 1 ).getEndParam() * multiplier;

					waveInfoDirty = false;
					waveDisplayDirty = false;
				}
			}
		}
	}
	
	
	/**
	 * Updates only the information required to visually render the pitch-bend of the note.
	 */
	public void updateWaveInfoDisplayOnly( )
	{
		if( waveDisplayDirty )
		{
			synchronized( this )
			{
				if( waveDisplayDirty )
				{
					int count;
					int sz = bendPerNoteU.getInterpolationPoints().size();
					freqPerNoteU.getInterpolationPoints().clear();
					for( count = 0 ; count < sz ; count++ )
					{
						InterpolationPoint intp = bendPerNoteU.getInterpolationPoints().get( count );
						InterpolationPoint ptp = new InterpolationPoint( intp.getParam() , intp.getValue() * baseFreq );
						freqPerNoteU.getInterpolationPoints().add( ptp );
					}
					freqPerNoteU.updateAll();
			
					waveDisplayDirty = false;
				}
			}
		}
	}
	
	
	/* public double getWaveNumber( NoteDesc desc , double noteU , double multiplier )
	{	
		updateWaveInfo( multiplier );
		
		if( noteU < 0.0 )
		{
			Wave Number at noteU = 0 is zero.
			double seconds = multiplier * noteU;
			return( initialFreq * seconds );
		}
		
		if( noteU > 1.0 )
		{
			double seconds = multiplier * noteU;
			return( finalWaveNumber + finalFreq * ( seconds - finalSeconds ) );
		}
		
		return( wavePerNoteU.eval( noteU ) );
	} */
	
	
	/**
	 * Gets the wave number at a particular elapsed time for a note.
	 * @param desc The note for which to calculate the wave number.
	 * @param elapsedTimeSeconds The elapsed time in seconds for which to calculate the wave number.
	 * @param core The number of the core thread.
	 * @return The calculated wave number.
	 */
	public double getWaveNumber( final NoteDesc desc , final double elapsedTimeSeconds , final int core )
	{	
		updateWaveInfo( desc.getActualStartBeatNumber() , desc.getActualEndBeatNumber() , core );
		
		// System.out.println( "+++ " + ( wavePerElapsedSeconds.evalSlope( elapsedTimeSeconds ) ) );
		
		return( wavePerElapsedSeconds.eval( elapsedTimeSeconds , core ) );
	}
	

	/**
	 * Gets the calculated curve of the frequency (in hertz) of the note defined over the "affine" [0,1] parameter over the extent of beats in the note.
	 * @return Ihe calculated curve of the frequency (in hertz) of the note defined over the "affine" [0,1] parameter over the extent of beats in the note.
	 */
	public PiecewiseCubicMonotoneBezierFlatMultiCore getFreqPerNoteU() {
		return freqPerNoteU;
	}

	/**
	 * Gets whether the wave information (wavePerElapsedSeconds member) of the note is dirty.
	 * @return Whether the wave information (wavePerElapsedSeconds member) of the note is dirty.
	 */
	public boolean isWaveInfoDirty() {
		return waveInfoDirty;
	}

	/**
	 * Sets whether the wave information (wavePerElapsedSeconds member) of the note is dirty.
	 * @param waveInfoDirty Whether the wave information (wavePerElapsedSeconds member) of the note is dirty.
	 */
	public void setWaveInfoDirty(boolean waveInfoDirty) {
		this.waveInfoDirty = waveInfoDirty;
		waveDisplayDirty = waveDisplayDirty || waveInfoDirty;
	}
	
	/**
	 * Calculates the minimum frequency (pitch) of the note in hertz.
	 * @return The minimum frequency (pitch) of the note in hertz.
	 */
	public double getMinFrequency()
	{
		int count;
		ArrayList<InterpolationPoint> interpolationPoints = freqPerNoteU.getInterpolationPoints();
		int sz = interpolationPoints.size();
		InterpolationPoint pt = interpolationPoints.get( 0 );
		double freq = pt.getValue();
		for( count = 1 ; count < sz ; count++ )
		{
			pt = interpolationPoints.get( count );
			freq = Math.min( freq , pt.getValue() );
		}
		return( freq );
	}
	
	/**
	 * Calculates the maximum frequency (pitch) of the note in hertz.
	 * @return The maximum frequency (pitch) of the note in hertz.
	 */
	public double getMaxFrequency()
	{
		int count;
		ArrayList<InterpolationPoint> interpolationPoints = freqPerNoteU.getInterpolationPoints();
		int sz = interpolationPoints.size();
		InterpolationPoint pt = interpolationPoints.get( 0 );
		double freq = pt.getValue();
		for( count = 1 ; count < sz ; count++ )
		{
			pt = interpolationPoints.get( count );
			freq = Math.max( freq , pt.getValue() );
		}
		return( freq );
	}


	/**
	 * Gets the base frequency (pitch) of the note in hertz.
	 * @return The base frequency (pitch) of the note in hertz.
	 */
	public double getBaseFreq() {
		return baseFreq;
	}


	/**
	 * Sets the base frequency (pitch) of the note in hertz.
	 * @param baseFreq The base frequency (pitch) of the note in hertz.
	 */
	public void setBaseFreq(double baseFreq) {
		this.baseFreq = baseFreq;
	}


	/**
	 * Gets the curve of the pitch-bend ratios of the note defined over the "affine" [0,1] parameter over the extent of beats in the note.
	 * @return Ihe curve of the pitch-bend ratios of the note defined over the "affine" [0,1] parameter over the extent of beats in the note.
	 */
	public PiecewiseCubicMonotoneBezierFlatMultiCore getBendPerNoteU() {
		return bendPerNoteU;
	}


	/**
	 * Sets the curve of the pitch-bend ratios of the note defined over the "affine" [0,1] parameter over the extent of beats in the note.
	 * @param bendPerNoteU Ihe curve of the pitch-bend ratios of the note defined over the "affine" [0,1] parameter over the extent of beats in the note.
	 */
	public void setBendPerNoteU(PiecewiseCubicMonotoneBezierFlatMultiCore bendPerNoteU) {
		this.bendPerNoteU = bendPerNoteU;
	}


	/**
	 * Gets whether the pitch-bend of the note is user-defined (as opposed to agent-defined).
	 * @return Whether the pitch-bend of the note is user-defined (as opposed to agent-defined).
	 */
	public boolean isUserDefinedBend() {
		return userDefinedBend;
	}


	/**
	 * Sets whether the pitch-bend of the note is user-defined (as opposed to agent-defined).
	 * @param userDefinedBend Whether the pitch-bend of the note is user-defined (as opposed to agent-defined).
	 */
	public void setUserDefinedBend(boolean userDefinedBend) {
		this.userDefinedBend = userDefinedBend;
	}
	
	/**
	 * Calculates the end wave number of a note.
	 * @param note The note for which to calculate the wave number.
	 * @param core The number of the core thread.
	 * @return The end wave number of the note.
	 */
	public double getEndWaveNumber( NoteDesc note , final int core )
	{
		updateWaveInfo( note.getActualStartBeatNumber() , note.getActualEndBeatNumber() , core );
		ArrayList<InterpolationPoint> interpPoints = wavePerElapsedSeconds.getInterpolationPoints();
		int sz = interpPoints.size();
		InterpolationPoint pt = interpPoints.get( sz - 1 );
		return( pt.getValue() );
	}

	
}

