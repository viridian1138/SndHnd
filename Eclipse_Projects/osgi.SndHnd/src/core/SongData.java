




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


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.SwingUtilities;

import aazon.dbl.AazonBaseMutableDbl;
import aazon.dbl.AazonMutableDbl;
import bezier.BezierCubicClampedCoefficient;
import bezier.BezierCubicNonClampedCoefficientFlat;
import bezier.BezierCubicNonClampedCoefficientFlatMultiCore;
import bezier.BezierCubicNonClampedCoefficientSlopingMultiCore;
import bezier.BezierMultiWaveAppx;
import bezier.BezierQuarticNonClampedCoefficientSlopingMultiCore;
import bezier.CubicBezierCurve;
import bezier.PiecewiseCubicMonotoneBezierFlat;
import bezier.PiecewiseCubicMonotoneBezierFlatMultiCore;
import bezier.PiecewiseCubicMonotoneBezierSlopingMultiCore;
import bezier.PiecewiseHepticBezierSlopingMultiCore;
import bezier.PiecewiseHexticBezierNaturalExtentMultiCore;
import bezier.PiecewiseQuarticBezierSlopingMultiCore;
import cwaves.AdditiveWaveForm;
import cwaves.AmplitudeDistortion;
import cwaves.AnalogPhaseDistortionWaveForm;
import cwaves.ConstantNonClampedCoefficient;
import cwaves.Inverter;
import cwaves.PhaseDistortionWaveForm;
import cwaves.SineWaveform;
import cwaves.SquareWaveform;



/**
 * The parameters of the current song, and methods for interacting with the parameters of the current song.
 * 
 * @author tgreen
 *
 */
public class SongData {
	
	/**
	 * The tempo function in beats per minute.  The input parameter for the function is the beat number.
	 */
	public static BezierCubicNonClampedCoefficientFlatMultiCore TEMPO_BEATS_PER_MINUTE_CRV = null;
	
	/**
	 * The beat number as a function of elapsed seconds.  Generated from TEMPO_BEATS_PER_MINUTE_CRV.
	 */
	public static BezierQuarticNonClampedCoefficientSlopingMultiCore BEAT_NUMBER_FUNCTION_SECONDS = null;
	
	/**
	 * An approximation of elapsed seconds as a function of beat number. Generated from TEMPO_BEATS_PER_MINUTE_CRV.
	 */
	public static BezierCubicNonClampedCoefficientSlopingMultiCore APPROX_SECONDS_FUNCTION_BEAT_NUMBER = null;
	
	/**
	 * For general vibrato (either pitch-vibrato or tremolo), the rate of the global vibrato function in vibrato waves per second.  The input parameter for the function is the beat number.
	 * The presence of a single global vibrato function (essentially a global "vibrato clock") allows the system to emulate e.g. the choral effect where
	 * multiple voices collaborate on a single vibrato.  Note that although the effect is emulated, the mechanism used here has no direct equivalent
	 * in the actual production of music-- actual collaboration on a vibrato is a function of a complex group interaction among
	 * instrumentalists rather than a a product of a prescribed global function.  Nevertheless, the overall effect is similar.
	 */
	public static BezierCubicNonClampedCoefficientFlatMultiCore VIBR_WAVES_PER_SECOND_CRV = null;
	
	/**
	 * For general vibrato (either pitch-vibrato or tremolo), the global vibrato number as a function of elapsed seconds.  Generated from VIBR_WAVES_PER_SECOND_CRV.
	 */
	public static PiecewiseHepticBezierSlopingMultiCore VIBR_FUNCTION_SECONDS = new PiecewiseHepticBezierSlopingMultiCore();
	
	/**
	 * For general vibrato (either pitch-vibrato or tremolo), an approximation of elapsed seconds versus the global vibrato number.  Generated from VIBR_WAVES_PER_SECOND_CRV.
	 */
	public static BezierCubicNonClampedCoefficientSlopingMultiCore APPROX_SECONDS_FUNCTION_VIBR_NUMBER = null;

	
	/**
	 * The number of measures in the song.
	 * Note: this number has no impact on the length of the generated audio.
	 */
	public static int NUM_MEASURES = 300;
	
	/**
	 * The time signature of each measure in terms of beats per measure.
	 * Note: this store has no direct impact on the length of the generated audio.
	 */
	public static MeasuresStore measuresStore = new MeasuresStore();
	
	/**
	 * The starting beat number of he song.
	 * Note: this starting beat number, as opposed to the number of measures, has a direct impact on the length of the generated audio.
	 */
	public static double START_BEAT = 0;
	
	/**
	 * The ending beat number of the song.
	 * Note: this ending beat number, as opposed to the number of measures, has a direct impact on the length of the generated audio.
	 */
	public static double END_BEAT = 300;
	
	
	
	/**
	 * The set of instrument tracks in the song.
	 */
	public static ArrayList<InstrumentTrack> instrumentTracks = new ArrayList<InstrumentTrack>();
	
	/**
	 * The set of pueudo-loops for initializing notes.
	 */
	public static HashMap<String,ArrayList<NoteInitializer>> pseudoLoopMap = new HashMap<String,ArrayList<NoteInitializer>>();
	
	/**
	 * Whether vibrato is skipped when generating audio in a rough-draft mode.
	 */
	public static boolean skipVibratoForRoughDraft = true;
	
	/**
	 * Whether the application of distortion effects (which can be computationally intensive) is skipped when generating audio in a rough-draft mode.
	 */
	public static boolean skipDistortionForRoughDraft = true;
	
	/**
	 * Whether the generated audio should be re-sampled upon the building of new track frames from the tap pad.
	 */
	public static boolean reSampleOnBuild = true;
	
	
	
	/**
	 * The current tempo (in beats per minute) that has been copied into the paste buffer.
	 */
	public static double PASTE_BUFFER_BEATS_PER_MINUTE = 110.0;
	
	/**
	 * The current frequency (in hertz) that has been copied into the paste buffer.
	 */
	public static double PASTE_BUFFER_FREQ = 110.0;
	
	/**
	 * The current note that has been copied into the paste buffer.
	 */
	public static NoteDesc PASTE_BUFFER_NOTE = null;
	
	
	
	
	/**
	 * A default frequency (pitch) in hertz for the first of two notes.
	 */
	public static final double FREQUENCY_X = 
		NoteTable.getCloseNoteDefaultScale_Key( 4 , NoteTable.STEPS_A );
	
	/**
	 * A default frequency (pitch) in hertz for the second of two notes.
	 */
	public static final double FREQUENCY_Y = 
		NoteTable.getCloseNoteDefaultScale_Key( /*2*/ 1 , /*NoteTable.STEPS_D*/ NoteTable.STEPS_A );
	
	
	
	
	/**
     * Default number of samples of oversampling used to determine a PCM (Pulse-Code Modulation) level in a oversampling mode.
     */
    public static final int ROUGH_DRAFT_OVERSAMPLING = 5;
    
    /**
     * Default number of samples of oversampling used to determine a PCM (Pulse-Code Modulation) level in a normal mode.
     */
    public static final int OVERSAMPLING = 20;
    
    /**
     * Whether to generate audio in rough-draft mode.
     */
    public static boolean ROUGH_DRAFT_MODE = true;
    
    /**
     * Whether to calculate the global maximum value to automatically normalize the generated audio.
     */
    public static boolean CALC_GLOBAL_MAX_VAL = false;
	
    
    /**
     * The number of the current track.
     */
    public static int currentTrack = 0;
    
    /**
     * The number of the sampled track from which to digitize note characteristics from the audio.
     */
    public static int voiceMarkTrack = 0;
    
    
    
    
    /**
     * The Bezier approximation rough-draft mode.  Other rough-draft modes are currently TBD.
     */
    public static final int ROUGH_DRAFT_MODE_BEZ_APPROX = 1;
    
    
    
    
    /**
     * Mode describing the kind of rough-draft to use when a Bezier rough-draft approximation is turned on.
     */
    public static int roughDraftMode = ROUGH_DRAFT_MODE_BEZ_APPROX;
    
