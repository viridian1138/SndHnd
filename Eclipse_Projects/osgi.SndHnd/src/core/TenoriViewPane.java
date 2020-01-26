




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


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.beans.ExceptionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.Externalizable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.vecmath.Point3d;

import jundo.runtime.ExtMilieuRef;
import labdaw.undo.pdx_VolumeViewPaneModel_pdx_ObjectRef;
import labdaw.undo.pdx_VolumeViewPaneModel_pdx_PairRef;
import meta.DataFormatException;
import meta.Meta;
import verdantium.ProgramDirector;
import verdantium.TransVersionBuffer;
import verdantium.VerdantiumUtils;
import verdantium.undo.UTag;
import verdantium.undo.UndoManager;
import aazon.AazonEnt;
import aazon.AazonImmutableFilledRectangle;
import aazon.AazonImmutableGroup;
import aazon.AazonImmutableOrderedGroup;
import aazon.AazonListener;
import aazon.AazonMutableGroup;
import aazon.AazonSmartIntSwitch;
import aazon.dbl.AazonBaseImmutableDbl;
import aazon.intg.AazonBaseImmutableInt;
import aazon.intg.AazonSimpleMutableInt;
import aazon.vect.AazonBaseImmutableVect;
import abzon.AbzonImmutableGeneralPathFactory;
import abzon.AbzonImmutableShape;
import aczon.AczonColor;
import aczon.AczonCoordinateConvert;
import aczon.AczonResizeVect;
import aczon.AczonRootFactory;



public class TenoriViewPane extends JComponent implements MouseListener,
		MouseMotionListener , PropertyChangeListener {

	/**
	 * The number of vertical levels in the tenori view.
	 */
	protected volatile int numLevels = 12;

	/**
	 * The number of beats to be contained by the tenori columns.
	 */
	protected volatile int numBeats = 4;
	
	/**
	 * The number of tenori columns for each beat.
	 */
	protected volatile int columnsPerBeat = 4;
	
	/**
	 * The number of the melodic interval for the current key.  Melodic interval numbers start at zero.
	 */
	protected volatile int startMelodicInterval = 3;
	
	/**
	 * The starting scale step for the key of the tenori.  The scale steps start at zero.
	 */
	protected volatile int startNote = 0;
	
	/**
	 * The tempo to play the tenori (in beats per minute).
	 */
	protected volatile double tempo = 75;
	
	
	/**
	 * Indicates whether to have statistical variation in pitch.
	 */
	protected boolean spreadPitchOn = false;
	
	/**
	 * The size of the random variation of the pitch (in hertz).
	 */
	protected double spreadPitchSize = 10;
	
	/**
	 * The random number seed with respect to pitch.
	 */
	protected long spreadPitchRandseed = 1554;
	
	
	
	/**
	 * Indicates whether to have statistical variation in time.
	 */
	protected boolean spreadTimeOn = false;
	
	/**
	 * The size of the random variation of the time (in seconds).
	 */
	protected double spreadTimeSize = 0.0085;
	
	/**
	 * The random number seed with respect to time.
	 */
	protected long spreadTimeRandseed = 1553;



	final static int NONEV = 0;
	
	final static int REDV = 1;
	
	final static int GREENV = 2;
	
	final static int DISPLAY_WHITE_V = 3;
	
	protected static final AazonBaseImmutableInt NONE = new AazonBaseImmutableInt( NONEV );
	
	protected static final AazonBaseImmutableInt RED = new AazonBaseImmutableInt( REDV );
	
	protected static final AazonBaseImmutableInt GREEN = new AazonBaseImmutableInt( GREENV );
	
	protected static final AazonBaseImmutableInt DISPLAY_WHITE = new AazonBaseImmutableInt( DISPLAY_WHITE_V );
	
	protected final AazonSimpleMutableInt column = new AazonSimpleMutableInt( new AazonBaseImmutableInt( 0 ) );
	
	
	/**
	 * The values for the matrix of displayed tenori states.
	 */
	protected AazonSimpleMutableInt[][] tenoriStates = null;
	
	/**
	 * The exact frequency for the pitch at each level in hertz.
	 */
	protected double[] freq;
	
	/**
	 * The MIDI pitch number for each pitch.
	 */
	protected double[] fpitch;
	
	/**
	 * The channels in which to play the tenori.
	 */
	protected MultiPlayChannel[] channels;
	
	
	/**
	 * The colored matrix squares displayed for the tenori.
	 */
	protected AazonMutableGroup frontSquares = new AazonMutableGroup();
	
	/**
	 * The front black (actually light gray) lines separating the squares of the tenori.
	 */
	protected AazonMutableGroup frontLineBlack = new AazonMutableGroup();
	
	
	protected AczonRootFactory rootFactory = null;
	
	
	protected AczonResizeVect resizeVect = new AczonResizeVect()
	{
		protected double getInnerX() {
			return (400);
		}

		protected double getInnerY() {
			return (400);
		}
	};
	
	protected AazonListener resizeListener = null;
	
	
	
	
	protected UndoManager undoMgr = null;
	
	ArrayList<TenoriViewPane> tenoriViewPanes;

	protected volatile boolean runThread = false;
	


	public TenoriViewPane( UndoManager _undoMgr , ArrayList<TenoriViewPane> _tenoriViewPanes ) {
		super();
		undoMgr = _undoMgr;
		tenoriViewPanes = _tenoriViewPanes;
		ExtMilieuRef mil = undoMgr.getCurrentMil();
		pdx_VolumeViewPaneModel_pdx_PairRef pair = pdx_VolumeViewPaneModel_pdx_ObjectRef.pdxm_new_VolumeViewPaneModel(
				mil , 0.0 , 0.0 , 0.0 , 0.0 );
		mil = pair.getMilieu();
		undoMgr.handleCommitTempChange( mil );
		undoMgr.addPropertyChangeListener( this );
		refreshStates();
		refreshKey();
	}
	
	
	/**
	 * Refreshes all of the state information for the tenori.
	 */
	public void refreshStates()
	{
		AazonSimpleMutableInt[][] prevStates = new AazonSimpleMutableInt[ 0 ][ 0 ];
		if( tenoriStates != null )
		{
			prevStates = tenoriStates;
		}
		tenoriStates = new AazonSimpleMutableInt[ getNumCols() ][ numLevels ];
		freq = new double[ numLevels ];
		fpitch = new double[ numLevels ];
		channels = new MultiPlayChannel[ numLevels ];
		{
			int notes;
			int levels;
			for( notes = 0 ; notes < getNumCols() ; notes++ )
			{
				for( levels = 0 ; levels < numLevels ; levels++ )
				{
					AazonBaseImmutableInt val = NONE;
					try
					{
						int vval = ( prevStates[ notes ][ levels ] ).getX();
						val = new AazonBaseImmutableInt( vval );
					}
					catch( Exception ex )
					{
						// None.
					}
					tenoriStates[ notes ][ levels ] = new AazonSimpleMutableInt( val );
				}
			}
		}
	}
	
	
	/**
	 * Refreshes the key and the set of pitches for the tenori.
	 */
	public void refreshKey()
	{
		int levels;
		int melodicIntervalNumber = startMelodicInterval;
		final int szz = NoteTable.getScaleSize();
		int note = startNote % szz;
		freq[ 0 ] = NoteTable.getNoteFrequencyDefaultScale_Key(
				melodicIntervalNumber, note);
		for( levels = 1 ; levels < numLevels ; levels++ )
		{
			note++;
			note = note % szz;
			double nFreq = NoteTable.getNoteFrequencyDefaultScale_Key(
					melodicIntervalNumber, note);
			while( nFreq > ( 2.0 * freq[ levels - 1 ] ) )
			{
				melodicIntervalNumber--;
				nFreq = NoteTable.getNoteFrequencyDefaultScale_Key(
						melodicIntervalNumber, note);
			}
			while( nFreq <= freq[ levels - 1 ] )
			{
				melodicIntervalNumber++;
				nFreq = NoteTable.getNoteFrequencyDefaultScale_Key(
						melodicIntervalNumber, note);
			}
			freq[ levels ] = nFreq;
		}
		
		for( levels = 0 ; levels < numLevels ; levels++ )
		{
			double freqDiv = freq[ levels ] / 261.6; // Divide frequency by middle C frequency.
			double flog2 = Math.log( freqDiv ) / Math.log( 2 );
			double fpitchI = flog2 * 12.0 + 60.0;
			fpitch[ levels ] = fpitchI;
		}
	}
	
	
	/**
	 * Prints the list of pitches in the current intonation at the various tenori levels.
	 */
	public void printPitchList()
	{
		int levels;
		int melodicIntervalNumber = startMelodicInterval;
		final int szz = NoteTable.getScaleSize();
		int note = startNote % szz;
		final String[] spitch = new String[ numLevels ];
		final double[] freq = new double[ numLevels ];
		final String[] scaleNames = NoteTable.getScaleNamesDefaultScale_Key( );
		freq[ 0 ] = NoteTable.getNoteFrequencyDefaultScale_Key(
				melodicIntervalNumber, note);
		spitch[ 0 ] = melodicIntervalNumber + "." + scaleNames[ note ];
		for( levels = 1 ; levels < numLevels ; levels++ )
		{
			note++;
			note = note % szz;
			double nFreq = NoteTable.getNoteFrequencyDefaultScale_Key(
					melodicIntervalNumber, note);
			while( nFreq > ( 2.0 * freq[ levels - 1 ] ) )
			{
				melodicIntervalNumber--;
				nFreq = NoteTable.getNoteFrequencyDefaultScale_Key(
						melodicIntervalNumber, note);
			}
			while( nFreq <= freq[ levels - 1 ] )
			{
				melodicIntervalNumber++;
				nFreq = NoteTable.getNoteFrequencyDefaultScale_Key(
						melodicIntervalNumber, note);
			}
			freq[ levels ] = nFreq;
			spitch[ levels ] = melodicIntervalNumber + "." + scaleNames[ note ];
		}
		
		for( levels = numLevels - 1 ; levels >= 0 ; levels-- )
		{
			System.out.println( spitch[ levels ] );
		}
	}
	
	
	/**
	* Handles property change events.
	*/
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName() == UndoManager.MODEL_STATE_CHANGED) {
			handleUndoStateChange();
		}

	}
	
	
	/**
	 * Handles the execution of "undo".
	 */
	public void handleUndoStateChange()
	{
		ExtMilieuRef mil = undoMgr.getCurrentMil();
	}

	


	
