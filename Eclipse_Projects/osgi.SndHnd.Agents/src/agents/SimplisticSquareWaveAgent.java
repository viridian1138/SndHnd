package agents;
import gredit.GWaveForm;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;

import meta.DataFormatException;
import meta.VersionBuffer;
import waves.GAppxBezWaveform;
import waves.GRoughDraftWaveSwitch;
import aazon.builderNode.AazonTransChld;
import aczon.AczonUnivAllocator;
import bezier.BezierCubicClampedCoefficient;
import bezier.PiecewiseCubicMonotoneBezierFlat;
import bezier.PiecewiseCubicMonotoneBezierFlatMultiCore;
import core.FreqAndBend;
import core.InstrumentTrack;
import core.IntelligentAgent;
import core.InterpolationPoint;
import core.NoteDesc;
import core.NoteInitializer;
import core.NoteTable;
import core.SongData;
import core.TrackFrame;
import core.VibratoParameters;
import core.WaveForm;
import cwaves.ConstantNonClampedCoefficient;
import cwaves.GSquareWaveform;
import cwaves.SineWaveform;
import cwaves.SquareWaveform;

/**
 * Agent emulating the playing of a relatively simple synthesized square wave (with some added effects like vibrato).
 * 
 * @author tgreen
 *
 */
public class SimplisticSquareWaveAgent extends IntelligentAgent implements Externalizable {
	
	/**
	 * Stores each interpolated rough-draft waveform under a unique name.
	 */
	public static final HashMap<String,WaveForm> roughDraftWaveformMap = new HashMap<String,WaveForm>();

	/**
	 * The minimum number of beats over which the instrument amplitude can decay without introducing an interruption.
	 */
	protected double minDecayTimeBeats = 4.0;

	/**
	 * For an instrument amplitude decay that is interrupted before minDecayTimeBeats, the number of waves over which the interruption of the amplitude takes place.
	 */
	protected double cutoffTimeWaves = 2.5; // ???????????????????????????????????????????

	/**
	 * The duration of the initial attack of a note in waves.
	 */
	protected double attackTimeWaves = 3.0 /* 5.0 */; // ????????????????????????????????????????
	
	/**
	 * The amount of distortion to be used in the modeling of the timbre.
	 */
	protected final double distortionCoeff = 2.0; // 0.51;
	
	/**
	 * Sets the duration of the initial attack of a note in waves.
	 * @param in The duration of the initial attack of a note in waves.
	 */
	public void setAttackTimeWaves( double in )
	{
		attackTimeWaves = in;
	}
	
	
	/**
	 * Gets interpolation points for the pitch bend of a default note parameterized over the barycentric duration of the note.
	 * @return The default interpolation points for the pitch bend.
	 */
	protected static ArrayList<InterpolationPoint> getInterpBend()
	{
		ArrayList<InterpolationPoint> interps = new ArrayList<InterpolationPoint>();
		interps.add( new InterpolationPoint( 0.0 , 1.0 ) );
		interps.add( new InterpolationPoint( 1.0 , 1.0 ) );
		return( interps );
	}
	
	/**
	 * Creates the interpolation points of the default amplitude envelope of a note parameterized over the barycentric duration of the note.
	 * @param interps The list into which to populate the interpolation points of the envelope.
	 */
	public static void createInitialEnvelope( ArrayList<InterpolationPoint> interps )
	{
		interps.add( new InterpolationPoint( 0.0 , 1.0  ) );
		interps.add( new InterpolationPoint( 1.0 , 1.0  ) );
	}
	