    /**
     * the number of wavelengths to approximate when a Bezier rough-draft approximation is tuned on.
     */
    protected static int roughDraftBezNumWaves = 5;
    
    /**
     * The number of samples to use per wavelength when a Bezier rough-draft approximation is tuned on.
     */
    protected static double roughDraftBezSamplesPerWave = 200.0;
    
    /**
     * Rough draft coefficient value used by various instances of classes GRoughDraftWaveSwitch and GRoughDraftCoeffSwitch to determine how "rough" the rough draft should be.
     */
    public static double roughDraftWaveCoeff = 1.0;
    
    
    /**
     * The initial vibrato rate (in vibrato waves per second) for a new song.
     */
    public static final double INITIAL_VIBRATO_RATE = 7.0 * 0.83;
    
    
    /**
     * Whether stereo separation has been turned on for the generated audio.
     */
    public static boolean STEREO_ON = false;
    
    /**
     * Constant defining the speed of sound in meters per second.
     */
    public static final double SPEED_SOUND_METERS_SEC = 343;
    
    /**
     * The default stereo separation in meters.
     */
    public static final double STEREO_SEP_METERS = 0.5;
    
    /**
     * The default stereo separation value in seconds for the generated audio when stereo separation has been turned on.
     */
    public static final double STEREO_SEP_SECONDS = STEREO_SEP_METERS / SPEED_SOUND_METERS_SEC;
    
    
    /**
     * The Java-2D flatness parameter to use when displaying curves.
     */
    public static final AazonBaseMutableDbl display_flatness = new AazonBaseMutableDbl( 0.01 );
    
    
    
    /**
     * Handles an update to the maximum beat number by updating data structures that operate as a function of beat number.
     * @param maxBeatNumber The input maximum beat number.
     * @param core The number of the core thread.
     * @throws Throwable
     */
    public static void updateMaxBeat( final double maxBeatNumber , final int core ) throws Throwable
    {
    	for( final InstrumentTrack trk : instrumentTracks )
    	{
    		ArrayList<InterpolationPoint> vol = trk.getTrackVolume(core).getBez().getInterpolationPoints();
    		int sz = vol.size();
    		InterpolationPoint pt = vol.get( sz - 1 );
    		if( ( maxBeatNumber - pt.getParam() ) > 1E-9 )
    		{
    			vol.add( new InterpolationPoint( maxBeatNumber , pt.getValue() ) );
    			trk.getTrackVolume(core).getBez().updateAll();
    			trk.setTrackVolume( trk.getTrackVolume(core) );
    		}
    	}
    	
    	{
    		ArrayList<InterpolationPoint> vol = VIBR_WAVES_PER_SECOND_CRV.getBez().getInterpolationPoints();
    		int sz = vol.size();
    		InterpolationPoint pt = vol.get( sz - 1 );
    		if( ( maxBeatNumber - pt.getParam() ) > 1E-9 )
    		{
    			vol.add( new InterpolationPoint( maxBeatNumber , pt.getValue() ) );
    			VIBR_WAVES_PER_SECOND_CRV.getBez().updateAll();
    		}
    	}
    	
    	{
    		ArrayList<InterpolationPoint> vol = TEMPO_BEATS_PER_MINUTE_CRV.getBez().getInterpolationPoints();
    		int sz = vol.size();
    		InterpolationPoint pt = vol.get( sz - 1 );
    		if( ( maxBeatNumber - pt.getParam() ) > 1E-9 )
    		{
    			vol.add( new InterpolationPoint( maxBeatNumber , pt.getValue() ) );
    			TEMPO_BEATS_PER_MINUTE_CRV.getBez().updateAll();
    		}
    	}
    	
    	SongData.handleTempoUpdate( core );
    }
    
    
    /**
     * Gets the actual ending beat number of the last note in the song.
     * @return The actual ending beat number of the last note in the song.
     */
    public static double getLastActualNoteBeat()
    {
    	double max = 0.0;
    	for( final InstrumentTrack trk : instrumentTracks )
    	{
    		for( final TrackFrame fr : trk.getTrackFrames() )
    		{
    			for ( final NoteDesc desc : fr.getNotes() ) {
    				max = Math.max( max , desc.getActualEndBeatNumber() );
    			}
    		}
    	}
    	return( max );
    }
    
    
    /**
     * Gets the beat number of the last interpolation point for either track volume, tempo, or the vibrato rate curve.
     * @param core The number of the core thread.
     * @return The beat number of the last interpolation point for either track volume, tempo, or the vibrato rate curve.
     */
    public static double getLastInterpBeat( final int core )
    {
    	double max = 0.0;
    	for( final InstrumentTrack trk : instrumentTracks )
    	{
    		ArrayList<InterpolationPoint> vol = trk.getTrackVolume(core).getBez().getInterpolationPoints();
    		int sz = vol.size();
    		InterpolationPoint pt = vol.get( sz - 1 );
    		max = Math.max( max , pt.getParam() );
    	}
    	
    	{
    		ArrayList<InterpolationPoint> vol = VIBR_WAVES_PER_SECOND_CRV.getBez().getInterpolationPoints();
    		int sz = vol.size();
    		InterpolationPoint pt = vol.get( sz - 1 );
    		max = Math.max( max , pt.getParam() );
    	}
    	
    	{
    		ArrayList<InterpolationPoint> vol = TEMPO_BEATS_PER_MINUTE_CRV.getBez().getInterpolationPoints();
    		int sz = vol.size();
    		InterpolationPoint pt = vol.get( sz - 1 );
    		max = Math.max( max , pt.getParam() );
    	}
    	
    	return( max );
    }
    
    
    /**
     * Given the beat number returns a string with the enclosing measure number and per-measure beat number.
     * @param beatNumber The input beat number.
     * @param core The number of the core thread.
     * @return A string with the enclosing measure number and per-measure beat number.
     */
    public static String getMeasureString( double beatNumber , int core )
    {
    	int measure = SongData.measuresStore.getMeasureNumberForBeatNumber(beatNumber, core );
    	double beatRem = SongData.measuresStore.getBeatOnMeasureForBeatNumber(beatNumber, core);
    	return( "" + measure + ":" + beatRem );
    }
    
    
    /**
     * Gets the number of wavelengths to approximate when a Bezier rough-draft approximation is tuned on.
     * @return The number of wavelengths to approximate when a Bezier rough-draft approximation is tuned on.
     */
    public static int getRoughDraftBezNumWaves()
    {
    	return( roughDraftBezNumWaves );
    }
    
    
    /**
     * Gets the number of samples to use per wavelength when a Bezier rough-draft approximation is tuned on.
     * @return The number of samples to use per wavelength when a Bezier rough-draft approximation is tuned on.
     */
    public static double getRoughDraftBezSamplesPerWave()
    {
    	return( roughDraftBezSamplesPerWave );
    }
    
    
    /**
     * Sets the approximation parameters to use when a Bezier rough-draft approximation is tuned on.
     * @param _roughDraftBezNumWaves The number of wavelengths to approximate when a Bezier rough-draft approximation is tuned on.
     * @param _roughDraftBezSamplesPerWave The number of samples to use per wavelength when a Bezier rough-draft approximation is tuned on.
     * @param core The number of the core thread.
     * @throws Throwable
     */
    public static void setApproxParams( int _roughDraftBezNumWaves , double _roughDraftBezSamplesPerWave , final int core ) throws Throwable
    {
    	roughDraftBezNumWaves = _roughDraftBezNumWaves;
    	roughDraftBezSamplesPerWave = _roughDraftBezSamplesPerWave;
    	AgentManager.clearMaps();
    	
    	for( final InstrumentTrack trk : instrumentTracks )
    	{
    		trk.updateTrackFrames( core );
    	}
    }
    
    
    /**
     * Handles a generic OSGi activator change for waves.  Also handles activator changes for Agents, Harmonies, Intonations, etc.
     * @param core The number of the core thread.
     * @throws Throwable
     */
    public static void handleWaveChange( int core ) throws Throwable
    {
    	AgentManager.clearMaps();
    	
    	for( final InstrumentTrack trk : instrumentTracks )
    	{
    		trk.updateTrackFrames( core );
    	}
    }
    
    
    