public void buildDisplayList() {
	
	setLayout(new BorderLayout(0,0));
	AczonRootFactory scene = createSceneGraph();

	add(BorderLayout.CENTER, scene.getCanvas() );
	
	final AazonListener al = new AazonListener()
	{

		public void handleListen() {
			refreshSquares();
		}
		
	};
	resizeListener = al;
	resizeVect.setFactory( scene );
	scene.getCanvas().addResizeVect( resizeVect );
	resizeVect.add( resizeListener );
}

	protected AczonRootFactory createSceneGraph() {
		
		refreshSquares();
		
		AazonEnt[] objOrderEnts = { frontSquares , frontLineBlack };
		final AazonImmutableOrderedGroup objOrderEnt = new AazonImmutableOrderedGroup( objOrderEnts );
		
		rootFactory = new AczonRootFactory( objOrderEnt );
		
		
		rootFactory.getCanvas().addMouseListener(this);
		rootFactory.getCanvas().addMouseMotionListener(this);

		return (rootFactory);
	}
	
	
	protected void refreshSquares()
	{
		ArrayList<AazonEnt> frontSquaresL = new ArrayList<AazonEnt>();
		
		int row;
		
		int column;
		
		for( column = 0 ; column < getNumCols() ; column++ )
		{
			double ux1 = 1.0 * column / getNumCols();
			double ux2 = 1.0 * ( column + 1 ) / getNumCols();
			double x1 = ( 1.0 - ux1 ) * ( -1.0 ) + ux1 * ( 1.0 );
			double x2 = ( 1.0 - ux2 ) * ( -1.0 ) + ux2 * ( 1.0 );
			
		
		for( row = 0 ; row < numLevels ; row++ )
		{		
			double uy1 = 1.0 * row / numLevels;
			double uy2 = 1.0 * ( row + 1 ) / numLevels;
			double y1 = ( 1.0 - uy1 ) * ( -calcYRatio() ) + uy1 * ( calcYRatio() );
			double y2 = ( 1.0 - uy2 ) * ( -calcYRatio() ) + uy2 * ( calcYRatio() );
			
		AazonEnt none = new AazonImmutableFilledRectangle( 
				new AazonBaseImmutableVect( x1 ,y1 ) ,
				new AazonBaseImmutableVect( x2 , y2 ) ,
				AczonColor.getFillBlack() );
		
		AazonEnt red = new AazonImmutableFilledRectangle( 
				new AazonBaseImmutableVect( x1 ,y1 ) ,
				new AazonBaseImmutableVect( x2 , y2 ) ,
				AczonColor.getFillRed() );
		
		AazonEnt green = new AazonImmutableFilledRectangle( 
				new AazonBaseImmutableVect( x1 , y1 ) ,
				new AazonBaseImmutableVect( x2 , y2 ) ,
				AczonColor.getFillGreen() );
		
		AazonEnt[] ents = { none , red , green };
		
		AazonEnt rectSwitch = AazonSmartIntSwitch.construct( ents , tenoriStates[ column ][ row ] );
		
		frontSquaresL.add( rectSwitch );
		
		}
		
		}
		
		AazonImmutableGroup frontSquaresA = new AazonImmutableGroup( frontSquaresL );
		
		frontSquares.setGrp( frontSquaresA );
		updateFrontLineBlack();
	}
	
	
	/**
	 * Updates the front black (actually light gray) lines separating the squares of the tenori.
	 */
	protected void updateFrontLineBlack( )
	{
		GeneralPath gp = new GeneralPath();
		int count;
		
		for( count = 0 ; count <= getNumCols() ; count++ )
		{
			double u = 1.0 * count / getNumCols();
			double x = ( 1.0 - u ) * ( -1.0 ) + u * ( 1.0 );
			gp.moveTo( (float)( x ) , (float)( -calcYRatio() ) );
			gp.lineTo( (float)( x ) , (float)( calcYRatio() ) );
		}
		
		for( count = 0 ; count <= numLevels ; count++ )
		{
			double u = 1.0 * count / numLevels;
			double y = ( 1.0 - u ) * ( -calcYRatio() ) + u * ( calcYRatio() );
			gp.moveTo( (float)( -1.0 ) , (float)( y ) );
			gp.lineTo( (float)( 1.0 ) , (float)( y ) );
		}
		
		AbzonImmutableGeneralPathFactory ig = new AbzonImmutableGeneralPathFactory( gp );
		AbzonImmutableShape sp = new AbzonImmutableShape( ig , new AazonBaseImmutableDbl( 0.001 ) , new AffineTransform() , AczonColor.getLineLightGray() );
		AazonEnt[] ents = { sp };
		AazonImmutableGroup igrp = new AazonImmutableGroup( ents );
		frontLineBlack.setGrp( igrp );
	}
	
	
	/**
	 * Handles the playing of a column of the tenori.
	 */
	protected void handleColumn()
	{
		int col = column.getX();
		int prevCol = col - 1;
		while( prevCol < 0 )
		{
			prevCol += getNumCols();
		}
		int level;
		for( level = 0 ; level < numLevels ; level++ )
		{
			int prev = ( tenoriStates[ prevCol ][ level ] ).getX();
			int curr = ( tenoriStates[ col ][ level ] ).getX();
			if( ( prev != NONEV ) || ( curr != NONEV ) )
			{
				try
				{
					if( channels[ level ] == null )
					{
						channels[ level ] = new MultiPlayChannel( );
						MultiPlayChannel c = channels[ level ];
					}
					if( ( prev != NONEV ) && ( curr != GREENV ) )
					{
						( channels[ level ] ).stop( );
					}
				}
				catch( Throwable ex )
				{
					ex.printStackTrace( System.out );
				}
			}
		}
		for( level = 0 ; level < numLevels ; level++ )
		{
			int prev = ( tenoriStates[ prevCol ][ level ] ).getX();
			int curr = ( tenoriStates[ col ][ level ] ).getX();
			if( curr != NONEV )
			{
				try
				{
					if( ( prev == NONEV ) || ( curr == REDV ) )
					{
						( channels[ level ] ).play( fpitch[ level ] );
					}
				}
				catch( Throwable ex )
				{
					ex.printStackTrace( System.out );
				}
			}
		}
	}
	
	
	/**
	 * Clones the tenori.
	 */
	public void cloneTenori()
	{
		UTag utag = new UTag();
		undoMgr.prepareForTempCommit(utag);
		final TenoriViewPane tenoriViewPane = new TenoriViewPane( undoMgr , tenoriViewPanes );
		TenoriViewPaneController controller = new TenoriViewPaneController(
				tenoriViewPane);
		tenoriViewPane.buildDisplayList();
		undoMgr.commitUndoableOp(utag, "Create tenoriViewPane" );
		undoMgr.clearUndoMemory();
		final JFrame fr = new JFrame();
		fr.getContentPane().setLayout(new BorderLayout(0, 0));
		fr.getContentPane().add(BorderLayout.CENTER, tenoriViewPane);
		fr.getContentPane().add(BorderLayout.SOUTH, controller);
		fr.pack();
		fr.show();
		tenoriViewPanes.add( tenoriViewPane );
		
		tenoriViewPane.setNumBeats( getNumBeats() );
		tenoriViewPane.setColumnsPerBeat( getColumnsPerBeat() );
		tenoriViewPane.setNumLevels( getNumLevels() );
		tenoriViewPane.setStartMelodicInterval( getStartMelodicInterval() );
		tenoriViewPane.setTempo( getTempo() );
		tenoriViewPane.setStartNote( getStartNote() );
		tenoriViewPane.refreshStates();
		tenoriViewPane.refreshKey();
		tenoriViewPane.refreshSquares();
		tenoriViewPane.cloneTenoriStates( tenoriStates );
		
		fr.addWindowListener( new WindowAdapter()
		{
			/**
			 * Indicates whether the window has been previously closed.
			 */
			boolean closed = false;
			
			@Override
			public void windowClosing( WindowEvent e )
			{
				if( !closed )
				{
					fr.setVisible( false );
					fr.dispose();
					closed = true;
					tenoriViewPanes.remove( tenoriViewPane );
				}
			}
			
			@Override
			public void windowClosed( WindowEvent e )
			{
				if( !closed )
				{
					fr.setVisible( false );
					fr.dispose();
					closed = true;
					tenoriViewPanes.remove( tenoriViewPane );
				}
			}
		} );
	}
	
	
	/**
	 * Clones the states of the cells in the tenori matrix.
	 * @param _tenoriStates Output array to contain a copy of the tenori cell states.
	 */
	public void cloneTenoriStates( AazonSimpleMutableInt[][] _tenoriStates )
	{
		int row;
		int col;
		
		for( row = 0 ; row < numLevels ; row++ )
		{
			for( col = 0 ; col < getNumCols() ; col++ )
			{
				int val = ( _tenoriStates[ col ][ row ] ).getX();
				if( val != ( ( tenoriStates[ col ][ row ] ).getX() ) )
				{
					( tenoriStates[ col ][ row ] ).setCoords( new AazonBaseImmutableInt( val ) );
				}
			}
		}
	}
	
	
	/**
	 * Plays the tenori.
	 */
	public void play()
	{
		if( !runThread )
		{
			runThread = true;
			refreshKey();
			Runnable runn = new Runnable()
			{
				public void run()
				{
					int col;
					while( runThread )
					{
						for( col = 0 ; col < getNumCols() ; col++ )
						{
							column.setCoords( new AazonBaseImmutableInt( col ) );
							handleColumn();
							try
							{
								Thread.sleep( (int)( 1000 * 60 / ( tempo * columnsPerBeat ) ) );
							}
							catch( Throwable ex )
							{
								ex.printStackTrace( System.out );
							}
						}
					}
				}
			};
			( new Thread( runn ) ).start();
		}
	}
	
	
	/**
	 * Stops playing of the tenori.
	 */
	public void stop()
	{
		runThread = false;
	}
	
	
	/**
	 * Displays an editor for changing the key of the tenori.
	 */
	public void editKey()
	{
		TenoriViewPaneKeyEditor editor = new TenoriViewPaneKeyEditor( this );
		ProgramDirector.showPropertyEditor(editor, null,
			"TenoriViewPaneKey Properties");
	}
	
	
	/**
	 * Displays an editor for adding statistical variation to model human error in timing and pitch.
	 */
	public void editSpread()
	{
		TenoriViewPaneSpreadEditor editor = new TenoriViewPaneSpreadEditor( this );
		ProgramDirector.showPropertyEditor(editor, null,
			"TenoriViewPaneSpread Properties");
	}
	
	
	/**
	 * Displays an editor for changing the vertical and horizontal grid size of the tenori.
	 */
	public void editGrid()
	{
		TenoriViewPaneGridEditor editor = new TenoriViewPaneGridEditor( this );
		ProgramDirector.showPropertyEditor(editor, null,
			"TenoriViewPaneGrid Properties");
	}
	
	
	/**
	 * Displays an editor for splitting the tenori.
	 */
	public void autoSplit()
	{
		TenoriViewPaneAutoSplitEditor editor = new TenoriViewPaneAutoSplitEditor( this );
		ProgramDirector.showPropertyEditor(editor, null,
			"AutoSplit");
	}
	
	
	/**
	 * Rotates the entire tenori one cell to the right.
	 */
	public void rotateRight()
	{
		int[][] prevStates = new int[ getNumCols() ][ numLevels ];
		int row;
		int col;
		for( row = 0 ; row < numLevels ; row++ )
		{
			for( col = 0 ; col < getNumCols() ; col++ )
			{
				prevStates[ col ][ row ] = ( tenoriStates[ col ][ row ] ).getX();
			}
		}
		
		for( row = 0 ; row < numLevels ; row++ )
		{
			for( col = 0 ; col < getNumCols() ; col++ )
			{
				int colx = ( col + ( getNumCols() - 1 ) ) % ( getNumCols() );
				int val = prevStates[ colx ][ row ];
				if( val != ( ( tenoriStates[ col ][ row ] ).getX() ) )
				{
					( tenoriStates[ col ][ row ] ).setCoords( new AazonBaseImmutableInt( val ) );
				}
			}
		}
		
	}
	
	
	/**
	 * Rotates the entire tenori one cell to the left.
	 */
	public void rotateLeft()
	{
		int[][] prevStates = new int[ getNumCols() ][ numLevels ];
		int row;
		int col;
		for( row = 0 ; row < numLevels ; row++ )
		{
			for( col = 0 ; col < getNumCols() ; col++ )
			{
				prevStates[ col ][ row ] = ( tenoriStates[ col ][ row ] ).getX();
			}
		}
		
		for( row = 0 ; row < numLevels ; row++ )
		{
			for( col = 0 ; col < getNumCols() ; col++ )
			{
				int colx = ( col + 1 ) % ( getNumCols() );
				int val = prevStates[ colx ][ row ];
				if( val != ( ( tenoriStates[ col ][ row ] ).getX() ) )
				{
					( tenoriStates[ col ][ row ] ).setCoords( new AazonBaseImmutableInt( val ) );
				}
			}
		}
	}
	
	
	/**
	 * Displays an editor for inserting tenori notes into the current song.
	 */
	public void insertNotes()
	{
		TenoriViewPaneInsertEditor editor = new TenoriViewPaneInsertEditor( this );
		ProgramDirector.showPropertyEditor(editor, null,
			"TenoriViewPaneInsert Properties");
	}
	
	
	/**
	 * Inserts notes from the tenori view pane into the song.
	 * @param startMeasure The measure number at which to begin inserting notes from the tenori.  Measure numbers start at zero.
	 * @param startBeatPerMeasure The beat number in the measure at which to begin inserting notes from the tenori.  Beat numbers start at zero.
	 * @param numRepeats The number of times the tenori is repeated during the insertion.
	 * @param userDefinedEnd Whether the inserted notes have user-defined ends, as opposed to agent-defined ends.
	 */
	public void insertNotes( final int startMeasure , final double startBeatPerMeasure , 
			final int numRepeats , final boolean userDefinedEnd )
	{
		final double startBeatNumber = SongData.measuresStore.getBeatNumberForMeasureNumber( startMeasure )
			+ ( startBeatPerMeasure - 1 );
		
		int[][] prevStates = new int[ getNumCols() ][ numLevels ];
		int row;
		int col;
		for( row = 0 ; row < numLevels ; row++ )
		{
			for( col = 0 ; col < getNumCols() ; col++ )
			{
				prevStates[ col ][ row ] = ( tenoriStates[ col ][ row ] ).getX();
			}
		}
		
		double[] colFreqs = new double[ getNumCols() ];
		int[] colBeats = new int[ getNumCols() ];
		for( col = 0 ; col < getNumCols() ; col++ )
		{
			int cnt = 0;
			for( row = 0 ; row < numLevels ; row++ )
			{
				int tmp = prevStates[ col ][ row ];
				if( tmp != NONEV )
				{
					cnt++;
					int acol = col - 1;
					if( acol < 0 )
					{
						acol = getNumCols() - 1;
					}
					if( ( tmp == REDV ) || ( ( prevStates[ acol ][ row ] ) == NONEV ) )
					{
						colFreqs[ col ] = freq[ row ];
						colBeats[ col ] = 1;
						int bcol = col + 1;
						int kcnt = 0;
						bcol = bcol % ( getNumCols() );
						while( ( prevStates[ bcol ][ row ] == GREENV )  && ( kcnt <= getNumCols() ) )
						{
							colBeats[ col ]++;
							bcol++;
							bcol = bcol % ( getNumCols() );
							kcnt++;
						}
					}
				}
				if( cnt > 1 )
				{
					throw( new RuntimeException( "Overfilled Row" ) );
				}
			}
		}
		
		final int core = 0;
		TrackFrame tr = new TrackFrame();
		InstrumentTrack track = SongData.instrumentTracks
				.get(SongData.currentTrack);
		IntelligentAgent agent = track.getAgent();
		ArrayList<NoteDesc> tvect = new ArrayList<NoteDesc>();

		Random randFreq =  null;
		if( spreadPitchOn )
		{
			randFreq = new Random( spreadPitchRandseed );
		}
		
		Random randTime = null;
		if( spreadTimeOn )
		{
			randTime = new Random( spreadTimeRandseed );
		}
		
		try
		{
			int acount;
			for( acount = 0 ; acount < numRepeats ; acount++ )
			{
				double acountOffset = ( (double)( acount * ( getNumCols() ) ) ) / columnsPerBeat;
				for( col = 0 ; col < getNumCols() ; col++ ) {
					if( colBeats[ col ] > 0 )
					{
						double colOffset = ( (double)( col ) ) / columnsPerBeat;
						double startBeat = startBeatNumber + acountOffset + colOffset;
						double endBeat = startBeat + ( ( (double)( colBeats[ col ] ) ) / columnsPerBeat );
						
						if( spreadTimeOn )
						{
							double startTime = SongData.getElapsedTimeForBeatBeat( startBeat, core);
							double endTime = SongData.getElapsedTimeForBeatBeat( endBeat, core);
							
							startTime += spreadTimeSize * ( randTime.nextGaussian() );
							endTime += spreadTimeSize * ( randTime.nextGaussian() );
							
							startBeat = SongData.getBeatNumber( startTime , core);
							endBeat = SongData.getBeatNumber( endTime , core);
							
							if( endBeat < startBeat )
							{
								endBeat = startBeat;
							}
						}
						
						double freq = colFreqs[ col ];
						
						if( spreadPitchOn )
						{
							double fa = ( Math.log( freq ) / Math.log( 2.0 ) ) * ( 12.0 * 100.0 );
							fa += spreadPitchSize * ( randFreq.nextGaussian() );
							double fb = Math.pow( 2.0 , fa / ( 12.0 * 100.0 ) );
							freq = fb;
						}
						
						NoteDesc nd = SongData.buildNoteDesc( agent , core ,
								startBeat , endBeat , freq );
						nd.setUserDefinedEnd( userDefinedEnd );
						tvect.add(nd);
						tr.getNotes().add(nd);
					}
				}
			}

			track.getTrackFrames().add(tr);
			track.updateTrackFrames(core);
			SongListeners.updateViewPanes();
		}
		catch( Throwable ex )
		{
			ex.printStackTrace( System.out );
		}
	}
	
	

	protected double startBeatU;

	protected double levelU;

	protected double beatX;

	protected double levelY;

	

	protected double calcYRatio()
	{
		double dx = resizeVect.getX();
		double dy = resizeVect.getY();
		if( dx < 1.0 ) dx = 1.0;
		return( Math.abs( dy ) / Math.abs( dx ) );
	}
	
	protected void calcXYFromU(double beatU, double levelU) {
		double aX = -1.0;
		double aY = -calcYRatio();
		double bX = 1.0;
		double bY = calcYRatio();

		double cX = aX;
		double cY = aY;
		double dX = bX;
		double dY = bY;

		beatX = (1.0 - beatU) * cX + beatU * dX;
		levelY = (1.0 - levelU) * cY + levelU * dY;
	}

	protected void calcUFromXY(double x, double y) {
		double aX = -1.0;
		double aY = -calcYRatio();
		double bX = 1.0;
		double bY = calcYRatio();

		double cX = aX;
		double cY = aY;
		double dX = bX;
		double dY = bY;

		startBeatU = (x - cX) / (dX - cX);
		levelU = (y - cY) / (dY - cY);
	}
	
	
	protected Point3d calcMouseCoord( int x , int y )
	{
		return( AczonCoordinateConvert.convertCoords(x, y, rootFactory ) );
	}
	

	/**
	 * Handles a mouse-down event by applying a change to the tenori matrix.
	 * @param e The mouse event.
	 */
	protected void mouserDown(MouseEvent e) {
		Point3d pt = calcMouseCoord( e.getX() , e.getY() );
		
		calcUFromXY( pt.x , pt.y );
		
		int column = (int)( startBeatU * getNumCols() );
		
		int row = (int)( levelU * numLevels );
		
		if( e.isPopupTrigger() )
		{
			double pitcha = freq[ row ];
			NoteTable.playNote( pitcha );
			return;
		}
		
		AazonSimpleMutableInt mint = tenoriStates[ column ][ row ];
		
		int val = mint.getX();
		
		switch ( val ) {

		case NONEV:
		{
			mint.setCoords( RED );
		}
			break;	
			
		case REDV: 
		{
			mint.setCoords( GREEN );
		}
			break;
			
		case GREENV: 
		{
			mint.setCoords( NONE );
		}
			break;

		}
	}
	
	
	/**
	 * Saves the tenori to aa file.
	 */
	public void saveTenori()
	{
		try {
			String FileName = null;

			JFileChooser fd = new JFileChooser();
			
			Component parent = this;
			while( !( parent instanceof Window ) && ( parent != null ) )
			{
				parent = parent.getParent();
			}
			
			int returnVal = fd.showSaveDialog(parent);

			if (returnVal != JFileChooser.APPROVE_OPTION)
				return;

			File MyFile = fd.getSelectedFile();
			FileName = MyFile.getPath();

			URL MyU = null;
			try {
				MyU = MyFile.toURL();
			} catch (Exception ex) {
				VerdantiumUtils
					.produceMessageWindow(
						ex,
						"Bad File Name",
						"Bad File Name",
						"Bad file name.  Please try again.",
						null,
						null /* !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! */
				);
				return;
			}
			
			TransVersionBuffer OutTrans = generateWriteBuffer();
			
			writeTenoriInfo(MyU, OutTrans);
			
		} catch (Throwable ex) {
			ex.printStackTrace( System.out );
		}
	}
	
	
	/**
	 * Writes the tenori state to a version buffer.
	 * @return The version buffer containing the tenori state.
	 */
	protected TransVersionBuffer generateWriteBuffer()
	{
		TransVersionBuffer MyF =
			new TransVersionBuffer("Tenori", "Tenori");
		MyF.setInt("numLevels", numLevels);
		MyF.setInt("numBeats", numBeats);
		MyF.setInt("columnsPerBeat", columnsPerBeat);
		MyF.setInt("startMelodicInterval", startMelodicInterval);
		MyF.setInt("startNote", startNote);
		MyF.setDouble("tempo", tempo);
		
		
		MyF.setBoolean("spreadPitchOn", spreadPitchOn);
		MyF.setDouble("spreadPitchSize", spreadPitchSize);
		MyF.setProperty( "spreadPitchRandseed" , new Long( spreadPitchRandseed ) );
	
		
		MyF.setBoolean("spreadTimeOn", spreadTimeOn);
		MyF.setDouble("spreadTimeSize", spreadTimeSize);
		MyF.setProperty( "spreadTimeRandseed" , new Long( spreadTimeRandseed ) );
		
		
		int row;
		int col;
		for( col = 0 ; col < getNumCols() ; col++ )
		{
			for( row = 0 ; row < numLevels ; row++ )
			{
				int tmp = tenoriStates[ col ][ row ].getX();
				String key = "tenori_" + ( row ) + "__" + ( col );
				MyF.setInt(key, tmp);
			}
		}
		

		return (MyF);
	}
	
	
	/**
	 * Reads the tenori state from a version buffer.
	 * @param MyF The input version buffer.
	 * @throws Throwable
	 */
	protected void readTransVersionBuffer( TransVersionBuffer MyF ) throws Throwable
	{
		numLevels = MyF.getInt("numLevels");
		numBeats = MyF.getInt("numBeats");
		columnsPerBeat = MyF.getInt("columnsPerBeat");
		startMelodicInterval = MyF.getInt("startMelodicInterval");
		startNote = MyF.getInt("startNote");
		tempo = MyF.getDouble("tempo");
		
		refreshStates();
		refreshKey();
		refreshSquares();
		
		
		spreadPitchOn = MyF.getBoolean("spreadPitchOn");
		spreadPitchSize = MyF.getDouble("spreadPitchSize");
		Object l1 = MyF.getProperty( "spreadPitchRandseed" );
		spreadPitchRandseed = ( (Long)( l1 ) ).longValue();
	
		
		spreadTimeOn = MyF.getBoolean("spreadTimeOn");
		spreadTimeSize = MyF.getDouble("spreadTimeSize");
		Object l2 = MyF.getProperty( "spreadTimeRandseed" );
		spreadTimeRandseed = ( (Long)( l2 ) ).longValue();
		
		
		int row;
		int col;
		for( col = 0 ; col < getNumCols() ; col++ )
		{
			for( row = 0 ; row < numLevels ; row++ )
			{
				int tmp = tenoriStates[ col ][ row ].getX();
				String key = "tenori_" + ( row ) + "__" + ( col );
				int val = MyF.getInt(key);
				if( val != tmp )
				{
					tenoriStates[ col ][ row ].setCoords( new AazonBaseImmutableInt( val ) );
				}
			}
		}
		
	}
	
	
	/**
	 * Writes the tenori info to a URL.
	 * @param u The URL to which to write the tenori.
	 * @param OutTrans The input version buffer to write to the URL.
	 * @throws IOException
	 */
	protected void writeTenoriInfo(
			URL u,
			TransVersionBuffer OutTrans)
			throws IOException {
			OutputStream MyStream = null;
			XMLEncoder ostream = null;
			BufferedOutputStream bstream = null;

			try {
				String FileName = u.getFile();
				System.out.println(FileName);

				if (!(u.getProtocol().equals("file")))
					return;

				MyStream = new FileOutputStream(FileName);

				/* System.out.println( u );
				System.out.println( u.getProtocol() );
				URLConnection MyCon = u.openConnection();
				System.out.println( MyCon );
				MyCon.setDoOutput( true );
				System.out.println( MyCon.getPermission() );
				MyStream = MyCon.getOutputStream(); */

				if ((OutTrans instanceof Serializable)
					|| (OutTrans instanceof Externalizable)) {
					final Vector<Exception> except = new Vector<Exception>();
					
					ClassLoader threadCL = Thread.currentThread().getContextClassLoader();
					Thread.currentThread().setContextClassLoader(Meta.getDefaultClassLoader()); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

					ostream = ProgramDirector.createComponentEncoder(MyStream);

					ostream.setExceptionListener(new ExceptionListener() {
						public void exceptionThrown(Exception exception) {
							except.add(exception);
						}
					});

					ostream.writeObject(OutTrans);
					ostream.flush();
					ostream.close();
					
					Thread.currentThread().setContextClassLoader(threadCL); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

					if (except.size() > 0) {
						Exception ex = except.elementAt(0);
						if (ex instanceof IOException) {
							throw ((IOException) ex);
						} else {
							throw (new DataFormatException(ex));
						}
					}
				} else {
					throw( new RuntimeException( "Not Supported" ) );
				}

			} catch (IOException e) {
				if (ostream != null) {
					try {
						ostream.close();
					} catch (Exception e2) { /* No Handle */
					}
				}

				if (bstream != null) {
					try {
						bstream.close();
					} catch (Exception e2) { /* No Handle */
					}
				}

				if (MyStream != null) {
					try {
						MyStream.close();
					} catch (Exception e2) { /* No Handle */
					}
				}

				throw (e);
			}

			System.out.println("Save Done");
		}
	
	
	/**
	 * Loads the tenori from a file.
	 */
	public void loadTenori()
	{
		try {
			String FileName = null;

			JFileChooser fd = new JFileChooser();
			
			Component parent = this;
			while( !( parent instanceof Window ) && ( parent != null ) )
			{
				parent = parent.getParent();
			}
			
			int returnVal = fd.showOpenDialog( parent );

			if (returnVal != JFileChooser.APPROVE_OPTION)
				return;

			File MyFile = fd.getSelectedFile();
			FileName = MyFile.getPath();

			URL myU = null;

			try {
				myU = MyFile.toURL();
			} catch (Exception ex) {
				VerdantiumUtils.produceMessageWindow(
					ex,
					"Bad File Name",
					"Bad File Name",
					"Bad file name.  Please try again.",
					null,
					null);
				return;
			}
			
			InputStream is = myU.openStream();
			
			ClassLoader threadCL = Thread.currentThread().getContextClassLoader();
			Thread.currentThread().setContextClassLoader(Meta.getDefaultClassLoader());
			
			XMLDecoder decoder = ProgramDirector.createComponentDecoder( is );
			
			Object myo = decoder.readObject();
			
			TransVersionBuffer tb = (TransVersionBuffer) myo;
			
			readTransVersionBuffer( tb );
			
			Thread.currentThread().setContextClassLoader(threadCL); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			
			
		} catch (Throwable in) {
			in.printStackTrace( System.out );
		}
	}


	/**
	 * Handles the start of a mouse-drag by doing nothing.
	 * @param e The mouse event.
	 */
	protected void mouserDrag(MouseEvent e) {
	}

	
	/**
	 * Handles the end of a mouse-drag by doing nothing.
	 * @param e The mouse event.
	 */
	public void mouserDragEnd(MouseEvent e) {
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
	 */
	public void mouseDragged(MouseEvent arg0) {
		mouserDrag(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent arg0) {
		mouserDragEnd(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent arg0) {
		mouserDragEnd(arg0);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent arg0) {
		mouserDragEnd(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent arg0) {
		// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		mouserDown(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent arg0) {
		mouserDragEnd(arg0);
	}
	
	@Override
	public Dimension minimumSize() {
		return (new Dimension(400, 400));
	}

	@Override
	public Dimension preferredSize() {
		return (new Dimension(400, 400));
	}
	
	
	/**
	 * Gets the number of columns in the tenori view.
	 * @return The number of columns in the tenori view.
	 */
	protected int getNumCols()
	{
		return( numBeats * columnsPerBeat );
	}
	
	

	/**
	 * Gets the number of the melodic interval for the current key.
	 * @return The number of the melodic interval for the current key.
	 */
	public int getStartMelodicInterval() {
		return startMelodicInterval;
	}

	/**
	 * Sets the number of the melodic interval for the current key.
	 * @param startMelodicInterval The number of the melodic interval for the current key.
	 */
	public void setStartMelodicInterval(int startMelodicInterval) {
		this.startMelodicInterval = startMelodicInterval;
	}

	/**
	 * Gets the starting scale step for the key of the tenori.
	 * @return The starting scale step for the key of the tenori.
	 */
	public int getStartNote() {
		return startNote;
	}

	/**
	 * Sets the starting scale step for the key of the tenori.
	 * @param startNote The starting scale step for the key of the tenori.
	 */
	public void setStartNote(int startNote) {
		this.startNote = startNote;
	}

	/**
	 * Gets the tempo to play the tenori (in beats per minute).
	 * @return The tempo to play the tenori (in beats per minute).
	 */
	public double getTempo() {
		return tempo;
	}

	/**
	 * Sets the tempo to play the tenori (in beats per minute).
	 * @param tempo The tempo to play the tenori (in beats per minute).
	 */
	public void setTempo(double tempo) {
		this.tempo = tempo;
	}
	
	/**
	 * Gets the number of vertical levels in the tenori view.
	 * @return The number of vertical levels in the tenori view.
	 */
	public int getNumLevels() {
		return numLevels;
	}

	/**
	 * Sets the number of vertical levels in the tenori view.
	 * @param numLevels The number of vertical levels in the tenori view.
	 */
	public void setNumLevels(int numLevels) {
		this.numLevels = numLevels;
	}

	/**
	 * Gets the number of beats to be contained by the tenori columns.
	 * @return The number of beats to be contained by the tenori columns.
	 */
	public int getNumBeats() {
		return numBeats;
	}

	/**
	 * Sets the number of beats to be contained by the tenori columns.
	 * @param numBeats The number of beats to be contained by the tenori columns.
	 */
	public void setNumBeats(int numBeats) {
		this.numBeats = numBeats;
	}

	/**
	 * Gets the number of tenori columns for each beat.
	 * @return The number of tenori columns for each beat.
	 */
	public int getColumnsPerBeat() {
		return columnsPerBeat;
	}

	/**
	 * Sets the number of tenori columns for each beat.
	 * @param columnsPerBeat The number of tenori columns for each beat.
	 */
	public void setColumnsPerBeat(int columnsPerBeat) {
		this.columnsPerBeat = columnsPerBeat;
	}

	/**
	 * Gets whether to have statistical variation in pitch.
	 * @return Whether to have statistical variation in pitch.
	 */
	public boolean isSpreadPitchOn() {
		return spreadPitchOn;
	}

	/**
	 * Sets whether to have statistical variation in pitch.
	 * @param spreadPitchOn Whether to have statistical variation in pitch.
	 */
	public void setSpreadPitchOn(boolean spreadPitchOn) {
		this.spreadPitchOn = spreadPitchOn;
	}

	/**
	 * Gets the size of the random variation of the pitch (in hertz).
	 * @return The size of the random variation of the pitch (in hertz).
	 */
	public double getSpreadPitchSize() {
		return spreadPitchSize;
	}

	/**
	 * Sets the size of the random variation of the pitch (in hertz).
	 * @param spreadPitchSize The size of the random variation of the pitch (in hertz).
	 */
	public void setSpreadPitchSize(double spreadPitchSize) {
		this.spreadPitchSize = spreadPitchSize;
	}
	
	/**
	 * Gets the random number seed with respect to pitch.
	 * @return The random number seed with respect to pitch.
	 */
	public long getSpreadPitchRandseed() {
		return spreadPitchRandseed;
	}

	/**
	 * Sets the random number seed with respect to pitch.
	 * @param spreadPitchRandseed The random number seed with respect to pitch.
	 */
	public void setSpreadPitchRandseed(long spreadPitchRandseed) {
		this.spreadPitchRandseed = spreadPitchRandseed;
	}
	
	/**
	 * Gets whether to have statistical variation in time.
	 * @return Whether to have statistical variation in time.
	 */
	public boolean isSpreadTimeOn() {
		return spreadTimeOn;
	}

	/**
	 * Sets whether to have statistical variation in time.
	 * @param spreadTimeOn Whether to have statistical variation in time.
	 */
	public void setSpreadTimeOn(boolean spreadTimeOn) {
		this.spreadTimeOn = spreadTimeOn;
	}

	/**
	 * Gets the size of the random variation of the time (in seconds).
	 * @return The size of the random variation of the time (in seconds).
	 */
	public double getSpreadTimeSize() {
		return spreadTimeSize;
	}

	/**
	 * Sets the size of the random variation of the time (in seconds).
	 * @param spreadTimeSize The size of the random variation of the time (in seconds).
	 */
	public void setSpreadTimeSize(double spreadTimeSize) {
		this.spreadTimeSize = spreadTimeSize;
	}

	/**
	 * Gets the random number seed with respect to time.
	 * @return The random number seed with respect to time.
	 */
	public long getSpreadTimeRandseed() {
		return spreadTimeRandseed;
	}

	/**
	 * Sets the random number seed with respect to time.
	 * @param spreadTimeRandseed The random number seed with respect to time.
	 */
	public void setSpreadTimeRandseed(long spreadTimeRandseed) {
		this.spreadTimeRandseed = spreadTimeRandseed;
	}



	/**
	 * Splits the tenori.
	 * @param splitNum The number of ways to split the tenori.
	 */
	public void performAutoSplit( final int splitNum )
	{
		int notes;
		int levels;
		
		final int[][] orig = new int[ getNumCols() ][ numLevels ];
		
		for( notes = 0 ; notes < getNumCols() ; notes++ )
		{
			for( levels = 0 ; levels < numLevels ; levels++ )
			{
				try
				{
					int vval = ( tenoriStates[ notes ][ levels ] ).getX();
					orig[ notes ][ levels ] = vval;
				}
				catch( Exception ex )
				{
					// None.
				}
			}
		}
		
		
		int[][] nst = null;
		
		
		int cnt;
		for( cnt = 0 ; cnt <= splitNum ; cnt++ )
		{
			final int[][] newState = new int[ getNumCols() ][ numLevels ];
			int lastCol = -1;
			int lastLevel = -1;
			for( notes = 0 ; notes < getNumCols() ; notes++ )
			{
				levels = searchLevels( orig , notes , lastCol , lastLevel );
				if( levels != -1 )
				{
					newState[ notes ][ levels ] = orig[ notes ][ levels ];
					orig[ notes ][ levels ] = NONEV;
					lastCol = notes;
					lastLevel = levels;
				}
			}
			nst = newState;
		}
		
		
		if( nst == null )
		{
			return;
		}
		
		
		for( notes = 0 ; notes < getNumCols() ; notes++ )
		{
			for( levels = 0 ; levels < numLevels ; levels++ )
			{
				try
				{
					AazonSimpleMutableInt mint = tenoriStates[ notes ][ levels ];
					
					switch( nst[ notes ][ levels ] )
					{
						
					case NONEV:
					{
						mint.setCoords( NONE );
					}
						break;	
						
					case REDV: 
					{
						mint.setCoords( RED );
					}
						break;
						
					case GREENV: 
					{
						mint.setCoords( GREEN );
					}
						break;
					
						
					}
				}
				catch( Exception ex )
				{
					// None.
				}
			}
		}
		
		
		
	}
	
	
	
	protected int searchLevels( int[][] orig , int notes , int lastCol , int lastLevel )
	{
		int ret = -1;
		
		final int nt = notes - 1 < 0 ? getNumCols() - 1 : notes - 1;
		if( nt == lastCol )
		{
			if( orig[ notes ][ lastLevel ] == GREENV )
			{
				return( lastLevel );
			}
		}
		
		int levels;
		for( levels = 0 ; levels < numLevels ; levels++ )
		{
			if( orig[ notes ][ levels ] != NONEV )
			{
				if( orig[ notes ][ levels ] == REDV )
				{
					return( levels );
				}
				
				// GreenV
				if( orig[ nt ][ levels ] == NONEV )
				{
					return( levels );
				}
				
			}
		}
		
		return( ret );
	}
	
	

}