	@Override public void setInitialNoteEnvelope( NoteDesc nd , NoteDesc nxt , double minDecayTimeBeats ) throws Throwable
	{
		final int core = 0;
		if( !( nd.isUserDefinedVibrato() ) )
		{
			nd.setVibratoParams( new VibratoParameters( vibratoParams ) );
		}
		if( !( nd.isUserDefinedNoteEnvelope() ) )
		{
			PiecewiseCubicMonotoneBezierFlat bezAC = new PiecewiseCubicMonotoneBezierFlat();
			bezAC.getInterpolationPoints().clear();
			createInitialEnvelope( bezAC.getInterpolationPoints() );
			nd.getVibratoParams().buildTremolo( nd , nxt , minDecayTimeBeats , bezAC.getInterpolationPoints() , core );
			bezAC.updateAll();
			BezierCubicClampedCoefficient bezA = new BezierCubicClampedCoefficient( bezAC );
		
			nd.setNoteEnvelope( bezA );
		}
	}
	
	/**
	 * Returns an adjustment for pitch bend values.  This is an estimate based on critical listening.
	 * @param val The original pitch bend value.
	 * @return The adjusted pitch bend value.
	 */
	protected static double cval( double val )
	{
		double diff = val - 1.0;
		return( 2.8 * diff + 1.0 );
	}
	
