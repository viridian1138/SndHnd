




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


import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Iterator;

import cwaves.SineWaveform;

import bezier.CubicBezierCurve;
import bezier.PiecewiseCubicMonotoneBezierFlat;

import meta.DataFormatException;
import meta.VersionBuffer;


/**
 * The vibrato parameters of a musical note.
 * 
 * Note: this is a set of parameters for "generic" vibrato, which can include either pitch vibrato and/or tremolo.
 * 
 * Note: by default SndHnd applies vibrato to musical instruments that typically don't vibrato.  This effect can be removed by using VIBRATO_MODE_NONE.
 * 
 * @author tgreen
 *
 */
public class VibratoParameters implements Externalizable {

	/**
	 * Constructs the vibrato parameters.
	 */
	public VibratoParameters() {
		super();
	}
	
	/**
	 * Vibrato mode indicating that no vibrato should be applied.
	 */
	public static final int VIBRATO_MODE_NONE = 0;
	
	/**
	 * Vibrato mode indicating that only amplitude tremolo should be applied.
	 */
	public static final int VIBRATO_MODE_TREMOLO = 1;
	
	/**
	 * Vibrato mode indicating that only pitch vibrato shpuld be applied.
	 */
	public static final int VIBRATO_MODE_PITCH_VIBRATO = 2;
	
	/**
	 * Vibrato mode indicating that both amplitude tremolo and pitch vibrato should be applied.
	 */
	public static final int VIBRATO_MODE_TREMOLO_AND_PITCH_VIBRATO = 3;
	
	
	/**
	 * Sine wave used as the model for the vibrato.
	 */
	protected static final SineWaveform SV = new SineWaveform();
	
	/**
	 * The number of beats to delay before starting the vibrato.
	 */
	protected double delayBeats = 0.5;
	
	/**
	 * The number of beats from the start of the vibrato to the "creschendo" where the vibrato has full effect.
	 */
	protected double creschenBeats = 0.5;
	
	/**
	 * The percentage (ratio) by which to alter the pitch for pitch vibrato.
	 * This is on a ratio scale where 0.0 means no change, and 1.0 means a
	 * pitch variation between zero on the trough and one octave on the peak.
	 */
	protected double stripPctFreq = 0.1 / ( 4.0 / 1.35 );
	
	/**
	 * The percentage (ratio) by which to alter the envelope for tremolo.
	 */
	protected double stripPctEnv = 0.575;
	
	/**
	 * The minimum number of vibrato cycles to have in a note.  If the number of
	 * vibrato cycles in the note isn't at least this number then the vibrato is skipped.
	 */
	protected double minCyclesToStart = 3.0;
	
	/**
	 * The amount for which to slide the initial pitch for pitch vibrato.
	 * A value of 0.0 means no such slide.  A value of -1.0 means the 
	 * peak of the pitch vibrato hits the initial pitch.
	 */
	protected double pitchSlide = -1.0;
	
	/**
	 * The vibrato mode.  Possible values are VIBRATO_MODE_TREMOLO_AND_PITCH_VIBRATO,
	 * VIBRATO_MODE_PITCH_VIBRATO, VIBRATO_MODE_TREMOLO, and VIBRATO_MODE_NONE.
	 */
	protected int vibratoMode = VIBRATO_MODE_TREMOLO_AND_PITCH_VIBRATO;
	
	
	/**
	 * Copy constructor.
	 * @param in The input VibratoParameters to be copied.
	 */
	public VibratoParameters( VibratoParameters in )
	{
		delayBeats = in.delayBeats;
		creschenBeats = in.creschenBeats;
		stripPctFreq = in.stripPctFreq;
		stripPctEnv = in.stripPctEnv;
		minCyclesToStart = in.minCyclesToStart;
		pitchSlide = in.pitchSlide;
		vibratoMode = in.vibratoMode;
	}
	
	
	/**
	 * Reads the node from serial storage.
	 */
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			delayBeats = myv.getDouble("DelayBeats");
			creschenBeats = myv.getDouble("CreschenBeats");
			stripPctFreq = myv.getDouble("StripPctFreq");
			stripPctEnv = myv.getDouble("StripPctEnv");
			minCyclesToStart = myv.getDouble("MinCyclesToStart");
			pitchSlide = myv.getDouble("PitchSlide");
			vibratoMode = myv.getInt("VibratoMode");
		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