    /**
     * Returns a WaveForm, or its Bezier rough-draft approximation.
     * @param wave The input WaveForm.
     * @param clss The class of the agent that generated the WaveForm.
     * @param vname The wave name that the agent ascribes to that particular WaveForm.
     * @param useRoughDraft Whether to use a Bezier rough-draft approximation.
     * @return The input WaveFrom, or its Bezier rough-draft approximation.
     * @throws Throwable
     */
    public static WaveForm appxBezWaveform( WaveForm wave , Class clss , String vname , boolean useRoughDraft ) throws Throwable
    {
    	if( !useRoughDraft )
    	{
    		return( wave );
    	}
    	
    	String key = vname;
    	Field fd = clss.getField( "roughDraftWaveformMap" );
    	HashMap<String,WaveForm> hm = (HashMap<String,WaveForm>)( fd.get( null ) );
    	
    	WaveForm tmp = hm.get( key );
    	
    	if( tmp != null )
    	{
    		try
    		{
    			return( (WaveForm)( tmp.genClone() ) );
    		}
    		catch( Throwable ex )
    		{
    			throw( new RuntimeException( "Failed" , ex ) );
    		}
    	}
    	
    	tmp = new BezierMultiWaveAppx( wave , roughDraftBezNumWaves , roughDraftBezSamplesPerWave );
    	
    	hm.put( key , tmp );
    	
    	return( tmp );
    }
    
    
    /**
     * Handles a change to the tempo curve.
     * @param core The number of the core thread.
     */
    public static void handleTempoUpdate( final int core )
    {
    	PiecewiseQuarticBezierSlopingMultiCore quartic = new PiecewiseQuarticBezierSlopingMultiCore();
    	quartic.integrateCurve( TEMPO_BEATS_PER_MINUTE_CRV.getBez() , 1.0 / 60.0 );
    	BEAT_NUMBER_FUNCTION_SECONDS = new BezierQuarticNonClampedCoefficientSlopingMultiCore( quartic );
    	PiecewiseCubicMonotoneBezierSlopingMultiCore approx = new PiecewiseCubicMonotoneBezierSlopingMultiCore();
    	approx.generateApproximation( quartic , 
    			TEMPO_BEATS_PER_MINUTE_CRV.getBez().getInterpolationPoints() , 60.0 , core );
    	APPROX_SECONDS_FUNCTION_BEAT_NUMBER = new BezierCubicNonClampedCoefficientSlopingMultiCore( approx );
    	
    	for( final InstrumentTrack ins : instrumentTracks )
    	{
    		for( final TrackFrame tr : ins.getTrackFrames() )
    		{
    			for ( final NoteDesc desc : tr.getNotes() ) {
    				desc.getFreqAndBend().setWaveInfoDirty( true );
    			}
    		}
    	}
    	
    	handleVibratoUpdate( core );
    }
    
    
    /**
     * Handles a change to the vibrato curve (i.e. the VIBR_WAVES_PER_SECOND_CRV member).
     * @param core The number of the core thread.
     */
    public static void handleVibratoUpdate( final int core )
    {
    	PiecewiseHexticBezierNaturalExtentMultiCore hextic = 
    		PiecewiseHexticBezierNaturalExtentMultiCore.buildHexticCompositeCurveComp( 
    				BEAT_NUMBER_FUNCTION_SECONDS.getBez() , 
    			    VIBR_WAVES_PER_SECOND_CRV.getBez() , 0.0 , core );
    	
    	VIBR_FUNCTION_SECONDS.integrateCurve( hextic  , 1.0 );
    	
    	PiecewiseCubicMonotoneBezierSlopingMultiCore approx = new PiecewiseCubicMonotoneBezierSlopingMultiCore();
    	approx.generateApproximation( VIBR_FUNCTION_SECONDS , 
    			VIBR_WAVES_PER_SECOND_CRV.getBez().getInterpolationPoints() , 1.0 , core );
    	APPROX_SECONDS_FUNCTION_VIBR_NUMBER = new BezierCubicNonClampedCoefficientSlopingMultiCore( approx );
    	
    	for( final InstrumentTrack ins : instrumentTracks )
    	{
    		for( final TrackFrame tr : ins.getTrackFrames() )
    		{
    			for ( final NoteDesc desc : tr.getNotes() ) {
    				// desc.getFreqAndBend().setWaveInfoDirty( true ); !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    			}
    		}
    	}
    }
    
    /**
     * Gets the current track.
     * @return The current track.
     */
    public static InstrumentTrack getCurrentTrack()
    {
    	return( instrumentTracks.get( getCurrentTrackNumber() ) );
    }
    
    /**
     * Gets the number of the sampled track from which to digitize note characteristics from the audio.
     * @return The number of the sampled track from which to digitize note characteristics from the audio.
     */
    public static InstrumentTrack getVoiceMarkTrack()
    {
    	return( instrumentTracks.get( voiceMarkTrack ) );
    }
    
    /**
     * Gets the number of the current instrument track.  Verifies the
     * validity of the number before returning it.  If no
     * tracks exist in the list of tracks, then a track is created
     * to ensure that a valid track number can be returned.
     * @return The current track number.
     */
    private static int getCurrentTrackNumber()
    {
    	if( currentTrack >= ( instrumentTracks.size() ) )
    	{
    		currentTrack = 0;
    		if( instrumentTracks.size() == 0 )
    		{
    			try
    			{
    				InstrumentTrack track = new InstrumentTrack();
    				SongData.instrumentTracks.add(track);
    				track.getTrackFrames().add(SongData.buildNotes());
    			
    				Class<? extends IntelligentAgent> clss = null;
    				while( clss == null )
    				{
    					clss = AgentManager.getMapClone().get( "High Guitar Agent" );
    				
    					if( clss == null )
    					{
    						synchronized( Thread.currentThread() )
    						{
    							Thread.currentThread().wait( 250 );
    						}
    						System.out.println( "Waiting For Agent..." );
    					}
    				}
    			
    				track.setAgent( clss.newInstance() );
    				track.getAgent().setHname( clss.getName() );
    			}
    			catch( Throwable ex )
    			{
    				ex.printStackTrace( System.out );
    			}
    		}
    		SwingUtilities.invokeLater( new Runnable()
    		{
    			public void run()
    			{
    				SongListeners.updateViewPanes();
    			}
    		} );
    	}
    	return( currentTrack );
    }
    
    /**
     * Gets the number of beats played in the generated audio.
     * @return The number of beats played in the generated audio.
     */
    public static double getNumBeatsPlayed()
    {
    	return( END_BEAT - START_BEAT );
    }
	
    /**
     * Gets the start time of the generated audio in seconds.
     * @param core The number of the core thread.
     * @return The start time of the generated audio in seconds.
     */
	public static int getStartTimeSeconds( final int core )
	{
		int start_time_seconds = (int)( getElapsedTimeForBeatBeat( SongData.START_BEAT , core ) );
		return( start_time_seconds );
	}
	
	 /**
     * Gets the end time of the generated audio in seconds.
     * @param core The number of the core thread.
     * @return The end time of the generated audio in seconds.
     */
	public static double getEndTimeSeconds( final int core )
	{
		double end_time_seconds = getElapsedTimeForBeatBeat( SongData.END_BEAT , core );
		return( end_time_seconds );
	}
	
	/**
	 * Gets the beat number at a particular elapsed time.
	 * @param elapsed_time_seconds The input elapsed time in seconds.
	 * @param core The number of the core thread.
	 * @return The beat number.
	 */
	public static double getBeatNumber( final double elapsed_time_seconds , final int core )
	{
		return( BEAT_NUMBER_FUNCTION_SECONDS.eval( elapsed_time_seconds , core ) );
	}
	
	/**
	 * Gets the vibrato wave number (see the description of global vibrato waves in the documentation for the VIBR_WAVES_PER_SECOND_CRV member) at a particular elapsed time.
	 * @param elapsed_time_seconds The input elapsed time in seconds.
	 * @param core The number of the core thread.
	 * @return The vibrato wave number.
	 */
	public static double getVibratoWaveNumber( final double elapsed_time_seconds , final int core )
	{
		return( VIBR_FUNCTION_SECONDS.eval( elapsed_time_seconds , core ) );
	}
	
	/**
	 * Gets the elapsed time in seconds for a particular beat number.
	 * @param beat_number The input beat number.
	 * @param core The number of the core thread.
	 * @return The elapsed time in seconds.
	 */
	public static double getElapsedTimeForBeatBeat( final double beat_number , final int core )
	{
		double appx_sec = APPROX_SECONDS_FUNCTION_BEAT_NUMBER.eval( beat_number , core );
		double appx_beat = BEAT_NUMBER_FUNCTION_SECONDS.eval( appx_sec , core );
		int itcnt = 0;
		while( Math.abs( appx_beat - beat_number ) > 1E-9 )
		{
			double slope = BEAT_NUMBER_FUNCTION_SECONDS.evalSlope( appx_sec , core );
			appx_sec = appx_sec - ( appx_beat - beat_number ) / slope;
			appx_beat = BEAT_NUMBER_FUNCTION_SECONDS.eval( appx_sec , core );
			itcnt++;
			if( itcnt > 1000 )
			{
				throw( new RuntimeException( "Failed!!!!!!!!!!!!!!! " + ( appx_beat - beat_number ) ) );
			}
		}
		return( appx_sec );
	}
	
	/**
	 * Gets the elapsed time in seconds for a particular vibrato wave number.
	 * @param vibr_number The vibrato wave number (see the description of global vibrato waves in the documentation for the VIBR_WAVES_PER_SECOND_CRV member).
	 * @param core The number of the core thread.
	 * @return The elapsed time in seconds.
	 */
	public static double getElapsedTimeForVibrNumber( final double vibr_number , final int core )
	{
		double appx_sec = APPROX_SECONDS_FUNCTION_VIBR_NUMBER.eval( vibr_number , core );
		double appx_vibr = VIBR_FUNCTION_SECONDS.eval( appx_sec , core );
		int itcnt = 0;
		while( Math.abs( appx_vibr - vibr_number ) > 1E-9 )
		{
			double slope = VIBR_FUNCTION_SECONDS.evalSlope( appx_sec , core );
			appx_sec = appx_sec - ( appx_vibr - vibr_number ) / slope;
			appx_vibr = VIBR_FUNCTION_SECONDS.eval( appx_sec , core );
			itcnt++;
			if( itcnt > 1000 )
			{
				throw( new RuntimeException( "Failed!!!!!!!!!!!!!!! " + ( appx_vibr - vibr_number ) ) );
			}
		}
		return( appx_sec );
	}
	
	/**
	 * Gets the elapsed time in seconds for the actual start of a note.
	 * @param desc The input note.
	 * @param core The number of the core thread.
	 * @return The elapsed time in seconds.
	 */
	public static double getElapsedTimeForNoteSecondsActual( NoteDesc desc , final int core )
	{
		return( SongData.getElapsedTimeForBeatBeat( desc.actualStartBeatNumber , core ) );
	}
	
	/**
	 * Gets the actual duration (including sustain) of a note in seconds.
	 * @param dsc The input note.
	 * @param core The number of the core thread.
	 * @return The duration in seconds.
	 */
	public static double getNoteDurationSeconds( NoteDesc dsc , final int core )
	{
		double deltaSecs = getElapsedTimeForBeatBeat( dsc.actualEndBeatNumber , core ) 
			- getElapsedTimeForBeatBeat( dsc.actualStartBeatNumber , core );
		return( deltaSecs );
	}
	
	/**
	 * Gets all of the notes that are playing at a particular beat number, with the exclusion of a particular note.
	 * @param beatNumber The input beat number.
	 * @param exclusionNote The input note to exclude.
	 * @param ignoreOffTracks Whether to ignore notes in tracks that have been turned off.
	 * @param core The number of the core thread.
	 * @return List of all notes that were found at the beat number.
	 */
	public static ArrayList<NoteDesc> getNotesAtTime( double beatNumber , NoteDesc exclusionNote , boolean ignoreOffTracks, final int core )
	{
		ArrayList<NoteDesc> vect = new ArrayList<NoteDesc>();
		for( final InstrumentTrack track : instrumentTracks )
		{
			if( !ignoreOffTracks || ( track.isTrackOn() ) )
			{
				NonClampedCoefficient volumeCoeff = track.getTrackVolume(core);
				ArrayList<TrackFrame> trackFrames = track.getTrackFrames();
				for( final TrackFrame fr : trackFrames )
				{
					NoteDesc desc = fr.getNoteDescActual( beatNumber , core );
					if( ( desc != null ) && ( desc != exclusionNote ) )
					{
						if( ( beatNumber >= desc.getActualStartBeatNumber() ) && 
							( beatNumber <= desc.getActualEndBeatNumber() ) )
						{
							double vol = volumeCoeff.eval( beatNumber );
							desc.setRefcon( new Double( vol ) );
							vect.add( desc );
						}
					}
				}
			}
		}
		return( vect );
	}
	
	
	/**
	 * Gets all of the notes in a track that are playing at a particular beat number, with the exclusion of a particular note.
	 * @param track The input track to search.
	 * @param beatNumber The input beat number.
	 * @param exclusionNote The input note to exclude.
	 * @param core The number of the core thread.
	 * @return List of all notes that were found at the beat number.
	 */
	public static ArrayList<NoteDesc> getNotesAtTime( InstrumentTrack track , double beatNumber , NoteDesc exclusionNote , final int core )
	{
		ArrayList<NoteDesc> vect = new ArrayList<NoteDesc>();
		NonClampedCoefficient volumeCoeff = track.getTrackVolume(core);
		ArrayList<TrackFrame> trackFrames = track.getTrackFrames();
		for( final TrackFrame fr : trackFrames )
		{
			NoteDesc desc = fr.getNoteDescActual( beatNumber , core );
			if( ( desc != null ) && ( desc != exclusionNote ) )
			{
				if( ( beatNumber >= desc.getActualStartBeatNumber() ) && 
						( beatNumber <= desc.getActualEndBeatNumber() ) )
				{
					double vol = volumeCoeff.eval( beatNumber );
					desc.setRefcon( new Double( vol ) );
					vect.add( desc );
				}
			}
		}
		return( vect );
	}
	
	
	/**
	 * Builds a simple curve for overdriving the amplitude.
	 * @return The constructed overdrive curve.
	 */
	public static PiecewiseCubicMonotoneBezierFlat buildOverdriveCurve()
	{
		PiecewiseCubicMonotoneBezierFlat bezBC = new PiecewiseCubicMonotoneBezierFlat();
		bezBC.getInterpolationPoints().add( new InterpolationPoint( -2.0 , -1.0 ) );
		bezBC.getInterpolationPoints().add( new InterpolationPoint( -1.0 , -1.0 ) );
		bezBC.getInterpolationPoints().add( new InterpolationPoint( -0.66 , -1.0 ) );
		bezBC.getInterpolationPoints().add( new InterpolationPoint( -0.33 , -0.66 ) );
		bezBC.getInterpolationPoints().add( new InterpolationPoint( 0.0 , 0.0 ) );
		bezBC.getInterpolationPoints().add( new InterpolationPoint( 0.33 , 0.66 ) );
		bezBC.getInterpolationPoints().add( new InterpolationPoint( 0.66 , 1.0 ) );
		bezBC.getInterpolationPoints().add( new InterpolationPoint( 1.0 , 1.0 ) );
		bezBC.getInterpolationPoints().add( new InterpolationPoint( 2.0 , 1.0 ) );
		bezBC.updateAll();
		return( bezBC );
	}
	
	
	/**
	 * Builds a simple curve for overdriving the amplitude.
	 * @param bezBC The piecewise cubic Bezier curve to configure for overdriving.
	 */
	public static void buildOverdriveCurve( PiecewiseCubicMonotoneBezierFlat bezBC )
	{
		bezBC.getInterpolationPoints().clear();
		bezBC.getInterpolationPoints().add( new InterpolationPoint( -2.0 , -1.0 ) );
		bezBC.getInterpolationPoints().add( new InterpolationPoint( -1.0 , -1.0 ) );
		bezBC.getInterpolationPoints().add( new InterpolationPoint( -0.66 , -1.0 ) );
		bezBC.getInterpolationPoints().add( new InterpolationPoint( -0.33 , -0.66 ) );
		bezBC.getInterpolationPoints().add( new InterpolationPoint( 0.0 , 0.0 ) );
		bezBC.getInterpolationPoints().add( new InterpolationPoint( 0.33 , 0.66 ) );
		bezBC.getInterpolationPoints().add( new InterpolationPoint( 0.66 , 1.0 ) );
		bezBC.getInterpolationPoints().add( new InterpolationPoint( 1.0 , 1.0 ) );
		bezBC.getInterpolationPoints().add( new InterpolationPoint( 2.0 , 1.0 ) );
		bezBC.updateAll();
	}
	
	
	/**
	 * Builds an overdriving distortion for an input WaveForm.
	 * @param wave The input WaveForm to be distorted.
	 * @param bezBC The overdriving amplification function.
	 * @return An overdriven version of the input WaveForm.
	 */
	public static WaveForm buildOverdrive( WaveForm wave , PiecewiseCubicMonotoneBezierFlat bezBC )
	{
		BezierCubicClampedCoefficient drive = new BezierCubicClampedCoefficient( bezBC );
		AmplitudeDistortion dist = new AmplitudeDistortion( wave , drive );
		return( dist );
	}
	
	
	/**
	 * Determines the frequency (pitch) of the note from the note initializer of the agent.
	 * @param noteCount The number of this note within the sequence of notes that is inserted, starting at zero.
	 * @param agent The agent for the note.
	 * @param nd The note into which to set the pitch.
	 * @throws Throwable
	 */
	protected static void configureNoteFreqMode0( int noteCount , IntelligentAgent agent , NoteDesc nd ) throws Throwable
	{
		if( noteCount % 2 == 0 )
		{
			if( agent != null )
			{
				agent.initializeFromInitializers( noteCount , nd );	
			}
			else
			{
				nd.setFrequency( 440.0 );
			}
		}
		else
		{
			if( agent != null )
			{
				agent.initializeFromInitializers( noteCount , nd );	
			}
			else
			{
				nd.setFrequency( 880.0 );
			}
		}
	}
	
	
	/**
	 * Determines the frequency (pitch) of the note by sampling the pitch from the voice-mark track.
	 * @param noteCount The number of this note within the sequence of notes that is inserted, starting at zero.
	 * @param agent The agent for the note.
	 * @param nd The note into which to set the pitch.
	 * @param core The number of the core thread.
	 * @throws Throwable
	 */
	protected static void configureNoteFreqMode1( int noteCount , IntelligentAgent agent , NoteDesc nd , final int core ) throws Throwable
	{
		double beatNumber = 0.5
			* (nd.getActualStartBeatNumber()) + 0.5
			* (nd.getActualEndBeatNumber());
		
		InstrumentTrack voiceMark = SongData.getVoiceMarkTrack();
		
		ArrayList<NoteDesc> notes = SongData.getNotesAtTime(voiceMark, beatNumber, nd, core);
		
		if( notes.size() == 0 )
		{
			System.out.println( "No note present to use.  Using defaults instead." );
			configureNoteFreqMode0( noteCount , agent , nd );
		}
		else
		{
			NoteDesc sampledNote = notes.get( 0 );
		
			NotePitchDigitizer.determineNotePitch( sampledNote , nd , agent );
			double baseFreq = nd.getFreqAndBend().getBaseFreq();
			nd.setFrequency( baseFreq );
		}
	}
	
	
	/**
	 * Determines the frequency (pitch) of the note and the pitch-bend of the note by sampling the pitch from the voice-mark track.
	 * @param agent The agent for the note.
	 * @param nd The note into which to set the pitch.
	 * @param core The number of the core thread.
	 */
	public static void configureNoteFreqMode2( IntelligentAgent agent , NoteDesc nd , final int core )
	{
		double beatNumber = 0.5
			* (nd.getActualStartBeatNumber()) + 0.5
			* (nd.getActualEndBeatNumber());
		
		InstrumentTrack voiceMark = SongData.getVoiceMarkTrack();
		
		ArrayList<NoteDesc> notes = SongData.getNotesAtTime(voiceMark, beatNumber, nd, core);
		
		NoteDesc sampledNote = notes.get( 0 );
		
		NotePitchDigitizer.determineFullPitch( sampledNote , nd );
	}
	
	
	/**
	 * Builds a musical note.
	 * @param mode The mode of the insertion, where 0 and 2 use agent note initializers, and 1 acquires the pitches from the voice-mark track.
	 * @param ti The time interval of the note.
	 * @param noteCount The number of this note within the sequence of notes that is inserted, starting at zero.
	 * @param agent The agent for the note.
	 * @param core The number of the core thread.
	 * @return The generated musical note.
	 * @throws Throwable
	 */
	public static NoteDesc buildNoteDesc( int mode , TimeInterval ti , int noteCount , IntelligentAgent agent , final int core ) throws Throwable
	{
		NoteDesc nd = new NoteDesc();
		
		int sample_start_time_seconds = getStartTimeSeconds( core );
		nd.setStartBeatNumber( getBeatNumber( ti.getStartTimeSeconds() + sample_start_time_seconds , core )  );
		nd.setEndBeatNumber( getBeatNumber( ti.getEndTimeSeconds() + sample_start_time_seconds , core )  );
		nd.setActualStartBeatNumber( getBeatNumber( ti.getStartTimeSeconds() + sample_start_time_seconds , core )  );
		nd.setActualEndBeatNumber( getBeatNumber( ti.getEndTimeSeconds() + sample_start_time_seconds , core )  );
		nd.setSteadyStateWaveNum( 100 );
		
		
		switch( mode )
		{
			case 0:
			case 2:
			{
				configureNoteFreqMode0( noteCount , agent , nd );
			}
			break;
			
			case 1:
			{
				configureNoteFreqMode1( noteCount , agent , nd , core );
			}
			break;
		
		}
		
		
		PhaseDistortionPacket pdc = new PhaseDistortionPacket( new SineWaveform( ) , 1.0 ,  /* 0.2 */ 0.1 );
		PhaseDistortionPacket[] pdcc = { pdc };
		Inverter inv = new Inverter( pdcc );
		// WaveForm wave3 = buildOverdrive( buildOverdrive( inv ) );
		WaveForm wave3 = inv;
		
		nd.setWaveform( wave3 );
		
		if( agent != null )
		{
			agent.setInitialNoteEnvelope( nd , null , 0.0 );
		}
		else
		{
			if( !( nd.isUserDefinedNoteEnvelope() ) )
			{
				PiecewiseCubicMonotoneBezierFlat bezAC = buildGradualDecayBezier( 20 , 0.0 , 1.0 );
				nd.getVibratoParams().buildTremolo( nd , null , 0.0 , bezAC.getInterpolationPoints() , core );
				bezAC.updateAll();
				BezierCubicClampedCoefficient bezA = new BezierCubicClampedCoefficient( bezAC );
		
				nd.setNoteEnvelope( bezA );
			}
		}
		
		ConstantNonClampedCoefficient constCoeff = new ConstantNonClampedCoefficient( 1.0 );
		nd.setWaveEnvelope( constCoeff );
		
		return( nd );
	}
	
	
	
	/**
	 * Builds a musical note.
	 * @param agent The agent for the note.
	 * @param core The number of the core thread.
	 * @param startBeatNumber The user-defined (as opposed to agent-defined) starting beat number of the note.
	 * @param endBeatNumber The user-defined (as opposed to agent-defined) ending beat number of the note.
	 * @param freq The frequency (pitch) of the note in hertz.
	 * @return The generated musical note.
	 * @throws Throwable
	 */
	public static NoteDesc buildNoteDesc( IntelligentAgent agent , final int core ,
			final double startBeatNumber , final double endBeatNumber , final double freq ) throws Throwable
	{
		NoteDesc nd = new NoteDesc();
		
		nd.setStartBeatNumber( startBeatNumber );
		nd.setEndBeatNumber( endBeatNumber );
		nd.setActualStartBeatNumber( startBeatNumber );
		nd.setActualEndBeatNumber( endBeatNumber );
		nd.setSteadyStateWaveNum( 100 );
		
		
		nd.setFrequency( freq ); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		
		
		PhaseDistortionPacket pdc = new PhaseDistortionPacket( new SineWaveform( ) , 1.0 ,  /* 0.2 */ 0.1 );
		PhaseDistortionPacket[] pdcc = { pdc };
		Inverter inv = new Inverter( pdcc );
		// WaveForm wave3 = buildOverdrive( buildOverdrive( inv ) );
		WaveForm wave3 = inv;
		
		nd.setWaveform( wave3 );
		
		if( agent != null )
		{
			agent.setInitialNoteEnvelope( nd , null , 0.0 );
		}
		else
		{
			if( !( nd.isUserDefinedNoteEnvelope() ) )
			{
				PiecewiseCubicMonotoneBezierFlat bezAC = buildGradualDecayBezier( 20 , 0.0 , 1.0 );
				nd.getVibratoParams().buildTremolo( nd , null , 0.0 , bezAC.getInterpolationPoints() , core );
				bezAC.updateAll();
				BezierCubicClampedCoefficient bezA = new BezierCubicClampedCoefficient( bezAC );
		
				nd.setNoteEnvelope( bezA );
			}
		}
		
		ConstantNonClampedCoefficient constCoeff = new ConstantNonClampedCoefficient( 1.0 );
		nd.setWaveEnvelope( constCoeff );
		
		return( nd );
	}
	
	
	
	/**
	 * Builds a sample phase-distorted WaveForm for a bass-clef note.
	 * @param wave0 Input WaveForm.
	 * @return The input WaveForm with phase distortion applied.
	 */
	public static WaveForm buildBassWaveForm( WaveForm wave0 )
	{
		PiecewiseCubicMonotoneBezierFlat bezAC = new PiecewiseCubicMonotoneBezierFlat();
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 0.0 , -10 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 1.0 , -5 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 2.0 , -2.5 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 3.0 , -1.25 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 4.0 , -0.50125 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 5.0 , 0.0 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 6.0 , 0.0 ) );
		bezAC.updateAll();
		