	/**
	 * Builds the interpolation points of the pitch bend for a note.
	 * @param interps The array list into which to populate the interpolation points.
	 * @param ratio The ratio by which to affine-scale the parameter space of the interpolation points.
	 */
	protected static void buildInterpBend( ArrayList<InterpolationPoint> interps , double ratio )
	{
		interps.clear();
		InterpolationPoint prev = null;
		for( final InterpolationPoint in : getInterpBend() )
		{
			double oparam = in.getParam() * ratio;
			if( oparam > 1.0 )
			{
				double u = ( 1.0 - prev.getParam() ) / ( oparam - prev.getParam() );
				double val = (1-u) * ( prev.getValue() ) + u * ( in.getValue() );
				interps.add( new InterpolationPoint( 1.0 , cval( val ) ) );
				return;
			}
			else
			{
				interps.add( new InterpolationPoint( oparam , cval( in.getValue() ) ) );
			}
			prev = in;
		}
	}
	
	
	/**
	 * Generates the pitch bend and portamento-related amplitude changes for a note other than the last note of a track frame.
	 * @param desc The note to be processed.
	 * @param nxt The next note in the track frame.
	 * @param minDecayTimeBeats The minimum number of beats over which the instrument amplitude can decay without introducing an interruption.
	 */
	public static void buildBend( NoteDesc desc , NoteDesc nxt , double minDecayTimeBeats ) throws Throwable
	{
		final int core = 0;
		double umax = ( desc.getActualEndBeatNumber() - desc.getActualStartBeatNumber() ) 
			/ ( minDecayTimeBeats );
		umax = Math.min( umax , 1.0 );
		FreqAndBend freq = desc.getFreqAndBend();
		if( !( freq.isUserDefinedBend() ) )
		{
			PiecewiseCubicMonotoneBezierFlatMultiCore bend = freq.getBendPerNoteU();
			ArrayList<InterpolationPoint> interp = bend.getInterpolationPoints();
			buildInterpBend( interp , 1.0 / umax );
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
	 * Generates a rough-draft version of the pitch bend and portamento-related amplitude changes for a note other than the last note of a track frame.
	 * @param desc The note to be processed.
	 * @param nxt The next note in the track frame.
	 * @param minDecayTimeBeats The minimum number of beats over which the instrument amplitude can decay without introducing an interruption.
	 */
	public static void buildBendRoughDraft( NoteDesc desc , NoteDesc nxt , double minDecayTimeBeats ) throws Throwable
	{
		final int core = 0;
		double umax = ( desc.getActualEndBeatNumber() - desc.getActualStartBeatNumber() ) 
			/ ( minDecayTimeBeats );
		umax = Math.min( umax , 1.0 );
		FreqAndBend freq = desc.getFreqAndBend();
		if( !( freq.isUserDefinedBend() ) )
		{
			PiecewiseCubicMonotoneBezierFlatMultiCore bend = freq.getBendPerNoteU();
			ArrayList<InterpolationPoint> interp = bend.getInterpolationPoints();
			buildInterpBend( interp , 1.0 / umax );
			bend.updateAll();
			freq.setWaveInfoDirty( true );
		}
	}
	

	/**
	 * Produces a representation of how a musician would play an instrument for a note other than the last note of a track frame.
	 * @param note1 The note to be processed.
	 * @param note2 The next note after the note to be processed.
	 * @throws Throwable
	 */
	protected void processFirstNote(NoteDesc note1, NoteDesc note2) throws Throwable {
		final int core = 0;
		double decayTimeBeats = note1.getEndBeatNumber()
				- note1.getStartBeatNumber();
		double decayTimeBeatsPlay = Math.max(minDecayTimeBeats, decayTimeBeats);
		note1.setActualEndBeatNumberValidated(note1.getStartBeatNumber()
				+ decayTimeBeatsPlay, note2.getStartBeatNumber());
		note1.setActualStartBeatNumber(note1.getStartBeatNumber());
		setInitialNoteEnvelope( note1 , note2 , minDecayTimeBeats );

		
		note1.setActualNoteEnvelope(note1.getNoteEnvelope( core ));
		note1.setWaveEnvelope( new ConstantNonClampedCoefficient( 1.0 ) );

		
		buildNoteInstrument(note1);
		
		if( SongData.ROUGH_DRAFT_MODE ) 
		{
			buildBendRoughDraft(note1,note2,minDecayTimeBeats);
		}
		else
		{
			buildBend(note1,note2,minDecayTimeBeats);
		}
	}

	/**
	 * Produces a representation of how a musician would play an instrument for the last note of a track frame.
	 * @param note1 The note to be processed.
	 * @throws Throwable
	 */
	protected void processLastNote(NoteDesc note1) throws Throwable {
		final int core = 0;
		double decayTimeBeats = note1.getEndBeatNumber()
				- note1.getStartBeatNumber();
		double decayTimeBeatsPlay = Math.max(minDecayTimeBeats, decayTimeBeats);
		note1.setActualEndBeatNumberValidated(note1.getStartBeatNumber()
				+ decayTimeBeatsPlay, 1E+40);
		note1.setActualStartBeatNumber(note1.getStartBeatNumber());
		setInitialNoteEnvelope( note1 , null , minDecayTimeBeats );
		note1.setActualNoteEnvelope(note1.getNoteEnvelope( core ));
		note1.setWaveEnvelope( new ConstantNonClampedCoefficient( 1.0 ) );
		buildNoteInstrument(note1);
		
		if( SongData.ROUGH_DRAFT_MODE ) 
		{
			buildBendRoughDraft(note1,null,minDecayTimeBeats);
		}
		else
		{
			buildBend(note1,null,minDecayTimeBeats);
		}
		
	}

	
	/**
	 * Represents how a musician would play an instrument for a particular note.
	 * @param note The note for which to build the representation.
	 * @throws Throwable
	 */
	protected void buildNoteInstrument(NoteDesc note) throws Throwable
	{
		currentNote = note;
		buildNoteInstrument(note, false);
		if( SongData.roughDraftMode == SongData.ROUGH_DRAFT_MODE_BEZ_APPROX )
		{
			buildNoteInstrument( note , true );
		}
	}
	

	/**
	 * Represents how a musician would play an instrument for a particular note.
	 * @param note The note for which to build the representation.
	 * @param useRoughDraft Whether the timbre should be constructed as a rough draft.
	 * @throws Throwable
	 */
	protected void buildNoteInstrument(NoteDesc note, boolean useRoughDraft) throws Throwable {
		
		final int core = 0;
		
		WaveForm inva = getEditPack2().processWave( new SineWaveform() );
		WaveForm invb = getEditPack3().processWave( new SineWaveform() );
		

		// WaveForm wave3 = buildOverdrive( buildOverdrive( inv ) );

		/*
		 * if( useInfraSonicHighPass() ) { inva = new HighPassFilter( inva ,
		 * 23.4 , 25 ); invb = new HighPassFilter( invb , 23.4 , 25 ); }
		 */

		WaveForm wave3a = inva;
		WaveForm wave3b = invb;

		
		WaveForm wave3 = editPack1.processWave( wave3a );

		
		note.setWaveform(wave3);
		
		
		wave3 = editPack1.processWave( wave3 );
		
		
		note.setWaveform( wave3 ); //
		
		
		note.setTotalEnvelopeMode( NoteDesc.TOTAL_ENVELOPE_MODE_NONE );
	}


	/**
	 * Produces a representation of how a musician would play an instrument on a particular track frame.
	 * @param tr The track frame.
	 * @throws Throwable
	 */
	protected void processTrackFrame(TrackFrame tr) throws Throwable {
		NoteDesc note1 = null;
		NoteDesc note2 = null;

		ArrayList<NoteDesc> notes = tr.getNotes();
		if (notes.size() > 0) {
			for( final NoteDesc note : notes ) {
				note1 = note2;
				note2 = note;
				if (note1 != null) {
					processFirstNote(note1, note2);
				}
			}

			if (note2 != null) {
				processLastNote(note2);
			} else {
				processLastNote(note1);
			}
		}
	}

	
	/**
	 * Constructs the agent.  This is usually invoked by introspection.
	 */
	public SimplisticSquareWaveAgent() {
		super();
		
		WaveForm inva = new SquareWaveform( 0.25 );
		WaveForm invb = new SquareWaveform( 0.25 );
		
		GWaveForm ina = new GRoughDraftWaveSwitch( inva.genWave( new HashMap() ) , new GSquareWaveform() );
		GWaveForm inb = new GRoughDraftWaveSwitch( invb.genWave( new HashMap() ) , new GSquareWaveform() );
		
		ina = new GAppxBezWaveform( ina , "A" , getClass() );
		inb = new GAppxBezWaveform( inb , "B" , getClass() );
		
		getEditPack2().getWaveOut().performAssign( ina );
		AazonTransChld.initialCoords(AczonUnivAllocator.allocateUniv(),getEditPack2().getElem(), null, getEditPack2().getWaveOut());
		
		getEditPack3().getWaveOut().performAssign( inb );
		AazonTransChld.initialCoords(AczonUnivAllocator.allocateUniv(),getEditPack3().getElem(), null, getEditPack3().getWaveOut());
		
		applyStdAssign( getEditPack1().getWaveIn() , getEditPack1().getWaveOut() , getEditPack1() );
	}
	
	
	
	
	@Override
	public void initializeInitializers()
	{
		NoteInitializer fa = new NoteInitializer( NoteTable.getCloseNoteDefaultScale_Key(3,
				NoteTable.STEPS_E) );
		NoteInitializer fb = new NoteInitializer( NoteTable.getCloseNoteDefaultScale_Key(3,
				NoteTable.STEPS_F) );
		noteInitializers.clear();
		noteInitializers.add( fa );
		noteInitializers.add( fb );
	}

	@Override
	public void processTrack(InstrumentTrack track) throws Throwable {
		final int core = 0;

		track.updateTrackFramesComp( core );

		ArrayList<TrackFrame> trackFrames = track.getTrackFrames();
		for( final TrackFrame tr : trackFrames ) {
			processTrackFrame(tr);
		}

	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setDouble("MinDecayTimeBeats", minDecayTimeBeats);
		myv.setDouble("CutoffTimeWaves", cutoffTimeWaves);
		myv.setDouble("AttackTimeWaves", attackTimeWaves);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			minDecayTimeBeats = myv.getDouble("MinDecayTimeBeats");
			cutoffTimeWaves = myv.getDouble("CutoffTimeWaves");
			attackTimeWaves = myv.getDouble("AttackTimeWaves");

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}