	/**
	 * Writes the node to serial storage.
	 * 
	 * @serialData TBD.
	 */
	public void writeExternal(ObjectOutput out) throws IOException {
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setDouble("DelayBeats",delayBeats);
		myv.setDouble("CreschenBeats",creschenBeats);
		myv.setDouble("StripPctFreq",stripPctFreq);
		myv.setDouble("StripPctEnv",stripPctEnv);
		myv.setDouble("MinCyclesToStart",minCyclesToStart);
		myv.setDouble("PitchSlide",pitchSlide);
		myv.setInt("VibratoMode",vibratoMode);

		out.writeObject(myv);
	}
	
	
	/**
	 * Returns whether the vibrato mode has tremolo.
	 * @return Whether the vibrato mode has tremolo.
	 */
	public boolean hasTremolo()
	{
		return( ( vibratoMode & VIBRATO_MODE_TREMOLO ) != 0 );
	}
	
	/**
	 * Returns whether the vibrato mode has pitch vibrato.
	 * @return Whether the vibrato mode has pitch vibrato.
	 */
	public boolean hasPitchVibrato()
	{
		return( ( vibratoMode & VIBRATO_MODE_PITCH_VIBRATO ) != 0 );
	}
	
	/**
	 * Builds the pitch-bend for the pitch vibrato.
	 * @param desc The note to which the pitch vibrato is to be applied.
	 * @param nxt The note after the note to which the pitch vibrato is to be applied.
	 * @param interps The input interpolation points of the pitch vibrato.
	 * @param core The number of the core thread.
	 */
	public void buildBend( NoteDesc desc , NoteDesc nxt , ArrayList<InterpolationPoint> interps , final int core ) // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	{
		if( SongData.ROUGH_DRAFT_MODE && SongData.skipVibratoForRoughDraft )
		{
			return;
		}
		
		if( desc.getFreqAndBend().isUserDefinedBend() )
		{
			return;
		}
		
		if( !hasPitchVibrato() )
		{
			return;
		}
		
		double startBeat = desc.getActualStartBeatNumber();
		double endBeat = desc.getActualEndBeatNumber();
		
//		if( nxt != null )
//		{
//			if( nxt.getStartBeatNumber() < endBeat )
//			{
//				endBeat = nxt.getStartBeatNumber();
//			}
//		}
		
		double startCreschenBeatNumber = startBeat + delayBeats;
		double startFullVibratroBeatNumber = startCreschenBeatNumber + creschenBeats;
		
		
		double startFullVibratoTime = SongData.getElapsedTimeForBeatBeat( startFullVibratroBeatNumber , core );
		double startFullVibratoWaveNumber = SongData.getVibratoWaveNumber( startFullVibratoTime , core );
		double minWaveNumber = startFullVibratoWaveNumber + minCyclesToStart;
		double minTime = SongData.getElapsedTimeForVibrNumber( minWaveNumber , core );
		double minBeatNumber = SongData.getBeatNumber( minTime , core );
		
		if( minBeatNumber > endBeat )
		{
			return;
		}
		
		System.out.println( "Generating Vibrato..." );
		
		CubicBezierCurve creschenCrv = new CubicBezierCurve();
		creschenCrv.getBezPts()[ 0 ] = 0.0;
		creschenCrv.getBezPts()[ 1 ] = 0.0;
		creschenCrv.getBezPts()[ 2 ] = 1.0;
		creschenCrv.getBezPts()[ 3 ] = 1.0;
		creschenCrv.setStartParam( startCreschenBeatNumber );
		creschenCrv.setEndParam( startFullVibratroBeatNumber );
		
		PiecewiseCubicMonotoneBezierFlat ibend = new PiecewiseCubicMonotoneBezierFlat();
		ArrayList<InterpolationPoint> interp2 = ibend.getInterpolationPoints();
		for( final InterpolationPoint pt : interps )
		{
			interp2.add( pt );
		}
		interps.clear();
		ibend.updateAll();
		
		
		ArrayList<InterpolationPoint> interps3 = new ArrayList<InterpolationPoint>();
		double startCreschenTime = SongData.getElapsedTimeForBeatBeat( startCreschenBeatNumber , core );
		double startCreschenWaveNumber = SongData.getVibratoWaveNumber( startCreschenTime , core );
		
		double endTime = SongData.getElapsedTimeForBeatBeat( endBeat , core );
		double endWaveNumber = SongData.getVibratoWaveNumber( endTime , core );
		