		AnalogPhaseDistortionWaveForm wave =
			new AnalogPhaseDistortionWaveForm( wave0 , bezAC );
		
		return( wave );
	}
	
	
	
	
	
	
	
	/**
	 * Builds an initial track frame for a track, and places an initial set of notes in the track frame.  Currently the initial set of notes is empty.
	 * @return The generated track frame.
	 * @throws Throwable
	 */
	public static TrackFrame buildNotes() throws Throwable
	{
		PhaseDistortionPacket pda = new PhaseDistortionPacket( new SineWaveform() , 1.5 , 0.4 );
		PhaseDistortionPacket[] ph1 = { pda };
		WaveForm w1 = new PhaseDistortionWaveForm( ph1 , new SineWaveform() );
		PhaseDistortionPacket pd = new PhaseDistortionPacket( w1 , 1.5 , 0.4 );
		PhaseDistortionPacket[] phases = { pd };
		WaveForm wave1 = new PhaseDistortionWaveForm( phases , new SineWaveform() );
		
		// WaveForm wave2 = new KochSnowflakeWaveform( null , 0.1 , 0.9 , 0.9 , 0.9 , 1E-6 );
		// WaveForm wave2a = new KochSnowflakeWaveform( 0.25 , 0.75 , 0.5 , 0.5 , 1E-6 , 0.95 );
		WaveForm wave2a = new SquareWaveform( 0.25 );
		PhaseDistortionPacket pdb = new PhaseDistortionPacket( wave2a , 1.5 , 0.01 );
		PhaseDistortionPacket[] pd2 = { pda };
		// WaveForm wave2b = new KochSnowflakeWaveform( 0.25 , 0.75 , 0.5 , 0.5 , 1E-6 , 0.95 );
		WaveForm wave2b = new SquareWaveform( 0.25 );
		WaveForm wave2 = new PhaseDistortionWaveForm( pd2 /* null */ , wave2b );
		// WaveForm wave2 = new KochSnowflakeWaveform( null , 0.01 , 0.99 , 0.99 , 0.99 , 1E-6 );
		
		PhaseDistortionPacket pdcxa = new PhaseDistortionPacket( new SineWaveform( ) , 1.0 ,  /* 0.2 */ 0.1 );
		PhaseDistortionPacket pdcxb = new PhaseDistortionPacket( new SineWaveform( ) , 1.0 ,  /* 0.2 */ 0.6 );
		PhaseDistortionPacket[] pdccxa = { pdcxa };
		PhaseDistortionPacket[] pdccxb = { pdcxb };
		Inverter inva = new Inverter( pdccxa );
		Inverter invb = new Inverter( pdccxb );
		// WaveForm wave3 = buildOverdrive( buildOverdrive( inv ) );
		WaveForm wave3a = buildBassWaveForm( inva );
		WaveForm wave3b = buildBassWaveForm( invb );
		
		ArrayList<NonClampedCoefficient> coefficients = new ArrayList<NonClampedCoefficient>();
		ArrayList<NonClampedCoefficient> coefficientCoefficients = new ArrayList<NonClampedCoefficient>();
		ArrayList<NonClampedCoefficient> parameterCoefficients = new ArrayList<NonClampedCoefficient>();
		
		coefficients.add( wave3b );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( 0.25 ) );
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ) );
		
		WaveForm wave3 = new AdditiveWaveForm( wave3a , new ConstantNonClampedCoefficient( 0.75 ) ,
				coefficients , coefficientCoefficients , parameterCoefficients );

		
		
		TrackFrame frame = new TrackFrame();
		
		NoteDesc dsc1 = new NoteDesc();
		
		/* frame.getNotes().add( dsc1 ); */
		
		dsc1.setFrequency( /* FREQUENCY_Y */ /* 220 */ 220 );
		
