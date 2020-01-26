




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


import gredit.EditPackWave;
import gredit.GWaveForm;

import java.awt.Toolkit;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Iterator;

import meta.DataFormatException;
import meta.VersionBuffer;
import verdantium.VerdantiumPropertiesEditor;
import cwaves.GSwitchGlobalMaxWave;
import aazon.builderNode.AazonTransChld;
import aczon.AczonUnivAllocator;
import bezier.BezierCubicClampedCoefficient;
import bezier.PiecewiseCubicMonotoneBezierFlat;
import cwaves.GAmplitudeRollingWaveForm;


/**
 * Software agent representing how a musician would play a particular instrument.
 * 
 * @author tgreen
 *
 */
public abstract class IntelligentAgent implements EditableAgent , Externalizable {
	
	
	/**
	 * The current note that is being processed within the context of processTrack().
	 */
	protected static transient NoteDesc currentNote = null;
	
	/**
	 * The human-readable name of the agent.
	 */
	protected String hname = "**";
	
	/**
	 * The set of note initializers for the agent.
	 */
	protected ArrayList<NoteInitializer> noteInitializers = new ArrayList<NoteInitializer>();
	
	/**
	 * The vibrato parameters applied to notes by the agent.
	 */
	protected VibratoParameters vibratoParams = new VibratoParameters();
	
	protected EditPackWave editPack1 = new EditPackWave();
	
	protected EditPackWave editPack2 = new EditPackWave();
	
	protected EditPackWave editPack3 = new EditPackWave();

	
	
	/**
	 * Constructs the agent.  This is usually invoked by introspection.
	 */
	public IntelligentAgent() {
		super();
		initializeInitializers();
	}
	
	
	/**
	 * Produces a representation of how a musician would play an instrument on a particular track.
	 * @param track The input InstrumentTrack.
	 * @throws Throwable
	 */
	public abstract void processTrack( InstrumentTrack track ) throws Throwable;
	
	
	/**
	 * Creates the default sequence of NoteInitializer instances for the agent.
	 */
	public abstract void initializeInitializers();

	
	/**
	 * Returns a typical frequency at which the agent plays, usually the frequency of the first NoteInitializer.
	 * @return The typical frequency.
	 */
	public double getFrequencyA() {
		NoteInitializer noti = noteInitializers.get( 0 );
		return( noti.getBaseFreq() );
	}
	
	
	/**
	 * Generates a set of notes from the current set of initializers.
	 * @param count The index into the set of initializers to use on the note.
	 * @param nd The note to initialize.
	 * @throws Throwable
	 */
	public void initializeFromInitializers( int count , NoteDesc nd ) throws Throwable
	{
		final int core = 0;
		int sz = noteInitializers.size();
		int index = count % sz;
		NoteInitializer noti = noteInitializers.get( index );
		noti.initToNoteDesc( nd , core );
	}
	
	
	/**
	 * Generates the initial amplitude interval for a note.
	 * @param nd The note to be processed.
	 * @param nxt The next note in the track frame, or null if this is the last note in the track frame.
	 * @param minDecayTimeBeats The minimum number of beats over which the instrument amplitude can decay without introducing an interruption.
	 */
	public void setInitialNoteEnvelope( NoteDesc nd , NoteDesc nxt , double minDecayTimeBeats ) throws Throwable
	{
		final int core = 0;
		if( !( nd.isUserDefinedVibrato() ) )
		{
			nd.setVibratoParams( new VibratoParameters( vibratoParams ) );
		}
		if( !( nd.isUserDefinedNoteEnvelope() ) )
		{
			PiecewiseCubicMonotoneBezierFlat bezAC = SongData.buildGradualDecayBezier( 20 , 0.0 , 1.0 );
			nd.getVibratoParams().buildTremolo( nd , nxt , minDecayTimeBeats , bezAC.getInterpolationPoints() , core );
			bezAC.updateAll();
			BezierCubicClampedCoefficient bezA = new BezierCubicClampedCoefficient( bezAC );
		
			nd.setNoteEnvelope( bezA );
		}
	}
	
	/**
	 * Imports a new set of note initializers into the agent.
	 * @param inits The note initializers to be imported.
	 */
	public void importInitializers( ArrayList<NoteInitializer> inits )
	{
		noteInitializers.clear();
		for( final NoteInitializer noti : inits )
		{
			noteInitializers.add( noti );
		}
	}
	