		final double delta = 1.0 / 32.0;
		double waveNumber = startCreschenWaveNumber;
		int wn = (int)( waveNumber ) + 1;
		while( waveNumber < wn )
		{
			double time = SongData.getElapsedTimeForVibrNumber( waveNumber , core );
			double beat = SongData.getBeatNumber( time , core );
			double u = ( beat - startBeat ) / ( endBeat - startBeat );
			double ibendv = ibend.eval( u );
			double env = stripPctFreq;
			if( waveNumber < startFullVibratoWaveNumber )
			{
				env = stripPctFreq * creschenCrv.eval( beat );
			}
			double multiplier = Math.pow( 2.0 , env * ( SV.eval( waveNumber ) + pitchSlide ) );
			InterpolationPoint intp = new InterpolationPoint( u , multiplier * ibendv );
			interps3.add( intp );
			waveNumber += delta;
		}
		waveNumber = wn;
		while( waveNumber < endWaveNumber )
		{
			double time = SongData.getElapsedTimeForVibrNumber( waveNumber , core );
			double beat = SongData.getBeatNumber( time , core );
			double u = ( beat - startBeat ) / ( endBeat - startBeat );
			double ibendv = ibend.eval( u );
			double env = stripPctFreq;
			if( waveNumber < startFullVibratoWaveNumber )
			{
				env = stripPctFreq * creschenCrv.eval( beat );
			}
			double multiplier = Math.pow( 2.0 , env * ( SV.eval( waveNumber ) + pitchSlide ) );
			InterpolationPoint intp = new InterpolationPoint( u , multiplier * ibendv );
			interps3.add( intp );
			waveNumber += delta;
		}
		