//		dsc0.setStartBeatNumber( 2.0 );
//		dsc0.setEndBeatNumber( 4.0 );
		dsc1.setStartBeatNumber( 5.0 );
		dsc1.setEndBeatNumber( 8.0 );
		
		dsc1.setWaveform( /* wave2 */ /* new HighPassFilter( */ wave3 /* , 23.4 , 25 ) */ );
		
		PiecewiseCubicMonotoneBezierFlat bezAC = new PiecewiseCubicMonotoneBezierFlat();
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 0.0 , 0.0 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 0.33 , 1.0 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 0.66 , 1.0 ) );
		bezAC.getInterpolationPoints().add( new InterpolationPoint( 1.0 , 0.0 ) );
		bezAC.updateAll();
		
		PiecewiseCubicMonotoneBezierFlat bezBC = buildGradualDecayBezier( 20 , 0.0 , 1.0 );
		bezBC.updateAll();
		
		BezierCubicClampedCoefficient bezA = new BezierCubicClampedCoefficient( bezAC );
		BezierCubicClampedCoefficient bezB = new BezierCubicClampedCoefficient( bezBC );
		
//		dsc0.setNoteEnvelope( /* bezA */ new ConstantClampedCoefficient( 1.0 ) );
		dsc1.setNoteEnvelope( bezB );
		