	/**
	 * Returns a copy of the set of note initializers for the agent.
	 * @return The set of note initializers for the agent.
	 */
	public ArrayList<NoteInitializer> copyInitializerList()
	{
		ArrayList<NoteInitializer> rt = new ArrayList<NoteInitializer>();
		for( final NoteInitializer noti : noteInitializers )
		{
			rt.add( noti );
		}
		return( rt );
	}

	
	public VibratoParameters getVibratoParams() {
		return vibratoParams;
	}
	
	public EditPackWave getEditPack1()
	{
		return( editPack1 );
	}
	
	public EditPackWave getEditPack2()
	{
		return( editPack2 );
	}
	
	public EditPackWave getEditPack3()
	{
		return( editPack3 );
	}

	/**
	 * @param vibratoParams The vibratoParams to set.
	 */
	public void setVibratoParams(VibratoParameters vibratoParams) {
		this.vibratoParams = vibratoParams;
	}
	
	public VerdantiumPropertiesEditor getVibratoEditor( InstrumentTrack ins )
	{
		return( new VibratoAgentEditor( this.getVibratoParams() , ins ) );
	}
	
	public VerdantiumPropertiesEditor getEditor( InstrumentTrack ins )
	{
		Toolkit.getDefaultToolkit().beep();
		return( null );
	}
	
	public static void applyStdAmplRoll( GWaveForm in , GWaveForm out , EditPackWave pack )
	{
		GAmplitudeRollingWaveForm globalOff = new GAmplitudeRollingWaveForm();
		globalOff.performAssign( in );
		
		GWaveForm globalOn = in;
		
		GSwitchGlobalMaxWave gswitch = new GSwitchGlobalMaxWave();
		gswitch.performAssign( globalOff );
		gswitch.performAssign( globalOn );
		
		out.performAssign( gswitch );
		
		if( pack != null )
		{
			AazonTransChld.initialCoords(AczonUnivAllocator.allocateUniv(),pack.getElem(), null, pack.getWaveOut());
		}
	}
	
	
	public static void applyStdAssign( GWaveForm in , GWaveForm out , EditPackWave pack )
	{
		out.performAssign( in );
		
		if( pack != null )
		{
			AazonTransChld.initialCoords(AczonUnivAllocator.allocateUniv(),pack.getElem(), null, pack.getWaveOut());
		}
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	public void writeExternal(ObjectOutput out) throws IOException {
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setProperty("Hname", hname);
		myv.setProperty("VibratoParams", vibratoParams);
		int sz = noteInitializers.size();
		int count;
		myv.setInt( "sz", sz);
		for( count = 0 ; count < sz ; count++ )
		{
			myv.setProperty(  "index_" + count , noteInitializers.get( count ) );
		}
		myv.setProperty("EditPack1", editPack1);
		myv.setProperty("EditPack2", editPack2);
		myv.setProperty("EditPack3", editPack3);

		out.writeObject(myv);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			hname = (String)( myv.getPropertyEx("Hname") );
			vibratoParams = (VibratoParameters)( myv.getPropertyEx("VibratoParams") );
			noteInitializers.clear();
			int sz = myv.getInt( "sz");
			int count;
			for( count = 0 ; count < sz ; count++ )
			{
				NoteInitializer noti = (NoteInitializer)( myv.getProperty(  "index_" + count ) );
				noteInitializers.add( noti );
			}
			editPack1 = (EditPackWave)( myv.getPropertyEx("EditPack1") );
			editPack2 = (EditPackWave)( myv.getPropertyEx("EditPack2") );
			editPack3 = (EditPackWave)( myv.getPropertyEx("EditPack3") );

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}
	
	
	/**
	 * Gets the current note that is being processed within the context of processTrack().
	 * @return The current note that is being processed within the context of processTrack().
	 */
	public static NoteDesc getCurrentNote()
	{
		return( currentNote );
	}
	
	/**
	 * Gets the human-readable name of the agent.
	 * @return The human-readable name of the agent.
	 */
	public String getHname()
	{
		return( hname );
	}
	
	/**
	 * Sets the human-readable name of the agent.
	 * @param in The human-readable name of the agemt.
	 */
	public void setHname( String in )
	{
		hname = in;
	}
	

}