		int acnt = 0;
		int bcnt = 0;
		InterpolationPoint apt = null;
		InterpolationPoint bpt = null;
		if( acnt < interp2.size() )
		{
			apt = interp2.get( acnt );
		}
		else
		{
			apt = null;
		}
		if( bcnt < interps3.size() )
		{
			bpt = interps3.get( bcnt );
		}
		else
		{
			bpt = null;
		}
		double prevParam = -50.0;
		while( ( apt != null ) || ( bpt != null ) )
		{
			if( apt == null )
			{
				if( Math.abs( bpt.getParam() - prevParam ) > 1E-9 )
				{
					interps.add( bpt );
				}
				prevParam = bpt.getParam();
				bcnt++;
			}
			else if( bpt == null )
			{
				if( Math.abs( apt.getParam() - prevParam ) > 1E-9 )
				{
					double u = apt.getParam();
					double beat = (1-u) * ( startBeat ) + u * endBeat;
					if( beat < startCreschenBeatNumber )
					{
						interps.add( apt );
					}
					else
					{
						double time = SongData.getElapsedTimeForBeatBeat( beat , core );
						waveNumber = SongData.getVibratoWaveNumber( time , core );
						double ibendv = apt.getValue();
						double env = stripPctFreq;
						if( waveNumber < startFullVibratoWaveNumber )
						{
							env = stripPctFreq * creschenCrv.eval( beat );
						}
						double multiplier = Math.pow( 2.0 , env * ( SV.eval( waveNumber ) + pitchSlide ) );
						apt.setValue( multiplier * ibendv );
						interps.add( apt );
					}
				}
				prevParam = apt.getParam();
				acnt++;
			}
			else
			{
				if( apt.getParam() < bpt.getParam() )
				{
					if( Math.abs( apt.getParam() - prevParam ) > 1E-9 )
					{
						double u = apt.getParam();
						double beat = (1-u) * ( startBeat ) + u * endBeat;
						if( beat < startCreschenBeatNumber )
						{
							interps.add( apt );
						}
						else
						{
							double time = SongData.getElapsedTimeForBeatBeat( beat , core );
							waveNumber = SongData.getVibratoWaveNumber( time , core );
							double ibendv = apt.getValue();
							double env = stripPctFreq;
							if( waveNumber < startFullVibratoWaveNumber )
							{
								env = stripPctFreq * creschenCrv.eval( beat );
							}
							double multiplier = Math.pow( 2.0 , env * ( SV.eval( waveNumber ) + pitchSlide ) );
							apt.setValue( multiplier * ibendv );
							interps.add( apt );
						}
					}
					prevParam = apt.getParam();
					acnt++;
				}
				else
				{
					if( Math.abs( bpt.getParam() - prevParam ) > 1E-9 )
					{
						interps.add( bpt );
					}
					prevParam = bpt.getParam();
					bcnt++;
				}
			}
			
			if( acnt < interp2.size() )
			{
				apt = interp2.get( acnt );
			}
			else
			{
				apt = null;
			}
			if( bcnt < interps3.size() )
			{
				bpt = interps3.get( bcnt );
			}
			else
			{
				bpt = null;
			}
		}
		
	}
	
	/**
	 * Builds the note envelope of the tremolo.
	 * @param desc The note to which the tremolo is to be applied.
	 * @param nxt The note after the note to which the tremolo is to be applied.
	 * @param minDecayTimeBeats The minimum number of beats over which the instrument amplitude can decay without introducing an interruption.
	 * @param interps The input interpolation points of the tremolo.
	 * @param core The number of the core thread.
	 */
	public void buildTremolo( NoteDesc desc , NoteDesc nxt , double minDecayTimeBeats , ArrayList<InterpolationPoint> interps , final int core )
	{
		
		if( desc.isUserDefinedNoteEnvelope() )
		{
			return;
		}
		
		if( !hasTremolo() )
		{
			return;
		}
		
		double startBeat = desc.getActualStartBeatNumber();
		double endBeat = desc.getActualEndBeatNumber();
		
		if( nxt != null )
		{
			double decayTimeBeats = desc.getEndBeatNumber()
					- desc.getStartBeatNumber();
			double decayTimeBeatsPlay = Math.max(minDecayTimeBeats, decayTimeBeats);
			double decayTimeBeatsFinal = desc.getActualEndBeatNumber() - desc.getActualStartBeatNumber();
			double ratio = decayTimeBeatsFinal / decayTimeBeatsPlay;
			if( ratio < 0.999999999 )
			{
				// ClampedCoefficientRemodulator running
				// Compensate for different coordinate system
				endBeat = startBeat + decayTimeBeatsPlay;
			}
		}
		
		
		double startCreschenBeatNumber = startBeat + delayBeats;
		double startFullVibratroBeatNumber = startCreschenBeatNumber + creschenBeats;
		
		
		double startFullVibratoTime = SongData.getElapsedTimeForBeatBeat( startFullVibratroBeatNumber , core );
		double startFullVibratoWaveNumber = SongData.getVibratoWaveNumber( startFullVibratoTime , core );
		double minWaveNumber = startFullVibratoWaveNumber + minCyclesToStart;
		double minTime = SongData.getElapsedTimeForVibrNumber( minWaveNumber , core );
		double minBeatNumber = SongData.getBeatNumber( minTime , core );
		
		if( minBeatNumber > endBeat )
		{
			return;
		}
		
		System.out.println( "Generating Tremolo..." );
		
		CubicBezierCurve creschenCrv = new CubicBezierCurve();
		creschenCrv.getBezPts()[ 0 ] = 0.0;
		creschenCrv.getBezPts()[ 1 ] = 0.0;
		creschenCrv.getBezPts()[ 2 ] = 1.0;
		creschenCrv.getBezPts()[ 3 ] = 1.0;
		creschenCrv.setStartParam( startCreschenBeatNumber );
		creschenCrv.setEndParam( startFullVibratroBeatNumber );
		
		PiecewiseCubicMonotoneBezierFlat ienvc = new PiecewiseCubicMonotoneBezierFlat();
		ArrayList<InterpolationPoint> interp2 = ienvc.getInterpolationPoints();
		for( final InterpolationPoint pt : interps )
		{
			interp2.add( pt );
		}
		interps.clear();
		ienvc.updateAll();
		
		
		ArrayList<InterpolationPoint> interps3 = new ArrayList<InterpolationPoint>();
		double startCreschenTime = SongData.getElapsedTimeForBeatBeat( startCreschenBeatNumber , core );
		double startCreschenWaveNumber = SongData.getVibratoWaveNumber( startCreschenTime , core );
		
		double endTime = SongData.getElapsedTimeForBeatBeat( endBeat , core );
		double endWaveNumber = SongData.getVibratoWaveNumber( endTime , core );
		
		final double delta = 1.0 / 32.0;
		double waveNumber = startCreschenWaveNumber;
		int wn = (int)( waveNumber ) + 1;
		while( waveNumber < wn )
		{
			double time = SongData.getElapsedTimeForVibrNumber( waveNumber , core );
			double beat = SongData.getBeatNumber( time , core );
			double u = ( beat - startBeat ) / ( endBeat - startBeat );
			double ienv = ienvc.eval( u );
			double env = stripPctEnv;
			if( waveNumber < startFullVibratoWaveNumber )
			{
				env = stripPctEnv * creschenCrv.eval( beat );
			}
			double multiplier = 1.0 - env * 0.5 * ( SV.eval( waveNumber ) + 1.0 );
			InterpolationPoint intp = new InterpolationPoint( u , multiplier * ienv );
			interps3.add( intp );
			waveNumber += delta;
		}
		waveNumber = wn;
		while( waveNumber < endWaveNumber )
		{
			double time = SongData.getElapsedTimeForVibrNumber( waveNumber , core );
			double beat = SongData.getBeatNumber( time , core );
			double u = ( beat - startBeat ) / ( endBeat - startBeat );
			double ienv = ienvc.eval( u );
			double env = stripPctEnv;
			if( waveNumber < startFullVibratoWaveNumber )
			{
				env = stripPctEnv * creschenCrv.eval( beat );
			}
			double multiplier = 1.0 - env * 0.5 * ( SV.eval( waveNumber ) + 1.0 );
			InterpolationPoint intp = new InterpolationPoint( u , multiplier * ienv );
			interps3.add( intp );
			waveNumber += delta;
		}
		
		int acnt = 0;
		int bcnt = 0;
		InterpolationPoint apt = null;
		InterpolationPoint bpt = null;
		if( acnt < interp2.size() )
		{
			apt = interp2.get( acnt );
		}
		else
		{
			apt = null;
		}
		if( bcnt < interps3.size() )
		{
			bpt = interps3.get( bcnt );
		}
		else
		{
			bpt = null;
		}
		double prevParam = -50.0;
		while( ( apt != null ) || ( bpt != null ) )
		{
			if( apt == null )
			{
				if( Math.abs( bpt.getParam() - prevParam ) > 1E-9 )
				{
					interps.add( bpt );
				}
				prevParam = bpt.getParam();
				bcnt++;
			}
			else if( bpt == null )
			{
				if( Math.abs( apt.getParam() - prevParam ) > 1E-9 )
				{
					double u = apt.getParam();
					double beat = (1-u) * ( startBeat ) + u * endBeat;
					if( beat < startCreschenBeatNumber )
					{
						interps.add( apt );
					}
					else
					{
						double time = SongData.getElapsedTimeForBeatBeat( beat , core );
						waveNumber = SongData.getVibratoWaveNumber( time , core );
						double ienv = apt.getValue();
						double env = stripPctEnv;
						if( waveNumber < startFullVibratoWaveNumber )
						{
							env = stripPctEnv * creschenCrv.eval( beat );
						}
						double multiplier = 1.0 - env * 0.5 * ( SV.eval( waveNumber ) + 1.0 );
						apt.setValue( multiplier * ienv );
						interps.add( apt );
					}
				}
				prevParam = apt.getParam();
				acnt++;
			}
			else
			{
				if( apt.getParam() < bpt.getParam() )
				{
					if( Math.abs( apt.getParam() - prevParam ) > 1E-9 )
					{
						double u = apt.getParam();
						double beat = (1-u) * ( startBeat ) + u * endBeat;
						if( beat < startCreschenBeatNumber )
						{
							interps.add( apt );
						}
						else
						{
							double time = SongData.getElapsedTimeForBeatBeat( beat , core );
							waveNumber = SongData.getVibratoWaveNumber( time , core );
							double ienv = apt.getValue();
							double env = stripPctEnv;
							if( waveNumber < startFullVibratoWaveNumber )
							{
								env = stripPctEnv * creschenCrv.eval( beat );
							}
							double multiplier = 1.0 - env * 0.5 * ( SV.eval( waveNumber ) + 1.0 );
							apt.setValue( multiplier * ienv );
							interps.add( apt );
						}
					}
					prevParam = apt.getParam();
					acnt++;
				}
				else
				{
					if( Math.abs( bpt.getParam() - prevParam ) > 1E-9 )
					{
						interps.add( bpt );
					}
					prevParam = bpt.getParam();
					bcnt++;
				}
			}
			
			if( acnt < interp2.size() )
			{
				apt = interp2.get( acnt );
			}
			else
			{
				apt = null;
			}
			if( bcnt < interps3.size() )
			{
				bpt = interps3.get( bcnt );
			}
			else
			{
				bpt = null;
			}
			
		}
	}

	
	/**
	 * Gets the number of beats from the start of the vibrato to the "creschendo" where the vibrato has full effect.
	 * @return The number of beats from the start of the vibrato to the "creschendo" where the vibrato has full effect.
	 */
	public double getCreschenBeats() {
		return creschenBeats;
	}

	/**
	 * Sets the number of beats from the start of the vibrato to the "creschendo" where the vibrato has full effect.
	 * @param creschenBeats The number of beats from the start of the vibrato to the "creschendo" where the vibrato has full effect.
	 */
	public void setCreschenBeats(double creschenBeats) {
		this.creschenBeats = creschenBeats;
	}

	/**
	 * Gets the number of beats to delay before starting the vibrato.
	 * @return The number of beats to delay before starting the vibrato.
	 */
	public double getDelayBeats() {
		return delayBeats;
	}

	/**
	 * Sets the number of beats to delay before starting the vibrato.
	 * @param delayBeats The number of beats to delay before starting the vibrato.
	 */
	public void setDelayBeats(double delayBeats) {
		this.delayBeats = delayBeats;
	}

	/**
	 * Gets the minimum number of vibrato cycles to have in a note.  If the number of
	 * vibrato cycles in the note isn't at least this number then the vibrato is skipped.
	 * @return The minimum number of vibrato cycles to have in a note. 
	 */
	public double getMinCyclesToStart() {
		return minCyclesToStart;
	}

	/**
	 * Sets the minimum number of vibrato cycles to have in a note.  If the number of
	 * vibrato cycles in the note isn't at least this number then the vibrato is skipped.
	 * @param minCyclesToStart The minimum number of vibrato cycles to have in a note. 
	 */
	public void setMinCyclesToStart(double minCyclesToStart) {
		this.minCyclesToStart = minCyclesToStart;
	}

	

	/**
	 * Gets the amount for which to slide the initial pitch for pitch vibrato.
	 * @return The amount for which to slide the initial pitch for pitch vibrato.
	 */
	public double getPitchSlide() {
		return pitchSlide;
	}


	/**
	 * Sets the amount for which to slide the initial pitch for pitch vibrato.
	 * @param pitchSlide The amount for which to slide the initial pitch for pitch vibrato.
	 */
	public void setPitchSlide(double pitchSlide) {
		this.pitchSlide = pitchSlide;
	}


	/**
	 * Gets the vibrato mode.  Possible values are VIBRATO_MODE_TREMOLO_AND_PITCH_VIBRATO,
	 * VIBRATO_MODE_PITCH_VIBRATO, VIBRATO_MODE_TREMOLO, and VIBRATO_MODE_NONE.
	 * @return The vibrato mode.
	 */
	public int getVibratoMode() {
		return vibratoMode;
	}

	/**
	 * Sets the vibrato mode.  Possible values are VIBRATO_MODE_TREMOLO_AND_PITCH_VIBRATO,
	 * VIBRATO_MODE_PITCH_VIBRATO, VIBRATO_MODE_TREMOLO, and VIBRATO_MODE_NONE.
	 * @param vibratoMode The vibrato mode.
	 */
	public void setVibratoMode(int vibratoMode) {
		this.vibratoMode = vibratoMode;
	}

	/**
	 * Gets the percentage (ratio) by which to alter the envelope for tremolo.
	 * @return The percentage (ratio) by which to alter the envelope for tremolo.
	 */
	public double getStripPctEnv() {
		return stripPctEnv;
	}

	/**
	 * Sets the percentage (ratio) by which to alter the envelope for tremolo.
	 * @param stripPctEnv The percentage (ratio) by which to alter the envelope for tremolo.
	 */
	public void setStripPctEnv(double stripPctEnv) {
		this.stripPctEnv = stripPctEnv;
	}

	/**
	 * Gets the percentage (ratio) by which to alter the pitch for pitch vibrato.
	 * @return The percentage (ratio) by which to alter the pitch for pitch vibrato.
	 */
	public double getStripPctFreq() {
		return stripPctFreq;
	}

	/**
	 * Sets the percentage (ratio) by which to alter the pitch for pitch vibrato.
	 * @param stripPctFreq The percentage (ratio) by which to alter the pitch for pitch vibrato.
	 */
	public void setStripPctFreq(double stripPctFreq) {
		this.stripPctFreq = stripPctFreq;
	}

	
}