//		ConstantNonClampedCoefficient constCoeff = new ConstantNonClampedCoefficient( 1.0 );
		PiecewiseCubicMonotoneBezierFlat bezDrive = buildGradualAttackBezier( 20 , 0.0 , 5.0 );
		bezDrive.updateAll();
		BezierCubicNonClampedCoefficientFlat bzC = new BezierCubicNonClampedCoefficientFlat( bezDrive );
		
//		dsc0.setWaveEnvelope( constCoeff );
		dsc1.setWaveEnvelope( bzC );
		
		/* dsc0.setWaveEnvelope( new ConstantNonClampedCoefficient( null , 0.0 ) );
		dsc1.setWaveEnvelope( new ConstantNonClampedCoefficient( null , 0.0 ) ); */
		
		return( frame );
	}
	
	
	/**
	 * Builds a piecewise cubic functional Bezier approximation of a curve that starts at unity with a flat slope, gradually descends, and ends at zero with a flat slope.
	 * @param max The number of interpolation points to use in the approximation.
	 * @param startInterval The functional parameter value of the start of the interval.
	 * @param endInterval The functional parameter value at the end of the interval.
	 * @return The piecewise cubic functional Bezier approximation.
	 */
	public static PiecewiseCubicMonotoneBezierFlat buildGradualDecayBezier( int max , double startInterval , double endInterval )
	{
		CubicBezierCurve crv = new CubicBezierCurve();
		crv.getBezPts()[ 0 ] = 1.0;
		crv.getBezPts()[ 1 ] = 1.0;
		crv.getBezPts()[ 2 ] = 0.0;
		crv.getBezPts()[ 3 ] = 0.0;
		crv.setStartParam( startInterval );
		crv.setEndParam( endInterval );
		PiecewiseCubicMonotoneBezierFlat bez = new PiecewiseCubicMonotoneBezierFlat();
		int count;
		for( count = 0 ; count < max ; count++ )
		{
			double u = ( (double) count ) / ( max - 1 );
			double uv = (1-u) * startInterval + u * endInterval;
			double val = crv.eval( uv );
			InterpolationPoint pt = new InterpolationPoint( uv , val );
			bez.getInterpolationPoints().add( pt );
		}
		return( bez );
	}
	
	
	/**
	 * Builds a piecewise cubic functional Bezier approximation of a curve that starts at a value with a flat slope, gradually descends, and ends at zero with a flat slope.
	 * @param max The number of interpolation points to use in the approximation.
	 * @param startInterval The functional parameter value of the start of the interval.
	 * @param endInterval The functional parameter value at the end of the interval.
	 * @param topVal The initial value at the start of the interval.
	 * @return The piecewise cubic functional Bezier approximation.
	 */
	public static PiecewiseCubicMonotoneBezierFlat buildGradualDecayBezier( int max , double startInterval , double endInterval , double topVal )
	{
		CubicBezierCurve crv = new CubicBezierCurve();
		crv.getBezPts()[ 0 ] = topVal;
		crv.getBezPts()[ 1 ] = topVal;
		crv.getBezPts()[ 2 ] = 0.0;
		crv.getBezPts()[ 3 ] = 0.0;
		crv.setStartParam( startInterval );
		crv.setEndParam( endInterval );
		PiecewiseCubicMonotoneBezierFlat bez = new PiecewiseCubicMonotoneBezierFlat();
		int count;
		for( count = 0 ; count < max ; count++ )
		{
			double u = ( (double) count ) / ( max - 1 );
			double uv = (1-u) * startInterval + u * endInterval;
			double val = crv.eval( uv );
			InterpolationPoint pt = new InterpolationPoint( uv , val );
			bez.getInterpolationPoints().add( pt );
		}
		return( bez );
	}
	
	
	/**
	 * Builds a piecewise cubic functional Bezier curve for a constant function.
	 * @return The piecewise cubic functional Bezier curve for a constant function.
	 */
	public static PiecewiseCubicMonotoneBezierFlat buildConstBezier( )
	{
		PiecewiseCubicMonotoneBezierFlat bez = new PiecewiseCubicMonotoneBezierFlat();
		
		InterpolationPoint pt = new InterpolationPoint( 0.0 , 1.0 );
		bez.getInterpolationPoints().add( pt );
		
		pt = new InterpolationPoint( 0.5 , 1.0 );
		bez.getInterpolationPoints().add( pt );
		
		pt = new InterpolationPoint( 1.0 , 1.0 );
		bez.getInterpolationPoints().add( pt );
		
		return( bez );
	}
	
	
	/**
	 * Builds a piecewise cubic functional Bezier approximation of a curve that starts at zero with a flat slope, gradually ascends, and ends at unity with a flat slope.
	 * @param max The number of interpolation points to use in the approximation.
	 * @param startInterval The functional parameter value of the start of the interval.
	 * @param endInterval The functional parameter value at the end of the interval.
	 * @return The piecewise cubic functional Bezier approximation.
	 */
	public static PiecewiseCubicMonotoneBezierFlat buildGradualAttackBezier( int max , double startInterval , double endInterval )
	{
		CubicBezierCurve crv = new CubicBezierCurve();
		crv.getBezPts()[ 0 ] = 0.0;
		crv.getBezPts()[ 1 ] = 0.0;
		crv.getBezPts()[ 2 ] = 1.0;
		crv.getBezPts()[ 3 ] = 1.0;
		crv.setStartParam( startInterval );
		crv.setEndParam( endInterval );
		PiecewiseCubicMonotoneBezierFlat bez = new PiecewiseCubicMonotoneBezierFlat();
		int count;
		for( count = 0 ; count < max ; count++ )
		{
			double u = ( (double) count ) / ( max - 1 );
			double uv = (1-u) * startInterval + u * endInterval;
			double val = crv.eval( uv );
			InterpolationPoint pt = new InterpolationPoint( uv , val );
			bez.getInterpolationPoints().add( pt );
		}
		return( bez );
	}
	
	
	/**
	 * Builds a simple pitch-bend curve for a note.
	 * @param interp The output list that receives the interpolation points of the generated pitch-bend.
	 * @param max The number of interpolation points to generate.
	 * @param uMax The maximum of the parameter space over which the hypothetical pitch-bend curve is to extend.
	 * @param mval Ratio describing the amount of pitch-bend to apply.
	 */
	public static void buildBendInterpPoints( final ArrayList<InterpolationPoint> interp , final int max , final double uMax , final double mval )
	{
		final double imval = 1.0 / mval;
		final CubicBezierCurve crvl = new CubicBezierCurve();
		crvl.getBezPts()[ 0 ] = mval;
		crvl.getBezPts()[ 1 ] = mval;
		crvl.getBezPts()[ 2 ] = imval;
		crvl.getBezPts()[ 3 ] = imval;
		crvl.setStartParam( 0.0 );
		crvl.setEndParam( 0.5 );
		final CubicBezierCurve crvh = new CubicBezierCurve();
		crvh.getBezPts()[ 0 ] = imval;
		crvh.getBezPts()[ 1 ] = imval;
		crvh.getBezPts()[ 2 ] = mval;
		crvh.getBezPts()[ 3 ] = mval;
		crvh.setStartParam( 0.5 );
		crvh.setEndParam( 1.0 );
		final double dmax = max;
		interp.clear();
		int count;
		for( count = 0 ; count < max ; count++ )
		{
			double u = ( count / dmax ) * uMax;
			InterpolationPoint pt = new InterpolationPoint();
			pt.setParam( count / dmax );
			if( u > 0.5 )
			{
				pt.setValue( crvh.eval( u ) );
			}
			else
			{
				pt.setValue( crvl.eval( u ) );
			}
			interp.add( pt );
		}
		InterpolationPoint pt = new InterpolationPoint();
		pt.setParam( 1.0 );
		/* if( uMax > 0.5 )
		{
			pt.setValue( crvh.eval( uMax ) );
		}
		else
		{
			pt.setValue( crvl.eval( uMax ) );
		} */
		pt.setValue( mval );
		interp.add( pt );
	}
	
	
	/**
	 * Gets a typical ratio describing the amount of pitch-bend to apply.
	 * @param baseBend Whether the pitch-bend is for a base instrument as opposed to a treble instrument.
	 * @return A typical ratio describing the amount of pitch-bend to apply.
	 */
	public static double getStandardBendValue( boolean baseBend )
	{
		if( baseBend )
		{
			return( Math.pow( 2.0 , 1.0 / ( 12 * 21 ) ) );
		}
		else
		{
			return( Math.pow( 2.0 , 1.0 / ( 12 * 14 ) ) );
		}
	}
	
	
	/**
	 * Builds a simple pitch-bend curve for a note.
	 * @param desc The note to which the pitch-bend is applied.
	 * @param nxt The note after the note to which the pitch-bend is applied.
	 * @param max The number of interpolation points to generate.
	 * @param minDecayTimeBeats The minimum number of beats over which the pitch-bend should decay.
	 * @param baseBend Whether the pitch-bend is for a base instrument as opposed to a treble instrument.
	 * @param core The number of the core thread.
	 */
	public static void buildBendInterpPoints( NoteDesc desc , NoteDesc nxt , final int max , double minDecayTimeBeats , boolean baseBend , final int core )
	{
		double mval = getStandardBendValue( baseBend );
		double umax = ( desc.getActualEndBeatNumber() - desc.getActualStartBeatNumber() ) 
			/ ( minDecayTimeBeats );
		umax = Math.min( umax , 1.0 );
		buildBendInterpPoints( desc , nxt , max , umax , mval , core );
	}
	
	
	/**
	 * Builds a simple pitch-bend curve for a note.
	 * @param desc The note to which the pitch-bend is applied.
	 * @param nxt The note after the note to which the pitch-bend is applied.
	 * @param max The number of interpolation points to generate.
	 * @param minDecayTimeBeats The minimum number of beats over which the pitch-bend should decay.
	 * @param baseBend Whether the pitch-bend is for a base instrument as opposed to a treble instrument.
	 * @param multiplier Multiplier coefficient to apply to the pitch-bend ratio.
	 * @param core The number of the core thread.
	 */
	public static void buildBendInterpPoints( NoteDesc desc , NoteDesc nxt , final int max , double minDecayTimeBeats , boolean baseBend , double multiplier , final int core )
	{
		double mval = getStandardBendValue( baseBend ) * multiplier;
		double umax = ( desc.getActualEndBeatNumber() - desc.getActualStartBeatNumber() ) 
			/ ( minDecayTimeBeats );
		umax = Math.min( umax , 1.0 );
		buildBendInterpPoints( desc , nxt , max , umax , mval , core );
	}
	
	
	/**
	 * Builds a simple pitch-bend curve for a note.
	 * @param desc The note to which the pitch-bend is applied.
	 * @param nxt The note after the note to which the pitch-bend is applied.
	 * @param max The number of interpolation points to generate.
	 * @param uMax The maximum of the parameter space over which the hypothetical pitch-bend curve is to extend.
	 * @param mval Ratio describing the amount of pitch-bend to apply.
	 * @param core The number of the core thread.
	 */
	public static void buildBendInterpPoints( NoteDesc desc , NoteDesc nxt , final int max , final double uMax , final double mval , final int core )
	{
		FreqAndBend freq = desc.getFreqAndBend();
		if( !( freq.isUserDefinedBend() ) )
		{
			PiecewiseCubicMonotoneBezierFlatMultiCore bend = freq.getBendPerNoteU();
			ArrayList<InterpolationPoint> interp = bend.getInterpolationPoints();
			buildBendInterpPoints( interp , max , uMax , mval );
			if( desc.getPortamentoDesc() != null )
			{
				desc.getPortamentoDesc().buildBend( desc , nxt ,
						desc.getFreqAndBend().getBendPerNoteU().getInterpolationPoints() , core );
			}
			desc.getVibratoParams().buildBend( desc , nxt , 
					desc.getFreqAndBend().getBendPerNoteU().getInterpolationPoints() , core );
			bend.updateAll();
			freq.setWaveInfoDirty( true );
		}
	}
	
	
	/**
	 * Builds a flat pitch-bend for a note.
	 * @param freq The pitch-bend description into which to write the flat pitch-bend.
	 */
	public static void buildFlatInterpPoints( FreqAndBend freq )
	{
		if( !( freq.isUserDefinedBend() ) )
		{
			PiecewiseCubicMonotoneBezierFlatMultiCore bend = freq.getBendPerNoteU();
			ArrayList<InterpolationPoint> interp = bend.getInterpolationPoints();
			interp.clear();
			interp.add( new InterpolationPoint(0.0,1.0) );
			interp.add( new InterpolationPoint(0.5,1.0) );
			interp.add( new InterpolationPoint(1.0,1.0) );
			bend.updateAll();
			freq.setWaveInfoDirty( true );
		}
	}
	
	
	/**
	 * Builds a piecewise hextic curve of frequency (in hertz) versus elapsed time (in seconds) for a note.
	 * @param freqPerBeatNumberNote Input cubic curve of frequency (in hertz) versus beat number for the note.
	 * @param startBeatNumberForNoteGlobal The beat number at which the note starts.
	 * @param core The number of the core thread.
	 * @return The piecewise hextic curve of frequency (in hertz) versus elapsed time (in seconds).
	 */
	public static PiecewiseHexticBezierNaturalExtentMultiCore buildHexticCompositeCurve( 
			PiecewiseCubicMonotoneBezierFlatMultiCore freqPerBeatNumberNote , double startBeatNumberForNoteGlobal , final int core )
	{
		return( PiecewiseHexticBezierNaturalExtentMultiCore.buildHexticCompositeCurveComp( BEAT_NUMBER_FUNCTION_SECONDS.getBez() , 
				freqPerBeatNumberNote , startBeatNumberForNoteGlobal , core ) );
	}
	
	
	/**
	 * Captures a pseudo-loop from the pseudo-loop map, and configures an agent to use the pseudo-loop as an initializer.
	 * @param pseudoLoopName The name of the pseudo-loop to capture.
	 * @param out The agent into which to set the pseudo-loop as an initializer.
	 * @throws Exception
	 */
	public static void capturePseudoLoopToAgent( String pseudoLoopName , IntelligentAgent out ) throws Exception
	{
		ArrayList<NoteInitializer> pseudoLoop = pseudoLoopMap.get( pseudoLoopName );
		if( pseudoLoop == null )
		{
			throw( new Exception( "No Loop By That Name!" ) );
		}
		
		out.importInitializers( pseudoLoop );
	}
	
	
	/**
	 * Captures the initializer of an agent, and configures it into a pseudo-loop.
	 * @param in The input agent from which to capture the initializer.
	 * @param pseudoLoopName The name to be given to the generated pseudo-loop.
	 * @throws Exception
	 */
	public static void captureAgentToPseudoLoop( IntelligentAgent in , String pseudoLoopName ) throws Exception
	{
		ArrayList<NoteInitializer> pseudoLoop = in.copyInitializerList();
		
		if( pseudoLoop.size() < 1 )
		{
			throw( new Exception( "TrackFrame doesn't have enough notes!" ) );
		}
		
		pseudoLoopMap.put( pseudoLoopName , pseudoLoop );
	}
	

	/**
	 * Captures the notes from a track frame, and configures them into a pseudo-loop.
	 * @param frame The track frame from which to capture the pseudo-loop.
	 * @param pseudoLoopName The name to be given to the generated pseudo-loop.
	 * @param core The number of the core thread.
	 * @throws Throwable
	 */
	public static void captureTrackFrameToPseudoLoop( TrackFrame frame , String pseudoLoopName , final int core ) throws Throwable
	{
		ArrayList<NoteInitializer> pseudoLoop = new ArrayList<NoteInitializer>();
		
		for ( final NoteDesc desc : frame.getNotes() ) {
			NoteInitializer init = new NoteInitializer();
			init.initFromNoteDesc( desc , core );
			pseudoLoop.add( init );
		}
		
		if( pseudoLoop.size() < 1 )
		{
			throw( new Exception( "TrackFrame doesn't have enough notes!" ) );
		}
		
		pseudoLoopMap.put( pseudoLoopName , pseudoLoop );
	}
	
	
}


